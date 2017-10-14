package com.calendar.cam.calendarcam.Model;

import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;


public class OcrDetectorProcessor implements Detector.Processor<TextBlock> {

    private String text;

    public OcrDetectorProcessor() {
        this.text = "";
    }

    @Override
    public void receiveDetections(Detector.Detections<TextBlock> detections) {
        SparseArray<TextBlock> items = detections.getDetectedItems();
        for (int i = 0; i < items.size(); ++i) {
            TextBlock item = items.valueAt(i);
            if (item != null && item.getValue() != null) {
                Log.i("OcrDetectorProcessor", "Text detected! " + item.getValue());
            }
            this.text = this.text + item.getValue();
        }
    }

    public String getText() {
        return this.text;
    }

    @Override
    public void release() {}
}
