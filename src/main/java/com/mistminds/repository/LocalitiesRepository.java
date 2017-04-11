package com.mistminds.repository;

import com.mistminds.domain.Cities;
import com.mistminds.domain.Localities;

import java.util.List;

import org.springframework.data.geo.Circle;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the Localities entity.
 */
public interface LocalitiesRepository extends MongoRepository<Localities,String> {

	public List<Localities> findBycityname(String cityname);

	public List<Localities> findByLocalitylocationWithin(Circle circle1);


}
