package ru.gorbunov.diaries.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ru.gorbunov.diaries.domain.Note;
import ru.gorbunov.diaries.domain.NoteElement;
import ru.gorbunov.diaries.exception.BadRequestException;
import ru.gorbunov.diaries.exception.ResourceNotFoundException;
import ru.gorbunov.diaries.service.NoteElementService;
import ru.gorbunov.diaries.service.NoteService;

/**
 * Controller for note elements page.
 *
 * @author Gorbunov.ia
 */
@Controller
@RequestMapping(path = "/notes-elements")
public class NoteElementController {

    /**
     * Logger for class.
     */
    private final Logger log = LoggerFactory.getLogger(NoteController.class);

    /**
     * Service for interaction with notes.
     */
    private final NoteService noteService;

    /**
     * Service for interaction with note elements.
     */
    private final NoteElementService noteElementService;

    /**
     * Base constructor.
     *
     * @param noteService           service for interaction with notes.
     * @param noteElementService    service for interaction with note elements.
     */
    public NoteElementController(final NoteService noteService,
                                 final NoteElementService noteElementService) {
        this.noteService = noteService;
        this.noteElementService = noteElementService;
    }

    /**
     * Method to get all note elements for note.
     *
     * @param noteId    note id
     * @param model     Model Spring MVC
     * @return          template name
     * @throws          ResourceNotFoundException if note not found in db
     */
    @GetMapping("/{noteId}")
    public String getAllNoteElements(@PathVariable final Integer noteId, ModelMap model) {
        log.debug("REST request to get NotesElements.");

        final Note note = noteService.getUserNoteById(noteId);

        if (note == null) {
            throw new ResourceNotFoundException();
        }

        List<NoteElement> notesElements = noteElementService.getUserNoteElementsByNoteWithSort(note.getId(),
                "sortBy", true);

        noteElementService.fillSortElement(notesElements);

        model.addAttribute("note", note);
        model.addAttribute("notesElements", notesElements);

        return "notes-elements";
    }

/*
    @PostMapping(value="/swapJson", headers="Content type")
    public String swap(@Valid @RequestBody SwapElementVM swapVM) {

        NoteElement element = noteElementService.changeSortBy(swapVM
                .getNoteElementId(),swapVM.getSortBy());

        if (element != null) {
            return "redirect:/notes-elements/" + element.getNote().getId();
        } else {
            throw new BadRequestException();
        }
    }
*/

    /**
     * Method set new sort by value to note element.
     *
     * @param noteElementId modifiable note element id
     * @param sortBy        new sort by value
     * @return              template name
     * @throws              BadRequestException if note element id or sort by is wrong
     */
    @PostMapping(value = "/swap")
    public String swap(@RequestParam("noteElementId") final Integer noteElementId,
                       @RequestParam("sortBy") final Integer sortBy) {
        log.debug("REST request to swap Note Element.");
        
        NoteElement element = noteElementService.changeSortBy(noteElementId, sortBy);

        if (element != null) {
            return "redirect:/notes-elements/" + element.getNote().getId();
        } else {
            throw new BadRequestException();
        }
    }

}
