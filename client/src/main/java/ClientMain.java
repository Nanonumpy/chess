import client.*;
import ui.EscapeSequences;
import websocket.messages.ServerMessage;

public class ClientMain implements ServerMessageObserver {
    public static void main(String[] args){
        ServerFacade facade = new ServerFacade("localhost", 8080, this);

        PreRepl preRepl = new PreRepl(facade);
        PostRepl postRepl = new PostRepl(facade);
        GameRepl gameRepl = new GameRepl(facade);

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

            else{
                gameRepl.loop();
            }
            System.out.println();
        }
    }


    @Override
    public void notify(ServerMessage message) {
        System.out.println("ERROR");
    }
}

