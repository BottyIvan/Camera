package com.botty.camera;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;


public class MainActivity extends ActionBarActivity implements DialogInterface.OnClickListener,
        SurfaceHolder.Callback, Camera.PictureCallback, View.OnClickListener {
    SurfaceView cameraView;
    SurfaceHolder surfaceHolder;
    Camera camera;
    ImageButton mTake;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cameraView = (SurfaceView) this.findViewById(R.id.preview);
        mTake = (ImageButton) this.findViewById(R.id.mtake);
        surfaceHolder = cameraView.getHolder();
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.addCallback(this);
        cameraView.setFocusable(true);
        cameraView.setFocusableInTouchMode(true);
        cameraView.setClickable(true);
        cameraView.setOnClickListener(this);
        mTake.setOnClickListener(this);

    }
    public void onClick(View v) {
        camera.takePicture(null, null, this);
    }
    public void onPictureTaken(byte[] data, Camera camera) {
        Uri imageFileUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
        try {
            OutputStream imageFileOS = getContentResolver().openOutputStream(
                    imageFileUri);
            imageFileOS.write(data);
            imageFileOS.flush();
            imageFileOS.close();
        } catch (Exception e) {
            Toast t = Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT);
            t.show();
        }
        camera.startPreview();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        camera.startPreview();
    }

    public void surfaceCreated(SurfaceHolder holder) {
        camera = Camera.open();
        try {
            camera.setPreviewDisplay(holder);
            Camera.Parameters parameters = camera.getParameters();
            if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                parameters.set("orientation", "portrait");
                camera.setDisplayOrientation(90);
            }
            List<String> colorEffects = parameters.getSupportedColorEffects();
            Iterator<String> cei = colorEffects.iterator();
            while (cei.hasNext()) {
                String currentEffect = cei.next();
                if (currentEffect.equals(Camera.Parameters.EFFECT_SOLARIZE)) {
                    parameters.setColorEffect(Camera.Parameters.EFFECT_SOLARIZE);
                    break;
                }
            }
            camera.setParameters(parameters);
        } catch (IOException exception) {
            camera.release();
        }
    }
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }
}
