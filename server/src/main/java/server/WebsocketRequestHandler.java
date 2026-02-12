package server;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.websocket.*;
import model.GameData;
import org.jetbrains.annotations.NotNull;
import service.AuthService;
import service.GameService;
import service.UnauthorizedException;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class WebsocketRequestHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConcurrentHashMap<Integer, ConnectionManager> clients = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();
    private final GameDAO gameDAO = new DatabaseGameDAO();
    private final AuthDAO authDAO = new DatabaseAuthDAO();


    private final GameService gameService = new GameService(gameDAO, authDAO);
    private final AuthService authService = new AuthService(authDAO);

    public WebsocketRequestHandler() throws DataAccessException {
    }

    @Override
    public void handleConnect(@NotNull WsConnectContext ctx) {
        ctx.enableAutomaticPings();
        System.out.println("Websocket connected");
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) {
        try {

            UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            switch (command.getCommandType()) {
                case UserGameCommand.CommandType.CONNECT -> connect(ctx);
                case UserGameCommand.CommandType.MAKE_MOVE -> makeMove(ctx);
                case UserGameCommand.CommandType.LEAVE -> leave(ctx);
                case UserGameCommand.CommandType.RESIGN -> resign(ctx);
                default -> throw new IllegalStateException("Unexpected value: " + command.getCommandType());
            }
        } catch (InvalidMoveException e){
            ctx.send(gson.toJson(new ErrorMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage())));
        } catch (UnauthorizedException e){
            ctx.send(gson.toJson(new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "UNAUTHORIZED")));
        } catch (Exception e){
            ctx.send(gson.toJson(new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "INTERNAL ERROR")));
        }

    }

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    public void connect(WsMessageContext root) throws IOException, UnauthorizedException, DataAccessException {
        UserGameCommand command = new Gson().fromJson(root.message(), UserGameCommand.class);
        Integer gameID = command.getGameID();

        // Send LOAD_GAME to root
        root.session.getRemote().sendString(gson.toJson(new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME,
                getGameData(command.getAuthToken(), gameID).game())));

        // Send Notification to all other clients saying someone joined as a player (with color) or observer
        if(!clients.containsKey(gameID)){
            clients.put(gameID, new ConnectionManager());
        }
        clients.get(gameID).add(root.session);
        clients.get(gameID).broadcast(root.session, new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, "Player has joined"));
    }

    public void makeMove(WsMessageContext root) throws IOException, UnauthorizedException, DataAccessException, InvalidMoveException {
        MakeMoveCommand command = new Gson().fromJson(root.message(), MakeMoveCommand.class);
        Integer gameID = command.getGameID();
        ChessMove move = command.getMove();
        GameData gameData = getGameData(command.getAuthToken(), gameID);
        String username = getUsername(command.getAuthToken());

        // validate move
        if(gameData.game().getTeamTurn() == ChessGame.TeamColor.WHITE && !gameData.whiteUsername().equals(username)){
            throw new InvalidMoveException("Not your turn!");
        }
        else if(gameData.game().getTeamTurn() == ChessGame.TeamColor.BLACK && !gameData.blackUsername().equals(username)){
            throw new InvalidMoveException("Not your turn!");
        }
        else if(gameData.game().getTeamTurn() == null){
            throw new InvalidMoveException("Game is over!");
        }

        // update game
        gameData.game().makeMove(move);
        gameDAO.updateGame(gameData);

        ChessGame game = gameData.game();

        // send LOAD_GAME to all clients
        clients.get(gameID).broadcast(null, new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game));

        // send Notification to other clients informing that a move was made
        String message = game.getBoard().getPiece(move.getEndPosition()).getPieceType().toString() + move;
        clients.get(gameID).broadcast(root.session, new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message));

        // send Notification to all clients if check, checkmate, or stalemate occurs
        if(game.isInCheckmate(game.getTeamTurn())){
            clients.get(gameID).broadcast(null, new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, "Checkmate"));

        }
        else if(game.isInStalemate(game.getTeamTurn())){
            clients.get(gameID).broadcast(null, new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, "Stalemate"));

        }
        else if(game.isInCheck(game.getTeamTurn())){
            clients.get(gameID).broadcast(null, new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, "Check"));
        }
    }

    public void leave(WsMessageContext root) throws IOException, UnauthorizedException, DataAccessException {
        UserGameCommand command = new Gson().fromJson(root.message(), UserGameCommand.class);
        Integer gameID = command.getGameID();
        GameData gameData = getGameData(command.getAuthToken(), gameID);
        String username = getUsername(command.getAuthToken());

        // Update game
        if(username.equals(gameData.whiteUsername())){
            gameDAO.updateGame(new GameData(gameData.gameID(), null,
                    gameData.blackUsername(), gameData.gameName(), gameData.game()));
        }
        else if(username.equals(gameData.blackUsername())) {
            gameDAO.updateGame(new GameData(gameData.gameID(), gameData.whiteUsername(),
                    null, gameData.gameName(), gameData.game()));
        }

        // Send Notification to other clients
        clients.get(gameID).broadcast(root.session, new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, "Player has left"));

        clients.get(gameID).remove(root.session);
    }

    public void resign(WsMessageContext root) throws IOException, UnauthorizedException, DataAccessException, InvalidMoveException {
        UserGameCommand command = new Gson().fromJson(root.message(), UserGameCommand.class);
        Integer gameID = command.getGameID();
        GameData gameData = getGameData(command.getAuthToken(), gameID);
        String username = getUsername(command.getAuthToken());

        if(!username.equals(gameData.whiteUsername()) && !username.equals(gameData.blackUsername())){
            throw new InvalidMoveException("Observer cannot resign");
        }
        if(gameData.game().getTeamTurn() == null){
            throw new InvalidMoveException("Game is already over!");
        }

        // Update game
        gameData.game().setTeamTurn(null);
        gameDAO.updateGame(gameData);

        // Send Notification to all clients
        clients.get(gameID).broadcast(null, new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, "Player has resigned"));

    }

    public GameData getGameData(String authToken, int gameID) throws UnauthorizedException, DataAccessException {
        for(GameData gameData: gameService.listGames(authToken).games()){
            if(gameData.gameID() == gameID){
                return gameData;
            }
        }
        throw new DataAccessException("Joining nonexistent game");
    }

    public String getUsername(String authToken) throws UnauthorizedException, DataAccessException {
        return authService.validateAuth(authToken).username();
    }

}
