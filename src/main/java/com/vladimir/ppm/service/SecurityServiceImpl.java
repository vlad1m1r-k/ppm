package com.vladimir.ppm.service;

import com.vladimir.ppm.security.IpAddr;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SecurityServiceImpl implements SecurityService {
    private final Map<String, IpAddr> ipAddrMap = new ConcurrentHashMap<>();

    @Override
    public boolean isIpBanned(String ip) {
        return ipAddrMap.get(ip) != null && ipAddrMap.get(ip).isBanned();
    }
}
