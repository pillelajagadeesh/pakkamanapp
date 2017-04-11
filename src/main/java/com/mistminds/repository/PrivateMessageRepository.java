package com.mistminds.repository;

import java.util.List;

import com.mistminds.domain.PrivateMessage;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the PrivateMessage entity.
 */
public interface PrivateMessageRepository extends MongoRepository<PrivateMessage,String> {

	/*public List<PrivateMessage> findByNotificationIdAndSenderIdAndreceiverIdOrNotificationIdAndSenderIdAndreceiverId(String notificationId, String senderId, String recieverId, String notificationId1, String senderId1, String recieverId1);*/
	public List<PrivateMessage> findByNotificationIdAndSenderIdAndReceiverIdOrNotificationIdAndReceiverIdAndSenderId(String notificationId, String senderId, String recieverId, String notificationId1, String recieverId1, String senderId1);
}
