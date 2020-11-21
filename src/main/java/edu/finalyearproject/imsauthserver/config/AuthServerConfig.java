package edu.finalyearproject.imsauthserver.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;

/**
 * Configuration class for creating a list of allowed clients to access authorization server, including which grant type
 * to use for each one. This class also sets the authenticationManager for the exposed endpoints.
 */
@Configuration
@EnableAuthorizationServer  //exposes some endpoints automatically
public class AuthServerConfig extends AuthorizationServerConfigurerAdapter
{
    @Autowired
    private AuthenticationManager authenticationManager;

    @Value("${client.authCode.id}")
    private String authCodeClientID;

    @Value("${client.authCode.secret}")
    private String authCodeClientSecret;

    private static final String AUTHORIZATION_CODE = "authorization_code";
    private static final String READ = "read";
    private static final String WRITE = "write";

    /**
     * Overridden method used to specify clients that are allowed to access the resource server.
     * @param clients - object specifying all client information for authorised clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception
    {
        // allowed clients configured in application.properties
        clients.inMemory()
                .withClient("client1")
                .secret("secret1")
                .authorizedGrantTypes("password")
                .scopes(READ)
                .and()
                .withClient("client2")
                .secret(authCodeClientSecret)
                .authorizedGrantTypes(AUTHORIZATION_CODE)
                .scopes(READ)
                .autoApprove(true)
                .redirectUris("http://localhost:3000/oauth_callback"); //specifies where the authorization server will redirect to after authorization
    }

    /**
     * Overriden method specifying which AuthenticationManager to use.
     * @param endpoints - object containing all endpoints for the server
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception
    {
        endpoints.authenticationManager(authenticationManager);
    }

    /**
     * Overridden method used to allow the endpoint 'oauth/check_token' to validate access tokens issued by the
     * server.
     * @param oauthServer
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception
    {
        oauthServer.checkTokenAccess("isAuthenticated()");
    }
}
