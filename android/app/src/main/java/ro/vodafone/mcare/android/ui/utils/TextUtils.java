package ro.vodafone.mcare.android.ui.utils;

import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import org.xml.sax.XMLReader;

import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;

/**
 * Created by Victor Radulescu on 1/20/2017.
 */

public class TextUtils {

    private static final String TAG = "TextUtils";
    public static Pattern EMAIL_PATTERN = Pattern.compile("^(.+)@(.+)(\\.(.{2,}))$");

    public static Spanned fromHtml(String html) {
        if(html==null){
            return null;
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {

            return Html.fromHtml(html, null, new UlTagHandler());
        }

    }

    public static Spanned fromHtmlWithoutSpace(String html) {
        if(html==null){
            return null;
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            return Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT);
        } else {
            
            return Html.fromHtml(html, null, new LiTagHandlerWithoutSpace());
        }
    }

    public static String removeParagraphTags(String html){
        if(html == null) {
            return null;
        }
        if(html.equals("")){
            return null;
        }

        html = html.replaceAll("<p>", "");
        html = html.replaceAll("</p>", "");
        return html;
    }

    private static class UlTagHandler implements Html.TagHandler {

        private static final String LI_TAG = "li";
        private static final String UL_TAG = "ul";

        @Override
        public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
            if(tag.equals(LI_TAG) && opening) output.append("• ");
            if(tag.equals(LI_TAG) && !opening) output.append("\n\n");

            if(tag.equals(UL_TAG) && opening) output.append("");
        }

    }

    private static class LiTagHandlerWithoutSpace implements Html.TagHandler {

        private static final String LI_TAG = "li";
        private static final String UL_TAG = "ul";

        @Override
        public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
            if(tag.equals(LI_TAG) && opening) output.append("• ");
            if(tag.equals(LI_TAG) && !opening) output.append("\n");
        }

    }

    /**
     * Sets the text size for a Paint object so a given string of text will be a
     * given width.
     *
     * @param paint
     *            the Paint to set the text size for
     * @param desiredWidth
     *            the desired width
     * @param text
     *            the text that should be that width
     */
    public static void setTextSizeForWidth(Paint paint, float desiredWidth,
                                            String text) {

        // Pick a reasonably large value for the test. Larger values produce
        // more accurate results, but may cause problems with hardware
        // acceleration. But there are workarounds for that, too; refer to
        // http://stackoverflow.com/questions/6253528/font-size-too-large-to-fit-in-cache
        final float testTextSize = 48f;

        // Get the bounds of the text, using our testTextSize.
        paint.setTextSize(testTextSize);
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);

        // Calculate the desired size as a proportion of our testTextSize.
        float desiredTextSize = testTextSize * desiredWidth / bounds.width();

        // Set the paint for that size.
        paint.setTextSize(desiredTextSize);
    }

    public static String fromMillisToDate(Long input, String dateFormat){
        if(input == null){
            return null;
        }

        Date date = new Date(input);
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat, Locale.getDefault());
        formatter.setTimeZone(TimeZone.getDefault());
        return formatter.format(date);
    }

    public static boolean checkValidInputText(String input){

        if(input == null)
            return false;

        input = input.trim();

        if(input.isEmpty())
            return false;

        return true;

    }

    public boolean isValidEmail(String emailAdress) {
        return !android.text.TextUtils.isEmpty(emailAdress) && EMAIL_PATTERN.matcher(emailAdress).matches() && emailAdress.substring(emailAdress.lastIndexOf(".")).length() > 2;
    }

    public static String removeDiacriticalMarks(String string) {
        return Normalizer.normalize(string, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    public static String NullIfEmptyAndTrim(String string)
    {
        if(string == null)
            return null;

        string = string.trim();

        if(string.length() == 0)
            return null;

        return string;
    }

    public static void setTextViewClickableLinks(TextView tv) {

        tv.setClickable(true);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        tv.setLinksClickable(true);
        tv.setLinkTextColor(ContextCompat.getColor(VodafoneController.currentActivity(), R.color.blue_chart_top_color));
        tv.setHighlightColor(ContextCompat.getColor(VodafoneController.currentActivity(), R.color.blue_chart_top_color));

    }


}
