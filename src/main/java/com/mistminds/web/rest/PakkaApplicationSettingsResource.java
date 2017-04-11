package com.mistminds.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.mistminds.domain.PakkaApplicationSettings;
import com.mistminds.repository.PakkaApplicationSettingsRepository;
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
 * REST controller for managing PakkaApplicationSettings.
 */
@RestController
@RequestMapping("/api")
public class PakkaApplicationSettingsResource {

    private final Logger log = LoggerFactory.getLogger(PakkaApplicationSettingsResource.class);
        
    @Inject
    private PakkaApplicationSettingsRepository pakkaApplicationSettingsRepository;
    
    /**
     * POST  /pakka-application-settings : Create a new pakkaApplicationSettings.
     *
     * @param pakkaApplicationSettings the pakkaApplicationSettings to create
     * @return the ResponseEntity with status 201 (Created) and with body the new pakkaApplicationSettings, or with status 400 (Bad Request) if the pakkaApplicationSettings has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/pakka-application-settings",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<PakkaApplicationSettings> createPakkaApplicationSettings(@RequestBody PakkaApplicationSettings pakkaApplicationSettings) throws URISyntaxException {
        log.debug("REST request to save PakkaApplicationSettings : {}", pakkaApplicationSettings);
        if (pakkaApplicationSettings.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("pakkaApplicationSettings", "idexists", "A new pakkaApplicationSettings cannot already have an ID")).body(null);
        }
        PakkaApplicationSettings result = pakkaApplicationSettingsRepository.save(pakkaApplicationSettings);
        return ResponseEntity.created(new URI("/api/pakka-application-settings/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("pakkaApplicationSettings", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /pakka-application-settings : Updates an existing pakkaApplicationSettings.
     *
     * @param pakkaApplicationSettings the pakkaApplicationSettings to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated pakkaApplicationSettings,
     * or with status 400 (Bad Request) if the pakkaApplicationSettings is not valid,
     * or with status 500 (Internal Server Error) if the pakkaApplicationSettings couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/pakka-application-settings",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<PakkaApplicationSettings> updatePakkaApplicationSettings(@RequestBody PakkaApplicationSettings pakkaApplicationSettings) throws URISyntaxException {
        log.debug("REST request to update PakkaApplicationSettings : {}", pakkaApplicationSettings);
        if (pakkaApplicationSettings.getId() == null) {
            return createPakkaApplicationSettings(pakkaApplicationSettings);
        }
        PakkaApplicationSettings result = pakkaApplicationSettingsRepository.save(pakkaApplicationSettings);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("pakkaApplicationSettings", pakkaApplicationSettings.getId().toString()))
            .body(result);
    }

    /**
     * GET  /pakka-application-settings : get all the pakkaApplicationSettings.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of pakkaApplicationSettings in body
     */
    @RequestMapping(value = "/pakka-application-settings",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<PakkaApplicationSettings> getAllPakkaApplicationSettings() {
        log.debug("REST request to get all PakkaApplicationSettings");
        List<PakkaApplicationSettings> pakkaApplicationSettings = pakkaApplicationSettingsRepository.findAll();
        return pakkaApplicationSettings;
    }

    /**
     * GET  /pakka-application-settings/:id : get the "id" pakkaApplicationSettings.
     *
     * @param id the id of the pakkaApplicationSettings to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the pakkaApplicationSettings, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/pakka-application-settings/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<PakkaApplicationSettings> getPakkaApplicationSettings(@PathVariable String id) {
        log.debug("REST request to get PakkaApplicationSettings : {}", id);
        PakkaApplicationSettings pakkaApplicationSettings = pakkaApplicationSettingsRepository.findOne(id);
        return Optional.ofNullable(pakkaApplicationSettings)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /pakka-application-settings/:id : delete the "id" pakkaApplicationSettings.
     *
     * @param id the id of the pakkaApplicationSettings to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/pakka-application-settings/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deletePakkaApplicationSettings(@PathVariable String id) {
        log.debug("REST request to delete PakkaApplicationSettings : {}", id);
        pakkaApplicationSettingsRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("pakkaApplicationSettings", id.toString())).build();
    }

}
