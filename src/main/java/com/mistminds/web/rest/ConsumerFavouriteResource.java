package com.mistminds.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.hazelcast.com.eclipsesource.json.JsonObject;
import com.mistminds.domain.ConsumerFavourite;
import com.mistminds.domain.Welcome;
import com.mistminds.repository.ConsumerFavouriteRepository;
import com.mistminds.service.ConsumerFavoriteService;
import com.mistminds.web.rest.util.HeaderUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.ListUtils;

import javax.inject.Inject;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing ConsumerFavourite.
 */
@RestController
@RequestMapping("/api")
public class ConsumerFavouriteResource {

    private final Logger log = LoggerFactory.getLogger(ConsumerFavouriteResource.class);
        
    @Inject
    private ConsumerFavouriteRepository consumerFavouriteRepository;
    
    @Inject
    private ConsumerFavoriteService consumerFavouriteService;
    
    private Welcome welcome;
    
    /**
     * POST  /consumerFavourites -> Create a new consumerFavourite.
     */
    @RequestMapping(value = "/consumerFavourites",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ConsumerFavourite> createConsumerFavourite(@RequestBody ConsumerFavourite consumerFavourite) throws URISyntaxException {
        log.debug("REST request to save ConsumerFavourite : {}", consumerFavourite);
        if (consumerFavourite.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("consumerFavourite", "idexists", "A new consumerFavourite cannot already have an ID")).body(null);
        }
        consumerFavourite.setCreated(ZonedDateTime.now());
        ConsumerFavourite result = consumerFavouriteRepository.save(consumerFavourite);
        return ResponseEntity.created(new URI("/api/consumerFavourites/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("consumerFavourite", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /consumerFavourites -> Updates an existing consumerFavourite.
     */
    @RequestMapping(value = "/consumerFavourites",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ConsumerFavourite> updateConsumerFavourite(@RequestBody ConsumerFavourite consumerFavourite) throws URISyntaxException {
        log.debug("REST request to update ConsumerFavourite : {}", consumerFavourite);
        if (consumerFavourite.getId() == null) {
            return createConsumerFavourite(consumerFavourite);
        }
        consumerFavourite.setLastUpdate(ZonedDateTime.now());
        ConsumerFavourite result = consumerFavouriteRepository.save(consumerFavourite);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("consumerFavourite", consumerFavourite.getId().toString()))
            .body(result);
    }

    /**
     * GET  /consumerFavourites -> get all the consumerFavourites.
     */
    @RequestMapping(value = "/consumerFavourites",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<ConsumerFavourite> getAllConsumerFavourites() {
        log.debug("REST request to get all ConsumerFavourites");
        return consumerFavouriteRepository.findAll();
            }

    /**
     * GET  /consumerFavourites/:id -> get the "id" consumerFavourite.
     */
    @RequestMapping(value = "/consumerFavourites/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ConsumerFavourite> getConsumerFavourite(@PathVariable String id) {
        log.debug("REST request to get ConsumerFavourite : {}", id);
        ConsumerFavourite consumerFavourite = consumerFavouriteRepository.findOne(id);
        return Optional.ofNullable(consumerFavourite)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /consumerFavourites/:id -> delete the "id" consumerFavourite.
     */
    @RequestMapping(value = "/consumerFavourites/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteConsumerFavourite(@PathVariable String id) {
        log.debug("REST request to delete ConsumerFavourite : {}", id);
        consumerFavouriteRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("consumerFavourite", id.toString())).build();
    }
    
    /**
     * POST  /addFavourite -> add consumer favorite.
     * @throws URISyntaxException 
     */
    @RequestMapping(value = "/addFavourite",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Welcome> addConsumerFavourite(@RequestBody ConsumerFavourite consumerFavourite) throws URISyntaxException {
        log.debug("REST request to add ConsumerFavourite : {}", consumerFavourite.getId());
        welcome = consumerFavouriteService.addFavorite(consumerFavourite);
        return ResponseEntity.created(new URI("/api/addFavourite/" +""))
                .headers(HeaderUtil.createEntityCreationAlert("consumerFavourite", ""))
                .body(welcome);
    }
    @RequestMapping(value = "/getAllProviderinfo/{Id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public String getMyprovider(@PathVariable("Id") String Id) throws URISyntaxException{
    	log.debug("REST request to get all fav provider ");
    	List<JsonObject> favProviderList  = consumerFavouriteService.getAllconsumerdetails(Id);
    	if (!ListUtils.isEmpty(favProviderList)) {
    		return favProviderList.toString();
    	}
    	return null;
    }
    
   
}
