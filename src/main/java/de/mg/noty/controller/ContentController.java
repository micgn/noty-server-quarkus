package de.mg.noty.controller;

import de.mg.noty.controller.content.NoteDto;
import de.mg.noty.controller.content.TagDto;
import de.mg.noty.controller.mapper.ContentMapper;
import de.mg.noty.repository.NoteRepo;
import de.mg.noty.repository.TagRepo;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/content/")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("notyRole")
public class ContentController {

    @Inject
    NoteRepo noteRepo;
    @Inject
    TagRepo tagRepo;
    @Inject
    ContentMapper mapper;

    @GET
    @Path("notes")
    public List<NoteDto> findAllNotes() {
        return mapper.mapNotes(noteRepo.findAll());
    }

    @GET
    @Path("tags")
    public List<TagDto> findAllTags() {
        return mapper.mapTags(tagRepo.findAll());
    }
}
