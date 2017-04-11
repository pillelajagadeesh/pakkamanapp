package com.mistminds.web.rest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.util.ListUtils;

import com.codahale.metrics.annotation.Timed;
import com.hazelcast.com.eclipsesource.json.JsonObject;
import com.mistminds.config.Constants;
import com.mistminds.config.PakkaApplicationSettingsConfiguration;
import com.mistminds.domain.Category;
import com.mistminds.domain.Consumer;
import com.mistminds.domain.DeviceInfo;
import com.mistminds.domain.Notification;
import com.mistminds.domain.PakkaApplicationSettings;
import com.mistminds.domain.Provider;
import com.mistminds.domain.Welcome;
import com.mistminds.domain.util.Util;
import com.mistminds.repository.CategoryRepository;
import com.mistminds.repository.ConsumerRepository;
import com.mistminds.repository.DeviceInfoRepository;
import com.mistminds.repository.NotificationRepository;
import com.mistminds.repository.PakkaApplicationSettingsRepository;
import com.mistminds.repository.ProviderRepository;
import com.mistminds.service.NotificationAcknowledgementService;
import com.mistminds.service.NotificationService;
import com.mistminds.service.SNSMobilePush;
import com.mistminds.web.rest.util.HeaderUtil;
import com.mistminds.web.rest.util.NotificationResult;

/**
 * REST controller for managing Notification.
 */
@RestController
@RequestMapping("/api")
public class NotificationResource {

    private final Logger log = LoggerFactory.getLogger(NotificationResource.class);
     
    @Inject
	private CategoryRepository categoryRepository;
    @Inject
	private DeviceInfoRepository deviceInfoRepository;
    @Inject
    private NotificationRepository notificationRepository;
    @Inject
    private ProviderRepository providerRepository;
    @Inject 
    private NotificationService notificationService;
    @Inject 
    private NotificationAcknowledgementService notificationAcknowledgementService;
	@Inject
    private ConsumerRepository consumerRepository;
	@Inject
	private PakkaApplicationSettingsRepository pakkaApplicationSettingsRepository;
	private Provider dbProvider;
	private Consumer dbConsumer;
	private Notification dbNotification;
    /**
     * POST  /notifications -> Create a new notification.
     * @throws IOException 
     */
    @RequestMapping(value = "/notifications",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Welcome> createNotification(@RequestBody Notification notification) throws URISyntaxException, IOException {
        log.debug("REST request to save Notification : {}", notification);
        Welcome welcome = new Welcome();
        String radiusresult=null;
        Double monthlycreditused=0.0;
        Double walletcreditused=0.0;
        Double monthfreecredit=0.0;
        Double pwallet=0.0;
        String adminPaid= "adminPaid";
        String adminUnpaid= "adminUnPaid";
		PakkaApplicationSettings adminMobiles1=pakkaApplicationSettingsRepository.findByName(PakkaApplicationSettingsConfiguration.KEY_NAME_ADMIN1);
	    PakkaApplicationSettings adminMobiles2=pakkaApplicationSettingsRepository.findByName(PakkaApplicationSettingsConfiguration.KEY_NAME_ADMIN2);
        PakkaApplicationSettings priceMappingValue=pakkaApplicationSettingsRepository.findByName(PakkaApplicationSettingsConfiguration.KEY_NAME_PRICE_MAPING);
        PakkaApplicationSettings validToDate=pakkaApplicationSettingsRepository.findByName(PakkaApplicationSettingsConfiguration.KEY_NAME_VALIDTO);
        String mobileNosPaid=adminMobiles1.getValue().toString();
        log.info("paidAdminNotes :" +mobileNosPaid);
		String mobileNosUnPaid=adminMobiles2.getValue().toString();
		log.info("UnPaidAdminNotes :" +mobileNosUnPaid);
		String[] paidAdminMobilesNo=mobileNosPaid.split(",");
		String[] unPaidAdminMobilesNo =mobileNosUnPaid.split(",");
		log.info("####################paidAdminDeviceIds####################"+paidAdminMobilesNo);
		log.info("********************unPaidAdminDeviceIds*****************"+unPaidAdminMobilesNo);
        String price_mapping =priceMappingValue.getValue().toString();
        String validTo=validToDate.getValue().toString();
        log.info("Valid upto no of duration in days :" +validTo);
        HashMap<String ,String> hashmap=new HashMap<String ,String>();
    	String  mappingvalue=price_mapping;
    	String[] strArray = mappingvalue.split(",");
    	for(int i=0; i<strArray.length;i++){
    		String local=strArray[i];
    		String[] mapArray = local.split("=");
    		hashmap.put(mapArray[0], mapArray[1]);
    	}
        if (notification.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("notification", "idexists", "A new notification cannot already have an ID")).body(null);
        }
        
        radiusresult = notification.getRadius();
        String str = ""+ (Integer.parseInt(radiusresult))/1000;
        String requiredpoints = hashmap.get(str);
        Consumer dbConsumer = consumerRepository.findOne(notification.getConsumerId());
        dbProvider = providerRepository.findOneByConsumerId(notification.getConsumerId());
        notification.setLocation(dbProvider.getLocation());
        notification.setProviderName(dbProvider.getName());
        notification.setProviderId(dbProvider.getId());
        notification.setMainCategoryId(categoryRepository.findById(notification.getCategoryId()).getParentId());
        log.info("Provider Location: "+notification.getLocation()+"Provider Name: "+notification.getProviderName()+"Provider Id: "+notification.getProviderId());
        monthfreecredit =  dbProvider.getMonthly_free_credits();
        pwallet = dbProvider.getWallet_credits();
        if(monthfreecredit>=Double.parseDouble(requiredpoints)){
        	monthlycreditused=(monthfreecredit-Double.parseDouble(requiredpoints));
    	    dbProvider.setMonthly_free_credits(monthlycreditused);
    	    providerRepository.save(dbProvider);
    	    monthlycreditused= (monthfreecredit-monthlycreditused);
    	    walletcreditused=0.0;
       }
       else if((monthfreecredit+pwallet)>=Double.parseDouble(requiredpoints)) {
    	   monthlycreditused=(monthfreecredit-Double.parseDouble(requiredpoints));
    	   monthlycreditused=pwallet+monthlycreditused;
    	    dbProvider.setMonthly_free_credits(0);
    	    dbProvider.setWallet_credits(monthlycreditused);
    	    providerRepository.save(dbProvider);
    	    walletcreditused= pwallet-monthlycreditused;
    	    monthlycreditused=monthfreecredit;
       }
       else{
    	   welcome.setMessage(Constants.FAILURE_RESULT);
    	   return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("notification", "wallet", "you don't have sufficient credits")).body(welcome);
       }
    
        notification  = notificationService.createNotification(notification);
        
        if(notification != null){
        	log.info("API REQUIST valid from :"+notification.getValidFrom()+ "valid to :"+notification.getValidTo());
        	if(notification.getValidFrom() == null){ 
        		notification.setValidFrom(ZonedDateTime.now());	
        	}

        	if(notification.getValidTo() == null){ 
        		notification.setValidTo((notification.getValidFrom().plusDays(Long.parseLong(validTo))));
        	}
        	log.info("API RESPONSE valid from :"+notification.getValidFrom()+ "valid to :"+notification.getValidTo());
        	//notification.setTrackId(Constants.generateTrackId());
        	notification.setTrackId(Constants.generateAdId());
        	notification.setFreeCreditsUsed(monthlycreditused);
        	notification.setWalletCreditsUsed(walletcreditused);
        	notification.setNotificationDate(ZonedDateTime.now());
        	notification.setLastUpdate(ZonedDateTime.now());
        	Notification result = notificationRepository.save(notification);
         	log.info("After storing notification in db:"+result.getId());
         	log.info("####################paidAdminDeviceIds####################"+paidAdminMobilesNo);
    		log.info("********************unPaidAdminDeviceIds*****************"+unPaidAdminMobilesNo);
      	if(result.getWalletCreditsUsed() > 0){
      		log.info("###########################WALLET CREDITS########################"+result.getWalletCreditsUsed());
      		log.info("###########################PAID NOTIFICATIONS########################");
      		for(String paid:paidAdminMobilesNo){
    			Consumer consume=consumerRepository.findByMobile(paid);
    				notificationService.pushNotification(result, result.getId(), consume.getDevice_info().getDeviceId(), result.getImageUrls()[0], dbProvider.getName(), dbProvider.getImageUrl(), adminPaid);
    		}
      		
      		/*for(String paidDeviceId:paidAdminDeviceIds){
      			notificationService.pushNotification(result, result.getId(), paidDeviceId, result.getImageUrls()[0], dbProvider.getName(), dbProvider.getImageUrl(), adminPaid);
      		}*/
     	   }
        	else{
        		log.info("###########################UNPAID NOTIFICATIONS########################");
        		/*for(String unPaidDeviceId:unPaidAdminDeviceIds){
        			notificationService.pushNotification(result, result.getId(), unPaidDeviceId, result.getImageUrls()[0], dbProvider.getName(), dbProvider.getImageUrl(),adminUnpaid);
        		}*/
        		for(String unPaid:unPaidAdminMobilesNo){
        			Consumer consume=consumerRepository.findByMobile(unPaid);
        				notificationService.pushNotification(result, result.getId(), consume.getDevice_info().getDeviceId(), result.getImageUrls()[0], dbProvider.getName(), dbProvider.getImageUrl(), adminUnpaid);
        		}
        		
        	}
        	welcome.setMessage(Constants.SUCCESS_RESULT);
        	log.info("SUCCESS MESSAGE"+welcome);
        	return ResponseEntity.created(new URI("/api/notifications/" + result.getId()))
        			.headers(HeaderUtil.createEntityCreationAlert("notification", result.getId().toString()))
        			.body(welcome);
        }else{
        	welcome.setMessage(Constants.FAILURE_RESULT);
        	log.info("FAILURE MESSAGE"+welcome);
        	return ResponseEntity.created(new URI("/api/notifications/" + ""))
        			.headers(HeaderUtil.createEntityCreationAlert("notification", ""))
        			.body(welcome);
        }
       
    }

    /**
     * PUT  /notifications -> Updates an existing notification.
     */
    @RequestMapping(value = "/notifications",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Notification> updateNotification(@RequestBody Notification notification) throws URISyntaxException {
        log.debug("REST request to update Notification : {}", notification);
        Notification result = notificationRepository.save(notification);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("notification", notification.getId().toString()))
            .body(result);
    }
    
    /**
     * POST  /notifications -> approve the notification and send to mobile.
     * @throws JSONException 
     */
    @SuppressWarnings("unused")
	@RequestMapping(value = "/notifications/approve", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Welcome> approveNotification(
			@RequestBody JSONObject obj) throws URISyntaxException, JSONException {
		String id = null;
		Welcome welcome = new Welcome();
		ZonedDateTime now = ZonedDateTime.now();
		Notification result = null;
			log.debug("REST request to approve Notification : {}",
					obj.getString("id"));
			id = (String) obj.get("id");
			result = notificationRepository.findOneById(id);
			result.setCategory(obj.getString("subCategory"));
			result.setMainCategoryId(obj.getString("category"));
			notificationRepository.save(result);
			if(result==null){
				return null;
			}
			notificationService.sendNotification(id);
				result.setActive(obj.getBoolean("active"));
				result.setApprovedBy(obj.getString("approvedBy"));
				result.setApprovedTime(now);
				notificationRepository.save(result);
				welcome.setMessage(Constants.SUCCESS_RESULT);
				return ResponseEntity.ok()
						.headers(HeaderUtil.createEntitySentAlert("notification", result.getId().toString()))
						.body(welcome);
				
			}
    
    
	/**
	 * POST /notifications -> approve the notification and send to mobile.
	 * 
	 * @throws JSONException
	 */
	@RequestMapping(value = "/notifications/mobileApprove", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Welcome> approveMobileNotification(@RequestBody Notification notification)
			throws URISyntaxException, JSONException {
		PakkaApplicationSettings adminMobiles1 = pakkaApplicationSettingsRepository
				.findByName(PakkaApplicationSettingsConfiguration.KEY_NAME_ADMIN1);
		PakkaApplicationSettings adminMobiles2 = pakkaApplicationSettingsRepository
				.findByName(PakkaApplicationSettingsConfiguration.KEY_NAME_ADMIN2);
		String mobileNosPaid = adminMobiles1.getValue().toString();
		log.info("paidAdminNotes :" + mobileNosPaid);
		String mobileNosUnPaid = adminMobiles2.getValue().toString();
		log.info("UnPaidAdminNotes :" + mobileNosUnPaid);
		String[] paidAdminMobilesNo = mobileNosPaid.split(",");
		String[] unPaidAdminMobilesNo = mobileNosUnPaid.split(",");
		Welcome welcome = new Welcome();
		ZonedDateTime now = ZonedDateTime.now();
		Notification result = null;
		log.info("REST request to approve Notification : {}" + "notificationId" + notification.getId());

		dbConsumer = consumerRepository.findOneById(notification.getConsumerId());
		result = notificationRepository.findOneById(notification.getId());
		if (result != null && dbConsumer != null) {
			if (result.getWalletCreditsUsed() > 0) {
				for (String paidAdminDeviceId : paidAdminMobilesNo) {
					if (paidAdminDeviceId.contains(dbConsumer.getMobile())) {
						notificationService.sendNotification(notification.getId());
						result.setActive(true);
						result.setApprovedTime(now);
						result.setApprovedBy(dbConsumer.getName());
						notificationRepository.save(result);
						welcome.setMessage(Constants.SUCCESS_RESULT);
					}
				}
			} else {

				for (String unPaidAdminDeviceId : unPaidAdminMobilesNo) {
					if (unPaidAdminDeviceId.contains(dbConsumer.getMobile())) {

						notificationService.sendNotification(notification.getId());
						result.setActive(true);
						result.setApprovedTime(now);
						result.setApprovedBy(dbConsumer.getName());
						notificationRepository.save(result);
						welcome.setMessage(Constants.SUCCESS_RESULT);
					}
				}

			}
		} else {

			welcome.setMessage(Constants.FAILURE_RESULT);
			return ((BodyBuilder) ResponseEntity.notFound()
					.headers(HeaderUtil.createEntitySentAlert("notification", result.getId().toString())))
							.body(welcome);

		}
		return ResponseEntity.ok().headers(HeaderUtil.createEntitySentAlert("notification", result.getId().toString()))
				.body(welcome);
	}
    /**
     * GET  /notifications -> get all the notifications.
     */
    @RequestMapping(value = "/notifications",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Notification> getAllNotifications(@RequestParam("status") String  filterStatus,@RequestParam("category") String filterCategory,@RequestParam("mainCategory") String filterMainCategory) 
    {
    	ZonedDateTime zonedateTime =ZonedDateTime.from(ZonedDateTime.now());
    	List<Notification> notifications = new ArrayList<Notification>();
    	List<String> parentIds =  new ArrayList<String>();
    	Category categoryDetails = null;
    	List<Category> subCategoryDetails = null;
    	
    	log.debug("REST request to get all Notifications");
        try
        {
        	log.info("Selecting Notifications With Status : "+filterStatus +" & with Category : "+filterCategory);
        	boolean Status=Boolean.parseBoolean(filterStatus);
        	/*if(!filterCategory.isEmpty()){
	        	categoryDetails = categoryRepository.findByName(filterCategory);
	        	subCategoryDetails=categoryRepository.findByParentId(categoryDetails.getId());
	        	if(!subCategoryDetails.isEmpty()){
	        	for(Category subCategories:subCategoryDetails){
	        		parentIds.add(subCategories.getId());	
	        	}
	        	}else{
	        		parentIds.add(categoryDetails.getId());
	        	}
        	}*/
         if(!filterCategory.isEmpty())
        	{ if("Expired".equals(filterStatus)){
        		notifications = notificationRepository.findByValidToBeforeAndCategoryIdInAndDeletedIsNull(zonedateTime, filterCategory);
        	}
          if("UnApproved-Paid".equals(filterStatus)){
              	 notifications = notificationRepository.findAllByActiveAndValidToAfterAndCategoryIdInAndDeletedIsNullOrderByWalletCreditsUsedDesc(Status,zonedateTime.minusDays(1),filterCategory);
               }
          if("DELETED".equals(filterStatus)){
        	  notifications =  notificationRepository.findByDeletedIsNotNullAndAndCategoryIdInOrderByCreatedDesc(filterCategory);
          }
        	if(!"Expired".equals(filterStatus) && !"UnApproved-Paid".equals(filterStatus) && !"DELETED".equals(filterStatus)){
        		notifications = notificationRepository.findByActiveAndValidToAfterAndCategoryIdInAndDeletedIsNull(Status,zonedateTime.minusDays(1), filterCategory);
        	}
        	}
         
         if(!filterMainCategory.isEmpty())
     	{ if("Expired".equals(filterStatus)){
     		notifications = notificationRepository.findByValidToBeforeAndMainCategoryIdInAndDeletedIsNull(zonedateTime, filterMainCategory);
     	}
       if("UnApproved-Paid".equals(filterStatus)){
           	 notifications = notificationRepository.findAllByActiveAndValidToAfterAndMainCategoryIdInAndDeletedIsNullOrderByWalletCreditsUsedDesc(Status,zonedateTime.minusDays(1),filterMainCategory);
            }
       if("DELETED".equals(filterStatus)){
     	  notifications =  notificationRepository.findByDeletedIsNotNullAndAndMainCategoryIdInOrderByCreatedDesc(filterMainCategory);
       }
     	if(!"Expired".equals(filterStatus) && !"UnApproved-Paid".equals(filterStatus) && !"DELETED".equals(filterStatus)){
     		notifications = notificationRepository.findByActiveAndValidToAfterAndMainCategoryIdInAndDeletedIsNull(Status,zonedateTime.minusDays(1), filterMainCategory);
     	}
     	}
         
         
         if(filterCategory.isEmpty()&& filterMainCategory.isEmpty()){
        	 if("Expired".equals(filterStatus)){
           		notifications = notificationRepository.findByValidToBeforeAndDeletedIsNullOrderByCreatedDesc(zonedateTime.minusDays(1));
           	}
        	 else if("UnApproved-Paid".equals(filterStatus)){
              	 notifications = notificationRepository.findAllByActiveAndValidToAfterAndDeletedIsNullOrderByWalletCreditsUsedDesc(Status,zonedateTime.minusDays(1),filterCategory);
               }
        	 else if("DELETED".equals(filterStatus)){
        		 notifications =  notificationRepository.findByDeletedIsNotNullOrderByCreatedDesc();
         	}
        	 else{
        		notifications = notificationRepository.findAllByActiveAndValidToAfterAndDeletedIsNullOrderByCreatedDesc(Status,zonedateTime.minusDays(1));
           	}
        	}
         
        }
        catch(Exception e){
        	log.error("Error While fetching the notifications ",e);
        }
        return notifications;
            
   }

    /**
     * GET  /notifications/:id -> get the "id" notification.
     */
    @RequestMapping(value = "/notifications/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Notification> getNotification(@PathVariable String id) {
        log.debug("REST request to get Notification : {}", id);
        Notification notification = notificationRepository.findOne(id);
        return Optional.ofNullable(notification)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    /**
     * suspend/delete  /notifications/:id -> block the "id" notification.
     */
    @RequestMapping(value = "/notifications/suspend/{id}",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> blockNotification(@PathVariable("id") String id) {
    	log.info("notification block id and rollback the points in provider table"+id);
    	Consumer result=null;
    	Provider provider=null;
    	dbNotification = notificationRepository.findOne(id);
    	if(dbNotification!=null){
    	String consumerid=dbNotification.getConsumerId();
    	Double freecredit=dbNotification.getFreeCreditsUsed();
    	Double walletcredit=dbNotification.getWalletCreditsUsed();	
        dbProvider=providerRepository.findOneByConsumerId(consumerid);
        log.info("database provider getting the roll backing points"+dbProvider);
        dbProvider.setMonthly_free_credits((dbProvider.getMonthly_free_credits())+freecredit);
        dbProvider.setWallet_credits((dbProvider.getWallet_credits())+walletcredit); 
        providerRepository.save(dbProvider);
        dbNotification.setDeleted(ZonedDateTime.now());
        notificationRepository.save(dbNotification);
    	}
        result = consumerRepository.findOneById(dbNotification.getConsumerId());
        if(result!=null){
        result.setActive(false);
        log.debug("Select by ID------------"+result);
        consumerRepository.save(result);
        }
        provider=providerRepository.findOneByConsumerId(dbNotification.getConsumerId());
        if(provider!=null){
        	
        	provider.setActive(false);
        	providerRepository.save(provider);
        }
        return ResponseEntity.ok().headers(HeaderUtil.rollBackAlert("notification", id.toString())).build();
    }

    /**
     * DELETE  /notifications/:id -> delete the "id" notification.
     */
    @RequestMapping(value = "/notifications/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteNotification(@PathVariable String id) {
        log.debug("REST request to delete Notification : {}", id);
        dbNotification = notificationRepository.findOne(id);
        if(dbNotification!=null){
        	String consumerid=dbNotification.getConsumerId();
        	Double freecredit=dbNotification.getFreeCreditsUsed();
        	Double walletcredit=dbNotification.getWalletCreditsUsed();	
            dbProvider=providerRepository.findOneByConsumerId(consumerid);
            log.info("database provider getting the roll backing points"+dbProvider);
            dbProvider.setMonthly_free_credits((dbProvider.getMonthly_free_credits())+freecredit);
            dbProvider.setWallet_credits((dbProvider.getWallet_credits())+walletcredit); 
            providerRepository.save(dbProvider);
        dbNotification.setDeleted(ZonedDateTime.now());
        notificationRepository.save(dbNotification);
        }
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("notification", id.toString())).build();
    }
    
    /**
     * GET get all notification posted by particular consumer in mobile
     * @throws URISyntaxException 
     * @throws ParseException 
     */
    @RequestMapping(value = "/getMyNotifications/{consumerId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public String getMyNotifications(@PathVariable("consumerId") String consumerId,@RequestParam("lastRecordDate") String lastRecordDate) throws URISyntaxException,JSONException, ParseException{
    	log.debug("REST request to get all notifications posted by particular consumer");
    	List<JsonObject> consumerNotifications = notificationService.getAllNotificationByConsumerId(consumerId,lastRecordDate);
    	if(StringUtils.isEmpty(consumerNotifications)) {
    		return null;
    	}
    	
    	return consumerNotifications.toString();
    }
    /**
     * GET get all notification posted by particular consumer in admin screen
     * @throws URISyntaxException 
     */
    @RequestMapping(value = "/getConsumerNotifications/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Notification> getconsumerNotifications(@PathVariable("id") String  id) throws URISyntaxException{
    	log.debug("REST request to get all notifications posted by particular consumer");
    	
    	List<Notification> notification= notificationService.getconsumerNotificationByConsumerId(id);
		return notification;
    	
    	
    }
    
    /**
     * GET get all notification posted by particular consumer
     * @throws URISyntaxException 
     * @throws JSONException 
     */
    @RequestMapping(value = "/getNotificationDetails/{notificationId}/{consumerId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public String getNotificationDetails(@PathVariable("consumerId") String consumerId, @PathVariable("notificationId") String notificationId) throws URISyntaxException{
    	log.debug("REST request to get notification detail");
    	JsonObject jsonObject = notificationService.getNotificationDetails(consumerId, notificationId);
    	if(jsonObject != null && !jsonObject.isEmpty()){
    		return jsonObject.toString();
    	}else{
    		return null;
    	}
    }
    /**
     * GET get all hotOffer posted by particular provider
     * @throws URISyntaxException 
     * @throws ParseException 
     * @throws JSONException 
     */
    @RequestMapping(value = "/getHotOffers", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public NotificationResult getNotificationDetails(@RequestBody Consumer consumer) throws URISyntaxException, ParseException, JSONException{
    	if(consumer.getId() == null || consumer.getLocation() == null || consumer.getLocation().isEmpty()){
    		log.error("Consumer id or location is empty or null");
    		return null;
    	}
    	
    	
    	return notificationService.getHotOffers(consumer);
    	
    }
    
    /**
     * GET all fake notification
     */
    
    @RequestMapping(value = "/getFakeNotifications", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Notification>> getFackeNotification(){
    	
    	log.debug("REST request to get all fake notificaion");
    	List<Notification> notifications = notificationRepository.findByOffensiveEquals("true");
    	return Optional.ofNullable(notifications)
                .map(result -> new ResponseEntity<>(
                    result,
                    HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
