package com.vladimir.ppm.service;

import com.vladimir.ppm.domain.Token;
import com.vladimir.ppm.dto.ContainerDto;

public interface ContainerService {
    ContainerDto getTree(Token token);
}
