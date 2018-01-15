package space.swordfish.user.service.service;

import space.swordfish.user.service.domain.User;

public interface UserService {

    void create(User user);

    void update(User user);

    void delete(User user);

}
