package ro.vodafone.mcare.android.interfaces.factory;

import android.view.ViewGroup;

/**
 * Created by Victor Radulescu on 8/28/2017.
 */

public interface InterfaceSortAndFilterFactory extends InterfaceSortFactory {
    public void setupFilterView();
    ViewGroup getSortAndFilterView();
}
