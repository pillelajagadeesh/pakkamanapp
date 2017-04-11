package com.mistminds.web.rest.util;

import java.util.List;

import com.mistminds.domain.Cities;
import com.mistminds.domain.Localities;

public class CitiesResult {
	
	List<Cities> citieslist;
	List<Localities> localitiesList;
	
	public List<Cities> getCitieslist() {
		return citieslist;
	}
	public void setCitieslist(List<Cities> citieslist) {
		this.citieslist = citieslist;
	}
	public List<Localities> getLocalitiesList() {
		return localitiesList;
	}
	public void setLocalitiesList(List<Localities> localitiesList) {
		this.localitiesList = localitiesList;
	}

}
