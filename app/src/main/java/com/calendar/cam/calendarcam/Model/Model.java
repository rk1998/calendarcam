package com.calendar.cam.calendarcam.Model;

import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.text.TextBlock;
import com.google.firebase.ml.vision.text.FirebaseVisionText;

import java.util.ArrayList;
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
        int month = 0;
        int day = 0;
        String startTime = "7PM";
        String endTime = "7PM";
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
            String[] textArray = textValue.toLowerCase().split("\\s+|-");
            // Find Time

            int firstAMIdx = stringContainsSearch("am", textArray, 0);
            int firstPMIdx = stringContainsSearch("pm", textArray, 0);
            Log.d("FirstAMIdx:", ""+firstAMIdx);
            Log.d("FirstPMIdx:", ""+firstPMIdx);
            if (firstAMIdx != -1 || firstPMIdx != -1) {
                //Check if array entry contains number, if not, earlier does
                if (firstAMIdx != -1) {
                    //Looking at AM
                    if (Character.isDigit(textArray[firstAMIdx].charAt(0))) {
                        startTime = textArray[firstAMIdx];
                    } else {
                        startTime = textArray[firstAMIdx - 1] + textArray[firstAMIdx];
                    }
                } else {
                    //Looking at PM
                    if (Character.isDigit(textArray[firstPMIdx].charAt(0))) {
                        startTime = textArray[firstPMIdx];
                    } else {
                        startTime = textArray[firstPMIdx - 1] + textArray[firstPMIdx];
                    }

                }
            }
            int secondAMIdx = stringContainsSearch("am", textArray, firstAMIdx + 1);
            int secondPMIdx = stringContainsSearch("pm", textArray, firstPMIdx + 1);
            if (secondAMIdx != -1 || secondPMIdx != -1) {
                //Check if array entry contains number, if not, earlier does
                if (secondAMIdx != -1) {
                    //Looking at AM
                    if (Character.isDigit(textArray[secondAMIdx].charAt(0))) {
                        endTime = textArray[secondAMIdx];
                    } else {
                        endTime = textArray[secondAMIdx - 1] + textArray[secondAMIdx];
                    }
                } else {
                    //Looking at PM
                    if (Character.isDigit(textArray[secondPMIdx].charAt(0))) {
                        endTime = textArray[secondPMIdx];
                    } else {
                        endTime = textArray[secondPMIdx - 1] + textArray[secondPMIdx];
                    }

                }
            }


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
                        if (result == 0) {
                            day = Integer.parseInt(textArray[result + 1]);
                        } else if (Character.isDigit(textArray[result -1].charAt(0))) {
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
        CalendarInteraction calendar = new CalendarInteraction(eventName,
                2017, month, day, startTime, endTime);
/*        Log.d("Day", "" + day);
        Log.d("Month", ""+ month);
        Log.d("startTime", startTime);
        Log.d("Month after", "" + calendar.getStartMonth());
        Log.d("Year", "" + ""+calendar);*/
        return calendar;
    }

    /**
     * Processes Text blocks found by OCR API, this method uses Firebase ML Vision API
     * @param blocks blocks of text found by API
     * @return Calendar Interaction event with parsed Date/Time info
     */
    public CalendarInteraction process_text_boxes(List<FirebaseVisionText.Block> blocks) {
        if(blocks.isEmpty()) {
            throw new IllegalArgumentException("No Text Detected");
        }
        String eventName = "";
        int month = 0;
        int day = 0;
        String startTime = "7PM";
        String endTime = "7PM";
        int maxArea = Integer.MIN_VALUE;
        int index = 0;
        for(int i = 0; i < blocks.size(); i++) {
            FirebaseVisionText.Block textBlock = blocks.get(i);
            int bboxArea = textBlock.getBoundingBox().height() * textBlock.getBoundingBox().width();
            if(bboxArea > maxArea) {
                maxArea = bboxArea;
                index = i;
            }
        }
        eventName = blocks.get(index).getText();
        for(int i = 0; i < blocks.size(); i++) {
            FirebaseVisionText.Block text = blocks.get(i);
            String textValue = text.getText();
            Log.d("TEXT: ", textValue);
            Month[] months = Month.values();
            String[] textArray = textValue.toLowerCase().split("\\s+|-");

            // Find Time
            //TODO: there is a problem when words contain "am" or "pm" in them, need to check
            //if these strings have any numbers in them
            int firstAMIdx = stringContainsSearch("am", textArray, 0);
            int firstPMIdx = stringContainsSearch("pm", textArray, 0);

            Log.d("FirstAMIdx:", ""+firstAMIdx);
            Log.d("FirstPMIdx:", ""+firstPMIdx);
            if (firstAMIdx != -1 || firstPMIdx != -1) {
                //Check if array entry contains number, if not, earlier does
                if (firstAMIdx != -1) {
                    //Looking at AM
                    if (Character.isDigit(textArray[firstAMIdx].charAt(0))) {
                        startTime = textArray[firstAMIdx];
                    } else {
                        startTime = textArray[firstAMIdx - 1] + textArray[firstAMIdx];
                    }
                } else {
                    //Looking at PM
                    if(textArray.length == 1) {
                        if(Character.isDigit(textArray[firstPMIdx].charAt(0))) {
                            startTime = textArray[firstPMIdx];
                        }
                    } else {
                        if (Character.isDigit(textArray[firstPMIdx].charAt(0))) {
                            startTime = textArray[firstPMIdx];
                        } else {
                            startTime = textArray[firstPMIdx - 1] + textArray[firstPMIdx];
                        }
                    }

                }
            }
            int secondAMIdx = stringContainsSearch("am", textArray, firstAMIdx + 1);
            int secondPMIdx = stringContainsSearch("pm", textArray, firstPMIdx + 1);
            if (secondAMIdx != -1 || secondPMIdx != -1) {
                //Check if array entry contains number, if not, earlier does
                if (secondAMIdx != -1) {
                    //Looking at AM
                    if (Character.isDigit(textArray[secondAMIdx].charAt(0))) {
                        endTime = textArray[secondAMIdx];
                    } else {
                        endTime = textArray[secondAMIdx - 1] + textArray[secondAMIdx];
                    }
                } else {
                    //Looking at PM
                    if (Character.isDigit(textArray[secondPMIdx].charAt(0))) {
                        endTime = textArray[secondPMIdx];
                    } else {
                        endTime = textArray[secondPMIdx - 1] + textArray[secondPMIdx];
                    }

                }
            }


            for(Month m: months) {

                int zeroresult = -1;
                int zeroAbrvResult = -1;
                //Special case,
                if (m.get_longForm().equals("october")) {
                    zeroresult = stringSearch("0ctober", textArray);
                    zeroAbrvResult = stringSearch("0ct", textArray);
                }
                int result = stringSearch(m.get_longForm(), textArray);
                int abbrvResult = stringSearch(m.get_abbreviation(), textArray);
                if(result != -1 || zeroresult != -1) {
                    month = m.get_monthNum();
                    //Todo: Need to handle case when day field has "th" i.e. "October 9th"
                    if (zeroresult != -1) {
                        if (Character.isDigit(textArray[zeroresult - 1].charAt(0))) {
                            day = Integer.parseInt(textArray[zeroresult - 1]);
                        } else {
                            day = Integer.parseInt(textArray[zeroresult + 1]);
                        }
                        break;
                    } else {
                        if (result == 0) {
                            day = Integer.parseInt(textArray[result + 1]);
                        } else if (Character.isDigit(textArray[result -1].charAt(0))) {
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
                        if(abbrvResult == 0) {
                            day = Integer.parseInt(textArray[abbrvResult + 1]);
                        } else if (Character.isDigit(textArray[abbrvResult - 1].charAt(0))) {
                            day = Integer.parseInt(textArray[abbrvResult - 1]);
                        } else {
                            day = Integer.parseInt(textArray[abbrvResult + 1]);
                        }
                        break;
                    }
                }
            }

        }
        CalendarInteraction calendar = new CalendarInteraction(eventName,
                2017, month, day, startTime, endTime);
/*        Log.d("Day", "" + day);
        Log.d("Month", ""+ month);
        Log.d("startTime", startTime);
        Log.d("Month after", "" + calendar.getStartMonth());
        Log.d("Year", "" + ""+calendar);*/
        return calendar;
    }

    /**
     * stringSearch searches for a pattern in a block of text and returns the word index
     * @param pattern the pattern
     * @param textArray the block array
     * @return an integer representing the index of the first pattern match, -1 if not found
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
     * stringContainsSearch searches for a subpattern in each "word" of a block of text, returning
     * the index of the word
     * @param subPattern the subpattern
     * @param textArray the block array
     * @param startingPoint the value in the block array to begin with.
     * @return an integer representing the index of the first subpattern match, -1 if not found
     */
    private int stringContainsSearch(String subPattern, String[] textArray, int startingPoint) {
        for (int i = startingPoint; i < textArray.length; i++) {
            //Log.d("Pattern is ", textArray[i]);
            if (textArray[i].contains(subPattern)) {
                return i;
            }
        }
        return -1;
    }



}
