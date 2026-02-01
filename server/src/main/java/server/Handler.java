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

    public String register(String body) throws JsonSyntaxException, AlreadyTakenException, InvalidRequest {
        UserData userData = gson.fromJson(body, UserData.class);
        if(userData.username() == null || userData.password() == null || userData.email() == null){throw new InvalidRequest("Missing field");}
        LoginResult loginResult = userService.register(userData);

        return gson.toJson(loginResult);
    }

    public String login(String body) throws JsonSyntaxException, UnauthorizedException, InvalidRequest {
        LoginRequest loginRequest = gson.fromJson(body, LoginRequest.class);
        if(loginRequest.username() == null || loginRequest.password() == null){throw new InvalidRequest("Missing field");}
        LoginResult loginResult = userService.login(loginRequest);

        return gson.toJson(loginResult);
    }

    public void logout(String authToken) throws UnauthorizedException {
        userService.logout(authToken);
    }

    public String listGames(String authToken) throws UnauthorizedException {
        ListGamesResult gameList = gameService.listGames(authToken);

        return gson.toJson(gameList);
    }

    public String createGame(String authToken, String body) throws UnauthorizedException, InvalidRequest {
        String gameName = gson.fromJson(body, CreateGameRequest.class).gameName();
        if(gameName == null){throw new InvalidRequest("No game name provided");}
        CreateGameResult gameID = gameService.createGame(authToken, gameName);

        return gson.toJson(gameID);
    }

    public void joinGame(String authToken, String body) throws UnauthorizedException, DataAccessException, AlreadyTakenException, InvalidRequest {
        JoinGameRequest joinRequest = gson.fromJson(body, JoinGameRequest.class);
        if(joinRequest.gameID() == 0 || joinRequest.playerColor() == null){throw new InvalidRequest("Missing fields");}
        gameService.joinGame(authToken, joinRequest);
    }

    public void clear(){
        userService.clear();
        authService.clear();
        gameService.clear();
    }
}
