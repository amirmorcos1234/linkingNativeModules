package ro.vodafone.mcare.android.ui.activities.support;

/**
 * Created by User1 on 4/2/2017.
 */

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ro.vodafone.mcare.android.DontObfuscate;

@DontObfuscate
public class JsonList {

    private String name;
    private List<JsonItem> items = null;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<JsonItem> getItems() {
        return items;
    }

    public void setItems(List<JsonItem> items) {
        this.items = items;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}