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

    public AuthData register(UserData userData) throws AlreadyTakenException{
        UserData checkData = userDAO.getUser(userData.username());

        if(checkData != null){throw new AlreadyTakenException("Username already taken");}

        userDAO.createUser(userData);

        return authService.generateAuth(userData);

    }

    public AuthData login(UserData userData) throws DataAccessException, UnauthorizedException {
        UserData checkData = userDAO.getUser(userData.username());

        if(checkData == null){throw new DataAccessException("No user found for given username");}
        if(!checkData.password().equals(userData.password())){throw new UnauthorizedException("Password does not match");}

        return authService.generateAuth(userData);
    }

    public void logout(String authToken) throws UnauthorizedException{

        AuthData authData = authService.validateAuth(authToken);
        authService.deleteAuth(authData);
    }
}
