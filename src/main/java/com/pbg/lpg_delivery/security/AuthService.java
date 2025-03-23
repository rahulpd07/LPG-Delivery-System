package com.pbg.lpg_delivery.security;

import com.pbg.lpg_delivery.common.Role;
import com.pbg.lpg_delivery.exceptionHandler.LpgException;
import com.pbg.lpg_delivery.exceptionHandler.ParentException;
import com.pbg.lpg_delivery.exceptionHandler.UserUnauthorizedException;
import com.pbg.lpg_delivery.model.entity.UserEntity;
import com.pbg.lpg_delivery.model.request.SignupRequest;
import com.pbg.lpg_delivery.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.Optional;



@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtils;
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }
    private static final EnumSet<Role> ROLE_TYPE = EnumSet.of(Role.ADMIN, Role.DELIVERY_PERSON,Role.CUSTOMER);


    private boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }


    public void registerUser(SignupRequest request) {
        try {

                if (isNullOrEmpty(request.username()) || isNullOrEmpty(request.password()) ||  isNullOrEmpty(request.email()) || isNullOrEmpty(request.address()) || isNullOrEmpty(request.phoneNumber())) {
                    logger.warn("Registration failed: Missing required fields for user {}", request.username());
                    throw new LpgException("ST-1003", "Invalid user details provided");
                }

                if (!ROLE_TYPE.contains(request.role())) {
                    logger.warn("Registration failed: Invalid role '{}' for user {}", request.role(), request.username());
                    throw new LpgException("ST-1004", "Invalid role provided");
                }

            Optional<UserEntity> existingUser = userRepository.findByUsername(request.username());

            if (existingUser.isPresent()) {
                logger.warn("Registration failed: User already exists with username {}", request.username());
                throw new LpgException("ST-1001","User already present in DB");
            }

            UserEntity user = UserEntity.mapToEntity(request, passwordEncoder);
            userRepository.save(user);

            logger.info("User registered successfully with username: {}", request.username());

        } catch (LpgException ex) {
            logger.error("User registration error: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logger.error("Unexpected error during user registration: {}", ex.getMessage(), ex);
            throw new ParentException("An unexpected error occurred during user registration", "ST-5001");
        }
    }



    public String authenticateUser(LoginRequest authRequest) {
        try {
            if (isNullOrEmpty(authRequest.username()) || isNullOrEmpty(authRequest.password())) {
                throw new LpgException("AUTH-0001", "Invalid credentials!");
            }
            UserEntity dbUser = userRepository.findByUsername(authRequest.username())
                    .orElseThrow(() -> {
                        logger.warn("Authentication failed: Invalid username {}", authRequest.username());
                        return new LpgException("ST-1002","Invalid username or password");
                    });

            if (!passwordEncoder.matches(authRequest.password(), dbUser.getPassword())) {
                logger.warn("Authentication failed: Incorrect password for user {}", dbUser.getUsername());
                throw new UserUnauthorizedException("ST-1002","Invalid username or password");
            }

            logger.info("Generating token for user: {}", dbUser.getUsername());
            return jwtUtils.generateToken(dbUser.getUsername());

        } catch (LpgException ex) {
            logger.error("User Authentication error: {}", ex.getMessage());
            throw ex;
        }catch (UserUnauthorizedException ex){
            logger.error("Authentication error: {}", ex.getErrorMessage());
            throw ex;
        }
        catch (Exception ex) {
            logger.error("Unexpected error during authentication: {}", ex.getMessage(), ex);
            throw new ParentException("An unexpected error occurred during authentication", "ST-5002");
        }
    }


    public void createDeliveryPersonOrAdmin(SignupRequest request) {
        try {
            Optional<UserEntity> existingUser = userRepository.findByUsername(request.username());

            if (existingUser.isPresent()) {
                logger.warn("Delivery person already exists for the username: {}", request.username());
                throw new LpgException( "ST-1001","User already exists in DB");
            }

            UserEntity user = UserEntity.mapToEntityForUser(request, passwordEncoder);
            userRepository.save(user);

            logger.info("Delivery person created successfully with username: {}", request.username());

        } catch (LpgException ex) {
            logger.error("Business error while creating delivery person: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logger.error("Unexpected error occurred while creating delivery person: {}", ex.getMessage(), ex);
            throw new ParentException("An unexpected error occurred while creating delivery person", "ST-5001");
        }
    }

}

