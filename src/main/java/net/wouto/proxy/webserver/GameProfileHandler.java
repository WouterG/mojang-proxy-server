package net.wouto.proxy.webserver;

import com.mojang.authlib.GameProfile;
import net.wouto.proxy.MojangProxyServer;
import net.wouto.proxy.cache.GameProfileCache;
import net.wouto.proxy.response.result.BasicGameProfile;
import net.wouto.proxy.response.result.ProfileSearchResultsResponseImpl;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class GameProfileHandler {

	private GameProfileCache cache;

	public GameProfileHandler() {
		this.cache = MojangProxyServer.get().getGameProfileCache();
	}

	@RequestMapping(value = "/profiles/minecraft", method = RequestMethod.POST)
	@ResponseBody
	public BasicGameProfile[] findProfilesByNames(@RequestBody List<String> names, @RequestParam(value = "proxyKey", required = false) String key) throws Exception {
		MojangProxyServer.authorize(key);
		if (MojangProxyServer.LOG_KNOWN_REQUESTS) {
			String namesCombined = "\"" + String.join("\", \"", names) + "\"";
			System.out.println("forwarding findProfilesByNames(names:[" + namesCombined + "])");
		}
		ProfileSearchResultsResponseImpl response = this.cache.findProfilesByNames(names);
		if (response.getProfiles() == null) {
			return new BasicGameProfile[0];
		}
		List<BasicGameProfile> profiles = new ArrayList<>();
		for (GameProfile gameProfile : response.getProfiles()) {
			if (gameProfile != null) {
				profiles.add(new BasicGameProfile(gameProfile.getId().toString().replace("-", ""), gameProfile.getName()));
			}
		}
		return profiles.toArray(new BasicGameProfile[profiles.size()]);
	}

}
