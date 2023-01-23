package com.learn.jwt.demo.auth;


import com.learn.jwt.demo.config.JwtService;
import com.learn.jwt.demo.user.Role;
import com.learn.jwt.demo.user.User;
import com.learn.jwt.demo.user.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {


    private final UserRepository dbUser;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(UserRepository dbUser, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.dbUser = dbUser;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthenticationResponse register(RegisterRequest req){

        User user= new User();
        user.setFirstName(req.getFirstname());
        user.setEmail(req.getEmail());
        user.setLastName(req.getLastname());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole(Role.USER);

        dbUser.save(user);

        var jwtToken=jwtService.generateToken(user);
        AuthenticationResponse authenticationResponse= new AuthenticationResponse();
        authenticationResponse.setToken(jwtToken);
//
        return authenticationResponse;
    }

    public AuthenticationResponse authenticate(AuthenticationRequest req){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );
        var user = dbUser.findByEmail(req.getEmail()).orElseThrow();
        var jwtToken=jwtService.generateToken(user);
        AuthenticationResponse authenticationResponse= new AuthenticationResponse();
        authenticationResponse.setToken(jwtToken);
        return authenticationResponse;
    }
}
