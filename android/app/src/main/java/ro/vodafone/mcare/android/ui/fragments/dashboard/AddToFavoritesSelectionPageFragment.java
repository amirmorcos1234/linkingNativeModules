package ro.vodafone.mcare.android.ui.fragments.dashboard;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafone;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneController;
import ro.vodafone.mcare.android.client.model.favoriteNumbers.FavoriteNumbersSuccess;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.system.TopUPLabels;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.service.RechargeService;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.ui.utils.CustomToast;

/**
 * Created by Deaconescu Eliza on 23.02.2017.
 */
public class AddToFavoritesSelectionPageFragment extends BaseFragment{

    public static final String TAG = "AddToFavoritesSelectionPageFragment";

    private LinearLayout fragmentContainer;
    RechargeService rechargeService;


    // private ProgressBarHandler progressBarHandler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.selection_page_fragment, null);

        fragmentContainer = (LinearLayout) v.findViewById(R.id.selection_page_container);

        return  v;
    }

    public void addFavoriteNumber(final Context context, String msisdn, String nickname, final  VoiceOfVodafone voiceOfVodafone){

        rechargeService = new RechargeService(context);
       // progressBarHandler = new ProgressBarHandler(getContext());

      //  progressBarHandler.show(false, R.string.loading_text);

        rechargeService.addFavoriteNumber(msisdn, nickname).subscribe(new RequestSaveRealmObserver<GeneralResponse<FavoriteNumbersSuccess>>() {
            @Override
            public void onNext(GeneralResponse<FavoriteNumbersSuccess> favoriteNumbersSuccessGeneralResponse) {
                super.onNext(favoriteNumbersSuccessGeneralResponse);

                if(favoriteNumbersSuccessGeneralResponse!=null && favoriteNumbersSuccessGeneralResponse.getTransactionStatus()==0) {
                    Log.d("addFavoriteNumber", "getTransactionSuccess");
                    new CustomToast.Builder(context).message(TopUPLabels.getTop_up_add_favorites_success()).success(true).show();
                    VoiceOfVodafoneController.getInstance().removeVoiceOfVodafone(voiceOfVodafone,true,true);
                } else {
                    Log.d("addFavoriteNumber", "getTransactionFault");
                    new CustomToast.Builder(context).message(TopUPLabels.getTop_up_add_favorites_failed()).success(false).show();
//                    CustomToast customToast = new CustomToast(VodafoneController.getInstance().getDashboardActivity(), context, TopUPLabels.getTop_up_add_favorites_failed(), false);
//                    customToast.show();
                }

            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                    Log.d("addFavoriteNumber", "onError");
                new CustomToast.Builder(context).message(TopUPLabels.getTop_up_add_favorites_api_failed()).success(false).show();
//                    CustomToast customToast = new CustomToast(VodafoneController.getInstance().getDashboardActivity(), context, TopUPLabels.getTop_up_add_favorites_api_failed(), false);
//                    customToast.show();
                  //  progressBarHandler.hide();

            }

            @Override
            public void onCompleted() {
            //    progressBarHandler.hide();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

}
