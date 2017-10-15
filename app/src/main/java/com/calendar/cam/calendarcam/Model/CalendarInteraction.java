package com.calendar.cam.calendarcam.Model;


import java.util.Calendar;

/**
 * Class representing a calendar event
 */

public class CalendarInteraction {
    private String eventName;
    private Calendar startTime = Calendar.getInstance();
    private Calendar endTime = Calendar.getInstance();
/** Creates a CalendarInteraction with parameters
 *  @param eventName the eventName
 *  @param eventYear the eventDate
 *  @param eventMonth the eventMonth
 *  @param eventDay the date number
 *  @param eventStartTime the startTime contains AM/PM
 *  @param eventEndTime the endTime contains AM/PM
 */
    public CalendarInteraction(String eventName, int eventYear, int eventMonth, int eventDay,
                               String eventStartTime, String eventEndTime) {
        int[] startTimeAr = processTime(eventStartTime);
        int[] endTimeAr = processTime(eventEndTime);
        this.eventName = eventName;
        this.startTime.set(eventYear, eventMonth - 1, eventDay, startTimeAr[0], startTimeAr[1]);
        this.endTime.set(eventYear, eventMonth - 1, eventDay, endTimeAr[0], endTimeAr[1]);
    }

    /**
     * @param time 11:00AM
     * @return an integer representation 11
     */
    private int[] processTime(String time) {
        String signifier = time.substring(time.length() - 2, time.length());
        int toAdd = signifier.equals("AM") ? 0 : 12;
        String shour;
        String smin = "00";
        int[] toRet = {0, 0};
        if (time.length() > 4) {
            //Colon is here
            shour = time.substring(0, time.indexOf(":"));
            smin = time.substring(time.indexOf(":") + 1, time.indexOf(":") + 3);
        } else {
            //No colon
            shour = time.length() < 4 ? time.substring(0, 1): time.substring(0, 2);
        }
        if (toAdd == 0) {
            if (shour.equals("12")) {
                toRet[0] = 0;
            } else {
                toRet[0] = Integer.parseInt(shour);
            }
        } else {
            if (shour.equals("12")) {
                toRet[0] = 12;
            } else {
                toRet[0] = Integer.parseInt(shour) + toAdd;
            }

        }
        toRet[1] = Integer.parseInt(smin);
        return toRet;
    }
    /**
     * @return eventName
     * */
    public String getEventName() {
        return this.eventName;
    }
    public int getStartYear() { return this.startTime.get(Calendar.YEAR); }
    public int getStartMonth() { return this.startTime.get(Calendar.MONTH); }
    public int getStartDay() { return this.startTime.get(Calendar.DATE); }
    public int getStartHour() { return this.startTime.get(Calendar.HOUR); }
    public int getStartMinute() { return this.startTime.get(Calendar.MINUTE); }
    public void setStartYear(int year) { this.startTime.set(Calendar.YEAR, year); }
    public void setStartMonth(int month) { this.startTime.set(Calendar.MONTH, month); }
    public void setStartDay(int day) { this.startTime.set(Calendar.DATE, day); }
    public void setStartHour(int hour) { this.startTime.set(Calendar.HOUR, hour); }
    public void setStartMinute(int minute) { this.startTime.set(Calendar.MINUTE, minute); }
    public int getEndYear() { return this.endTime.get(Calendar.YEAR); }
    public int getEndMonth() { return this.endTime.get(Calendar.MONTH); }
    public int getEndDay() { return this.endTime.get(Calendar.DATE); }
    public int getEndHour() { return this.endTime.get(Calendar.HOUR); }
    public int getEndMinute() { return this.endTime.get(Calendar.MINUTE); }
    public void setEndYear(int year) { this.endTime.set(Calendar.YEAR, year); }
    public void setEndMonth(int month) { this.endTime.set(Calendar.MONTH, month); }
    public void setEndDay(int day) { this.endTime.set(Calendar.DATE, day); }
    public void setEndHour(int hour) { this.endTime.set(Calendar.HOUR, hour); }
    public void setEndMinute(int minute) { this.endTime.set(Calendar.MINUTE, minute); }
}
