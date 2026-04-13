package com.example.notes.controller;

import com.example.notes.model.Note;
import com.example.notes.service.NoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST API for the Personal Notes app.
 *
 * Endpoints:
 *   GET    /api/notes              — list the caller's notes
 *   GET    /api/notes/all          — list ALL notes (admin role required)
 *   POST   /api/notes              — create a note
 *   DELETE /api/notes/{id}         — delete own note (or any note if admin)
 */
@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    // -------------------------------------------------------------------------
    // GET /api/notes
    //
    // Returns only the notes belonging to the authenticated user.
    // @AuthenticationPrincipal Jwt injects the parsed, verified token object.
    // -------------------------------------------------------------------------
    @GetMapping
    public ResponseEntity<List<Note>> getMyNotes(@AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getClaimAsString("preferred_username");
        return ResponseEntity.ok(noteService.getNotesForUser(username));
    }

    // -------------------------------------------------------------------------
    // GET /api/notes/all
    //
    // Returns every note from all users.
    // Spring returns 403 Forbidden automatically if the role is absent.
    // -------------------------------------------------------------------------
    @GetMapping("/all")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Collection<Note>> getAllNotes() {
        return ResponseEntity.ok(noteService.getAllNotes());
    }

    // -------------------------------------------------------------------------
    // POST /api/notes
    //
    // Creates a note owned by the authenticated user.
    // -------------------------------------------------------------------------
    @PostMapping
    public ResponseEntity<Note> createNote(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody Map<String, String> body) {

        String content = body.get("content");
        if (content == null || content.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        String username = jwt.getClaimAsString("preferred_username");
        Note created = noteService.createNote(username, content);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // -------------------------------------------------------------------------
    // DELETE /api/notes/{id}
    //
    // Deletes the note with the given ID.
    //
    // hasRole('admin') checks for ROLE_admin in the SecurityContext authorities,
    // which were mapped from realm_access.roles in SecurityConfig.
    // -------------------------------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String id) {

        Optional<Note> note = noteService.findById(id);
        if (note.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        String username  = jwt.getClaimAsString("preferred_username");
        boolean isOwner  = note.get().getUsername().equals(username);
        boolean isAdmin  = jwt.getClaimAsMap("realm_access") != null
                && jwt.getClaimAsStringList("realm_access") == null
                && hasAdminRole(jwt);

        if (!isOwner && !isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        noteService.deleteNote(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Extracts the realm roles list from the JWT and checks for "admin".
     */
    @SuppressWarnings("unchecked")
    private boolean hasAdminRole(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess == null) return false;
        Object roles = realmAccess.get("roles");
        if (roles instanceof List<?> roleList) {
            return roleList.contains("admin");
        }
        return false;
    }
}
