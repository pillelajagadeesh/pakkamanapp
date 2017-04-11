package com.mistminds.repository;

import com.mistminds.domain.ConsumerRegions;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the ConsumerRegions entity.
 */
public interface ConsumerRegionsRepository extends MongoRepository<ConsumerRegions,String> {

	public ConsumerRegions findByConsumerId(String consumerId);
}
