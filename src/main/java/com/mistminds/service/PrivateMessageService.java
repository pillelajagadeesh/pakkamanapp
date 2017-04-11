package com.mistminds.service;

import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mistminds.config.Constants;
import com.mistminds.domain.Consumer;
import com.mistminds.domain.Notification;
import com.mistminds.domain.PrivateMessage;
import com.mistminds.domain.Provider;
import com.mistminds.domain.Welcome;
import com.mistminds.domain.util.Util;
import com.mistminds.repository.ConsumerRepository;
import com.mistminds.repository.NotificationRepository;
import com.mistminds.repository.PrivateMessageRepository;
import com.mistminds.repository.ProviderRepository;

@Service
public class PrivateMessageService {

	private final Logger log = LoggerFactory.getLogger(PrivateMessageService.class);
	
	@Inject
	SNSMobilePush snsMobilePush;
	
	@Inject
	private PrivateMessageRepository privateMessageRepository;
	
	@Inject
	private ConsumerRepository consumerRepository;
	
	@Inject
	private ProviderRepository providerRepository;
	
	@Inject
	private NotificationRepository notificationRepository;
	
	private PrivateMessage dbPrivateMessage;
	
	private Notification dbNotification;
	
	private Consumer dbConsumer;
	
	private Provider dbProvider;
	private Welcome welcome;
	
	private Map<String, String> pushData;
	
	public Welcome postMessage(PrivateMessage privateMessage){
		
		String deviceId, notificationTitle,imageUrl;
		Consumer recieverConsumer;
		welcome = new Welcome();
		if(privateMessage.getSenderId() != null && !privateMessage.getSenderId().isEmpty() && !Util.checkOffensive(privateMessage.getMessage())
		&& privateMessage.getReceiverId() != null && !privateMessage.getReceiverId().isEmpty() 
		&& privateMessage.getNotificationId() != null && !privateMessage.getNotificationId().isEmpty()){
			dbConsumer = consumerRepository.findOne(privateMessage.getSenderId());
			dbProvider =providerRepository.findOneByConsumerId(dbConsumer.getId());
			if(dbConsumer != null && dbConsumer.isActive()){
				recieverConsumer = consumerRepository.findOne(privateMessage.getReceiverId());
				dbNotification = notificationRepository.findOne(privateMessage.getNotificationId());
				if(recieverConsumer != null && dbNotification != null){
					deviceId = recieverConsumer.getDevice_info().getDeviceId();
					notificationTitle = dbNotification.getTitle();
					imageUrl=dbNotification.getImageUrls()[0];
					privateMessage.setCreated(ZonedDateTime.now());
					privateMessageRepository.save(privateMessage);
					String mobile=dbConsumer.getMobile().substring(dbConsumer.getMobile().length() - 3,dbConsumer.getMobile().length());
					String consumermobile=""+"*******"+mobile;
					publishPrivateMessage(privateMessage, deviceId, notificationTitle,imageUrl,consumermobile,dbProvider.getName(),dbProvider.getImageUrl());
					welcome.setMessage(Constants.SUCCESS_RESULT);
				}
			}else{
				welcome.setMessage(Constants.FAILURE_RESULT);
			}
			
		}else{
			welcome.setMessage(Constants.FAILURE_RESULT);
		}
		return welcome;
	}
	
	public List<PrivateMessage> getPrivateMessage(PrivateMessage privateMessage){
		
		log.debug("Inside getPrivate message to get all message between two user");
		if(privateMessage.getNotificationId() != null && !privateMessage.getNotificationId().isEmpty() &&
			privateMessage.getReceiverId() != null && !privateMessage.getReceiverId().isEmpty() &&
			privateMessage.getSenderId() != null && !privateMessage.getSenderId().isEmpty()){
			return privateMessageRepository.findByNotificationIdAndSenderIdAndReceiverIdOrNotificationIdAndReceiverIdAndSenderId(privateMessage.getNotificationId(), privateMessage.getSenderId(), privateMessage.getReceiverId(), privateMessage.getNotificationId(), privateMessage.getSenderId(), privateMessage.getReceiverId());
		}else{
			return null;
		}
	}
	
	public Welcome messageRead(PrivateMessage privateMessage){
		log.debug("Inside method message read of PrivateMessageService class");
		
		dbPrivateMessage = privateMessageRepository.findOne(privateMessage.getId());
		welcome = new Welcome();
		if(dbPrivateMessage != null){
			dbPrivateMessage.setRead(ZonedDateTime.now());
			privateMessageRepository.save(dbPrivateMessage);
			welcome.setMessage(Constants.SUCCESS_RESULT);
		}else{
			welcome.setMessage(Constants.FAILURE_RESULT);
		}
		return welcome;
	}
	
	public Welcome messageDelivered(PrivateMessage privateMessage){
		log.debug("Inside method delivered read of PrivateMessageService class");
		
		dbPrivateMessage = privateMessageRepository.findOne(privateMessage.getId());
		welcome = new Welcome();
		if(dbPrivateMessage != null){
			dbPrivateMessage.setDelivered(ZonedDateTime.now());
			privateMessageRepository.save(dbPrivateMessage);
			welcome.setMessage(Constants.SUCCESS_RESULT);
		}else{
			welcome.setMessage(Constants.FAILURE_RESULT);
		}
		return welcome;
	}
	
	public void publishPrivateMessage(PrivateMessage privateMessage, String installationId, String notificationTitle,String imageUrl, String mobile, String Sendername, String providerImageUrl){
		try {
			pushData = new HashMap<String, String>();
			pushData.put("installationId", installationId);
			pushData.put("title", notificationTitle);
			pushData.put("message", privateMessage.getMessage());
			pushData.put("notification_id", privateMessage.getNotificationId());
			pushData.put("category", "private");
			pushData.put("imageUrl", imageUrl);
			pushData.put("mobile", mobile);
			pushData.put("sender_name", Sendername);
			pushData.put("providerImageUrl", providerImageUrl);
			pushData.put("validFrom", null);
			pushData.put("validTo", null);
			pushData.put("sender_id", privateMessage.getSenderId());
			snsMobilePush.pushNotification(pushData);

		} catch (Exception ex) {
			System.out.println("Exception while publish private message");
		}
	}
	
	public String dateFormat(){
		SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
		Date date = new Date();
		return outputDateFormat.format(date);
	}
}
