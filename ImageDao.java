package com.actolap.wse.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import com.actolap.wse.commons.Utils;
import com.actolap.wse.model.EntityType;
import com.actolap.wse.model.Image;
import com.actolap.wse.model.Image.ImageStatus;
import com.actolap.wse.model.game.poker.ListResponse;
import com.actolap.wse.mongo.ConnectionFactory;

public class ImageDao {

	public static void persist(Image image) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		datastore.save(image);
	}

	public static ListResponse list(Integer limit, Integer skip, Boolean fetchTotal, String sortFiled, Boolean order,
			String imageType, EntityType entityType, ImageStatus status) {
		ListResponse listResponse = new ListResponse();
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Image> query = datastore.createQuery(Image.class);
		int limitVirtual = 0;
		if (limit != null)
			limitVirtual = limit + 1;
		if (Utils.isNotEmpty(imageType)) {
			query.filter("type", imageType);
		}
		if (status != null) {
			query.filter("status", status);
		}
		if (entityType != null) {
			query.filter("entityType", entityType);
		}
		query.limit(limitVirtual);
		if (skip != null) {
			query.offset(skip);
		}
		if (fetchTotal != null && fetchTotal) {
			listResponse.setTotal(query.countAll());
		}
		if (Utils.isNotEmpty(sortFiled)) {
			if (order != null && order) {
				query.order(sortFiled);
			} else {
				query.order("-" + sortFiled);
			}
		}
		List<Image> result = query.asList();
		if (result != null && !result.isEmpty())
			if (result.size() == limitVirtual) {
				result.remove(result.size() - 1);
				listResponse.setHm(true);
			}
		listResponse.getData().addAll(result);
		return listResponse;
	}

	public static Image getById(String id) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Image> query = datastore.createQuery(Image.class).field("id").equal(id);
		return query.get();
	}

	public static void update(String id, Map<String, Object> updatedImageType) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Image> query = datastore.createQuery(Image.class);
		query.field("id").equal(id);
		if (updatedImageType.size() > 0) {
			UpdateOperations<Image> ops = datastore.createUpdateOperations(Image.class);
			for (String key : updatedImageType.keySet()) {
				ops.set(key, updatedImageType.get(key));
			}
			ops.set("lastUpdate", new Date());
			datastore.update(query, ops, false);
		}
	}

	public static void delete(String id) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Image> query = datastore.createQuery(Image.class).field("id").equal(id);
		datastore.delete(query);
	}

	public static List<Image> getImageForFe(String type) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Image> query = datastore.createQuery(Image.class).field("status").equal(ImageStatus.ENABLE);
		if (type != null) {
			query.field("type").equal(type);
		}
		return query.asList();
	}
}

