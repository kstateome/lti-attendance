package edu.ksu.canvas.attendance.repository;

import edu.ksu.canvas.attendance.entity.lti.LtiKeyEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LtiKeyRepository extends CrudRepository<LtiKeyEntity, Long> {

    LtiKeyEntity findByKeyKey(String key);
}
