package ro.vodafone.mcare.android.ui.activities.support;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import java.util.List;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.presenter.adapterelements.base.DynamicAdapterElement;
import ro.vodafone.mcare.android.ui.views.viewholders.general.BasicViewHolder;

/**
 * Created by Bogdan Marica on 10/11/2017.
 */

public class ChatElement extends DynamicAdapterElement {

    public ChatElement(int type, int order, @NonNull Context context, List list) {
        super(type, order, context, list);
    }

    @Override
    protected View getView() {
        return View.inflate(getContext(), R.layout.faq_expandable_recyclerview_item, null);
    }

    @Override
    public BasicViewHolder getViewHolder(int viewType) {
        return new ChatElementViewHolder(getView());
    }
}


