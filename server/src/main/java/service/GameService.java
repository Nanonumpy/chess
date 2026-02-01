package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthService authService;
    private int nextID = 1;

    public GameService(GameDAO gameDAO, AuthDAO authDAO){
        this.gameDAO = gameDAO;
        this.authService = new AuthService(authDAO);
    }

    public void clear(){
        gameDAO.clear();
    }

    public CreateGameResult createGame(String authToken, String gameName) throws UnauthorizedException{
        authService.validateAuth(authToken);

        GameData gameData = new GameData(nextID++, null, null, gameName, new ChessGame());
        gameDAO.createGame(gameData);

        return new CreateGameResult(gameData.gameID());
    }

    public void joinGame(String authToken, JoinGameRequest joinRequest) throws UnauthorizedException, DataAccessException, AlreadyTakenException {
        AuthData authData = authService.validateAuth(authToken);

        GameData gameData = gameDAO.getGame(joinRequest.gameID());

        if(gameData == null){throw new DataAccessException("Game does not exist");}
        if(joinRequest.playerColor() == ChessGame.TeamColor.WHITE && gameData.whiteUsername() != null){throw new AlreadyTakenException("White player already in game");}
        if(joinRequest.playerColor() == ChessGame.TeamColor.BLACK && gameData.blackUsername() != null){throw new AlreadyTakenException("Black player already in game");}

        if(joinRequest.playerColor() == ChessGame.TeamColor.WHITE){
            gameDAO.updateGame(new GameData(joinRequest.gameID(), authData.username(), gameData.blackUsername(), gameData.gameName(), gameData.game()));
        }
        else{
            gameDAO.updateGame(new GameData(joinRequest.gameID(), gameData.whiteUsername(), authData.username(), gameData.gameName(), gameData.game()));
        }
    }

    public ListGamesResult listGames(String authToken) throws UnauthorizedException {
        authService.validateAuth(authToken);

        return new ListGamesResult(gameDAO.listGames());
    }
}
