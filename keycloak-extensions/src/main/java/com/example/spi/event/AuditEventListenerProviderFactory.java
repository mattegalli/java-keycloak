package com.example.spi.event;

import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

/**
 * SPI Factory: Event Listener
 *
 * Keycloak discovers this class via the service file - META-INF/services/org.keycloak.events.EventListenerProviderFactory
 */
public class AuditEventListenerProviderFactory implements EventListenerProviderFactory {

    public static final String PROVIDER_ID = "audit-event-listener";

    @Override
    public EventListenerProvider create(KeycloakSession session) {
        return new AuditEventListenerProvider();
    }

    @Override
    public void init(Config.Scope config) {
        // no config at this stage
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {}

    @Override
    public void close() {}

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
