package de.mg.noty.controller.delta.req;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Builder
@Data
public class NoteDeltaDto {

    @NotNull
    private Long updated;

    @NotBlank
    private String noteId;

    private String text;

    @Pattern(regexp = "\\d\\d\\d\\d-\\d\\d-\\d\\d")
    private String dueDate;
}
