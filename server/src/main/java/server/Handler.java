package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import service.*;

public class Handler {

    private final AuthService authService;
    private final GameService gameService;
    private final UserService userService;
    private final Gson gson;

    public Handler(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO){
        this.authService = new AuthService(authDAO);
        this.gameService = new GameService(gameDAO, authDAO);
        this.userService = new UserService(userDAO, authDAO);
        gson = new Gson();
    }

    public String register(String body) throws JsonSyntaxException, AlreadyTakenException {
        UserData userData = gson.fromJson(body, UserData.class);
        AuthData authData = userService.register(userData);

        return gson.toJson(authData);
    }

    public String login(String body) throws JsonSyntaxException, UnauthorizedException {
        LoginRequest loginRequest = gson.fromJson(body, LoginRequest.class);
        AuthData authData = userService.login(loginRequest);

        return gson.toJson(authData);
    }

    public void logout(String authToken) throws UnauthorizedException {
        userService.logout(authToken);
    }

    public String listGames(String authToken) throws UnauthorizedException {
        ListGamesResult gameList = gameService.listGames(authToken);

        return gson.toJson(gameList);
    }

    public String createGame(String authToken, String body) throws UnauthorizedException {
        String gameName = gson.fromJson(body, String.class);
        CreateGameResult gameID = gameService.createGame(authToken, gameName);

        return gson.toJson(gameID);
    }

    public void joinGame(String authToken, String body) throws UnauthorizedException, DataAccessException, AlreadyTakenException {
        JoinGameRequest joinRequest = gson.fromJson(body, JoinGameRequest.class);
        gameService.joinGame(authToken, joinRequest);
    }

    public void clear(){
        userService.clear();
        authService.clear();
        gameService.clear();
    }
}
