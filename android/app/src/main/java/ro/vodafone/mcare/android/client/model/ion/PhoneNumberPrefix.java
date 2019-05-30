package ro.vodafone.mcare.android.client.model.ion;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class PhoneNumberPrefix {

    @SerializedName("prefix")
    private String prefix = null;

    @SerializedName("country")
    private String country = null;

    @SerializedName("minLength")
    private int minLength;

    @SerializedName("maxLength")
    private int maxLength;

    @SerializedName("desc")
    private String desc = null;

    public PhoneNumberPrefix prefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public PhoneNumberPrefix country(String country) {
        this.country = country;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public PhoneNumberPrefix minLength(Integer minLength) {
        this.minLength = minLength;
        return this;
    }

    public Integer getMinLength() {
        return minLength;
    }

    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
    }

    public PhoneNumberPrefix maxLength(Integer maxLength) {
        this.maxLength = maxLength;
        return this;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    public PhoneNumberPrefix desc(String desc) {
        this.desc = desc;
        return this;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PhoneNumberPrefix phoneNumberPrefix = (PhoneNumberPrefix) o;
        return Objects.equals(this.prefix, phoneNumberPrefix.prefix) &&
                Objects.equals(this.country, phoneNumberPrefix.country) &&
                Objects.equals(this.minLength, phoneNumberPrefix.minLength) &&
                Objects.equals(this.maxLength, phoneNumberPrefix.maxLength) &&
                Objects.equals(this.desc, phoneNumberPrefix.desc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(prefix, country, minLength, maxLength, desc);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class PhoneNumberPrefix {\n");

        sb.append("    prefix: ").append(toIndentedString(prefix)).append("\n");
        sb.append("    country: ").append(toIndentedString(country)).append("\n");
        sb.append("    minLength: ").append(toIndentedString(minLength)).append("\n");
        sb.append("    maxLength: ").append(toIndentedString(maxLength)).append("\n");
        sb.append("    desc: ").append(toIndentedString(desc)).append("\n");
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
