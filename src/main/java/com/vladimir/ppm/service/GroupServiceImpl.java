package com.vladimir.ppm.service;

import com.vladimir.ppm.domain.Acts;
import com.vladimir.ppm.domain.Group;
import com.vladimir.ppm.domain.Objects;
import com.vladimir.ppm.domain.Token;
import com.vladimir.ppm.domain.User;
import com.vladimir.ppm.dto.AccessTreeDto;
import com.vladimir.ppm.dto.GroupDto;
import com.vladimir.ppm.dto.MessageDto;
import com.vladimir.ppm.dto.UserDto;
import com.vladimir.ppm.provider.Logger;
import com.vladimir.ppm.repository.GroupRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupServiceImpl implements GroupService {
    private final UserService userService;
    private final GroupRepository groupRepository;
    private final ValidatorService validatorService;
    private final Logger logger;

    public GroupServiceImpl(UserService userService, GroupRepository groupRepository, ValidatorService validatorService,
                            Logger logger) {
        this.userService = userService;
        this.groupRepository = groupRepository;
        this.validatorService = validatorService;
        this.logger = logger;
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupDto> getGroups(Token token, String sort) {
        if (userService.isAdmin(token)) {
            String sortField = sort.substring(0, sort.indexOf(","));
            Sort.Direction sortDirection = Sort.Direction.fromString(sort.substring(sort.indexOf(",") + 1));
            List<Group> groups = groupRepository.findAll(Sort.by(sortDirection, sortField));
            return groups.stream().map(g -> GroupDto.builder()
                    .id(g.getId())
                    .name(g.getName())
                    .adminSettings(g.isAdminSettings())
                    .users(g.getUsers().stream().map(u -> UserDto.builder()
                            .id(u.getId())
                            .login(u.getLogin())
                            .build())
                            .collect(Collectors.toList()))
                    .build())
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    @Transactional
    public MessageDto addGroup(Token token, String name, boolean adminSettings) {
        if (userService.isAdmin(token)) {
            if (!validatorService.validateString(name)) {
                return MessageDto.builder().message("gpe1").build();
            }
            Group group = groupRepository.findGroupByName(name);
            if (group != null) {
                return MessageDto.builder().message("gpe2").build();
            }
            group = new Group(name, adminSettings);
            groupRepository.save(group);
            logger.log(token.getLogin(), Acts.CREATE, Objects.GROUP, name, new Date(), "Admin settings: " + adminSettings);
        }
        return MessageDto.builder().build();
    }

    @Override
    @Transactional
    public MessageDto editGroup(Token token, long groupId, String name, boolean adminSettings) {
        if (userService.isAdmin(token)) {
            if (!validatorService.validateString(name)) {
                return MessageDto.builder().message("gpe1").build();
            }
            Group group = groupRepository.getOne(groupId);
            if (!group.getName().equals(name) && groupRepository.findGroupByName(name) != null) {
                return MessageDto.builder().message("gpe2").build();
            }
            group.setName(name);
            group.setAdminSettings(adminSettings);
            logger.log(token.getLogin(), Acts.UPDATE, Objects.GROUP, name, new Date(), "Admin settings: " + adminSettings);
        }
        return MessageDto.builder().build();
    }

    @Override
    @Transactional
    public MessageDto deleteGroup(Token token, long groupId) {
        if (userService.isAdmin(token)) {
            Group group = groupRepository.getOne(groupId);
            group.getUsers().forEach(u -> u.getGroups().remove(group));
            groupRepository.delete(group);
            logger.log(token.getLogin(), Acts.DELETE, Objects.GROUP, group.getName(), new Date(), "");
        }
        return MessageDto.builder().build();
    }

    @Override
    @Transactional
    public MessageDto editGroupMembers(Token token, long groupId, long userId, boolean member) {
        if (userService.isAdmin(token)) {
            Group group = groupRepository.getOne(groupId);
            User user = userService.getUserById(userId);
            if (member) {
                group.getUsers().add(user);
            } else {
                group.getUsers().remove(user);
            }
            logger.log(token.getLogin(), Acts.UPDATE, Objects.GROUP, group.getName(), new Date(),
                    "User: " + user.getLogin() + " Member: " + member);
        }
        return MessageDto.builder().build();
    }

    @Override
    @Transactional(readOnly = true)
    public Group getGroupById(long groupId) {
        return groupRepository.getOne(groupId);
    }
}
