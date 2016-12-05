package net.wouto.proxy.webserver;

import net.wouto.proxy.MojangProxyServer;
import net.wouto.proxy.response.BaseResponse;
import net.wouto.proxy.response.error.UnknownRequestResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
public class UnknownRestHandler {

	private File unknownRequestsFolder = new File("./unknown-requests/");
	private DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH.mm.ss.SSS");

	public UnknownRestHandler() {
		if (!this.unknownRequestsFolder.exists()) {
			this.unknownRequestsFolder.mkdirs();
		}
	}

	@RequestMapping(value = "/**", method = {RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public BaseResponse handleUnknownRequest(HttpServletRequest request) throws Exception {
		String body = null;
		if (request.getMethod().equals(RequestMethod.POST.name())) {
			StringBuilder jb = new StringBuilder();
			String line;
			BufferedReader reader = request.getReader();
			while ((line = reader.readLine()) != null) {
				jb.append(line);
			}
			body = jb.toString();
		}
		UnknownRequestResponse response = new UnknownRequestResponse(getFullURL(request), request.getMethod(), body);
		if (MojangProxyServer.LOG_UNKNOWN_REQUESTS) {
			try {
				File f = getNextRequestDumpPath();
				MojangProxyServer.get().getMapper(true).writeValue(f, response);
				System.err.println("unknown request written to: " + f.getAbsolutePath());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

	public static String getFullURL(HttpServletRequest request) {
		StringBuffer requestURL = request.getRequestURL();
		String queryString = request.getQueryString();

		if (queryString == null) {
			return requestURL.toString();
		} else {
			return requestURL.append('?').append(queryString).toString();
		}
	}

	private File getNextRequestDumpPath() {
		String name = createFileName(new Date());
		File f = new File(unknownRequestsFolder, name + ".json");
		long id = 1;
		while (f.exists()) {
			f = new File(unknownRequestsFolder, name + "-" + (id++) + ".json");
		}
		return f;
	}

	private String createFileName(Date d) {
		return dateFormat.format(d);
	}

}
