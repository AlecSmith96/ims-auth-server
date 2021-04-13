/**
 * Copyright (C) Alec R. C. Smith - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Alec Smith <alec.smith@uea.ac.uk>, 2020-2021
 */
package edu.finalyearproject.imsauthserver;

import edu.finalyearproject.imsauthserver.controller.AuthController;
import edu.finalyearproject.imsauthserver.models.Role;
import edu.finalyearproject.imsauthserver.models.User;
import edu.finalyearproject.imsauthserver.repositories.RoleRepository;
import edu.finalyearproject.imsauthserver.repositories.UserRepository;
import edu.finalyearproject.imsauthserver.requests.UserRequest;
import org.checkerframework.checker.nullness.Opt;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AuthControllerTest
{
    private static final List<Role> ROLES = getRoles();
    private static final List<User> USERS = getUsers();
    private static final Role ROLE = new Role(1, "ADMIN");
    private static final User USER = new User("username", "email@email.com", "password", Set.of(ROLE));

    @InjectMocks
    private AuthController target;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    private User createUserResult;
    private List<User> getUsersResult;
    private List<Role> getRolesResult;
    private User resetPasswordForUserResult;
    private User updateUserDetailsResult;
    private User updateUserPasswordResult;

    private Fixture fixture;
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Before
    public void before()
    {
        fixture = new Fixture();
    }

    @Test
    public void createUser_userCreatedSuccessfully()
    {
        fixture.givenRoleRepositoryReturnsOptionalWithRole();
        fixture.whenCreateUserIsCalled();
        fixture.thenAssertUserIsSaved();
    }

    @Test
    public void getUsers_returnsAllUserRecords()
    {
        fixture.givenUserRepositoryReturnsUserRecords();
        fixture.whenGetUsersIsCalled();
        fixture.thenAssertUsersAreReturned();
    }

    @Test
    public void getRoles_returnsAllRoleRecords()
    {
        fixture.givenRoleRepositoryReturnsAllRoles();
        fixture.whenGetRolesIsCalled();
        fixture.thenAssertRolesAreReturned();
    }

    @Test
    public void resetPasswordForUser_resetsPasswordCorrectly()
    {
        fixture.givenUserRepositoryReturnsOptionalWithUser();
        fixture.whenResetPasswordForUserIsCalled();
        fixture.thenAssertUsersPasswordIsReset();
    }

    @Test
    public void resetPasswordForUser_emptyUserReturned()
    {
        fixture.givenUserRepositoryReturnsEmptyOptional();
        fixture.whenResetPasswordForUserIsCalled();
        fixture.thenAssertEmptyUserReturned(resetPasswordForUserResult);
    }

    @Test
    public void updateUserDetails_updatesUserDetailsCorrectly()
    {
        fixture.givenUserRepositoryReturnsOptionalWithUser();
        fixture.givenRoleRepositoryReturnsRoleFromName();
        fixture.whenUpdateUserDetailsIsCalled();
        fixture.thenAssertUsersDetailsAreUpdated();
    }

    @Test
    public void updateUserDetails_returnsEmptyUser()
    {
        fixture.givenUserRepositoryReturnsEmptyOptional();
        fixture.whenUpdateUserDetailsIsCalled();
        fixture.thenAssertEmptyUserReturned(updateUserDetailsResult);
    }

    @Test
    public void updateUserPassword_passwordIsUpdatedCorrectly()
    {
        fixture.givenUserRepositoryReturnsOptionalWithUserByName();
        fixture.whenUpdatedUserPasswordIsCalled("username", "newPassword");
        fixture.thenAssertUserPasswordIsChanged();
    }

    @Test
    public void updateUserPassword_returnsEmptyUser()
    {
        fixture.givenUserRepositoryReturnsEmptyOptionalByName();
        fixture.whenUpdatedUserPasswordIsCalled("aUsername", "newPassword");
        fixture.thenAssertEmptyUserReturned(updateUserPasswordResult);
    }

    private static List<User> getUsers()
    {
        List<User> users = new ArrayList<>();
        users.add(new User("alec", "alec@email.com", "password", Set.of(new Role())));
        users.add(new User("andrew", "andrew@email.com", "password", Set.of(new Role())));
        users.add(new User("andy", "andy@email.com", "password", Set.of(new Role())));
        return users;
    }

    private static List<Role> getRoles()
    {
        List<Role> roles = new ArrayList<>();
        roles.add(new Role(1, "ADMIN"));
        roles.add(new Role(2, "SUPERVISOR"));
        roles.add(new Role(3, "USER"));
        return roles;
    }

    private class Fixture
    {
        void givenRoleRepositoryReturnsOptionalWithRole()
        {
            when(roleRepository.findBynameIgnoreCase("ADMIN")).thenReturn(Optional.of(new Role(1, "ADMIN")));
        }

        void givenUserRepositoryReturnsUserRecords()
        {
            when(userRepository.findAll()).thenReturn(USERS);
        }

        void givenRoleRepositoryReturnsAllRoles()
        {
            when(roleRepository.findAll()).thenReturn(ROLES);
        }

        void givenUserRepositoryReturnsOptionalWithUser()
        {
            when(userRepository.findById(1)).thenReturn(Optional.of(USER));
        }

        void givenUserRepositoryReturnsEmptyOptional()
        {
            when(userRepository.findById(1)).thenReturn(Optional.empty());
        }

        void givenRoleRepositoryReturnsRoleFromName()
        {
            when(roleRepository.findBynameIgnoreCase("SUPERVISOR")).thenReturn(Optional.of(new Role(2, "SUPERVISOR")));
        }

        void givenUserRepositoryReturnsOptionalWithUserByName()
        {
            when(userRepository.findByUsernameIgnoreCase("username")).thenReturn(Optional.of(USER));
        }

        void givenUserRepositoryReturnsEmptyOptionalByName()
        {
            when(userRepository.findByUsernameIgnoreCase("aUsername")).thenReturn(Optional.empty());
        }

        void whenCreateUserIsCalled()
        {
            UserRequest userRequest = new UserRequest("username", "email@email.com", "password", "ADMIN");
            createUserResult = target.createUser(userRequest);
        }

        void whenGetUsersIsCalled()
        {
            getUsersResult = target.getUsers();
        }

        void whenGetRolesIsCalled()
        {
            getRolesResult = target.getRoles();
        }

        void whenResetPasswordForUserIsCalled()
        {
            resetPasswordForUserResult = target.resetPasswordForUser(1);
        }

        void whenUpdateUserDetailsIsCalled()
        {
            UserRequest userRequest = new UserRequest("newUsername", "new@email.com", "newPassword", "SUPERVISOR");
            updateUserDetailsResult = target.updateUserDetails(1, userRequest);
        }

        void whenUpdatedUserPasswordIsCalled(String username, String newPassword)
        {
            updateUserPasswordResult = target.updateUserPassword(username, newPassword);
        }

        void thenAssertUserIsSaved()
        {
            assertEquals(createUserResult.getUsername(), "username");
            assertTrue(passwordEncoder.matches("password", createUserResult.getPassword()));
            assertEquals(createUserResult.getEmail(), "email@email.com");
            assertEquals(createUserResult.getRoles(), Set.of(ROLE));
        }

        void thenAssertUsersAreReturned()
        {
            assertEquals(getUsersResult, USERS);
        }

        void thenAssertRolesAreReturned()
        {
            assertEquals(getRolesResult, ROLES);
        }

        void thenAssertUsersPasswordIsReset()
        {
            PasswordEncoder encoder = new BCryptPasswordEncoder();
            assertTrue(encoder.matches("password", resetPasswordForUserResult.getPassword()));
        }

        void thenAssertEmptyUserReturned(User userResult)
        {
            assertNull(userResult.getUsername());
            assertNull(userResult.getPassword());
            assertNull(userResult.getEmail());
        }

        void thenAssertUsersDetailsAreUpdated()
        {
            assertEquals(updateUserDetailsResult.getUsername(), "newUsername");
            assertEquals(updateUserDetailsResult.getEmail(), "new@email.com");
            assertTrue(updateUserDetailsResult.getRoles().contains(new Role(2, "SUPERVISOR")));
        }

        void thenAssertUserPasswordIsChanged()
        {
            assertTrue(passwordEncoder.matches("newPassword", updateUserPasswordResult.getPassword()));
        }
    }
}
