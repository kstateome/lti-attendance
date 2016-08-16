package edu.ksu.canvas.attendance.services;

import edu.ksu.canvas.entity.lti.OauthToken;
import edu.ksu.canvas.repository.OauthTokenRepository;
import edu.ksu.lti.launch.service.OauthTokenService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AttendanceOauthTokenService implements OauthTokenService{

    private static final String APPLICATION_NAME = "Attendance";
    private static final Logger LOG = Logger.getLogger(AttendanceOauthTokenService.class);

    @Autowired
    private OauthTokenRepository oauthTokenRepository;

    @Override
    public String storeToken(String eid, String token) {
        LOG.info("Storing token for " + eid);
        OauthToken oauthToken = new OauthToken();
        oauthToken.seteID(eid);
        oauthToken.setToken(token);
        oauthToken.setApplicationName(APPLICATION_NAME);
        oauthTokenRepository.save(oauthToken);
        return token;
    }

    @Override
    public String updateToken(String eid, String token) {
        LOG.info("Updating token for " + eid);
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
        LOG.info("Getting refresh token for " + eid);
        OauthToken token = oauthTokenRepository.findByEIDAndApplicationName(eid, APPLICATION_NAME);
        if (token == null) {
            return null;
        }
        return token.getToken();
    }
}