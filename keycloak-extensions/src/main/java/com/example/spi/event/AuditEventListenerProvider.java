package com.example.spi.event;

import java.util.logging.Logger;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;

/**
 * SPI: Event Listener - logger
 * 
 * onEvent(Event) -> Fires for end-user actions: LOGIN, LOGIN_ERROR, LOGOUT, REGISTER, UPDATE_PASSWORD, TOKEN_EXCHANGE, etc.
 * onEvent(AdminEvent, includeRepresentation) -> Fires for admin console / Admin REST API actions: creating users, assigning roles, etc.
 */
public class AuditEventListenerProvider implements EventListenerProvider {

    private static final Logger log = Logger.getLogger(AuditEventListenerProvider.class.getName());

    @Override
    public void onEvent(Event event) {
        // Highlight failed logins in the log — the most security-relevant events
        if (event.getType() == EventType.LOGIN_ERROR) {
            log.warning(String.format("[AUDIT] FAILED LOGIN — realm=%s user=%s client=%s ip=%s error=%s",
                    event.getRealmId(),
                    event.getUserId(),
                    event.getClientId(),
                    event.getIpAddress(),
                    event.getError()));
        } else {
            log.info(String.format("[AUDIT] %s — realm=%s user=%s client=%s ip=%s",
                    event.getType(),
                    event.getRealmId(),
                    event.getUserId(),
                    event.getClientId(),
                    event.getIpAddress()));
        }
    }

    @Override
    public void onEvent(AdminEvent adminEvent, boolean includeRepresentation) {
        log.info(String.format("[AUDIT][ADMIN] %s %s — realm=%s resource=%s%s",
                adminEvent.getOperationType(),
                adminEvent.getResourceType(),
                adminEvent.getRealmId(),
                adminEvent.getResourcePath(),
                includeRepresentation && adminEvent.getRepresentation() != null
                        ? " body=" + adminEvent.getRepresentation()
                        : ""));
    }

    @Override
    public void close() {}
}
