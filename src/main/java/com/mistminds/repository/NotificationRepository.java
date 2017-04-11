package com.mistminds.repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import com.mistminds.domain.Notification;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

/**
 * Spring Data MongoDB repository for the Notification entity.
 */
public interface NotificationRepository extends
		MongoRepository<Notification, String> {

	public Notification findOneById(String id);

	public List<Notification> findByConsumerId(String consumerId);
	
	public List<Notification> findByConsumerIdAndActiveIsTrue(String consumerId);

	public List<Notification> findByOffensiveEquals(String offensive);

	public List<Notification> findByConsumerIdInAndCategoryNotInAndValidToAfterAndActiveIsTrue(
			Set<String> consumerIds, List<String> unsubscribeCategory,ZonedDateTime dateTime);
	
	public List<Notification> findByConsumerIdInAndCategoryIdNotInAndValidToAfterAndActiveIsTrue(
			String consumerIds, List<String> unsubscribeCategory,ZonedDateTime dateTime);

	public List<Notification> findByConsumerIdIn(Set<String> consumerIds);

	public List<Notification> findAllByValidToGreaterThan(String validTo);

	public List<Notification> findAllByOrderByIdDesc();
	public Notification findOneByIdAndValidToAfter(String notificationId,ZonedDateTime dateTime);
	
	/*@Query("{'categoryId' : ?0,'active': ?1}")*/
	public List<Notification> findByActiveAndValidToAfterAndCategoryIdInAndDeletedIsNull(Boolean status,ZonedDateTime dateTime,String categoryId);
	
	@Query("{'active': ?0}")
	public List<Notification> findAllActiveInactiveNotificationsAndDeletedNotInNotifications(Boolean status);
	
	//@Query("{'active': ?0}")
	public List<Notification> findAllByActiveAndValidToAfterAndCategoryIdInAndDeletedIsNullOrderByWalletCreditsUsedDesc(Boolean Status,ZonedDateTime dateTime,String parentIds);
	
	public List<Notification> findAllByActiveAndValidToAfterAndDeletedIsNullOrderByCreatedDesc(Boolean Status,ZonedDateTime dateTime);
	
	public List<Notification> findByDeletedIsNotNullOrderByCreatedDesc();

	public List<Notification> findByConsumerIdInAndCategoryIdInAndValidToAfterAndActiveIsTrue(
			String consumerId, List<String> subscribeCategory,
			ZonedDateTime dateTime);

	public List<Notification> findByConsumerIdInAndValidToAfterAndActiveIsTrueAndHomeBannerStatusIsTrue(
			String consumerId, ZonedDateTime dateTime);
	
	public List<Notification> findByConsumerIdInAndCategoryIdInAndValidToAfterAndActiveIsTrueAndCategoryBannerStatusIsTrue(
			String consumerId,String parentIds, ZonedDateTime dateTime);

	public Notification findOneByproviderId(String id);

	public List<Notification> findByValidToBeforeAndDeletedIsNullOrderByCreatedDesc(ZonedDateTime now);

	public List<Notification> findByValidToBeforeAndCategoryIdInAndDeletedIsNull(ZonedDateTime minusDays,String parentIds);

	public List<Notification> findAllByActiveAndValidToAfterAndDeletedIsNullOrderByWalletCreditsUsedDesc(boolean status,
			ZonedDateTime minusDays, String parentIds);

	public List<Notification> findByDeletedIsNotNullAndAndCategoryIdInOrderByCreatedDesc(String parentIds);

	public List<Notification> findByValidToBeforeAndMainCategoryIdInAndDeletedIsNull(ZonedDateTime minusDays,
			String filterMainCategory);

	public List<Notification> findAllByActiveAndValidToAfterAndMainCategoryIdInAndDeletedIsNullOrderByWalletCreditsUsedDesc(
			boolean status, ZonedDateTime minusDays, String filterMainCategory);

	public List<Notification> findByDeletedIsNotNullAndAndMainCategoryIdInOrderByCreatedDesc(String filterMainCategory);

	public List<Notification> findByActiveAndValidToAfterAndMainCategoryIdInAndDeletedIsNull(Boolean status,
			ZonedDateTime dateTime, String filterMainCategory);


	

	

	
	

}
