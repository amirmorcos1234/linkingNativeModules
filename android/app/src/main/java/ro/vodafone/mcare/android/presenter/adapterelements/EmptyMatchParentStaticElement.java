package ro.vodafone.mcare.android.presenter.adapterelements;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.presenter.adapterelements.base.StaticAdapterElement;

/**
 * Created by Victor Radulescu on 2/12/2018.
 */

public class EmptyMatchParentStaticElement extends StaticAdapterElement {

    View matchParentview;

    public EmptyMatchParentStaticElement(int type, int order, View matchParentview) {
        super(type, order);
        this.matchParentview = matchParentview;
    }

    @Override
    protected View getView() {
        matchParentview.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        matchParentview.setBackgroundResource(R.color.card_background_gray);
        return matchParentview;
    }
}
