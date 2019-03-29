package com.actolap.wse.dao;

import java.util.Date;
import java.util.List;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;

import com.actolap.wse.model.States;
import com.actolap.wse.model.player.RestrictedIP;
import com.actolap.wse.model.player.MaxMindDatabase;
import com.actolap.wse.mongo.ConnectionFactory;

public class IPRestrictionDao {
	
	
	public static List<RestrictedIP> get() {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<RestrictedIP> query = datastore.createQuery(RestrictedIP.class).field("archive").equal(false);
		return query.asList();
	}
	
	public static List<MaxMindDatabase> getdataFromMaxmindByName(String stateName) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		//like query shoul be implemented
		Query<MaxMindDatabase> query = datastore.createQuery(MaxMindDatabase.class).field("archive").equal(false).field("cityName").equal(stateName);
		return query.asList();
	}
	
	public static List<MaxMindDatabase> getdataFromMaxmind() {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<MaxMindDatabase> query = datastore.createQuery(MaxMindDatabase.class).field("archive").equal(false);
		return query.asList();
	}
	
	public static void updateRestrictedIp(String state) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<RestrictedIP> query = datastore.createQuery(RestrictedIP.class).field("archive").equal(false).field("cityName").equal(state);
		UpdateOperations<RestrictedIP> update = datastore.createUpdateOperations(RestrictedIP.class).set("archive", true);
		UpdateResults updateResults = datastore.update(query, update, false, null);
		update.set("lastUpdate", new Date());
	}
	   
	public static void save(List<RestrictedIP> reqList) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		datastore.save(reqList);
	}
	
	
	public static void delete() {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<RestrictedIP> query = datastore.createQuery(RestrictedIP.class);
		query.getCollection().drop();
		
	}
	
	public static Key<MaxMindDatabase> deleteMaxmindDB() {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<MaxMindDatabase> query = datastore.createQuery(MaxMindDatabase.class);
		query.getCollection().drop();
		return query.getKey();
		
	}
	
	public static long maxmindDBCount() {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<MaxMindDatabase> query = datastore.createQuery(MaxMindDatabase.class);
		return query.getCollection().count();
		
	}
	
	public static void persist(RestrictedIP restrictedIP) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		datastore.save(restrictedIP);
	}
	
	public static void persistMaxMindDatabase(MaxMindDatabase maxMindDatabasetedIP) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		datastore.save(maxMindDatabasetedIP);
	}
	
	public static void deleteStates() {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<States> query = datastore.createQuery(States.class);
		query.getCollection().drop();
		
	}
	
	public static void persistState(States restrictedState) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		datastore.save(restrictedState);
	}
	
	public static States getRestrictedState() {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		Query<States> query = datastore.createQuery(States.class);
		return query.get();
	}
	
	public static Iterable<Key<MaxMindDatabase>> saveMaxmindDB(List<MaxMindDatabase> reqList) {
		Datastore datastore = ConnectionFactory.getInstance().getDatastore();
		return datastore.save(reqList);
	}
}

