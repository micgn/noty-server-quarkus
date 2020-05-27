package de.mg.noty.model.content;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Note extends AbstractContent {

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable
    private Set<Tag> tags;

    @Column(nullable = false)
    private String text;

    @Column
    private LocalDate dueDate;
}
