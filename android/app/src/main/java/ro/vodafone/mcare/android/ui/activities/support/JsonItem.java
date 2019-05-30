package ro.vodafone.mcare.android.ui.activities.support;

/**
 * Created by User1 on 4/2/2017.
 */

import java.util.HashMap;
import java.util.Map;

import ro.vodafone.mcare.android.DontObfuscate;

@DontObfuscate
public class JsonItem {

    private String title;
    private String content;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}