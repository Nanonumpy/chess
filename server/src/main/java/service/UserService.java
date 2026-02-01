package service;

import java.util.UUID;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;

public class UserService {

    private final UserDAO userDAO;
    private final AuthService authService;

    public UserService(UserDAO userDAO, AuthDAO authDAO){
        this.userDAO = userDAO;
        this.authService = new AuthService(authDAO);
    }

    public void clear(){
        userDAO.clear();
    }

    public LoginResult register(UserData userData) throws AlreadyTakenException{
        UserData checkData = userDAO.getUser(userData.username());

        if(checkData != null){throw new AlreadyTakenException("Username already taken");}

        userDAO.createUser(userData);

        AuthData loginAuth = authService.generateAuth(userData.username());
        return new LoginResult(loginAuth.username(), loginAuth.authToken());

    }

    public LoginResult login(LoginRequest loginRequest) throws UnauthorizedException {
        UserData checkData = userDAO.getUser(loginRequest.username());

        if(checkData == null || !checkData.password().equals(loginRequest.password())){throw new UnauthorizedException("Invalid credentials");}

        AuthData loginAuth = authService.generateAuth(loginRequest.username());
        return new LoginResult(loginAuth.username(), loginAuth.authToken());
    }

    public void logout(String authToken) throws UnauthorizedException{
        AuthData authData = authService.validateAuth(authToken);
        authService.deleteAuth(authData);
    }
}
