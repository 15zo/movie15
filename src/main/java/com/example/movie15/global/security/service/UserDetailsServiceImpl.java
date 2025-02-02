package com.example.movie15.global.security.service;

import com.example.movie15.domain.user.entity.User;
import com.example.movie15.domain.user.repository.UserRepository;
import com.example.movie15.global.exception.BadValueException;
import com.example.movie15.global.exception.ExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    // userId로 사용자 정보를 검색하고 반환
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        try {
            Long id = Long.valueOf(userId);

            Optional<User> user = userRepository.findById(id);

            if (user.isEmpty()) {
                throw new BadValueException(ExceptionType.USER_NOT_FOUND);
            }
            return new UserDetailsImpl(user.get());
        } catch (NumberFormatException e) {
            throw new BadValueException(ExceptionType.USER_NOT_FOUND);
        }
    }
}