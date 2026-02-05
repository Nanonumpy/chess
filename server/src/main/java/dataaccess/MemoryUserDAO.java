package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO{
    final private HashMap<String, UserData> users = new HashMap<>();

    @Override
    public void clear() {
        users.clear();
    }

    @Override
    public void createUser(UserData data) {
        users.put(data.username(), new UserData(data.username(),
                BCrypt.hashpw(data.password(), BCrypt.gensalt()), data.email()));
    }

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }

}
