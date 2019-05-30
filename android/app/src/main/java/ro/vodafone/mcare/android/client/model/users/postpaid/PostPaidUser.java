package ro.vodafone.mcare.android.client.model.users.postpaid;

import java.util.List;

import ro.vodafone.mcare.android.client.model.dashboard.gauge.GaugeOptionsType;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.ui.fragments.dashboard.BaseDashboardFragment;
import ro.vodafone.mcare.android.ui.fragments.dashboard.PostpaidDashboardFragment;

/**
 * Created by Victor Radulescu on 2/10/2017.
 */

public abstract class PostPaidUser extends User {



    protected  List<GaugeOptionsType> balanceList;

/*    public List<GaugeOptionsType> getBalanceList() {
        if(balanceList==null){
            balanceList= new ArrayList<>();
            if(!isGreenCard){
                balanceList.add(0,GaugeOptionsType.MOBILE_DATA);
            }
            if(isTobe){
                return balanceList;
            }
            balanceList.add(balanceList.size(),GaugeOptionsType.VAS);
            balanceList.add(balanceList.size(),GaugeOptionsType.CVT);

        }
        return balanceList;
    }*/

    @Override
    public BaseDashboardFragment getDashboardFragment() {
        return PostpaidDashboardFragment.newInstance();
    }

    public  abstract boolean showSecondaryButtonInSuplimentaryCost();

}
