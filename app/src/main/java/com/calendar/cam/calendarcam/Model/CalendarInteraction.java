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

    /**
     * @return startTime
     */
    public Calendar getEventStartTime() {
        return this.startTime;
    }
    /**
     * @return endTime
     */
    public Calendar getEventEndTime() {
        return this.endTime;
    }
}
