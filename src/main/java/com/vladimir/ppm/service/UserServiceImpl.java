package com.vladimir.ppm.service;

import com.vladimir.ppm.domain.User;
import com.vladimir.ppm.dto.TokenDto;
import com.vladimir.ppm.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @Override
    public TokenDto login(String login, String password, String frontPublicKey, String remoteAddr, String userAgent) {
        User user = userRepository.findUserByLogin(login);
        if (user == null || !encoder.matches(password, user.getPassword())) {
            return TokenDto.builder().message("LE1").build();
        }
        //TODO
        return null;
    }
}
