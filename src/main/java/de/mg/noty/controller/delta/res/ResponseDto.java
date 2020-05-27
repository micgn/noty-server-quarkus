package de.mg.noty.controller.delta.res;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Builder
@Data
public class ResponseDto {

    private Boolean saved;

    @Builder.Default
    private List<DeltaActionDto> newDeltas = new ArrayList<>();
}
