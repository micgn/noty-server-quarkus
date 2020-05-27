package de.mg.noty.controller.delta.req;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Builder
@Data
public class TagDeltaDto {

    @NotNull
    private Long updated;

    @NotBlank
    private String tagId;

    private String name;
}
