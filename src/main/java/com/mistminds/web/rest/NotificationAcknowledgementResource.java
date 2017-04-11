package com.mistminds.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.hazelcast.com.eclipsesource.json.JsonObject;
import com.mistminds.config.Constants;
import com.mistminds.domain.ConsumerFeedback;
import com.mistminds.domain.Notification;
import com.mistminds.domain.NotificationAcknowledgement;
import com.mistminds.domain.Welcome;
import com.mistminds.repository.NotificationAcknowledgementRepository;
import com.mistminds.service.NotificationAcknowledgementService;
import com.mistminds.web.rest.util.HeaderUtil;

import org.json.JSONException;
import org.json.JSONObject;
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
 * REST controller for managing NotificationAcknowledgement.
 */
@RestController
@RequestMapping("/api")
public class NotificationAcknowledgementResource {

    private final Logger log = LoggerFactory.getLogger(NotificationAcknowledgementResource.class);
        
    @Inject
    private NotificationAcknowledgementRepository notificationAcknowledgementRepository;
    
    @Inject
    private NotificationAcknowledgementService	notificationAcknowledgementService;
    
    /**
     * POST  /notificationAcknowledgements -> Create a new notificationAcknowledgement.
     */
    @RequestMapping(value = "/notificationAcknowledgements",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<NotificationAcknowledgement> createNotificationAcknowledgement(@RequestBody NotificationAcknowledgement notificationAcknowledgement) throws URISyntaxException {
        log.debug("REST request to save NotificationAcknowledgement : {}", notificationAcknowledgement);
        if (notificationAcknowledgement.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("notificationAcknowledgement", "idexists", "A new notificationAcknowledgement cannot already have an ID")).body(null);
        }
        NotificationAcknowledgement result = notificationAcknowledgementRepository.save(notificationAcknowledgement);
        return ResponseEntity.created(new URI("/api/notificationAcknowledgements/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("notificationAcknowledgement", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /notificationAcknowledgements -> Updates an existing notificationAcknowledgement.
     */
    @RequestMapping(value = "/notificationAcknowledgements",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<NotificationAcknowledgement> updateNotificationAcknowledgement(@RequestBody NotificationAcknowledgement notificationAcknowledgement) throws URISyntaxException {
        log.debug("REST request to update NotificationAcknowledgement : {}", notificationAcknowledgement);
        if (notificationAcknowledgement.getId() == null) {
            return createNotificationAcknowledgement(notificationAcknowledgement);
        }
        NotificationAcknowledgement result = notificationAcknowledgementRepository.save(notificationAcknowledgement);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("notificationAcknowledgement", notificationAcknowledgement.getId().toString()))
            .body(result);
    }

    /**
     * GET  /notificationAcknowledgements -> get all the notificationAcknowledgements.
     */
    @RequestMapping(value = "/notificationAcknowledgements",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<NotificationAcknowledgement> getAllNotificationAcknowledgements() {
        log.debug("REST request to get all NotificationAcknowledgements");
        return notificationAcknowledgementRepository.findAll();
            }

    /**
     * GET  /notificationAcknowledgements/:id -> get the "id" notificationAcknowledgement.
     */
    @RequestMapping(value = "/notificationAcknowledgements/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<NotificationAcknowledgement> getNotificationAcknowledgement(@PathVariable String id) {
        log.debug("REST request to get NotificationAcknowledgement : {}", id);
        NotificationAcknowledgement notificationAcknowledgement = notificationAcknowledgementRepository.findOne(id);
        return Optional.ofNullable(notificationAcknowledgement)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /notificationAcknowledgements/:id -> delete the "id" notificationAcknowledgement.
     */
    @RequestMapping(value = "/notificationAcknowledgements/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteNotificationAcknowledgement(@PathVariable String id) {
        log.debug("REST request to delete NotificationAcknowledgement : {}", id);
        notificationAcknowledgementRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("notificationAcknowledgement", id.toString())).build();
    }
    
    /**
     * POST /notificationDelivered - > notification delivered acknowledgement 
     * @throws URISyntaxException 
     */
    @RequestMapping(value = "/notificationDelivered", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Welcome> notificationDelivered(@RequestBody NotificationAcknowledgement notificationAcknowledgement) throws URISyntaxException{
    	log.debug("REST request for notification is delivered to user");
    	Welcome welcome = notificationAcknowledgementService.notificationDelivered(notificationAcknowledgement);
    	return ResponseEntity.created(new URI("/api/notificationDelivered/" + ""))
                .headers(HeaderUtil.createEntityCreationAlert("welcome", ""))
                .body(welcome);
    	
    }
    
    /**
     * POST /notificationRead - > notification read acknowledgement 
     * @throws URISyntaxException 
     */
    @RequestMapping(value = "/notificationRead", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Welcome> notificationRead(@RequestBody NotificationAcknowledgement notificationAcknowledgement) throws URISyntaxException{
    	log.debug("REST request for notification is delivered to user");
    	Welcome welcome = notificationAcknowledgementService.notificationRead(notificationAcknowledgement);
    	return ResponseEntity.created(new URI("/api/notificationRead/" + ""))
                .headers(HeaderUtil.createEntityCreationAlert("welcome", ""))
                .body(welcome);
    }
    
    /**
     * GET /notificationReadDeliveredAndSendCount/{notificationId}/{consumerId} - > get count for how many user read, received and receive notification
     * @throws URISyntaxException 
     * @throws JSONException 
     * 
     */
    @RequestMapping(value = "/notificationReadDeliveredAndSendCount/{notificationId}/{consumerId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> notificationReadDeliveredAndSendCount(@PathVariable("notificationId") String notificationId, @PathVariable("consumerId") String consumerId) throws URISyntaxException, JSONException{
    	
    	log.debug("REST request for notification is delivered to user");
    	JSONObject jsonObject =  notificationAcknowledgementService.notificationReadDeliveredAndSendCount(notificationId, consumerId);
    	
    	if(jsonObject != null){
    		return ResponseEntity.created(new URI("/api/notificationRead/" + ""))
                    .headers(HeaderUtil.createEntityCreationAlert("welcome", ""))
                    .body(jsonObject.toString());
    	}else{
    		return ResponseEntity.created(new URI("/api/notificationRead/" + ""))
                    .headers(HeaderUtil.createEntityCreationAlert("welcome", ""))
                    .body(null);
    	}
    }
    /**
     * POST / -> add consumer check-in on particular notification
     * @throws URISyntaxException 
     */
    @RequestMapping(value = "/consumerCheckIn", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Welcome> addConsumerCheckin (@RequestBody Notification notification) throws URISyntaxException{
    	
    	if(notification.getId()==null || notification.getLocation()==null || notification.getLocation().isEmpty()){
    		Welcome welcome=new Welcome();
    		welcome.setMessage(Constants.FAILURE_RESULT);
    		return ResponseEntity.created(new URI("/api/consumerCheckIn/" + ""))
                    .headers(HeaderUtil.createEntityCreationAlert("welcome", ""))
                    .body(welcome);
    	}
    	Welcome welcome=new Welcome();	welcome= notificationAcknowledgementService.consumerCheckIn(notification);
    	return ResponseEntity.created(new URI("/api/consumerCheckIn/" + ""))
                .headers(HeaderUtil.createEntityCreationAlert("welcome", ""))
                .body(welcome);
    }
    
}
