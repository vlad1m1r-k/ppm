package com.vladimir.ppm.service;

import com.vladimir.ppm.domain.Access;
import com.vladimir.ppm.domain.Container;
import com.vladimir.ppm.domain.Group;
import com.vladimir.ppm.domain.Token;
import com.vladimir.ppm.domain.User;
import com.vladimir.ppm.dto.ContainerDto;
import com.vladimir.ppm.repository.ContainerRepository;
import com.vladimir.ppm.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
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
    public boolean moveContainer(Token token, long itemId, long moveToId) {
        Container container = containerRepository.getOne(itemId);
        Container cntMoveTo = containerRepository.getOne(moveToId);
        if (getAccess(container, userService.getGroups(token)) == Access.RW && getAccess(cntMoveTo, userService.getGroups(token)) == Access.RW
                && !container.getName().equals("root") && !container.equals(cntMoveTo)) {
            container.getParent().getChildren().remove(container);
            container.setParent(cntMoveTo);
            cntMoveTo.addChild(container);
            return true;
        }
        return false;
    }

    private ContainerDto buildTree(Container container, Set<Group> groups) {
        Access access = getAccess(container, groups);
        if (access == Access.NA) {
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
