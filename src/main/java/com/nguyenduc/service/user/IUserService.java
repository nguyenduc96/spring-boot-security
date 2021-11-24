package com.nguyenduc.service.user;

import com.nguyenduc.model.user.User;
import com.nguyenduc.service.IBaseService;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

public interface IUserService extends IBaseService<User>, UserDetailsService {
    Optional<User> findByUsername(String username);
}
