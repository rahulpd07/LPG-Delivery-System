package com.pbg.lpg_delivery.security;

import com.pbg.lpg_delivery.model.entity.UserEntity;
import com.pbg.lpg_delivery.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Retrieve the user from the database by username
        Optional<UserEntity> userOptional = userRepository.findByUsername(username);

        if (!userOptional.isPresent()) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        UserEntity user = userOptional.get();

        // You may want to map the roles/authorities here based on your requirements
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        // Assuming user.getRole() returns a string role like "USER", you can modify this as needed.
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole())); // If you have role stored, map it here

        // Return a UserDetails object with username, password, and authorities (roles)
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }

}

