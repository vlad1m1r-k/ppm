package com.vladimir.ppm.service;

import com.vladimir.ppm.domain.Access;
import com.vladimir.ppm.domain.Token;
import com.vladimir.ppm.dto.AccessDto;
import com.vladimir.ppm.dto.ContainerDto;
import com.vladimir.ppm.dto.MessageDto;
import com.vladimir.ppm.dto.PasswordDto;

import java.util.List;

public interface ContainerService {
    ContainerDto getTree(Token token);
    MessageDto moveContainer(Token token, long itemId, long moveToId);
    MessageDto add(Token token, long parentId, String name);
    MessageDto restore(Token decryptedToken, long contId, long restoreToId);
    MessageDto delete(Token token, long itemId, boolean permanent);
    MessageDto rename(Token token, long itemId, String name);
    MessageDto addNote(Token token, long parentId, String name, String text);
    MessageDto getNote(Token token, long noteId);
    MessageDto editNote(Token token, long noteId, String name, String text);
    MessageDto removeNote(Token token, long noteId, boolean permanent);
    MessageDto addPasswd(Token token, long parentId, String name, String login, String passwd, String note);
    PasswordDto getPwdEnv(Token token, long pwdId);
    PasswordDto getPwdBody(Token token, long pwdId);
    MessageDto editPassword(Token token, long pwdId, String name, String login, String pass, String note);
    MessageDto removePassword(Token token, long pwdId, boolean permanent);
    ContainerDto getDeletedItems(Token token, long containerId, String sortNotes, String sortPwd);
    MessageDto restoreNote(Token token, long noteId);
    MessageDto restorePasswd(Token token, long pwdId);
    List<ContainerDto> getDeletedContainers(Token token, String sort);
    MessageDto setAccess(Token token, long containerId, long groupId, Access access, boolean ptAbove, boolean sameBelow);
    List<AccessDto> getAssignedGroups(Token token, long containerId);
}
