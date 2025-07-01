package com.example.notebook.Repository;

import com.example.notebook.Model.Note;
import com.example.notebook.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findByUser(User user); // Kullanıcıya ait tüm notları getirir
}
