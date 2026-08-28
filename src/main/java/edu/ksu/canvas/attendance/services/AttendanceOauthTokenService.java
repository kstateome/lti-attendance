package edu.ksu.canvas.attendance.services;

import edu.ksu.canvas.attendance.entity.lti.OauthToken;
import edu.ksu.canvas.attendance.repository.OauthTokenRepository;
import edu.ksu.lti.launch.service.OauthTokenService;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AttendanceOauthTokenService implements OauthTokenService{

    private static final String APPLICATION_NAME = "Attendance";
    private static final Logger LOG = LogManager.getLogger(AttendanceOauthTokenService.class);

    @Autowired
    private OauthTokenRepository oauthTokenRepository;

    @Override
    public String storeToken(String eid, String token) {
        LOG.info("Storing OAuth token.");
        OauthToken oauthToken = new OauthToken();
        oauthToken.seteID(eid);
        oauthToken.setToken(token);
        oauthToken.setApplicationName(APPLICATION_NAME);
        oauthTokenRepository.save(oauthToken);
        return token;
    }

    @Override
    public String updateToken(String eid, String token) {
        LOG.info("Updating OAuth token.");
        OauthToken oauthToken = oauthTokenRepository.findByEIDAndApplicationName(eid, APPLICATION_NAME);
        if (oauthToken == null) {
            return storeToken(eid, token);
        }
        oauthToken.setToken(token);
        oauthTokenRepository.save(oauthToken);
        return token;
    }

    @Override
    public String getRefreshToken(String eid) {
        LOG.info("Retrieving OAuth refresh token.");
        OauthToken token = oauthTokenRepository.findByEIDAndApplicationName(eid, APPLICATION_NAME);
        if (token == null) {
            return null;
        }
        return token.getToken();
    }
}