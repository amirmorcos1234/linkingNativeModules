package ro.vodafone.mcare.android.presenter.adapterelements.base;

import android.support.annotation.NonNull;
import android.view.View;

/**
 * Created by Victor Radulescu on 9/14/2017.
 */

public class ViewStaticAdapterElement extends StaticAdapterElement {

    View view;

    public ViewStaticAdapterElement(int type, int order,@NonNull View view) {
        super(type, order);
        this.view = view;
    }

    @Override
    protected View getView() {
        return view;
    }

    @Override
    public Object getData() {
        return null;
    }
}
