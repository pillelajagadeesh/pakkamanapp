package com.mistminds.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.mistminds.domain.ConsumerRegions;
import com.mistminds.domain.Region;
import com.mistminds.repository.ConsumerRegionsRepository;
import com.mistminds.service.ConsumerRegionService;
import com.mistminds.web.rest.util.HeaderUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing ConsumerRegions.
 */
@RestController
@RequestMapping("/api")
public class ConsumerRegionsResource {

    private final Logger log = LoggerFactory.getLogger(ConsumerRegionsResource.class);
        
    @Inject
    private ConsumerRegionsRepository consumerRegionsRepository;
    
    @Inject
    private ConsumerRegionService consumerRegionService;
    
    /**
     * POST  /consumerRegionss -> Create a new consumerRegions.
     */
    @RequestMapping(value = "/consumerRegionss",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ConsumerRegions> createConsumerRegions(@RequestBody ConsumerRegions consumerRegions) throws URISyntaxException {
        log.debug("REST request to save ConsumerRegions : {}", consumerRegions);
        if (consumerRegions.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("consumerRegions", "idexists", "A new consumerRegions cannot already have an ID")).body(null);
        }
        ConsumerRegions result = consumerRegionsRepository.save(consumerRegions);
        return ResponseEntity.created(new URI("/api/consumerRegionss/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("consumerRegions", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /consumerRegionss -> Updates an existing consumerRegions.
     */
    @RequestMapping(value = "/consumerRegionss",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ConsumerRegions> updateConsumerRegions(@RequestBody ConsumerRegions consumerRegions) throws URISyntaxException {
        log.debug("REST request to update ConsumerRegions : {}", consumerRegions);
        if (consumerRegions.getId() == null) {
            return createConsumerRegions(consumerRegions);
        }
        ConsumerRegions result = consumerRegionsRepository.save(consumerRegions);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("consumerRegions", consumerRegions.getId().toString()))
            .body(result);
    }

    /**
     * GET  /consumerRegionss -> get all the consumerRegionss.
     */
    @RequestMapping(value = "/consumerRegionss",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<ConsumerRegions> getAllConsumerRegionss() {
        log.debug("REST request to get all ConsumerRegionss");
        return consumerRegionsRepository.findAll();
            }

    /**
     * GET  /consumerRegionss/:id -> get the "id" consumerRegions.
     */
    @RequestMapping(value = "/consumerRegionss/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ConsumerRegions> getConsumerRegions(@PathVariable String id) {
        log.debug("REST request to get ConsumerRegions : {}", id);
        ConsumerRegions consumerRegions = consumerRegionsRepository.findOne(id);
        return Optional.ofNullable(consumerRegions)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /consumerRegionss/:id -> delete the "id" consumerRegions.
     */
    @RequestMapping(value = "/consumerRegionss/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteConsumerRegions(@PathVariable String id) {
        log.debug("REST request to delete ConsumerRegions : {}", id);
        consumerRegionsRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("consumerRegions", id.toString())).build();
    }
    
    /**
     * POST  /addDonsumerRegionss -> adding a region in consumer
     */
    @RequestMapping(value = "/addConsumerRegion",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Region>> addConsumerRegions(@RequestBody ConsumerRegions consumerRegions) {
        log.debug("REST request to add ConsumerRegions : {}");
        List<Region> region = consumerRegionService.addConsumerRegions(consumerRegions);

        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("consumerRegions", ""))
                .body(region);
    }
    
    /**
     * POST  /removeConsumerRegion -> removing a region in consumer
     */
    @RequestMapping(value = "/removeConsumerRegion",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Region>> removeConsumerRegions(@RequestBody ConsumerRegions consumerRegions) {
        log.debug("REST request to remove ConsumerRegions : {}");
        List<Region> region = consumerRegionService.removeConsumerRegions(consumerRegions);

        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("consumerRegions", ""))
                .body(region);
    }
    
}
