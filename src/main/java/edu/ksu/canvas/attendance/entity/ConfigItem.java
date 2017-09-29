package edu.ksu.canvas.attendance.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "config_item")
public class ConfigItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "config_id", nullable = false)
    private long id;

    @Column(name = "lti_application", nullable = false)
    private String ltiApplication;

    @Column(name="config_key", nullable = false)
    private String key;

    @Column(name="config_value")
    private String value;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLtiApplication() {
        return ltiApplication;
    }

    public void setLtiApplication(String ltiApplication) {
        this.ltiApplication = ltiApplication;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Transient
    public int getValueAsInt() {
        return Integer.valueOf(value);
    }
}
