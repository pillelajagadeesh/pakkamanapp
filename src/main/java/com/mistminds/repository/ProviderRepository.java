package com.mistminds.repository;

import java.util.List;



import org.springframework.data.geo.Circle;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.mistminds.domain.Provider;

/**
 * 
 * @author Jitesh
 * Spring data MongoDB repository for Category entity
 */

public interface ProviderRepository extends MongoRepository<Provider,String>{
	public Provider findOneByConsumerId(String id);
	public Provider findOneById(String id);
	public List<Provider> findByLocationWithin(Circle c);



}