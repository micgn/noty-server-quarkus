package de.mg.noty.service;

import de.mg.noty.controller.delta.ActionEnum;
import de.mg.noty.controller.delta.req.AllContentDto;
import de.mg.noty.controller.delta.req.NoteDeltaDto;
import de.mg.noty.controller.delta.req.NoteTagDeltaDto;
import de.mg.noty.controller.delta.req.TagDeltaDto;
import de.mg.noty.controller.delta.res.DeltaActionDto;
import de.mg.noty.controller.delta.res.ResponseDto;
import de.mg.noty.controller.mapper.DeltaMapper;
import de.mg.noty.model.action.NoteDelta;
import de.mg.noty.model.action.NoteTagDelta;
import de.mg.noty.model.action.TagDelta;
import de.mg.noty.model.content.Note;
import de.mg.noty.model.content.Tag;
import de.mg.noty.repository.NoteDeltaRepo;
import de.mg.noty.repository.NoteRepo;
import de.mg.noty.repository.NoteTagDeltaRepo;
import de.mg.noty.repository.TagDeltaRepo;
import de.mg.noty.repository.TagRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Slf4j
public class DeltaService {

    @Inject
    NoteRepo noteRepo;
    @Inject
    TagRepo tagRepo;
    @Inject
    NoteDeltaRepo noteDeltaRepo;
    @Inject
    TagDeltaRepo tagDeltaRepo;
    @Inject
    NoteTagDeltaRepo noteTagDeltaRepo;

    @Inject
    DeltaMapper deltaMapper;

    @Transactional
    public ResponseDto create(NoteDeltaDto noteDto, String lastReceivedServerDelta) {

        log.debug("create note");
        if (isEmpty(noteDto.getText()) ||
                noteRepo.findById(noteDto.getNoteId()).isPresent() ||
                noteRepo.findByText(noteDto.getText()).isPresent())
            return getDeltas(false, lastReceivedServerDelta);

        ResponseDto deltas = getDeltas(true, lastReceivedServerDelta);
        NoteDelta deltaToSave = deltaMapper.map2Delta(noteDto);
        deltaToSave.setAction(ActionEnum.CREATE);
        noteDeltaRepo.save(deltaToSave);

        noteRepo.save(deltaMapper.map2Entity(noteDto));
        return deltas;
    }

    @Transactional
    public ResponseDto update(NoteDeltaDto noteDto, String lastReceivedServerDelta) {
        log.debug("update note");
        if (isEmpty(noteDto.getText()))
            return getDeltas(false, lastReceivedServerDelta);
        Optional<Note> entity = noteRepo.findById(noteDto.getNoteId());
        Instant updated = Instant.ofEpochMilli(noteDto.getUpdated());
        if (!entity.isPresent() || entity.get().getUpdated().isAfter(updated))
            return getDeltas(false, lastReceivedServerDelta);

        ResponseDto deltas = getDeltas(true, lastReceivedServerDelta);

        NoteDelta deltaToSave = deltaMapper.map2Delta(noteDto);
        deltaToSave.setAction(ActionEnum.UPDATE);
        noteDeltaRepo.save(deltaToSave);

        noteRepo.save(deltaMapper.map2Entity(noteDto));
        return deltas;
    }


    @Transactional
    public ResponseDto delete(NoteDeltaDto noteDto, String lastReceivedServerDelta) {
        log.debug("delete note");
        Optional<Note> entity = noteRepo.findById(noteDto.getNoteId());
        Instant updated = Instant.ofEpochMilli(noteDto.getUpdated());
        if (!entity.isPresent() || entity.get().getUpdated().isAfter(updated))
            return getDeltas(false, lastReceivedServerDelta);

        ResponseDto deltas = getDeltas(true, lastReceivedServerDelta);

        NoteDelta deltaToSave = deltaMapper.map2Delta(noteDto);
        deltaToSave.setAction(ActionEnum.DELETE);
        noteDeltaRepo.save(deltaToSave);

        noteRepo.deleteById(noteDto.getNoteId());
        return deltas;
    }


    @Transactional
    public ResponseDto create(TagDeltaDto tagDto, String lastReceivedServerDelta) {
        log.debug("create tag");
        if (isEmpty(tagDto.getName()) ||
                tagRepo.findById(tagDto.getTagId()).isPresent() ||
                tagRepo.findByName(tagDto.getName()).isPresent())
            return getDeltas(false, lastReceivedServerDelta);

        ResponseDto deltas = getDeltas(true, lastReceivedServerDelta);

        TagDelta deltaToSave = deltaMapper.map2Delta(tagDto);
        deltaToSave.setAction(ActionEnum.CREATE);
        tagDeltaRepo.save(deltaToSave);

        tagRepo.save(deltaMapper.map2Entity(tagDto));
        return deltas;
    }


    @Transactional
    public ResponseDto update(TagDeltaDto tagDto, String lastReceivedServerDelta) {
        log.debug("update tag");
        Optional<Tag> entity = tagRepo.findById(tagDto.getTagId());
        Instant updated = Instant.ofEpochMilli(tagDto.getUpdated());
        if (isEmpty(tagDto.getName()) || !entity.isPresent() || entity.get().getUpdated().isAfter(updated))
            return getDeltas(false, lastReceivedServerDelta);

        ResponseDto deltas = getDeltas(true, lastReceivedServerDelta);

        TagDelta deltaToSave = deltaMapper.map2Delta(tagDto);
        deltaToSave.setAction(ActionEnum.UPDATE);
        tagDeltaRepo.save(deltaToSave);

        tagRepo.save(deltaMapper.map2Entity(tagDto));
        return deltas;
    }


    @Transactional
    public ResponseDto delete(TagDeltaDto tagDto, String lastReceivedServerDelta) {
        log.debug("delete tag");
        Optional<Tag> entity = tagRepo.findById(tagDto.getTagId());
        Instant updated = Instant.ofEpochMilli(tagDto.getUpdated());
        if (!entity.isPresent() || entity.get().getUpdated().isAfter(updated))
            isEmpty(tagDto.getName());

        ResponseDto deltas = getDeltas(true, lastReceivedServerDelta);

        TagDelta deltaToSave = deltaMapper.map2Delta(tagDto);
        deltaToSave.setAction(ActionEnum.DELETE);
        tagDeltaRepo.save(deltaToSave);

        tagRepo.deleteById(tagDto.getTagId());
        return deltas;
    }


    @Transactional
    public ResponseDto post(NoteTagDeltaDto noteTag, String lastReceivedServerDelta) {

        log.debug("create noteTag");
        return postOrDelete(noteTag, ActionEnum.CREATE, lastReceivedServerDelta);
    }


    @Transactional
    public ResponseDto delete(NoteTagDeltaDto noteTag, String lastReceivedServerDelta) {

        log.debug("delete noteTag");
        return postOrDelete(noteTag, ActionEnum.DELETE, lastReceivedServerDelta);
    }

    private ResponseDto postOrDelete(NoteTagDeltaDto noteTag, ActionEnum action, String lastReceivedServerDelta) {

        List<NoteTagDelta> existingDeltas = noteTagDeltaRepo.findStartingFromLatest(noteTag.getNoteId(), noteTag.getTagId(), PageRequest.of(0, 1));
        if (!existingDeltas.isEmpty()) {
            NoteTagDelta last = existingDeltas.get(0);
            if (last.getUpdated() > noteTag.getUpdated() || last.getAction() == action)
                return getDeltas(false, lastReceivedServerDelta);
        }

        ResponseDto deltas = getDeltas(true, lastReceivedServerDelta);

        NoteTagDelta deltaToSave = deltaMapper.map2Delta(noteTag);
        deltaToSave.setAction(action);
        noteTagDeltaRepo.save(deltaToSave);

        Note noteToSave = noteRepo.findById(noteTag.getNoteId()).orElse(null);
        Tag savedTag = tagRepo.findById(noteTag.getTagId()).orElse(null);
        if (noteToSave == null || savedTag == null)
            return getDeltas(false, lastReceivedServerDelta);

        if (noteToSave.getTags() == null)
            noteToSave.setTags(new HashSet<>());
        if (action == ActionEnum.CREATE)
            noteToSave.getTags().add(savedTag);
        else if (action == ActionEnum.DELETE)
            noteToSave.getTags().removeIf(t -> t.getId().equals(savedTag.getId()));
        noteRepo.save(noteToSave);
        return deltas;
    }

    private ResponseDto getDeltas(boolean success, String lastReceivedServerDelta) {
        ResponseDto result = getDeltas(lastReceivedServerDelta);
        result.setSaved(success);
        return result;
    }


    public ResponseDto getDeltas(String lastReceivedServerDelta) {

        log.debug("get deltas");
        Long since = Long.valueOf(lastReceivedServerDelta);

        List<NoteDelta> notes = noteDeltaRepo.findAllSince(since);
        List<TagDelta> tags = tagDeltaRepo.findAllSince(since);
        List<NoteTagDelta> noteTags = noteTagDeltaRepo.findAllSince(since);

        List<DeltaActionDto> newDeltas = new ArrayList<>();
        notes.forEach(n ->
                newDeltas.add(DeltaActionDto.builder()
                        .note(deltaMapper.map(n))
                        .action(n.getAction())
                        .build()));
        tags.forEach(t ->
                newDeltas.add(DeltaActionDto.builder()
                        .tag(deltaMapper.map(t))
                        .action(t.getAction())
                        .build()));
        noteTags.forEach(nt ->
                newDeltas.add(DeltaActionDto.builder()
                        .noteTag(deltaMapper.map(nt))
                        .action(nt.getAction())
                        .build()));

        newDeltas.sort((a1, a2) -> a2.getUpdated().compareTo(a1.getUpdated()));

        return ResponseDto.builder().newDeltas(newDeltas).build();
    }

    private boolean isEmpty(String text) {
        return text == null || text.trim().length() == 0;
    }

    @Transactional
    public void overwrite(AllContentDto allContent, String lastReceivedServerDelta) {
        noteTagDeltaRepo.deleteAll();
        noteDeltaRepo.deleteAll();
        tagDeltaRepo.deleteAll();
        noteRepo.deleteAll();
        tagRepo.deleteAll();

        allContent.getTagCreateDeltas().forEach(t -> create(t, lastReceivedServerDelta));
        allContent.getNoteCreateDeltas().forEach(n -> create(n, lastReceivedServerDelta));
        allContent.getNoteTagCreateDeltas().forEach(nt -> post(nt, lastReceivedServerDelta));

        log.info("overwrite: saved deltas\n" + allContent);
    }
}
