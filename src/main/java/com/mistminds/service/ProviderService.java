package com.mistminds.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.stereotype.Service;

import com.hazelcast.com.eclipsesource.json.JsonObject;
import com.mistminds.config.PakkaApplicationSettingsConfiguration;
import com.mistminds.domain.Category;
import com.mistminds.domain.Consumer;
import com.mistminds.domain.ConsumerFeedback;
import com.mistminds.domain.Notification;
import com.mistminds.domain.PakkaApplicationSettings;
import com.mistminds.domain.Provider;
import com.mistminds.domain.util.Util;
import com.mistminds.repository.ConsumerFeedbackRepository;
import com.mistminds.repository.ConsumerRepository;
import com.mistminds.repository.NotificationRepository;
import com.mistminds.repository.PakkaApplicationSettingsRepository;
import com.mistminds.repository.ProviderRepository;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

@Service
public class ProviderService {
private final Logger log = LoggerFactory.getLogger(ConsumerService.class);
@Value("${spring.data.mongodb.database}")
private String dbName;
public static final int DEFAULT_PAGE_COUNT= 20;
	@Inject
    private ConsumerRepository consumerRepository;
	@Inject
    private ProviderRepository providerRepository;
	@Inject
    private NotificationRepository notificationRepository;
	@Inject
	private PakkaApplicationSettingsRepository pakkaApplicationSettingsRepository;
	@Inject
    private ConsumerFeedbackRepository consumerFeedbackRepository;
	@Inject
    private MongoDbFactory mongoDbFactory;
	private Provider dbProvider;
	
	public Provider createProviderInfo(Provider provider){
		Provider newProvider = new Provider();
	  Consumer dbConsumer = consumerRepository.findOne(provider.getId());
	PakkaApplicationSettings freeCreditValue = pakkaApplicationSettingsRepository.findByName(PakkaApplicationSettingsConfiguration.KEY_NAME_FREE_CREDITS);
	double freeCredits=(double)Double.parseDouble(freeCreditValue.getValue().toString());
	if(dbConsumer != null){
		newProvider.setConsumerId(provider.getId());
		newProvider.setName(provider.getName());
		newProvider.setMobile(provider.getMobile());
		newProvider.setEmail(provider.getEmail());
		newProvider.setMonthly_free_credits(freeCredits);
		newProvider.setWallet_credits(0);
		newProvider.setEleigible_for_promo_credit(false);
		newProvider.setAddress(provider.getAddress());
		if(provider.getLocation()!= null  && !provider.getLocation().isEmpty() && provider.getLocation().get(0) != null && provider.getLocation().get(1) != null)
		{
			newProvider.setLocation(provider.getLocation());
		}
		newProvider.setLastUpdate(ZonedDateTime.now());
		newProvider.setActive(true);
	}
	return newProvider;
	}
				
	public Provider updateProviderInfo(Provider provider){
	Consumer dbConsumer = consumerRepository.findOne(provider.getId());
	dbProvider = providerRepository.findOneByConsumerId(provider.getId());
		if(dbConsumer != null && dbProvider != null){
				if(provider.getName() != null && !provider.getName().isEmpty()){
					dbProvider.setName(provider.getName());
				}if(provider.getMobile() != null && !provider.getMobile().isEmpty()){
					dbProvider.setMobile(provider.getMobile());
				}if(provider.getEmail() != null && !provider.getEmail().isEmpty()){
					dbProvider.setEmail(provider.getEmail());
				}
				if(provider.getAddress() != null && !provider.getAddress().isEmpty()){
					dbProvider.setAddress(provider.getAddress());
				}if(provider.getLocation()!= null  && !provider.getLocation().isEmpty() && provider.getLocation().get(0) != null && provider.getLocation().get(1) != null)
				{
					dbProvider.setLocation(provider.getLocation());
				}
				dbProvider.setLastUpdate(ZonedDateTime.now());
				if((Util.checkOffensive(provider.getName())).equals("true")){
					dbProvider.setActive(false);
				}
				
			
	}	
		return dbProvider;
       }

	public List<JsonObject> getCreditDetails(String id) {
		List<JsonObject> provider = new ArrayList<JsonObject>();
		JsonObject jsonObject = new JsonObject();
		try{
		if (id != null && !id.isEmpty()){
			dbProvider = providerRepository.findOneByConsumerId(id);
			PakkaApplicationSettings pas1=pakkaApplicationSettingsRepository.findByName(PakkaApplicationSettingsConfiguration.KEY_NAME_PRICE_MAPING);
			PakkaApplicationSettings pas2=pakkaApplicationSettingsRepository.findByName(PakkaApplicationSettingsConfiguration.KEY_NAME_HOME_BANNER_PRICE);
			PakkaApplicationSettings pas3=pakkaApplicationSettingsRepository.findByName(PakkaApplicationSettingsConfiguration.KEY_NAME_CATEGORY_BANNER_PRICE);
			String price_mapping =pas1.getValue().toString();
			String HomeBannerPricing=pas2.getValue().toString();
			String CategoryBannerPricing=pas3.getValue().toString();
			if(dbProvider!=null){
				jsonObject.set("free_credit",dbProvider.getMonthly_free_credits());
				jsonObject.set("wallet_credit",dbProvider.getWallet_credits());
				jsonObject.set("Price_mapping",price_mapping);
				jsonObject.set("HomeBannerPrice_mapping",HomeBannerPricing);
				jsonObject.set("CategoryBannerPrice_mapping",CategoryBannerPricing);
				jsonObject.set("provider_status",dbProvider.isActive());
				provider.add(jsonObject);			
		}
			
		}
		}catch(NullPointerException npx){
			log.error(npx.toString());
			return null;
		}
		return provider;
	}

	public List<DBObject> getFeedBackComment(Consumer consumer) throws ParseException, JSONException {
		List<String> notificationId= new ArrayList<String>();
        Integer pageCount = DEFAULT_PAGE_COUNT;
        ZonedDateTime lastRecordDate = consumer.getLastRecordDate();
        BasicDBObject query = null;
        int limitCount=0;
        List<DBObject> commentFeedback = new ArrayList<DBObject>();
        if ((consumer.getLastRecordDate() != null)) {
    		lastRecordDate = consumer.getLastRecordDate();
    	}
        Provider provider = providerRepository.findOneByConsumerId(consumer.getId());
        if(provider!=null){
      
        
        List<Notification> allnotification= notificationRepository.findByConsumerId(provider.getConsumerId());
             for (Notification notification :allnotification) {
            	 notificationId.add(notification.getId());
            }
        }
        do {
    		
    		BasicDBObject notificationIdVal = new BasicDBObject();
    		BasicDBObject comment = new BasicDBObject();
    		comment.append("$ne",null);
    		if(notificationId != null){
    			notificationIdVal.append("$in", notificationId);
    		}
    		if (lastRecordDate != null) {
    			String date = lastRecordDate.toString().replace("[GMT]", "");
    			DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    			Date recordedDate = format.parse(date);

    			BasicDBObject feedbackDate = new BasicDBObject();
    			feedbackDate.append("$lt", recordedDate);
    				query = new BasicDBObject("notification_id", notificationIdVal)
    						.append("created", feedbackDate).append("comment", comment);
    			
    		} else {
    			query = new BasicDBObject("notification_id", notificationIdVal).append("comment", comment);
    		}
    		DBCollection collection=mongoDbFactory.getDb(dbName).getCollection("consumer_feedback");
    		log.info("limit applying for query to fetch notifications: "+(pageCount-limitCount));
    		DBCursor dbcursor = collection.find(query)
    				.limit(pageCount-limitCount).sort(new BasicDBObject("created", -1));
    		log.info("Total records !!!!!!!!!!!" + dbcursor.count());
    		log.info("Inside loop query !!!!!!!!!!!!:" + query);
    		commentFeedback.addAll(dbcursor.toArray());
    		log.info("############# Notification Size ############" + commentFeedback.size());
    		limitCount=commentFeedback.size();
    		
    	} while ((commentFeedback.size() < pageCount) );
        if(commentFeedback.isEmpty()){
    		log.info("NO notifications available to return. Size of Notifications:=" +commentFeedback.size());
    		return null;}
    	return commentFeedback;
		
		
	}

	
	}