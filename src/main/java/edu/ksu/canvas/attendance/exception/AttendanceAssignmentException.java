package edu.ksu.canvas.attendance.exception;

public class AttendanceAssignmentException extends Exception{
    public enum Error{
        NOT_SAVED(0, "Assignment configuration needs to be saved before pushing to Canvas."),
        FAILED_PUSH(1,"Could not push grades of section."),
        NO_CONNECTION(2, "Could not connect to Canvas to get assignment"),
        NO_ASSIGNMENT_FOUND(3, "Assignment not found in Canvas"),
        CREATION_ERROR(4, "Error while creating canvas assignment for section."),
        DELETION_ERROR(5, "Error while deleting canvas assignment."),
        EDITING_ERROR(6, "Error while editing canvas assignment."),
        NON_EXISTENT_SECTION_ERROR(7, "Cannot load data into course form, no sections found for this course.");



        private final int index;
        private final String description;

        private Error(int index, String description){
            this.index = index;
            this.description = description;
        }

        public int getIndex() {
            return index;
        }

        public String getDescription() {
            return description;
        }


    }

    @Override
    public String getMessage(){
        return error.getDescription();
    }

    public Error error;

    public AttendanceAssignmentException(Error error){
       this.error = error;
    }
}
