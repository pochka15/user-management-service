package com.pochka15.itra4.service;

import com.pochka15.itra4.domain.user.User;
import com.pochka15.itra4.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

//ok?
@Service
public class UserService {
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    private User withUpdatedLoginDate(User user) {
        user.getActivity().setLastLoginDate(LocalDateTime.now());
        return user;
    }

    public void refreshLastLoginDate(String userName) {
        userRepo.findByName(userName).ifPresent(user -> userRepo.save(withUpdatedLoginDate(user)));
    }

    public List<User> fetchAllUsers() {
        return StreamSupport.stream(userRepo.findAll().spliterator(), true)
                .collect(Collectors.toList());
    }

    public void deleteAll(Iterable<Long> userIds) {
        userRepo.deleteAll(userRepo.findAllById(userIds));
    }

    public Optional<User> findByName(String name) {
        return userRepo.findByName(name);
    }

    private void updateUsersStatus(Collection<Long> ids, boolean enabled) {
        userRepo.saveAll(StreamSupport.stream(userRepo.findAllById(ids).spliterator(), true)
                                 .peek(user -> user.setEnabled(enabled))
                                 .collect(Collectors.toList())
        );
    }

    public void unblockUsersByIds(Collection<Long> ids) {
        updateUsersStatus(ids, true);
    }

    public void blockUsersByIds(Collection<Long> ids) {
        updateUsersStatus(ids, false);
    }

    public void saveNew(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepo.save(user);
    }
}
