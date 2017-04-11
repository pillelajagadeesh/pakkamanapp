package com.mistminds.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.mistminds.domain.Consumer;
import com.mistminds.domain.Provider;
import com.mistminds.domain.Region;
import com.mistminds.domain.Welcome;
import com.mistminds.repository.RegionRepository;
import com.mistminds.service.RegionService;
import com.mistminds.web.rest.util.BanerImageResult;
import com.mistminds.web.rest.util.HeaderUtil;
import com.mistminds.web.rest.util.NotificationResult;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Region.
 */
@RestController
@RequestMapping("/api")
public class RegionResource {

    private final Logger log = LoggerFactory.getLogger(RegionResource.class);
        
    @Inject
    private RegionRepository regionRepository;
    
    @Inject
    private RegionService regionService;
    
    /**
     * POST  /regions -> Create a new region.
     */
    @RequestMapping(value = "/regions",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Region> createRegion(@RequestBody Region region) throws URISyntaxException {
        log.debug("REST request to save Region : {}", region);
        if (region.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("region", "idexists", "A new region cannot already have an ID")).body(null);
        }
        
        boolean flag = regionService.addConsumerRegion(region);
        if(flag){
        Region result = regionRepository.save(region);
        return ResponseEntity.created(new URI("/api/regions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("region", result.getId().toString()))
            .body(result);
        }else{
        	return ResponseEntity.created(new URI("/api/regions/" + ""))
                    .headers(HeaderUtil.createEntityCreationAlert("region", ""))
                    .body(null);
        }
    }

    /**
     * PUT  /regions -> Updates an existing region.
     */
    @RequestMapping(value = "/regions",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Region> updateRegion(@RequestBody Region region) throws URISyntaxException {
        log.debug("REST request to update Region : {}", region);
        if (region.getId() == null) {
            return createRegion(region);
        }
        Region result = regionRepository.save(region);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("region", region.getId().toString()))
            .body(result);
    }

    /**
     * GET  /regions -> get all the regions.
     */
    @RequestMapping(value = "/regions",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Region> getAllRegions() {
        log.debug("REST request to get all Regions");
        return regionRepository.findAll();
            }

    /**
     * GET  /regions/:id -> get the "id" region.
     */
    @RequestMapping(value = "/regions/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Region> getRegion(@PathVariable String id) {
        log.debug("REST request to get Region : {}", id);
        Region region = regionRepository.findOne(id);
        return Optional.ofNullable(region)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /regions/:id -> delete the "id" region.
     */
    @RequestMapping(value = "/regions/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteRegion(@PathVariable String id) {
        log.debug("REST request to delete Region : {}", id);
        regionRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("region", id.toString())).build();
    }
    
    /**
     * POST /regionNotification -> send notification is user is entering in different regions
     */
    
    @RequestMapping(value = "/sendRegionNotification", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Welcome> sendRegionNotificaion(@RequestBody Consumer consumer){
    	
    	log.debug("REST request to send regions push notification to user");
    	
    	if(consumer.getId() == null || consumer.getLocation() == null || consumer.getLocation().isEmpty()){
    		log.error("Consumer id or location is empty or null");
    		return null;
    	}
    	Welcome welcome = regionService.sendRegionNotification(consumer);
    	return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("regionNotification", ""))
                .body(welcome);
    	
    }
    
    
    /**
     * POST / getConsumerNotification -> get on demand consumer notification 
     * @param consumerId
     * @param lat
     * @param lon
     */
    @SuppressWarnings("deprecation")
	//TODO - change the name to getNearByNotifications
    //change the requestbody from Consumer to json string
    @RequestMapping(value = "/getNearByNotifications", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Provider> getConsumerNotification(@RequestBody Consumer consumer){ // read as a normal json string
    	
    	log.debug("REST reguest to get consumer on demand notification for " + consumer.getId() + " with long and lat as "+ consumer.getLocation());
    	
    	if(consumer.getId() == null || consumer.getLocation() == null || consumer.getLocation().isEmpty()){
    		return null;
    	}
    	
    	
    	return regionService.getNearByProviderNotification(consumer);
    	

    }
	
	/**
     * POST / getNotifications -> get on demand consumer notification 
     * @param consumerId
     * @param lat
     * @param lon
     * @throws ParseException 
     * @throws JSONException 
     */
    @RequestMapping(value = "/getNotificationsNearBy", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public NotificationResult getNotifications(@RequestBody Consumer consumer) throws ParseException, JSONException{ // read as a normal json string
    	
    	log.debug("REST reguest to get consumer on demand notification for " + consumer.getId() + " with long and lat as "+ consumer.getLocation());
    	
    	if(consumer.getId() == null || consumer.getLocation() == null || consumer.getLocation().isEmpty()){
    		log.error("Consumer id or location is empty or null");
    		return null;
    	}
    	
    	
    	return regionService.getNearByNotifications(consumer);
    	

    }
    
    
    //API to get the near by home/category banner images 
    @RequestMapping(value = "/getNearByBannerImages", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<BanerImageResult> getConsumerBannerImages(@RequestBody Consumer consumer){ // read as a normal json string
    	
    	log.debug("REST reguest to get home/category banner images from notification for consumer id:" + consumer.getId() + " with long and lat as "+ consumer.getLocation());
    	if(consumer.getId() == null || consumer.getLocation() == null || consumer.getLocation().isEmpty()){
    		log.error("Consumer id or location is empty or null");
    		return null;
    	}
    	if(StringUtils.isBlank(consumer.getCategoryId()) ){
    		return regionService.getNearByNotificationHomeBannerImages(consumer);	
    	}
    	else{
    	return regionService.getNearByNotificationCategoryBannerImages(consumer);
    	}

    }
}
