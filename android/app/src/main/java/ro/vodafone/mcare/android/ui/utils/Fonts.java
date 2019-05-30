package ro.vodafone.mcare.android.ui.utils;
/**
 * Created by Bivol Pavel on 30.03.2016.
 */
import android.content.Context;
import android.graphics.Typeface;

public class Fonts {
    private static Typeface canaroExtraBold;
    private static Typeface vodafoneLT;
    private static Typeface vodafoneRG;
    private static Typeface vodafoneRGBD;

    private Fonts() {}

    public static void init(Context context) {
        Fonts.canaroExtraBold =
                Typeface.createFromAsset(context.getAssets(), "fonts/canaro_extra_bold.ttf");
        Fonts.vodafoneLT =
                Typeface.createFromAsset(context.getAssets(), "fonts/vodafonelt.ttf");
        Fonts.vodafoneRG =
                Typeface.createFromAsset(context.getAssets(), "fonts/vodafonerg.ttf");
        Fonts.vodafoneRGBD =
                Typeface.createFromAsset(context.getAssets(), "fonts/vodafonergbd.ttf");
    }

    public static Typeface getCanaroExtraBold() {
        return canaroExtraBold;
    }

    public static Typeface getVodafoneLT() {
        return vodafoneLT;
    }

    public static Typeface getVodafoneRG() {
        return vodafoneRG;
    }

    public static Typeface getVodafoneRGBD() {
        return vodafoneRGBD;
    }
}