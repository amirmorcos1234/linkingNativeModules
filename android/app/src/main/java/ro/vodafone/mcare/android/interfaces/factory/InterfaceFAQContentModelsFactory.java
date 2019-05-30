package ro.vodafone.mcare.android.interfaces.factory;

import android.view.View;

/**
 * Created by Bogdan Marica on 9/7/2017.
 */

public interface InterfaceFAQContentModelsFactory {

    View getFAQTextModel(String value);

    View getFAQImageModel(String value);

    View getFAQVideoModel(String value);

    View getFAQButtonModel();

}
