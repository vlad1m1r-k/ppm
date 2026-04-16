package com.volodymyr.ppm.service;

import java.util.List;

import com.volodymyr.ppm.domain.Group;
import com.volodymyr.ppm.domain.Token;
import com.volodymyr.ppm.dto.GroupDto;
import com.volodymyr.ppm.dto.MessageDto;

public interface GroupService {
    List<GroupDto> getGroups(Token token, String sort);
    MessageDto addGroup(Token token, String name, boolean adminSettings);
    MessageDto editGroup(Token token, long groupId, String name, boolean adminSettings);
    MessageDto deleteGroup(Token token, long groupId);
    MessageDto editGroupMembers(Token token, long groupId, long userId, boolean member);
    Group getGroupById(long groupId);
}
