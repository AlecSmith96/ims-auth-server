package edu.finalyearproject.imsauthserver.repositories;

import edu.finalyearproject.imsauthserver.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Interface for defining requests to the User table in the database.
 */
public interface UserRepository extends JpaRepository<User, Integer>
{
    /**
     * Finds the User record in the user database based on the username field.
     * @param username String - the username of the User record to return
     * @return Optional<User> - An Optional containing a User object if it exists in the databse
     */
    Optional<User> findByUsernameIgnoreCase(String username);

    List<User> findAll();
}

