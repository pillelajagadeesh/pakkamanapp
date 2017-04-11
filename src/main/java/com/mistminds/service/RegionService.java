package com.mistminds.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.stereotype.Service;

import com.mistminds.config.Constants;
import com.mistminds.config.PakkaApplicationSettingsConfiguration;
import com.mistminds.domain.BannerImage;
import com.mistminds.domain.Category;
import com.mistminds.domain.Consumer;
import com.mistminds.domain.Notification;
import com.mistminds.domain.PakkaApplicationSettings;
import com.mistminds.domain.Provider;
import com.mistminds.domain.Region;
import com.mistminds.domain.Welcome;
import com.mistminds.repository.BannerImageRepository;
import com.mistminds.repository.CategoryRepository;
import com.mistminds.repository.ConsumerRepository;
import com.mistminds.repository.NotificationRepository;
import com.mistminds.repository.PakkaApplicationSettingsRepository;
import com.mistminds.repository.ProviderRepository;
import com.mistminds.repository.RegionRepository;
import com.mistminds.web.rest.util.BanerImageResult;
import com.mistminds.web.rest.util.NotificationResult;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

@Service
public class RegionService {
	
	public static final Double DEFAULT_MIN_RADIUS= 0.00;
	public static final Double DEFAULT_MAX_RADIUS= 25000.00;
	public static final int DEFAULT_PAGE_COUNT= 20;
	public static final Double DEFAULT_RANGE = 5000.00;
	public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	
	public static final int DEFAULT_BANNER_IMAGES_SIZE = 5;

	private final Logger log = LoggerFactory.getLogger(RegionService.class);
	
	@Value("${spring.data.mongodb.database}")
	private String dbName;
	
	@Inject
	private CategoryRepository categoryRepository;
	
	@Inject
	private ConsumerRepository consumerRepository;
    
    @Inject
	private PakkaApplicationSettingsRepository pakkaApplicationSettingsRepository;
    
	@Inject
	private BannerImageRepository bannerImageRepository;

	@Inject
	private ProviderRepository providerRepository;

	@Inject
	private RegionRepository regionRepository;

	@Inject
	private NotificationRepository notificationRepository;

	@Inject
	private NotificationAcknowledgementService notificationAcknowledgementService;

	@Inject
	private NotificationService notificationService;
	
	@Inject
    private MongoDbFactory mongoDbFactory;
    
	public Welcome sendRegionNotification(Consumer consumer) {
		String imageUrl = null;
		Welcome welcome = new Welcome();
		
			Consumer dbConsumer = consumerRepository.findOne(consumer.getId());
			if (dbConsumer == null ){
				log.error("Consumer id :"+consumer.getId()+" Is not valid or null");
				return null;
				}
			BasicDBObject query = null;
			String notificationId=null;
			String consumerId=null;
			DBObject obj;
			DBCursor dbcursor;
			Double minRadius = DEFAULT_MIN_RADIUS;
			Double maxRadius = DEFAULT_MIN_RADIUS + DEFAULT_RANGE;
			do{
			BasicDBObject point = new BasicDBObject("type", "Point");
			double[] ptCordinate = { consumer.getLocation().get(0), consumer.getLocation().get(1) };
			point.put("coordinates", ptCordinate);
			BasicDBObject geometryContents = new BasicDBObject("$geometry", point).append("$minDistance", minRadius)
					.append("$maxDistance", maxRadius);
			BasicDBObject walletCredit=new BasicDBObject("$gt",0);
			
			BasicDBObject nearContents = new BasicDBObject("$nearSphere", geometryContents);
			query = new BasicDBObject("active", true).append("wallet_credits_used", walletCredit)
					.append("location", nearContents);
			
			log.info("query to get paid notifications from DB to push : "+query);
			
			DBCollection collection=mongoDbFactory.getDb(dbName).getCollection("notification");

			 dbcursor = collection.find(query).limit(1).sort(new BasicDBObject("notification_date", -1));

			log.info("Total record for push notification " + dbcursor.count());
			minRadius += DEFAULT_RANGE;
			maxRadius += DEFAULT_RANGE;
			}
			while(dbcursor.count()<=0 && maxRadius <DEFAULT_RANGE*3);
			if(dbcursor.count()<=0){
				log.info("No records with in this range");
				return null;
			}
			 obj=dbcursor.one();
			JSONObject jsonObject;
			try {
				jsonObject = new JSONObject(obj.toString());
				imageUrl=jsonObject.getJSONArray("image_urls").getString(0);
				JSONObject idObj = (JSONObject) jsonObject.get("_id");
				 notificationId=idObj.getString("$oid");
				 consumerId=jsonObject.getString("consumer_id");
			} catch (JSONException e1) {
				log.error("Json Exception occurs while reading notification details from dbcursor object");
				e1.printStackTrace();
			} 

					
					if (notificationAcknowledgementService.notificationAcknowledgment(notificationId,
							dbConsumer.getId())) {
						Provider dbProvider = providerRepository.findOneByConsumerId(consumerId);

						notificationService.pushNotification(notificationRepository.findOne(notificationId), notificationId,
								dbConsumer.getDevice_info().getDeviceId(), imageUrl, dbProvider.getName(),dbProvider.getImageUrl(),null);

						welcome.setMessage(Constants.SUCCESS_RESULT);

					}
					else{
						welcome.setMessage(Constants.FAILURE_RESULT);
					}
		return welcome;
	}

	public List<BanerImageResult> getNearByNotificationHomeBannerImages(Consumer consumer) {
		Consumer dbConsumer = consumerRepository.findOne(consumer.getId());
		if (dbConsumer == null ) {
			log.error("Consumer id :"+consumer.getId()+" Is not valid or null");
			return null;
		}
		List<BanerImageResult> homeBannerImage = new ArrayList<BanerImageResult>();
		BanerImageResult baner;

		BasicDBObject query = null;
		List<DBObject> notifications = new ArrayList<DBObject>();
		Double DEFAULT_RADIUS_FOR_BANNER_IMAGE = defaultRegionRadius();
		BasicDBObject point = new BasicDBObject("type", "Point");
		double[] ptCordinate = { consumer.getLocation().get(0), consumer.getLocation().get(1) };
		point.put("coordinates", ptCordinate);
		BasicDBObject geometryContents=new BasicDBObject("$geometry", point)
		.append("$maxDistance", DEFAULT_RADIUS_FOR_BANNER_IMAGE);
		BasicDBObject nearContents = new BasicDBObject("$nearSphere", geometryContents);
		
		PakkaApplicationSettings pas=pakkaApplicationSettingsRepository.findByName(PakkaApplicationSettingsConfiguration.KEY_NAME_HOME_BANNER_AGE_IN_DAYS);
		long days=Long.parseLong(pas.getValue().toString());
		
		ZonedDateTime zonedateTime =ZonedDateTime.from(ZonedDateTime.now());
		ZonedDateTime minusdate=zonedateTime.minusDays(days);
	    String validDate = minusdate.format(DateTimeFormatter.ISO_INSTANT);
	    
	    DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
	    Date date;
		try {
			date = format.parse(validDate);
	    
		BasicDBObject notificationDate= new BasicDBObject();
		notificationDate.append("$gte", date);
		
		BasicDBObject currentDate= new BasicDBObject();
		currentDate.append("$gte", currentFormatedDate());
		
		
		query = new BasicDBObject("active", true).append("notification_date", notificationDate).append("valid_to", currentDate).append("homeBannerStatus", true)
				.append("location", nearContents);
		
		} catch (ParseException e) {
			log.error("Exception occurs while formating date in home banner",e.getMessage(),e);
		}
		
		log.info("query to get notifications from DB with homeBannerStatus true : "+query);
		log.info("Radius to get banner images :" + DEFAULT_RADIUS_FOR_BANNER_IMAGE);
		
		DBCollection collection=mongoDbFactory.getDb(dbName).getCollection("notification");

		DBCursor dbcursor = collection.find(query).limit(DEFAULT_BANNER_IMAGES_SIZE).sort(new BasicDBObject("notification_date", -1));

		log.info("Total records with homeBannerStatus true" + dbcursor.count());

		notifications.addAll(dbcursor.toArray());
		log.info("Limited notifications from DB with homeBannerStatus true:" + notifications.size());

		for (DBObject obj : notifications) {
			 baner=new BanerImageResult();
			JSONObject jsonObject;
			try {
				jsonObject = new JSONObject(obj.toString());
				baner.setImageUrl(jsonObject.getJSONArray("image_urls").getString(0));
				JSONObject idObj = (JSONObject) jsonObject.get("_id");
				baner.setId(idObj.getString("$oid"));
				baner.setTitle(jsonObject.getString("title"));
 				homeBannerImage.add(baner);
			} catch (JSONException e1) {
				log.error("Json Exception occurs while reading home banner image Urls from db");
				e1.printStackTrace();
			} 

		}
		if (homeBannerImage.size() == DEFAULT_BANNER_IMAGES_SIZE) {
			log.info("size of home banner from Notifications equal to:" + homeBannerImage.size());
			return homeBannerImage;}

		BannerImage result = bannerImageRepository.findOne("5832d4463f7a242d84157f49");
		if(result!= null){
			String[] images = result.getHomeBannerImages();
			int i = 0;
			while (homeBannerImage.size() < DEFAULT_BANNER_IMAGES_SIZE) {
				baner=new BanerImageResult();
				baner.setImageUrl(images[i]);
				homeBannerImage.add(baner);
				++i;
			}
		}
		log.info("Home Banner Images from notifications :"+ notifications.size()+" + Default Home Banner Images from DB to make count:" + DEFAULT_BANNER_IMAGES_SIZE);
		Collections.reverse(homeBannerImage);
		return homeBannerImage;
	}

	public List<BanerImageResult> getNearByNotificationCategoryBannerImages(Consumer consumer) {
		Consumer dbConsumer = consumerRepository.findOne(consumer.getId());
		if (dbConsumer == null) {
			log.error("Consumer id :"+consumer.getId()+" Is not valid or null");
			return null;
		}
		List<BanerImageResult> categoryBannerImage = new ArrayList<BanerImageResult>();
		BanerImageResult baner;
		List<Category> subCategoryDetails = null;
		List<String> parentIds =  new ArrayList<String>();
		subCategoryDetails=categoryRepository.findByParentId(consumer.getCategoryId());
		if(!subCategoryDetails.isEmpty()){
			for(Category subCategories:subCategoryDetails){
				parentIds.add(subCategories.getId());	
			}
		}else{
			parentIds.add(consumer.getCategoryId());
		}

		BasicDBObject query = null;
		BasicDBObject point = new BasicDBObject("type", "Point");
		Double DEFAULT_RADIUS_FOR_BANNER_IMAGE = defaultRegionRadius();
		double[] ptCordinate = { consumer.getLocation().get(0), consumer.getLocation().get(1) };
		point.put("coordinates", ptCordinate);
		BasicDBObject categoryIdVal = new BasicDBObject();
		categoryIdVal.append("$in", parentIds);
		
		PakkaApplicationSettings pas=pakkaApplicationSettingsRepository.findByName(PakkaApplicationSettingsConfiguration.KEY_NAME_CATEGORY_BANNER_AGE_IN_DAYS);
		long days=Long.parseLong(pas.getValue().toString());
		ZonedDateTime zonedateTime =ZonedDateTime.from(ZonedDateTime.now());
		ZonedDateTime minusdate=zonedateTime.minusDays(days);
	    String validDate = minusdate.format(DateTimeFormatter.ISO_INSTANT);
	    
	    DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
	    Date date;
		try {
			date = format.parse(validDate);
			BasicDBObject notificationDate= new BasicDBObject();
			notificationDate.append("$gte", date);
			
			BasicDBObject currentDate= new BasicDBObject();
			currentDate.append("$gte", currentFormatedDate());
		
		BasicDBObject geometryContents = new BasicDBObject("$geometry", point).append("$maxDistance", DEFAULT_RADIUS_FOR_BANNER_IMAGE);
		BasicDBObject nearContents = new BasicDBObject("$nearSphere", geometryContents);
		query = new BasicDBObject("active", true).append("notification_date", notificationDate).append("valid_to", currentDate).append("category_id", categoryIdVal).append("categoryBannerStatus", true).append("location", nearContents);
		} catch (ParseException e) {
			log.error("Exception occurs while formating date in category banner",e.getMessage(),e);
		}
		
		log.info("query to get notifications from DB with categoryBannerStatus true : "+query);
		log.info("Radius to get banner images :" + DEFAULT_RADIUS_FOR_BANNER_IMAGE);

		DBCollection collection=mongoDbFactory.getDb(dbName).getCollection("notification");
		DBCursor dbcursor = collection.find(query).limit(DEFAULT_BANNER_IMAGES_SIZE).sort(new BasicDBObject("notification_date", -1));

		log.info("Total records with categoryBannerStatus true: " + dbcursor.count());
		List<DBObject> notifications = new ArrayList<DBObject>();
		notifications.addAll(dbcursor.toArray());
		log.info("Limited notifications from DB with categoryBannerStatus true:" + notifications.size());

		for (DBObject obj : notifications) {
			JSONObject jsonObject;
			try {
				jsonObject = new JSONObject(obj.toString());
				baner=new BanerImageResult();
				baner.setImageUrl(jsonObject.getJSONArray("image_urls").getString(0));
				JSONObject idObj = (JSONObject) jsonObject.get("_id");
				baner.setId(idObj.getString("$oid"));
				baner.setTitle(jsonObject.getString("title"));
				categoryBannerImage.add(baner);
			} catch (JSONException e1) {
				log.error("Json Exception occurs while reading category banner image Urls from db"+e1);
			} 

		}
		if (categoryBannerImage.size() == DEFAULT_BANNER_IMAGES_SIZE) {
			log.info("size of category banner from Notifications equal to:" + categoryBannerImage.size());
			return categoryBannerImage;}

		BannerImage result = bannerImageRepository.findOne("5832d4463f7a242d84157f49");
		String[] categoryImages = null;
		if ("57837b01a8db501e5ff95f32".contains(consumer.getCategoryId())) {
			categoryImages = result.getFoodCategoryBannerImages();
		} else if ("57837b32a8db501e5ff95f33".contains(consumer.getCategoryId())) {
			categoryImages = result.getFashionCategoryBannerImages();
		} else if ("57837b48a8db501e5ff95f34".contains(consumer.getCategoryId())) {
			categoryImages = result.getEntertainmentCategoryBannerImages();
		} else if ("57837b56a8db501e5ff95f35".contains(consumer.getCategoryId())) {
			categoryImages = result.getElectronicsCategoryBannerImages();
		} else if ("57837b63a8db501e5ff95f36".contains(consumer.getCategoryId())) {
			categoryImages = result.getEducationCategoryBannerImages();
		} else if ("57837b7aa8db501e5ff95f38".contains(consumer.getCategoryId())) {
			categoryImages = result.getBuyCategoryBannerImages();
		} else if ("57837b90a8db501e5ff95f3a".contains(consumer.getCategoryId())) {
			categoryImages = result.getJobCategoryBannerImages();
		} else if ("57837b9aa8db501e5ff95f3b".contains(consumer.getCategoryId())) {
			categoryImages = result.getGovernmentCategoryBannerImages();
		}else if ("57837baea8db501e5ff95f3d".contains(consumer.getCategoryId())) {
			categoryImages = result.getMiscellaneousBannerImages();
		}
		if(categoryImages!=null){
			int i = 0;
			while (categoryBannerImage.size() < DEFAULT_BANNER_IMAGES_SIZE) {
				baner=new BanerImageResult();
				baner.setImageUrl(categoryImages[i]);
				categoryBannerImage.add(baner);
				++i;
			}

		}
		log.info("Category Banner Images from notifications :"+ notifications.size()+" + Default Banner Images from DB to make count:" + DEFAULT_BANNER_IMAGES_SIZE);
		Collections.reverse(categoryBannerImage);
		return categoryBannerImage;
	}
@Deprecated
	public List<Provider> getNearByProviderNotification(Consumer consumer) {
		ZonedDateTime now = ZonedDateTime.now();
		List<String> unsubscribeCategory = new ArrayList<String>();
		List<Notification> notifications = null;
		List<Notification> providernotification = null;
		List<Provider> providers = new ArrayList<Provider>();
		try {
			Consumer dbConsumer = consumerRepository.findOne(consumer.getId());
			if (dbConsumer != null && consumer.getLocation() != null && !consumer.getLocation().isEmpty()) {
				if (dbConsumer.getUnsubscribeCategory() != null && !dbConsumer.getUnsubscribeCategory().isEmpty()) {
					for (Category category : dbConsumer.getUnsubscribeCategory()) {
						unsubscribeCategory.add(category.getName());
					}
				}
				Point center = new Point(consumer.getLocation().get(0), consumer.getLocation().get(1));
				Double radius = (defaultRegionRadius()) / 1000;
				Distance distance = new Distance(Math.toDegrees(radius / 6378.137));
				Circle circle = new Circle(center, distance);
				List<Provider> provider = providerRepository.findByLocationWithin(circle);
				for (Provider nearestProvider : provider) {
					log.info("nearest provider info  for circle area:+" + nearestProvider);
					notifications = new ArrayList<Notification>();
					if (!consumer.getSubscribeCategory().get(0).equalsIgnoreCase("All")) {
						providernotification = notificationRepository
								.findByConsumerIdInAndCategoryIdInAndValidToAfterAndActiveIsTrue(
										nearestProvider.getConsumerId(), consumer.getSubscribeCategory(),
										now.minusDays(1));
					} else {
						providernotification = notificationRepository
								.findByConsumerIdInAndCategoryIdNotInAndValidToAfterAndActiveIsTrue(
										nearestProvider.getConsumerId(), unsubscribeCategory, now.minusDays(1));
					}
					for (Notification notification : providernotification) {
						log.info("nearest provider notification info:" + notification);
						notifications.add(notification);
						Collections.sort(notifications, new Comparator<Notification>() {
							public int compare(Notification m1, Notification m2) {
								return (m1.getNotificationDate().compareTo(m2.getNotificationDate()));
							}
						});

					}
					if (!notifications.isEmpty()) {
						nearestProvider.setNotification(notifications);
					}
					if (nearestProvider.getNotification() != null && !nearestProvider.getNotification().isEmpty()) {
						providers.add(nearestProvider);
					}

				}
			}
		} catch (NullPointerException npx) {
			log.debug("Null pointer exception " + npx);
		}
		return providers;

	}
	    
      
public NotificationResult getNearByNotifications(Consumer consumer) throws ParseException, JSONException {
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
	List<String> subscribeCategory = null;
	String searchItem="";
	
	if ((consumer.getSubscribeCategory() != null)) {
		subscribeCategory = consumer.getSubscribeCategory();
	}
	

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
		BasicDBObject categoryIdVal = new BasicDBObject();
		if(subscribeCategory != null){
		categoryIdVal.append("$in", subscribeCategory);
		}
		
	    
		BasicDBObject validToDate= new BasicDBObject();
		validToDate.append("$gte", currentFormatedDate());
		
		if (lastRecordDate != null) {
			String date = lastRecordDate.toString().replace("[GMT]", "");
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
			Date recordedDate = format.parse(date);

			BasicDBObject notificationDate = new BasicDBObject();
			notificationDate.append("$lt", recordedDate);

			geometryContents = new BasicDBObject("$geometry", point).append("$minDistance", minRadius)
					.append("$maxDistance", maxRadius);
			nearContents = new BasicDBObject("$nearSphere", geometryContents);
			if (subscribeCategory == null) {
			query = new BasicDBObject("active", true).append("valid_to", validToDate).append("$or", regex).append("notification_date", notificationDate).append("location", nearContents);
			}else{
				query = new BasicDBObject("active", true).append("valid_to", validToDate).append("category_id", categoryIdVal)
						.append("$or", regex).append("notification_date", notificationDate).append("location", nearContents);
			}
		} else {
			geometryContents = new BasicDBObject("$geometry", point).append("$minDistance", minRadius)
					.append("$maxDistance", maxRadius);
			nearContents = new BasicDBObject("$nearSphere", geometryContents);
			if (subscribeCategory == null) {
			query = new BasicDBObject("active", true).append("valid_to", validToDate).append("$or", regex).append("location",
					nearContents);
			}
			else{
				query = new BasicDBObject("active", true).append("valid_to", validToDate).append("category_id", categoryIdVal).append("$or", regex).append("location",
						nearContents);
			}
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
	if (subscribeCategory != null) {
	Category categoryDetails=categoryRepository.findOne(subscribeCategory.get(0));
	String markerPins=categoryDetails.getMarkerPins();
	if(!StringUtils.isBlank(markerPins)){
	notificationResult.setMarkerPinUrl(markerPins);
	}
	}else{
		notificationResult.setMarkerPinUrl("");
	}
	notificationResult.setNotifications(notifications);
	notificationResult.setMinRadius(minRadius - DEFAULT_RANGE);
	notificationResult.setMaxRadius(maxRadius - DEFAULT_RANGE);
	notificationResult.setLastRecordDate(zdt4);
	log.info("Notification Result :" + "Minimum Radius :" + notificationResult.getMinRadius() + "Maximum Radius :"
			+ notificationResult.getMaxRadius() + "Last Record Date :" + notificationResult.getLastRecordDate()
			+ "Notification Size :" + notificationResult.getNotifications().size());
	return notificationResult;

}

	public boolean addConsumerRegion(Region region) {
		log.debug("Inside addConsumerregion method of RegionServic");
		boolean flag = true;
		List<Region> regions = regionRepository.findAll();
		for (Region dbRegion : regions) {
			if (dbRegion.getLocation() != null
					&& Double.compare(dbRegion.getLocation().get(0), region.getLocation().get(0)) == 0 ? true : false)
				if (Double.compare(dbRegion.getLocation().get(1), region.getLocation().get(1)) == 0 ? true : false) {
					flag = false;
					break;
				}
		}
		return flag;
	}
	public Double defaultRegionRadius (){
		PakkaApplicationSettings pas=pakkaApplicationSettingsRepository.findByName(PakkaApplicationSettingsConfiguration.KEY_NAME_DEFAULT_REGIONS_RADIUS);
		return (double)Double.parseDouble(pas.getValue().toString());
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
}
