package de.mg.noty.controller.delta.req;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Builder
@Data
public class AllContentDto {

    @Builder.Default
    @NotNull
    private List<NoteDeltaDto> noteCreateDeltas = new ArrayList<>();

    @Builder.Default
    @NotNull
    private List<TagDeltaDto> tagCreateDeltas = new ArrayList<>();

    @Builder.Default
    @NotNull
    private List<NoteTagDeltaDto> noteTagCreateDeltas = new ArrayList<>();
}
