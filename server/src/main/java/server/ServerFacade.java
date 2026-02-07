package server;

import com.google.gson.Gson;
import model.UserData;
import service.CreateGameResult;
import service.ListGamesResult;
import service.LoginRequest;
import service.LoginResult;

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

    public void logout(String header){
        HttpRequest request = buildRequest("DELETE", "/session", header, null);
        sendRequest(request);
    }

    public CreateGameResult createGame(String header, String data){
        HttpRequest request = buildRequest("POST", "/game", header, data);
        HttpResponse<String> response = sendRequest(request);
        return handleResponse(response, CreateGameResult.class);
    }

    public ListGamesResult listGames(String data){
        HttpRequest request = buildRequest("GET", "/game", null, data);
        HttpResponse<String> response = sendRequest(request);
        return handleResponse(response, ListGamesResult.class);
    }

    public void playGame(String header, String data){
        HttpRequest request = buildRequest("PUT", "/game", header, data);
        sendRequest(request);
    }

    public void clear(){
        HttpRequest request = buildRequest("DELETE", "/db", null, null);
        sendRequest(request);
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