package net.wouto.proxy.response.result;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;

import java.util.UUID;

public class HasJoinedMinecraftServerResponseImpl extends MinecraftProfilePropertiesResponseImpl {

	public HasJoinedMinecraftServerResponseImpl() {
	}

	public HasJoinedMinecraftServerResponseImpl(GameProfile profile) {
		super(profile);
	}

	public HasJoinedMinecraftServerResponseImpl(UUID id, String name, PropertyMap properties) {
		super(id, name, properties);
	}
}
