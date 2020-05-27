package de.mg.noty.service;

import de.mg.noty.controller.delta.ActionEnum;
import de.mg.noty.model.action.NoteDelta;
import de.mg.noty.model.action.NoteTagDelta;
import de.mg.noty.model.action.TagDelta;
import de.mg.noty.repository.NoteDeltaRepo;
import de.mg.noty.repository.NoteTagDeltaRepo;
import de.mg.noty.repository.TagDeltaRepo;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.time.LocalDateTime;

import static de.mg.noty.controller.delta.ActionEnum.CREATE;
import static de.mg.noty.controller.delta.ActionEnum.DELETE;
import static de.mg.noty.controller.delta.ActionEnum.UPDATE;
import static java.time.LocalDateTime.now;
import static java.time.ZoneOffset.UTC;


@QuarkusTest
public class CleanupDeltasServiceTest {

    @Inject
    CleanupDeltasService testee;

    @Inject
    NoteDeltaRepo noteDeltaRepo;
    @Inject
    TagDeltaRepo tagDeltaRepo;
    @Inject
    NoteTagDeltaRepo noteTagDeltaRepo;

    @Test
    public void testRun() {

        LocalDateTime old = now().minusDays(35);

        noteDeltaRepo.save(note("n1", old, CREATE));
        tagDeltaRepo.save(tag("t1", now(), CREATE));
        noteTagDeltaRepo.save(noteTag("n1", "t1", now().plusMinutes(1), CREATE));
        noteTagDeltaRepo.save(noteTag("n1", "t1", now().plusMinutes(2), DELETE));
        tagDeltaRepo.save(tag("t1", now().plusMinutes(3), DELETE));
        noteDeltaRepo.save(note("n1", now().plusMinutes(4), DELETE));


        tagDeltaRepo.save(tag("t2", old, CREATE));
        noteDeltaRepo.save(note("n2", old.plusMinutes(1), CREATE));
        noteDeltaRepo.save(note("n2", old.plusMinutes(2), UPDATE));
        noteTagDeltaRepo.save(noteTag("n2", "t2", old.plusMinutes(3), CREATE));
        noteTagDeltaRepo.save(noteTag("n2", "t2", old.plusMinutes(4), DELETE));
        noteDeltaRepo.save(note("n2", old.plusMinutes(5), DELETE));
        tagDeltaRepo.save(tag("t2", old.plusMinutes(6), DELETE));

        testee.run();
/*
        assertThat(noteDeltaRepo.findByNoteId("n1")).hasSize(2);
        assertThat(noteDeltaRepo.findByNoteId("n2")).isEmpty();

        assertThat(tagDeltaRepo.findByTagId("t1")).hasSize(2);
        assertThat(tagDeltaRepo.findByTagId("t2")).isEmpty();

        assertThat(noteTagDeltaRepo.findByNoteIdAndTagId("n1", "t1")).hasSize(2);
        assertThat(noteTagDeltaRepo.findByNoteIdAndTagId("n2", "t2")).isEmpty();*/
    }


    private NoteDelta note(String id, LocalDateTime updated, ActionEnum action) {
        NoteDelta n = new NoteDelta();
        n.setNoteId(id);
        n.setAction(action);
        n.setUpdated(updated.toInstant(UTC).toEpochMilli());
        return n;
    }

    private TagDelta tag(String id, LocalDateTime updated, ActionEnum action) {
        TagDelta t = new TagDelta();
        t.setTagId(id);
        t.setAction(action);
        t.setUpdated(updated.toInstant(UTC).toEpochMilli());
        return t;
    }

    private NoteTagDelta noteTag(String noteId, String tagId, LocalDateTime updated, ActionEnum action) {
        NoteTagDelta nt = new NoteTagDelta();
        nt.setNoteId(noteId);
        nt.setTagId(tagId);
        nt.setAction(action);
        nt.setUpdated(updated.toInstant(UTC).toEpochMilli());
        return nt;
    }

}
