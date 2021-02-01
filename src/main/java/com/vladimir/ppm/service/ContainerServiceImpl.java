package com.vladimir.ppm.service;

import com.vladimir.ppm.domain.Access;
import com.vladimir.ppm.domain.Container;
import com.vladimir.ppm.domain.Group;
import com.vladimir.ppm.domain.Note;
import com.vladimir.ppm.domain.Password;
import com.vladimir.ppm.domain.Token;
import com.vladimir.ppm.dto.ContainerDto;
import com.vladimir.ppm.dto.MessageDto;
import com.vladimir.ppm.dto.NoteDto;
import com.vladimir.ppm.dto.PasswordDto;
import com.vladimir.ppm.repository.ContainerRepository;
import com.vladimir.ppm.repository.NoteRepository;
import com.vladimir.ppm.repository.PasswordRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
public class ContainerServiceImpl implements ContainerService {
    private final UserService userService;
    private final ContainerRepository containerRepository;
    private final CryptoProvider cryptoProvider;
    private final NoteRepository noteRepository;
    private final PasswordRepository passwordRepository;

    public ContainerServiceImpl(UserService userService, ContainerRepository containerRepository, CryptoProvider cryptoProvider,
                                NoteRepository noteRepository, PasswordRepository passwordRepository) {
        this.userService = userService;
        this.containerRepository = containerRepository;
        this.cryptoProvider = cryptoProvider;
        this.noteRepository = noteRepository;
        this.passwordRepository = passwordRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ContainerDto getTree(Token token) {
        Set<Group> groups = userService.getGroups(token);
        Container root = containerRepository.getContainerByName("root");
        return buildTree(root, groups);
    }

    @Override
    @Transactional
    public MessageDto moveContainer(Token token, long itemId, long moveToId) {
        Container container = containerRepository.getOne(itemId);
        Container cntMoveTo = containerRepository.getOne(moveToId);
        if (getAccess(container, userService.getGroups(token)) != Access.RW || getAccess(cntMoveTo, userService.getGroups(token)) != Access.RW
                || container.getName().equals("root") || container.equals(cntMoveTo) || cntMoveTo.getChildren().contains(container)
                || container.isDeleted() || cntMoveTo.isDeleted()) {
            return MessageDto.builder().message("cfe1").build();
        }
        container.getParent().getChildren().remove(container);
        container.setParent(cntMoveTo);
        cntMoveTo.addChild(container);
        return MessageDto.builder().build();
    }

    @Override
    @Transactional
    public MessageDto add(Token token, long parentId, String name) {
        Container parent = containerRepository.getOne(parentId);
        if (getAccess(parent, userService.getGroups(token)) == Access.RW && name.length() > 0 && !parent.isDeleted()) {
            Container container = new Container(name, parent);
            if (parent.getChildren().contains(container)) {
                return MessageDto.builder().message("ive1").build();
            }
            container.setGroupsRO(new HashSet<>(parent.getGroupsRO()));
            container.setGroupsRW(new HashSet<>(parent.getGroupsRW()));
            container = containerRepository.save(container);
            parent.addChild(container);
        }
        return MessageDto.builder().build();
    }

    @Override
    @Transactional
    public MessageDto delete(Token token, long itemId) {
        Container container = containerRepository.getOne(itemId);
        if (container.isDeleted() || container.getName().equals("root") || container.getChildren().size() != 0
                || getAccess(container, userService.getGroups(token)) != Access.RW) {
            return MessageDto.builder().message("ive2").build();
        }
        container.getParent().getChildren().remove(container);
        container.setDeleted(true);
        container.setName(container.getName() + "_Deleted_By_" + token.getLogin() + "_" + new Date());
        return MessageDto.builder().build();
    }

    @Override
    @Transactional
    public MessageDto rename(Token token, long itemId, String name) {
        Container container = containerRepository.getOne(itemId);
        if (container.isDeleted() || container.getName().equals("root") || name == null || name.length() == 0
                || getAccess(container, userService.getGroups(token)) != Access.RW) {
            return MessageDto.builder().message("ive3").build();
        }
        for (Container child : container.getParent().getChildren()) {
            if (child.getName().equals(name)) {
                return MessageDto.builder().message("ive1").build();
            }
        }
        container.setName(name);
        return MessageDto.builder().build();
    }

    @Override
    @Transactional
    public MessageDto addNote(Token token, long parentId, String name, String text) {
        Container container = containerRepository.getOne(parentId);
        if (cryptoProvider.isSystemClosed() || container.isDeleted() || name == null || name.length() == 0 || getAccess(container, userService.getGroups(token)) != Access.RW) {
            return MessageDto.builder().message("ive4").build();
        }
        byte[] encryptedText = cryptoProvider.encryptDbEntry(text);
        Note note = new Note(container, name, encryptedText, token.getLogin());
        noteRepository.save(note);
        return MessageDto.builder().build();
    }

    @Override
    @Transactional(readOnly = true)
    public MessageDto getNote(Token token, long noteId) {
        Note note = noteRepository.getOne(noteId);
        Container parent = note.getParent();
        if (cryptoProvider.isSystemClosed() || getAccess(parent, userService.getGroups(token)) != Access.RW && getAccess(parent, userService.getGroups(token)) != Access.RO) {
            return MessageDto.builder().build();
        }
        String text = cryptoProvider.decryptDbEntry(note.getEncryptedText());
        return MessageDto.builder().message(text).build();
    }

    @Override
    @Transactional
    public MessageDto editNote(Token token, long noteId, String name, String text) {
        Note note = noteRepository.getOne(noteId);
        Container parent = note.getParent();
        if (cryptoProvider.isSystemClosed() || getAccess(parent, userService.getGroups(token)) != Access.RW || name == null || name.length() == 0 ||
                parent.isDeleted() || note.isDeleted()) {
            return MessageDto.builder().message("ive5").build();
        }
        note.setName(name);
        note.setEncryptedText(cryptoProvider.encryptDbEntry(text));
        note.setEditedDate(new Date());
        note.setEditedBy(token.getLogin());
        return MessageDto.builder().build();
    }

    @Override
    @Transactional
    public MessageDto removeNote(Token token, long noteId) {
        Note note = noteRepository.getOne(noteId);
        Container parent = note.getParent();
        if (getAccess(parent, userService.getGroups(token)) != Access.RW) {
            return MessageDto.builder().message("ive6").build();
        }
        if (note.isDeleted()) {
            noteRepository.delete(note);
        } else {
            note.setDeleted(true);
            note.setDeletedDate(new Date());
            note.setDeletedBy(token.getLogin());
        }
        return MessageDto.builder().build();
    }

    @Override
    @Transactional
    public MessageDto addPasswd(Token token, long parentId, String name, String login, String passwd, String note) {
        Container parent = containerRepository.getOne(parentId);
        if (cryptoProvider.isSystemClosed() || parent.isDeleted() || name == null || name.length() == 0 || getAccess(parent, userService.getGroups(token)) != Access.RW) {
            return MessageDto.builder().message("ive4").build();
        }
        byte[] encryptedLogin = cryptoProvider.encryptDbEntry(login);
        byte[] encryptedPasswd = cryptoProvider.encryptDbEntry(passwd);
        byte[] encryptedNote = cryptoProvider.encryptDbEntry(note);
        Password password = new Password(name, parent, token.getLogin());
        password.setEncryptedLogin(encryptedLogin);
        password.setEncryptedPass(encryptedPasswd);
        password.setEncryptedNote(encryptedNote);
        passwordRepository.save(password);
        return MessageDto.builder().build();
    }

    @Override
    @Transactional(readOnly = true)
    public PasswordDto getPwdEnv(Token token, long pwdId) {
        Password password = passwordRepository.getOne(pwdId);
        Container parent = password.getParent();
        if (cryptoProvider.isSystemClosed() || getAccess(parent, userService.getGroups(token)) != Access.RW && getAccess(parent, userService.getGroups(token)) != Access.RO) {
            return PasswordDto.builder().build();
        }
        return PasswordDto.builder()
                .login(cryptoProvider.decryptDbEntry(password.getEncryptedLogin()))
                .note(cryptoProvider.decryptDbEntry(password.getEncryptedNote()))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PasswordDto getPwdBody(Token token, long pwdId) {
        Password password = passwordRepository.getOne(pwdId);
        Container parent = password.getParent();
        if (cryptoProvider.isSystemClosed() || getAccess(parent, userService.getGroups(token)) != Access.RW && getAccess(parent, userService.getGroups(token)) != Access.RO) {
            return PasswordDto.builder().build();
        }
        return PasswordDto.builder()
                .password(cryptoProvider.decryptDbEntry(password.getEncryptedPass()))
                .build();
    }

    @Override
    @Transactional
    public MessageDto editPassword(Token token, long pwdId, String name, String login, String pass, String note) {
        Password password = passwordRepository.getOne(pwdId);
        Container parent = password.getParent();
        if (cryptoProvider.isSystemClosed() || parent.isDeleted() || getAccess(parent, userService.getGroups(token)) != Access.RW || name == null || name.length() == 0) {
            return MessageDto.builder().message("ive8").build();
        }
        password.setName(name);
        password.setEncryptedLogin(cryptoProvider.encryptDbEntry(login));
        password.setEncryptedNote(cryptoProvider.encryptDbEntry(note));
        if (pass.length() > 0) {
            password.setEncryptedPass(cryptoProvider.encryptDbEntry(pass));
        }
        password.setEditedDate(new Date());
        password.setEditedBy(token.getLogin());
        return MessageDto.builder().build();
    }

    @Override
    @Transactional
    public MessageDto removePassword(Token token, long pwdId) {
        Password password = passwordRepository.getOne(pwdId);
        Container parent = password.getParent();
        if (getAccess(parent, userService.getGroups(token)) != Access.RW) {
            return MessageDto.builder().message("ive9").build();
        }
        if (password.isDeleted()) {
            passwordRepository.delete(password);
        } else {
            password.setDeleted(true);
            password.setDeletedDate(new Date());
            password.setDeletedBy(token.getLogin());
        }
        return MessageDto.builder().build();
    }

    @Override
    @Transactional(readOnly = true)
    public ContainerDto getDeletedItems(Token token, long containerId, String sortNotes, String sortPwd) {
        Container container = containerRepository.getOne(containerId);
        if (cryptoProvider.isSystemClosed() || container.isDeleted() || !userService.isAdmin(token)) {
            return ContainerDto.builder().build();
        }
        String notesSortField = sortNotes.substring(0, sortNotes.indexOf(","));
        Sort.Direction notesSortDirection = Sort.Direction.fromString(sortNotes.substring(sortNotes.indexOf(",") + 1));
        List<Note> deletedNotes = noteRepository.getAllByParentAndDeleted(container, true, Sort.by(notesSortDirection, notesSortField));
        Set<Password> deletedPasswords = passwordRepository.getAllByParentAndDeleted(container, true);
        //TODO sort password implement
        return ContainerDto.builder()
                .id(containerId)
                .notes(deletedNotes.stream().map(n -> NoteDto.builder()
                        .id(n.getId())
                        .name(n.getName())
                        .createdDate(n.getCreatedDate())
                        .createdBy(n.getCreatedBy())
                        .editedDate(n.getEditedDate())
                        .editedBy(n.getEditedBy())
                        .deletedDate(n.getDeletedDate())
                        .deletedBy(n.getDeletedBy())
                        .build())
                        .collect(Collectors.toList()))
                .passwords(deletedPasswords.stream().map(p -> PasswordDto.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .createdDate(p.getCreatedDate())
                        .createdBy(p.getCreatedBy())
                        .editedDate(p.getEditedDate())
                        .editedBy(p.getEditedBy())
                        .deletedDate(p.getDeletedDate())
                        .deletedBy(p.getDeletedBy())
                        .build())
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    @Transactional
    public MessageDto restoreNote(Token token, long noteId) {
        if (!userService.isAdmin(token)) {
            return MessageDto.builder().message("di4").build();
        }
        Note note = noteRepository.getOne(noteId);
        note.setDeleted(false);
        return MessageDto.builder().build();
    }

    @Override
    @Transactional
    public MessageDto restorePasswd(Token token, long pwdId) {
        if (!userService.isAdmin(token)) {
            return MessageDto.builder().message("di5").build();
        }
        Password password = passwordRepository.getOne(pwdId);
        password.setDeleted(false);
        return MessageDto.builder().build();
    }

    private ContainerDto buildTree(Container container, Set<Group> groups) {
        Access access = getAccess(container, groups);
        if (access == Access.NA || container.isDeleted()) {
            return ContainerDto.builder().build();
        }
        Set<ContainerDto> children = new TreeSet<>();
        if (!container.getChildren().isEmpty()) {
            for (Container childContainer : container.getChildren()) {
                ContainerDto childDto = buildTree(childContainer, groups);
                if (childDto.getId() != null) {
                    children.add(childDto);
                }
            }
        }
        List<NoteDto> notes = container.getNotes().stream()
                .filter(n -> !n.isDeleted())
                .map(n -> NoteDto.builder().id(n.getId()).name(n.getName()).build())
                .sorted(Comparator.comparing(NoteDto::getName))
                .collect(Collectors.toList());
        List<PasswordDto> passwords = container.getPasswords().stream()
                .filter(p -> !p.isDeleted())
                .map(p -> PasswordDto.builder().id(p.getId()).name(p.getName()).build())
                .sorted(Comparator.comparing(PasswordDto::getName))
                .collect(Collectors.toList());
        return ContainerDto.builder()
                .id(container.getId())
                .name(container.getName())
                .access(access)
                .children(children)
                .notes(notes)
                .passwords(passwords)
                .build();
    }

    private Access getAccess(Container container, Set<Group> groups) {
        for (Group group : groups) {
            if (group.isAdminSettings() || container.getGroupsRW().contains(group)) {
                return Access.RW;
            }
        }
        for (Group group : groups) {
            if (container.getGroupsRO().contains(group)) {
                return Access.RO;
            }
        }
        return Access.NA;
    }
}
