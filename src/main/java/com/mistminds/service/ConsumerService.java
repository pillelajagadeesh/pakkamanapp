package com.mistminds.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

import com.hazelcast.com.eclipsesource.json.JsonObject;
import com.mistminds.config.Constants;
import com.mistminds.config.PakkaApplicationSettingsConfiguration;
import com.mistminds.domain.Category;
import com.mistminds.domain.ContentMetadata;
import com.mistminds.domain.Consumer;
import com.mistminds.domain.ConsumerRegions;
import com.mistminds.domain.DeviceInfo;
import com.mistminds.domain.Notification;
import com.mistminds.domain.PakkaApplicationSettings;
import com.mistminds.domain.Provider;
import com.mistminds.domain.Region;
import com.mistminds.domain.Welcome;
import com.mistminds.domain.util.Util;
import com.mistminds.repository.CategoryRepository;
import com.mistminds.repository.ConsumerRegionsRepository;
import com.mistminds.repository.ConsumerRepository;
import com.mistminds.repository.DeviceInfoRepository;
import com.mistminds.repository.NotificationRepository;
import com.mistminds.repository.PakkaApplicationSettingsRepository;
import com.mistminds.repository.RegionRepository;
import com.sun.tools.javac.util.Log;


/**
 * Service class for managing Consumer.
 */

@Service
public class ConsumerService {
	private final Logger log = LoggerFactory.getLogger(ConsumerService.class);
	@Inject
	private PakkaApplicationSettingsRepository pakkaApplicationSettingsRepository;
	
	@Inject
    private NotificationRepository notificationRepository;
	
	@Inject
    private ConsumerRepository consumerRepository;
	
	@Inject
    private CategoryRepository categoryRepository;
	
	@Inject
	private DeviceInfoRepository deviceInfoRepository;
	
	@Inject 
	private ConsumerRegionsRepository consumerRegionsRepository;
	
	@Inject 
	private RegionRepository regionRepository;
	
	private Consumer dbConsumer;
	private Welcome welcome;
	private ConsumerRegions dbConsumerRegions;
	
	public ContentMetadata uploadPhoto(Map<String, Object> file, String id) {

		ContentMetadata cloudinary = new ContentMetadata();
			for(String key : file.keySet()){
				if(key.equals("signature")){
					cloudinary.setSignature(file.get(key).toString());
				}else if(key.equals("format")){
					cloudinary.setFormat(file.get(key).toString());
				}else if(key.equals("type" )){
					cloudinary.setType(file.get(key).toString());
				}else if(key.equals("version" )){
					cloudinary.setImageVersion(file.get(key).toString());
				}else if(key.equals("url" )){
					cloudinary.setUrl(file.get(key).toString());
				}else if(key.equals("tags" )){
					cloudinary.setTags(file.get(key).toString());
				}else if(key.equals("eTag" )){
					cloudinary.seteTag(file.get(key).toString());
				}else if(key.equals("bytes" )){
					cloudinary.setBytes(Integer.parseInt(file.get(key).toString()));
				}else if(key.equals("width")){
					cloudinary.setWidth(Integer.parseInt(file.get(key).toString()));
				}else if(key.equals("height" )){
					cloudinary.setHeight(Integer.parseInt(file.get(key).toString()));
				}
			}
			return cloudinary;
	}
	
	public Consumer updateConsumer(Consumer consumer){
		dbConsumer = consumerRepository.findOne(consumer.getId());
		if(dbConsumer != null ){
			if(consumer.getName() != null && !consumer.getName().isEmpty()){
				dbConsumer.setName(consumer.getName());
				dbConsumer.setMobile(consumer.getMobile());
			}
			
			if(consumer.getAddress()!= null && !consumer.getAddress().isEmpty()){
				dbConsumer.setAddress(consumer.getAddress());
				
			}
			if(consumer.getLocation()!= null  && !consumer.getLocation().isEmpty() && consumer.getLocation().get(0) != null && consumer.getLocation().get(1) != null)
			{
				dbConsumer.setLocation(consumer.getLocation());
			}
			if(consumer.getUrl() != null && !consumer.getUrl().isEmpty()){
				dbConsumer.setUrl(consumer.getUrl());
				dbConsumer.setLastUpdate(consumer.getLastUpdate());
			}

			}
		return dbConsumer ;
	}
	
	public Welcome addUnsubscribeCategory(Consumer consumer){
		welcome = new Welcome();
		if(consumer != null && consumer.getId() != null){
			dbConsumer = consumerRepository.findOne(consumer.getId());
			if(dbConsumer != null){
				dbConsumer.setUnsubscribeCategory(consumer.getUnsubscribeCategory());
				consumerRepository.save(dbConsumer);
				welcome.setMessage(Constants.SUCCESS_RESULT);
			}else{
				welcome.setMessage(Constants.FAILURE_RESULT);
			}
		}else{
			welcome.setMessage(Constants.FAILURE_RESULT);
		}
		return welcome;
	}
	public List<Category> addRemoveUnsubscribeCategory(Consumer consumer){
		List<Category> allCategory = null;
		List<Category> userCategory = new ArrayList<Category>();
		if(consumer != null && consumer.getId() != null){
			dbConsumer = consumerRepository.findOne(consumer.getId());
			if(dbConsumer != null && consumer.getStatus().equals("false")){
				allCategory = dbConsumer.getUnsubscribeCategory();
				if(allCategory == null){
					allCategory = consumer.getUnsubscribeCategory();
				}else{
					allCategory.addAll(consumer.getUnsubscribeCategory());
				}
				dbConsumer.setUnsubscribeCategory(allCategory);
				consumerRepository.save(dbConsumer);
				return allCategory;
			}else if(consumer.getStatus().equals("true")){
				allCategory = dbConsumer.getUnsubscribeCategory();
				if(allCategory == null){
					allCategory = consumer.getUnsubscribeCategory();
				}else{
					for(Category category : allCategory){
						for(Category dbCategory : consumer.getUnsubscribeCategory()){
							if(category.getId().equals(dbCategory.getId())){
							}else{
								userCategory.add(category);
							}
						}
					}
				}
				dbConsumer.setUnsubscribeCategory(userCategory);
				consumerRepository.save(dbConsumer);
				return userCategory;
			}
			else{
				
				allCategory = dbConsumer.getUnsubscribeCategory();
				
			}
		}
		return allCategory;
	}
	
	public List<Category> getUnsubscribeCategory(String consumerId) {

		if (consumerId != null && !consumerId.isEmpty()) {
			dbConsumer = consumerRepository.findOne(consumerId);
			if (dbConsumer != null) {
				return (List<Category>) dbConsumer.getUnsubscribeCategory();
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
	
	public List<Region> setLocation(Consumer consumer) {//Double latitude = null, longitude = null;
		List<Region> consumerRegions = null;
		PakkaApplicationSettings defaultRadiusValue=pakkaApplicationSettingsRepository.findByName(PakkaApplicationSettingsConfiguration.KEY_NAME_DEFAULT_REGIONS_RADIUS);
		double DefaultRegionRadius = (double)Double.parseDouble(defaultRadiusValue.getValue().toString());
		dbConsumer = consumerRepository.findOne(consumer.getId());
		if (dbConsumer != null) {
			dbConsumerRegions = consumerRegionsRepository.findByConsumerId(consumer.getId());
			if (dbConsumerRegions != null) {
				return dbConsumerRegions.getRegion();
			}else {
				dbConsumerRegions = new ConsumerRegions();
				dbConsumerRegions.setConsumerId(consumer.getId());
				dbConsumer.setLocation(consumer.getLocation());
				dbConsumer.setLastUpdate(ZonedDateTime.now());
				consumerRepository.save(dbConsumer);
				Point center = new Point(consumer.getLocation().get(0), consumer.getLocation().get(1));
				Double radius=(DefaultRegionRadius)/1000;
				Distance distance = new Distance(Math.toDegrees(radius/6378.137));
				Circle circle = new Circle(center,distance);
				List<Region> consumers = regionRepository
						.findByLocationWithin(circle);
				if (!consumers.isEmpty()) {
					TreeMap<Double, Region> sort = sortCoordinates(consumers,
							consumer.getLocation().get(0), consumer.getLocation().get(1));
					Set<Double> keys = sort.keySet();
					consumerRegions = new ArrayList<Region>();
					int i = 0;
					for (Double key : keys) {
						if (i == 4) {
							break;
						}
						if (Double.compare(key, 0.0) != 0 && consumerRegions.size() < 3){
							consumerRegions.add(sort.get(key));
						i++;
					}}
					dbConsumerRegions.setRegion(consumerRegions);
					consumerRegionsRepository.save(dbConsumerRegions);
				}
			}
		}
		return consumerRegions;
	}

	

	public TreeMap<Double, Region> sortCoordinates(List<Region> regions,
			Double lat, Double lon) {
		int Radius = 6371;// radius of earth in Km
		double lat1 = lat;
		TreeMap<Double, Region> treeMap = new TreeMap<Double, Region>();
		double lon1 = lon;

		for (Region region : regions) {
			double lat2 = region.getLocation().get(0);
			double lon2 = region.getLocation().get(1);
			double dLat = Math.toRadians(lat2 - lat1);
			double dLon = Math.toRadians(lon2 - lon1);
			double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
					+ Math.cos(Math.toRadians(lat1))
					* Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
					* Math.sin(dLon / 2);
			double c = 2 * Math.asin(Math.sqrt(a));
			Double d = Radius * c;
			double roundOff = Math.round(d * 100.0) / 100.0;
			treeMap.put(roundOff, region);
		}
		return treeMap;
	}

	public JsonObject parseProviderToJsonObject(Provider provider,Notification dbNotification) {
		JsonObject jsonObject = new JsonObject();
		if(provider.getId() != null){
		jsonObject.set("id", provider.getId());
		}
		if(provider.getName() != null){
		jsonObject.set("name", provider.getName());
		}
		if(provider.getMobile() != null){
		jsonObject.set("mobile", provider.getMobile());
		}
		if(provider.getEmail() != null){
		jsonObject.set("email", provider.getEmail());
		}
		if(provider.getAddress() != null){
		jsonObject.set("address", provider.getAddress());
		}
		if(provider.getLocation().get(0) != null && provider.getAddress() != null){
		jsonObject.set("latitude", provider.getLocation().get(0));
		}
		if(provider.getLocation().get(1)!= null && provider.getAddress() != null){
		jsonObject.set("longitude", provider.getLocation().get(1));
		}
		if(provider.getImageUrl() != null){
			
		jsonObject.set("url", provider.getImageUrl());
		}
		if(provider.getImage() != null){
		jsonObject.set("image", provider.getImage());
		}
		return jsonObject;
	}
	
	public JsonObject parseProviderToJsonObject(Provider provider) {
		JsonObject jsonObject = new JsonObject();
		if(provider.getId() != null){
		jsonObject.set("id", provider.getId());
		}
		if(provider.getName() != null){
		jsonObject.set("name", provider.getName());
		}
		if(provider.getMobile() != null){
		jsonObject.set("mobile", provider.getMobile());
		}
		if(provider.getEmail() != null){
		jsonObject.set("email", provider.getEmail());
		}
		if(provider.getAddress() != null){
		jsonObject.set("address", provider.getAddress());
		}
		if(provider.getLocation().get(0) != null && provider.getAddress() != null){
		jsonObject.set("latitude", provider.getLocation().get(0));
		}
		if(provider.getLocation().get(1)!= null && provider.getAddress() != null){
		jsonObject.set("longitude", provider.getLocation().get(1));
		}
		if(provider.getImageUrl() != null){
			
		jsonObject.set("url", provider.getImageUrl());
		}
		if(provider.getImage() != null){
		jsonObject.set("image", provider.getImage());
		}
		return jsonObject;
	}

	public Consumer accountUpdate(Consumer consumer) {
		
	 Consumer dbconsumer = consumerRepository.findByMobile(consumer.getMobile());
	 DeviceInfo device =  deviceInfoRepository.findByConsumerId(dbconsumer.getId());
	 
	if( dbconsumer!=null){
		
		if(consumer.getMobile() != null && !consumer.getMobile().isEmpty()){
			dbconsumer.setMobile(consumer.getMobile());
		}
		if(consumer.getGender() != null && !consumer.getGender().isEmpty()){
			dbconsumer.setGender(consumer.getGender());
		}
		if(consumer.getDevice_info() != null && consumer.getDevice_info().getDeviceId()!=null){
			dbconsumer.setDevice_info(consumer.getDevice_info());
		}
		if(device!=null)
		{
			device.setDeviceId(consumer.getDevice_info().getDeviceId());
			deviceInfoRepository.save(device);
			
		}
		
		dbconsumer.setLastUpdate(ZonedDateTime.now());
	}
		
		return dbconsumer;
	}

	
	
	public void sendOtp(String mobileNumber, String otp){
		System.out.println("OTP_PROVIDER mobile and otp"+mobileNumber+"------"+otp);
		
		PakkaApplicationSettings otpUrl=pakkaApplicationSettingsRepository.findByName(PakkaApplicationSettingsConfiguration.KEY_NAME_OTP_PROVIDER_URL);
		PakkaApplicationSettings otpKey=pakkaApplicationSettingsRepository.findByName(PakkaApplicationSettingsConfiguration.KEY_NAME_SEND_OTP_KEY);
		PakkaApplicationSettings senderName=pakkaApplicationSettingsRepository.findByName(PakkaApplicationSettingsConfiguration.KEY_NAME_SENDER_NAME);
		PakkaApplicationSettings routeCode=pakkaApplicationSettingsRepository.findByName(PakkaApplicationSettingsConfiguration.KEY_NAME_ROUTE_CODE);
		PakkaApplicationSettings countryCode=pakkaApplicationSettingsRepository.findByName(PakkaApplicationSettingsConfiguration.KEY_NAME_COUNTRY_CODE);
		
		String OTP_PROVIDER_URL = otpUrl.getValue().toString();
		String SEND_OTP_KEY = otpKey.getValue().toString();
		String SENDER_NAME = senderName.getValue().toString();
		String ROUTE_CODE = routeCode.getValue().toString();
		String COUNTRY_CODE = countryCode.getValue().toString();
		
	 String urlString = OTP_PROVIDER_URL+
	          "authkey=" + SEND_OTP_KEY +
	          "&mobiles=" + mobileNumber +
	          "&message=" + "Use"+" "+ otp +" "+"as OTP to verify your identity and reach out to your local."+
	           "&sender=" + SENDER_NAME +
	           "&route=" +ROUTE_CODE+
	            "&country=" +COUNTRY_CODE;
	try {
		 URL oracle = new URL(urlString);
		 URLConnection yc = oracle.openConnection(); 
	        BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream())); 
	        while (in.readLine() != null)  
	            System.out.println("Otp send successfully"); 
	        in.close(); 
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} 
	}
	
	/*public Consumer createProviderInfo(JSONObject obj) throws JSONException {
		dbConsumer = consumerRepository.findOne((String) obj.get("id"));
		if(dbConsumer != null && dbConsumer.getProvider() != null){
			Provider dbProvider = dbConsumer.getProvider();
				if(obj.getString("name") != null && !obj.getString("name").isEmpty()){
					dbProvider.setName(obj.getString("name"));
				}if(obj.getString("mobile") != null && !((String) obj.get("mobile")).isEmpty()){
					dbProvider.setMobile(obj.getString("mobile"));
				}if(obj.getString("email") != null && !((String) obj.get("email")).isEmpty()){
					dbProvider.setEmail(obj.getString("email"));
				}if(obj.getString("latitude") != null && (String)obj.getString("latitude") != null){
					dbProvider.setLatitude(obj.getDouble("latitude"));
					dbProvider.setLongitude(obj.getDouble("longitude"));
				}
				dbConsumer.setUpdated(new Date().toString());
				if(Util.checkOffensive(obj.getString("name")).equals("true")){
					dbConsumer.setActive(false);
				}
			dbConsumer.setProvider(dbProvider);
		}else if(dbConsumer != null){
			Provider newProvider = new Provider();
			newProvider.setName(obj.getString("name"));
			newProvider.setMobile(obj.getString("mobile"));
			newProvider.setEmail(obj.getString("email"));
			newProvider.setUrl(obj.getString("image"));
			newProvider.setAddress(obj.getString("address"));
			newProvider.setLatitude(obj.getDouble("latitude"));
			newProvider.setLongitude(obj.getDouble("longitude"));
			dbConsumer.setUpdated(new Date().toString());
			dbConsumer.setProvider(newProvider);
		}
		return dbConsumer;
	}*/
}
