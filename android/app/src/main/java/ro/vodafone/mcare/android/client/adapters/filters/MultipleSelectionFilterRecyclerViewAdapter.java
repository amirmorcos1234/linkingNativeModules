package ro.vodafone.mcare.android.client.adapters.filters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;

/**
 * Created by user1 on 22-Dec-17.
 */

public class MultipleSelectionFilterRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<String> selectionLists;

    Context context;

    boolean[] anySelectionChecked;

    boolean currentAreAnySelectionChecked = false;

    MultipleSelectionFilterRecyclerViewAdapter.OnSelectionListenr selectionListenr;

    ArrayList<String> savedSelections;


    public MultipleSelectionFilterRecyclerViewAdapter(Context context, List<String> selectionLists, MultipleSelectionFilterRecyclerViewAdapter.OnSelectionListenr selectionListenr, String[] savedSelections) {
        this.selectionLists = selectionLists;
        this.context = context;
        this.selectionListenr = selectionListenr;
        anySelectionChecked = new boolean[selectionLists.size()];
        this.savedSelections = savedSelections != null ? new ArrayList<String>(Arrays.asList(savedSelections)) : new ArrayList<String>();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        @SuppressLint("RestrictedApi") final AppCompatCheckBox checkBox = new AppCompatCheckBox(new ContextThemeWrapper(context, R.style.FilterSelectionCheckBox), null, 0);
        checkBox.setTextColor(AppCompatResources.getColorStateList(checkBox.getContext(), R.color.selector_checkbox_filter_selection_text));
        checkBox.setMinHeight(ScreenMeasure.dpToPx(32));
        checkBox.setPaddingRelative(ScreenMeasure.dpToPx(10), 0, ScreenMeasure.dpToPx(10), 0);


        return new CheckBoxViewHolder(checkBox);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder tempHolder, final int position) {

        CheckBoxViewHolder holder = (CheckBoxViewHolder) tempHolder;

        int topMargin = 0, rightMargin = 0, bottomMargin = 0, leftMargin = 0;

        if (position >= 2)
            topMargin = (int) context.getResources().getDimension(R.dimen.general_margin);

        if (position % 2 == 0)
            rightMargin = (int) context.getResources().getDimension(R.dimen.general_margin);


        //LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) holder.checkBox.getLayoutParams();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);

        holder.checkBox.setLayoutParams(lp);

        setSavedValue(holder.checkBox, position);

        holder.checkBox.setText(selectionLists.get(position));
        holder.checkBox.setTag(position);
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onCheckItem(isChecked, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return selectionLists != null ? selectionLists.size() : 0;
    }

    public static class CheckBoxViewHolder extends RecyclerView.ViewHolder {

        public AppCompatCheckBox checkBox;

        public CheckBoxViewHolder(View view) {
            super(view);
            checkBox = (AppCompatCheckBox) view;

        }
    }


    public Object getItem(int position) {
        return selectionLists.get(position);
    }

    public interface OnSelectionListenr {
        void onAnySelectionSelected(boolean anySelected);
    }

    public void onCheckItem(boolean checked, int position) {
        try {

            if (checked) {
                savedSelections.add(selectionLists.get(position));
            } else if (savedSelections.contains(selectionLists.get(position))) {
                savedSelections.remove(selectionLists.get(position));
            }

            anySelectionChecked[position] = checked;

            if (anySelectionChecked()) {
                if (!currentAreAnySelectionChecked) {
                    currentAreAnySelectionChecked = true;
                    selectionListenr.onAnySelectionSelected(true);
                }
            } else if (currentAreAnySelectionChecked) {
                currentAreAnySelectionChecked = false;
                selectionListenr.onAnySelectionSelected(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setSavedValue(CheckBox checkBox, int position) {
        if (savedSelections != null) {
            boolean checked = savedSelections.contains((selectionLists.get(position)));
            currentAreAnySelectionChecked = checked || currentAreAnySelectionChecked;
            checkBox.setChecked(checked);
            selectionListenr.onAnySelectionSelected(anySelectionChecked());
            anySelectionChecked[position] = checked;
        }
    }

    private boolean anySelectionChecked() {
        return !savedSelections.isEmpty();
    }

    public void setCurrentAreAnySelectionChecked(boolean currentAreAnySelectionChecked) {
        this.currentAreAnySelectionChecked = currentAreAnySelectionChecked;
    }

    public ArrayList<String> getSavedSelections() {
        return savedSelections;
    }
}
