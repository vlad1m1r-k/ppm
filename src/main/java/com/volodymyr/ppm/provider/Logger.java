package com.volodymyr.ppm.provider;

import java.util.Date;

import com.volodymyr.ppm.domain.Acts;
import com.volodymyr.ppm.domain.Objects;

public interface Logger {
    void log(String user, Acts act, Objects object, String objName, Date date, String comment);
}
