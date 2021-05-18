package com.vladimir.ppm.service;

import com.vladimir.ppm.domain.Access;
import com.vladimir.ppm.domain.Container;
import com.vladimir.ppm.domain.Group;
import com.vladimir.ppm.domain.Note;
import com.vladimir.ppm.domain.Password;
import com.vladimir.ppm.domain.Token;
import com.vladimir.ppm.dto.AccessDto;
import com.vladimir.ppm.dto.AccessTreeDto;
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
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ContainerServiceImpl implements ContainerService {
    private final UserService userService;
    private final ContainerRepository containerRepository;
    private final CryptoProvider cryptoProvider;
    private final NoteRepository noteRepository;
    private final PasswordRepository passwordRepository;
    private final GroupService groupService;

    public ContainerServiceImpl(UserService userService, ContainerRepository containerRepository, CryptoProvider cryptoProvider,
                                NoteRepository noteRepository, PasswordRepository passwordRepository, GroupService groupService) {
        this.userService = userService;
        this.containerRepository = containerRepository;
        this.cryptoProvider = cryptoProvider;
        this.noteRepository = noteRepository;
        this.passwordRepository = passwordRepository;
        this.groupService = groupService;
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
                || container.getName().equals("root") || container.equals(cntMoveTo) || container.isDeleted() || cntMoveTo.isDeleted()) {
            return MessageDto.builder().message("cfe1").build();
        }
        container.getParent().getChildren().remove(container);
        container.setEditedBy(token.getLogin());
        container.setEditedDate(new Date());
        cntMoveTo.addChild(container);
        return MessageDto.builder().build();
    }

    @Override
    @Transactional
    public MessageDto add(Token token, long parentId, String name) {
        Container parent = containerRepository.getOne(parentId);
        if (getAccess(parent, userService.getGroups(token)) == Access.RW && name.length() > 0 && !parent.isDeleted()) {
            Container container = new Container(name, parent, token.getLogin());
            container.setGroupsNA(new HashSet<>(parent.getGroupsNA()));
            container.setGroupsPT(new HashSet<>(parent.getGroupsPT()));
            container.setGroupsRO(new HashSet<>(parent.getGroupsRO()));
            container.setGroupsRW(new HashSet<>(parent.getGroupsRW()));
            container = containerRepository.save(container);
            parent.addChild(container);
        }
        return MessageDto.builder().build();
    }

    @Override
    @Transactional
    public MessageDto restore(Token decryptedToken, long contId, long restoreToId) {
        Container container = containerRepository.getOne(contId);
        Container cntRestoreTo = containerRepository.getOne(restoreToId);
        if (!userService.isAdmin(decryptedToken) || !container.isDeleted() || cntRestoreTo.isDeleted() || container.equals(cntRestoreTo)) {
            return MessageDto.builder().message("die3").build();
        }
        container.getParent().getChildren().remove(container);
        cntRestoreTo.addChild(container);
        container.setDeleted(false);
        return MessageDto.builder().build();
    }

    @Override
    @Transactional
    public MessageDto delete(Token token, long itemId, boolean permanent) {
        Container container = containerRepository.getOne(itemId);
        if (container.getName().equals("root") || container.getChildren().size() != 0
                || getAccess(container, userService.getGroups(token)) != Access.RW) {
            return MessageDto.builder().message("ive2").build();
        }
        if (container.isDeleted() && userService.isAdmin(token) && permanent) {
            containerRepository.getContainersByParent(container).forEach(c -> c.setParent(null));
            containerRepository.delete(container);
        } else {
            container.getParent().getChildren().remove(container);
            container.setDeletedBy(token.getLogin());
            container.setDeletedDate(new Date());
            container.setDeleted(true);
        }
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
        container.setEditedBy(token.getLogin());
        container.setEditedDate(new Date());
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
    public MessageDto removeNote(Token token, long noteId, boolean permanent) {
        Note note = noteRepository.getOne(noteId);
        Container parent = note.getParent();
        if (getAccess(parent, userService.getGroups(token)) != Access.RW) {
            return MessageDto.builder().message("ive6").build();
        }
        if (note.isDeleted() && userService.isAdmin(token) && permanent) {
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
    public MessageDto removePassword(Token token, long pwdId, boolean permanent) {
        Password password = passwordRepository.getOne(pwdId);
        Container parent = password.getParent();
        if (getAccess(parent, userService.getGroups(token)) != Access.RW) {
            return MessageDto.builder().message("ive9").build();
        }
        if (password.isDeleted() && userService.isAdmin(token) && permanent) {
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
        String pwdSortField = sortPwd.substring(0, sortPwd.indexOf(","));
        Sort.Direction pwdSortDirection = Sort.Direction.fromString(sortPwd.substring(sortPwd.indexOf(",") + 1));
        List<Password> deletedPasswords = passwordRepository.getAllByParentAndDeleted(container, true, Sort.by(pwdSortDirection, pwdSortField));
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
            return MessageDto.builder().message("die1").build();
        }
        Note note = noteRepository.getOne(noteId);
        note.setDeleted(false);
        return MessageDto.builder().build();
    }

    @Override
    @Transactional
    public MessageDto restorePasswd(Token token, long pwdId) {
        if (!userService.isAdmin(token)) {
            return MessageDto.builder().message("die2").build();
        }
        Password password = passwordRepository.getOne(pwdId);
        password.setDeleted(false);
        return MessageDto.builder().build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContainerDto> getDeletedContainers(Token token, String sort) {
        if (cryptoProvider.isSystemClosed() || !userService.isAdmin(token)) {
            return new ArrayList<>();
        }
        String sortField = sort.substring(0, sort.indexOf(","));
        Sort.Direction sortDirection = Sort.Direction.fromString(sort.substring(sort.indexOf(",") + 1));
        List<Container> deletedContainers = containerRepository.getAllByDeleted(true, Sort.by(sortDirection, sortField));
        return deletedContainers.stream().map(c -> ContainerDto.builder()
                .id(c.getId())
                .name(containerPathBuilder(c))
                .notes(c.getNotes().stream().map(n -> NoteDto.builder().name(n.getName()).build()).collect(Collectors.toList()))
                .passwords(c.getPasswords().stream().map(p -> PasswordDto.builder().name(p.getName()).build()).collect(Collectors.toList()))
                .createdDate(c.getCreatedDate())
                .createdBy(c.getCreatedBy())
                .editedDate(c.getEditedDate())
                .editedBy(c.getEditedBy())
                .deletedDate(c.getDeletedDate())
                .deletedBy(c.getDeletedBy())
                .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MessageDto setAccess(Token token, long containerId, long groupId, Access access, boolean ptAbove, boolean sameBelow) {
        if (userService.isAdmin(token)) {
            Container container = containerRepository.getOne(containerId);
            Group group = groupService.getGroupById(groupId);
            setAccessSwitch(container, group, access);
            if (ptAbove) {
                Container parent = container.getParent();
                while (parent != null) {
                    parent.getGroupsPT().add(group);
                    parent = parent.getParent();
                }
            }
            if (sameBelow) {
                setAccessRecursively(container, group, access);
            }
        }
        return MessageDto.builder().build();
    }

    @Override
    @Transactional
    public void removeAccess(Token token, long containerId, long groupId, Access access) {
        if (userService.isAdmin(token)) {
            Container container = containerRepository.getOne(containerId);
            Group group = groupService.getGroupById(groupId);
            removeAccessRecursively(container, group, access);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccessDto> getAssignedGroups(Token token, long containerId) {
        if (userService.isAdmin(token)) {
            Container container = containerRepository.getOne(containerId);
            List<AccessDto> accessList = new ArrayList<>();
            accessList.addAll(mapGroupToAccessDto(container.getGroupsNA(), Access.NA));
            accessList.addAll(mapGroupToAccessDto(container.getGroupsPT(), Access.PT));
            accessList.addAll(mapGroupToAccessDto(container.getGroupsRO(), Access.RO));
            accessList.addAll(mapGroupToAccessDto(container.getGroupsRW(), Access.RW));
            return accessList;
        }
        return new ArrayList<>();
    }

    @Override
    @Transactional(readOnly = true)
    public AccessTreeDto getAccessTree(Token token, long groupId) {
        if (userService.isAdmin(token)) {
            Group group = groupService.getGroupById(groupId);
            Container root = containerRepository.getContainerByName("root");
            return buildAccessTree(root, group);
        }
        return AccessTreeDto.builder().build();
    }

    private AccessTreeDto buildAccessTree(Container container, Group group) {
        boolean na = container.getGroupsNA().contains(group);
        boolean pt = container.getGroupsPT().contains(group);
        boolean ro = container.getGroupsRO().contains(group);
        boolean rw = container.getGroupsRW().contains(group);
        List<AccessTreeDto> children = new ArrayList<>();
        for (Container child : container.getChildren()) {
            AccessTreeDto childDto = buildAccessTree(child, group);
            if (childDto.getName() != null) {
                children.add(childDto);
            }
        }
        if (children.isEmpty() && !na && !pt && !ro && !rw) {
            return AccessTreeDto.builder().build();
        }
        return AccessTreeDto.builder()
                .id(container.getId())
                .name(container.getName())
                .children(children)
                .accessNA(na)
                .accessPT(pt)
                .accessRO(ro)
                .accessRW(rw)
                .build();
    }

    private List<AccessDto> mapGroupToAccessDto(Set<Group> groups, Access access) {
        return groups.stream().map(g -> AccessDto.builder()
                .id(g.getId())
                .name(g.getName())
                .access(access)
                .build())
                .collect(Collectors.toList());
    }

    private void setAccessRecursively(Container container, Group group, Access access) {
        for (Container childContainer : container.getChildren()) {
            setAccessSwitch(childContainer, group, access);
            setAccessRecursively(childContainer, group, access);
        }
    }

    private void setAccessSwitch(Container container, Group group, Access access) {
        switch (access) {
            case NA:
                container.getGroupsNA().add(group);
                break;
            case PT:
                container.getGroupsPT().add(group);
                break;
            case RO:
                container.getGroupsRO().add(group);
                break;
            case RW:
                container.getGroupsRW().add(group);
        }
    }

    private void removeAccessRecursively(Container container, Group group, Access access) {
        removeAccessSwitch(container, group, access);
        for (Container childContainer : container.getChildren()) {
            removeAccessRecursively(childContainer, group, access);
        }
    }

    private void removeAccessSwitch(Container container, Group group, Access access) {
        switch (access) {
            case NA:
                container.getGroupsNA().remove(group);
                break;
            case PT:
                container.getGroupsPT().remove(group);
                break;
            case RO:
                container.getGroupsRO().remove(group);
                break;
            case RW:
                container.getGroupsRW().remove(group);
        }
    }

    private ContainerDto buildTree(Container container, Set<Group> groups) {
        Access access = getAccess(container, groups);
        if (access == Access.NA || container.isDeleted()) {
            return ContainerDto.builder().build();
        }
        List<ContainerDto> children = new ArrayList<>();
        if (!container.getChildren().isEmpty()) {
            for (Container childContainer : container.getChildren()) {
                ContainerDto childDto = buildTree(childContainer, groups);
                if (childDto.getId() != null) {
                    children.add(childDto);
                }
            }
        }
        children.sort(Comparator.comparing(ContainerDto::getName));
        List<NoteDto> notes = new ArrayList<>();
        List<PasswordDto> passwords = new ArrayList<>();
        if (access == Access.RW) {
            notes = container.getNotes().stream()
                    .filter(n -> !n.isDeleted())
                    .map(n -> NoteDto.builder()
                            .id(n.getId())
                            .name(n.getName())
                            .createdDate(n.getCreatedDate())
                            .createdBy(n.getCreatedBy())
                            .editedDate(n.getEditedDate())
                            .editedBy(n.getEditedBy())
                            .deletedDate(n.getDeletedDate())
                            .deletedBy(n.getDeletedBy())
                            .build())
                    .sorted(Comparator.comparing(NoteDto::getName))
                    .collect(Collectors.toList());
            passwords = container.getPasswords().stream()
                    .filter(p -> !p.isDeleted())
                    .map(p -> PasswordDto.builder()
                            .id(p.getId())
                            .name(p.getName())
                            .createdDate(p.getCreatedDate())
                            .createdBy(p.getCreatedBy())
                            .editedDate(p.getEditedDate())
                            .editedBy(p.getEditedBy())
                            .deletedDate(p.getDeletedDate())
                            .deletedBy(p.getDeletedBy())
                            .build())
                    .sorted(Comparator.comparing(PasswordDto::getName))
                    .collect(Collectors.toList());
        }
        if (access == Access.RO) {
            notes = container.getNotes().stream()
                    .filter(n -> !n.isDeleted())
                    .map(n -> NoteDto.builder().id(n.getId()).name(n.getName()).build())
                    .sorted(Comparator.comparing(NoteDto::getName))
                    .collect(Collectors.toList());
            passwords = container.getPasswords().stream()
                    .filter(p -> !p.isDeleted())
                    .map(p -> PasswordDto.builder().id(p.getId()).name(p.getName()).build())
                    .sorted(Comparator.comparing(PasswordDto::getName))
                    .collect(Collectors.toList());
        }
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
            if (group.isAdminSettings()) {
                return Access.RW;
            }
        }
        for (Group group : groups) {
            if (container.getGroupsNA().contains(group)) {
                return Access.NA;
            }
        }
        for (Group group : groups) {
            if (container.getGroupsRW().contains(group)) {
                return Access.RW;
            }
        }
        for (Group group : groups) {
            if (container.getGroupsRW().contains(group)) {
                return Access.RW;
            }
        }
        for (Group group : groups) {
            if (container.getGroupsRO().contains(group)) {
                return Access.RO;
            }
        }
        for (Group group : groups) {
            if (container.getGroupsPT().contains(group)) {
                return Access.PT;
            }
        }
        return Access.NA;
    }

    private String containerPathBuilder(Container container) {
        if (container.getParent() != null) {
            return containerPathBuilder(container.getParent()) + "/" + container.getName();
        }
        return container.getName();
    }
}
