/**
 * Copyright (C) Alec R. C. Smith - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Alec Smith <alec.smith@uea.ac.uk>, 2020-2021
 */
package edu.finalyearproject.imsauthserver.repositories;

import edu.finalyearproject.imsauthserver.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * JPA Repository to perform auto-generated queries on the Role table in the database.
 */
public interface RoleRepository extends JpaRepository<Role, Integer>
{
        /**
         * Finds the Role record in the user database based on the name field.
         * @param name String - the name of the Role record to return
         * @return Optional<Role> - An Optional containing a Role object if it exists in the database
         */
        Optional<Role> findBynameIgnoreCase(String name);

        List<Role> findAll();
}
