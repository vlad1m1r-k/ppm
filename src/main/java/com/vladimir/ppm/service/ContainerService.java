package com.vladimir.ppm.service;

import com.vladimir.ppm.domain.Token;
import com.vladimir.ppm.dto.ContainerDto;
import com.vladimir.ppm.dto.MessageDto;

public interface ContainerService {
    ContainerDto getTree(Token token);
    MessageDto moveContainer(Token token, long itemId, long moveToId);
    MessageDto add(Token token, long parentId, String name);
    MessageDto delete(Token token, long itemId);
    MessageDto rename(Token token, long itemId, String name);
}