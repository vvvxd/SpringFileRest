package com.example.springfilerest.rest.v1;

import com.example.springfilerest.dto.AuthenticationRequestDto;
import com.example.springfilerest.dto.UserRegistrationDto;
import com.example.springfilerest.model.Status;
import com.example.springfilerest.model.User;
import com.example.springfilerest.security.jwt.JwtTokenProvider;
import com.example.springfilerest.service.UserService;
import com.example.springfilerest.util.ErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/auth/")
public class AuthenticationRestControllerV1 {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final ErrorResponse errorResponse;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthenticationRestControllerV1(AuthenticationManager authenticationManager,
                                          JwtTokenProvider jwtTokenProvider,
                                          UserService userService,
                                          ErrorResponse errorResponse,
                                          PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
        this.errorResponse = errorResponse;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("singin")
    public ResponseEntity login(@RequestBody AuthenticationRequestDto requestDto) {
        try {
            String username = requestDto.getUsername();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, requestDto.getPassword()));
            User user = userService.findByUserName(username);
            if (user == null) {
                return new ResponseEntity("User with username : " + username + " not found", HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok(jwtTokenProvider.getResponseWithToken(user));
        } catch (AuthenticationException e) {
            return new ResponseEntity("Invalid username or password", HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @PostMapping(value = "singup")
    public ResponseEntity registerUser(@RequestBody UserRegistrationDto userRegistrationDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity(errorResponse.registrationResponse(bindingResult), HttpStatus.BAD_REQUEST);
        }
        User user = userRegistrationDto.dtoToUser();
        userService.register(user);
        return new ResponseEntity(jwtTokenProvider.getResponseWithToken(user), HttpStatus.OK);
    }

    @PostMapping(value = "restore")
    public ResponseEntity restoreAccount(@RequestBody AuthenticationRequestDto requestDto) {
        try {
            String username = requestDto.getUsername();
            User user = userService.findByUserName(username);
            if (user == null) {
                return new ResponseEntity("User with username : " + username + " not found", HttpStatus.NOT_FOUND);
            }
            user.setStatus(Status.ACTIVE);
            user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
            userService.updateStatus(user);
            return ResponseEntity.ok(jwtTokenProvider.getResponseWithToken(user));
        } catch (AuthenticationException e) {
            return new ResponseEntity("Invalid username or password", HttpStatus.NOT_ACCEPTABLE);
        }
    }
}
