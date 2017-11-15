package edu.ksu.canvas.attendance.repository;

import edu.ksu.canvas.attendance.entity.ConfigItem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigRepository extends CrudRepository<ConfigItem, Long>{

    ConfigItem findByLtiApplicationAndKey(String ltiApplication, String key);
}
