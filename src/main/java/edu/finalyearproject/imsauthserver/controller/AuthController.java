/**
 * Copyright (C) Alec R. C. Smith - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Alec Smith <alec.smith@uea.ac.uk>, 2020-2021
 */
package edu.finalyearproject.imsauthserver.controller;

import edu.finalyearproject.imsauthserver.models.Role;
import edu.finalyearproject.imsauthserver.models.User;
import edu.finalyearproject.imsauthserver.repositories.RoleRepository;
import edu.finalyearproject.imsauthserver.repositories.UserRepository;
import edu.finalyearproject.imsauthserver.requests.UserRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * REST controller for all requests relating to Users.
 */
@RestController
public class AuthController
{
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private static final String DEFAULT_PASSWORD = "password";
    private Logger log = LoggerFactory.getLogger(AuthController.class);

    /**
     * POST mapping for creating a new user in the system.
     * @param userRequest - object containing new users credentials.
     */
    @PostMapping("/users/add")
    public User createUser(@RequestBody UserRequest userRequest)
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
        return user;
    }

    /**
     * GET method for returning all users in the database.
     * @return List<User> - List of User objects.
     */
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

    /**
     * POST method used to reset the password for a User.
     * @param id - the id of the User to reset the password of.
     */
    @PostMapping("/users/password-reset/{id}")
    public User resetPasswordForUser(@PathVariable int id)
    {
        String password = passwordEncoder.encode(DEFAULT_PASSWORD);
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent())
        {
            log.info("Resetting password for "+user.get().getUsername()+"...");
            User returnedUser = user.get();
            returnedUser.setPassword(password);
            userRepository.save(returnedUser);
            return returnedUser;
        }
        return new User();
    }

    /**
     * POST method used to update a Users details.
     * @param id - the id of the User to reset the password of.
     * @param userRequest - the updated User details.
     */
    @PostMapping("/users/update-details/{id}")
    public User updateUserDetails(@PathVariable int id, @RequestBody UserRequest userRequest)
    {
        Optional<User> user = userRepository.findById(id);
        Optional<Role> role = roleRepository.findBynameIgnoreCase(userRequest.getRole());
        Set<Role> roles = new HashSet<>();
        role.ifPresent(roles::add);

        if (user.isPresent())
        {
            log.info("Updating "+ user.get().getUsername() +"'s details...");
            User updatedUser = user.get();
            updatedUser.setUsername(userRequest.getUsername());
            updatedUser.setEmail(userRequest.getEmail());
            updatedUser.setRoles(roles);
            userRepository.save(updatedUser);
            return updatedUser;
        }

        return new User();
    }

    /**
     * POST method to update the password of a User.
     * @param username - the username of the User.
     * @param password - the new password to change to.
     */
    @PostMapping("/users/password-change/{username}")
    public User updateUserPassword(@PathVariable String username, @RequestBody String password)
    {
        log.info("Updating password for "+username+"...");

        String encodedPassword = passwordEncoder.encode(password);
        Optional<User> user = userRepository.findByUsernameIgnoreCase(username);
        if (user.isPresent())
        {
            User foundUser = user.get();
            foundUser.setPassword(encodedPassword);
            userRepository.save(foundUser);
            return foundUser;
        }

        return new User();
    }
}
