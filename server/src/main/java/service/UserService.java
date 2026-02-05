package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

public class UserService {

    private final UserDAO userDAO;
    private final AuthService authService;

    public UserService(UserDAO userDAO, AuthDAO authDAO){
        this.userDAO = userDAO;
        this.authService = new AuthService(authDAO);
    }

    public void clear() throws DataAccessException {
        userDAO.clear();
    }

    public LoginResult register(UserData userData) throws AlreadyTakenException, DataAccessException {
        UserData checkData = userDAO.getUser(userData.username());

        if(checkData != null){throw new AlreadyTakenException("Username already taken");}

        userDAO.createUser(userData);

        AuthData loginAuth = authService.generateAuth(userData.username());
        return new LoginResult(loginAuth.username(), loginAuth.authToken());

    }

    public LoginResult login(LoginRequest loginRequest) throws UnauthorizedException, DataAccessException {
        UserData checkData = userDAO.getUser(loginRequest.username());
        if(checkData == null || !BCrypt.checkpw(loginRequest.password(), checkData.password())){
            throw new UnauthorizedException("Invalid credentials");
        }

        AuthData loginAuth = authService.generateAuth(loginRequest.username());
        return new LoginResult(loginAuth.username(), loginAuth.authToken());
    }

    public void logout(String authToken) throws UnauthorizedException, DataAccessException {
        AuthData authData = authService.validateAuth(authToken);
        authService.deleteAuth(authData);
    }
}
