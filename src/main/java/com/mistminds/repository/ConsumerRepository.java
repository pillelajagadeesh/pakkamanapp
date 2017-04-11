package com.mistminds.repository;

import java.util.List;

import com.mistminds.domain.Category;
import com.mistminds.domain.Consumer;
import com.mistminds.domain.DeviceInfo;
import com.mistminds.domain.Notification;

import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the Consumer entity.
 */
public interface ConsumerRepository extends MongoRepository<Consumer,String> {
	
	public Consumer findOneById(String id);
	
	public Consumer findByMobile(String mobileNumber);
	
	public int countByIdAndOtp(String Id, String Otp);
	
	public List<Consumer> findByIdIn(List<String> categoryId);
	
	public List<Consumer> findByLocationWithin(Circle circle);

}
