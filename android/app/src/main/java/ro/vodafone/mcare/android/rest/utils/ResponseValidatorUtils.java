package ro.vodafone.mcare.android.rest.utils;

import io.realm.RealmObject;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;

/**
 * Created by Victor Radulescu on 10/25/2017.
 */

public class ResponseValidatorUtils {

    public static boolean isValidGeneralRealmResponse(GeneralResponse<? extends RealmObject > generalResponse){
        return generalResponse != null && generalResponse.getTransactionSuccess() != null && generalResponse.getTransactionStatus()==0;
    }

}
