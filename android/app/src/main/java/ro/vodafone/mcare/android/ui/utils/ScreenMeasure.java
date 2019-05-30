package ro.vodafone.mcare.android.ui.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;


/**
 * Created by Victor Radulescu on 12/13/2016.
 */

public class ScreenMeasure {

    Context context;

    public ScreenMeasure(Context context) {
        this.context = context;
    }

    public int getHeight(){
        if (context == null) {
            return 0;
        }

        DisplayMetrics displaymetrics = new DisplayMetrics();
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        display.getMetrics(displaymetrics);
        Point point = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealSize(point);
            return point.y;
        }

        return displaymetrics.heightPixels;
    }

    public static int getHeight(Context context){
        if(context==null){
            return 0;
        }
        DisplayMetrics displaymetrics = new DisplayMetrics();
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        display.getMetrics(displaymetrics);
        Point point = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealSize(point);
            return point.y;
        }

        return displaymetrics.heightPixels;
    }

    public int getWidth(){

        DisplayMetrics displaymetrics = new DisplayMetrics();
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        display.getMetrics(displaymetrics);

        return displaymetrics.widthPixels;
    }
    public DisplayMetrics getDisplayMetrics(){

        DisplayMetrics displaymetrics = new DisplayMetrics();
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        display.getMetrics(displaymetrics);

        return displaymetrics;
    }
    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px) {
        float scaleValue = Resources.getSystem().getDisplayMetrics().density;
        return(int) (px * scaleValue + 0.5f);
    }

    public static int spToPx(float sp) {
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, Resources.getSystem().getDisplayMetrics());
        return px;
    }

    public double degreesToRadians(int degrees){
        return  (degrees*Math.PI/180);
    }

    /**
     * This should not be used as it cannot properly calculate the total view heights in a listview
     */
    @Deprecated
    public static void setListHeight(ListView list, ListAdapter listAdapter){
        int totalHeight = 0;//it is the ListView Height
        totalHeight += list.getPaddingTop() + list.getPaddingBottom();
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(list.getWidth(), View.MeasureSpec.EXACTLY);
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            try {
                View listItem = listAdapter.getView(i, null, list);
                listItem.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                int list_child_item_height = listItem.getMeasuredHeight() + list.getDividerHeight();//item height
                totalHeight += list_child_item_height; //
            }catch (Exception ex){
                Log.e("setListHeight", "Exception in setListHeigh", ex);
            }
        }

        ViewGroup.LayoutParams params = list.getLayoutParams();

        if (params != null) {
            params.height = totalHeight;
            list.setLayoutParams(params);
        }
    }

    /**
     * This should not be used as it cannot properly calculate the total view heights in a listview
     */
    @Deprecated
    public static void setExpandableListHeight(ExpandableListView list, ExpandableListAdapter listAdapter){

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getGroupCount(); i++) {



            View listGroupItem = listAdapter.getGroupView(i,true,null,list);
            listGroupItem.measure(0, 0);
            int list_group_item_height = listGroupItem.getMeasuredHeight()+list.getDividerHeight();//item height

            View listItem;

            if(list.isGroupExpanded(i)){
                for(int j = 0; j < listAdapter.getChildrenCount(i); j++) {
                    if(j == listAdapter.getChildrenCount(i)) {
                        listItem = listAdapter.getChildView(i, j,true ,null,list);
                    }else{
                        listItem = listAdapter.getChildView(i, j,false ,null,list);
                    }
                    listItem.measure(0, 0);
                    int list_child_item_height = listItem.getMeasuredHeight()+list.getDividerHeight();//item height
                    totalHeight += list_child_item_height;
                }
            }

            totalHeight += list_group_item_height;
        }



        ViewGroup.LayoutParams params = list.getLayoutParams();

        if (params != null) {
            params.height = totalHeight;
            list.setLayoutParams(params);
        }
    }

    public static void setViewMargins(View view, int dpLeft, int dpTop, int dpRight, int dpBottom){
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
        params.setMargins(dpToPx(dpLeft), dpToPx(dpTop), dpToPx(dpRight), dpToPx(dpBottom));
    }


}
