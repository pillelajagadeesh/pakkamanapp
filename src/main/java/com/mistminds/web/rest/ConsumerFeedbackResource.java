package com.mistminds.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.mistminds.config.Constants;
import com.mistminds.domain.ConsumerFeedback;
import com.mistminds.domain.Welcome;
import com.mistminds.repository.ConsumerFeedbackRepository;
import com.mistminds.service.ConsumerFeedbackService;
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
 * REST controller for managing ConsumerFeedback.
 */
@RestController
@RequestMapping("/api")
public class ConsumerFeedbackResource {

    private final Logger log = LoggerFactory.getLogger(ConsumerFeedbackResource.class);
        
    @Inject
    private ConsumerFeedbackRepository consumerFeedbackRepository;
    
    @Inject
    private ConsumerFeedbackService consumerFeedbackService;
    
    Welcome welcome;
    
    /**
     * POST  /consumerFeedbacks -> Create a new consumerFeedback.
     */
    @RequestMapping(value = "/consumerFeedbacks",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ConsumerFeedback> createConsumerFeedback(@RequestBody ConsumerFeedback consumerFeedback) throws URISyntaxException {
        log.debug("REST request to save ConsumerFeedback : {}", consumerFeedback);
        if (consumerFeedback.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("consumerFeedback", "idexists", "A new consumerFeedback cannot already have an ID")).body(null);
        }
        ConsumerFeedback result = consumerFeedbackRepository.save(consumerFeedback);
        return ResponseEntity.created(new URI("/api/consumerFeedbacks/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("consumerFeedback", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /consumerFeedbacks -> Updates an existing consumerFeedback.
     */
    @RequestMapping(value = "/consumerFeedbacks",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ConsumerFeedback> updateConsumerFeedback(@RequestBody ConsumerFeedback consumerFeedback) throws URISyntaxException {
        log.debug("REST request to update ConsumerFeedback : {}", consumerFeedback);
        if (consumerFeedback.getId() == null) {
            return createConsumerFeedback(consumerFeedback);
        }
        ConsumerFeedback result = consumerFeedbackRepository.save(consumerFeedback);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("consumerFeedback", consumerFeedback.getId().toString()))
            .body(result);
    }

    /**
     * GET  /consumerFeedbacks -> get all the consumerFeedbacks.
     */
    @RequestMapping(value = "/consumerFeedbacks",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<ConsumerFeedback> getAllConsumerFeedbacks() {
        log.debug("REST request to get all ConsumerFeedbacks");
        return consumerFeedbackRepository.findAll();
            }

    /**
     * GET  /consumerFeedbacks/:id -> get the "id" consumerFeedback.
     */
    @RequestMapping(value = "/consumerFeedbacks/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ConsumerFeedback> getConsumerFeedback(@PathVariable String id) {
        log.debug("REST request to get ConsumerFeedback : {}", id);
        ConsumerFeedback consumerFeedback = consumerFeedbackRepository.findOne(id);
        return Optional.ofNullable(consumerFeedback)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /consumerFeedbacks/:id -> delete the "id" consumerFeedback.
     */
    @RequestMapping(value = "/consumerFeedbacks/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteConsumerFeedback(@PathVariable String id) {
        log.debug("REST request to delete ConsumerFeedback : {}", id);
        consumerFeedbackRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("consumerFeedback", id.toString())).build();
    }
    
    /**
     * POST /addConsumerFeedBack -> add consumer feedBack on particular notification
     */
    @RequestMapping(value = "/addConsumerFeedBack", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Welcome> postConsumerFeedback(@RequestBody ConsumerFeedback consumerFeedback){

    	log.debug("REST api to add consumer feedback");
    	welcome = new Welcome();
    	boolean result = consumerFeedbackService.addConsumerFeedback(consumerFeedback);
    	if(result){
    		welcome.setMessage(Constants.SUCCESS_RESULT);
    	}else{
    		welcome.setMessage(Constants.FAILURE_RESULT);
    	}
    	return ResponseEntity.ok()
    	        .headers(HeaderUtil.createEntityUpdateAlert("consumerfeedback",null))
    	        .body(welcome);
    }
    
    /**
     * POST /addConsumerComments -> add consumer comment on particular notification
     */
    @RequestMapping(value = "/postConsumerComment", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<ConsumerFeedback> postConsumerCommennt (@RequestBody ConsumerFeedback consumerFeedback){

    	log.debug("REST api to add consumer feedback");
    	return consumerFeedbackService.postConsumerComment(consumerFeedback);
    }
}
