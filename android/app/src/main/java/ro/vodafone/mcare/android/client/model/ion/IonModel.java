package ro.vodafone.mcare.android.client.model.ion;

public class IonModel {

    private String msisdn;
    private String country;
    private boolean isSelected;

    public IonModel(String msisdn, String country) {
        this.msisdn = msisdn;
        this.country = country;
        this.isSelected = false;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
