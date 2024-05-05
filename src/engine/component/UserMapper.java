package engine.component;

import engine.model.AppUser;
import engine.model.UserRequest;

public class UserMapper {

    public static AppUser mapToUser(UserRequest userRequest) {
        AppUser appUser = new AppUser();
        appUser.setUsername(userRequest.getEmail());
        appUser.setPassword(userRequest.getPassword());
        return appUser;
    }

}
