package com.mistminds.service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

import com.hazelcast.com.eclipsesource.json.JsonObject;
import com.mistminds.config.Constants;
import com.mistminds.domain.Consumer;
import com.mistminds.domain.ConsumerFeedback;
import com.mistminds.domain.Notification;
import com.mistminds.domain.NotificationAcknowledgement;
import com.mistminds.domain.Provider;
import com.mistminds.domain.Welcome;
import com.mistminds.repository.ConsumerRepository;
import com.mistminds.repository.NotificationAcknowledgementRepository;
import com.mistminds.repository.NotificationRepository;
import com.mistminds.repository.ProviderRepository;

@Service
public class NotificationAcknowledgementService {
	
	private Logger log = LoggerFactory.getLogger(NotificationAcknowledgementService.class);
	public static final double DEFAULT_CHECKIN_RADIUS= 10.0;
	@Inject
    private NotificationAcknowledgementRepository notificationAcknowledgementRepository;
	
	@Inject
	private ConsumerRepository consumerRepository;
	@Inject
	private NotificationRepository notificationRepository;
	@Inject
	private ProviderRepository providerRepository;


	private NotificationAcknowledgement dbNotificationAcknowledgement;
	
	private Welcome welcome;
	
	private Consumer dbConsumer;
	
	public boolean notificationAcknowledgment(String notificationId, String consumerId){
		
		log.debug("Inside notificationAcknowledgment method of NotificationAcknowledgementService class to avoid send "
				+ "duplication notification to user");
		NotificationAcknowledgement notificationAcknowledgement = notificationAcknowledgementRepository.findByConsumerIdAndNotificationId(consumerId, notificationId);
		boolean flag = false;
		if(notificationAcknowledgement == null){
			notificationAcknowledgement = new NotificationAcknowledgement();
			notificationAcknowledgement.setConsumerId(consumerId);
			notificationAcknowledgement.setNotificationId(notificationId);
			notificationAcknowledgement.setSent(ZonedDateTime.now());
			notificationAcknowledgementRepository.save(notificationAcknowledgement);
			flag = true;
		}
		return flag;
	}
	
	public Welcome notificationDelivered(NotificationAcknowledgement notificationAcknowledgement){
		
		log.debug("Inside notificationAcknowledgment method of NotificationAcknowledgementService class to check notification is"
				+ "delivered to user or not");
		welcome = new Welcome();
		if(notificationAcknowledgement.getConsumerId() != null && !notificationAcknowledgement.getConsumerId().isEmpty()){
			dbConsumer = consumerRepository.findOneById(notificationAcknowledgement.getConsumerId());
			if(dbConsumer != null){
				dbNotificationAcknowledgement = notificationAcknowledgementRepository.findByConsumerIdAndNotificationId(notificationAcknowledgement.getConsumerId(), notificationAcknowledgement.getNotificationId());
				if(dbNotificationAcknowledgement != null){
					dbNotificationAcknowledgement.setDelivered(ZonedDateTime.now());
					notificationAcknowledgementRepository.save(dbNotificationAcknowledgement);
					welcome.setMessage(Constants.SUCCESS_RESULT);
				}else{
					welcome.setMessage(Constants.FAILURE_RESULT);
				}
			}else{
				welcome.setMessage(Constants.FAILURE_RESULT);
			}
		}else{
			welcome.setMessage(Constants.FAILURE_RESULT);
		}
		return welcome;
	}
	
	public Welcome notificationRead(NotificationAcknowledgement notificationAcknowledgement){
		
		log.debug("Inside notificationAcknowledgment method of NotificationAcknowledgementService class to check notification is"
				+ "delivered to user or not");
		welcome = new Welcome();
		
		if(notificationAcknowledgement.getConsumerId() != null && !notificationAcknowledgement.getConsumerId().isEmpty()){
			dbConsumer = consumerRepository.findOneById(notificationAcknowledgement.getConsumerId());
			if(dbConsumer != null){
				dbNotificationAcknowledgement = notificationAcknowledgementRepository.findByConsumerIdAndNotificationId(notificationAcknowledgement.getConsumerId(), notificationAcknowledgement.getNotificationId());
				if(dbNotificationAcknowledgement != null){
					dbNotificationAcknowledgement.setRead(ZonedDateTime.now());
					notificationAcknowledgementRepository.save(dbNotificationAcknowledgement);
					welcome.setMessage(Constants.SUCCESS_RESULT);
				}else{
					welcome.setMessage(Constants.FAILURE_RESULT);
				}
			}else{
				welcome.setMessage(Constants.FAILURE_RESULT);
			}
		}else{
			welcome.setMessage(Constants.FAILURE_RESULT);
		}
		return welcome;
	}
	
	public JSONObject notificationReadDeliveredAndSendCount(String notificationId, String consumerId) throws JSONException{
		log.debug("Inside notificationReadDeliveredAndSendCount method of NotificationAcknowledgementService class to count for how many user receive, read and send notification");
		if(StringUtils.isBlank(consumerId) || StringUtils.isBlank(notificationId)){
			log.info("consumerId:"+consumerId+" or notificationId:"+notificationId +" is null or empty");
			return null;
		}
		JSONObject json=new JSONObject();
		int readCount = 0, deliveredCount = 0, sendCount = 0, checkInCount = 0;
		JSONArray jsonarray=new JSONArray();
		Notification dbNotification = notificationRepository.findOneById(notificationId);
		if(dbNotification==null){
			log.info("dbNotification is not available for notificationId:"+notificationId);
			return null;
		}
		Provider dbProvider = providerRepository.findOneByConsumerId(consumerId);
		if(dbProvider==null){
			log.info("dbProvider is not available for consumerId:"+consumerId);
			return null;
		}

		Consumer dbConsumer = consumerRepository.findOneById(consumerId);
		if(dbConsumer == null){
			log.info("dbConsumer is not available for consumerId:"+consumerId);
			return null;
		}
		List<NotificationAcknowledgement>  listNotificationAcknowledgement = notificationAcknowledgementRepository.findByNotificationId(notificationId);

		if(listNotificationAcknowledgement.isEmpty()){
			log.info("list of NotificationAcknowledgement is empty");
			return null;
		}
		List<String> listOfConsumerId =  new ArrayList<String>();
		for(NotificationAcknowledgement notificationAck : listNotificationAcknowledgement){
			listOfConsumerId.add(notificationAck.getConsumerId());

			if(notificationAck.getDelivered() !=null){
				deliveredCount++;
			}
			if(notificationAck.getRead() != null){
				readCount++;
			}
			if(notificationAck.getSent() != null){
				sendCount++;
			}
			if(notificationAck.getCheckIn() != null){
				checkInCount++;
			}

			json.put("read", readCount);
			json.put("delivered", deliveredCount);
			json.put("send", sendCount);
			json.put("checkIn", checkInCount);

		}
		List<Consumer> consumer = consumerRepository.findByIdIn(listOfConsumerId);
		for(Consumer cum:consumer){
				jsonarray.put(cum.getLocation());
		}
		json.put("consumerLocation",jsonarray);
		json.put("providerLocation",dbProvider.getLocation());

		return json;
	}
	
	
	public JsonObject notificationReadCount(String notificationId, String consumerId) throws JSONException{
		log.debug("Inside notificationReadDeliveredAndSendCount method of NotificationAcknowledgementService class to count for how many user read notification");
		if(StringUtils.isBlank(consumerId) || StringUtils.isBlank(notificationId)){
			log.info("consumerId:"+consumerId+" or notificationId:"+notificationId +" is null or empty");
			return null;
		}
		JsonObject json=new JsonObject();
		int readCount = 0;
		Notification dbNotification = notificationRepository.findOneById(notificationId);
		if(dbNotification==null){
			log.info("dbNotification is not available for notificationId:"+notificationId);
			return null;
		}
		Provider dbProvider = providerRepository.findOneByConsumerId(consumerId);
		if(dbProvider==null){
			log.info("dbProvider is not available for consumerId:"+consumerId);
			return null;
		}

		Consumer dbConsumer = consumerRepository.findOneById(consumerId);
		if(dbConsumer == null){
			log.info("dbConsumer is not available for consumerId:"+consumerId);
			return null;
		}
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
			json.add("read", readCount);
		}
		return json;
	}
	
	public Welcome consumerCheckIn(Notification notification) {
		welcome = new Welcome();
		Double latitude, longitude;
		Notification dbNotification = notificationRepository.findOneById(notification.getId());
		Provider dbProvider=	providerRepository.findOneByConsumerId(dbNotification.getConsumerId());
		if(dbNotification!=null && dbProvider!=null){
		dbNotificationAcknowledgement = notificationAcknowledgementRepository.findByConsumerIdAndNotificationId(notification.getConsumerId(),dbNotification.getId());
		if(dbNotificationAcknowledgement!=null){
		Double defaultRedius =DEFAULT_CHECKIN_RADIUS;
		List<Double> consumerLocation = notification.getLocation();
		latitude = consumerLocation.get(0);
		longitude = consumerLocation.get(1);
		Point center = new Point(latitude, longitude);
		Double radius = defaultRedius / 1000;
		Distance distance = new Distance(Math.toDegrees(radius / 6378.137));
		Circle circle = new Circle(center, distance);
		List<Provider> provider = providerRepository.findByLocationWithin(circle);
		for (Provider nearestProvider : provider){
			if(nearestProvider!=null){
			if(dbProvider.getId().equals(nearestProvider.getId())){
				dbNotificationAcknowledgement.setCheckIn(ZonedDateTime.now());
				notificationAcknowledgementRepository.save(dbNotificationAcknowledgement);
				welcome.setMessage(Constants.SUCCESS_RESULT);
				break;
			}
			else{
				welcome.setMessage(Constants.FAILURE_RESULT);
			}
			
		}
			else{
				welcome.setMessage(Constants.FAILURE_RESULT);
			}
		}
	    }
	else{
			welcome.setMessage(Constants.FAILURE_RESULT);
		}
		}
		else{
			welcome.setMessage(Constants.FAILURE_RESULT);
		}
		return welcome;
	}

		
}






