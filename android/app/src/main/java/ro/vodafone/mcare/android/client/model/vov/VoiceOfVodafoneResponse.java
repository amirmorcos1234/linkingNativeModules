package ro.vodafone.mcare.android.client.model.vov;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Victor Radulescu on 2/2/2018.
 */
public class VoiceOfVodafoneResponse {

@SerializedName("VoVs")
@Expose
private List<VoiceOfVodafoneRestPojo> voVs = null;

public List<VoiceOfVodafoneRestPojo> getVoVs() {
return voVs;
}

public void setVoVs(List<VoiceOfVodafoneRestPojo> voVs) {
this.voVs = voVs;
}

}
