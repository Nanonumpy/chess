package client;

import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpCommunicator {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public HttpCommunicator(String serverURL){
        this.serverUrl = serverURL;
    }


    public HttpRequest buildRequest(String method, String path, String header, Object body) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (header != null) {
            request.setHeader("Authorization", header);
        }
        return request.build();
    }

    public HttpRequest.BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return HttpRequest.BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }

    public HttpResponse<String> sendRequest(HttpRequest request){
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    public <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass){
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
