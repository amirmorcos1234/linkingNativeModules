package ro.vodafone.mcare.android.client.model.costControl;

import java.util.List;

/**
 * Created by Victor Radulescu on 1/11/2018.
 Used to keep list of AdditionalCost and ExtraOption from multiple CostControl objects
 */

public class CostControlDataVolatile {

    List<AdditionalCost> additionalCosts;
    List<Extraoption> extraoptions;

    public CostControlDataVolatile(List<AdditionalCost> additionalCosts, List<Extraoption> extraoptions) {
        this.additionalCosts = additionalCosts;
        this.extraoptions = extraoptions;
    }

    public List<AdditionalCost> getAdditionalCosts() {
        return additionalCosts;
    }

    public void setAdditionalCosts(List<AdditionalCost> additionalCosts) {
        this.additionalCosts = additionalCosts;
    }

    public List<Extraoption> getExtraoptions() {
        return extraoptions;
    }

    public void setExtraoptions(List<Extraoption> extraoptions) {
        this.extraoptions = extraoptions;
    }
}
