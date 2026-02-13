package client;

import ui.EscapeSequences;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Scanner;

public class ChessClient implements ServerMessageObserver {
    PreRepl preRepl;
    PostRepl postRepl;
    GameRepl gameRepl;
    private final Scanner scanner = new Scanner(System.in);


    public void run(){
        ServerFacade facade = new ServerFacade("localhost", 8080, this);

        preRepl = new PreRepl(facade, scanner);
        postRepl = new PostRepl(facade, scanner);
        gameRepl = new GameRepl(facade, scanner);

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

            else{
                // waiting for server to load, so just busy spin
                try { Thread.sleep(20); } catch (InterruptedException ignored) {}
            }
        }
    }

    @Override
    public void notify(ServerMessage message) {
        boolean printLoop = gameRepl.getGameData() != null;
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
            if(!printLoop){gameRepl.setJoinGameRequest(null);}
        }

        if(printLoop) {gameRepl.printLoop();}
    }
}
