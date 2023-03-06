package com.webktx.repository.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.webktx.entity.Image;
import com.webktx.entity.Post;
import com.webktx.model.ImageModel;
import com.webktx.repository.IImageRepository;
import com.webktx.ultil.Ultil;
@Repository
@Transactional(rollbackFor = Exception.class)
public class ImageRepositoryImpl implements IImageRepository{
	@Autowired
	private SessionFactory sessionFactory;
	private static final Logger LOGGER = LoggerFactory.getLogger(ImageRepositoryImpl.class);

	@Override
	public int add(Image image) {
		Session session  = sessionFactory.getCurrentSession();
		int result = (int) session.save(image);
		return result;
	}

	@Override
	public ImageModel toModel(Image image) {
		ImageModel imageModel = new ImageModel();
		imageModel.setId(image.getId());
		imageModel.setTitle(image.getTitle());
		imageModel.setLink(Ultil.converBaseImageNameToLink(image.getImageName()));
		return imageModel;
	}

	@Override
	public List<Image> findAllBaseImage() {
		Session session  = sessionFactory.getCurrentSession();
		String hql = "FROM images";
		List<Image> images = new ArrayList<>();
		try {
			Query query = session.createQuery(hql);
			images = query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return images;
	}
	@Override
	public Image findById(int id) {
		StringBuilder hql = new StringBuilder("FROM images as img");
		hql.append(" WHERE img.id = :id");
		Image image = null;
		try {
			Session session = sessionFactory.getCurrentSession();
			Query query = session.createQuery(hql.toString());
			query.setParameter("id", id);
			image = (Image) query.getSingleResult();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return image;
	}
	@Override
	public Integer edit(Image image) {
		Session session = sessionFactory.getCurrentSession();
		try {
			session.update(image);
			return 1;
		} catch (Exception e) {
			LOGGER.error("Error has occured at edit() ", e);
			return 0;
		}
	}
	@Override
	public Integer delete(Integer id) {
		Session session = sessionFactory.getCurrentSession();
		try {
			Image image = session.find(Image.class, id);
			session.remove(image);
			return 1;
		} catch (Exception e) {
			LOGGER.error("Error has occured in delete() ", e);
			return 0;
		}
	}
}
