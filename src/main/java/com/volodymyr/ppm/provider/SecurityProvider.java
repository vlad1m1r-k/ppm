package com.volodymyr.ppm.provider;

import java.util.Map;

import com.volodymyr.ppm.domain.DynamicListItem;

public interface SecurityProvider {
    boolean isIpBanned(String ip);
    void registerLoginAttempt(String ip, boolean  success);
    void registerPasswordAttempt(long userId, boolean success);
    Map<String, DynamicListItem> getDynamicIpMap();
}
