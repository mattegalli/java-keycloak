package com.example.notes.controller;

import com.example.notes.model.Note;
import com.example.notes.service.NoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * REST API for the Personal Notes app.
 *
 * Endpoints:
 *   GET    /api/notes         — list notes for the calling user
 *   GET    /api/notes?admin=true — list ALL notes (admin only)
 *   POST   /api/notes         — create a note
 *   DELETE /api/notes/{id}    — delete a note
 *
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
    // Returns the calling user's notes.
    // If ?admin=true is passed, returns ALL notes from all users.
    //
    @GetMapping
    public ResponseEntity<Collection<Note>> getNotes(
            @RequestHeader(value = "X-User", defaultValue = "anonymous") String username,
            @RequestParam(value = "admin", defaultValue = "false") boolean admin) {

        if (admin) {
            return ResponseEntity.ok(noteService.getAllNotes());
        }
        List<Note> notes = noteService.getNotesForUser(username);
        return ResponseEntity.ok(notes);
    }

    // -------------------------------------------------------------------------
    // POST /api/notes
    //
    // Request body: { "content": "..." }
    // Creates a note owned by the calling user.
    // -------------------------------------------------------------------------
    @PostMapping
    public ResponseEntity<Note> createNote(
            @RequestHeader(value = "X-User", defaultValue = "anonymous") String username,
            @RequestBody Map<String, String> body) {

        String content = body.get("content");
        if (content == null || content.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        Note created = noteService.createNote(username, content);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // -------------------------------------------------------------------------
    // DELETE /api/notes/{id}
    //
    // Deletes the note with the given ID.
    //
    // -------------------------------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(
            @RequestHeader(value = "X-User", defaultValue = "anonymous") String username,
            @PathVariable String id) {

        boolean deleted = noteService.deleteNote(id);
        return deleted
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
