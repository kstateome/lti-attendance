package edu.ksu.canvas.attendance.util;

import edu.ksu.lti.LtiLaunchData;
import org.apache.log4j.Logger;

import java.util.List;

//FIXME: This is cut and paste from the canvas-lti project
public class RoleChecker {
    
    private static final Logger LOG = Logger.getLogger(RoleChecker.class);
    private final List<LtiLaunchData.InstitutionRole> validRoles;

    
    public RoleChecker(List<LtiLaunchData.InstitutionRole> validRoles) {
        this.validRoles = validRoles;
    }

    public boolean roleAllowed(List<LtiLaunchData.InstitutionRole> userRoles) {
        if (userRoles == null || userRoles.isEmpty()) {
            LOG.warn("Found empty role list - this shouldn't be happening");
            return false;
        }
        return validRoles.stream().anyMatch(userRoles::contains);
    }

}