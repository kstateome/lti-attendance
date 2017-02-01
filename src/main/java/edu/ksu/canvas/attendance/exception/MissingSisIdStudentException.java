package edu.ksu.canvas.attendance.exception;

/**
 * Created by james on 1/20/17.
 */
public class MissingSisIdStudentException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public MissingSisIdStudentException(String msg) {
        super(msg);
    }
}
