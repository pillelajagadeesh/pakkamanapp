package com.mistminds.repository;

import java.util.List;

import com.mistminds.domain.ConsumerFeedback;
import com.mistminds.domain.Notification;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the ConsumerFeedback entity.
 */
public interface ConsumerFeedbackRepository extends MongoRepository<ConsumerFeedback,String> {

	public List<ConsumerFeedback> findBynotificationId(String notificationId);
	
	public ConsumerFeedback findByConsumerIdAndNotificationIdAndLikeDislike(String consumerId, String notificationId, String likeDislike);

	public ConsumerFeedback findByConsumerIdAndNotificationIdAndShare(String consumerId, String notificationId, String share);
	public List<ConsumerFeedback> findByNotificationIdIn(List<String> notificationId);

	
}