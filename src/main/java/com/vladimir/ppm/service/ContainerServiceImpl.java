package com.vladimir.ppm.service;

import com.vladimir.ppm.domain.Access;
import com.vladimir.ppm.domain.Container;
import com.vladimir.ppm.domain.Group;
import com.vladimir.ppm.domain.Token;
import com.vladimir.ppm.dto.ContainerDto;
import com.vladimir.ppm.dto.MessageDto;
import com.vladimir.ppm.repository.ContainerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

@Service
public class ContainerServiceImpl implements ContainerService {
    private final UserService userService;
    private final ContainerRepository containerRepository;

    public ContainerServiceImpl(UserService userService, ContainerRepository containerRepository) {
        this.userService = userService;
        this.containerRepository = containerRepository;
    }

    @Override
    @Transactional
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
        return ContainerDto.builder()
                .id(container.getId())
                .name(container.getName())
                .access(access)
                .children(children)
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