package client;

import chess.ChessMove;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.websocket.*;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebsocketCommunicator extends Endpoint {
    Session session;
    ServerMessageObserver notificationHandler;
    private final Gson gson = new Gson();

    public WebsocketCommunicator(String url, ServerMessageObserver notificationHandler) {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler((MessageHandler.Whole<String>) message -> {
                JsonObject obj = JsonParser.parseString(message).getAsJsonObject();
                String type = obj.get("serverMessageType").getAsString();

                ServerMessage notification;

                switch (type) {
                    case "LOAD_GAME" ->
                            notification = gson.fromJson(message, LoadGameMessage.class);
                    case "NOTIFICATION" ->
                            notification = gson.fromJson(message, NotificationMessage.class);
                    case "ERROR" ->
                            notification = gson.fromJson(message, ErrorMessage.class);
                    default ->
                            throw new IllegalStateException("Unknown message type");
                }
                notificationHandler.notify(notification);
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connect(String authToken, int gameID){
        try {
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    public void makeMove(String authToken, int gameID, ChessMove move){
        try {
            UserGameCommand command = new MakeMoveCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID, move);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    public void leave(String authToken, int gameID){
        try {
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    public void resign(String authToken, int gameID){
        try {
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
}
