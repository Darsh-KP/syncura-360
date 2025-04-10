package com.syncura360.security;

import com.syncura360.model.Staff;
import com.syncura360.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

/**
 * Service class that implements UserDetailsService to load staff user details from the database.
 * Used for authentication and authorization in Spring Security.
 *
 * @author Benjamin Leiby
 */
@Service
public class StaffDetailsService implements UserDetailsService {

    @Autowired
    private StaffRepository staffRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {


        Optional<Staff> staffOptional = staffRepository.findByUsername(username);
        Staff staff = staffOptional.orElseThrow(() -> new UsernameNotFoundException("Staff member not found with username: " + username));

        return new org.springframework.security.core.userdetails.User(
                staff.getUsername(),
                staff.getPasswordHash(),
                getAuthorities(staff.getRole().getValue())
        );

    }

    private Collection<? extends GrantedAuthority> getAuthorities(String role) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role));
        return authorities;
    }

}
