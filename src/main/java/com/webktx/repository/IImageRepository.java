package com.webktx.repository;

import java.util.List;

import com.webktx.entity.Image;
import com.webktx.model.ImageModel;

public interface IImageRepository {
	int add(Image image);
	List<Image> findAllBaseImage();
	ImageModel toModel(Image image);
	Image findById(int id);
	Integer edit(Image image);
	Integer delete(Integer id);
	
}
