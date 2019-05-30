package ro.vodafone.mcare.android.client.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ro.vodafone.mcare.android.ui.utils.Fonts;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;

/**
 * Created by user on 24.10.2017.
 */

public class TariffsAdapter extends BaseExpandableListAdapter {


    private Context mContext;
    private List<String> categoryList;
    private Map<String, List<String>> tariffsMap;

    public TariffsAdapter(Context mContext, Set<String> categorySet, Map<String, List<String>> tariffsMap) {
        this.mContext = mContext;
        this.categoryList = new ArrayList<>(categorySet);
        this.tariffsMap = tariffsMap;
    }

    @Override
    public int getGroupCount() {
        return tariffsMap.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return tariffsMap.get(categoryList.get(i)).size();
    }

    @Override
    public Object getGroup(int i) {
        return categoryList.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return tariffsMap.get(categoryList.get(i)).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {

        String category = (String) getGroup(i);

        return setMargins(makeView(view, category, Color.BLACK, 18, Fonts.getVodafoneRGBD()),
                0, ScreenMeasure.dpToPx(18), 0 ,0);
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        String tariff = (String) getChild(i, i1);

        return setMargins(makeView(view, tariff, Color.BLACK, 16, Fonts.getVodafoneRG()),
                0, ScreenMeasure.dpToPx(5), 0, ScreenMeasure.dpToPx(5));
    }

    private TextView makeView(View view, String text, int textColor, int textSize, Typeface typeface){

        TextView v;

        if(view == null){
            v = new TextView(mContext);
        }else{
            v = (TextView) view;
        }

        v.setText(text);
        v.setTextColor(textColor);
        v.setTextSize(textSize);
        v.setTypeface(typeface);

        return v;
    }

    private static View setMargins (View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }else{
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(l, t, r, b);
            v.setLayoutParams(params);
            v.requestLayout();
        }

        return v;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

}
