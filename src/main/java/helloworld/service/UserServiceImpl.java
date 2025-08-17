package helloworld.service;

import helloworld.Model.User;
import helloworld.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public UserServiceImpl() {
        // Initialize demo user if it doesn't exist
        initializeDemoUser();
    }
    
    private void initializeDemoUser() {
        if (!userRepository.existsByUsername("user123")) {
            User demoUser = new User("user123", "user123@example.com", "securepass");
            demoUser.setPassword(passwordEncoder.encode("securepass"));
            demoUser.setCreatedAt(LocalDateTime.now());
            demoUser.setUpdatedAt(LocalDateTime.now());
            userRepository.save(demoUser);
        }
    }
    
    @Override
    public User findByUsername(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        return userOpt.orElse(null);
    }
    
    @Override
    public User save(User user) {
        if (user.getId() == null) {
            user.setCreatedAt(LocalDateTime.now());
        }
        user.setUpdatedAt(LocalDateTime.now());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
    
    @Override
    public boolean authenticateUser(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.isActive()) {
                return passwordEncoder.matches(password, user.getPassword());
            }
        }
        return false;
    }
}
