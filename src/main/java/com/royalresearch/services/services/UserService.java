package com.royalresearch.services.services;

import com.royalresearch.services.entities.User;
import com.royalresearch.services.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepo;

    @Autowired
    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public void createUser(User user) {
        userRepo.save(user);
    }

    public boolean login(String username , String password) {
        if(userRepo.checkIfUserExists(username,password) != null){
            return true;
        }else{
            return false;
        }
    }

    public User getUserByCredentials(String username, String password){
        return userRepo.checkIfUserExists(username,password);
    }

    public void setLastLoginToNow (User user){
        user.setLastLoginTime(LocalDateTime.now());
        userRepo.save(user);
    }
}
