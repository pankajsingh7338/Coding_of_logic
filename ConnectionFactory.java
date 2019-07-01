package com.actolap.wse.mongo;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.mongodb.morphia.AdvancedDatastore;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.actolap.config.Config;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoException;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoDatabase;

public class ConnectionFactory {

	private static final Logger LOG = LoggerFactory.getLogger(ConnectionFactory.class);

	private static ConnectionFactory connectionFactory = new ConnectionFactory();

	private MongoClient mongo;

	private Morphia morphia;

	private AggregateItemCodec aggregateItemCodec = new AggregateItemCodec();

	private TournamentAggregateItemCodec tournamentAggregateItemCodec = new TournamentAggregateItemCodec();

	private ConnectionFactory() {
		try {
			makeConnection();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	public static ConnectionFactory getInstance() {
		return connectionFactory;
	}

	// public boolean reconnect() {
	// boolean success = false;
	// try {
	// makeConnection();
	// success = true;
	// } catch (Exception e) {
	// success = false;
	// }
	// return success;
	// }

	public void makeConnection() throws UnknownHostException, MongoException {
		CodecRegistry codecRegistry = CodecRegistries.fromRegistries(MongoClient.getDefaultCodecRegistry(),
				CodecRegistries.fromCodecs(aggregateItemCodec),
				CodecRegistries.fromCodecs(tournamentAggregateItemCodec));
		if (Config.replication) {
			MongoClientOptions options = new MongoClientOptions.Builder().writeConcern(WriteConcern.SAFE)
					.codecRegistry(codecRegistry).connectionsPerHost(Config.cph)
					.readPreference(ReadPreference.secondaryPreferred()).build();
			List<ServerAddress> mongoinstances = new ArrayList<ServerAddress>();
			for (String host : Config.MONGOHOST) {
				mongoinstances.add(new ServerAddress(host, Config.port));
			}
			mongo = new MongoClient(mongoinstances, options);
		} else {
			MongoClientOptions options = new MongoClientOptions.Builder().writeConcern(WriteConcern.SAFE).build();
			for (String host : Config.MONGOHOST) {
				mongo = new MongoClient(new ServerAddress(host, Config.port), options);
				break;
			}
		}
		morphia = new Morphia();
		morphia.mapPackage("com.actolap.wse.model");
		morphia.mapPackage("com.actolap.wse.model.backoffice");
		morphia.mapPackage("com.actolap.wse.model.player");
		morphia.mapPackage("com.actolap.wse.model.game.poker");
		morphia.mapPackage("com.actolap.wse.model.affiliate");
		morphia.mapPackage("com.actolap.wse.model.promotion");
		morphia.mapPackage("com.actolap.wse.analytics.aggregate.model");
		morphia.mapPackage("com.actolap.wse.analytics.aggregate.elearning.model");
		getDatastore().ensureIndexes();
	}

	public Morphia getMorphiaInstance() {
		return morphia;
	}

	public Datastore getDatastore() {
		Datastore ds = null;
		ds = morphia.createDatastore(mongo, Config.DB_NAME);
		return ds;
	}

	public Datastore getDatastore(String dbName) {
		Datastore ds = null;
		ds = morphia.createDatastore(mongo, dbName);
		return ds;
	}

	public AdvancedDatastore getAdvanceDatastore(String dbName) {
		AdvancedDatastore ds = (AdvancedDatastore) morphia.createDatastore(mongo, dbName);
		return ds;
	}

	public AdvancedDatastore getAdvanceDatastore() {
		AdvancedDatastore ds = (AdvancedDatastore) morphia.createDatastore(mongo, Config.DB_NAME);
		return ds;
	}

	public MongoDatabase getMongoDatabase() { 
		return mongo.getDatabase(Config.DB_NAME);  
	} 
	  
	public MongoDatabase MongoDatabase(String dbName) { 
		return mongo.getDatabase(dbName); 
	}  
  
} 
  
  

