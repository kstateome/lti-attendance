package edu.ksu.canvas.attendance.entity.lti;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "lti_key")
public class LtiKeyEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "key_id", nullable = false)
    private long keyId;
    @Column(name = "key_key", unique = true, nullable = false, insertable = true, updatable = true, length = 32)
    private String keyKey;
    @Column(name = "secret", nullable = false, insertable = true, updatable = true, length = 32)
    private String secret;
    @Column(name = "consumer_profile", nullable = true, length = 100)
    private String consumerProfile;

    protected LtiKeyEntity() {
    }

    /**
     * @param key    the key
     * @param secret [OPTIONAL] secret (can be null)
     */
    public LtiKeyEntity(String key, String secret) {
        assert StringUtils.isNotBlank(key);
        this.keyKey = key;
        if (StringUtils.isNotBlank(secret)) {
            this.secret = secret;
        }
    }

    public long getKeyId() {
        return keyId;
    }

    public void setKeyId(long keyId) {
        this.keyId = keyId;
    }

    public String getKeyKey() {
        return keyKey;
    }

    public void setKeyKey(String keyKey) {
        this.keyKey = keyKey;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getConsumerProfile() {
        return consumerProfile;
    }

    public void setConsumerProfile(String consumerProfile) {
        this.consumerProfile = consumerProfile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        LtiKeyEntity that = (LtiKeyEntity) o;

        if (keyId != that.keyId) {
            return false;
        }
        if (keyKey != null ? !keyKey.equals(that.keyKey) : that.keyKey != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) keyId;
        result = 31 * result + (keyKey != null ? keyKey.hashCode() : 0);
        return result;
    }

}
