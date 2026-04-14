package com.example.spi.authenticator;

import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.List;

/**
 * SPI Factory: Custom Authenticator
 *
 * Keycloak discovers this class via the service file - META-INF/services/org.keycloak.authentication.AuthenticatorFactory
 * 
 * SETUP:
 * - deploy the JAR
 * - Open Admin Console -> Authentication -> Select any flow -> Add step -> search for "Account Approval Check"
 */
public class AccountApprovalAuthenticatorFactory implements AuthenticatorFactory {

    public static final String PROVIDER_ID = "account-approval-authenticator";

    // A singleton is better because AccountApprovalAuthenticator holds no mutable state
    private static final AccountApprovalAuthenticator INSTANCE = new AccountApprovalAuthenticator();

    @Override
    public Authenticator create(KeycloakSession session) {
        return INSTANCE;
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getDisplayType() {
        return "Account Approval Check";
    }

    @Override
    public String getHelpText() {
        return "Denies login if the user does not have the 'account.approved = true' attribute. " + 
                "Set this attribute on a user in Users → Attributes.";
    }

    @Override
    public String getReferenceCategory() {
        return "account-approval";
    }

    @Override
    public boolean isConfigurable() {
        // false keeps it simple — no runtime config needed.
        return false;
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return List.of();
    }

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        // REQUIRED: must pass; DISABLED: step is skipped.
        return new AuthenticationExecutionModel.Requirement[] {
            AuthenticationExecutionModel.Requirement.REQUIRED,
            AuthenticationExecutionModel.Requirement.DISABLED
        };
    }

    @Override
    public boolean isUserSetupAllowed() {
        // true would allow this authenticator to be added as a "required action"
        // that users must complete on first login. Not needed here.
        return false;
    }

    @Override
    public void init(Config.Scope config) {}

    @Override
    public void postInit(KeycloakSessionFactory factory) {}

    @Override
    public void close() {}
}
