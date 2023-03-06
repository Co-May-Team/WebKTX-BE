package com.webktx.ultil;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.util.IOUtils;
import org.springframework.stereotype.Component;

import com.webktx.constant.Constant;

@Component
public class Ultil {
	public byte[] getImageByName(String name, String path) throws IOException {
		byte[] image = null;
		StringBuilder baseURL = new StringBuilder(System.getProperty("user.dir")).append(path);
		final InputStream in = new BufferedInputStream(new FileInputStream(baseURL + name.trim()));
		image = IOUtils.toByteArray(in);
		return image;
	}
	public static String converImageNameToLink(String imgName) {
		StringBuilder link = new StringBuilder();
		link.append(Constant.SERVER_IP).append("/api/get-image/").append(imgName);
		return link.toString();
	}
	public static String converBaseImageNameToLink(String imgName) {
		StringBuilder link = new StringBuilder();
		link.append(Constant.SERVER_IP).append("/api/get-base-image/").append(imgName);
		return link.toString();
	}
}
