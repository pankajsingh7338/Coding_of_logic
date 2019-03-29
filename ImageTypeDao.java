package com.actolap.wse.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import com.actolap.wse.commons.Utils;
import com.actolap.wse.model.ImageType;
import com.actolap.wse.model.game.poker.ListResponse;
import com.actolap.wse.mongo.ConnectionFactory;

public class ImageTypeDao {

	public static void persist(ImageType imageType) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		datastore.save(imageType);
	}

	public static ListResponse list(Integer limit, Integer skip, String query1, Integer width, Integer height,
			Boolean fetchTotal, String sortFiled, Boolean order) {
		ListResponse listResponse = new ListResponse();
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<ImageType> query = datastore.createQuery(ImageType.class);
		if (Utils.isNotEmpty(query1)) {
			Pattern pattern = Pattern.compile(query1, Pattern.CASE_INSENSITIVE);
			query.filter("type", pattern);
		}
		int limitVirtual = 0;
		if (limit != null)
			limitVirtual = limit + 1;
		if (width != null) {
			query.field("width").equal(width);
		}
		if (height != null) {
			query.field("height").equal(height);
		}
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
		List<ImageType> result = query.asList();
		if (result != null && !result.isEmpty())
			if (result.size() == limitVirtual) {
				result.remove(result.size() - 1);
				listResponse.setHm(true);
			}
		listResponse.getData().addAll(result);
		return listResponse;
	}

	public static List<ImageType> getAll() {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<ImageType> query = datastore.createQuery(ImageType.class);
		return query.asList();
	}

	public static ImageType getById(String id) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<ImageType> query = datastore.createQuery(ImageType.class).field("id").equal(id);
		return query.get();
	}

	public static void update(String id, Map<String, Object> updatedImageType) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<ImageType> query = datastore.createQuery(ImageType.class);
		query.field("id").equal(id);
		if (updatedImageType.size() > 0) {
			UpdateOperations<ImageType> ops = datastore.createUpdateOperations(ImageType.class);
			for (String key : updatedImageType.keySet()) {
				ops.set(key, updatedImageType.get(key));
			}
			ops.set("lastUpdate", new Date());
			datastore.update(query, ops, false);
		}
	}

	public static void delete(String id) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<ImageType> query = datastore.createQuery(ImageType.class).field("id").equal(id);
		datastore.delete(query);

	}
}

