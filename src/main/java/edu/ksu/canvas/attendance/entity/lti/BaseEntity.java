package edu.ksu.canvas.attendance.entity.lti;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import java.sql.Timestamp;

/**
 * Base entity to implement common database functionality
 */
@MappedSuperclass
public class BaseEntity {

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "application_name", length = 50)
    private String applicationName;

    @PrePersist
    public void setTimeStamp() {
        setCreatedAt(new Timestamp(System.currentTimeMillis()));
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }
}