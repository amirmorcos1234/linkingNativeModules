package ro.vodafone.mcare.android.client.adapters.filters;

import android.content.Context;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;

/**
 * Created by Victor Radulescu on 4/5/2017.
 */

public class MultipleSelectionFilterAdapter extends BaseAdapter {

    List<String> selectionLists;

    Context context;

    boolean [] anySelectionChecked;

    boolean currentAreAnySelectionChecked = false;

    OnSelectionListenr selectionListenr;

    ArrayList<String> savedSelections;


    public MultipleSelectionFilterAdapter(Context context, List<String> selectionLists, OnSelectionListenr selectionListenr, String[] savedSelections)  {
        this.selectionLists = selectionLists;
        this.context = context;
        this.selectionListenr = selectionListenr;
        anySelectionChecked = new boolean[selectionLists.size()];
        this.savedSelections = savedSelections!=null? new ArrayList<String>(Arrays.asList(savedSelections)) :new ArrayList<String>();
    }

    @Override
    public int getCount() {
        return selectionLists !=null? selectionLists.size():0;
    }

    @Override
    public Object getItem(int position) {
        return selectionLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final AppCompatCheckBox checkBox = new AppCompatCheckBox(new ContextThemeWrapper(context, R.style.FilterSelectionCheckBox), null, 0);
        checkBox.setTextColor(AppCompatResources.getColorStateList(checkBox.getContext(), R.color.selector_checkbox_filter_selection_text));
        checkBox.setMinHeight(ScreenMeasure.dpToPx(32));
        checkBox.setPaddingRelative(ScreenMeasure.dpToPx(10),0,ScreenMeasure.dpToPx(10),0);

        setSavedValue(checkBox,position);

        checkBox.setText(selectionLists.get(position));
        checkBox.setTag(position);
        final int checkboxPositionInList = position;
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onCheckItem(isChecked,checkboxPositionInList);
            }
        });
        if(position == (getCount()-1) || position == (getCount() - 2)) {

            ViewTreeObserver vto = checkBox.getViewTreeObserver();
            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    if (checkBox.getViewTreeObserver().isAlive()) {
                        checkBox.getViewTreeObserver().removeOnPreDrawListener(this);
                        // only need to calculate once, so remove listener
//                        checkBox.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                    int realCheckBoxHeight = checkBox.getHeight();
                    ViewGroup.LayoutParams  layoutParams = checkBox.getLayoutParams();
                    layoutParams.height = realCheckBoxHeight;
                    checkBox.setHeight(realCheckBoxHeight);
                    checkBox.setLayoutParams(layoutParams);
                    checkBox.invalidate();
                    return true;
                }

            });
        }
        return checkBox;
    }
    public interface OnSelectionListenr {
        void onAnySelectionSelected(boolean anySelected);
    }
    public void onCheckItem(boolean checked, int position){
        try{

            if(checked) {
                savedSelections.add(selectionLists.get(position));
            } else
            if(savedSelections.contains(selectionLists.get(position))) {
                savedSelections.remove(selectionLists.get(position));
            }

            anySelectionChecked[position] = checked;

            if(anySelectionChecked()){
                if(!currentAreAnySelectionChecked){
                    currentAreAnySelectionChecked = true;
                    selectionListenr.onAnySelectionSelected(true);
                }
            }else if(currentAreAnySelectionChecked){
                currentAreAnySelectionChecked = false;
                selectionListenr.onAnySelectionSelected(false);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setSavedValue(CheckBox checkBox,int position){
        if(savedSelections !=null ){
            boolean checked = savedSelections.contains((selectionLists.get(position)));
            currentAreAnySelectionChecked = checked || currentAreAnySelectionChecked;
            checkBox.setChecked(checked);
            selectionListenr.onAnySelectionSelected(anySelectionChecked());
            anySelectionChecked[position] = checked;
        }
    }

    private boolean anySelectionChecked(){
        return !savedSelections.isEmpty();
    }

    public void setCurrentAreAnySelectionChecked(boolean currentAreAnySelectionChecked) {
        this.currentAreAnySelectionChecked = currentAreAnySelectionChecked;
    }

    public ArrayList<String> getSavedSelections() {
        return savedSelections;
    }
}

