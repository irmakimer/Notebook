package com.example.notebook.Controller;

import com.example.notebook.Model.Note;
import com.example.notebook.Model.User;
import com.example.notebook.Repository.UserRepository;
import com.example.notebook.Service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/notes")
public class NoteController {

    private final NoteService noteService;
    private final UserRepository userRepository;

    @Autowired
    public NoteController(NoteService noteService, UserRepository userRepository) {
        this.noteService = noteService;
        this.userRepository = userRepository;
    }

    // Notları listele
    @GetMapping
    public String listNotes(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Optional<User> user = userRepository.findByUsername(userDetails.getUsername());
        if (user.isPresent()) {
            model.addAttribute("notes", noteService.getNotesByUser(user.get()));
        }
        return "note/list";
    }

    // Not oluşturma formu
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("note", new Note());
        return "note/create";
    }

    // Not oluştur
    @PostMapping("/create")
    public String createNote(@ModelAttribute("note") Note note,
                             @AuthenticationPrincipal UserDetails userDetails) {
        Optional<User> user = userRepository.findByUsername(userDetails.getUsername());
        user.ifPresent(note::setUser);
        noteService.createNote(note);
        return "redirect:/notes";
    }

    // Not düzenleme formu
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model,
                               @AuthenticationPrincipal UserDetails userDetails) {
        Optional<Note> noteOpt = noteService.getNoteById(id);
        if (noteOpt.isPresent() && noteOpt.get().getUser().getUsername().equals(userDetails.getUsername())) {
            model.addAttribute("note", noteOpt.get());
            return "note/edit";
        }
        return "redirect:/notes";
    }

    // Notu güncelle
    @PostMapping("/edit/{id}")
    public String updateNote(@PathVariable Long id,
                             @ModelAttribute("note") Note note,
                             @AuthenticationPrincipal UserDetails userDetails) {
        Optional<Note> original = noteService.getNoteById(id);
        if (original.isPresent() && original.get().getUser().getUsername().equals(userDetails.getUsername())) {
            note.setUser(original.get().getUser());
            noteService.updateNote(id, note);
        }
        return "redirect:/notes";
    }

    // Notu sil
    @GetMapping("/delete/{id}")
    public String deleteNote(@PathVariable Long id,
                             @AuthenticationPrincipal UserDetails userDetails) {
        Optional<Note> note = noteService.getNoteById(id);
        if (note.isPresent() && note.get().getUser().getUsername().equals(userDetails.getUsername())) {
            noteService.deleteNote(id);
        }
        return "redirect:/notes";
    }
}
