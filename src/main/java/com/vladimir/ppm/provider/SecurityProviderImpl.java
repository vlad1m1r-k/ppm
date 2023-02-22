package com.vladimir.ppm.provider;

import com.vladimir.ppm.domain.DynamicListItem;
import com.vladimir.ppm.domain.User;
import com.vladimir.ppm.domain.UserStatus;
import com.vladimir.ppm.repository.UserRepository;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@EnableScheduling
public class SecurityProviderImpl implements SecurityProvider {
    private final SettingsProvider settingsProvider;
    private final UserRepository userRepository;
    private final Map<String, DynamicListItem> ipAddrMap = new ConcurrentHashMap<>();
    private final Map<Long, DynamicListItem> usersMap = new ConcurrentHashMap<>();

    public SecurityProviderImpl(SettingsProvider settingsProvider, UserRepository userRepository) {
        this.settingsProvider = settingsProvider;
        this.userRepository = userRepository;
    }

    @Scheduled(fixedRate = 2 * 60 * 60 * 1000)
    public void clearDynamicLists() {
        for (Map.Entry<String, DynamicListItem> entry : ipAddrMap.entrySet()) {
            if (!entry.getValue().isBanned() && entry.getValue().getLastLoginAttemptTime() + 2 * 60 * 60 * 1000 < System.currentTimeMillis()) {
                ipAddrMap.remove(entry.getKey());
            }
            if (entry.getValue().isBanned() && entry.getValue().getUnbanTime() < System.currentTimeMillis()) {
                ipAddrMap.remove(entry.getKey());
            }
        }
        for (Map.Entry<Long, DynamicListItem> entry : usersMap.entrySet()) {
            if (entry.getValue().getLastLoginAttemptTime() + 2 * 60 * 60 * 1000 < System.currentTimeMillis()) {
                usersMap.remove(entry.getKey());
            }
        }
    }

    @Override
    public boolean isIpBanned(String ip) {
        return !settingsProvider.isIpWhitelisted(ip) && (settingsProvider.isIpBlackListed(ip)
                || ipAddrMap.get(ip) != null && ipAddrMap.get(ip).isBanned());
    }

    @Override
    public void registerLoginAttempt(String ip, boolean success) {
        if (!settingsProvider.isIpWhitelisted(ip)) {
            if (success) {
                ipAddrMap.remove(ip);
                return;
            }
            DynamicListItem ipAddr = ipAddrMap.get(ip);
            if (ipAddr == null) {
                ipAddr = new DynamicListItem();
                ipAddr.setLastLoginAttemptTime(System.currentTimeMillis());
                ipAddr.setTotalLoginAttempts(1);
                ipAddrMap.put(ip, ipAddr);
            } else {
                ipAddr.setLastLoginAttemptTime(System.currentTimeMillis());
                ipAddr.setTotalLoginAttempts(ipAddr.getTotalLoginAttempts() + 1);
                if (ipAddr.getTotalLoginAttempts() >= settingsProvider.getIncorrectLoginAttempts()) {
                    if (settingsProvider.getIpBanTimeDays() == 0) {
                        settingsProvider.addIpToBlackList(ip);
                        ipAddrMap.remove(ip);
                    } else {
                        ipAddr.setBanned(true);
                        ipAddr.setUnbanTime(System.currentTimeMillis() + (long) settingsProvider.getIpBanTimeDays() * 24 * 60 * 60 * 1000);
                    }
                }
            }
        }
    }

    @Override
    @Transactional
    public void registerPasswordAttempt(long userId, boolean success) {
        if (success) {
            usersMap.remove(userId);
            return;
        }
        DynamicListItem userItem = usersMap.get(userId);
        if (userItem == null) {
            userItem = new DynamicListItem();
            userItem.setLastLoginAttemptTime(System.currentTimeMillis());
            userItem.setTotalLoginAttempts(1);
            usersMap.put(userId, userItem);
        } else {
            userItem.setLastLoginAttemptTime(System.currentTimeMillis());
            userItem.setTotalLoginAttempts(userItem.getTotalLoginAttempts() + 1);
            if (userItem.getTotalLoginAttempts() >= settingsProvider.getIncorrectPasswdAttempts()) {
                User user = userRepository.getReferenceById(userId);
                user.setStatus(UserStatus.DISABLED);
            }
        }
    }

    @Override
    public Map<String, DynamicListItem> getDynamicIpMap() {
        return ipAddrMap;
    }
}
