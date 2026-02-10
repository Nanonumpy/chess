package client;

import chess.ChessGame;
import model.GameData;
import server.ServerFacade;
import service.CreateGameRequest;
import service.JoinGameRequest;
import service.LoginResult;

import java.util.Scanner;

public class PostRepl {
    private final Scanner scanner = new Scanner(System.in);
    private final ServerFacade facade;
    private LoginResult loginResult;

    public PostRepl(ServerFacade facade){
        this.facade = facade;
    }

    public LoginResult getLoginResult() {
        return loginResult;
    }

    public void setLoginResult(LoginResult loginResult) {
        this.loginResult = loginResult;
    }

    public JoinGameRequest loop(){
        JoinGameRequest joinGameRequest = null;
        String id;
        System.out.print("[" + loginResult.username() + "] >>> ");
        String input = scanner.nextLine().toLowerCase();
        switch(input){
            case "help":
                System.out.println("  Register - create an account");
                System.out.println("  Login - login and play chess");
                System.out.println("  Quit - exit chess client");
                System.out.println("  Help - list available commands");

                break;

            case "logout":
                try {
                    facade.logout(loginResult.authToken());
                    setLoginResult(null);
                }
                catch(RuntimeException e){
                    System.out.print("Internal server error");
                }
                break;

            case "create":
                System.out.print("Enter Game Name: ");
                String gameName = scanner.nextLine();

                try{
                    int tempGameID = facade.createGame(loginResult.authToken(), new CreateGameRequest(gameName)).gameID();
                    System.out.println("Your game ID is " + tempGameID);
                }
                catch(RuntimeException e){
                    System.out.print("Internal server error");
                }
                break;

            case "list":
                try{
                    GameData[] gamesList = facade.listGames(loginResult.authToken()).games();
                    for(GameData game : gamesList) {
                        System.out.println(game.gameID() + ": " + game.gameName());
                    }
                }
                catch(RuntimeException e){
                    System.out.print("Internal server error");
                }
                break;

            case "play":
                System.out.print("Enter Team Color (WHITE/BLACK): ");
                String color = scanner.nextLine();
                System.out.print("Enter Game id: ");
                id = scanner.nextLine();

                try{
                    joinGameRequest = new JoinGameRequest(ChessGame.TeamColor.valueOf(color), Integer.parseInt(id));
                    facade.playGame(loginResult.authToken(), joinGameRequest);
                }
                catch(RuntimeException e){
                    System.out.print("Bad input or Internal server error");
                }
                break;

            case "observe":
                System.out.print("Enter Game id: ");
                id = scanner.nextLine();
                try{
                    joinGameRequest = new JoinGameRequest(ChessGame.TeamColor.WHITE, Integer.parseInt(id));
                }
                catch(RuntimeException e){
                    System.out.print("Internal server error");
                }
                break;

            default:
                System.out.println("Invalid command!\n");

        }
        return joinGameRequest;
    }
}
