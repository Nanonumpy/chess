package client;

import chess.ChessMove;
import model.UserData;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServerFacade {

    private final HttpCommunicator httpCommunicator;
    private final WebsocketCommunicator websocketCommunicator;

    public ServerFacade(String host, int port, ServerMessageObserver serverMessageObserver) {
        String url = "http://" + host + ":" + port;
        httpCommunicator = new HttpCommunicator(url);
        websocketCommunicator = new WebsocketCommunicator(url, serverMessageObserver);
    }

    public LoginResult register(UserData data) {
        HttpRequest request = httpCommunicator.buildRequest("POST", "/user", null, data);
        HttpResponse<String> response = httpCommunicator.sendRequest(request);
        return httpCommunicator.handleResponse(response, LoginResult.class);
    }

    public LoginResult login(LoginRequest data) {
        HttpRequest request = httpCommunicator.buildRequest("POST", "/session", null, data);
        HttpResponse<String> response = httpCommunicator.sendRequest(request);
        return httpCommunicator.handleResponse(response, LoginResult.class);
    }

    public void logout(String authToken) {
        HttpRequest request = httpCommunicator.buildRequest("DELETE", "/session", authToken, null);
        HttpResponse<String> response = httpCommunicator.sendRequest(request);
        httpCommunicator.handleResponse(response, null);
    }

    public CreateGameResult createGame(String authToken, CreateGameRequest gameName) {
        HttpRequest request = httpCommunicator.buildRequest("POST", "/game", authToken, gameName);
        HttpResponse<String> response = httpCommunicator.sendRequest(request);
        return httpCommunicator.handleResponse(response, CreateGameResult.class);
    }

    public ListGamesResult listGames(String authToken) {
        HttpRequest request = httpCommunicator.buildRequest("GET", "/game", authToken, null);
        HttpResponse<String> response = httpCommunicator.sendRequest(request);
        return httpCommunicator.handleResponse(response, ListGamesResult.class);
    }

    public void playGame(String authToken, JoinGameRequest data) {
        HttpRequest request = httpCommunicator.buildRequest("PUT", "/game", authToken, data);
        HttpResponse<String> response = httpCommunicator.sendRequest(request);
        httpCommunicator.handleResponse(response, null);
    }

    public void connect(String authToken, int gameID) {
        websocketCommunicator.connect(authToken, gameID);
    }

    public void makeMove(String authToken, int gameID, ChessMove move) {
        websocketCommunicator.makeMove(authToken, gameID, move);
    }

    public void leave(String authToken, int gameID) {
        websocketCommunicator.leave(authToken, gameID);
    }
    public void resign(String authToken, int gameID){
        websocketCommunicator.resign(authToken, gameID);
    }

    public void clear(){
        HttpRequest request = httpCommunicator.buildRequest("DELETE", "/db", null, null);
        HttpResponse<String> response = httpCommunicator.sendRequest(request);
        httpCommunicator.handleResponse(response, null);
    }
}