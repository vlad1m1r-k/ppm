package com.vladimir.ppm.service;

import com.vladimir.ppm.domain.Group;
import com.vladimir.ppm.domain.PwdGenSettings;
import com.vladimir.ppm.domain.Token;
import com.vladimir.ppm.domain.User;
import com.vladimir.ppm.domain.UserStatus;
import com.vladimir.ppm.dto.GroupDto;
import com.vladimir.ppm.dto.MessageDto;
import com.vladimir.ppm.dto.TokenDto;
import com.vladimir.ppm.dto.UserDto;
import com.vladimir.ppm.provider.CryptoProvider;
import com.vladimir.ppm.provider.SecurityProvider;
import com.vladimir.ppm.provider.SettingsProvider;
import com.vladimir.ppm.repository.PwdGenSettingsRepository;
import com.vladimir.ppm.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.IpAddressMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    private final TokenService tokenService;
    private final CryptoProvider cryptoProvider;
    private final ValidatorService validatorService;
    private final SettingsProvider settingsProvider;
    private final SecurityProvider securityProvider;
    private final PwdGenSettingsRepository pwdGenSettingsRepository;

    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder encoder, TokenService tokenService,
                           CryptoProvider cryptoProvider, ValidatorService validatorService, SettingsProvider settingsProvider,
                           SecurityProvider securityProvider, PwdGenSettingsRepository pwdGenSettingsRepository) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.tokenService = tokenService;
        this.cryptoProvider = cryptoProvider;
        this.validatorService = validatorService;
        this.settingsProvider = settingsProvider;
        this.securityProvider = securityProvider;
        this.pwdGenSettingsRepository = pwdGenSettingsRepository;
    }

    @Override
    @Transactional
    public TokenDto login(String login, String password, String remoteAddr, String userAgent) {
        User user = userRepository.findUserByLogin(login);
        if (user == null) {
            securityProvider.registerLoginAttempt(remoteAddr, false);
            return TokenDto.builder().message("lfe1").build();
        }
        if (!isIpAllowed(user, remoteAddr)) {
            securityProvider.registerLoginAttempt(remoteAddr, false);
            return TokenDto.builder().message("lfe4").build();
        }
        if (!encoder.matches(password, user.getPassword())) {
            securityProvider.registerPasswordAttempt(user.getId(), false);
            return TokenDto.builder().message("lfe1").build();
        }
        if (!user.isEnabled()) {
            return TokenDto.builder().message("lfe3").build();
        }
        if (cryptoProvider.isSystemClosed() && !isAdmin(user.getGroups())) {
            return TokenDto.builder().message("lfe2").build();
        }
        Token token = tokenService.getToken(user, remoteAddr, userAgent);
        long tokenLifeTime = token.getLifeTime();
        String encryptedToken = tokenService.encrypt(token);
        securityProvider.registerLoginAttempt(remoteAddr, true);
        securityProvider.registerPasswordAttempt(user.getId(), true);
        return TokenDto.builder()
                .lifeTime(tokenLifeTime)
                .token(encryptedToken)
                .adminSettings(isAdmin(user.getGroups()))
                .systemClosed(cryptoProvider.isSystemClosed())
                .build();
    }

    @Override
    @Transactional
    public TokenDto renewToken(Token token) {
        User user = userRepository.findUserByLogin(token.getLogin());
        Token newToken = tokenService.getToken(user, token.getRemoteAddr(), token.getUserAgent());
        long tokenLifeTime = newToken.getLifeTime();
        String encryptedToken = tokenService.encrypt(newToken);
        return TokenDto.builder()
                .lifeTime(tokenLifeTime)
                .token(encryptedToken)
                .adminSettings(isAdmin(user.getGroups()))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(long userId) {
        return userRepository.getOne(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Group> getGroups(Token token) {
        User user = userRepository.findUserByLogin(token.getLogin());
        return user.getGroups();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAdmin(Token token) {
        User user = userRepository.findUserByLogin(token.getLogin());
        return isAdmin(user.getGroups());
    }

    @Override
    @Transactional
    public MessageDto changePassword(Token token, String newPwd) {
        User user = userRepository.findUserByLogin(token.getLogin());
        if (!validatorService.validatePwdLength(newPwd, settingsProvider.getPwdMinLength())) {
            return MessageDto.builder().message("usse2").build();
        }
        if (encoder.matches(newPwd, user.getPassword())) {
            return MessageDto.builder().message("usse1").build();
        }
        if (settingsProvider.getPwdComplexity() && !validatorService.validatePwdComplexity(newPwd)) {
            return MessageDto.builder().message("usse3").build();
        }
        if (settingsProvider.getPwdSpecialChar() && !validatorService.validatePwdSpecialChar(newPwd)) {
            return MessageDto.builder().message("usse4").build();
        }
        if (!validatorService.validatePwdLoginIncluded(newPwd, token.getLogin())) {
            return MessageDto.builder().message("usse6").build();
        }
        if (!validatorService.validatePwdRepeatedChars(newPwd)) {
            return MessageDto.builder().message("usse7").build();
        }
        user.setPassword(encoder.encode(newPwd));
        return MessageDto.builder().build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsers(Token token, String sort) {
        if (isAdmin(token)) {
            String sortField = sort.substring(0, sort.indexOf(","));
            Sort.Direction sortDirection = Sort.Direction.fromString(sort.substring(sort.indexOf(",") + 1));
            List<User> users = userRepository.findAll(Sort.by(sortDirection, sortField));
            return users.stream().map(u -> UserDto.builder()
                    .id(u.getId())
                    .login(u.getLogin())
                    .status(u.getStatus())
                    .groups(u.getGroups().stream().map(g -> GroupDto.builder()
                            .id(g.getId())
                            .name(g.getName())
                            .adminSettings(g.isAdminSettings())
                            .build())
                            .collect(Collectors.toList()))
                    .build())
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUserEnabled(Token token) {
        User user = userRepository.findUserByLogin(token.getLogin());
        return user.isEnabled();
    }

    @Override
    @Transactional
    public MessageDto addUser(Token token, String login, String pwd, UserStatus status) {
        if (isAdmin(token)) {
            if (!validatorService.validateString(login)) {
                return MessageDto.builder().message("use1").build();
            }
            if (!validatorService.validateString(pwd)) {
                return MessageDto.builder().message("use2").build();
            }
            User user = userRepository.findUserByLogin(login);
            if (user != null) {
                return MessageDto.builder().message("use3").build();
            }
            user = new User(login, encoder.encode(pwd), status);
            userRepository.save(user);
        }
        return MessageDto.builder().build();
    }

    @Override
    @Transactional
    public MessageDto editUser(Token token, long userId, String login, String pwd, UserStatus status) {
        if (isAdmin(token)) {
            if (!validatorService.validateString(login)) {
                return MessageDto.builder().message("use1").build();
            }
            User user = userRepository.getOne(userId);
            if (pwd != null && pwd.length() > 0) {
                user.setPassword(encoder.encode(pwd));
            }
            if (!user.getLogin().equals(login) && userRepository.findUserByLogin(login) != null) {
                return MessageDto.builder().message("use3").build();
            }
            user.setLogin(login);
            user.setStatus(status);
        }
        return MessageDto.builder().build();
    }

    @Override
    @Transactional
    public MessageDto deleteUser(Token token, long userId) {
        if (isAdmin(token)) {
            User user = userRepository.getOne(userId);
            user.getGroups().forEach(g -> g.getUsers().remove(user));
            userRepository.delete(user);
        }
        return MessageDto.builder().build();
    }

    @Override
    @Transactional
    public MessageDto addAllowedIp(Token token, long userId, String ip) {
        if (isAdmin(token) && validatorService.validateIpOrSubnet(ip)) {
            User user = userRepository.getOne(userId);
            user.getAllowedIps().add(ip);
        }
        return MessageDto.builder().build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllowedIp(Token token, long userId) {
        if (isAdmin(token)) {
            User user = userRepository.getOne(userId);
            return user.getAllowedIps().stream().sorted().collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    @Transactional
    public void removeAllowedIp(Token token, long userId, String ip) {
        if (isAdmin(token) && validatorService.validateIpOrSubnet(ip)) {
            User user = userRepository.getOne(userId);
            user.getAllowedIps().remove(ip);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PwdGenSettings getPwdGenSettings(Token token) {
        User user = userRepository.findUserByLogin(token.getLogin());
        return user.getPwdGenSettings() == null ? new PwdGenSettings() : user.getPwdGenSettings();
    }

    @Override
    @Transactional
    public MessageDto setPwdGenSettings(Token token, int pwdLength, boolean numbers, boolean symbols) {
        if (pwdLength > 0) {
            User user = userRepository.findUserByLogin(token.getLogin());
            PwdGenSettings settings = user.getPwdGenSettings();
            if (settings == null) {
                settings = new PwdGenSettings();
                pwdGenSettingsRepository.save(settings);
            }
            settings.setPwdLength(pwdLength);
            settings.setNumbers(numbers);
            settings.setSymbols(symbols);
            user.setPwdGenSettings(settings);
            return MessageDto.builder().build();
        }
        return MessageDto.builder().message("pge1").build();
    }

    private boolean isAdmin(Set<Group> groups) {
        for (Group group : groups) {
            if (group.isAdminSettings()) {
                return true;
            }
        }
        return false;
    }

    private boolean isIpAllowed(User user, String remoteAddr) {
        if (user.getAllowedIps().size() == 0) {
            return true;
        }
        for (String ip : user.getAllowedIps()) {
            IpAddressMatcher matcher = new IpAddressMatcher(ip);
            if (matcher.matches(remoteAddr)) {
                return true;
            }
        }
        return false;
    }
}
