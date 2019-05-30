package ro.vodafone.mcare.android.client.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;


import com.bumptech.glide.Glide;

import java.util.List;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;

/**
 * Created by Victor Radulescu on 3/31/2017.
 */

public class ImageSlideAdapter extends PagerAdapter {


    private Context context;
    private List<String> uriList;
    private LayoutInflater layoutInflater;
    String title;
    private int           size;
    private boolean       isInfiniteLoop;


    public ImageSlideAdapter(Context context,String title, List<String> uriList){
        this.context = context;
        this.title = title;
        this.uriList = uriList;
        this.size = uriList.size();
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        isInfiniteLoop = false;
    }

    private int getPosition(int position) {
        return isInfiniteLoop ? position % size : position;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        AppCompatImageView imageView = new AppCompatImageView(context);
        RelativeLayout relativeLayout = new RelativeLayout(context);
        relativeLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        VodafoneTextView nameTextView = new VodafoneTextView(context);
        nameTextView.setText(title);
        nameTextView.setId(R.id.nameTextView);
        nameTextView.setTextColor(ContextCompat.getColor(context,R.color.text_white));
        nameTextView.setTextSize(19);
        nameTextView.setGravity(View.TEXT_ALIGNMENT_CENTER);
        RelativeLayout.LayoutParams textParameters = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textParameters.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE);
        relativeLayout.addView(nameTextView,textParameters);


        RelativeLayout.LayoutParams imageViewParameters = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, ScreenMeasure.dpToPx(200));
        imageViewParameters.setMargins(ScreenMeasure.dpToPx(32),ScreenMeasure.dpToPx(10),ScreenMeasure.dpToPx(32),0);
        imageViewParameters.addRule(RelativeLayout.BELOW,R.id.nameTextView);
        relativeLayout.addView(imageView,imageViewParameters);

        //textParameters.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE);

        Glide.with(VodafoneController.getInstance())
                .load(getSafePhoto(uriList.get(getPosition(position))))
                .override(400,800)
                .centerCrop()
                .placeholder(R.drawable.phone_no_image)
                .into(imageView);

        container.addView(relativeLayout);
        return relativeLayout;
    }
    private String getSafePhoto(String photo){
        return String.valueOf((photo.isEmpty()? R.drawable.phone_no_image:photo));
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //super.destroyItem(container, position, object);
        container.removeView ((RelativeLayout) object);
    }

    @Override

    public int getCount() {
        return isInfiniteLoop ? 100 : uriList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public ImageSlideAdapter setInfiniteLoop(boolean isInfiniteLoop) {
        this.isInfiniteLoop = isInfiniteLoop;
        return this;
    }
}

