package co.familyplay.androidprototype.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import co.familyplay.androidprototype.R;
import co.familyplay.androidprototype.adapter.NewPostAdapter;
import co.familyplay.androidprototype.dialog.Msg;
import co.familyplay.androidprototype.graphics.AnimatedGifEncoder;
import co.familyplay.androidprototype.graphics.Bmp;
import co.familyplay.androidprototype.object.Post;

//Visit the wiki for more info

public class CreateGIFActivity extends ActionBarActivity implements SurfaceHolder.Callback {
    private Context context;
    private List<Bitmap> bitmaps;
    private SharedPreferences prefs;
    private boolean isSchool, isGroup;
    private String from;

    private Camera camera;
    private SurfaceHolder surfaceHolder;

    private android.hardware.Camera.PictureCallback jpegCallback;
    private boolean isPreviewRunning = false;
    private int currentCameraId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_gif);
        context = CreateGIFActivity.this;
        isSchool = getIntent().getBooleanExtra("isSchool", false);
        isGroup = getIntent().getBooleanExtra("isGroup", false);
        try {
            from = getIntent().getStringExtra("from");
        } catch (Exception e) {
            e.printStackTrace();
        }
        bitmaps = new ArrayList<>();
        prefs = getSharedPreferences("appData", Context.MODE_PRIVATE);
        ImageButton record = (ImageButton) findViewById(R.id.btnRecord);

        record.setOnClickListener(new RecordPress());

        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        // Listener for displaying the captured image.
        jpegCallback = new android.hardware.Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, android.hardware.Camera camera) {
                try {
                    int screenWidth = getResources().getDisplayMetrics().widthPixels;
                    int screenHeight = getResources().getDisplayMetrics().heightPixels;
                    Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        Bitmap scaled = Bitmap.createScaledBitmap(bm, screenHeight, screenWidth, true);
                        int w = scaled.getWidth();
                        int h = scaled.getHeight();
                        Matrix mtx = new Matrix();
                        mtx.postRotate(90);
                        bm = Bitmap.createBitmap(scaled, 0, 0, w, h, mtx, true);
                    } else {
                        bm = Bitmap.createScaledBitmap(bm, screenWidth, screenHeight, true);
                    }
                    bitmaps.add(Bmp.scaleDownBitmap(bm, 200, context));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                refreshCamera();
            }
        };
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_gi, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_save:
                new GenerateGif().execute();
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera = Camera.open();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        Camera.Parameters param;
        param = camera.getParameters();

        param.setPreviewSize(352, 288);
        camera.setParameters(param);
        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (isPreviewRunning) {
            camera.stopPreview();
        }

        Camera.Parameters parameters = camera.getParameters();
        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

        if (display.getRotation() == Surface.ROTATION_0) {
            parameters.setPreviewSize(height, width);
            camera.setDisplayOrientation(90);
        }

        if (display.getRotation() == Surface.ROTATION_90) {
            parameters.setPreviewSize(width, height);
        }

        if (display.getRotation() == Surface.ROTATION_180) {
            parameters.setPreviewSize(height, width);
        }

        if (display.getRotation() == Surface.ROTATION_270) {
            parameters.setPreviewSize(width, height);
            camera.setDisplayOrientation(180);
        }

        camera.setParameters(parameters);
        refreshCamera();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    private void refreshCamera() {
        if (surfaceHolder.getSurface() == null) {
            return;
        }

        try {
            camera.stopPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
            isPreviewRunning = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Generate the GIF inside a background task.
    private class GenerateGif extends AsyncTask<String, String, ByteArrayOutputStream> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context);
            pd.setMessage("Rendering GIF...");
            pd.show();
        }

        @Override
        protected ByteArrayOutputStream doInBackground(String... params) {

            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                AnimatedGifEncoder encoder = new AnimatedGifEncoder();
                encoder.start(bos);
                encoder.setRepeat(0);
                for (Bitmap rb : bitmaps) {
                    encoder.addFrame(rb);
                }
                encoder.finish();
                return bos;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ByteArrayOutputStream bos) {
            super.onPostExecute(bos);
            if (pd.isShowing()) {
                pd.hide();
            }
            bitmaps = null;
            if (bos == null) {
                Msg.Toast("Error generating GIF please try again.", context);
            } else {
                displayPost(true, bos.toByteArray());
            }
        }
    }

    // Initiate the new post view display class.
    private void displayPost(final Boolean isImage, final byte[] byteArray) {
        double latitude = Double.parseDouble(prefs.getString("latitude", "0"));
        double longitude = Double.parseDouble(prefs.getString("longitude", "0"));
        String region = prefs.getString("region", null);
        String area = prefs.getString("area", null);
        if (latitude != 0 && longitude != 0 && area != null && region != null) {
            Post newPost = new Post();
            ParseGeoPoint point = new ParseGeoPoint(latitude, longitude);
            newPost.setUserId(ParseUser.getCurrentUser().getObjectId());
            newPost.setUserName(ParseUser.getCurrentUser().getUsername());
            newPost.setLocation(point);
            newPost.setArea(area);
            newPost.isGroup(isGroup);
            newPost.isSchool(isSchool);
            NewPostAdapter postAdaptor = new NewPostAdapter(newPost, isImage, null,
                    byteArray, context);
            postAdaptor.show();
            postAdaptor.setOnPostCompleteListener(new NewPostAdapter.OnPostComplete() {
                @Override
                public void onComplete(Post post, String error) {
                    if (error == null) {
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        Msg.Toast("Post success", context);
                        SharedPreferences prefs = getSharedPreferences("appData", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("search", post.getObjectId());
                        editor.putString("from", from);
                        editor.commit();
                        finish();
                    } else {
                        Msg.AlertDialog(context, "Error posting", error);
                    }
                }
            });
        } else {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    displayPost(isImage, byteArray);
                }
            }, 3000);
        }
    }

    // capture photo method
    public void capturePhoto() {
        if (bitmaps == null){
            bitmaps = new ArrayList<>();
        }
        camera.takePicture(null, null, jpegCallback);
    }

    // use front or back camera
    public void rotateCamera(View v) {
        if (isPreviewRunning) {
            camera.stopPreview();
        }
        camera.release();
        if (currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        } else {
            currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
        camera = Camera.open(currentCameraId);

        setCameraDisplayOrientation(CreateGIFActivity.this, currentCameraId, camera);
        refreshCamera();
    }

    // Rotate the camera to the correct orientation
    private static void setCameraDisplayOrientation(Activity activity,
                                                    int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    // Listener for the capture button
    private class RecordPress implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            capturePhoto();
        }
    }
}
