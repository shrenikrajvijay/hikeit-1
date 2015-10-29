package com.mountaineer.trekking.hikeit.screens;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mountaineer.trekking.hikeit.R;
import com.mountaineer.trekking.hikeit.connector.HTTPImageDownload;
import com.mountaineer.trekking.hikeit.constants.Row;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by vijayshrenikraj on 4/21/15.
 */
public class secondScreen extends Activity implements SensorEventListener{

    private SensorManager sensorManager;
    private boolean color = false;
    private long lastUpdate;
    private GoogleMap GM;
    private ImageView v;
    private String imgUrl;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.secondscreen);

        Bundle bund = getIntent().getExtras();

        v = (ImageView) findViewById(R.id.secImage);
        TextView imageTitle= (TextView) findViewById(R.id.imageTitle);
        TextView imageAddr= (TextView) findViewById(R.id.imageAddr);

        v.setImageResource(R.drawable.image1);
        imageTitle.setText("" + bund.get("title"));
        imageAddr.setText("" + bund.get("address"));
        imgUrl = ""+bund.get("imageUrl");
        GM=((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        GM.addMarker(new MarkerOptions().position(new LatLng(bund.getDouble("latitude"), bund.getDouble("longitude")))

                .draggable(true));
        GM.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(bund.getDouble("latitude"), bund.getDouble("longitude")), 12.0f));
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        lastUpdate = System.currentTimeMillis();

        callImageDownload();
    }

    private void callImageDownload() {
        HTTPImageDownload down = new HTTPImageDownload();
        down.setConnectionListener(this, "secondscreen");
        try {
                down.connect(imgUrl, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void downloadCompleteBitmap(Bitmap bitmap, int imageID) {
        v.setImageBitmap(bitmap);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            displayAccelerometer(event);
            checkShake(event);
        }
    }

    private void checkShake(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float accelationSquareRoot = (x * x + y * y + z * z) / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        long actualTime = System.currentTimeMillis();
        if (accelationSquareRoot >= 1.5) {
            if (actualTime - lastUpdate < 200) {
                return;
            }
            lastUpdate = actualTime;
            //Toast.makeText(this, "Don't shake me!", Toast.LENGTH_SHORT).show();

            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "Here is the share content body";
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
            if (color) {
                //Toast.makeText(context, "Don't shake me!", Toast.LENGTH_SHORT).show();

            } else {
                //Toast.makeText(context, "Don't shake me!", Toast.LENGTH_SHORT).show();

            }
            color = !color;
        }
    }

    private void displayAccelerometer(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }


}
