package com.raywenderlich.facespotter;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;

import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.material.tabs.TabLayout;
import com.raywenderlich.facespotter.BottomSheet.BottomSheetAdapter;
import com.raywenderlich.facespotter.BottomSheet.EarringsFragment;
import com.raywenderlich.facespotter.BottomSheet.MaangTikkaFragment;
import com.raywenderlich.facespotter.BottomSheet.NecklaceFragment;
import com.raywenderlich.facespotter.BottomSheet.NosePinFragment;
import com.raywenderlich.facespotter.BottomSheet.RingFragment;
import com.raywenderlich.facespotter.BottomSheet.TryOnPojo;
import com.raywenderlich.facespotter.ui.camera.CameraSourcePreview;
import com.raywenderlich.facespotter.ui.camera.GraphicOverlay;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.google.android.gms.vision.CameraSource.CAMERA_FACING_BACK;

public final class FaceActivity extends AppCompatActivity {

    private static final String TAG = "FaceActivity";
    public static Drawable drawable1;
    public static String type1;

    Dialog dialoggg;


    private static final int RC_HANDLE_GMS = 9001;
    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 255;

    private CameraSource mCameraSource = null;
    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;
    private boolean mIsFrontFacing = true;

    //////////////BOTTOM SHEET////////////////
    RelativeLayout bottom_sheet_ll;
    BottomSheetBehavior sheetBehavior;
    ViewPager viewpager_bottomSheet;
    TabLayout tab_bottomSheet;
    com.raywenderlich.facespotter.BottomSheet.EarringsFragment earringsFragment = new EarringsFragment();
    com.raywenderlich.facespotter.BottomSheet.MaangTikkaFragment maangTikkaFragment = new MaangTikkaFragment();
    com.raywenderlich.facespotter.BottomSheet.NecklaceFragment necklaceFragment = new NecklaceFragment();
    com.raywenderlich.facespotter.BottomSheet.NosePinFragment nosePinFragment = new NosePinFragment();
    com.raywenderlich.facespotter.BottomSheet.RingFragment ringFragment = new RingFragment();
    ImageView up_icon_img;
    //////////////BOTTOM SHEET////////////////


    // Activity methods
    // ================

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face);

        bottom_sheet_ll = findViewById(R.id.bottom_sheet_ll);
        sheetBehavior = BottomSheetBehavior.from(bottom_sheet_ll);
        tab_bottomSheet = findViewById(R.id.tab_bottomSheet);
        viewpager_bottomSheet = findViewById(R.id.viewpager_bottomSheet);
        viewpager_bottomSheet.setOffscreenPageLimit(5);
        up_icon_img = findViewById(R.id.up_icon_img);

        up_icon_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    up_icon_img.setImageResource(R.drawable.down_icon);
                } else {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    up_icon_img.setImageResource(R.drawable.upicon);
                }
            }
        });

        setupTabLayout();

        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay) findViewById(R.id.faceOverlay);

        final ImageButton button = (ImageButton) findViewById(R.id.flipButton);
        button.setOnClickListener(mFlipButtonListener);

        if (savedInstanceState != null) {
            mIsFrontFacing = savedInstanceState.getBoolean("IsFrontFacing");
        }

        // Check for the camera permission before accessing the camera.
        // Request permission if the user hasn't yet granted it.
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        } else {
            requestCameraPermission();
        }
    }

    private void setupTabLayout() {
        BottomSheetAdapter bottomSheetAdapter = new BottomSheetAdapter(getSupportFragmentManager());
        bottomSheetAdapter.addFragment(necklaceFragment, "Necklace");
        bottomSheetAdapter.addFragment(earringsFragment, "Earrings");
        bottomSheetAdapter.addFragment(maangTikkaFragment, "Maang Tikka");
        bottomSheetAdapter.addFragment(nosePinFragment, "Nose Pin");
        bottomSheetAdapter.addFragment(ringFragment, "Ring");

        viewpager_bottomSheet.setAdapter(bottomSheetAdapter);
        tab_bottomSheet.setupWithViewPager(viewpager_bottomSheet);
    }

    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }


    @Override
    protected void onResume() {
        super.onResume();

        startCameraSource();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource
            createCameraSource();
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Face Tracker sample")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }


    // Called when the device orientation changes.
    // We need to save one piece of info: which camera is being used.
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean("IsFrontFacing", mIsFrontFacing);
    }

    // Toggle between front and rear cameras.
    private View.OnClickListener mFlipButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            mIsFrontFacing = !mIsFrontFacing;

            if (mCameraSource != null) {
                mCameraSource.release();
                mCameraSource = null;
            }

            createCameraSource();
            startCameraSource();
        }
    };


    // Detector
    // ========

    // Create the face detector, and check if it's ready for use.
    @NonNull
    private FaceDetector createFaceDetector(final Context context) {
        FaceDetector detector = new FaceDetector.Builder(context)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setTrackingEnabled(true)
                .setMode(FaceDetector.FAST_MODE)
                .setProminentFaceOnly(mIsFrontFacing)
                .setMinFaceSize(mIsFrontFacing ? 0.35f : 0.15f)
                .build();

        MultiProcessor.Factory<Face> factory = new MultiProcessor.Factory<Face>() {
            @Override
            public Tracker<Face> create(Face face) {
                return new FaceTracker(mGraphicOverlay, context, mIsFrontFacing);
            }
        };

        Detector.Processor<Face> processor = new MultiProcessor.Builder<>(factory).build();
        detector.setProcessor(processor);

        if (!detector.isOperational()) {
            Log.w(TAG, "Face detector dependencies are not yet available.");

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            IntentFilter lowStorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowStorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(this, R.string.low_storage_error, Toast.LENGTH_LONG).show();
                Log.w(TAG, getString(R.string.low_storage_error));
            }
        }
        return detector;
    }

    // Camera source
    // =============

    private void createCameraSource() {
        Context context = getApplicationContext();
        FaceDetector detector = createFaceDetector(context);

        int facing = CameraSource.CAMERA_FACING_FRONT;
        if (!mIsFrontFacing) {
            facing = CAMERA_FACING_BACK;
        }

        // The camera source is initialized to use either the front or rear facing camera.  We use a
        // relatively low resolution for the camera preview, since this is sufficient for this app
        // and the face detector will run faster at lower camera resolutions.
        //
        // However, note that there is a speed/accuracy trade-off with respect to choosing the
        // camera resolution.  The face detector will run faster with lower camera resolutions,
        // but may miss smaller faces, landmarks, or may not correctly detect eyes open/closed in
        // comparison to using higher camera resolutions.  If you have any of these issues, you may
        // want to increase the resolution.
        mCameraSource = new CameraSource.Builder(context, detector)
                .setFacing(facing)
                .setRequestedPreviewSize(640, 480)
                .setRequestedFps(60.0f)
                .setAutoFocusEnabled(true)
                .build();
    }

    private void startCameraSource() {
        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (sheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            super.onBackPressed();
        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    public void getDownloadedDrawable(String url, Context context, String type) {
        type1 = type;

        new LongOperation(context).execute(url);

    }

    private void showLoadingDialog(Context context) {

        //final Dialog dialog = new Dialog(context);
        dialoggg = new Dialog(context);
        dialoggg.setContentView(R.layout.dialog_please_wait);
        dialoggg.setCancelable(true);
        dialoggg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialoggg.show();
    }

    private final class LongOperation extends AsyncTask<String, Void, Drawable> {

        Context contextt;

        public LongOperation(Context context) {
            super();
            this.contextt = context;
            showLoadingDialog(contextt);
            // do stuff
        }

        @Override
        protected void onPreExecute() {
            //showLoadingDialog();
            super.onPreExecute();
        }

        @Override
        protected Drawable doInBackground(String... params) {

            String receivedurl = params[0];
            Bitmap x;
            InputStream input = null;
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) new URL(receivedurl).openConnection();
                connection.connect();
                input = connection.getInputStream();

            } catch (IOException e) {
                e.printStackTrace();
            }

            x = BitmapFactory.decodeStream(input);
            return new BitmapDrawable(Resources.getSystem(), x);
        }

        @Override
        protected void onPostExecute(Drawable drawable) {
            drawable1 = drawable;
            stopLoadingDialog();
            super.onPostExecute(drawable);
        }
    }

    private void stopLoadingDialog() {
        dialoggg.dismiss();

    }

    public static TryOnPojo hereIsDrawableAndType() {
        if (type1 != null && drawable1 != null) {
            TryOnPojo tryOnPojo = new TryOnPojo();
            tryOnPojo.setType(type1);
            tryOnPojo.setImage(drawable1);
            return tryOnPojo;

        } else {
            TryOnPojo tryOnPojo = new TryOnPojo();
            tryOnPojo.setType("default_image");
            tryOnPojo.setImage(null);
            return tryOnPojo;

        }
    }


}
