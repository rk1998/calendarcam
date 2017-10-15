package com.calendar.cam.calendarcam.Model;

import android.util.Log;
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
        JANUARY("jan", "january", 1),
        FEBRUARY("feb", "february", 2),
        MARCH("mar", "march", 3),
        APRIL("apr", "april", 4),
        MAY("may", "may", 5),
        JUNE("jun", "june", 6),
        JULY("jul", "july", 7),
        AUGUST("aug", "august", 8),
        SEPTEMBER("sept", "september", 9),
        OCTOBER("oct", "october", 10),
        NOVEMBER("nov", "november", 11),
        DECEMBER("dec", "december", 12);


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
    private static final int BASE = 599;
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
        if(camTextArray.size() == 0) {
            throw new IllegalArgumentException("No Text Detected");
        }
        String eventName = "";
        String date = "";
        String time = "";
        int month = 0;
        int day = 0;
        List<TextBlock> camTextList = new LinkedList<>();
        int maxArea = Integer.MIN_VALUE;
        int index = 0;
        for(int i = 0; i < camTextArray.size(); i++) {
            TextBlock text = camTextArray.valueAt(i);
            int bboxArea = text.getBoundingBox().height() * text.getBoundingBox().width();
            if(bboxArea > maxArea) {
                maxArea = bboxArea;
                index = i;
            }
        }
        eventName = camTextArray.valueAt(index).getValue();
        for(int i = 0; i < camTextArray.size(); i++) {
            TextBlock text = camTextArray.valueAt(i);
            String textValue = text.getValue();
            Month[] months = Month.values();
            String[] textArray = textValue.toLowerCase().split("\\s+");
            for(Month m: months) {

                int zeroresult = -1;
                int zeroAbrvResult = -1;
                if (m.get_longForm().equals("october")) {
                    zeroresult = stringSearch("0ctober", textArray);
                    zeroAbrvResult = stringSearch("0ct", textArray);
                }
                int result = stringSearch(m.get_longForm(), textArray);
                int abbrvResult = stringSearch(m.get_abbreviation(), textArray);
                if(result != -1 || zeroresult != -1) {
                    month = m.get_monthNum();
                    if (zeroresult != -1) {
                        if (Character.isDigit(textArray[zeroresult -1].charAt(0))) {
                            day = Integer.parseInt(textArray[zeroresult - 1]);
                        } else {
                            day = Integer.parseInt(textArray[zeroresult + 1]);
                        }
                        break;
                    } else {
                        if (Character.isDigit(textArray[result -1].charAt(0))) {
                            day = Integer.parseInt(textArray[result - 1]);
                        } else {
                            day = Integer.parseInt(textArray[result + 1]);
                        }
                        break;
                    }



                } else if(abbrvResult != -1 || zeroAbrvResult != -1) {
                    month = m.get_monthNum();
                    if (zeroAbrvResult != -1) {
                        if (Character.isDigit(textArray[zeroAbrvResult - 1].charAt(0))) {
                            day = Integer.parseInt(textArray[zeroAbrvResult - 1]);
                        } else {
                            day = Integer.parseInt(textArray[zeroAbrvResult + 1]);
                        }
                        break;
                    } else {
                        if (Character.isDigit(textArray[abbrvResult - 1].charAt(0))) {
                            day = Integer.parseInt(textArray[abbrvResult - 1]);
                        } else {
                            day = Integer.parseInt(textArray[abbrvResult + 1]);
                        }
                        break;
                    }
                }
            }

        }
        CalendarInteraction calendar = new CalendarInteraction(eventName, 2017, month, day, "5PM", "7PM");
        Log.d("Day", "" + day);
        Log.d("Month", ""+ month);
        Log.d("Month after", "" + calendar.getStartMonth());
        Log.d("Year", "" + ""+calendar);
        return calendar;
    }

    /**
     * stringSearch searches for a pattern in a block of text and returns the word index
     * @param pattern the pattern
     * @param textArray the block array
     * @return an integer representing the index of the first pattern match
     */
    private int stringSearch(String pattern, String[] textArray) {
        for (int i = 0; i < textArray.length; i++) {
            if (textArray[i].equals(pattern)) {
                return i;
            }
        }
        return -1;
    }



}
