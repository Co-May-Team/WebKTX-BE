package com.webktx.controller;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.poi.util.IOUtils;
import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.webktx.constant.Constant;
import com.webktx.entity.ResponseObject;
import com.webktx.service.APIService;
import com.webktx.ultil.Ultil;

@Controller
@RequestMapping("/api")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ApiController {
	@Autowired
	ServletContext context;
	
	@Autowired
	Ultil ultil;
	@Autowired
	APIService apiService;
	
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@GetMapping(value = "/get-image/{name}", produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody byte[] getImageWithMediaType(
    		@PathVariable String name
    		) throws IOException {
        return ultil.getImageByName(name.trim(), Constant.URL_IMAGE_SERVER_POST);
    }
	@GetMapping(value = "/get-base-images")
    public  ResponseEntity<Object> getListBaseImage(
    		) throws IOException {
        return apiService.getListBaseImage();
    }
	@GetMapping(value = "/get-base-image/{name}", produces = MediaType.IMAGE_JPEG_VALUE)
	 public @ResponseBody byte[] getBaseImageWithMediaType(
	    		@PathVariable String name
	    		) throws IOException {
	        return ultil.getImageByName(name.trim(), Constant.URL_IMAGE_SERVER);
	    }
	@PostMapping("/upload-images")
	@ResponseBody
	public ResponseEntity<Object> uploadFile(MultipartHttpServletRequest request) throws IOException {
		String base64Result = "";
//		// Lay r ds ten file
		MultiValueMap<String, MultipartFile> form = request.getMultiFileMap();
		List<MultipartFile> files = form.get("image");
		StringBuilder pathSaveFile = new StringBuilder(System.getProperty("user.dir"));
		Map<String, String> result = new LinkedHashMap<>();
		for (MultipartFile mpf : files) {
			if (mpf.getOriginalFilename().equals("")) {
				continue;
			}
//				B1: lay ra duong dan se luu file
			pathSaveFile.append(Constant.URL_IMAGE_SERVER_POST);

			// Get extension
			String[] extensions = mpf.getOriginalFilename().split("\\.");
			StringBuilder ext = new StringBuilder(".").append(extensions[extensions.length - 1]);
//				B2: Tao file
			System.out.println("Path save file: " + pathSaveFile);
			String newFileName = resizeImage(mpf, Constant.IMAGE_WIDTH, Constant.IMAGE_HEIGHT);
			if (!newFileName.equals("")) {
				result.put("name", newFileName);
				StringBuilder link = new StringBuilder();
				link.append(Constant.SERVER_IP).append("/api/get-image/").append(newFileName);
				result.put("link",link.toString());
				return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "", result));
			}else {
				return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("ERROR", "Some error when save file", ""));
			}
			
		}
		return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "No image to save", ""));

	}
	 public String resizeImage(MultipartFile imageFile,int targetWidth, int targetHeight) {
//	        try {
//	            BufferedImage bufferedImage = ImageIO.read(sourceFile);
//	            Image resultingImage = bufferedImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_DEFAULT);
//			    BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
//			    outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);
//	            return true;
//	        } catch (IOException e) {
//	        	LOGGER.error(e.getMessage(), e);
//	            return false;
//	        }
		 try {
	            BufferedImage bufferedImage = ImageIO.read(imageFile.getInputStream());
	            BufferedImage outputImage = Scalr.resize(bufferedImage, targetWidth);
//	            String newFileName ="(" + CMDConstrant.IMAGE_WIDTH + ")" + sourceFile.getName() ;
	            String[] extensions = imageFile.getOriginalFilename().split("\\.");
//	            String newFileName = "(" + targetWidth + ")" + imageFile.getOriginalFilename();
	            String newFileName = String.format("%s_%s", new SimpleDateFormat("yyyy-MM-dd-HHmmss").format(new Date().getTime()),
						RandomStringUtils.randomAlphanumeric(5) + "." +  extensions[extensions.length-1]);
	            LOGGER.info("resizeImage: file name: " +  newFileName);
	    		StringBuilder pathSaveFile = new StringBuilder(System.getProperty("user.dir"));
				pathSaveFile.append(Constant.URL_IMAGE_SERVER_POST);

	            Path path = Paths.get(pathSaveFile.toString(),newFileName);
	            LOGGER.info("resizeImage: path save: " +  path.toString());
	            File newImageFile = path.toFile();
	            ImageIO.write(outputImage, extensions[extensions.length-1], newImageFile);
	            outputImage.flush();
	            return newFileName;
	        } catch (IOException e) {
	        	System.out.println("Some error when resize file:");
	            e.printStackTrace();
	            return "";
	        }
	    }
}
