/**
 * Copyright (C) Alec R. C. Smith - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Alec Smith <alec.smith@uea.ac.uk>, 2020-2021
 */
package edu.finalyearproject.imsauthserver.requests;

import lombok.Data;

/**
 * Wrapper class containing form data to update a Users account details.
 */
@Data
public class UserRequest
{
    private String username;
    private String email;
    private String password;
    private String role;
}
