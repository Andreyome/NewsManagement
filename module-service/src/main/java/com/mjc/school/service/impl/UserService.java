package com.mjc.school.service.impl;

import com.mjc.school.repository.impl.UserRepository;
import com.mjc.school.repository.model.UserModel;
import com.mjc.school.service.dto.UserResponse;
import com.mjc.school.service.dto.UserSignIn;
import com.mjc.school.service.dto.UserSignUp;
import com.mjc.school.service.mapper.UserMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserService implements UserDetailsService {
    @Autowired
    public UserService(UserRepository userRepository, JwtService jwtService, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder,UserMapper userMapper) {
        this.userRepository=userRepository;
        this.jwtService=jwtService;
        this.authenticationManager=authenticationManager;
        this.passwordEncoder=passwordEncoder;
        this.mapper= userMapper;
    }
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper mapper;

    public UserResponse signUp(UserSignUp userSignUp) {
        UserModel userModel = mapper.userDtoToModel(userSignUp);
        if(!userRepository.findByUsername(userModel.getUsername()).isPresent()) {
        userModel.setPassword(passwordEncoder.encode(userModel.getPassword()));
            userRepository.save(userModel);}
        var jwt = jwtService.generateToken(userModel);
        return new UserResponse(jwt);
    }

    public UserResponse signIn(UserSignIn userSignIn) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                userSignIn.username(), userSignIn.password()
        ));
        UserModel user = userRepository.findByUsername(userSignIn.username())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
       var jwt = jwtService.generateToken(user);
            return new UserResponse(jwt);}

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
