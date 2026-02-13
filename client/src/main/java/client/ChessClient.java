package client;

import ui.EscapeSequences;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

public class ChessClient implements ServerMessageObserver {
    PreRepl preRepl;
    PostRepl postRepl;
    GameRepl gameRepl;

    public void run(){
        ServerFacade facade = new ServerFacade("localhost", 8080, this);

        preRepl = new PreRepl(facade);
        postRepl = new PostRepl(facade);
        gameRepl = new GameRepl(facade);

        System.out.println(EscapeSequences.WHITE_QUEEN + " 240 Chess Client:");
        System.out.println("Type Help to get started.\n");

        //noinspection InfiniteLoopStatement
        while(true){
            if(postRepl.getLoginResult() == null){
                postRepl.setLoginResult(preRepl.loop());
            }

            else if (gameRepl.getJoinGameRequest() == null){
                gameRepl.setAuthToken(postRepl.getLoginResult().authToken());
                gameRepl.setJoinGameRequest(postRepl.loop());
            }

            else if (gameRepl.getGameData() != null){
                gameRepl.loop();
            }
        }
    }


    @Override
    public void notify(ServerMessage message) {
        if(message.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME){
            LoadGameMessage loadGame = (LoadGameMessage)message;
            gameRepl.setGameData(loadGame.getGame());
            gameRepl.redraw(null);
        }
        if(message.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION){
            NotificationMessage notification = (NotificationMessage)message;
            System.out.println("\r" + EscapeSequences.ERASE_LINE + notification.getMessage());
        }
        if(message.getServerMessageType() == ServerMessage.ServerMessageType.ERROR){
            ErrorMessage error = (ErrorMessage)message;
            System.out.println("\r" + EscapeSequences.ERASE_LINE + error.getErrorMessage());
        }

        gameRepl.printLoop();
    }
}
