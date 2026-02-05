package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class AuthService {
    private final AuthDAO authDAO;

    public AuthService(AuthDAO authDAO){
        this.authDAO = authDAO;
    }

    public void clear() throws DataAccessException {
        authDAO.clear();
    }

    public AuthData generateAuth(String username) throws DataAccessException {
        AuthData authData = new AuthData(UUID.randomUUID().toString(), username);
        authDAO.createAuth(authData);

        return authData;
    }

    public AuthData validateAuth(String authToken) throws UnauthorizedException, DataAccessException {
        AuthData authData = authDAO.getAuth(authToken);

        if(authData == null){throw new UnauthorizedException("Invalid auth token");}
        return authData;
    }

    public void deleteAuth(AuthData authData) throws DataAccessException {
        authDAO.deleteAuth(authData);
    }
}
