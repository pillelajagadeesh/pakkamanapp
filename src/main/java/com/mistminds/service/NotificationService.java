package com.mistminds.service;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.hazelcast.com.eclipsesource.json.JsonArray;
import com.hazelcast.com.eclipsesource.json.JsonObject;
import com.mistminds.domain.Category;
import com.mistminds.domain.Consumer;
import com.mistminds.domain.ConsumerFavourite;
import com.mistminds.domain.ConsumerFeedback;
import com.mistminds.domain.ContentMetadata;
import com.mistminds.domain.Notification;
import com.mistminds.domain.NotificationAcknowledgement;
import com.mistminds.domain.Provider;
import com.mistminds.domain.util.Util;
import com.mistminds.repository.CategoryRepository;
import com.mistminds.repository.ConsumerFavouriteRepository;
import com.mistminds.repository.ConsumerFeedbackRepository;
import com.mistminds.repository.ConsumerRepository;
import com.mistminds.repository.ContentMetadataRepository;
import com.mistminds.repository.NotificationAcknowledgementRepository;
import com.mistminds.repository.NotificationRepository;
import com.mistminds.repository.ProviderRepository;
import com.mistminds.web.rest.util.NotificationResult;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

@Service
public class NotificationService {
	@Value("${spring.data.mongodb.database}")
	private String dbName;
	public static final int DEFAULT_PAGE_COUNT= 20;
	public static final Double DEFAULT_MIN_RADIUS= 0.00;
	public static final Double DEFAULT_MAX_RADIUS= 25000.00;
	public static final Double DEFAULT_RANGE = 5000.00;
	public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";


	private final Logger log = LoggerFactory.getLogger(NotificationService.class);
    
	@Inject
	private ConsumerRepository consumerRepository;
	@Inject
    private MongoDbFactory mongoDbFactory;
	@Inject
    private MongoTemplate mongoTemplate;
	@Inject
	private SNSMobilePush snsMobilePush;
	
	@Inject
	private ProviderRepository providerRepository;

	@Inject
	private ContentMetadataService contentMetadataService;
	
	@Inject
	private CategoryRepository categoryRepository;
	
	@Inject
	private ContentMetadataRepository contentMetadataRepository;

	@Inject
	private NotificationRepository notificationRepository;

	@Inject
	private ConsumerFavouriteRepository consumerFavouriteRepository;

	@Inject
	private ConsumerFeedbackRepository consumerFeedbackRepository;

	@Inject
	private ConsumerService consumerService;
	
	@Inject
	private NotificationAcknowledgementRepository notificationAcknowledgementRepository;

	@Inject
	private NotificationAcknowledgementService notificationAcknowledgementService;

	private Category dbCategory;
	private Provider dbProvider;

	public Notification createNotification(Notification notification) throws IOException {
         String content;
		log.debug("Inside NotificationService for managing notification for image");
		if (notification == null || notification.getConsumerId() == null) {
			return null;
		}
			dbProvider = providerRepository.findOneByConsumerId(notification.getConsumerId());
			if (dbProvider == null || !dbProvider.isActive()) {
				return null;
			}
		notification.setActive(false);
		String title = notification.getTitle();
		String describ = notification.getDescription();
		content = title + "" + describ;
		notification.setOffensive(Util.checkOffensive(content));
		String[] arr = null;
		String[] imageArr = null;
		ArrayList<String> arrList = new ArrayList<String>();
		if (notification.getImage() != null)
		{
			String webImage = notification.getImage();
			arr = webImage.split(",");
			log.info("Number of image urls from admin panel :" + arr.length);
			for (int i = 0; i < arr.length; i++) {
				URL url = new URL(arr[i]);
				InputStream is = url.openStream();
				byte[] bytes = IOUtils.toByteArray(is);
				String imageDataString = encodeImage(bytes);
				arrList.add(imageDataString);
			}
			Object[] convertStringArray = arrList.toArray();
			imageArr = Arrays.copyOf(convertStringArray, convertStringArray.length, String[].class);
			notification.setImages(imageArr);
		}
				if (notification.getImages() !=null && !(notification.getImages().length > 0) && notification.getImage() ==null) {
					dbCategory = categoryRepository.findOne(notification.getCategoryId());
					if (dbCategory != null) {
						String[] defaultImage={dbCategory.getImageUrl()};
						notification.setImageUrls(defaultImage);
						return notification;
				} 
				}
				else if(notification.getImages() ==null && notification.getImage() ==null)
				{
					dbCategory = categoryRepository.findOne(notification.getCategoryId());
					if (dbCategory != null) {
						String[] defaultImage={dbCategory.getImageUrl()};
						notification.setImageUrls(defaultImage);
						return notification;
				}
				}
				int length = notification.getImages().length;
				List<String> list = new ArrayList<String>();
				String [] multipleImages = null;
				
				for (int i = 0; i < length; i++) {
					Map<String, Object> uploadResult = contentMetadataService.cloudanaryUploadImage(notification.getImages()[i]);
					ContentMetadata cloudinay = consumerService.uploadPhoto(uploadResult, notification.getConsumerId());
					if (cloudinay != null) {
						ContentMetadata result = contentMetadataRepository.save(cloudinay);
						if (result != null) {
							list.add(cloudinay.getUrl());
							multipleImages = list.toArray(new String[list.size()]);
						}
					}
				}

				if (length > 0) {
					notification.setImageUrls(multipleImages);
					notification.setImages(new String[0]);
				}
				return notification;
	}

	@SuppressWarnings("unused")
	@Async
	public void sendNotification(String notificationId) {
		ZonedDateTime now = ZonedDateTime.now();
		Double latitude, longitude;
		String url;
		String normalUser="normalUser";
		boolean flag = true;
		Notification dbNotification = notificationRepository.findOneByIdAndValidToAfter(notificationId,now.minusDays(1));
		if (dbNotification != null) {
			url=dbNotification.getImageUrls()[0];
			Consumer dbConsumer = consumerRepository.findOne(dbNotification.getConsumerId());
			Provider dbProvider=	providerRepository.findOneByConsumerId(dbNotification.getConsumerId());
			
			if (dbProvider != null && dbProvider.isActive()) {
				if (dbProvider != null) {
					latitude = dbProvider.getLocation().get(0);
					longitude = dbProvider.getLocation().get(1);
				} else {
					List<Double> location = dbConsumer.getLocation();
					latitude = location.get(0);
					longitude = location.get(1);
				}

				for (ConsumerFavourite consumerFavourite : consumerFavouriteRepository.findByproviderId(dbConsumer.getId())) {
					if (notificationAcknowledgementService.notificationAcknowledgment(notificationId,consumerFavourite.getConsumerId())) {
						Consumer consumer = consumerRepository.findOne(consumerFavourite.getConsumerId());
						if(consumer!=null){
						pushNotification(dbNotification, notificationId,consumer.getDevice_info().getDeviceId(), url, dbProvider.getName(),dbProvider.getImageUrl(),normalUser);
						}
					}
				}
				Point center = new Point(latitude, longitude);
				Double radius=Double.parseDouble(dbNotification.getRadius())/1000;
				Distance distance = new Distance(Math.toDegrees(radius/6378.137));
				Circle circle = new Circle(center,distance);	
				List<Consumer> consumers = consumerRepository.findByLocationWithin(circle);
				for (Consumer consumer : consumers) {
					flag = true;
					log.info("consumer data within location =================================================================================="+consumer);
					if (notificationAcknowledgementService.notificationAcknowledgment(notificationId,consumer.getId())) {
						
						if(consumer.getUnsubscribeCategory() != null){
						for (Category category : consumer.getUnsubscribeCategory()) {
							String userCategory = category.getId();
							if (userCategory.equals(dbNotification.getCategoryId())) {

								flag = false;
							} 
							}
						}
						if (flag && consumer.getDevice_info() != null && dbNotification.getRadius()!=null && !dbNotification.getRadius().equalsIgnoreCase("0")) {
							
							pushNotification(dbNotification, notificationId, consumer.getDevice_info().getDeviceId(), url, dbProvider.getName(),dbProvider.getImageUrl(),normalUser);
						}
					}
				}

			}
			
		}
		return;
	}
    
	@Async
	public void pushNotification(Notification notificationContent,
			String notificationId, String installationId, String imagUrl, String senderName, String providerImageUrl,String approval) {
		try {
			Map<String, String>pushData = new HashMap<String, String>();
			if(approval.equals("adminPaid")){
				pushData.put("adminNotification", "paidNotification");
			}
			else if(approval.equals("adminUnPaid")){
				pushData.put("adminNotification", "unPaidNotification");
			}
			else{
				pushData.put("adminNotification", "userNotification");
			}
			if(notificationContent.isHomeBannerStatus()){
				pushData.put("homeBanner", "homeBanner");
			}
			else{
				pushData.put("homeBanner", "noHomeBanner");
			}
			if(notificationContent.isCategoryBannerStatus()){
				pushData.put("categoryBanner", "categoryBanner");
			}
			else{
				pushData.put("categoryBanner", "noCategoryBanner");
			}
			pushData.put("installationId", installationId);
			pushData.put("message",notificationContent.getDescription());
			pushData.put("title", notificationContent.getTitle());
			pushData.put("notification_id", notificationId);
			pushData.put("category", notificationContent.getCategoryId());
			pushData.put("imageUrl", imagUrl);
			pushData.put("providerImageUrl", providerImageUrl);
			pushData.put("sender_id", null);
			pushData.put("sender_name", senderName);
			pushData.put("mobile", null);
			if (notificationContent.getValidFrom() != null) {
				pushData.put("validFrom", notificationContent.getValidFrom().toString());
			}
			if (notificationContent.getValidTo() != null) {
				pushData.put("validTo", notificationContent.getValidTo().toString());
			}
			snsMobilePush.pushNotification(pushData);
		} catch (Exception ex) {
			log.error("There is an exception while pushNotification " + ex.toString(), ex);
		}
	}
	
	
	/*public void adminPushNotification(Notification notificationContent,
			String notificationId, String installationId, String imagUrl, String senderName, String providerImageUrl) {
		try {
			Map<String, String>pushData = new HashMap<String, String>();
			if(notificationContent.getWalletCreditsUsed()>0){
				pushData.put("adminNotification", "paidNotification");
			}
			else{
				pushData.put("adminNotification", "unPaidNotification");
			}
			pushData.put("status","UnApproved");
			pushData.put("user", "Admin");
			pushData.put("installationId", installationId);
			pushData.put("message",notificationContent.getDescription());
			pushData.put("title", notificationContent.getTitle());
			pushData.put("notification_id", notificationId);
			pushData.put("category", notificationContent.getCategoryId());
			pushData.put("imageUrl", imagUrl);
			pushData.put("providerImageUrl", providerImageUrl);
			pushData.put("sender_id", null);
			pushData.put("sender_name", senderName);
			pushData.put("mobile", null);
			if (notificationContent.getValidFrom() != null) {
				pushData.put("validFrom", notificationContent.getValidFrom().toString());
			}
			if (notificationContent.getValidTo() != null) {
				pushData.put("validTo", notificationContent.getValidTo().toString());
			}
			snsMobilePush.pushNotification(pushData);
		} catch (Exception ex) {
			log.error("There is an exception while pushNotification " + ex.toString(), ex);
		}
	}*/
	
	
	
	
	
	public List<Notification> getconsumerNotificationByConsumerId(String consumerId) {
		List<Notification> allConsumerNotification = new ArrayList<Notification>();
		dbProvider = providerRepository.findOneByConsumerId(consumerId);
		if (dbProvider == null) {
			return null;
		}
		List<Notification> consumerNotification=notificationRepository.findByConsumerId(consumerId);
		if(consumerNotification.isEmpty()){
			return null;
		}

		for (Notification notification : consumerNotification) {
			allConsumerNotification.add(notification);
		}
		return allConsumerNotification;

	}

	public List<JsonObject> getAllNotificationByConsumerId(String consumerId ,String lastRecordDate) throws ParseException, JSONException {
		int like = 0, dislike = 0, share = 0;
		double rating;
		String userStausLikeOrDislike = null, senderMobileNumber = null, favoriteStatus = null;
		List<String> comments = new ArrayList<String>();
		Integer pageCount = DEFAULT_PAGE_COUNT;
		BasicDBObject query = null;
	    int limitCount=0;
	    List<Notification> returnList = new ArrayList<Notification>();
	    List<DBObject> providerNotification = new ArrayList<DBObject>();
		List<JsonObject> nots = new ArrayList<JsonObject>();
		List<JsonObject> allConsumerNotification = new ArrayList<JsonObject>();
		Consumer dbConsumer = consumerRepository.findOne(consumerId);
		Provider dbProvider=providerRepository.findOneByConsumerId(consumerId);
		BasicDBObject validToDate= new BasicDBObject( "$gte", currentFormatedDate());
		if (dbConsumer == null || dbProvider ==null) {
			return null;
		}
		 
	    		if(consumerId != null){
	    		if (StringUtils.isBlank(lastRecordDate)&& StringUtils.isEmpty(lastRecordDate)) {
	    			query = new BasicDBObject("consumer_id", consumerId).append("valid_to", validToDate);
	    		} else {
	    			String date = lastRecordDate.toString().replace("[GMT]", "");
	    			DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
	    			Date recordedDate = format.parse(date);

	    			BasicDBObject feedbackDate = new BasicDBObject();
	    			feedbackDate.append("$lt", recordedDate);
	    				query = new BasicDBObject("consumer_id", consumerId).append("valid_to", validToDate)
	    						.append("notification_date", feedbackDate);
	    		}
	    		}
	    		DBCollection collection=mongoDbFactory.getDb(dbName).getCollection("notification");
	    		log.info("limit applying for query to fetch notifications: "+(pageCount-limitCount));
	    		DBCursor  dbcursor = collection.find(query)
	    				.limit(pageCount-limitCount).sort(new BasicDBObject("notification_date", -1));
	    		log.info("Total records !!!!!!!!!!!" + dbcursor.size());
	    		log.info("Inside loop query !!!!!!!!!!!!:" + query);
	    		providerNotification.addAll( dbcursor.toArray());
	    		log.info("############# Notification Size ############" + providerNotification.size());
	    		limitCount=providerNotification.size();
	    		 if(providerNotification.isEmpty()){
	 	    		log.info("NO notifications available to return. Size of Notifications:=" +providerNotification.size());
	 	    		return null;}
	    	
		     
		       String lastDateVal = null;
		       JSONObject notifyJson=new JSONObject(providerNotification.get(providerNotification.size()-1).toString());
			   JSONObject jsonObject1 = (JSONObject) notifyJson.get("notification_date");
			   lastDateVal = (String) jsonObject1.get("$date");
			   ZonedDateTime zdt4 = ZonedDateTime.parse(lastDateVal);
			   ListIterator<DBObject> litr=providerNotification.listIterator();
	    		 while (litr.hasNext()) {
	 			    DBObject obj = litr.next();
	 			   Notification notification = mongoTemplate.getConverter().read(Notification.class, obj); 
	 			    returnList.add(notification);
	 			}
			for (Notification notification :returnList) {	
				JsonObject jsonObject = parseNotificationToJsonObject(notification);
				like = 0; dislike = 0; share = 0;
				for (ConsumerFeedback consumerFeedback : consumerFeedbackRepository.findBynotificationId(notification.getId())) {
					if (consumerFeedback.getLikeDislike() != null&& "1".equals(consumerFeedback.getLikeDislike())) {
						like++;
					} else if (consumerFeedback.getLikeDislike() != null && "0".equals(consumerFeedback.getLikeDislike())) {
						dislike++;
					} else if (consumerFeedback.getComment() != null && consumerFeedback.getComment() != "") {
						Consumer consumer = consumerRepository.findOne(consumerFeedback.getConsumerId());
						if(consumer!=null){
							senderMobileNumber = consumer.getMobile().substring(consumer.getMobile().length() - 3,consumer.getMobile().length());
							comments.add(consumerFeedback.getComment() + "-----" + "*******" + senderMobileNumber);
						}
					}
					if (consumerFeedback.getShare() != null	&& consumerFeedback.getShare().equalsIgnoreCase("true")) {
						share = share + consumerFeedback.getCount();
					}
				}
				jsonObject.set("like", like);
				jsonObject.set("dislike", dislike);
				if (comments != null && !comments.isEmpty()) {
					JsonArray commentArray = new JsonArray();
					for (String comment : comments) {
						commentArray.add(comment);
					}
				jsonObject.set("comments", commentArray);
				}
				jsonObject.set("share", share);
				if(userStausLikeOrDislike == null){
					jsonObject.set("userStausLikeOrDislike", "nolikedislike");
				}else{
					jsonObject.set("userStausLikeOrDislike", userStausLikeOrDislike);
				}
				if(favoriteStatus == null){
					jsonObject.set("favoriteStaus", "false");
				}else{
					jsonObject.set("favoriteStaus", favoriteStatus);
				}
				nots.add(jsonObject);
			}
			
			rating = generateRating(consumerId);
			for (JsonObject jsonObject : nots) {
				jsonObject.set("rating", rating);
				jsonObject.set("lastRecordDate",zdt4.toString());
				allConsumerNotification.add(jsonObject);
			}
		return allConsumerNotification;

	}
	public JsonObject getNotificationDetails(String consumerId, String notificationId) {

		int like = 0, dislike = 0, share = 0, readCount =0;
		double rating;
		JsonObject jsonObject = null;
		String userStausLikeOrDislike = null, senderMobileNumber = null, favoriteStatus = null;
		List<String> comments = new ArrayList<String>();

		Consumer dbConsumer = consumerRepository.findOne(consumerId);
		
		if (dbConsumer == null) {
			return null;
		}
			Notification  dbNotification = notificationRepository.findOne(notificationId); 	
			if (dbNotification == null) {
				return null;
			}
			NotificationAcknowledgement dbNotificationAcknowledgement;
			if(!notificationAcknowledgementService.notificationAcknowledgment(notificationId, consumerId)){
				dbNotificationAcknowledgement = notificationAcknowledgementRepository.findByConsumerIdAndNotificationId(consumerId, notificationId);
				if(dbNotificationAcknowledgement != null && dbNotificationAcknowledgement.getRead()==null){
					dbNotificationAcknowledgement.setRead(ZonedDateTime.now());
					notificationAcknowledgementRepository.save(dbNotificationAcknowledgement);
			}
			}
				jsonObject = parseNotificationToJsonObject(dbNotification);
			Provider provider = providerRepository.findOneByConsumerId(dbNotification.getConsumerId());
				/*Provider provider = dbProvider.getProvider();*/
				if( provider != null){
					if( dbNotification.getCallStatus()!=true){
						provider.setMobile(null);
					}if(dbNotification.getShowLocation()!=true){
						provider.setAddress(null);	
					}
					jsonObject.set("provider_info", consumerService.parseProviderToJsonObject(provider,dbNotification));
				}
				ConsumerFavourite consumerFavourite = consumerFavouriteRepository.findByConsumerId(consumerId);
				if(consumerFavourite != null && consumerFavourite.getProviderId().contains(dbNotification.getConsumerId())){
					favoriteStatus = "true";
				}
				for (ConsumerFeedback consumerFeedback : consumerFeedbackRepository.findBynotificationId(notificationId)) {
					if (consumerFeedback.getLikeDislike() != null && "1".equals(consumerFeedback.getLikeDislike())) {
						if(consumerFeedback.getConsumerId().equalsIgnoreCase(consumerId)){
							userStausLikeOrDislike = "true";
						}
						like++;
					} else if (consumerFeedback.getLikeDislike() != null && "0".equals(consumerFeedback.getLikeDislike())) {
						if(consumerFeedback.getConsumerId().equalsIgnoreCase(consumerId)){
							userStausLikeOrDislike = "false";
						}
						dislike++;
					} else if (consumerFeedback.getComment() != null && consumerFeedback.getComment() != "") {
						Consumer consumer = consumerRepository.findOne(consumerFeedback.getConsumerId());
						if(consumer!=null){
						senderMobileNumber = consumer.getMobile().substring(consumer.getMobile().length() - 3,consumer.getMobile().length());
						
						comments.add(consumerFeedback.getComment() + "-----" + "*******" + senderMobileNumber);
						}
					}

					if (consumerFeedback.getShare() != null	&& consumerFeedback.getShare().equalsIgnoreCase("true")) {
						share = share + consumerFeedback.getCount();
					}
				}
				rating = generateRating(dbNotification.getConsumerId());
				
				
				List<NotificationAcknowledgement>  listNotificationAcknowledgement = notificationAcknowledgementRepository.findByNotificationId(notificationId);

				if(listNotificationAcknowledgement.isEmpty()){
					log.info("list of NotificationAcknowledgement is empty");
					return null;
				}
				List<String> listOfConsumerId =  new ArrayList<String>();
				for(NotificationAcknowledgement notificationAck : listNotificationAcknowledgement){
					listOfConsumerId.add(notificationAck.getConsumerId());

					
					if(notificationAck.getRead() != null){
						readCount++;
					}
					jsonObject.set("read", readCount);
				}
				
				dbNotificationAcknowledgement = notificationAcknowledgementRepository
						.findByConsumerIdAndNotificationId(consumerId, notificationId);
				if (dbNotificationAcknowledgement.getCheckIn() != null  ){
					jsonObject.set("checkInStatus", "true");
				} else {
					jsonObject.set("checkInStatus", "false");
				}
				if (comments != null && !comments.isEmpty()) {
					JsonArray commentArray = new JsonArray();
					for (String comment : comments) {
						commentArray.add(comment);
					}
				jsonObject.set("comments", commentArray);
				}
				jsonObject.set("like", like);
				jsonObject.set("dislike", dislike);
				jsonObject.set("share", share);
				jsonObject.set("rating", rating);
				if(dbNotification.getHotLink()== null || dbNotification.getHotLink()== ""){
					jsonObject.set("hotlink", "");
				}else{
					jsonObject.set("hotlink", dbNotification.getHotLink());
				}
				if(dbNotification.getTrackId()== null || dbNotification.getTrackId()== ""){
					jsonObject.set("addId", "");
				}else{
					jsonObject.set("addId", dbNotification.getTrackId());
				}
				if(userStausLikeOrDislike == null){
					jsonObject.set("userStausLikeOrDislike", "nolikedislike");
				}else{
				    jsonObject.set("userStausLikeOrDislike", userStausLikeOrDislike);
				}
				if(favoriteStatus ==  null){
					jsonObject.set("favoriteStaus", "false");
				}else{
				jsonObject.set("favoriteStaus", favoriteStatus);
			}
		return jsonObject;

	}

	public JsonObject parseNotificationToJsonObject(Notification notification) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.set("categoryId", notification.getCategoryId());
		jsonObject.set("title", notification.getTitle());
		jsonObject.set("description", notification.getDescription());
		if (notification.getValidFrom() != null) {
			jsonObject.set("validFrom", notification.getValidFrom().toString());
		} else {
			jsonObject.set("validFrom", "");
		}
		if (notification.getValidTo() == null) {
			jsonObject.set("validTo", "");
		} else {
			jsonObject.set("validTo", notification.getValidTo().toString());
		}
		jsonObject.set("active", notification.getActive());
		if (notification.getCreated() == null) {
			jsonObject.set("created", "");
		} else {
			jsonObject.set("created", notification.getCreated().toString());
		}
		if (notification.getLastUpdate() == null) {
			jsonObject.set("updated", "");
		} else {
			jsonObject.set("updated", notification.getLastUpdate().toString());
		}
        if (notification.getDeleted()!=null){
        jsonObject.set("delete", "true");
        }
        else{
        	 jsonObject.set("delete", "false");
        }
        if (notification.getOfferPrice() != 0.0) {
			jsonObject.set("offerPrice", notification.getOfferPrice());
		} else {
			jsonObject.set("offerPrice", "");
		}
        if (notification.getMrpPrice() != 0.0) {
			jsonObject.set("mrpPrice", notification.getOfferPrice());
		} else {
			jsonObject.set("mrpPrice", "");
		}
		jsonObject.set("offensive", notification.getOffensive());
		jsonObject.set("approve", notification.getActive());
		if (notification.getImageUrls() != null) {
			JsonArray urls = new JsonArray();
			for (String member : notification.getImageUrls()) {
					urls.add(member);
			}
		jsonObject.set("urls", urls);
		}
		jsonObject.set("consumerId", notification.getConsumerId());
		jsonObject.set("deducted_freecredit", notification.getFreeCreditsUsed());
		jsonObject.set("deducted_walletcredit", notification.getWalletCreditsUsed());
		jsonObject.set("id", notification.getId());
		jsonObject.set("radius", notification.getRadius());
		if (notification.getImages() != null &&! notification.getImages().equals("")) {
			JsonArray images = new JsonArray();
			if(notification.getImageUrls()!=null){
			for (String member : notification.getImageUrls()) {
				images.add(member);
			}}
		jsonObject.set("images", images);
		}
		return jsonObject;
	}

	public double generateRating(String consumerId) {
		int like = 0, dislike = 0, share = 0;
		double noOfPost = 0, shopRating = 0;
		noOfPost = getAllPostCountByConsumer(consumerId);
		for (Notification notification : notificationRepository
				.findByConsumerId(consumerId)) {
			like = 0; dislike = 0; share = 0;

			for (ConsumerFeedback consumerFeedback : consumerFeedbackRepository
					.findBynotificationId(notification.getId())) {
				if (consumerFeedback.getLikeDislike() != null&& "1".equals(consumerFeedback.getLikeDislike())) {
					like++;
				} else if (consumerFeedback.getLikeDislike() != null && "0".equals(consumerFeedback.getLikeDislike())) {
					dislike++;
				}
				if (consumerFeedback.getShare() != null
						&& "true".equalsIgnoreCase(consumerFeedback.getShare())) {
					share = share + consumerFeedback.getCount();
				}
			}
			shopRating = shopRating + generateRating(like, dislike, share);
		}
		shopRating = ((shopRating / noOfPost) + 5) / 2;
		return shopRating;
	}

	public int getAllPostCountByConsumer(String consumer_id) {
		List<Notification> notifications = new ArrayList<Notification>();
		for (Notification notitication : notificationRepository
				.findByConsumerId(consumer_id)) {
			notifications.add(notitication);
		}
		return notifications.size();
	}

	public double generateRating(int like, int dislike, int share) {
		double rating;
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(1);
		rating = Double.valueOf((5 * like + 4 * dislike - 1 * share))
				/ Double.valueOf((like + dislike + share));
		if (rating > 0) {
			return rating;
		} else if (rating == 0) {
			return 0;
		} else {
			return 5;
		}
	}
	
	/**
	 * Encodes the byte array into base64 string
	 *
	 * @param imageByteArray - byte array
	 * @return String a {@link java.lang.String}
	 */
	public static String encodeImage(byte[] imageByteArray) {
	    return Base64.encodeBase64URLSafeString(imageByteArray);
	}
	public NotificationResult  getHotOffers(Consumer consumer) throws ParseException, JSONException {
		NotificationResult notificationResult = new NotificationResult();
		Consumer dbConsumer = consumerRepository.findOne(consumer.getId());
		if (dbConsumer == null ) {
			log.error("Consumer id :"+consumer.getId()+" Is not valid or null");
			return null;
		}
		log.info("Inside getNearByProviderNotification!!!!!!!!!!!!!!!!!!!");

		// assign defaults;
		Double minRadius = DEFAULT_MIN_RADIUS;
		Double maxRadius = DEFAULT_MIN_RADIUS + DEFAULT_RANGE;
		Integer pageCount = DEFAULT_PAGE_COUNT;
		String searchItem="";

		if ((consumer.getMinRadius() != null)) {
			minRadius = consumer.getMinRadius();
		}
		if ((consumer.getMaxRadius() != null)) {
			maxRadius = consumer.getMaxRadius();
		}
		if ((consumer.getPageCount() != null)) {
			pageCount = consumer.getPageCount();
		}
		ZonedDateTime lastRecordDate = consumer.getLastRecordDate();
		if ((consumer.getLastRecordDate() != null)) {
			lastRecordDate = consumer.getLastRecordDate();
		}
		if(!StringUtils.isBlank(consumer.getSearch())){
			searchItem=consumer.getSearch();
		}
		BasicDBObject query = null;
		List<DBObject> notifications = new ArrayList<DBObject>();
		int limitCount=0;
		String[] arrayOfSerch =searchItem.split("\\s+");
		List<BasicDBObject> regex=new ArrayList<BasicDBObject>();
		for (String str:arrayOfSerch){
			BasicDBObject serchValue=new BasicDBObject("$regex","(?i)"+str);
			regex.add(new BasicDBObject("title",serchValue));
			regex.add(new BasicDBObject("description", serchValue));
		}

		do {
			BasicDBObject point = new BasicDBObject("type", "Point");
			double[] ptCordinate = { consumer.getLocation().get(0), consumer.getLocation().get(1) };
			point.put("coordinates", ptCordinate);
			BasicDBObject geometryContents;
			BasicDBObject nearContents;
			BasicDBObject validToDate= new BasicDBObject( "$gte", currentFormatedDate() ).append( "$lte", updateFormatedDate()  );
			if (lastRecordDate != null) {
				String date = lastRecordDate.toString().replace("[GMT]", "");
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
				Date recordedDate = format.parse(date);

				BasicDBObject notificationDate = new BasicDBObject();
				notificationDate.append("$lt", recordedDate);

				geometryContents = new BasicDBObject("$geometry", point).append("$minDistance", minRadius)
						.append("$maxDistance", maxRadius);
				nearContents = new BasicDBObject("$nearSphere", geometryContents);
				
				query = new BasicDBObject("active", true).append("valid_to", validToDate).append("$or", regex).append("notification_date", notificationDate).append("location", nearContents);
				
			} else {
				geometryContents = new BasicDBObject("$geometry", point).append("$minDistance", minRadius)
						.append("$maxDistance", maxRadius);
				nearContents = new BasicDBObject("$nearSphere", geometryContents);
				
				query = new BasicDBObject("active", true).append("valid_to", validToDate).append("$or", regex).append("location",
						nearContents);
				
			}

			DBCollection collection=mongoDbFactory.getDb(dbName).getCollection("notification");
			log.info("limit applying for query to fetch notifications: "+(pageCount-limitCount));
			DBCursor dbcursor = collection.find(query)
					.limit(pageCount-limitCount).sort(new BasicDBObject("notification_date", -1));
			log.info("Total records !!!!!!!!!!!" + dbcursor.count());
			log.info("Inside loop query !!!!!!!!!!!!:" + query);
			notifications.addAll(dbcursor.toArray());
			minRadius += DEFAULT_RANGE;
			maxRadius += DEFAULT_RANGE;
			log.info("############# Notification Size ############" + notifications.size());
			limitCount=notifications.size();
			
		} while ((notifications.size() < pageCount) && (maxRadius < DEFAULT_MAX_RADIUS + DEFAULT_RANGE));
		
		
		
		//If there is no records it will return null
		if(notifications.isEmpty()){
			log.info("NO notifications available to return. Size of Notifications:=" +notifications.size());
			return null;}
		
		String lastDateVal = null;
		JSONObject jsonObject=new JSONObject(notifications.get(notifications.size()-1).toString());
		JSONObject jsonObject1 = (JSONObject) jsonObject.get("notification_date");
		lastDateVal = (String) jsonObject1.get("$date");
		
		ZonedDateTime zdt4 = ZonedDateTime.parse(lastDateVal);
		
		notificationResult.setNotifications(notifications);
		notificationResult.setMinRadius(minRadius - DEFAULT_RANGE);
		notificationResult.setMaxRadius(maxRadius - DEFAULT_RANGE);
		notificationResult.setLastRecordDate(zdt4);
		log.info("Notification Result :" + "Minimum Radius :" + notificationResult.getMinRadius() + "Maximum Radius :"
				+ notificationResult.getMaxRadius() + "Last Record Date :" + notificationResult.getLastRecordDate()
				+ "Notification Size :" + notificationResult.getNotifications().size());
		return notificationResult;
	}
	public Date currentFormatedDate(){
		ZonedDateTime zonedateTime =ZonedDateTime.from(ZonedDateTime.now());
	    String validDate = zonedateTime.format(DateTimeFormatter.ISO_INSTANT);
	    
	    DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
	    Date date = null;
			 try {
				date = format.parse(validDate);
			} catch (ParseException e) {
				log.error("Exception occurs while formating date",e.getMessage(),e);
			}
			 return date;
			 }
	public Date updateFormatedDate(){
		ZonedDateTime zonedateTime =ZonedDateTime.from(ZonedDateTime.now());
	    String validDate = zonedateTime.format(DateTimeFormatter.ISO_INSTANT);
	    Calendar c = Calendar.getInstance();
	    
	    
	    DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
	    Date date = null;
			 try {
				date = format.parse(validDate);
				c.setTime(date);
				c.add(Calendar.DATE, 1);
			} catch (ParseException e) {
				log.error("Exception occurs while formating date",e.getMessage(),e);
			}
			 return c.getTime();
			 }
}
