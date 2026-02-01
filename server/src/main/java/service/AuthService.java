package service;

import dataaccess.AuthDAO;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class AuthService {
    private final AuthDAO authDAO;

    public AuthService(AuthDAO authDAO){
        this.authDAO = authDAO;
    }

    public void clear(){
        authDAO.clear();
    }

    public AuthData generateAuth(UserData userData) {
        AuthData authData = new AuthData(userData.username(), UUID.randomUUID().toString());
        authDAO.createAuth(authData);

        return authData;
    }

    public AuthData validateAuth(String authToken) throws UnauthorizedException{
        AuthData authData = authDAO.getAuth(authToken);

        if(authData == null){throw new UnauthorizedException("Invalid auth token");}
        return authData;
    }

    public void deleteAuth(AuthData authData){
        authDAO.deleteAuth(authData);
    }
}
