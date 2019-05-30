package ro.vodafone.mcare.android.card.activeServices;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.BaseCardControllerInterface;
import ro.vodafone.mcare.android.client.model.beo.prepaid.activeOffers.ActiveOffersSuccess;
import ro.vodafone.mcare.android.client.model.offers.ActiveOffersPostpaidSuccess;
import ro.vodafone.mcare.android.client.model.offers.activeOffersEbu.ActiveOffersPostpaidEbuSuccess;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.CBUUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidUser;
import ro.vodafone.mcare.android.ui.fragments.yourServices.YourServicesFragment;

/**
 * Created by user on 28.03.2017.
 */

public class ActiveServicesCardController implements BaseCardControllerInterface{

    public static String TAG = "ASCardController";

    private ActiveServicesViewGroup viewGroup;
    private YourServicesFragment yourServicesFragment;

    private ActiveOffersSuccess activeOffersSuccess;
    private ActiveOffersPostpaidSuccess activeOffersPostpaidSuccess;
    private ActiveOffersPostpaidEbuSuccess activeOffersPostpaidEbuSuccess;

    public ActiveServicesCardController(Context context) {
        this.viewGroup = new ActiveServicesViewGroup(context, this);
    }


    public void requestData(){
        viewGroup.displayLoadingCard();
        yourServicesFragment = (YourServicesFragment) VodafoneController.findFragment(YourServicesFragment.class);

        if(yourServicesFragment != null){
            yourServicesFragment.requestData();
        }
    }

    public ActiveServicesViewGroup getViewGroup() {
        return viewGroup;
    }

    @Override
    public void onDataLoaded(Object... args) {
        for (Object value : args) {
            if (value instanceof ActiveOffersSuccess) {
                this.activeOffersSuccess = (ActiveOffersSuccess) value;
            }
            if (value instanceof ActiveOffersPostpaidSuccess) {
                this.activeOffersPostpaidSuccess = (ActiveOffersPostpaidSuccess) value;
            }
            if (value instanceof ActiveOffersPostpaidEbuSuccess) {
                this.activeOffersPostpaidEbuSuccess = (ActiveOffersPostpaidEbuSuccess) (value);
            }
        }

        if(VodafoneController.getInstance().getUser() instanceof PrepaidUser){
            buildPrepaidCardsList();
        }else if(VodafoneController.getInstance().getUser() instanceof CBUUser){
            buildCBUCardsList();
        }else if(VodafoneController.getInstance().getUser() instanceof EbuMigrated){
            buildEBUCardsList();
        }
    }

    @Override
    public void onRequestFailed() {
        viewGroup.displayErrorCard();
    }

    private void buildPrepaidCardsList() {
        if(activeOffersSuccess == null){
            viewGroup.displayErrorCard();
        }else if (activeOffersSuccess.getActiveServicesList() == null) {
            viewGroup.displayNoResultCard();
        } else {
            if (activeOffersSuccess.getActiveServicesList().size() == 0) {
                viewGroup.displayNoResultCard();
            } else {

                List<ActiveServicesCard> cardList = new ArrayList<>();
                for (int i = 0; i < activeOffersSuccess.getActiveServicesList().size(); i++) {
                    cardList.add(new ActiveServicesCard(viewGroup.getContext())
                            .setViewGroup(viewGroup)
                            .buildPrepaidCard(activeOffersSuccess.getActiveServicesList().get(i)));
                }
                viewGroup.atachCards(cardList);
            }
        }
    }

    private void buildCBUCardsList() {
        if(activeOffersPostpaidSuccess == null){
            viewGroup.displayErrorCard();
        } else if (activeOffersPostpaidSuccess.getActiveServicesList() == null) {
            viewGroup.displayNoResultCard();
        } else {
            if (activeOffersPostpaidSuccess.getActiveServicesList().size() == 0) {
                viewGroup.displayNoResultCard();
            } else {
                List<ActiveServicesCard> cardList = new ArrayList<>();

                for (int i = 0; i < activeOffersPostpaidSuccess.getActiveServicesList().size(); i++) {
                    cardList.add(new ActiveServicesCard(viewGroup.getContext())
                            .setViewGroup(viewGroup)
                            .buildCBUCard(activeOffersPostpaidSuccess.getActiveServicesList().get(i)));
                }
                viewGroup.atachCards(cardList);
            }
        }
    }

    private void buildEBUCardsList(){
        if(activeOffersPostpaidEbuSuccess == null){
            yourServicesFragment.hideActiveServicesCards();
        } else if(activeOffersPostpaidEbuSuccess.getActiveServices() != null
                && activeOffersPostpaidEbuSuccess.getActiveServices().getPromoList() != null
                && !activeOffersPostpaidEbuSuccess.getActiveServices().getPromoList().isEmpty()){

            List<ActiveServicesCard> cardList = new ArrayList<>();

            for (int i = 0; i < activeOffersPostpaidEbuSuccess.getActiveServices().getPromoList().size(); i++) {
                cardList.add(new ActiveServicesCard(viewGroup.getContext())
                        .setViewGroup(viewGroup)
                        .buildEBUCard(activeOffersPostpaidEbuSuccess.getActiveServices().getPromoList().get(i)));
            }

            if(!cardList.isEmpty()){
                viewGroup.atachCards(cardList);
            }else{
                if(yourServicesFragment != null){
                    yourServicesFragment.hideActiveServicesCards();
                }
            }
        }else{
            if(yourServicesFragment != null){
                yourServicesFragment.hideActiveServicesCards();
            }
        }
    }
}
