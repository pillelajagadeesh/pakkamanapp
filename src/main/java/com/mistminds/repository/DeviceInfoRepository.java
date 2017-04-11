package com.mistminds.repository;

import com.mistminds.domain.DeviceInfo;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the DeviceInfo entity.
 */
public interface DeviceInfoRepository extends MongoRepository<DeviceInfo,String> {

	DeviceInfo findBydeviceId(String deviceId);

	 public DeviceInfo findByConsumerId(String consumerid);

}
