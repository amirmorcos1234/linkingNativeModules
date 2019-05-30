package ro.vodafone.mcare.android.card.tariffs;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ExpandableListView;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.card.VodafoneAbstractCard;
import ro.vodafone.mcare.android.client.adapters.TariffsAdapter;
import ro.vodafone.mcare.android.ui.views.ExpandableAdapterBackedLinearLayout;

/**
 * Created by BivolPavel on 24.10.2017.
 */

public class TariffsCard extends VodafoneAbstractCard{

    @BindView(R.id.tariffs_expandable_backed_linear_layout)
    ExpandableAdapterBackedLinearLayout expandableAdapterBackedLinearLayout;

    private Set<String> categorySet = new LinkedHashSet<>();
    private Map<String, List<String>> tariffsMap;

    public TariffsCard(Context context) {
        super(context);
        init();
    }

    public TariffsCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected int setContent() {
        return R.layout.card_tariffs;
    }

    private void init(){
        ButterKnife.bind(this);
    }

    public TariffsCard setTariffs(Set<String> categorySet, Map<String, List<String>> tariffsMap){
        this.categorySet = categorySet;
        this.tariffsMap = tariffsMap;
        return this;
    }

    public void build(){

        expandableAdapterBackedLinearLayout.setClickable(false);
        expandableAdapterBackedLinearLayout.setAdapter(new TariffsAdapter(getContext(), categorySet, tariffsMap));

        // This way the expander cannot be collapsed
        expandableAdapterBackedLinearLayout.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                expandableAdapterBackedLinearLayout.expandGroup(groupPosition);
                return true;
            }
        });

        //Exapnd all categoryes
        for (int i = 0; i < categorySet.size(); i++) {
            expandableAdapterBackedLinearLayout.expandGroup(i);
        }
    }


}
