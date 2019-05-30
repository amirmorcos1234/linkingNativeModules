package ro.vodafone.mcare.android.ui.views.viewholders.identity;

import android.support.annotation.CallSuper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Serban Radulescu on 8/16/2017.
 */

public class IdentityViewHolder extends RecyclerView.ViewHolder {

    protected ExpandableListTree.Node node;


    public IdentityViewHolder(View itemView) {
        super(itemView);
    }

    public int computeStartMargin(ExpandableListTree.Node nodeToCompute){
        int startMargin = 1;

        try {
            while(nodeToCompute.getParent() != null){
                nodeToCompute = nodeToCompute.getParent();
                startMargin++;
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return startMargin;
    }

    public ExpandableListTree.Node getNode() {
        return node;
    }
    @CallSuper
    public void setData(ExpandableListTree.Node node) {
        this.node = node;
    }
}
