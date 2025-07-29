package com.epam.gymcrm.domain.service;

import com.epam.gymcrm.api.payload.request.LoginRequest;
import com.epam.gymcrm.db.entity.UserEntity;
import com.epam.gymcrm.db.repository.UserRepository;
import com.epam.gymcrm.domain.mapper.UserDomainMapper;
import com.epam.gymcrm.domain.model.User;
import com.epam.gymcrm.exception.BadRequestException;
import com.epam.gymcrm.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void login(LoginRequest request) {
        String username = request.username();
        logger.info("Login attempt. username={}", username);

        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("Login failed: user not found. username={}", username);
                    return new NotFoundException(
                            String.format("Login failed: User not found for login. (username=%s)", username)
                    );
                });

        User user = UserDomainMapper.toUser(userEntity);

        if (!user.checkPassword(request.password())) {
            logger.warn("Login failed: invalid password. username={}", username);
            throw new BadRequestException("Login failed: Invalid credentials.");
        }

        if (!user.isActive()) {
            logger.warn("Login failed: user not active. username={}", request.username());
            throw new BadRequestException("Login failed: User is not active.");
        }

        logger.info("Login success! username={}", request.username());
    }
}
