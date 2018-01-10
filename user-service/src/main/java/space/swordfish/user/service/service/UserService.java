package space.swordfish.user.service.service;

import org.springframework.stereotype.Service;
import space.swordfish.user.service.domain.User;

@Service
public interface UserService {

    void create(User user);

    void update(User user);

    void delete(User user);

}
