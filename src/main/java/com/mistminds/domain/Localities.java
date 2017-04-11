package com.mistminds.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * A Localities.
 */

@Document(collection = "localities")
public class Localities implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("localityname")
    private String localityname;

    @Field("localitylocation")
    private List<Double> localitylocation;

    @Field("cityname")
    private String cityname;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocalityname() {
        return localityname;
    }

    public void setLocalityname(String localityname) {
        this.localityname = localityname;
    }

    public List<Double> getLocalitylocation() {
		return localitylocation;
	}

	public void setLocalitylocation(List<Double> localitylocation) {
		this.localitylocation = localitylocation;
	}

	public String getCityname() {
        return cityname;
    }

    public void setCityname(String cityname) {
        this.cityname = cityname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Localities localities = (Localities) o;
        if(localities.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, localities.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Localities{" +
            "id=" + id +
            ", localityname='" + localityname + "'" +
            ", localitylocation='" + localitylocation + "'" +
            ", cityname='" + cityname + "'" +
            '}';
    }
}
