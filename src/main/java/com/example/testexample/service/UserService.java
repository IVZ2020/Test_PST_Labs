package com.example.testexample.service;

import com.example.testexample.dao.entity.Car;
import com.example.testexample.dao.entity.Job;
import com.example.testexample.dao.entity.User;
import com.example.testexample.dao.repository.CarRepository;
import com.example.testexample.dao.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public List<User> findAll(Pageable pagingSort) {
        return userRepository.findAll(pagingSort).getContent();
    }

    @Transactional
    public Optional<User> getById(long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public void deleteById(long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }
}
