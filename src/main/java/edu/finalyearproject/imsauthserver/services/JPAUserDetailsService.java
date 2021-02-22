package edu.finalyearproject.imsauthserver.services;

import edu.finalyearproject.imsauthserver.models.SecurityUser;
import edu.finalyearproject.imsauthserver.models.User;
import edu.finalyearproject.imsauthserver.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

/**
 * Class for retrieving a User from the userdb using the JPA Java data persistence API.
 */
public class JPAUserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService
{
    @Autowired
    private UserRepository userRepository;

    /**
     * Overidden method for returning the User instance from the Spring Context
     * @param username - The username of the User to return
     * @return UserDetails - Spring Security object relating to a User object
     * @throws UsernameNotFoundException - If no User record is found.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
//        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//        System.out.println(passwordEncoder.encode("1234"));
        Optional<User> user = userRepository.findByUsernameIgnoreCase(username);
        User u = user.orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new SecurityUser(u);
    }
}

