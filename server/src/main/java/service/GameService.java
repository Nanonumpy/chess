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

    public GameService(GameDAO gameDAO, AuthDAO authDAO){
        this.gameDAO = gameDAO;
        this.authService = new AuthService(authDAO);
    }

    public void clear() throws DataAccessException {
        gameDAO.clear();
    }

    public CreateGameResult createGame(String authToken, String gameName) throws UnauthorizedException, DataAccessException {
        authService.validateAuth(authToken);

        int gameID = gameDAO.createGame(gameName);

        return new CreateGameResult(gameID);
    }

    public void joinGame(String authToken, JoinGameRequest joinRequest) throws UnauthorizedException, DataAccessException, AlreadyTakenException {
        AuthData authData = authService.validateAuth(authToken);

        GameData gameData = gameDAO.getGame(joinRequest.gameID());

        if(gameData == null){throw new DataAccessException("Game does not exist");}
        if(joinRequest.playerColor() == ChessGame.TeamColor.WHITE && gameData.whiteUsername() != null){
            throw new AlreadyTakenException("White player already in game");
        }
        if(joinRequest.playerColor() == ChessGame.TeamColor.BLACK && gameData.blackUsername() != null){
            throw new AlreadyTakenException("Black player already in game");
        }

        if(joinRequest.playerColor() == ChessGame.TeamColor.WHITE){
            gameDAO.updateGame(new GameData(joinRequest.gameID(), authData.username(),
                    gameData.blackUsername(), gameData.gameName(), gameData.game()));
        }
        else{
            gameDAO.updateGame(new GameData(joinRequest.gameID(), gameData.whiteUsername(),
                    authData.username(), gameData.gameName(), gameData.game()));
        }
    }

    public ListGamesResult listGames(String authToken) throws UnauthorizedException, DataAccessException {
        authService.validateAuth(authToken);

        return new ListGamesResult(gameDAO.listGames());
    }
}
