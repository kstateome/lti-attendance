package edu.ksu.canvas.attendance.exception;

/**
 * Created by james on 2/1/17.
 */
public class MissingSisIdInstructorException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public MissingSisIdInstructorException(String msg) {
        super(msg);
    }
}
