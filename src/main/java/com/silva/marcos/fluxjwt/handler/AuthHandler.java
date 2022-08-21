package com.silva.marcos.fluxjwt.handler;

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
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    public AuthHandler(UserRepository userRepository, BCryptPasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    public Mono<ServerResponse> signUp(ServerRequest request) {
        Mono<User> userMono = request.bodyToMono(User.class);

        return userMono
                .map(u -> {
                    String passwordEncoded = encoder.encode(u.getPassword());
                    User newUser = new User(u.getUsername(), passwordEncoded);
                    return newUser;
                })
                .flatMap(this.userRepository::save)
                .flatMap(user -> ServerResponse.ok().body(BodyInserters.fromValue(user)));
    }
}
