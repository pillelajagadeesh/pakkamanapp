package com.mistminds.service;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mistminds.domain.Consumer;
import com.mistminds.domain.ConsumerRegions;
import com.mistminds.domain.Region;
import com.mistminds.repository.ConsumerRegionsRepository;
import com.mistminds.repository.ConsumerRepository;

@Service
public class ConsumerRegionService{

	private Logger log = LoggerFactory.getLogger(ConsumerRegionService.class);
	
	@Inject
	private ConsumerRegionsRepository consumerRegionsRepository;
	
	@Inject
	private ConsumerRepository consumerRepository;
	
	private ConsumerRegions dbConsumerRegions;
	
	private Consumer dbConsumer;
	
	public List<Region> addConsumerRegions(ConsumerRegions consumerRegion) {

		boolean flag = true;
		log.debug("Inside addConsumerRegions method of ConsumerRegionService for adding consumer regions");
		try{
		dbConsumer = consumerRepository.findOne(consumerRegion.getConsumerId());
			if (dbConsumer != null) {
				dbConsumerRegions = consumerRegionsRepository.findByConsumerId(consumerRegion.getConsumerId());
				if (dbConsumerRegions != null) {
					List<Region> regions = dbConsumerRegions.getRegion();
					Region region = consumerRegion.getRegion().get(0);
					for(Region dbRegion : regions){
						if(dbRegion.getLocation() != null && Double.compare(dbRegion.getLocation().get(0), region.getLocation().get(0)) == 0 ? true : false)
								if(Double.compare(dbRegion.getLocation().get(1), region.getLocation().get(1)) == 0 ? true : false){
							flag = false;
							break;
						}
					}
					if(flag){
						regions.add(region);
					}
					dbConsumerRegions.setRegion(regions);
					consumerRegionsRepository.save(dbConsumerRegions);
				} else {
					consumerRegionsRepository.save(consumerRegion);
				}
		}
		dbConsumerRegions = consumerRegionsRepository.findByConsumerId(consumerRegion.getConsumerId());
		if(dbConsumerRegions != null){
				return dbConsumerRegions.getRegion();
			}else{
				return null;
			}
	}catch(NullPointerException npx){
		log.error("Null pointer exception " + npx);
		return null;
	}
	}
	
	public List<Region> removeConsumerRegions(ConsumerRegions consumerRegion) {

		List<Region> regions;
		log.debug("Inside addConsumerRegions method of ConsumerRegionService for remove consumer regions");
		dbConsumer = consumerRepository.findOne(consumerRegion.getConsumerId());
		if (dbConsumer != null) {
				dbConsumerRegions = consumerRegionsRepository.findByConsumerId(consumerRegion.getConsumerId());
				if (dbConsumerRegions != null) {
					regions = new ArrayList<Region>(); 
					for (Region region : dbConsumerRegions.getRegion()) {
						if(Double.compare(region.getLocation().get(0), consumerRegion.getRegion().get(0).getLocation().get(0)) == 0 && Double.compare(region.getLocation().get(1), consumerRegion.getRegion().get(0).getLocation().get(1)) == 0){
						} else {
							regions.add(region);
						}
					}
					dbConsumerRegions.setRegion(regions);
					consumerRegionsRepository.save(dbConsumerRegions);
					return regions;
				}
			}
		dbConsumerRegions = consumerRegionsRepository.findByConsumerId(consumerRegion.getConsumerId());
		if(dbConsumerRegions != null){
			return dbConsumerRegions.getRegion();
		}else{
			return null;
		}
	}
}
