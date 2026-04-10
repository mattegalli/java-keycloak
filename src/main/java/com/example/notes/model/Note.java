package com.example.notes.model;

import java.time.Instant;
import java.util.UUID;

/**
 * A single note belonging to one user.
 *
 * The 'username' field identifies the owner.
 *
 */
public class Note {

    private final String id;
    private final String username;
    private final String content;
    private final Instant createdAt;

    public Note(String username, String content) {
        this.id        = UUID.randomUUID().toString();
        this.username  = username;
        this.content   = content;
        this.createdAt = Instant.now();
    }

    // Getters only — notes are immutable after creation

    public String getId()         { return id; }
    public String getUsername()   { return username; }
    public String getContent()    { return content; }
    public Instant getCreatedAt() { return createdAt; }
}
