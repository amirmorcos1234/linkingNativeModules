package ro.vodafone.mcare.android.ui.views.viewholders.general;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.ui.header.NavigationHeader;

/**
 * Created by Victor Radulescu on 8/25/2017.
 */

public class NavigationViewHolder extends RecyclerView.ViewHolder {

    protected NavigationHeader navigationHeader;

    NavigationViewHolder(NavigationHeader itemView) {
        super(itemView);
        navigationHeader = itemView;
    }

    public NavigationHeader getNavigationHeader() {

        return navigationHeader;
    }

    public void setupData(){
        if(navigationHeader !=null){
            init();
        }
    }

    public void init() {
        if (navigationHeader == null) {
            navigationHeader.setId(R.id.navigation_header);
            navigationHeader.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            navigationHeader.displayDefaultHeader();
            setTitle();
        }
    }

    private void setTitle() {
    }

}
