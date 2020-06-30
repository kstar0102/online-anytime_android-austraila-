package com.austraila.online_anytime.activitys.cameraActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.austraila.online_anytime.R;
import com.austraila.online_anytime.activitys.FormActivity;

public class CameraActivity extends AppCompatActivity {
    private Button btnCapture;
    private ImageView imgCapture;
    public static final int Image_Capture_Code = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
//        btnCapture =(Button)findViewById(R.id.btnTakePicture);
//        imgCapture = (ImageView) findViewById(R.id.capturedImage);
        Intent cInt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cInt,Image_Capture_Code);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Image_Capture_Code) {
            if (resultCode == RESULT_OK) {
                Bitmap bp = (Bitmap) data.getExtras().get("data");
                Intent intent = new Intent(CameraActivity.this, FormActivity.class);
                intent.putExtra("photoImage", bp);
                startActivity(intent);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            }
        }
    }
}
