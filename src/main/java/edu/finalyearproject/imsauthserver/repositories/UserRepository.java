/**
 * Copyright (C) Alec R. C. Smith - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Alec Smith <alec.smith@uea.ac.uk>, 2020-2021
 */
package edu.finalyearproject.imsauthserver.repositories;

import edu.finalyearproject.imsauthserver.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * JPA Repository to perform auto-generated queries on the User table in the database.
 */
public interface UserRepository extends JpaRepository<User, Integer>
{
    /**
     * Finds the User record in the user database based on the username field.
     * @param username String - the username of the User record to return
     * @return Optional<User> - An Optional containing a User object if it exists in the database
     */
    Optional<User> findByUsernameIgnoreCase(String username);

    List<User> findAll();
}

