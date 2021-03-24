package edu.finalyearproject.imsauthserver.requests;

import lombok.Data;

@Data
public class UserRequest
{
    private String username;
    private String email;
    private String password;
    private String role;
}
