package com.webktx.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.webktx.constant.Constant;
import com.webktx.entity.ResponseObject;
import com.webktx.ultil.Ultil;

@Service
public class APIService {
	@Autowired
	Ultil ultil;
	
	public static String convertToBase64(String name) throws IOException {
		StringBuilder baseURL = new StringBuilder(System.getProperty("user.dir")).append("/image/");
		byte[] data = null;
		try {
			InputStream in = new FileInputStream(baseURL + name.trim());
			System.out.println("file size (bytes)=" + in.available());
			data = new byte[in.available()];
			in.read(data);
			in.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return "";
		}
		// Base64 encoded by byte arrays with a string of Base64 encoded
		return new String(Objects.requireNonNull(Base64.encodeBase64(data)));
	}
	public ResponseEntity<Object> getListBaseImage(){
		 List<String> imageList = new ArrayList<>();
		 imageList = traverseDepthFiles(Constant.URL_IMAGE_SERVER);
		return ResponseEntity.status(HttpStatus.OK)
				.body(new ResponseObject("OK", "Successfully", imageList));
	}
	 public List<String> traverseDepthFiles(String dirPath) {
		 File fileOrDir = null;
		 try {
			 StringBuilder baseURL = new StringBuilder(System.getProperty("user.dir")).append(Constant.URL_IMAGE_SERVER);
			 fileOrDir = new File(baseURL.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		 List<String> imageList = new ArrayList<>();
		 if (fileOrDir.isDirectory()) {
	            // in ten folder ra man hinh
	            System.out.println(fileOrDir.getAbsolutePath());
	             
	            final File[] children = fileOrDir.listFiles();
	            if (children == null) {
	                return imageList;
	            }
	            // sắp xếp file theo thứ tự tăng dần
//	            Arrays.sort(children, new Comparator<File>() {
//	                public int compare(final File o1, final File o2) {
//	                    return o1.getName().compareTo(o2.getName());
//	                }
//	            });
	            for (final File each : children) {
	            	// Only get image name in folder, not loop folder
	            	if(!each.isDirectory() && each.isFile())
	            	imageList.add(ultil.converBaseImageNameToLink(each.getName()));
	            }
	            
	        }
	        return imageList;
	    }
}
