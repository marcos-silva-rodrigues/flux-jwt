package com.silva.marcos.fluxjwt.handler;

import com.silva.marcos.fluxjwt.config.JwtUtils;
import com.silva.marcos.fluxjwt.model.User;
import com.silva.marcos.fluxjwt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Service
public class AuthHandler {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final BCryptPasswordEncoder encoder;

    @Autowired
    private final JwtUtils jwtUtils;

    public AuthHandler(UserRepository userRepository, BCryptPasswordEncoder encoder, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
    }

    public Mono<ServerResponse> signUp(ServerRequest request) {
        Mono<User> userMono = request.bodyToMono(User.class);

        return userMono.map(u -> {
            String passwordEncoded = encoder.encode(u.getPassword());
            User newUser = new User(u.getUsername(), passwordEncoded);
            return newUser;
        }).flatMap(this.userRepository::save).flatMap(user -> ServerResponse.ok().body(BodyInserters.fromValue(user)));
    }

    public Mono<ServerResponse> login(ServerRequest request) {
        Mono<User> userMono = request.bodyToMono(User.class);
        return userMono.flatMap(u -> this.userRepository.findByUsername(u.getUsername()).flatMap(user -> {
            boolean isMatcherPasswords = this.isMatcherPasswords(u.getPassword(), user.getPassword());

            if (isMatcherPasswords) {
                String token = this.jwtUtils.genToken(user);
                return ServerResponse.ok().body(BodyInserters.fromValue(token));
            } else {
                return ServerResponse.badRequest().body(BodyInserters.fromValue("Invalid credentials"));
            }
        }).switchIfEmpty(ServerResponse.badRequest().body(BodyInserters.fromValue("User does not exists"))));
    }

    private boolean isMatcherPasswords(String rawPassword, String comparePasswordEncoded) {
        return this.encoder.matches(rawPassword, comparePasswordEncoded);
    }
}
