package com.calendar.cam.calendarcam.Controller;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.calendar.cam.calendarcam.Model.OcrDetectorProcessor;
import com.calendar.cam.calendarcam.R;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextRecognizer;

public class StartActivity extends AppCompatActivity {
    private Button mTakePicButton;
    private Button mImportPicButton;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mTakePicButton = (Button) findViewById(R.id.take_pic_button);
        mImportPicButton = (Button) findViewById(R.id.import_pic_button);

        mTakePicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Camera Intent", "Attempting to Launch Camera Intent");
                launchCameraIntent();
            }
        });

    }

    private void launchCameraIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap photo = (Bitmap) extras.get("data");
            Context context = getApplicationContext();
            TextRecognizer textRecognizer = new TextRecognizer.Builder(context).build();
            OcrDetectorProcessor processor = new OcrDetectorProcessor();
            textRecognizer.setProcessor(processor);
            Frame frame = new Frame.Builder().setBitmap(photo).build();
            textRecognizer.receiveFrame(frame);

            TextView text2 = (TextView) findViewById(R.id.text2);
            text2.append(processor.getText());
        }
    }



}
