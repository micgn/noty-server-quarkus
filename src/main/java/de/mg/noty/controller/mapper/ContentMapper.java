package de.mg.noty.controller.mapper;

import de.mg.noty.controller.content.NoteDto;
import de.mg.noty.controller.content.TagDto;
import de.mg.noty.model.content.Note;
import de.mg.noty.model.content.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ContentMapper {

    List<NoteDto> mapNotes(List<Note> entity);

    NoteDto mapNote(Note entity);

    List<TagDto> mapTags(List<Tag> entity);

    TagDto mapTag(Tag entity);

}
