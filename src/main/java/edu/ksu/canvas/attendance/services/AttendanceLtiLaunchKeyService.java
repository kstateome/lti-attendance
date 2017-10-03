package edu.ksu.canvas.attendance.services;


import edu.ksu.canvas.attendance.repository.LtiKeyRepository;
import edu.ksu.lti.launch.service.LtiLaunchKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AttendanceLtiLaunchKeyService implements LtiLaunchKeyService {

    @Autowired
    private AttendanceConfigService attendanceConfigService;

    @Autowired
    private LtiKeyRepository ltiKeyRepository;

    @Override
    public String findSecretForKey(String key) {
        return ltiKeyRepository.findByKeyKey(key).getSecret();
    }
}