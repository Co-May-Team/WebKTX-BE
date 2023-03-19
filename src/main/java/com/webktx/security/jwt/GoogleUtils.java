package com.webktx.security.jwt;
import java.util.Base64;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class GoogleUtils {

	public static JSONObject parseJwt(String token) {
	    String[] splitToken = token.split("\\.");
	    String base64EncodedBody = splitToken[1];
	    String body = new String(Base64.getUrlDecoder().decode(base64EncodedBody));
	    return new JSONObject(body);
	}
}
