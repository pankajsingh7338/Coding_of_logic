package com.actolap.wse.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import com.actolap.wse.commons.Utils;
import com.actolap.wse.model.Blog;
import com.actolap.wse.model.Blog.BlogStatus;
import com.actolap.wse.model.Common;
import com.actolap.wse.model.Topic.TopicStatus;
import com.actolap.wse.mongo.ConnectionFactory;

public class BlogDao {

	public static void persist(Blog blog) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		datastore.save(blog);
	}
	
	public static void persistCommon(Common common) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		datastore.save(common);
	}
	
	public static void updateLikeId(String id, String uniqueId) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Common> query = datastore.createQuery(Common.class);
		UpdateOperations<Common> ops = datastore.createUpdateOperations(Common.class);
		query.field("id").equal(id);
		ops.set("likesId", uniqueId);
		datastore.update(query, ops);
		}
	
	
	public static void updateCommentId(String id, String uniqueId) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Common> query = datastore.createQuery(Common.class);
		UpdateOperations<Common> ops = datastore.createUpdateOperations(Common.class);
		query.field("id").equal(id);
		ops.set("commentId", uniqueId);
		datastore.update(query, ops);
		}
	
	
	public static Common getCommon() {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Common> query = datastore.createQuery(Common.class);
		return query.get();
	}


	public static Blog getById(String id) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Blog> query = datastore.createQuery(Blog.class).field("id").equal(id);
		return query.get();
	}

	public static Blog getBySlug(String slug, BlogStatus status) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Blog> query = datastore.createQuery(Blog.class);
		query.order("order");
		if (status != null) {
			query.field("status").equal(status);
		}
		if (Utils.isNotEmpty(slug)) {
			query.field("slug").equal(slug);
		}
		return query.get();
	}

	public static Blog getByUserId(String id, String userId) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Blog> query = datastore.createQuery(Blog.class).field("id").equal(id).field("userId").equal(userId);
		return query.get();
	}

	public static Blog getByTopicId(String topicId) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Blog> query = datastore.createQuery(Blog.class).field("topicId").equal(topicId);
		return query.get();
	}

	public static Blog getLatestTriviaBlog() {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Blog> query = datastore.createQuery(Blog.class).field("status").equal(BlogStatus.ENABLE).field("topicTitle").equal("Trivia").field("trivia").equal(true).field("pokerMantra").equal(false).order("-lastUpdate");
		return query.get();
	}

	public static Blog getLatestTipsBlog() {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Blog> query = datastore.createQuery(Blog.class).field("status").equal(BlogStatus.ENABLE).field("topicTitle").equal("Tips").field("trivia").equal(true).field("pokerMantra").equal(false).order("-lastUpdate");
		return query.get();
	}

	public static boolean canDisable(String topicId) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Blog> query = datastore.createQuery(Blog.class);
		query.field("topicId").equal(topicId);
		query.field("status").equal(TopicStatus.ENABLE);
		if (query.asList().isEmpty())
			return true;
		else
			return false;
	}

	public static List<Blog> list(String query1, BlogStatus status, String topicId, Boolean trivia) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Blog> query = datastore.createQuery(Blog.class);
		if (Utils.isNotEmpty(query1)) {
			Pattern pattern = Pattern.compile(query1, Pattern.CASE_INSENSITIVE);
			query.filter("title", pattern);
		}
		if (status != null) {
			query.field("status").equal(status);
		}
		if (Utils.isNotEmpty(topicId)) {
			query.field("topicId").equal(topicId);
		}
		if (trivia != null) {
			query.field("trivia").equal(trivia);
		}
		return query.asList();
	}

	public static List<Blog> listByUserId(String userId, BlogStatus status, List<String> blogIds) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Blog> query = datastore.createQuery(Blog.class);
		if (Utils.isNotEmpty(userId)) {
			query.field("userId").equal(userId);
		}
		if (status != null) {
			query.field("status").equal(status);
		}
		if (blogIds != null && !blogIds.isEmpty())
			query.field("id").notIn(blogIds);
		return query.asList();
	}

	public static List<Blog> listByIds(String query1, BlogStatus status, String topicId, List<String> blogIds) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Blog> query = datastore.createQuery(Blog.class);
		query.order("order");
		if (Utils.isNotEmpty(query1)) {
			Pattern pattern = Pattern.compile(query1, Pattern.CASE_INSENSITIVE);
			query.or(query.criteria("title").equal(pattern), query.criteria("shortContent").equal(pattern));
		}
		if (status != null) {
			query.field("status").equal(status);
		}
		if (Utils.isNotEmpty(topicId)) {
			query.field("topicId").equal(topicId);
		}
		if (blogIds != null && !blogIds.isEmpty())
			query.field("id").in(blogIds);
		return query.asList();
	}
	
	
	public static void update(String id, Map<String, Object> mongoUpdate) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Blog> query = datastore.createQuery(Blog.class);
		query.field("id").equal(id);
		if (mongoUpdate.size() > 0) {
			UpdateOperations<Blog> ops = datastore.createUpdateOperations(Blog.class);
			for (String key : mongoUpdate.keySet()) {
				ops.set(key, mongoUpdate.get(key));
			}
			ops.set("lastUpdate", new Date());
			datastore.update(query, ops);
		}
	}

	public static void updateOrder(String id, int order) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Blog> query = datastore.createQuery(Blog.class);
		query.field("id").equal(id);
		UpdateOperations<Blog> ops = datastore.createUpdateOperations(Blog.class);
		ops.set("order", order);
		ops.set("lastUpdate", new Date());
		datastore.update(query, ops);

	}

	public static void delete(String id) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Blog> query = datastore.createQuery(Blog.class).field("id").equal(id);
		datastore.delete(query);

	}

	public static void moveDown(String id, int order) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		int size = datastore.createQuery(Blog.class).field("order").exists().asList().size();
		if (order < size) {
			Query<Blog> query1 = datastore.createQuery(Blog.class).field("order").equal(order + 1);
			UpdateOperations<Blog> ops1 = datastore.createUpdateOperations(Blog.class);
			ops1.set("order", order);
			datastore.update(query1, ops1);
			Query<Blog> query = datastore.createQuery(Blog.class).field("id").equal(id).field("order").equal(order);
			UpdateOperations<Blog> ops = datastore.createUpdateOperations(Blog.class);
			ops.set("order", order + 1);
			datastore.update(query, ops);
		}

	}

	public static void moveUp(String id, int order) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		if (order > 1) {
			Query<Blog> query1 = datastore.createQuery(Blog.class).field("order").equal(order - 1);
			UpdateOperations<Blog> ops1 = datastore.createUpdateOperations(Blog.class);
			ops1.set("order", order);
			datastore.update(query1, ops1);
			Query<Blog> query = datastore.createQuery(Blog.class).field("id").equal(id).field("order").equal(order);
			UpdateOperations<Blog> ops = datastore.createUpdateOperations(Blog.class);
			ops.set("order", order - 1);
			datastore.update(query, ops);
		}

	}

	public static List<Blog> getChronologicalBlogs(String id, BlogStatus status) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Blog> query = datastore.createQuery(Blog.class);
		query.order("-lastUpdate");
		query.field("id").notEqual(id);
		if (status != null) {
			query.field("status").equal(status);
		}
		return query.asList();
	}
	
	public static List<Blog> getTopicsBlogs(String id, BlogStatus status, String topicTitle) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Blog> query = datastore.createQuery(Blog.class);
		query.field("topicTitle").equal(topicTitle);	
		query.field("id").notEqual(id);
		query.field("trivia").equal(true);
		if (status != null) {
			query.field("status").equal(status);
		}
		return query.asList();
	}
		
	
	public static void updateComments(String id, Map<String, Object> mongoUpdate) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<Blog> query = datastore.createQuery(Blog.class);
		query.field("id").equal(id);
		if (mongoUpdate.size() > 0) {
			UpdateOperations<Blog> ops = datastore.createUpdateOperations(Blog.class);
			for (String key : mongoUpdate.keySet()) {
				ops.set(key, mongoUpdate.get(key));
			}
			ops.set("lastUpdate", new Date());
			datastore.update(query, ops);
		}
	}

}

