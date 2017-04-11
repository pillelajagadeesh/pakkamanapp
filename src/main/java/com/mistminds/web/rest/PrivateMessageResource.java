package com.mistminds.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.mistminds.domain.PrivateMessage;
import com.mistminds.domain.Welcome;
import com.mistminds.repository.PrivateMessageRepository;
import com.mistminds.service.PrivateMessageService;
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
 * REST controller for managing PrivateMessage.
 */
@RestController
@RequestMapping("/api")
public class PrivateMessageResource {

    private final Logger log = LoggerFactory.getLogger(PrivateMessageResource.class);
        
    @Inject
    private PrivateMessageRepository privateMessageRepository;
    
    @Inject
    private PrivateMessageService privateMessageService;
    
    private Welcome welcome;
    
    /**
     * POST  /privateMessages -> Create a new privateMessage.
     */
    @RequestMapping(value = "/privateMessages",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)	
    @Timed
    public ResponseEntity<PrivateMessage> createPrivateMessage(@RequestBody PrivateMessage privateMessage) throws URISyntaxException {
        log.debug("REST request to save PrivateMessage : {}", privateMessage);
        if (privateMessage.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("privateMessage", "idexists", "A new privateMessage cannot already have an ID")).body(null);
        }
        PrivateMessage result = privateMessageRepository.save(privateMessage);
        return ResponseEntity.created(new URI("/api/privateMessages/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("privateMessage", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /privateMessages -> Updates an existing privateMessage.
     */
    @RequestMapping(value = "/privateMessages",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<PrivateMessage> updatePrivateMessage(@RequestBody PrivateMessage privateMessage) throws URISyntaxException {
        log.debug("REST request to update PrivateMessage : {}", privateMessage);
        if (privateMessage.getId() == null) {
            return createPrivateMessage(privateMessage);
        }
        PrivateMessage result = privateMessageRepository.save(privateMessage);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("privateMessage", privateMessage.getId().toString()))
            .body(result);
    }

    /**
     * GET  /privateMessages -> get all the privateMessages.
     */
    @RequestMapping(value = "/privateMessages",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<PrivateMessage> getAllPrivateMessages() {
        log.debug("REST request to get all PrivateMessages");
        return privateMessageRepository.findAll();
            }

    /**
     * GET  /privateMessages/:id -> get the "id" privateMessage.
     */
    @RequestMapping(value = "/privateMessages/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<PrivateMessage> getPrivateMessage(@PathVariable String id) {
        log.debug("REST request to get PrivateMessage : {}", id);
        PrivateMessage privateMessage = privateMessageRepository.findOne(id);
        return Optional.ofNullable(privateMessage)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /privateMessages/:id -> delete the "id" privateMessage.
     */
    @RequestMapping(value = "/privateMessages/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deletePrivateMessage(@PathVariable String id) {
        log.debug("REST request to delete PrivateMessage : {}", id);
        privateMessageRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("privateMessage", id.toString())).build();
    }
    
    /**
     * POST /postMessage create a private message
     */
    @RequestMapping(value = "/postMessage", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Welcome> postMessage(@RequestBody PrivateMessage privateMessage){
    	log.debug("REST request to post PrivateMessage : {}");
    	welcome = privateMessageService.postMessage(privateMessage);
    	
    	return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("privateMessage", ""))
                .body(welcome);
    }
    
    /**
     * POST /getMessage create a private message
     */
    @RequestMapping(value = "/getMessage", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<PrivateMessage> getMessage(@RequestBody PrivateMessage privateMessage){
    	log.debug("REST request to post PrivateMessage : {}");
    	return privateMessageService.getPrivateMessage(privateMessage);	
    }
    
    /**
     * POST /messageDelivered - > It conform message has been send to user
     */
    @RequestMapping(value = "/messageDelivered", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Welcome> messageDelivered(@RequestBody PrivateMessage privateMessage){
    	log.debug("REST request to post PrivateMessage : {}");
    	welcome = privateMessageService.messageDelivered(privateMessage);
    	return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("privateMessage", ""))
                .body(welcome);
    }
    
    /**
     * POST /getMessage ->  It conform message has been deliver to user
     */
    @RequestMapping(value = "/messageRead", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Welcome> messageRead(@RequestBody PrivateMessage privateMessage){
    	log.debug("REST request to post PrivateMessage : {}");
    	welcome =  privateMessageService.messageRead(privateMessage);	
    	return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("privateMessage", ""))
                .body(welcome);
    }
}
