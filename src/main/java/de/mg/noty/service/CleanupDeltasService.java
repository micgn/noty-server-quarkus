package de.mg.noty.service;

import de.mg.noty.model.action.NoteDelta;
import de.mg.noty.model.action.TagDelta;
import de.mg.noty.repository.NoteDeltaRepo;
import de.mg.noty.repository.NoteTagDeltaRepo;
import de.mg.noty.repository.TagDeltaRepo;
import io.quarkus.scheduler.Scheduled;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.time.ZoneId;
import java.util.List;

import static de.mg.noty.controller.delta.ActionEnum.DELETE;
import static java.lang.System.currentTimeMillis;
import static java.time.Instant.ofEpochMilli;
import static java.time.LocalDateTime.ofInstant;
import static java.time.ZoneId.of;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;

@ApplicationScoped
@Slf4j
public class CleanupDeltasService {

    private static final ZoneId ZONE = of("Europe/Berlin");

    @ConfigProperty(name = "noty.cleanupDelayHours", defaultValue = "720")
    Long cleanupDelayHours;

    @Inject
    NoteDeltaRepo noteDeltaRepo;
    @Inject
    TagDeltaRepo tagDeltaRepo;
    @Inject
    NoteTagDeltaRepo noteTagDeltaRepo;


    @Scheduled(every = "24h")  // every day
    @Transactional
    public void run() {

        Long before = currentTimeMillis() - cleanupDelayHours * 60 * 60 * 1000;
        log.info("cleanup before = " + ofInstant(ofEpochMilli(before), ZONE).format(ISO_LOCAL_DATE_TIME));
        long countBefore = noteDeltaRepo.count() + tagDeltaRepo.count() + noteTagDeltaRepo.count();
        log.info("total delta count = {}", countBefore);

        cleanupDeletedNotes(before);
        cleanupDeletedTags(before);

        long countAfter = noteDeltaRepo.count() + tagDeltaRepo.count() + noteTagDeltaRepo.count();
        log.info("deleted {}", countBefore - countAfter);
    }

    private void cleanupDeletedNotes(Long before) {

        List<NoteDelta> noteDeltas = noteDeltaRepo.findAllBefore(before);

        noteDeltas.stream().filter(delta -> delta.getAction() == DELETE)
                .forEach(deletionDelta -> {
                    noteDeltas.stream()
                            .filter(delta -> delta.getNoteId().equals(deletionDelta.getNoteId()))
                            .forEach(toDelete ->
                                    noteDeltaRepo.delete(toDelete));
                    noteTagDeltaRepo.deleteAll(noteTagDeltaRepo.findByNoteId(deletionDelta.getNoteId()));
                });
    }

    private void cleanupDeletedTags(Long before) {

        List<TagDelta> tagDeltas = tagDeltaRepo.findAllBefore(before);

        tagDeltas.stream().filter(delta -> delta.getAction() == DELETE)
                .forEach(deletionDelta -> {
                    tagDeltas.stream()
                            .filter(delta -> delta.getTagId().equals(deletionDelta.getTagId()))
                            .forEach(toDelete ->
                                    tagDeltaRepo.delete(toDelete));
                    noteTagDeltaRepo.deleteAll(noteTagDeltaRepo.findByTagId(deletionDelta.getTagId()));
                });
    }


}
