package ro.vodafone.mcare.android.widget.messaging;


import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.utils.D;

public class ViewImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        Bundle extras = getIntent().getExtras();
        final Uri uri = extras.getParcelable("URI");
        D.w("uri = " + uri);

        final ImageView imageView = (ImageView) findViewById(R.id.image_view_large);

        if (uri != null) {
            Glide.with(getApplicationContext())
                    .load(uri)
                    .into(imageView);
        } else
            Glide.with(getApplicationContext())
                    .load(R.mipmap.ic_launcher)
                    .into(imageView);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
   //     finish();
//FIXME why is this here?
        VodafoneController.getInstance().supportWindow(this).show();
//        new NavigationAction(this).startAction(IntentActionName.SUPPORT_24_7);
    }
}
