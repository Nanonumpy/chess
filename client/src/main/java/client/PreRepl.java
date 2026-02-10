package client;

import model.UserData;

import java.util.Scanner;

public class PreRepl {
    private final Scanner scanner = new Scanner(System.in);
    private final ServerFacade facade;

    public PreRepl(ServerFacade facade){
        this.facade = facade;
    }

    public LoginResult loop() {
        LoginResult loginResult = null;
        System.out.print("[LOGGED OUT] >>> ");
        String input = scanner.nextLine().toLowerCase();
        switch (input) {
            case "help":
                help();
                break;

            case "quit":
                System.exit(0);
                break;

            case "login":
                loginResult = login(scanner);
                break;

            case "register":
                loginResult = register(scanner);
                break;

            default:
                System.out.println("Invalid command!\n");

        }
        return loginResult;
    }


    public void help(){
        System.out.println("  Register - create an account");
        System.out.println("  Login - login and play chess");
        System.out.println("  Quit - exit chess client");
        System.out.println("  Help - list available commands");
    }

    public LoginResult login(Scanner scanner){
        System.out.print("Enter Username: ");
        String username = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        try{
            return facade.login(new LoginRequest(username, password));
        }
        catch(RuntimeException e){
            System.out.print("Internal server error or invalid credentials");
        }

        return null;
    }

    public LoginResult register(Scanner scanner){
        System.out.print("Enter Username: ");
        String username = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();
        System.out.print("Enter Email: ");
        String email = scanner.nextLine();

        try {
            return facade.register(new UserData(username, password, email));
        } catch (RuntimeException e) {
            System.out.print("Internal server error or username taken");
        }

        return null;
    }
}
