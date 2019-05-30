package ro.vodafone.mcare.android.interfaces;

import android.content.Context;

import ro.vodafone.mcare.android.card.VodafoneAbstractCard;

/**
 * Created by Victor Radulescu on 2/6/2018.
 */

public interface CardModelViewInterface {
    VodafoneAbstractCard showLoading(boolean show);
    VodafoneAbstractCard showError(boolean show);
    Context getContext();
}
