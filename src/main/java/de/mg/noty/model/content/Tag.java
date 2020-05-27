package de.mg.noty.model.content;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Tag extends AbstractContent {

    @Column(nullable = false)
    private String name;

}
