package com.calendar.cam.calendarcam.Model;

import android.util.SparseArray;

import com.google.android.gms.vision.text.TextBlock;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * This class interfaces with the activity classes
 */

public class Model {
    public enum Month {
        JANUARY("Jan", "January", 1),
        FEBRUARY("Feb", "February", 2),
        MARCH("Mar", "March", 3),
        APRIL("Apr", "April", 4),
        MAY("May", "May", 5),
        JUNE("Jun", "June", 6),
        JULY("JUL", "July", 7),
        AUGUST("Aug", "August", 8),
        SEPTEMBER("Sept", "September", 9),
        OCTOBER("Oct", "October", 10),
        NOVEMBER("Nov", "November", 11),
        DECEMBER("Dec", "December", 12);


        private final String _abbreviation;
        private final String _longForm;
        private final int _monthNum;
        Month(String abbrv, String longForm, int monthNum) {
            _abbreviation = abbrv;
            _longForm = longForm;
            _monthNum = monthNum;

        }
        public String get_abbreviation() {
            return _abbreviation;
        }
        public String get_longForm() {
            return _longForm;
        }
        public int get_monthNum() {
            return _monthNum;
        }
    }
    /**Singleton Instance**/
    private static final Model _instance = new Model();
    /**
     * Gets singleton instance of model
     * @return The singleton instance of model
     */
    public static Model get_instance() {return _instance;}

    List<String> months;

    private Model() {
        months = new ArrayList<>();


    }
    public CalendarInteraction process_text_boxes(SparseArray<TextBlock> camTextArray) {
        String eventName = "";
        String date = "";
        String time = "";
        List<TextBlock> camTextList = new LinkedList<>();
        int maxArea = Integer.MIN_VALUE;
        int index = 0;
        for(int i = 0; i < camTextArray.size(); i++) {
            TextBlock text = camTextArray.valueAt(i);
            int bboxArea = text.getBoundingBox().height() * text.getBoundingBox().width();
            if(bboxArea > maxArea) {

            }
        }

        return new CalendarInteraction("Bingus", "Boingus", "Dingus");

    }


}
