package com.mistminds.service;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

import com.mistminds.config.PakkaApplicationSettingsConfiguration;
import com.mistminds.domain.Cities;
import com.mistminds.domain.Consumer;
import com.mistminds.domain.Localities;
import com.mistminds.domain.PakkaApplicationSettings;
import com.mistminds.repository.CitiesRepository;
import com.mistminds.repository.LocalitiesRepository;
import com.mistminds.repository.PakkaApplicationSettingsRepository;
import com.mistminds.web.rest.util.CitiesResult;

@Service
public class CitiesService {
	
	@Inject
	CitiesRepository citiesRepository;
	
	@Inject
	LocalitiesRepository localitiesRepository;
	
	@Inject
    private PakkaApplicationSettingsRepository pakkaApplicationSettingsRepository;
	
	public CitiesResult listCitiesAndLocalities(Consumer consumer){
		List<Localities> locality = new ArrayList<Localities>();
		PakkaApplicationSettings citiesDistance = pakkaApplicationSettingsRepository.findByName(PakkaApplicationSettingsConfiguration.KEY_NAME_DISTANCE_OF_CITIES);
		PakkaApplicationSettings localitiesDistance = pakkaApplicationSettingsRepository.findByName(PakkaApplicationSettingsConfiguration.KEY_NAME_DISTANCE_OF_LOCALITIES);
		Double cityRange = Double.parseDouble(citiesDistance.getValue().toString());
		Double localityRange = Double.parseDouble(localitiesDistance.getValue().toString());
		Point CitiesCenter = new Point(consumer.getLocation().get(0), consumer.getLocation().get(1));
		Distance distance = new Distance(Math.toDegrees(cityRange / 6378.137));
		Circle circle = new Circle(CitiesCenter, distance);
		List<Cities> cities = citiesRepository.findByCitylocationWithin(circle);
				 Point LocalitiesCenter = new Point(consumer.getLocation().get(0),consumer.getLocation().get(1));
					Distance distance1 = new Distance(Math.toDegrees(localityRange / 6378.137));
					Circle circle1 = new Circle(LocalitiesCenter, distance1);
					List<Localities> locs = localitiesRepository.findByLocalitylocationWithin(circle1);
				  for(Localities loc: locs){
					  locality.add(loc);
					}
	
		CitiesResult result=new CitiesResult(); 
		result.setCitieslist(cities);
		result.setLocalitiesList(locality);
		return result;
		
	}

}
