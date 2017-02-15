package edu.ksu.canvas.attendance.exception;

/**
 * Created by james on 1/20/17.
 */
public class MissingSisIdException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final boolean authority;

    public MissingSisIdException(String msg, boolean hasOneAuthorityRole) {
        super(msg);
        authority = hasOneAuthorityRole;
    }

    public boolean isAuthority() {
        return authority;
    }
}
