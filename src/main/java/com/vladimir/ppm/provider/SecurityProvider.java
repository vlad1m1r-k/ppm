package com.vladimir.ppm.provider;

import com.vladimir.ppm.domain.DynamicListItem;

import java.util.Map;

public interface SecurityProvider {
    boolean isIpBanned(String ip);
    void registerLoginAttempt(String ip, boolean  success);
    void registerPasswordAttempt(long userId, boolean success);
    Map<String, DynamicListItem> getDynamicIpMap();
}
