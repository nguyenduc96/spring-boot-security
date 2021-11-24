package com.nguyenduc.controller;

import com.nguyenduc.model.user.JwtResponse;
import com.nguyenduc.model.user.Role;
import com.nguyenduc.model.user.User;
import com.nguyenduc.model.user.UserPrinciple;
import com.nguyenduc.service.user.IUserService;
import com.nguyenduc.service.user.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api")
public class LoginController {
    @Autowired
    private IUserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService JwtService;


    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = JwtService.generateTokenLogin(authentication);
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        User currentUser = userService.findByUsername(user.getUsername()).get();
        JwtResponse jwtResponse = new JwtResponse(
                jwt,
                currentUser.getId(),
                currentUser.getUsername(),
                userPrinciple.getAuthorities()
        );

        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        if (user.getRole() == null) {
            List<Role> roles = new ArrayList<>();
            roles.add(new Role(2L, null));
            roles.add(new Role(1L, null));
            user.setRole(roles);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User userRegister = userService.save(user);
        if (userRegister == null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(userRegister, HttpStatus.CREATED);
    }
}
