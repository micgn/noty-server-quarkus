package de.mg.noty.controller.delta.res;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.mg.noty.controller.delta.ActionEnum;
import de.mg.noty.controller.delta.req.NoteDeltaDto;
import de.mg.noty.controller.delta.req.NoteTagDeltaDto;
import de.mg.noty.controller.delta.req.TagDeltaDto;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class DeltaActionDto {

    private ActionEnum action;

    // one of the following will be set:
    private NoteDeltaDto note;
    private TagDeltaDto tag;
    private NoteTagDeltaDto noteTag;

    @JsonIgnore
    public Long getUpdated() {
        if (note != null) return note.getUpdated();
        else if (tag != null) return tag.getUpdated();
        else if (noteTag != null) return noteTag.getUpdated();
        else throw new RuntimeException();
    }
}
