package com.webktx.ultil;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.util.IOUtils;
import org.springframework.stereotype.Component;

@Component
public class Ultil {
	public byte[] getImageByName(String name, String path) throws IOException {
		byte[] image = null;
		StringBuilder baseURL = new StringBuilder(System.getProperty("user.dir")).append(path);
		final InputStream in = new BufferedInputStream(new FileInputStream(baseURL + name.trim()));
		image = IOUtils.toByteArray(in);
		return image;
	}
}
