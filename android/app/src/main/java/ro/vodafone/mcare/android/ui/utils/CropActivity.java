package ro.vodafone.mcare.android.ui.utils;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.ui.utils.imagecontroller.cropoverlay.CropOverlayView;
import ro.vodafone.mcare.android.ui.utils.imagecontroller.cropoverlay.edge.Edge;
import ro.vodafone.mcare.android.ui.utils.imagecontroller.cropoverlay.utils.ImageViewUtil;
import ro.vodafone.mcare.android.ui.utils.imagecontroller.photoview.PhotoView;
import ro.vodafone.mcare.android.ui.utils.imagecontroller.photoview.PhotoViewAttacher;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.PhotoUtils;

/**
 * Created by bogdan marica on 4/27/2017.
 */


public class CropActivity extends AppCompatActivity {
    public static final String TAG = "CropActivity";
    public static final String ERROR = "error";
    final static String IMAGE_PATH = "image_path";
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 218;
    private final Bitmap.CompressFormat mOutputFormat = Bitmap.CompressFormat.JPEG;
    Uri uri;
    PhotoView photoView;
    private final int IMAGE_MAX_SIZE = 1024;
    private float minScale = 1f;
    private CropOverlayView mCropOverlayView;
    private ContentResolver mContentResolver;
    //Temp file to save cropped image
    private Uri mImageUri = null;
    //File for capturing camera images
    private File mFileTemp;
    private View.OnClickListener btnCancelListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            userCancelled();
        }
    };
    private View.OnClickListener btnSendListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            saveUploadCroppedImageNoPermission();
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        mContentResolver = getContentResolver();
        photoView = (PhotoView) findViewById(R.id.photoView);
        mCropOverlayView = (CropOverlayView) findViewById(R.id.crop_overlay);
        Button btnSend = (Button) findViewById(R.id.sendBtn);
        btnSend.setOnClickListener(btnSendListener);
        Button btnCancel = (Button) findViewById(R.id.cancelBtn);
        btnCancel.setOnClickListener(btnCancelListener);
        photoView.addListener(new PhotoViewAttacher.IGetImageBounds() {
            @Override
            public Rect getImageBounds() {
                return new Rect((int) Edge.LEFT.getCoordinate(), (int) Edge.TOP.getCoordinate(), (int) Edge.RIGHT.getCoordinate(), (int) Edge.BOTTOM.getCoordinate());
            }
        });

        Bundle getBundle = null;
        getBundle = this.getIntent().getExtras();
        if (getBundle != null) {
            uri = (Uri) getBundle.getParcelable("photoUri");
            D.w("uri = " + uri);
        }

        mImageUri = uri;
        init();
    }

    private void saveUploadCroppedImageNoPermission() {

        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
            } else {
                saveUploadCroppedImage();
            }
        } else {
            saveUploadCroppedImage();
        }
    }

    private void saveUploadCroppedImage() {
        Log.d(TAG, "saving cropped ...");
        Bitmap b = getCroppedImage();
        String uriString = PhotoUtils.storeImage(getContentResolver(), b, ("VDF_IMG_" + String.valueOf(System.currentTimeMillis())), "");
        Intent intent = new Intent();
        D.w("intent = " + intent);
        if(uriString!=null) {
            Uri goodUri = Uri.parse(uriString);
            D.w("goodUri = " + goodUri);
            intent.putExtra("imageUri", goodUri);
        }
        b.recycle();
        setResult(RESULT_OK, intent);
        finish();
    }

    public void init() {
        if (mImageUri != null) {
            Glide.with(this)
                    .load(mImageUri)
                    .into(photoView);

            photoView.setMaximumScale(6);
            photoView.setMinimumScale(0.5f);
            photoView.setScale(1f);
            D.w();
        } else
            D.e("ERROR");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    saveUploadCroppedImage();
                } else {
                    finish();
                    Toast.makeText(this, "No permission granted to access the external storage", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
    private Bitmap getCurrentDisplayedImage() {
        Bitmap result = Bitmap.createBitmap(photoView.getWidth(), photoView.getHeight(), Bitmap.Config.RGB_565);
        Canvas c = new Canvas(result);
        photoView.draw(c);
        return result;
    }

    public Bitmap getCroppedImage() {
//        D.w();

        Bitmap mCurrentDisplayedBitmap = getCurrentDisplayedImage();
        Rect displayedImageRect = ImageViewUtil.getBitmapRectCenterInside(mCurrentDisplayedBitmap, photoView);

        // Get the scale factor between the actual Bitmap dimensions and the
        // displayed dimensions for width.
        float actualImageWidth = mCurrentDisplayedBitmap.getWidth();
        float displayedImageWidth = displayedImageRect.width();
        float scaleFactorWidth = actualImageWidth / displayedImageWidth;

        // Get the scale factor between the actual Bitmap dimensions and the
        // displayed dimensions for height.
        float actualImageHeight = mCurrentDisplayedBitmap.getHeight();
        float displayedImageHeight = displayedImageRect.height();
        float scaleFactorHeight = actualImageHeight / displayedImageHeight;
//        D.w();

        // Get crop window position relative to the displayed image.
        float cropWindowX = Edge.LEFT.getCoordinate() - displayedImageRect.left;
        float cropWindowY = Edge.TOP.getCoordinate() - displayedImageRect.top;
        float cropWindowWidth = Edge.getWidth();
        float cropWindowHeight = Edge.getHeight();
//        D.w();

        // Scale the crop window position to the actual size of the Bitmap.
        float actualCropX = cropWindowX * scaleFactorWidth;
        float actualCropY = cropWindowY * scaleFactorHeight;
        float actualCropWidth = cropWindowWidth * scaleFactorWidth;
        float actualCropHeight = cropWindowHeight * scaleFactorHeight;

//        D.w();
        // Crop the subset from the original Bitmap.
        return Bitmap.createBitmap(mCurrentDisplayedBitmap, (int) actualCropX, (int) actualCropY, (int) actualCropWidth, (int) actualCropHeight);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void userCancelled() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }
}




