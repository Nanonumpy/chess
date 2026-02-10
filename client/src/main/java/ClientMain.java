import client.GameRepl;
import client.PostRepl;
import client.PreRepl;
import client.ServerFacade;
import ui.EscapeSequences;

public class ClientMain {
    public static void main(String[] args) {
        ServerFacade facade = new ServerFacade("localhost", 0);

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


}

