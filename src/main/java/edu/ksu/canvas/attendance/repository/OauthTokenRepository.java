package edu.ksu.canvas.attendance.repository;

import edu.ksu.canvas.attendance.entity.lti.OauthToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OauthTokenRepository extends CrudRepository<OauthToken, Long> {

    OauthToken findByEIDAndApplicationName(String eID, String ApplicationName);
}
