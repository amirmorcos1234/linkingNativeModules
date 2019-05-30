package ro.vodafone.mcare.android.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;

/**
 * Created by Victor Radulescu on 10/25/2017.
 */

public class CostControlWidgetEbuMigrated extends CostControlWidgetPostpaid {

    public CostControlWidgetEbuMigrated(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CostControlWidgetEbuMigrated(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    List<View> generateExtraOptionsButtons() {
        List<View> buttonList= new ArrayList<>();

        Button buttonMore = createDialViewButton(R.drawable.black_circle,"Altele", Color.WHITE);
        buttonMore.setClickable(true);
        buttonMore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                new NavigationAction(context).startAction(IntentActionName.OFFERS_BEO_NO_SEAMLESS);
                extraOptionsController.toggle(300);
            }
        });
        buttonList.add(buttonMore);
        View  offerView = getOfferViewAfterMostImportantHiddenEligibleCategory();
        if(offerView!=null){
            buttonList.add(offerView);
        }

        return buttonList;
    }
}
