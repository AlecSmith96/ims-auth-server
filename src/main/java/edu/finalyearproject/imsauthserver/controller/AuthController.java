package edu.finalyearproject.imsauthserver.controller;

import edu.finalyearproject.imsauthserver.models.Role;
import edu.finalyearproject.imsauthserver.models.User;
import edu.finalyearproject.imsauthserver.repositories.RoleRepository;
import edu.finalyearproject.imsauthserver.repositories.UserRepository;
import edu.finalyearproject.imsauthserver.requests.UserRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class AuthController
{
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private Logger log = LoggerFactory.getLogger(AuthController.class);

    /**
     * POST mapping for creating a new user in the system.
     * @param userRequest - object containing new users credentials.
     */
    @PostMapping("/users/add")
    public void createUser(@RequestBody UserRequest userRequest)
    {
        log.info("Adding user '"+userRequest.getUsername()+"' to database...");
        Optional<Role> role = roleRepository.findBynameIgnoreCase(userRequest.getRole());
        Set<Role> roles = new HashSet<>();
        if (role.isPresent())
        {
            roles.add(role.get());
        }

        User user = new User(userRequest.getUsername(), userRequest.getEmail(),
                                                            passwordEncoder.encode(userRequest.getPassword()), roles);
        userRepository.save(user);
    }

    @GetMapping("/users/all")
    public List<User> getUsers()
    {
        log.info("Retrieving Users...");
        return userRepository.findAll();
    }

    /**
     * GET mapping to return all user roles from the database.
     * @return - List<Role> - List of Role objects.
     */
    @GetMapping("/users/roles")
    public List<Role> getRoles()
    {
        log.info("Retrieving all user Roles from database...");
        return roleRepository.findAll();
    }

    @PostMapping("/users/password-reset/{id}")
    public void resetPasswordForUser(@PathVariable int id)
    {
        String password = passwordEncoder.encode("password");
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent())
        {
            log.info("Resetting password for "+user.get().getUsername()+"...");
            User returnedUser = user.get();
            returnedUser.setPassword(password);
            userRepository.save(returnedUser);
        }
    }

    @PostMapping("/users/update-details/{id}")
    public void updateUserDetails(@PathVariable int id, @RequestBody UserRequest userRequest)
    {
        Optional<User> user = userRepository.findById(id);
        log.info("Updating "+ user.get().getUsername() +"'s details...");
        Optional<Role> role = roleRepository.findBynameIgnoreCase(userRequest.getRole());
        Set<Role> roles = new HashSet<>();
        role.ifPresent(roles::add);

        if (user.isPresent())
        {
            User updatedUser = user.get();
            updatedUser.setUsername(userRequest.getUsername());
            updatedUser.setEmail(userRequest.getEmail());
            updatedUser.setRoles(roles);
            userRepository.save(updatedUser);
        }
    }

    @PostMapping("/users/password-change/{username}")
    public void updateUserPassword(@PathVariable String username, @RequestBody String password)
    {
        log.info("Updating password for "+username+"...");

        String encodedPassword = passwordEncoder.encode(password);
        Optional<User> user = userRepository.findByUsernameIgnoreCase(username);
        if (user.isPresent())
        {
            User foundUser = user.get();
            foundUser.setPassword(encodedPassword);
            userRepository.save(foundUser);
        }
    }
}
