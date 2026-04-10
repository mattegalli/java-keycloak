package com.example.notes.service;

import com.example.notes.model.Note;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory note store backed by a ConcurrentHashMap.
 *
 * Key   = note ID (UUID string)
 * Value = Note object
 *
 * ConcurrentHashMap makes individual put/remove operations thread-safe
 * without explicit synchronization — suitable for concurrent HTTP requests.
 */
@Service
public class NoteService {

    private final Map<String, Note> store = new ConcurrentHashMap<>();

    /**
     * Returns all notes owned by the given username
     */
    public List<Note> getNotesForUser(String username) {
        return store.values().stream()
                .filter(note -> note.getUsername().equals(username))
                .collect(Collectors.toList());
    }

    /**
     * Returns every note in the store, regardless of owner
     */
    public Collection<Note> getAllNotes() {
        return store.values();
    }

    /**
     * Creates and persists a new note in memory
     */
    public Note createNote(String username, String content) {
        Note note = new Note(username, content);
        store.put(note.getId(), note);
        return note;
    }

    /**
     * Deletes a note by ID
     *
     * Returns true if the note existed and was removed, false otherwise.
     */
    public boolean deleteNote(String id) {
        return store.remove(id) != null;
    }

    /**
     * Looks up a note by ID
     */
    public Optional<Note> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }
}
