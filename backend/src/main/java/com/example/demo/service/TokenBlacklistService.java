package com.example.demo.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.*;

@Service
public class TokenBlacklistService {

    private final Map<String, Long> blacklistTokens = new ConcurrentHashMap<>();
    private final ScheduledExecutorService cleanupService = Executors.newSingleThreadScheduledExecutor();

    public TokenBlacklistService(){
        cleanupService.scheduleAtFixedRate(this::removeExpiredTokens, 1, 1, TimeUnit.HOURS);
    }

    public void blackListToken(String token, long expirationTime){
        blacklistTokens.put(token, expirationTime);
    }

    public boolean isBlacklisted(String token){
        return blacklistTokens.containsKey(token);
    }

    public void removeExpiredTokens(){
        long currentTime = System.currentTimeMillis();
        blacklistTokens.entrySet().removeIf(entry -> entry.getValue() < currentTime);
    }

}
