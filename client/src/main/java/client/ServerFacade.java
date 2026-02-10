package client;

import com.google.gson.Gson;
import model.UserData;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String host, int port) {
        serverUrl = "http://" + host + ":" + port;
    }

    public LoginResult register(UserData data){
        HttpRequest request = buildRequest("POST", "/user", null, data);
        HttpResponse<String> response = sendRequest(request);
        return handleResponse(response, LoginResult.class);

    }

    public LoginResult login(LoginRequest data){
        HttpRequest request = buildRequest("POST", "/session", null, data);
        HttpResponse<String> response = sendRequest(request);
        return handleResponse(response, LoginResult.class);
    }

    public void logout(String authToken){
        HttpRequest request = buildRequest("DELETE", "/session", authToken, null);
        HttpResponse<String> response = sendRequest(request);
        handleResponse(response, null);
    }

    public CreateGameResult createGame(String authToken, CreateGameRequest gameName){
        HttpRequest request = buildRequest("POST", "/game", authToken, gameName);
        HttpResponse<String> response = sendRequest(request);
        return handleResponse(response, CreateGameResult.class);
    }

    public ListGamesResult listGames(String authToken){
        HttpRequest request = buildRequest("GET", "/game", authToken, null);
        HttpResponse<String> response = sendRequest(request);
        return handleResponse(response, ListGamesResult.class);
    }

    public void playGame(String authToken, JoinGameRequest data){
        HttpRequest request = buildRequest("PUT", "/game", authToken, data);
        HttpResponse<String> response = sendRequest(request);
        handleResponse(response, null);
    }

    public void clear(){
        HttpRequest request = buildRequest("DELETE", "/db", null, null);
        HttpResponse<String> response = sendRequest(request);
        handleResponse(response, null);
    }

    private HttpRequest buildRequest(String method, String path, String header, Object body) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (header != null) {
            request.setHeader("Authorization", header);
        }
        return request.build();
    }

    private BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request){
        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass){
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                throw new RuntimeException(body);
            }

            throw new RuntimeException("other failure: " + status);
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}