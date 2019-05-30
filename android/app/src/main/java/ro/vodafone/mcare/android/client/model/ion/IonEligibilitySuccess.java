package ro.vodafone.mcare.android.client.model.ion;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class IonEligibilitySuccess {

    @SerializedName("fnfNumbers")
    private List<String> fnfNumbers = null;

    @SerializedName("limitNumbers")
    private int limitNumbers;

    @SerializedName("prefixes")
    private List<PhoneNumberPrefix> prefixes = null;

    @SerializedName("offerId")
    private String offerId = null;

    public IonEligibilitySuccess fnfNumbers(List<String> fnfNumbers) {
        this.fnfNumbers = fnfNumbers;
        return this;
    }

    public IonEligibilitySuccess addFnfNumbersItem(String fnfNumbersItem) {
        if (this.fnfNumbers == null) {
            this.fnfNumbers = new ArrayList<String>();
        }
        this.fnfNumbers.add(fnfNumbersItem);
        return this;
    }

    public List<String> getFnfNumbers() {
        return fnfNumbers;
    }

    public void setFnfNumbers(List<String> fnfNumbers) {
        this.fnfNumbers = fnfNumbers;
    }

    public IonEligibilitySuccess limitNumbers(Integer limitNumbers) {
        this.limitNumbers = limitNumbers;
        return this;
    }

    public Integer getLimitNumbers() {
        return limitNumbers;
    }

    public void setLimitNumbers(Integer limitNumbers) {
        this.limitNumbers = limitNumbers;
    }

    public IonEligibilitySuccess prefixes(List<PhoneNumberPrefix> prefixes) {
        this.prefixes = prefixes;
        return this;
    }

    public IonEligibilitySuccess addPrefixesItem(PhoneNumberPrefix prefixesItem) {
        if (this.prefixes == null) {
            this.prefixes = new ArrayList<PhoneNumberPrefix>();
        }
        this.prefixes.add(prefixesItem);
        return this;
    }

    public List<PhoneNumberPrefix> getPrefixes() {
        return prefixes;
    }

    public void setPrefixes(List<PhoneNumberPrefix> prefixes) {
        this.prefixes = prefixes;
    }

    public IonEligibilitySuccess offerId(String offerId) {
        this.offerId = offerId;
        return this;
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        IonEligibilitySuccess ionEligibilitySuccess = (IonEligibilitySuccess) o;
        return Objects.equals(this.fnfNumbers, ionEligibilitySuccess.fnfNumbers) &&
                Objects.equals(this.limitNumbers, ionEligibilitySuccess.limitNumbers) &&
                Objects.equals(this.prefixes, ionEligibilitySuccess.prefixes) &&
                Objects.equals(this.offerId, ionEligibilitySuccess.offerId) &&
                super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fnfNumbers, limitNumbers, prefixes, offerId, super.hashCode());
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class IonEligibilitySuccess {\n");
        sb.append("    ").append(toIndentedString(super.toString())).append("\n");
        sb.append("    fnfNumbers: ").append(toIndentedString(fnfNumbers)).append("\n");
        sb.append("    limitNumbers: ").append(toIndentedString(limitNumbers)).append("\n");
        sb.append("    prefixes: ").append(toIndentedString(prefixes)).append("\n");
        sb.append("    offerId: ").append(toIndentedString(offerId)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

}
