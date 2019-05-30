package ro.vodafone.mcare.android.ui.utils;

/**
 * @author Andrei DOLTU
 */

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;

/**
 * Created by Andrei DOLTU on 2/09/2017.
 */

public class ViewPagerAdapterTutorial extends PagerAdapter {
    // Declare Variables
    Context context;
    //	String[] description;
    private static final String TAG = "ViewPageAdapterTutorial";
    int[] pageOneA;
    int[] pageOneB;
    int[] pageTwo;
    int[] pageThree;
    int[] pageFour;
    int[] pageFive;
    int[] pageSix;
    int[] skipButton;
    LayoutInflater inflater;

    public ViewPagerAdapterTutorial(Context context, //String[] description,
                                    int[] pageOneA, int[] pageOneB, int[] pageTwo, int[] pageThree,
                                    int[] pageFour, int[] pageFive, int[] pageSix, int[] skipButton){
        //, int[] continueButton, String[] buttonText) {
        this.context = context;
        this.pageOneA = pageOneA;
        this.pageOneB = pageOneB;
        this.pageTwo = pageTwo;
        this.pageThree = pageThree;
        this.pageFour = pageFour;
        this.pageFive = pageFive;
        this.pageSix = pageSix;
        this.skipButton = skipButton;
    }

    @Override
    public int getCount() {
        return pageOneA.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((ScrollView) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        // Declare Variables
        ImageView pageOneAView;
        ImageView pageTwoView;
        ImageView pageThreeView;
        ImageView pageFourView;
        ImageView pageFiveView;
        ImageView pageSixView;
        Button closeButton;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View itemView = inflater.inflate(R.layout.viewpager_tutorial_item, container,
                false);

        //get resources
        Resources res = VodafoneController.getInstance().getResources();

        pageOneAView = (ImageView) itemView.findViewById(R.id.tutorial_screen_1);
        pageOneAView.setImageResource(pageOneA[position]);
        pageTwoView = (ImageView) itemView.findViewById(R.id.tutorial_screen_2);
        pageTwoView.setImageResource(pageTwo[position]);
        pageThreeView = (ImageView) itemView.findViewById(R.id.tutorial_screen_3);
        pageThreeView.setImageResource(pageThree[position]);
        pageFourView = (ImageView) itemView.findViewById(R.id.tutorial_screen_4);
        pageFourView.setImageResource(pageFour[position]);
        pageFiveView = (ImageView) itemView.findViewById(R.id.tutorial_screen_5);
        pageFiveView.setImageResource(pageFive[position]);
        pageSixView = (ImageView) itemView.findViewById(R.id.tutorial_screen_6);
        pageSixView.setImageResource(pageSix[position]);

        Log.d(TAG, "Created closeButton steps 1-5");
        closeButton = (Button) itemView.findViewById(R.id.skipTutorialBtn);
        //closeButton.setBackgroundResource(skipButton[position]);

        if(position == 0){
            //Page1
			//backgroundLayout.setBackgroundResource(R.drawable.tutorial_background_screen_1);
            Log.d(TAG, "closeButton unfinished step:" + position);

        }
        if(position == 1){
            //Page2
//			backgroundLayout.setBackgroundResource(R.drawable.tutorial_background_screen_2);
            Log.d(TAG, "closeButton unfinished step:" + position);

        }
        if(position == 2){
            //Page3
//			backgroundLayout.setBackgroundResource(R.drawable.tutorial_background_screen_3);
            Log.d(TAG, "closeButton unfinished step:" + position);

        }
        if(position == 3){
            //Page4
//			backgroundLayout.setBackgroundResource(R.drawable.tutorial_background_screen_4);
            Log.d(TAG, "closeButton unfinished step:" + position);

        }
        if(position == 4){
            //Page5
//			backgroundLayout.setBackgroundResource(R.drawable.tutorial_background_screen_5);
            Log.d(TAG, "closeButton unfinished step:" + position);

        }
        if(position == 5){
            //Page6
//			backgroundLayout.setBackgroundResource(R.drawable.tutorial_background_screen_6);
            closeButton = (Button) itemView.findViewById(R.id.continue_button);
            Log.d(TAG, "closeButton Finished step:" + position);

        }

        // Add viewpager_item.xml to ViewPager
        ((ViewPager) container).addView(itemView);

        return itemView;
    }

    @Override
    public int getItemPosition(Object object) {
        Log.d(TAG, object.toString());
        return super.getItemPosition(object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Remove viewpager_item.xml from ViewPager
        ((ViewPager) container).removeView((ScrollView) object);

    }
}
