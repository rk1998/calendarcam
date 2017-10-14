package com.calendar.cam.calendarcam.Model;

/**
 * Created by David Goldfarb 
 */

public class CalendarInteraction {
    private String eventName;
    private String eventDate;
    private String eventTime;
/** Creates a CalendarInteraction with parameters
 *  @param eventName the eventName
 *  @param eventDate the eventDate
 *  @param eventTime the eventTime*/
    public CalendarInteraction(String eventName, String eventDate, String eventTime) {
        this.eventDate = eventDate;
        this.eventName = eventName;
        this.eventTime = eventTime;
    }
    public String toQuickAdd() {
        return this.eventName + " on " + this.eventDate + " at " + this.eventTime;
    }


}
