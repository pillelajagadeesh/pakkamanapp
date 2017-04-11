package com.mistminds.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.mistminds.domain.DeviceInfo;
import com.mistminds.repository.DeviceInfoRepository;
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
 * REST controller for managing DeviceInfo.
 */
@RestController
@RequestMapping("/api")
public class DeviceInfoResource {

    private final Logger log = LoggerFactory.getLogger(DeviceInfoResource.class);
        
    @Inject
    private DeviceInfoRepository deviceInfoRepository;
    
    /**
     * POST  /deviceInfos -> Create a new deviceInfo.
     */
    @RequestMapping(value = "/deviceInfos",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<DeviceInfo> createDeviceInfo(@RequestBody DeviceInfo deviceInfo) throws URISyntaxException {
        log.debug("REST request to save DeviceInfo : {}", deviceInfo);
        if (deviceInfo.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("deviceInfo", "idexists", "A new deviceInfo cannot already have an ID")).body(null);
        }
        DeviceInfo result = deviceInfoRepository.save(deviceInfo);
        return ResponseEntity.created(new URI("/api/deviceInfos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("deviceInfo", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /deviceInfos -> Updates an existing deviceInfo.
     */
    @RequestMapping(value = "/deviceInfos",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<DeviceInfo> updateDeviceInfo(@RequestBody DeviceInfo deviceInfo) throws URISyntaxException {
        log.debug("REST request to update DeviceInfo : {}", deviceInfo);
        if (deviceInfo.getId() == null) {
            return createDeviceInfo(deviceInfo);
        }
        DeviceInfo result = deviceInfoRepository.save(deviceInfo);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("deviceInfo", deviceInfo.getId().toString()))
            .body(result);
    }

    /**
     * GET  /deviceInfos -> get all the deviceInfos.
     */
    @RequestMapping(value = "/deviceInfos",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<DeviceInfo> getAllDeviceInfos() {
        log.debug("REST request to get all DeviceInfos");
        return deviceInfoRepository.findAll();
            }

    /**
     * GET  /deviceInfos/:id -> get the "id" deviceInfo.
     */
    @RequestMapping(value = "/deviceInfos/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<DeviceInfo> getDeviceInfo(@PathVariable String id) {
        log.debug("REST request to get DeviceInfo : {}", id);
        DeviceInfo deviceInfo = deviceInfoRepository.findOne(id);
        return Optional.ofNullable(deviceInfo)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /deviceInfos/:id -> delete the "id" deviceInfo.
     */
    @RequestMapping(value = "/deviceInfos/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteDeviceInfo(@PathVariable String id) {
        log.debug("REST request to delete DeviceInfo : {}", id);
        deviceInfoRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("deviceInfo", id.toString())).build();
    }
}
