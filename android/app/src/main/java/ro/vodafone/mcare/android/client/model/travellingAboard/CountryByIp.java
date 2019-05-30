package ro.vodafone.mcare.android.client.model.travellingAboard;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Eliza Deaconescu
 */

public class CountryByIp extends RealmObject {

    @PrimaryKey
    @SerializedName("country")
    private String country;

    @SerializedName("countryCode")
    private String countryCode;

    @SerializedName("region")
    private String region;

    @SerializedName("regionName")
    private String regionName;

    public CountryByIp() {}

    public CountryByIp(String country, String countryCode, String region, String regionName) {
        this.country = country;
        this.countryCode = countryCode;
        this.region = region;
        this.regionName = regionName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String countryCodeISO) {
        this.country = country;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryName) {
        this.countryCode = countryCode;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String flagURL) {
        this.region = region;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String messageRo) {
        this.regionName = regionName;
    }


    @Override
    public String toString()  {
        StringBuilder sb = new StringBuilder();
        sb.append("class CountryByIp {\n");
        sb.append("  country: ").append(country).append("\n");
        sb.append("  countryCode: ").append(countryCode).append("\n");
        sb.append("  region: ").append(region).append("\n");
        sb.append("  regionName: ").append(regionName).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}
