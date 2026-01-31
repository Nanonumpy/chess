package dataaccess;

import model.UserData;

public interface UserDAO {

    void clear();

    void createUser(UserData data);

    UserData getUser(String username);
}
