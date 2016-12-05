package net.wouto.proxy.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.wouto.proxy.Config;
import net.wouto.proxy.Util;
import net.wouto.proxy.response.result.HasJoinedMinecraftServerResponseImpl;
import net.wouto.proxy.response.result.MinecraftProfilePropertiesResponseImpl;
import net.wouto.proxy.response.result.ProfileSearchResultsResponseImpl;
import net.wouto.proxy.service.MojangAPI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class GameProfileCache {

    private Cache<String, GameProfile> nameProfileCache;
    private Cache<UUID, GameProfile> uuidProfileCache;

    private Callable<GameProfile> getNullProfile = () -> null;

    public GameProfileCache(Config config) {
        this.nameProfileCache = CacheBuilder.newBuilder()
                .maximumSize(Long.parseLong(config.getProperty("cacheCount", "10000")))
                .expireAfterWrite(Long.parseLong(config.getProperty("cacheDuration", "3600")), TimeUnit.SECONDS)
                .build();

        this.uuidProfileCache = CacheBuilder.newBuilder()
                .maximumSize(Long.parseLong(config.getProperty("cacheCount", "10000")))
                .expireAfterWrite(Long.parseLong(config.getProperty("cacheDuration", "3600")), TimeUnit.SECONDS)
                .build();
    }

    public MinecraftProfilePropertiesResponseImpl fillGameProfile(String uuid, boolean unsigned) {
        MinecraftProfilePropertiesResponseImpl response = null;
        try {
            UUID uuidObj = Util.deserialize(uuid);
            if (uuidObj != null) {
                response = new MinecraftProfilePropertiesResponseImpl(this.uuidProfileCache.get(uuidObj, getNullProfile));
            }
        } catch (Exception ignored) {
        }
        if (response == null) {
            response = MojangAPI.getInstance().fillGameProfile(uuid, true);
            GameProfile cacheEntry = response.getGameProfile();
            this.nameProfileCache.put(cacheEntry.getName(), cacheEntry);
            this.uuidProfileCache.put(cacheEntry.getId(), cacheEntry);
        }
        return response;
    }

    public ProfileSearchResultsResponseImpl findProfilesByNames(List<String> names) {
        ProfileSearchResultsResponseImpl response = new ProfileSearchResultsResponseImpl();
        List<GameProfile> profiles = new ArrayList<>();
        names.removeIf(s -> {
            try {
                GameProfile profile = this.nameProfileCache.get(s, this.getNullProfile);
                if (profile != null) {
                    profiles.add(profile);
                    return true;
                }
            } catch (Exception ignored) {
            }
            return false;
        });
        if (!names.isEmpty()) {
            ProfileSearchResultsResponseImpl res = MojangAPI.getInstance().findProfilesByNames(names);
            Collections.addAll(profiles, res.getProfiles());
        }
        response.setProfiles(profiles.toArray(new GameProfile[profiles.size()]));
        // <DEBUG>
        System.out.println("GameProfileCache's interpretation");
        int length = (response.getProfiles() != null ? response.getProfiles().length : 0);
        System.out.println("Profile count: " + length);
        if (response.getProfiles() != null) {
            for (GameProfile gameProfile : response.getProfiles()) {
                for (Property property : gameProfile.getProperties().values()) {
                    System.out.println("\t" + property.getName() + " = " + property.getValue() + (!property.hasSignature() ? "" : " (signed: " + property.getSignature() + ")"));
                }
            }
        }
        // </DEBUG>
        return response;
    }

    public HasJoinedMinecraftServerResponseImpl hasJoined(String username, String serverId) throws Exception {
        HasJoinedMinecraftServerResponseImpl response = null;
        try {
            response = MojangAPI.getInstance().hasJoined(username, serverId);
            GameProfile gameProfile = new GameProfile(response.getId(), response.getName());
            gameProfile.getProperties().putAll(response.getPropertyMap());
            this.nameProfileCache.put(gameProfile.getName(), gameProfile);
            this.uuidProfileCache.put(gameProfile.getId(), gameProfile);
        } catch (Exception e) {
            e.printStackTrace();
            GameProfile profile = null;
            try {
                profile = this.nameProfileCache.get(username, getNullProfile);
            } catch (Exception ignored) {

            }
            if (profile != null) {
                response = new HasJoinedMinecraftServerResponseImpl(profile.getId(), profile.getName(), profile.getProperties());
            }
        }
        return response;
    }

}
