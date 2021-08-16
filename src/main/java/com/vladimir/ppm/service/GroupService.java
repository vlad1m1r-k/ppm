package com.vladimir.ppm.service;

import com.vladimir.ppm.domain.Group;
import com.vladimir.ppm.domain.Token;
import com.vladimir.ppm.dto.GroupDto;
import com.vladimir.ppm.dto.MessageDto;

import java.util.List;

public interface GroupService {
    List<GroupDto> getGroups(Token token, String sort);
    MessageDto addGroup(Token token, String name, boolean adminSettings);
    MessageDto editGroup(Token token, long groupId, String name, boolean adminSettings);
    MessageDto deleteGroup(Token token, long groupId);
    MessageDto editGroupMembers(Token token, long groupId, long userId, boolean member);
    Group getGroupById(long groupId);
}
