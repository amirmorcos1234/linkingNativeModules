package ro.vodafone.mcare.android.ui.fragments.yourServices;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.card.tariffs.TariffsCard;
import ro.vodafone.mcare.android.client.model.offers.activeOffersEbu.ActiveOffersPostpaidEbuSuccess;
import ro.vodafone.mcare.android.client.model.offers.activeOffersEbu.Tarrif;
import ro.vodafone.mcare.android.ui.activities.MenuActivity;
import ro.vodafone.mcare.android.ui.activities.offers.OffersFragment;
import ro.vodafone.mcare.android.ui.activities.yourServices.YourServicesActivity;

/**
 * Created by Bivol Pavel on 23.10.2017.
 */

public class YourServicesTariffsFragment extends OffersFragment {

    public static final String ACTIVE_OFFERS_POSTPAID_EBU = "activeOffersPostpaidEbuSuccess";

    @BindView(R.id.container)
    LinearLayout containter;

    private ActiveOffersPostpaidEbuSuccess activeOffersPostpaidEbuSuccess;

    private Map<String, List<String>> tariffsMap = new HashMap<>();
    private Set<String> categorySet = new LinkedHashSet<>();

    public YourServicesTariffsFragment setArgsOnBundle(String key , Serializable serializableObject){

        Bundle args = new Bundle();
        args.putSerializable(key, serializableObject);
        setArguments(args);

        return this;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null){
            this.activeOffersPostpaidEbuSuccess = (ActiveOffersPostpaidEbuSuccess) getArguments()
                    .getSerializable(ActiveOffersPostpaidEbuSuccess.class.getCanonicalName());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.fragment_services_tariffs, null);
        ((YourServicesActivity) getActivity()).getNavigationHeader().hideSelectorView();

        ButterKnife.bind(this, v);
        listToMap(activeOffersPostpaidEbuSuccess.getTariffsList());
        addTariffsCard();

        ((MenuActivity)getActivity()).scrolltoTop();

        return v;
    }

    private void listToMap(List<Tarrif> tariffsList){

        if(tariffsList != null && !tariffsList.isEmpty()){

            List<String> descriptionsList = new ArrayList<>();

            for(Tarrif tariff : activeOffersPostpaidEbuSuccess.getTariffsList()){
                categorySet.add(tariff.getTarrifCategory());
            }

            for(String category : categorySet){
                if(!descriptionsList.isEmpty()){
                    descriptionsList.clear();
                }

                for(Tarrif tariff : activeOffersPostpaidEbuSuccess.getTariffsList()){
                    if(tariff.getTarrifCategory().equals(category)){
                        if(tariff.getTarrifDescription() != null && !tariff.getTarrifDescription().equals("")){
                            descriptionsList.add(tariff.getTarrifDescription());
                        }
                    }
                }
                tariffsMap.put(category, new ArrayList<>(descriptionsList));
            }
        }
    }

    private void addTariffsCard(){
        TariffsCard tariffsCard = new TariffsCard(getContext());
        tariffsCard.setTariffs(categorySet, tariffsMap)
                .build();

        containter.addView(tariffsCard);
    }

    /*private static class ActiveOptionsComparator implements Comparator<CategoryEnum> {
        public int compare(CategoryEnum c1, CategoryEnum c2)
        {
            return c1.compareTo(c2);
        }
    }

    private List<CategoryEnum> addToEnumList(Set<String> categorySet){
        List<CategoryEnum> categoryEnumList = new ArrayList<>();
        for(String categoryString : categorySet){
            categoryEnumList.add(CategoryEnum.getCategoryByString(categoryString));
        }
        return categoryEnumList;
    }*/

    @Override
    public String getTitle() {
        return "Tarife abonament";
    }
}
