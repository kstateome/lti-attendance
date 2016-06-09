package edu.ksu.canvas.aviation.services;


public class UnexpectedCanvasWrapperException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    
    public UnexpectedCanvasWrapperException(String msg) {
        super(msg);
    }
    
    public UnexpectedCanvasWrapperException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
