package de.mg.noty.controller;

import de.mg.noty.controller.delta.req.AllContentDto;
import de.mg.noty.controller.delta.req.NoteDeltaDto;
import de.mg.noty.controller.delta.req.NoteTagDeltaDto;
import de.mg.noty.controller.delta.req.TagDeltaDto;
import de.mg.noty.controller.delta.res.ResponseDto;
import de.mg.noty.service.DeltaService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("delta/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("notyRole")
public class DeltaController {

    @Inject
    DeltaService service;

    @POST
    @Path("note")
    public ResponseDto create(@Valid NoteDeltaDto noteDto,
                              @NotEmpty @Pattern(regexp = "\\d+") @QueryParam("lastReceivedServerDelta") String lastReceivedServerDelta) {

        return service.create(noteDto, lastReceivedServerDelta);
    }


    @PUT
    @Path("note")
    public ResponseDto update(@Valid NoteDeltaDto noteDto,
                              @NotEmpty @Pattern(regexp = "\\d+") @QueryParam("lastReceivedServerDelta") String lastReceivedServerDelta) {

        return service.update(noteDto, lastReceivedServerDelta);
    }

    @DELETE
    @Path("note")
    public ResponseDto delete(@Valid NoteDeltaDto noteDto,
                              @NotEmpty @Pattern(regexp = "\\d+") @QueryParam("lastReceivedServerDelta") String lastReceivedServerDelta) {

        return service.delete(noteDto, lastReceivedServerDelta);
    }

    @POST
    @Path("tag")
    public ResponseDto create(@Valid TagDeltaDto tagDto,
                              @NotEmpty @Pattern(regexp = "\\d+") @QueryParam("lastReceivedServerDelta") String lastReceivedServerDelta) {

        return service.create(tagDto, lastReceivedServerDelta);
    }

    @PUT
    @Path("tag")
    public ResponseDto update(@Valid TagDeltaDto tagDto,
                              @NotEmpty @Pattern(regexp = "\\d+") @QueryParam("lastReceivedServerDelta") String lastReceivedServerDelta) {

        return service.update(tagDto, lastReceivedServerDelta);
    }

    @DELETE
    @Path("tag")
    public ResponseDto delete(@Valid TagDeltaDto tagDto,
                              @NotEmpty @Pattern(regexp = "\\d+") @QueryParam("lastReceivedServerDelta") String lastReceivedServerDelta) {

        return service.delete(tagDto, lastReceivedServerDelta);
    }

    @POST
    @Path("notetag")
    public ResponseDto post(@Valid NoteTagDeltaDto noteTag,
                            @NotEmpty @Pattern(regexp = "\\d+") @QueryParam("lastReceivedServerDelta") String lastReceivedServerDelta) {

        return service.post(noteTag, lastReceivedServerDelta);
    }

    @DELETE
    @Path("notetag")
    public ResponseDto delete(@Valid NoteTagDeltaDto noteTag,
                              @NotEmpty @Pattern(regexp = "\\d+") @QueryParam("lastReceivedServerDelta") String lastReceivedServerDelta) {

        return service.delete(noteTag, lastReceivedServerDelta);
    }

    @GET
    @Path("deltas")
    public ResponseDto getDeltas(@NotEmpty @Pattern(regexp = "\\d+") @QueryParam("lastReceivedServerDelta") String lastReceivedServerDelta) {

        return service.getDeltas(lastReceivedServerDelta);
    }

    @POST
    @Path("all")
    public void overwrite(@Valid AllContentDto allContent,
                          @NotEmpty @Pattern(regexp = "\\d+") @QueryParam("lastReceivedServerDelta") String lastReceivedServerDelta) {
        service.overwrite(allContent, lastReceivedServerDelta);
    }
}
