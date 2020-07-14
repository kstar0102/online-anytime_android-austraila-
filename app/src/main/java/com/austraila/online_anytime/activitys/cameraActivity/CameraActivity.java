package com.austraila.online_anytime.activitys.cameraActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.austraila.online_anytime.R;
import com.austraila.online_anytime.activitys.FormActivity;

import java.util.List;

public class CameraActivity extends AppCompatActivity {
    private Button btnCapture;
    private ImageView imgCapture;
    private Uri imageUri;
    Bitmap thumbnail;
    public static final int Image_Capture_Code = 1;
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        ContentValues values = new ContentValues();
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cInt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cInt.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(cInt,Image_Capture_Code);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case Image_Capture_Code:
                if (requestCode == Image_Capture_Code)
                    if (resultCode == Activity.RESULT_OK) {
                        try {
                            thumbnail = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                            imgCapture.setImageBitmap(thumbnail);
                            String imageurl = getRealPathFromURI(imageUri);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
