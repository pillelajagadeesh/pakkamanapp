package com.mistminds.repository;

import java.util.List;

import com.mistminds.domain.NotificationAcknowledgement;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the NotificationAcknowledgement entity.
 */
public interface NotificationAcknowledgementRepository extends MongoRepository<NotificationAcknowledgement,String> {

	public NotificationAcknowledgement findByConsumerIdAndNotificationId(String consumerId, String notificationId);
	
	public List<NotificationAcknowledgement> findByNotificationId(String notificationId);
}
