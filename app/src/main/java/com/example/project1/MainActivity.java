package com.example.project1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;


public class MainActivity extends AppCompatActivity{

    private static final int pic_id = 123;
    Button camera_open_id;
    ImageView click_image_id;
    String encoded_img1, encoded_img2, encoded_img3, encoded_img4;
    Bitmap image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        camera_open_id = findViewById(R.id.captureBtn);
        click_image_id = findViewById(R.id.imageView);
        OpenCVLoader.initDebug();

        camera_open_id.setOnClickListener(v -> {
            Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(camera_intent, pic_id);
        });

        Button btn = (Button)findViewById(R.id.nextBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intnt1 = new Intent(MainActivity.this,MainActivity2.class);
                intnt1.putExtra("encoded_img1", encoded_img1);
                intnt1.putExtra("encoded_img2", encoded_img2);
                intnt1.putExtra("encoded_img3", encoded_img3);
                intnt1.putExtra("encoded_img4", encoded_img4);
                intnt1.putExtra("captured_image", image);
                startActivity(intnt1);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == pic_id) {
            image = (Bitmap) data.getExtras().get("data");
            click_image_id.setImageBitmap(image);

            Bitmap bmp32 = image.copy(Bitmap.Config.ARGB_8888, true);
            Mat mat = new Mat();
            Utils.bitmapToMat(bmp32, mat);

            Mat mat_gray = new Mat();
            Mat mat_inv = new Mat();
            Mat resized_digit = new Mat();
            Mat padded_digit = new Mat();

            Imgproc.cvtColor(mat, mat_gray, Imgproc.COLOR_BGR2GRAY);
            Imgproc.threshold(mat_gray, mat_inv, 125, 255, Imgproc.THRESH_BINARY_INV);
            Imgproc.resize(mat_inv, resized_digit, new Size(18, 18));
            Core.copyMakeBorder(resized_digit, padded_digit, 5, 5, 5, 5, Core.BORDER_CONSTANT);

            Mat q1, q2, q3, q4;

            q1 = padded_digit.submat(0, 14, 0, 14);
            q2 = padded_digit.submat(14, 28, 0, 14);
            q3 = padded_digit.submat(0, 14, 14, 28);
            q4 = padded_digit.submat(14, 28, 14, 28);

            Bitmap q_bitmap = Bitmap.createBitmap(14, 14, Bitmap.Config.ARGB_8888);
            byte [] bitmapdata;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            String imgData;

            Utils.matToBitmap(q1, q_bitmap);
            q_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bitmapdata = bos.toByteArray();
            imgData = Base64.encodeToString(bitmapdata, Base64.DEFAULT);
            encoded_img1 = imgData.replace("\n", "%20");

            Utils.matToBitmap(q2, q_bitmap);
            q_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bitmapdata = bos.toByteArray();
            imgData = Base64.encodeToString(bitmapdata, Base64.DEFAULT);
            encoded_img2 = imgData.replace("\n", "%20");

            Utils.matToBitmap(q3, q_bitmap);
            q_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bitmapdata = bos.toByteArray();
            imgData = Base64.encodeToString(bitmapdata, Base64.DEFAULT);
            encoded_img3 = imgData.replace("\n", "%20");

            Utils.matToBitmap(q4, q_bitmap);
            q_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bitmapdata = bos.toByteArray();
            imgData = Base64.encodeToString(bitmapdata, Base64.DEFAULT);
            encoded_img4 = imgData.replace("\n", "%20");
        }
    }
}