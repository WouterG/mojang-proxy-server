package net.wouto.proxy.service;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.HttpAuthenticationService;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.yggdrasil.YggdrasilGameProfileRepository;
import com.mojang.authlib.yggdrasil.response.ProfileSearchResultsResponse;
import com.mojang.util.UUIDTypeAdapter;
import net.wouto.proxy.response.result.HasJoinedMinecraftServerResponseImpl;
import net.wouto.proxy.response.result.MinecraftProfilePropertiesResponseImpl;
import net.wouto.proxy.response.result.ProfileSearchResultsResponseImpl;

import java.net.Proxy;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class MojangAPI {

	private static MojangAPI instance;

	private YggdrasilAuthenticationServiceProxy authenticationService;
	private YggdrasilMinecraftSessionServiceProxy sessionService;
	private YggdrasilGameProfileRepository gameProfileRepository;

	private String clientToken;

	private MojangAPI() {
		this.clientToken = UUID.randomUUID().toString();
		this.authenticationService = new YggdrasilAuthenticationServiceProxy(Proxy.NO_PROXY, clientToken);
		this.sessionService = new YggdrasilMinecraftSessionServiceProxy(this.authenticationService);
		this.gameProfileRepository = new YggdrasilGameProfileRepository(this.authenticationService);
	}

	public static MojangAPI getInstance() {
		if (instance == null) {
			instance = new MojangAPI();
		}
		return instance;
	}

	public HasJoinedMinecraftServerResponseImpl hasJoined(String name, String serverId) throws AuthenticationUnavailableException {
		if (name == null) {
			return null;
		}
		GameProfile profile = this.sessionService.hasJoinedServer(new GameProfile(null, name), serverId);
		return new HasJoinedMinecraftServerResponseImpl(profile.getId(), profile.getName(), profile.getProperties());
	}

	public MinecraftProfilePropertiesResponseImpl fillGameProfile(String uuid, boolean unsigned) {
		if (uuid == null) {
			return null;
		}
		GameProfile p = this.sessionService.fillGameProfile(new GameProfile(UUIDTypeAdapter.fromString(uuid), null), unsigned);
		return new MinecraftProfilePropertiesResponseImpl(p.getId(), p.getName(), p.getProperties());
	}

	public ProfileSearchResultsResponseImpl findProfilesByNames(List<String> names) {
		if (names == null) {
			return null;
		}
		if (names.size() > 100) {
			names = names.subList(0, 100);
		}
		Agent game = Agent.MINECRAFT;
		HashSet<String> nameSet = Sets.newHashSet();
		nameSet.addAll(names);

		for (Object o : Iterables.partition(nameSet, 100)) {
			List nameSetPart = (List) o;
			try {
				ProfileSearchResultsResponse result = this.authenticationService.makeRequest(HttpAuthenticationService.constantURL("https://api.mojang.com/profiles/" + game.getName().toLowerCase()), nameSetPart, ProfileSearchResultsResponse.class);
				return new ProfileSearchResultsResponseImpl(result.getProfiles());
			} catch (AuthenticationException e) {
				e.printStackTrace();
			}

		}
		return null;
	}

}
