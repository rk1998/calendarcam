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
            String[] textArray = textValue.split("\\s+");
            for(Month m: months) {
                /*List<Integer> result = rabinKarp(m.get_longForm(), textValue);
                Log.d("Textbox Value", textValue);
                List<Integer> abbrvResult = rabinKarp(m.get_abbreviation(), textValue);*/
                int result = stringSearch(m.get_longForm(), textArray);
                int abbrvResult = stringSearch(m.get_abbreviation(), textArray);
                if(result != -1 || abbrvResult != -1) {
                    /*month = m.get_monthNum();
                    String dayString = "";
                    int k = result.get(0) - 3;
                    int temp = result.get(0) - 1;
                    String tempDay = "";
                    while(k < temp) {
                        dayString += textValue.charAt(k);
                        k++;
                    }
                    day = Integer.parseInt(dayString); */
                    month = m.get_monthNum();
                    if (Character.isDigit(textArray[result -1].charAt(0))) {
                        day = Integer.parseInt(textArray[result - 1]);
                    } else {
                        day = Integer.parseInt(textArray[result + 1]);
                    }
                    break;


                } else if(abbrvResult != -1) {
                    /*month = m.get_monthNum();
                    month = m.get_monthNum();
                    String dayString = "";
                    int k = abbrvResult.get(0) - 3;
                    int temp = abbrvResult.get(0) - 1;
                    String tempDay = "";
                    while(k < temp) {
                        dayString += textValue.charAt(k);
                        k++;
                    }
                    day = Integer.parseInt(dayString); */
                    month = m.get_monthNum();
                    if (Character.isDigit(textArray[abbrvResult - 1].charAt(0))) {
                        day = Integer.parseInt(textArray[abbrvResult - 1]);
                    } else {
                        day = Integer.parseInt(textArray[abbrvResult + 1]);
                    }
                    break;
                }

            }

        }

        

        return new CalendarInteraction(eventName, 2017, month, day, "5PM", "7PM");

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


    /**
     * Runs Rabin-Karp algorithm. Generate the pattern hash, and compare it with
     * the hash from a substring of text that's the same length as the pattern.
     * If the two hashes match, compare their individual characters, else update
     * the text hash and continue.
     *
     * @throws IllegalArgumentException if the pattern is null or of length 0
     * @throws IllegalArgumentException if text is null
     * @param pattern a string you're searching for in a body of text
     * @param text the body of text where you search for pattern
     * @return list containing the starting index for each match found
     */
    /*private  List<Integer> rabinKarp(CharSequence pattern,
                                          CharSequence text) {

        List<Integer> matches = new LinkedList<>();
        if (pattern == null || pattern.length() == 0) {
            throw new IllegalArgumentException("Cannot use null pattern "
                    + "or zero length pattern");
        }
        if (text == null) {
            throw new IllegalArgumentException("Cannot use null text");
        }
        if (pattern.length() > text.length()) {
            return  matches;
        }

        int textHash = generateHash(text, pattern.length());
        int patternHash = generateHash(pattern, pattern.length());
        int i = 0;
        while (i <= text.length() - pattern.length()) {
            if (patternHash == textHash) {
                int j = 0;
                while (j < pattern.length()
                        && text.charAt(i + j) == pattern.charAt(j)) {
                    j++;

                }
                if (j == pattern.length()) {
                    matches.add(i);
                }

            }
            i++;
            if (i <= text.length() - pattern.length()) {
                char oldChar = text.charAt(i - 1);
                char newChar = text.charAt(i + pattern.length() - 1);
                textHash = updateHash(textHash, pattern.length(),
                        oldChar, newChar);
            }
        }
        return matches;

    }
    private int generateHash(CharSequence current, int length) {
        if (current == null) {
            throw new IllegalArgumentException("Cannot generate hash "
                    + "of null CharSequence");
        }
        if (length <= 0 || length > current.length()) {
            throw new IllegalArgumentException("Cannot use negative length or "
                    + "length that is greater than current.length()");
        }
        int hash = 0;
        for (int i = 0; i < length; i++) {
            hash += current.charAt(i) * Math.pow(BASE, length - 1 - i);
        }
        return hash;

    }

    private int updateHash(int oldHash, int length, char oldChar,
                                 char newChar) {
        if (length <= 0) {
            throw new IllegalArgumentException("Cannot use a negative or "
                    + "zero length");
        }
        return BASE * (oldHash - oldChar * (int) Math.pow(BASE, length - 1))
                + newChar;

    }

*/
}
