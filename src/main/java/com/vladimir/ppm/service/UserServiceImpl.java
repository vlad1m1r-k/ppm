package com.vladimir.ppm.service;

import com.vladimir.ppm.dto.TokenDto;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public TokenDto login(String login, String password, String frontPublicKey, String remoteAddr, String userAgent) {

        //TODO
        return null;
    }

    //TODO Get User
}
