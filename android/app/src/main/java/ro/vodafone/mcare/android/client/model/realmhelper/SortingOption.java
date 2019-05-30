package ro.vodafone.mcare.android.client.model.realmhelper;

import io.realm.Sort;

/**
 * Created by Victor Radulescu on 9/15/2017.
 */

public class SortingOption {

    public SortingOption(String[] sortFieldNames, Sort[] sort) {
        this.sortFieldNames = sortFieldNames;
        this.sort = sort;
    }

    final private String [] sortFieldNames;
    final private Sort sort [];

    public String[] getSortFieldNames() {
        return sortFieldNames;
    }

    public Sort[] getSort() {
        return sort;
    }
}
