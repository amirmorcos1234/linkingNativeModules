package ro.vodafone.mcare.android.client.model.realmhelper.loyalty;

import ro.vodafone.mcare.android.client.model.realmhelper.SortingOption;

/**
 * Created by Victor Radulescu on 9/19/2017.
 */

public class LoyaltyVoucherListsOptions {

    public static final String DEFAULT_SELECTED_SORT_TYPE  = "Sortează";
    private String selectedSortType = DEFAULT_SELECTED_SORT_TYPE;
    private String owner;
    private LoyaltyFilterOptions loyaltyFilterOptions;
    private SortingOption sortingOption;

    public LoyaltyVoucherListsOptions(String owner, LoyaltyFilterOptions loyaltyFilterOptions, SortingOption sortingOption) {
        this.owner = owner;
        this.loyaltyFilterOptions = loyaltyFilterOptions;
        this.sortingOption = sortingOption;
    }

    public LoyaltyVoucherListsOptions(String owner) {
        this.owner = owner;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public LoyaltyFilterOptions getLoyaltyFilterOptions() {
        return loyaltyFilterOptions;
    }

    public void setLoyaltyFilterOptions(LoyaltyFilterOptions loyaltyFilterOptions) {
        this.loyaltyFilterOptions = loyaltyFilterOptions;
    }

    public SortingOption getSortingOption() {
        return sortingOption;
    }

    public void setSortingOption(SortingOption sortingOption) {
        this.sortingOption = sortingOption;
    }

    public String getSelectedSortType() {
        return selectedSortType;
    }

    public void setSelectedSortType(String selectedSortType) {
        this.selectedSortType = selectedSortType;
    }
}
