package server;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.websocket.*;
import model.GameData;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import service.UnauthorizedException;
import websocket.commands.UserGameCommand;
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
        } catch (Exception e){
            e.printStackTrace();
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
                getGame(command.getAuthToken(), gameID))));

        // Send Notification to all other clients saying someone joined as a player (with color) or observer


        if(!clients.containsKey(gameID)){
            clients.put(gameID, new ConnectionManager());
        }
        clients.get(gameID).add(root.session);
        clients.get(gameID).broadcast(root.session, new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, "Player has joined"));
    }

    public void makeMove(WsMessageContext root) throws IOException {
        UserGameCommand command = new Gson().fromJson(root.message(), UserGameCommand.class);
        Integer gameID = command.getGameID();

        // validate move

        // update game

        // send LOAD_GAME to all clients

        // send Notification to other clients informing that a move was made
        clients.get(gameID).add(root.session);
        clients.get(gameID).broadcast(root.session, new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, "Player has moved"));

        // send Notification to all clients if check, checkmate, or stalemate occurs
    }

    public void leave(WsMessageContext root) throws IOException {
        UserGameCommand command = new Gson().fromJson(root.message(), UserGameCommand.class);
        Integer gameID = command.getGameID();

        // Update game

        // Send Notification to other clients
        clients.get(gameID).broadcast(root.session, new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, "Player has left"));

        clients.get(gameID).remove(root.session);
    }

    public void resign(WsMessageContext root) throws IOException {
        UserGameCommand command = new Gson().fromJson(root.message(), UserGameCommand.class);
        Integer gameID = command.getGameID();
        // Update game

        // Send Notification to all clients
        clients.get(gameID).broadcast(root.session, new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, "Player has resigned"));

        clients.get(gameID).remove(root.session);
    }

    public ChessGame getGame(String authToken, int gameID) throws UnauthorizedException, DataAccessException {
        for(GameData gameData: gameService.listGames(authToken).games()){
            if(gameData.gameID() == gameID){
                return gameData.game();
            }
        }
        throw new DataAccessException("Joining nonexistent game");
    }

}
