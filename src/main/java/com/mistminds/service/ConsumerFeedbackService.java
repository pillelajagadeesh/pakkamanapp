package com.mistminds.service;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mistminds.domain.Consumer;
import com.mistminds.domain.ConsumerFeedback;
import com.mistminds.domain.Notification;
import com.mistminds.domain.Provider;
import com.mistminds.domain.util.Util;
import com.mistminds.repository.ConsumerFeedbackRepository;
import com.mistminds.repository.ConsumerRepository;
import com.mistminds.repository.NotificationRepository;
import com.mistminds.repository.ProviderRepository;


@Service
public class ConsumerFeedbackService {

	private final Logger log = LoggerFactory.getLogger(ConsumerFeedbackService.class);
	
	@Inject
	private ConsumerFeedbackRepository  consumerFeedbackRepository;
	
	@Inject
	private ConsumerRepository consumerRepository;
	@Inject
    private ProviderRepository providerRepository;
	 @Inject
	 private NotificationRepository notificationRepository;
	private Consumer dbConsumer;
	private Notification dbNotification;
	
	private ConsumerFeedback dbConsumerFeedBack;
	
	public boolean addConsumerFeedback(ConsumerFeedback consumerFeedback){
		
		log.debug("Inside Consumer Feedback Service to add consumer feedback for particular notification");
		dbConsumer = consumerRepository.findOne(consumerFeedback.getConsumerId());
		if(dbConsumer != null && dbConsumer.isActive()){
			dbConsumerFeedBack = consumerFeedbackRepository.findByConsumerIdAndNotificationIdAndLikeDislike(consumerFeedback.getConsumerId(), 
					consumerFeedback.getNotificationId(), "0");
			if(dbConsumerFeedBack == null){
				dbConsumerFeedBack = consumerFeedbackRepository.findByConsumerIdAndNotificationIdAndLikeDislike(consumerFeedback.getConsumerId(), 
						consumerFeedback.getNotificationId(), "1");
			}
			//dbConsumerFeedBack = consumerFeedbackRepository.findByConsumerIdAndNotificationId(consumerFeedback.getConsumerId(), consumerFeedback.getNotificationId());
			if (dbConsumerFeedBack == null) {
				if (consumerFeedback.getLikeDislike().equalsIgnoreCase("true")) {
					consumerFeedback.setLikeDislike("1");
				}else if(consumerFeedback.getLikeDislike().equalsIgnoreCase("false")) {
					consumerFeedback.setLikeDislike("0");
				}
				consumerFeedbackRepository.save(consumerFeedback);
				return true;
			} else {
					if (consumerFeedback.getLikeDislike().equalsIgnoreCase("true")) {
						dbConsumerFeedBack.setLikeDislike("1");
						consumerFeedbackRepository.save(dbConsumerFeedBack);
						return true;
					}else if(consumerFeedback.getLikeDislike().equalsIgnoreCase("false")) {
						dbConsumerFeedBack.setLikeDislike("0");
						consumerFeedbackRepository.save(dbConsumerFeedBack);
						return true;
				}
			}
			
		}
		
		return true;
	}
	
	public List<ConsumerFeedback> postConsumerComment(ConsumerFeedback consumerFeedback){
		 String content;
		log.debug("Inside Consumer Feedback Service to add comment for particular notification");
		dbConsumer = consumerRepository.findOne(consumerFeedback.getConsumerId());
		Provider dbProvider = providerRepository.findOneByConsumerId(consumerFeedback.getConsumerId());
		if(dbProvider!=null){
			
			consumerFeedback.setProviderId(dbProvider.getId());
		}
		
		if(dbConsumer != null && dbConsumer.isActive()){

			if(consumerFeedback.getComment() != null && consumerFeedback.getComment() != ""){
				String comment=consumerFeedback.getComment();
				content=  ""+comment ;
				consumerFeedback.setOffensive( Util.checkOffensive(content));
				consumerFeedbackRepository.save(consumerFeedback);
			}else if(consumerFeedback.getShare() != null && consumerFeedback.getShare() != ""){
				dbConsumerFeedBack = consumerFeedbackRepository.findByConsumerIdAndNotificationIdAndShare(consumerFeedback.getConsumerId(), consumerFeedback.getNotificationId(), consumerFeedback.getShare());
				if(dbConsumerFeedBack == null){
					consumerFeedback.setCount(1);
					consumerFeedbackRepository.save(consumerFeedback);
				}else{
					int count = dbConsumerFeedBack.getCount() + 1;
					dbConsumerFeedBack.setCount(count);
					consumerFeedbackRepository.save(dbConsumerFeedBack);
				}
			}
			else if(consumerFeedback.getReportStatus() !=null && consumerFeedback.getReportStatus() !=""){
				
				dbNotification = notificationRepository.findOneById(consumerFeedback.getNotificationId());
				if(dbNotification.getSpamCount()!=null && dbNotification.getSpamCount()!=0 ) {
					int count = dbNotification.getSpamCount() + 1;
					dbNotification.setSpamCount(count);
					notificationRepository.save(dbNotification);
					
				}
				else{
					dbNotification.setSpamCount(1);
					notificationRepository.save(dbNotification);
				}
				consumerFeedback.setReportStatus(consumerFeedback.getReportStatus());
				consumerFeedbackRepository.save(consumerFeedback);
			}
		}
		return consumerFeedbackRepository.findAll();	
	}
}
