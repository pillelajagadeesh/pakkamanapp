package com.mistminds.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.mistminds.config.Constants;
import com.mistminds.config.PakkaApplicationSettingsConfiguration;
import com.mistminds.domain.Category;
import com.mistminds.domain.Consumer;
import com.mistminds.domain.ContentMetadata;
import com.mistminds.domain.DeviceInfo;
import com.mistminds.domain.PakkaApplicationSettings;
import com.mistminds.domain.Provider;
import com.mistminds.domain.Region;
import com.mistminds.domain.Welcome;

import com.mistminds.repository.ConsumerRepository;
import com.mistminds.repository.ContentMetadataRepository;
import com.mistminds.repository.DeviceInfoRepository;
import com.mistminds.repository.PakkaApplicationSettingsRepository;
import com.mistminds.repository.ProviderRepository;
import com.mistminds.service.ConsumerService;
import com.mistminds.service.ContentMetadataService;
import com.mistminds.web.rest.util.HeaderUtil;

/**
 * REST controller for managing Consumer.
 */
@RestController
@RequestMapping("/api")
public class ConsumerResource {

	private final Logger log = LoggerFactory.getLogger(ConsumerResource.class);

	@Inject
	private PakkaApplicationSettingsRepository pakkaApplicationSettingsRepository;
	
	@Inject
	private ConsumerRepository consumerRepository;
	@Inject
	private ProviderRepository providerRepository;
	@Inject
	private DeviceInfoRepository deviceInfoRepository;
	@Inject
	private ConsumerService consumerService;

	@Inject
	private ContentMetadataService contentMetadataService;

	@Inject
	private ContentMetadataRepository contentMetadataRepository;

	Welcome welcome;

	/**
	 * POST /consumers -> Create a new consumer.
	 */
	@RequestMapping(value = "/consumers", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Consumer> createConsumer(
			@RequestBody Consumer consumer) throws URISyntaxException {
		log.debug("REST request to save Consumer : {}", consumer);
		if (consumer.getId() != null) {
			return ResponseEntity
					.badRequest()
					.headers(
							HeaderUtil.createFailureAlert("consumer",
									"idexists",
									"A new consumer cannot already have an ID"))
					.body(null);
		}
		Consumer result = consumerRepository.save(consumer);
		return ResponseEntity
				.created(new URI("/api/consumers/" + result.getId()))
				.headers(
						HeaderUtil.createEntityCreationAlert("consumer", result
								.getId().toString())).body(result);
	}

	/**
	 * PUT /consumers -> Updates an existing consumer.
	 */
	@RequestMapping(value = "/consumers", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Consumer> updateConsumer(
			@RequestBody Consumer consumer) throws URISyntaxException {
		log.debug("REST request to update Consumer : {}", consumer);
		if (consumer.getId() == null) {
			return createConsumer(consumer);
		}
		Consumer result = consumerRepository.save(consumer);
		return ResponseEntity
				.ok()
				.headers(
						HeaderUtil.createEntityUpdateAlert("consumer", consumer
								.getId().toString())).body(result);
	}

	/**
	 * GET /consumers -> get all the consumers.
	 */
	@RequestMapping(value = "/consumers", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public List<Consumer> getAllConsumers() {
		log.debug("REST request to get all Consumers");
		return consumerRepository.findAll();
	}

	/**
	 * GET /consumers/:id -> get the "id" consumer.
	 */
	@RequestMapping(value = "/consumers/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Consumer> getConsumer(@PathVariable String id) {
		log.debug("REST request to get Consumer : {}", id);
		Consumer consumer = consumerRepository.findOne(id);
		return Optional.ofNullable(consumer)
				.map(result -> new ResponseEntity<>(result, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /consumers/:id -> delete the "id" consumer.
	 */
	@RequestMapping(value = "/consumers/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteConsumer(@PathVariable String id) {
		log.debug("REST request to delete Consumer : {}", id);
		consumerRepository.delete(id);
		return ResponseEntity
				.ok()
				.headers(
						HeaderUtil.createEntityDeletionAlert("consumer",
								id.toString())).build();
	}
	/**
	 * POST /registerMobile -> Register the mobile after installation.
	 */
	@RequestMapping(value = "/registerMobile", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Welcome> registerMobile(@RequestBody Consumer consumer)
			throws URISyntaxException {
		log.debug("REST request to register Mobile : {}");
		welcome = new Welcome();
		Consumer result = consumerRepository.findByMobile(consumer.getMobile());
		DeviceInfo consumerdevice = deviceInfoRepository.findBydeviceId(consumer.getDevice_info().getDeviceId());
		if (result != null && result.getId() != null) {
			welcome.setMessage("Mobile number already exist, Please use different number");
		} else {
			
			if(consumerdevice!=null && consumerdevice.getDeviceId()!=null) {
				consumerRepository.delete(consumerdevice.getConsumerId());
				deviceInfoRepository.delete(consumerdevice.getId());
			}
			
			result = consumerRepository.save(consumer);
			
			welcome.setMessage("Please enter the OTP");
			welcome.setAuthToken(result.getId());
			if (consumer != null) {
				result.setOtpCount(1);
				result.setOtp(Constants.generatePassword());
				consumerService.sendOtp(result.getMobile(), result.getOtp());
			}
			consumerRepository.save(result);
			consumerdevice=deviceInfoRepository.save(result.getDevice_info());
			consumerdevice.setConsumerId(result.getId());
			deviceInfoRepository.save(consumerdevice);
		}
		return ResponseEntity
				.created(new URI("/api/registerMobile/" + result.getId()))
				.headers(
						HeaderUtil.createEntityCreationAlert("welcome", result
								.getId().toString())).body(welcome);
	}
	
	/**
	 * POST /registerMobile -> Transfer the account   after installation if before account was there.
	 */
	@RequestMapping(value = "/transferAccount", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Welcome> transferAccount(@RequestBody Consumer consumer)
			throws URISyntaxException {
		log.debug("REST request to register Mobile : {}");
		Consumer consumerupdate = null;
		welcome = new Welcome();
		Consumer result = consumerService.accountUpdate(consumer);
		if (result != null) {
			consumerupdate =consumerRepository.save(result);
			
			consumerupdate.setOtpCount(consumerupdate.getOtpCount()+1);
			consumerupdate.setStatus("");
			consumerupdate.setActive(false);
			consumerupdate.setOtp(Constants.generatePassword());
			consumerService.sendOtp(consumerupdate.getMobile(), consumerupdate.getOtp());
			consumerRepository.save(consumerupdate);
			welcome.setMessage("Please enter the OTP");
			
			welcome.setAuthToken(consumerupdate.getId());
			Provider provider=providerRepository.findOneByConsumerId(consumerupdate.getId());
			if(provider!=null&&provider.getId()!=null){
			welcome.setStatus("true");
			welcome.setId(provider.getId());
			}
			else{
				welcome.setStatus("false");
				welcome.setId("null");
				
			}
			
		}
		return ResponseEntity
				.created(new URI("/api/transferAccount/" + result.getId()))
				.headers(
						HeaderUtil.createEntityCreationAlert("welcome", result
								.getId().toString())).body(welcome);
	}
	@RequestMapping(value = "/verifyOTP", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Welcome> veryfyOTP(@RequestBody Consumer consumer) {
		log.debug("REST request to veryfyOTP");
		welcome = new Welcome();
		 PakkaApplicationSettings freeCreditsValue = pakkaApplicationSettingsRepository.findByName(PakkaApplicationSettingsConfiguration.KEY_NAME_FREE_CREDITS);
		 String freeCredits=freeCreditsValue.getValue().toString();
		Consumer dbConsumer = consumerRepository.findOne(consumer.getId());
		if (dbConsumer.getOtp().equals(consumer.getOtp())) {
			dbConsumer.setStatus("Verified success");
			dbConsumer.setActive(true);
			welcome.setMessage(Constants.SUCCESS_RESULT);
			welcome.setMonthlycredit(freeCredits);
		} else {
			dbConsumer.setStatus("Verified failure");
			dbConsumer.setActive(false);
			welcome.setMessage(Constants.FAILURE_RESULT);
		}
		consumer.setLastUpdate(ZonedDateTime.now());
		consumerRepository.save(dbConsumer);
		return ResponseEntity
				.ok()
				.headers(
						HeaderUtil.createEntityUpdateAlert("consumer", consumer
								.getId().toString())).body(welcome);

	}

	@RequestMapping(value = "/resendOTP/{id}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Welcome> reSendOTP(@PathVariable("id") final String id) {

		log.debug("REST request to reSendOTP");
		welcome = new Welcome();
		Consumer consumer = consumerRepository.findOne(id);

		if (consumer != null) {
			consumer.setOtpCount(2);
			consumer.setOtp(Constants.generatePassword());
			consumerService.sendOtp(consumer.getMobile(), consumer.getOtp());
		}

		consumerRepository.save(consumer);
		welcome.setMessage(Constants.SUCCESS_RESULT);
		return ResponseEntity
				.ok()
				.headers(
						HeaderUtil.createEntityUpdateAlert("consumer", consumer
								.getId().toString())).body(welcome);
	}

	@RequestMapping(value = "/getOTP/{mobile}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> getOTP(@PathVariable("mobile") String mobile) {
		log.debug("REST request to getOTP");
		Consumer consumer = consumerRepository.findByMobile(mobile);

		return Optional
				.ofNullable(consumer)
				.map(result -> new ResponseEntity<String>(consumer.getOtp(),
						HttpStatus.OK))
				.orElse(new ResponseEntity<String>(HttpStatus.NOT_FOUND));
	}

	@RequestMapping(value = "/updateConsumer", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Welcome> updateMyConsumer(
			@RequestBody Consumer consumer) {
		log.debug("REST request to update Consumer : {}", consumer);
		welcome = new Welcome();
		Map<String, Object> uploadResult = null;
		if (consumer != null && consumer.getImage() != null
				&& !consumer.getImage().isEmpty()) {
			uploadResult = contentMetadataService.cloudanaryUploadImage(consumer
					.getImage());
		}
		if (uploadResult != null && consumer.getId() != null) {
			ContentMetadata contentMetadata = consumerService.uploadPhoto(uploadResult,
					consumer.getId());
			if (contentMetadata != null) {
				ContentMetadata result = contentMetadataRepository.save(contentMetadata);
				if (result != null && result.getId() != null) {
					consumer.setUrl(contentMetadata.getUrl());
					consumer.setLastUpdate(ZonedDateTime.now());
					welcome.setMessage(Constants.SUCCESS_RESULT);
				} else {
					welcome.setMessage(Constants.FAILURE_RESULT);
				}
			} else {
				welcome.setMessage(Constants.FAILURE_RESULT);
			}
		}

		Consumer dbConsumer = consumerService.updateConsumer(consumer);
		try {
			Consumer result = consumerRepository.save(dbConsumer);
			if (result.getId() != null) {
				welcome.setMessage(Constants.SUCCESS_RESULT);
			}
		} catch (Exception ex) {
			log.debug("Error while updating consumer");
			welcome.setMessage(Constants.FAILURE_RESULT);
		}
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("consumer", ""))
				.body(welcome);
	}

	@RequestMapping(value = "/addUnsubscribeCategory", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Welcome> addUnsubscribeCategory(
			@RequestBody Consumer consumer) {
		log.debug("REST request for add user unsubscribe category");

		Welcome welcome = consumerService.addUnsubscribeCategory(consumer);
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("consumer", ""))
				.body(welcome);
	}

	/**
	 * add unsubscribe category and remove subscribe category.
	 */
	@RequestMapping(value = "/addRemoveUnsubscribeCategory", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<Category>> addremoveUnsubscribeCategory(
			@RequestBody Consumer consumer) {
		log.debug("REST request for add user unsubscribe category");

		List<Category> UnsubscribeCategory = consumerService
				.addRemoveUnsubscribeCategory(consumer);
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("consumer", ""))
				.body(UnsubscribeCategory);
	}

	@RequestMapping(value = "/getUnsubscribeCategory/{id}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<Category>> getUnsubscribeCategory(
			@PathVariable("id") String id) {
		log.debug("REST request to get user unsubscrivbe");

		List<Category> UnsubscribeCategory = consumerService
				.getUnsubscribeCategory(id);
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("consumer", ""))
				.body(UnsubscribeCategory);
	}

	@RequestMapping(value = "/setLocation", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<Region>> setLocation(
			@RequestBody Consumer consumer) {
		log.debug("REST request to set location");

		List<Region> UnsubscribeCategory = consumerService
				.setLocation(consumer);
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("consumer", ""))
				.body(UnsubscribeCategory);
	}

	@RequestMapping(value = "/consumers/userActivation", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Consumer> userActivation(@RequestBody JSONObject obj)
			throws URISyntaxException {
		String id = null;
		Consumer result = null;
		log.debug("UserActivation value: " + obj);
		try {
			log.debug("REST request to consumer activation : {}",
					obj.getString("id"));
			id = (String) obj.get("id");
			if (id != null) {
				result = consumerRepository.findOneById(id);
				result.setActive(obj.getBoolean("active"));

				log.debug("Select by ID: " + result);
				consumerRepository.save(result);
			}
		} catch (JSONException e) {
			log.error("Error while useractivation id:"+id, e);
		}

		return ResponseEntity
				.ok()
				.headers(
						HeaderUtil.createEntityUpdateAlert("consumer",
								id.toString())).body(result);
	}
}