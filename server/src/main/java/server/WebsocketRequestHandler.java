package server;

import com.google.gson.Gson;
import io.javalin.websocket.*;
import org.jetbrains.annotations.NotNull;
import websocket.commands.UserGameCommand;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class WebsocketRequestHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConcurrentHashMap<Integer, ConnectionManager> clients = new ConcurrentHashMap<>();

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
        } catch (IOException e){
            e.printStackTrace();
        }

    }

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    public void connect(WsMessageContext root) throws IOException {
        // Send LOAD_GAME to root

        // Send Notification to all other clients saying someone joined as a player (with color) or observer
        UserGameCommand command = new Gson().fromJson(root.message(), UserGameCommand.class);
        Integer gameId = command.getGameID();

        if(!clients.containsKey(gameId)){
            clients.put(gameId, new ConnectionManager());
        }
        clients.get(gameId).add(root.session);
        clients.get(gameId).broadcast(root.session, new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, "Player has joined"));
    }

    public void makeMove(WsMessageContext root) throws IOException {
        UserGameCommand command = new Gson().fromJson(root.message(), UserGameCommand.class);
        Integer gameId = command.getGameID();

        // validate move

        // update game

        // send LOAD_GAME to all clients

        // send Notification to other clients informing that a move was made
        clients.get(gameId).add(root.session);
        clients.get(gameId).broadcast(root.session, new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, "Player has moved"));

        // send Notification to all clients if check, checkmate, or stalemate occurs
    }

    public void leave(WsMessageContext root) throws IOException {
        UserGameCommand command = new Gson().fromJson(root.message(), UserGameCommand.class);
        Integer gameId = command.getGameID();

        // Update game

        // Send Notification to other clients
        clients.get(gameId).broadcast(root.session, new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, "Player has left"));

        clients.get(gameId).remove(root.session);
    }

    public void resign(WsMessageContext root) throws IOException {
        UserGameCommand command = new Gson().fromJson(root.message(), UserGameCommand.class);
        Integer gameId = command.getGameID();
        // Update game

        // Send Notification to all clients
        clients.get(gameId).broadcast(root.session, new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, "Player has resigned"));

        clients.get(gameId).remove(root.session);
    }

}
