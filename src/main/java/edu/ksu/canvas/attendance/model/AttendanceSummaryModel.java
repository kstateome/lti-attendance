package edu.ksu.canvas.attendance.model;

import java.util.ArrayList;
import java.util.List;


/**
 * Represents all of the attendance summary data for a given section
 */
public class AttendanceSummaryModel {

    private final long sectionId;
    private final List<Entry> entries;


    public static class Entry {

        private final long courseId;
        private final long sectionId;
        private final long studentId;
        private final String SisUserId;
        private final String studentName;
        private final int sumMinutesMadeup;
        private final int remainingMinutesMadeup;
        private final int sumMinutesMissed;
        private final double percentCourseMissed;
        private final boolean dropped;
        private final int totalClassesMissed;
        private final int totalClassesExcused;
        private final int totalClassesTardy;
        private final int totalClassesPresent;


        public Entry(long courseId, long sectionId, long studentId,
                     String sisUserId, String studentName, boolean dropped, int sumMinutesMadeup, int remainingMinutesMadeup,
                     int sumMinutesMissed, double percentCourseMissed) {

            this.courseId = courseId;
            this.sectionId = sectionId;
            this.studentId = studentId;
            this.SisUserId = sisUserId;
            this.studentName = studentName;
            this.dropped = dropped;
            this.sumMinutesMadeup = sumMinutesMadeup;
            this.remainingMinutesMadeup = remainingMinutesMadeup;
            this.sumMinutesMissed = sumMinutesMissed;
            this.percentCourseMissed = percentCourseMissed;
            this.totalClassesExcused = -1;
            this.totalClassesMissed = -1;
            this.totalClassesTardy = -1;
            this.totalClassesPresent = -1;
        }

        public Entry(long courseId, long sectionId, long studentId,
                     String SisUserId, String studentName, boolean dropped, int totalClassesTardy, int totalClassesMissed, int totalClassesExcused, int totalClassesPresent) {

            this.courseId = courseId;
            this.sectionId = sectionId;
            this.studentId = studentId;
            this.SisUserId = SisUserId;
            this.studentName = studentName;
            this.dropped = dropped;
            this.totalClassesExcused = totalClassesExcused;
            this.sumMinutesMadeup = -1;
            this.remainingMinutesMadeup = -1;
            this.sumMinutesMissed = -1;
            this.percentCourseMissed = -1;
            this.totalClassesMissed = totalClassesMissed;
            this.totalClassesTardy = totalClassesTardy;
            this.totalClassesPresent = totalClassesPresent;
        }


        public long getCourseId() {
            return courseId;
        }


        public long getSectionId() {
            return sectionId;
        }


        public long getStudentId() {
            return studentId;
        }


        public String getStudentName() {
            return studentName;
        }


        public int getSumMinutesMadeup() {
            return sumMinutesMadeup;
        }


        public int getRemainingMinutesMadeup() {
            return remainingMinutesMadeup;
        }


        public int getSumMinutesMissed() {
            return sumMinutesMissed;
        }


        public double getPercentCourseMissed() {
            return percentCourseMissed;
        }

        public boolean isDropped() {
            return dropped;
        }

        public int getTotalClassesTardy() {
            return totalClassesTardy;
        }

        public int getTotalClassesMissed() {
            return totalClassesMissed;
        }

        public int getTotalClassesExcused() {
            return totalClassesExcused;
        }


        public int getTotalClassesPresent() {
            return totalClassesPresent;
        }

        public String getSisUserId() {
            return SisUserId;
        }
    }


    public AttendanceSummaryModel(long sectionId) {
        entries = new ArrayList<Entry>();

        this.sectionId = sectionId;
    }

    public void add(Entry entry) {
        entries.add(entry);
    }

    public long getSectionId() {
        return sectionId;
    }

    public List<Entry> getEntries() {
        return entries;
    }

}
