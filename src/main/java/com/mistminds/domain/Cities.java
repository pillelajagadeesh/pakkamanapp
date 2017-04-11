package com.mistminds.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * A Cities.
 */

@Document(collection = "cities")
public class Cities implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("cityname")
    private String cityname;

    @Field("citylocation")
    private List<Double> citylocation;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCityname() {
        return cityname;
    }

    public void setCityname(String cityname) {
        this.cityname = cityname;
    }


    public List<Double> getCitylocation() {
		return citylocation;
	}

	public void setCitylocation(List<Double> citylocation) {
		this.citylocation = citylocation;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Cities cities = (Cities) o;
        if(cities.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, cities.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Cities{" +
            "id=" + id +
            ", cityname='" + cityname + "'" +
            ", citylocation='" + citylocation + "'" +
            '}';
    }
}
