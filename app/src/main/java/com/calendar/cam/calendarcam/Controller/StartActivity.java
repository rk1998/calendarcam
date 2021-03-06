package com.calendar.cam.calendarcam.Controller;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.CalendarContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.calendar.cam.calendarcam.Model.CalendarInteraction;
import com.calendar.cam.calendarcam.Model.Model;
import com.calendar.cam.calendarcam.R;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.gms.vision.Frame;
//import com.google.android.gms.vision.text.TextBlock;
//import com.google.android.gms.vision.text.TextRecognizer;

import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;
import com.google.firebase.ml.vision.text.FirebaseVisionText;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;

public class StartActivity extends AppCompatActivity {
    private Button mTakePicButton;
    private Button mImportPicButton;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_IMPORT = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        mTakePicButton = (Button) findViewById(R.id.take_pic_button);
        /*mTakePicButton.setElevation(6);*/
        mImportPicButton = (Button) findViewById(R.id.import_pic_button);
        /*mImportPicButton.setElevation(6);*/

        mTakePicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Camera Intent", "Attempting to Launch Camera Intent");
                launchCameraIntent();
            }
        });

        mImportPicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Import Intent", "Attempting to Launch Import Intent");
                launchImportIntent();
            }
        });

    }

    private void launchImportIntent() {
        Intent importIntent = new Intent(Intent.ACTION_PICK);
        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String path = pictureDirectory.getPath();
        Uri data = Uri.parse(path);
        importIntent.setDataAndType(data, "image/*");
        startActivityForResult(importIntent, REQUEST_IMAGE_IMPORT);
    }

    private void launchCameraIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap photo;

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            photo = (Bitmap) extras.get("data");
            this.getTextFromImage(photo);
        }

        if (requestCode == REQUEST_IMAGE_IMPORT && resultCode == RESULT_OK) {
            Uri uri = data.getData();

            try {
                InputStream stream = getContentResolver().openInputStream(uri);
                photo = BitmapFactory.decodeStream(stream);
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                photo =  Bitmap.createBitmap(photo, 0, 0, photo.getWidth(), photo.getHeight(), matrix, true);
                this.getTextFromImage(photo);
            } catch (IOException ioe) {
                ioe.printStackTrace();
                Toast.makeText(this, "Unable to open file", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void getTextFromImage(Bitmap image) {
        final Context context = getApplicationContext();
        FirebaseVisionImage fireImage = FirebaseVisionImage.fromBitmap(image);
        FirebaseVisionTextDetector detector  = FirebaseVision.getInstance().getVisionTextDetector();
        Task<FirebaseVisionText> result = detector.detectInImage(fireImage)
                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText firebaseVisionText) {
                        List<FirebaseVisionText.Block> textBlocks
                                = firebaseVisionText.getBlocks();
                        Model model = Model.get_instance();
                        try {
                            CalendarInteraction calendarEvent = model.process_text_boxes(textBlocks);
                            Intent calendarIntent = new Intent(Intent.ACTION_INSERT)
                                    .setData(CalendarContract.Events.CONTENT_URI)
                                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                                            calendarEvent.getStartTime().getTimeInMillis() )
                                    .putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                                            calendarEvent.getEndTime().getTimeInMillis())
                                    .putExtra(CalendarContract.Events.TITLE,
                                            calendarEvent.getEventName());
                            startActivity(calendarIntent);

                        } catch(IllegalArgumentException iae) {
                            Toast.makeText(context, iae.getMessage(), Toast.LENGTH_LONG).show();

                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "No Text Detected", Toast.LENGTH_LONG).show();
                    }
                });
//        TextRecognizer textRecognizer = new TextRecognizer.Builder(context).build();
//        Frame frame = new Frame.Builder().setBitmap(image).build();
//        SparseArray<TextBlock> items = textRecognizer.detect(frame);
//
//        Model model = Model.get_instance();
//
//        try {
//            CalendarInteraction calendarEvent = model.process_text_boxes(items);
//
//            Intent calendarIntent = new Intent(Intent.ACTION_INSERT)
//                    .setData(CalendarContract.Events.CONTENT_URI)
//                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, calendarEvent.getStartTime().getTimeInMillis() )
//                    .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, calendarEvent.getEndTime().getTimeInMillis())
//                    .putExtra(CalendarContract.Events.TITLE, calendarEvent.getEventName());
//            startActivity(calendarIntent);
//        } catch (IllegalArgumentException iae) {
//            System.out.println(iae.getMessage());
//            Toast.makeText(this, iae.getMessage(), Toast.LENGTH_LONG).show();
//        }

    }

}
