package com.actolap.wse.dao;

import java.util.List;
import java.util.regex.Pattern;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import com.actolap.wse.analytics.aggregate.model.AggregatePlayer;
import com.actolap.wse.commons.Utils;
import com.actolap.wse.mongo.ConnectionFactory; 

public class AggregatePlayerDao { 
	
	public static List<AggregatePlayer> getReportList(String mode, String startDate, String endDate) { 
		Datastore datastore = ConnectionFactory.getInstance().getDatastore(); 
		Query<AggregatePlayer> query = datastore.createQuery(AggregatePlayer.class); 
		if (Utils.isNotEmpty(mode)) { 
			if(mode.equals("HOLDEM")) 
			{
				if (startDate != null) 
					query.field("date").greaterThanOrEq(startDate); 
				if (endDate != null) 
					query.field("date").lessThanOrEq(endDate);  
				query.or(query.criteria("mode").equal(mode),
						query.criteria("mode").equal(null)); 
			}
			else {
				if (startDate != null) 
					query.field("date").greaterThanOrEq(startDate); 
				if (endDate != null) 
					query.field("date").lessThanOrEq(endDate); 
				Pattern pattern = Pattern.compile(mode, Pattern.CASE_INSENSITIVE);
				query.field("mode").equal(pattern); 
			} 
		}
		return query.asList(); 
	} 
} 



