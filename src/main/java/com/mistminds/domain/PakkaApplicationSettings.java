package com.mistminds.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Objects;

/**
 * A PakkaApplicationSettings.
 */

@Document(collection = "pakka_application_settings")
public class PakkaApplicationSettings implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("name")
    @Indexed(unique = true)
    private String name;

    @Field("description")
    private String description;

    @Field("value")
    private String value;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PakkaApplicationSettings pakkaApplicationSettings = (PakkaApplicationSettings) o;
        if(pakkaApplicationSettings.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, pakkaApplicationSettings.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "PakkaApplicationSettings{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", description='" + description + "'" +
            ", value='" + value + "'" +
            '}';
    }
}
