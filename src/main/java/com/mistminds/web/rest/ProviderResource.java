package com.mistminds.web.rest;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import org.json.JSONArray;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.hazelcast.com.eclipsesource.json.JsonObject;
import com.mistminds.config.Constants;
import com.mistminds.config.PakkaApplicationSettingsConfiguration;
import com.mistminds.domain.Consumer;
import com.mistminds.domain.ConsumerFeedback;
import com.mistminds.domain.ContentMetadata;
import com.mistminds.domain.Notification;
import com.mistminds.domain.PakkaApplicationSettings;
import com.mistminds.domain.Provider;
import com.mistminds.domain.Welcome;
import com.mistminds.repository.ConsumerFeedbackRepository;
import com.mistminds.repository.ConsumerRepository;
import com.mistminds.repository.ContentMetadataRepository;
import com.mistminds.repository.NotificationRepository;
import com.mistminds.repository.PakkaApplicationSettingsRepository;
import com.mistminds.repository.ProviderRepository;
import com.mistminds.service.ContentMetadataService;
import com.mistminds.service.ConsumerService;
import com.mistminds.service.ProviderService;
import com.mistminds.web.rest.util.HeaderUtil;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

@RestController
@RequestMapping("/api")
public class ProviderResource {
	private final Logger log = LoggerFactory.getLogger(ProviderResource.class);

	@Inject
	private PakkaApplicationSettingsRepository pakkaApplicationSettingsRepository;
	
	@Inject
	private ProviderRepository providerRepository;
	@Inject
    private NotificationRepository notificationRepository;
	@Inject
	private ConsumerRepository consumerRepository;
	@Inject
    private ConsumerFeedbackRepository consumerFeedbackRepository;
	@Inject
	private ConsumerService consumerService;
	@Inject
	private ProviderService providerService;
	@Inject
	private ContentMetadataService contentMetadataService;

	@Inject
	private ContentMetadataRepository contentMetadataRepository;

	Welcome welcome;

	/**
	 * POST /consumers -> Create a new provider.admin screen
	 */
	@RequestMapping(value = "/providers", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Provider> createProvider(
			@RequestBody Provider provider) throws URISyntaxException {
		log.debug("REST request to save Consumer : {}", provider);
		if (provider.getId() != null) {
			return ResponseEntity
					.badRequest()
					.headers(
							HeaderUtil.createFailureAlert("consumer",
									"idexists",
									"A new consumer cannot already have an ID"))
					.body(null);
		}
		Provider result = providerRepository.save(provider);
		return ResponseEntity
				.created(new URI("/api/providers/" + result.getId()))
				.headers(
						HeaderUtil.createEntityCreationAlert("provider", result
								.getId().toString())).body(result);
	}

	/**
	 * PUT /consumers -> Updates an existing provider on admin screen.
	 */
	@RequestMapping(value = "/providers", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Provider> updateProvider(
			@RequestBody Provider provider) throws URISyntaxException {
		log.debug("REST request to update Consumer : {}", provider);
		if (provider.getId() == null) {
			return createProvider(provider);
		}
		Provider result = providerRepository.save(provider);
		return ResponseEntity
				.ok()
				.headers(
						HeaderUtil.createEntityUpdateAlert("provider", provider
								.getId().toString())).body(result);
	}

	/**
	 * GET /providers/:id -> get the "id" provider.
	 */
	@RequestMapping(value = "/providers/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Provider> getProvider(@PathVariable String id) {
		log.debug("REST request to get Consumer : {}", id);
		Provider provider = providerRepository.findOneByConsumerId(id);
		return Optional.ofNullable(provider)
				.map(result -> new ResponseEntity<>(result, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * GET /providers -> get all the providers.
	 */
	@RequestMapping(value = "/providers", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public List<Provider> getAllProviders() {
		log.debug("REST request to get all Providers");
		return providerRepository.findAll();
	}

	/**
	 * GET /providers/:id -> get the "id" provider for mobile screen
	 */
	@RequestMapping(value = "/providersdetails/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Provider> getProviderdetail(@PathVariable String id) {
		log.debug("REST request to get Consumer : {}", id);
		Provider provider = providerRepository.findOneByConsumerId(id);
		return Optional.ofNullable(provider)
				.map(result -> new ResponseEntity<>(result, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	/**
	 * POST /providersComment/:id -> get the consumerCommentDetails
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping(value = "/consumerCommentDetails", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public JSONObject getProvidercomment(@RequestBody Consumer consumer) throws ParseException, JSONException, UnsupportedEncodingException{
        log.debug("REST request to get Consumer : {}", consumer.getId());
        String senderMobileNumber = null;
        List<String> reviewComment= new ArrayList<String>();
        if(consumer.getId() == null){
    		log.error("Consumer id is empty or null");
    		return null;
    	}
      
        List<DBObject> commentFeedback=  providerService.getFeedBackComment(consumer);
        String lastDateVal = null;
    	JSONObject jsonObject=new JSONObject(commentFeedback.get(commentFeedback.size()-1).toString());
    	JSONObject jsonObject1 = (JSONObject) jsonObject.get("created");
    	lastDateVal = (String) jsonObject1.get("$date");
    	JSONObject mapJosn=new JSONObject ();
    	ZonedDateTime zdt4 = ZonedDateTime.parse(lastDateVal);
        ListIterator<DBObject> litr=commentFeedback.listIterator();
       while( litr.hasNext()){
    	   DBObject sss=  litr.next();
    	   JSONObject output = new JSONObject(JSON.serialize(sss));
    	   if(output.has("comment")){
     		  Consumer dbconsumer = consumerRepository.findOne(output.getString("consumer_id"));
     		  if(dbconsumer!=null){
						senderMobileNumber = dbconsumer.getMobile().substring(dbconsumer.getMobile().length() - 3,dbconsumer.getMobile().length());
						reviewComment.add(output.getString("comment") + "-----" + "*******" + senderMobileNumber +"----"+output.getString("created").substring(10,output.getString("created").lastIndexOf("Z"))+"Z");
     		  }
     		 
     		
       }}
       if(reviewComment!=null && !reviewComment.isEmpty()){
       mapJosn.put("ConsumerComment", reviewComment);
       mapJosn.put("lastRecordDate", zdt4);
       }
       else{
    	   mapJosn.put("ConsumerComment", "null");
    	   return mapJosn;
       }
       return  mapJosn;
       }
        

       
	
	/**
	 * POST /consumers -> Create a new provider for mobile
	 */
	@RequestMapping(value = "/createProviderInfo", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Welcome> createUpdateProvider(
			@RequestBody Provider provider) {
		log.debug("REST request to update Provider Info");
		welcome = new Welcome();
		Provider providercreate = null;
		Provider dbProvider = providerService.createProviderInfo(provider);
		if (dbProvider != null) {
			providercreate = providerRepository.save(dbProvider);
			if (providercreate.getId() == null) {
				providercreate = providerRepository.findOne(provider.getId());
			}
		} else {
			welcome.setMessage(Constants.FAILURE_RESULT);
		}
		if (providercreate != null && provider.getImage() != null
				&& !provider.getImage().isEmpty()) {
			Map<String, Object> uploadResult = contentMetadataService
					.cloudanaryUploadImage(provider.getImage());
			ContentMetadata cloudinay = consumerService.uploadPhoto(uploadResult,
					provider.getId());
			if (cloudinay != null) {
				ContentMetadata result = contentMetadataRepository.save(cloudinay);
				/* Provider dbProvider = consumer.getProvider(); */
				if (result.getId() != null) {
					dbProvider.setImageUrl(cloudinay.getUrl());
//					dbProvider.setPublicId(cloudinay.getSecureUrl());
				}
			}
			providercreate = providerRepository.save(providercreate);
		}
		if (providercreate != null && providercreate.getId() != null) {
			welcome.setMessage(Constants.SUCCESS_RESULT);
			welcome.setAuthToken(providercreate.getId());
		} else {
			welcome.setMessage(Constants.FAILURE_RESULT);
		}
		return ResponseEntity
				.ok()
				.headers(
						HeaderUtil.createEntityUpdateAlert("provider",
								"providercreate.getId()")).body(welcome);
	}

	/**
	 * PUT /consumers -> Updates an existing provider.
	 */
	@RequestMapping(value = "/updateProvider", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Welcome> UpdateProvider(@RequestBody Provider provider) {
		log.debug("REST request to update Provider Info");
		welcome = new Welcome();
		Provider providercreate = null;
		Provider dbProvider = providerService.updateProviderInfo(provider);
		if (dbProvider != null) {
			providercreate = providerRepository.save(dbProvider);
			if (providercreate.getId() == null) {
				providercreate = providerRepository.findOne(provider.getId());
			}
		} else {
			welcome.setMessage(Constants.FAILURE_RESULT);
		}
		if (providercreate != null && provider.getImage() != null
				&& !provider.getImage().isEmpty()) {
			Map<String, Object> uploadResult = contentMetadataService
					.cloudanaryUploadImage(provider.getImage());
			ContentMetadata cloudinay = consumerService.uploadPhoto(uploadResult,
					provider.getId());
			if (cloudinay != null) {
				ContentMetadata result = contentMetadataRepository.save(cloudinay);
				if (result.getId() != null) {
					dbProvider.setImageUrl(cloudinay.getUrl());
//					dbProvider.setPublicId(cloudinay.getSecureUrl());
				}
			}
			providercreate = providerRepository.save(providercreate);
		}
		if (providercreate != null && providercreate.getId() != null) {
			welcome.setMessage(Constants.SUCCESS_RESULT);
		} else {
			welcome.setMessage(Constants.FAILURE_RESULT);
		}
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("consumer", ""))
				.body(welcome);
	}

	/**
	 * POST /notifications -> approve the promo balance and add monthly free
	 * credit in provider account .
	 */
	@RequestMapping(value = "/elegibleforcredit/approve", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Provider> approveNotification(
			@RequestBody JSONObject obj) throws URISyntaxException {
		String id = null;
		Double monthlycredit = 0.0;
		Provider provider = null;
		try {
			log.debug("REST request to approve Notification : {}",
					obj.getString("id"));
			id = (String) obj.get("id");
			provider = providerRepository.findOneById(id);
			monthlycredit = provider.getMonthly_free_credits();
			PakkaApplicationSettings promoCreditValue = pakkaApplicationSettingsRepository.findByName(PakkaApplicationSettingsConfiguration.KEY_NAME_PROMO_CREDITS);
			double promoCredits=(double)Double.parseDouble(promoCreditValue.getValue().toString());
			Double freepromopoints = (monthlycredit + promoCredits);
			log.info("added free and promo credit for provider after click on checkbox"
					+ freepromopoints);
			boolean flag = obj.getBoolean("eleigible_for_promo_credit");
			if (flag) {

				provider.setMonthly_free_credits(freepromopoints);

				provider.setEleigible_for_promo_credit(obj
						.getBoolean("eleigible_for_promo_credit"));
				provider.setPromo_createdDate(new Date().toString());
			} else {
				provider.setEleigible_for_promo_credit(obj
						.getBoolean("eleigible_for_promo_credit"));
			}

			providerRepository.save(provider);

		} catch (JSONException e) {
			log.error("Error while approveing notification ", e);
		}

		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityGetAlert("provider", id))
				.body(provider);
	}
	
	@RequestMapping(value = "/providers/userActivation",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
        @Timed
        public ResponseEntity<Provider> userActivation(@RequestBody JSONObject obj) throws URISyntaxException {
            String id = null;
            Provider result = null;
            log.debug("Obj value----------------------------"+obj);
			try {
				log.debug("REST request to provider activation : {}", obj.getString("id"));
				id = (String) obj.get("id");
				boolean flag=obj.getBoolean("active");
				  if (flag) {
		            result = providerRepository.findOneById(id);
		            result.setActive(obj.getBoolean("active"));
		            log.debug("Select by ID------------"+result);
		            providerRepository.save(result);
		            return ResponseEntity.ok()
				            .headers(HeaderUtil.createEntityActivateAlert("provider", id.toString()))
				            .body(result);
		            
				  }
				  else{
					  result = providerRepository.findOneById(id);
			            result.setActive(obj.getBoolean("active"));
			            log.debug("Select by ID------------"+result);
			            providerRepository.save(result);    
			            return ResponseEntity.ok()
					            .headers(HeaderUtil.createEntityBlockAlert("provider", id.toString()))
					            .body(result);
				  }
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
          return null;
        
			
    }
	
	@RequestMapping(value = "/maps", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public JSONObject getProviderLocation() throws JSONException {
		JSONArray locationArray = new JSONArray();
		JSONObject jsonObj= new JSONObject();
		List<Provider> provider = providerRepository.findAll();
		try {
			for (Provider location : provider) {
				if(location.getLocation()!=null){
				JSONObject json = new JSONObject();
				json.put("lat", location.getLocation().get(0));
				json.put("long", location.getLocation().get(1));
				json.put("Address", (location.getAddress()));
				json.put("title", (location.getName()));
				locationArray.put(json);
			}
			}
		} catch (Exception e) {
			log.error("Error ", e);
		}
		jsonObj.put("location", locationArray);
		return jsonObj;

	}
	
	
	
	@RequestMapping(value = "/consumermaps", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public @ResponseBody JSONObject getConsumerLocation() throws JSONException {
		JSONArray locationArray = new JSONArray();
		JSONObject jsonObj= new JSONObject();
		List<Consumer> consumer = consumerRepository.findAll();
		try {
			for (Consumer location :consumer) {
				if(location.getLocation()!=null){
				JSONObject json = new JSONObject();
				json.put("lat", location.getLocation().get(0));
				json.put("long", location.getLocation().get(1));
				json.put("Mobile", (location.getMobile()));
				json.put("title", (location.getName()));
				locationArray.put(json);
			}
			}
		} catch (Exception e) {
			log.error("Error ", e);
		}
		jsonObj.put("Consumerlocation", locationArray);
		return jsonObj;
		}
	
	@RequestMapping(value = "/getCredit/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public String getProvidercredit(@PathVariable String id) {
		log.debug("REST request to get Consumer : {}", id);
		List<JsonObject> credit = providerService.getCreditDetails(id);
		if (credit != null && !credit.isEmpty()) {
			return credit.toString();
		} else {
			return null;
		}
	}
}