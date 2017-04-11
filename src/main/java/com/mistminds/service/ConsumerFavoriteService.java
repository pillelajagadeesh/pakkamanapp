package com.mistminds.service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.hazelcast.com.eclipsesource.json.JsonObject;
import com.mistminds.config.Constants;
import com.mistminds.domain.Consumer;
import com.mistminds.domain.ConsumerFavourite;
import com.mistminds.domain.Provider;
import com.mistminds.domain.Welcome;
import com.mistminds.repository.ConsumerFavouriteRepository;
import com.mistminds.repository.ConsumerRepository;
import com.mistminds.repository.ProviderRepository;

@Service
public class ConsumerFavoriteService {
	
	private final Logger log = LoggerFactory.getLogger(ConsumerFeedbackService.class);
	
	@Inject
	private ConsumerRepository consumerRepository;
	
	@Inject
	private ProviderRepository providerRepository;

	@Inject
	private ConsumerService consumerService;

	@Inject
	private NotificationService notificationService;
	
	@Inject
	private ConsumerFavouriteRepository consumerFavouriteRepository;
	
	private ConsumerFavourite dbConsumerFavourite;
	
	private Consumer dbConsumer;
	
	private Welcome welcome;
	
	public Welcome addFavorite(ConsumerFavourite consumerFavourite){
		log.debug("Inside addFavorite method of ConsumerFavoriteService");
		try{
		dbConsumer = consumerRepository.findOne(consumerFavourite.getConsumerId());
		if(dbConsumer != null){
			welcome = new Welcome();
			dbConsumerFavourite = consumerFavouriteRepository.findByConsumerId(dbConsumer.getId());
			if(dbConsumerFavourite != null && consumerFavourite.getFavouriteStatus().equalsIgnoreCase("true")){
				dbConsumerFavourite.getProviderId().addAll(consumerFavourite.getProviderId());
				dbConsumerFavourite.setLastUpdate(ZonedDateTime.now());
				consumerFavouriteRepository.save(dbConsumerFavourite);
				welcome.setMessage(Constants.SUCCESS_RESULT);
			}else if(dbConsumerFavourite != null && consumerFavourite.getFavouriteStatus().equalsIgnoreCase("false")){
				dbConsumerFavourite.getProviderId().removeAll(consumerFavourite.getProviderId());
				dbConsumerFavourite.setLastUpdate(ZonedDateTime.now());
				consumerFavouriteRepository.save(dbConsumerFavourite);
				welcome.setMessage(Constants.SUCCESS_RESULT);
			}else{
				consumerFavourite.setCreated(ZonedDateTime.now());
				consumerFavouriteRepository.save(consumerFavourite);
				welcome.setMessage(Constants.SUCCESS_RESULT);
			}
		}
		}catch(NullPointerException npx){
			log.error(npx.toString());
			welcome.setMessage(Constants.FAILURE_RESULT);
		}
		return welcome;
	}

	public List<JsonObject> getAllconsumerdetails(String id) {
		List<JsonObject> allProvider = new ArrayList<JsonObject>();
		Consumer consumer = null;
		Provider provider = null;
		JsonObject rjsonObject = null;
		double rating;
		try{
			dbConsumer=consumerRepository.findOneById(id);
			if(dbConsumer != null){
				dbConsumerFavourite  = consumerFavouriteRepository.findByConsumerId(id);
				if(dbConsumerFavourite != null){
				for(String providerId : dbConsumerFavourite.getProviderId()){
					rjsonObject = new JsonObject();
					consumer = consumerRepository.findOneById(providerId);
					if(consumer != null){
						provider = providerRepository.findOneByConsumerId(consumer.getId());
						if(provider != null){
							rjsonObject = consumerService.parseProviderToJsonObject(provider);
						}
						rating = notificationService.generateRating(providerId);
						rjsonObject.set("rating", rating);
						rjsonObject.set("providerId", consumer.getId());
						allProvider.add(rjsonObject);
					}
				}
				}
			}
		}catch(NullPointerException npx){
			log.error(npx.toString());
			return null;
		}
		return allProvider;
}
}