package client;

import chess.*;
import model.GameData;
import model.UserData;
import server.Server;
import server.ServerFacade;
import service.CreateGameRequest;
import service.JoinGameRequest;
import service.LoginRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static ui.EscapeSequences.WHITE_QUEEN;

public class ClientMain {
    public static void main(String[] args) {
        Server server = new Server();
        int port = server.run(0);
        ServerFacade facade = new ServerFacade("localhost", port);
        System.out.println(WHITE_QUEEN + " 240 Chess Client:");
        System.out.println("Type Help to get started.\n");
        Scanner scanner = new Scanner(System.in);
        String authToken = null;
        String username = null;
        GameData curGame = null;
        Map<Integer, GameData> games = new HashMap<>();
        facade.clear();

        // repl
        while(true){
            if(authToken == null){
                System.out.print("[LOGGED OUT] >>> ");
                String input = scanner.nextLine().toLowerCase();
                switch(input){
                    case "help":
                        System.out.println("  Register - create an account");
                        System.out.println("  Login - login and play chess");
                        System.out.println("  Quit - exit chess client");
                        System.out.println("  Help - list available commands");

                        break;

                    case "quit":
                        System.exit(0);
                        break;

                    case "login":
                        System.out.print("Enter Username: ");
                        username = scanner.nextLine();
                        System.out.print("Enter Password: ");
                        String password = scanner.nextLine();

                        try{
                            authToken = facade.login(new LoginRequest(username, password)).authToken();
                        }
                        catch(RuntimeException e){
                            System.out.print("Internal server error or invalid credentials");
                        }
                        break;

                    case "register":
                        System.out.print("Enter Username: ");
                        username = scanner.nextLine();
                        System.out.print("Enter Password: ");
                        password = scanner.nextLine();
                        System.out.print("Enter Email: ");
                        String email = scanner.nextLine();

                        try{
                            facade.register(new UserData(username, password, email));
                        }
                        catch(RuntimeException e){
                            System.out.print("Internal server error or username taken");
                        }
                        break;

                    default:
                        System.out.println("Invalid command!\n");

                }
            }

            else{
                System.out.print("[" + username + "] >>> ");
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
                            facade.logout(authToken);
                            authToken = null;
                        }
                        catch(RuntimeException e){
                            System.out.print("Internal server error");
                        }
                        break;

                    case "create":
                        System.out.print("Enter Game Name: ");
                        String gameName = scanner.nextLine();


                        try{
                            int gameID = facade.createGame(authToken, new CreateGameRequest(gameName)).gameID();
                            System.out.println("Your game ID is " + gameID);                        }
                        catch(RuntimeException e){
                            System.out.print("Internal server error");
                        }
                        break;

                    case "list":
                        try{
                            GameData[] gamesList = facade.listGames(authToken).games();
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
                        String id = scanner.nextLine();

                        try{
                            facade.playGame(authToken, new JoinGameRequest(ChessGame.TeamColor.valueOf(color), Integer.parseInt(id)));
                            if(games.isEmpty()) {
                                GameData[] gamesList = facade.listGames(authToken).games();
                                for (GameData game : gamesList) {
                                    games.put(game.gameID(), game);
                                }
                            }
                            curGame = games.get(Integer.parseInt(id));
                            System.out.println("Playing Game " + curGame.gameName());
                            displayBoard(curGame.game());
                        }
                        catch(RuntimeException e){
                            System.out.print("Bad input or Internal server error");
                        }
                        break;

                    case "observe":
                        System.out.print("Enter Game id: ");
                        id = scanner.nextLine();
                        try{
                            if(games.isEmpty()) {
                                GameData[] gamesList = facade.listGames(authToken).games();
                                for (GameData game : gamesList) {
                                    games.put(game.gameID(), game);
                                }
                            }
                            curGame = games.get(Integer.parseInt(id));
                            System.out.println("Observing Game " + curGame.gameName());
                            displayBoard(curGame.game());
                        }
                        catch(RuntimeException e){
                            System.out.print("Internal server error");
                        }
                        break;

                    default:
                        System.out.println("Invalid command!\n");

                }
            }

        }
    }
}

