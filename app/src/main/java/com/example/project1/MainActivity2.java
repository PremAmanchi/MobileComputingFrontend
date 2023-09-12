package com.example.project1;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

public class MainActivity2 extends AppCompatActivity {

    float[] predictions;
    float[] prediction1, prediction2, prediction3, prediction4;
    String encoded_img, client_ip;

    public String getCurrentTimeString() {
        int yyyy = Calendar.getInstance().get(Calendar.YEAR);
        int MM = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int dd = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int hh = Calendar.getInstance().get(Calendar.HOUR);
        int mm = Calendar.getInstance().get(Calendar.MINUTE);
        int ss = Calendar.getInstance().get(Calendar.SECOND);

        String result = yyyy+"-"+MM+"-"+dd+" "+hh+":"+mm+":"+ss;
        return result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Button bt=(Button)findViewById(R.id.uploadBtn);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap image = getIntent().getParcelableExtra("captured_image");
                encoded_img = getIntent().getStringExtra("encoded_img1");
                client_ip = "http://192.168.0.185:8080";
                new upload_image().execute(client_ip, encoded_img);
                prediction1 = predictions;

//                encoded_img = getIntent().getStringExtra("encoded_img2");
//                client_ip = "http://192.168.0.185:8080";
//                new upload_image().execute(client_ip, encoded_img);
//                prediction2 = predictions;
//
//                encoded_img = getIntent().getStringExtra("encoded_img3");
//                client_ip = "http://192.168.0.185:8080";
//                new upload_image().execute(client_ip, encoded_img);
//                prediction3 = predictions;
//
//                encoded_img = getIntent().getStringExtra("encoded_img4");
//                client_ip = "http://192.168.0.185:8080";
//                new upload_image().execute(client_ip, encoded_img);
//                prediction4 = predictions;

                try {
                    String root = Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES).toString();
                    File myDir = new File(root + "/saved_images");
                    myDir.mkdirs();
                    String fname = getCurrentTimeString().replaceAll(":", "-") + ".jpg";
                    File file = new File(myDir, fname);
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    image.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                    fileOutputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    class upload_image extends AsyncTask<String, Void, Integer> {
        URL url;
        HttpURLConnection client;
        int code;

        @Override
        protected Integer doInBackground(String... data) {
            url = null;
            try {
                url = new URL(data[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try {
                JSONObject postData = new JSONObject();
                postData.put("encoded_image", data[1]);

                client = (HttpURLConnection) url.openConnection();
                client.setRequestProperty("Content-Type", "application/json");
                client.setRequestMethod("POST");
                client.setDoInput(true);
                client.setDoOutput(true);
                OutputStream outputPost = new BufferedOutputStream(client.getOutputStream());
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                        outputPost, "UTF-8"));
                writer.write(postData.toString());
                writer.flush();
                InputStream input = new BufferedInputStream(client.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        input, "UTF-8"));

                StringBuilder sb = new StringBuilder();
                sb.append(reader.readLine() + "\n");
                String line="0";
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                input.close();
                String result = sb.toString();

                String[] string = result.replaceAll("\\[", "")
                        .replaceAll("]", "")
                        .split(",");

                predictions = new float[string.length];

                for (int i = 0; i < string.length; i++) {
                    predictions[i] = Float.valueOf(string[i]);
                }

                code = client.getResponseCode();
                if (code != 200) {
                    throw new IOException("Upload was unsuccessful - return code: " + code);
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } finally {
                if (client != null) {
                    client.disconnect();
                }
            }
            return code;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if(result != 200){
                Toast.makeText(getBaseContext(),"Upload was Unsuccessful, Please Try again",Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(getBaseContext(),"Upload was successful",Toast.LENGTH_LONG).show();
            }
        }
    }
}