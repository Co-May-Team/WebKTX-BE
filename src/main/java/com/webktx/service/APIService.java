package com.webktx.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.webktx.constant.Constant;
import com.webktx.entity.Image;
import com.webktx.entity.ResponseObject;
import com.webktx.model.ImageModel;
import com.webktx.repository.impl.ImageRepositoryImpl;
import com.webktx.ultil.Ultil;

@Service
public class APIService {
	@Autowired
	ImageRepositoryImpl imageRepositoryImpl;
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

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

	public ResponseEntity<Object> getListBaseImage() {
		List<Image> imageList = new ArrayList<>();
		List<ImageModel> imageModelList = new ArrayList<>();
		
		imageList = imageRepositoryImpl.findAllBaseImage() ;
		for(Image img : imageList) {
			ImageModel imageModel = imageRepositoryImpl.toModel(img);
			imageModelList.add(imageModel);
		}
		return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", imageModelList));
	}

//	public List<String> traverseDepthFiles(String dirPath) {
//		File fileOrDir = null;
//		try {
//			StringBuilder baseURL = new StringBuilder(System.getProperty("user.dir")).append(Constant.URL_IMAGE_SERVER);
//			fileOrDir = new File(baseURL.toString());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		List<String> imageList = new ArrayList<>();
//		if (fileOrDir.isDirectory()) {
//			// in ten folder ra man hinh
//			System.out.println(fileOrDir.getAbsolutePath());
//
//			final File[] children = fileOrDir.listFiles();
//			if (children == null) {
//				return imageList;
//			}
//			// sắp xếp file theo thứ tự tăng dần
////	            Arrays.sort(children, new Comparator<File>() {
////	                public int compare(final File o1, final File o2) {
////	                    return o1.getName().compareTo(o2.getName());
////	                }
////	            });
//			for (final File each : children) {
//				// Only get image name in folder, not loop folder
//				if (!each.isDirectory() && each.isFile())
//					imageList.add(Ultil.converBaseImageNameToLink(each.getName()));
//			}
//
//		}
//		return imageList;
//	}

	public ResponseEntity<Object> uploadBaseImage(MultipartHttpServletRequest request){
		String base64Result = "";
//			// Lay r ds ten file
		MultiValueMap<String, MultipartFile> form = request.getMultiFileMap();
		List<MultipartFile> files = form.get("image");
		String title = request.getParameter("title");
		
		StringBuilder pathSaveFile = new StringBuilder(System.getProperty("user.dir"));
		List<MultipartFile> listImageSave = new ArrayList<>();
		List<ImageModel> imageModels = new ArrayList<>();
		StringBuilder newFileName = new StringBuilder("");
		Image img = null;
		JsonMapper jsonMapper = new JsonMapper();
//		String title = jsonMapper.readTree(json).get("title").asText();
		UserDetailsImpl userDetail = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		ImageModel imageModel = null;
		for (MultipartFile mpf : files) {
			if (mpf.getOriginalFilename().equals("")) {
				continue;
			}
//					B1: lay ra duong dan se luu file
			pathSaveFile.append(Constant.URL_IMAGE_SERVER);

			// Get extension
			String[] extensions = mpf.getOriginalFilename().split("\\.");
			// validate extensions
			if (!extensions[extensions.length - 1].equals("jpg") && !extensions[extensions.length - 1].equals("png")) {
				return ResponseEntity.status(HttpStatus.OK)
						.body(new ResponseObject("ERROR", "Image's format only support JPG or PNG", ""));
			}
			StringBuilder ext = new StringBuilder(".").append(extensions[extensions.length - 1]);
			listImageSave.add(mpf);
//					B2: Tao file
			System.out.println("Path save file: " + pathSaveFile);
		}
		if (listImageSave.isEmpty()) {
			return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "No image to save", ""));
		}
		for (MultipartFile mpf : listImageSave) {
			newFileName.setLength(0);
			try {
				newFileName.append(resizeImage(mpf, Constant.IMAGE_WIDTH, Constant.IMAGE_HEIGHT,Constant.URL_IMAGE_SERVER));
				img =  new Image();
				img.setTitle(title);
				img.setUserId(userDetail.getId());
				img.setImageName(newFileName.toString());
				int imageId = imageRepositoryImpl.add(img);
				imageModel = new ImageModel();
				imageModel = imageRepositoryImpl.toModel(img);
				imageModels.add(imageModel);
			} catch (Exception e) {
				return ResponseEntity.status(HttpStatus.OK)
						.body(new ResponseObject("ERROR", "Some error when save file", ""));
			}
		}
		return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "", imageModels));
	}
	public ResponseEntity<Object> edit(String json){
		JsonMapper jsonMapper = new JsonMapper();
		JsonNode jsonObject;
		Image image;
		ImageModel imageModel = new ImageModel();
		UserDetailsImpl userDetail = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		try {
			jsonObject = jsonMapper.readTree(json);
			Integer imageId = jsonObject.get("id") == null  ? 0 : jsonObject.get("id").asInt();
			String title = ((jsonObject.get("title") == null) || (jsonObject.get("title").asText() == "")) ? ""
					: jsonObject.get("title").asText();
			image = imageRepositoryImpl.findById(imageId);
			if(image==null) {
				return ResponseEntity.status(HttpStatus.OK)
						.body(new ResponseObject("ERROR", "Image file not found", "")); 
			}
			image.setTitle(title);
			image.setUserId(userDetail.getId());
			if(imageRepositoryImpl.edit(image)==0) {
				return ResponseEntity.status(HttpStatus.OK)
						.body(new ResponseObject("ERROR", "Edit image fail", "")); 
			}
			imageModel = imageRepositoryImpl.toModel(image);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.OK)
				.body(new ResponseObject("OK", "Successfully", imageModel)); 
	}
	public ResponseEntity<Object> findById(Integer id) {
		Image image = null;
		ImageModel imageModel = new ImageModel();
		image = imageRepositoryImpl.findById(id);
		if(image==null) {
			return ResponseEntity.status(HttpStatus.OK)
					.body(new ResponseObject("ERROR", "Image file not found", "")); 
		}
		imageModel = imageRepositoryImpl.toModel(image);
		return ResponseEntity.status(HttpStatus.OK)
				.body(new ResponseObject("OK", "Successfully", imageModel)); 
	}
	public ResponseEntity<Object> delete(Integer id, String pathToDelete) {
		Image imageTmp = new Image();
		imageTmp = imageRepositoryImpl.findById(id);
		Integer updateStatus = imageRepositoryImpl.delete(id);
		try {
			if (updateStatus.equals(1)) {
				StringBuilder pathSaveFile = new StringBuilder(System.getProperty("user.dir"));
				pathSaveFile.append(pathToDelete);
				File file = new File(pathSaveFile + imageTmp.getImageName());
				if(!file.exists()) {
					return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("ERROR", "Delete base image fail", ""));	
				}
				try {
					file.delete();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Delete Successfully", " "));
			} else {
				return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("Error", "Delete base image fail", ""));
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ResponseObject("ERROR", "Have error delete service: ", e.getMessage()));
		}
	}
	 public String resizeImage(MultipartFile imageFile,int targetWidth, int targetHeight, String pathToSave) throws IOException {
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
	            BufferedImage bufferedImage = ImageIO.read(imageFile.getInputStream());
	            BufferedImage outputImage = Scalr.resize(bufferedImage, targetWidth);
//	            String newFileName ="(" + CMDConstrant.IMAGE_WIDTH + ")" + sourceFile.getName() ;
	            String[] extensions = imageFile.getOriginalFilename().split("\\.");
//	            String newFileName = "(" + targetWidth + ")" + imageFile.getOriginalFilename();
	            String newFileName = String.format("%s_%s", new SimpleDateFormat("yyyy-MM-dd-HHmmss").format(new Date().getTime()),
						RandomStringUtils.randomAlphanumeric(5) + "." +  extensions[extensions.length-1]);
	            LOGGER.info("resizeImage: file name: " +  newFileName);
	    		StringBuilder pathSaveFile = new StringBuilder(System.getProperty("user.dir"));
				pathSaveFile.append(pathToSave);

	            Path path = Paths.get(pathSaveFile.toString(),newFileName);
	            LOGGER.info("resizeImage: path save: " +  path.toString());
	            File newImageFile = path.toFile();
	            ImageIO.write(outputImage, extensions[extensions.length-1], newImageFile);
	            outputImage.flush();
	            return newFileName;
	    }
}
