package com.vladimir.ppm.service;

import com.vladimir.ppm.domain.Acts;
import com.vladimir.ppm.domain.Objects;

import java.util.Date;

public interface LoggerService {
    void log(String user, Acts act, Objects object, String objName, Date date, String comment);
}
