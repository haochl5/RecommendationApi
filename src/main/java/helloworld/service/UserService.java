package helloworld.service;

import helloworld.Model.User;

public interface UserService {
    
    User findByUsername(String username);
    
    User save(User user);
    
    boolean authenticateUser(String username, String password);
}
