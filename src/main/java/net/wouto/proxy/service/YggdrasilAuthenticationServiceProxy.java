package net.wouto.proxy.service;

import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.response.Response;

import java.net.Proxy;
import java.net.URL;

public class YggdrasilAuthenticationServiceProxy extends YggdrasilAuthenticationService {

	public YggdrasilAuthenticationServiceProxy(Proxy proxy, String s) {
		super(proxy, s);
	}

	@Override
	public <T extends Response> T makeRequest(URL url, Object o, Class<T> aClass) throws AuthenticationException {
		return super.makeRequest(url, o, aClass);
	}

}
