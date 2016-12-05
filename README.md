## Mojang Proxy Server

Configure the Mojang Proxy server by editing the configurationfile at `./proxy.properties`.

Defaults:

```
hostname=0.0.0.0
port=8000
logAllUnknownRequests=false
logKnownRequest=true
# gameProfileCacheClass=
authKey=
cacheCount=10000
cacheDuration=3600
```

 - **hostname:** `<ip/hostname>` The ip/hostname to listen to. 0.0.0.0 is global
 - **port:** `<port>` The port to listen to
 - **logAllUnknownRequests:** `<true/false>` Whether to log unknown requests to a `./unknown-requests` folder. Mainly for debugging.
 - **logKnownRequests:** `<true/false>` Whether to announce requests in the console to known endpoints
 - **gameProfileCacheClass:** `<null/class>` Either empty/null or a full class identifier for a class to take over the GameProfile caching. The default is the internal `net.wouto.proxy.cache.GameProfileCache` which is assumed if nothing is provided.
 - **authKey:** `<null/key>` Either empty/null or a string of characters that the server requires for authentication. Needs to match the server's proxy key if the server has one.
 - **cacheCount:** `<count>` The maximum amount of GameProfiles to keep in the application memory (or other storage if a custom gameProfileCacheClass is defined)
 - **cacheDuration:** `<timeout>` The amount of seconds to cache a GameProfile.

You can also decide to route one type of requests through Mojang's API and the other through the proxy, although this prevents the proxy from being able to cache items.

### Spigot Plugin

See [mojang-proxy-plugin](https://github.com/WouterG/mojang-proxy-plugin)