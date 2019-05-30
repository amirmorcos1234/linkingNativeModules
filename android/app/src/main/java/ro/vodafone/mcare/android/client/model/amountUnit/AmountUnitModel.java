package ro.vodafone.mcare.android.client.model.amountUnit;

/**
 * Created by Bivol Pavel on 22.04.2017.
 */

public class AmountUnitModel {

    private Double amount;
    private String unit;

    public AmountUnitModel() {
    }

    public AmountUnitModel(Double amount, String unit) {
        this.amount = amount;
        this.unit = unit;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
