package ro.vodafone.mcare.android.ui.views.vodafoneSpinner;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.interfaces.VodafoneSpinnerObjectInterface;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.views.textviews.AutoResizeTextView;

/**
 * Created by Bivol Pavel on 02/27/2017.
 */

@SuppressWarnings("unused")
public class VodafoneSpinner extends AutoResizeTextView {

    private static final int MAX_LEVEL = 10000;
    private static final int DEFAULT_ELEVATION = 16;
    private static final String INSTANCE_STATE = "instance_state";
    private static final String SELECTED_INDEX = "selected_index";
    private static final String IS_POPUP_SHOWING = "is_popup_showing";

    private int selectedIndex;
    private Drawable drawable;
    private PopupWindow popupWindow;
    private ListView listView;
    private BaseAdapter adapter;
    private AdapterView.OnItemClickListener onItemClickListener;
    private AdapterView.OnItemSelectedListener onItemSelectedListener;
    private boolean isArrowHide;
    private int textColor;
    private int backgroundSelector;
    private boolean isDisplayedDefaultValue;
    private String defaulValue;
    private Object selectedValue;
    private boolean isFromChildFragment = false;
    private Callback callback;


    @SuppressWarnings("ConstantConditions")
    public VodafoneSpinner(Context context) {
        super(context);
        init(context, null);
    }

    public VodafoneSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public VodafoneSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putInt(SELECTED_INDEX, selectedIndex);
        if (popupWindow != null) {
            bundle.putBoolean(IS_POPUP_SHOWING, popupWindow.isShowing());
            dismissDropDown();
        }
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable savedState) {
        if (savedState instanceof Bundle) {
            Bundle bundle = (Bundle) savedState;
            selectedIndex = bundle.getInt(SELECTED_INDEX);

            if (adapter != null) {
                setSelectedValue(adapter.getItem(selectedIndex));
                //setText(adapter.getItem(selectedIndex).toString());
                //adapter.notifyItemSelected(selectedIndex);
                adapter.notifyDataSetChanged();
            }

            if (bundle.getBoolean(IS_POPUP_SHOWING)) {
                if (popupWindow != null) {
                    // Post the show request into the looper to avoid bad token exception
                    post(new Runnable() {
                        @Override
                        public void run() {
                            showDropDown();
                        }
                    });
                }
            }
            savedState = bundle.getParcelable(INSTANCE_STATE);
        }
        super.onRestoreInstanceState(savedState);
    }

    private void init(final Context context, AttributeSet attrs) {
        Resources resources = getResources();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.VodafoneSpinner);
        int defaultPadding = resources.getDimensionPixelSize(R.dimen.one_and_a_half_grid_unit);

        setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        setPadding(resources.getDimensionPixelSize(R.dimen.three_grid_unit), defaultPadding, defaultPadding,
                defaultPadding);
        setClickable(true);

        backgroundSelector = typedArray.getResourceId(R.styleable.VodafoneSpinner_backgroundSelector, R.drawable.selector);
        setBackgroundResource(backgroundSelector);

        defaulValue = typedArray.getString(R.styleable.VodafoneSpinner_defaultValue);

        if (defaulValue != null) {
            isDisplayedDefaultValue = true;
        }

        textColor = typedArray.getColor(R.styleable.VodafoneSpinner_textTint, -1);
        setTextColor(textColor);

        setBackground(ContextCompat.getDrawable(context, R.drawable.gray_default_input_border));

        listView = new ListView(context);
        // Set the spinner's id into the listview to make it pretend to be the right parent in
        // onItemClick
        listView.setId(getId());

        listView.setDivider(new ColorDrawable(Color.LTGRAY));
        listView.setDividerHeight(ScreenMeasure.dpToPx(1));

        listView.setItemsCanFocus(true);
        //hide vertical and horizontal scrollbars
        listView.setVerticalScrollBarEnabled(false);
        listView.setHorizontalScrollBarEnabled(false);

        listView.setOnTouchListener(new OnTouchListener() {

            float startX;
            float startY;

            @Override
            public boolean onTouch(View view, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        startY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float nStartX = event.getX();
                        float nStartY = event.getY();

                        float realSize = ((listView.getCount() * (listView.getChildAt(0).getHeight() + listView.getDividerHeight())) - listView.getDividerHeight());

//                        D.w("listView size = " + listView.getHeight());
//                        D.w("listView size = " + realSize);


                        if (listView.getHeight() == realSize) {
                            if (Math.abs(startX - nStartX) > ScreenMeasure.dpToPx(100))
                                popupWindow.dismiss();
                            if (Math.abs(startY - nStartY) > ScreenMeasure.dpToPx(50))
                                popupWindow.dismiss();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }


                return false;

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Need to set selected index before calling listeners or getSelectedIndex() can be
                // reported incorrectly due to race conditions.
                selectedIndex = position;

                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(parent, view, position, id);
                }

                if (onItemSelectedListener != null) {
                    onItemSelectedListener.onItemSelected(parent, view, position, id);
                }


                adapter.notifyDataSetChanged();
                setSelectedValue(adapter.getItem(position));

                if (callback != null) {
                    callback.selectSpinnerElement(adapter.getItem(position));
                }

                //settext(adapter.getItemInDataset(position).toString())

                dismissDropDown();
            }
        });

        popupWindow = new PopupWindow(context);
        popupWindow.setContentView(listView);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.spinner_drawable));

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                if (!isArrowHide) {
                    animateArrow(false);
                }
            }
        });


        isArrowHide = typedArray.getBoolean(R.styleable.VodafoneSpinner_hideArrow, false);
        if (!isArrowHide) {
            Drawable basicDrawable = ContextCompat.getDrawable(context, R.drawable.arrow);


            int resId = typedArray.getColor(R.styleable.VodafoneSpinner_arrowTint, -1);
            if (basicDrawable != null) {
                drawable = DrawableCompat.wrap(basicDrawable);

                if (resId != -1) {
                    DrawableCompat.setTint(drawable, resId);
                }
            }
            setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
        }
        typedArray.recycle();
        drawable.mutate();
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    /**
     * Set the default spinner item using its index
     *
     * @param position the item's position
     */
    public void setSelectedIndex(int position) {
        if (adapter != null) {
            if (position >= 0 && position <= adapter.getCount()) {
                adapter.notifyDataSetChanged();
                //adapter.notifyItemSelected(position);
                selectedIndex = position;
                //setText(adapter.getItemInDataset(position).toString());
                setSelectedValue(adapter.getItem(position).toString());
            } else {
                throw new IllegalArgumentException("Position must be lower than adapter count!");
            }
        }
    }

    public void setSelectedValue(Object value) {
        //      Log.d("", "setSelected value :" +  value);
        //     Log.d("", "setSelected value displayed :" +  isDisplayedDefaultValue);
        selectedValue = value;
        if (isDisplayedDefaultValue) {
            setText(defaulValue);
        } else {
            if (value instanceof VodafoneSpinnerObjectInterface) {
                if (((VodafoneSpinnerObjectInterface) value).getDisplayedSpinnerValue() != null) {
                    setText(((VodafoneSpinnerObjectInterface) value).getDisplayedSpinnerValue());
                }
            } else {
                try {
                    setText(String.valueOf(value));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void addOnItemClickListener(@NonNull AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemSelectedListener(@NonNull AdapterView.OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    public BaseAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(@NonNull BaseAdapter baseAdapter) {
        adapter = baseAdapter;
        setAdapterInternal(adapter);
    }

    private void setAdapterInternal(@NonNull BaseAdapter adapter) {
        // If the adapter needs to be settled again, ensure to reset the selected index as well
        selectedIndex = 0;
        listView.setAdapter(adapter);
        //setText(adapter.getItemInDataset(selectedIndex).toString());
        setSelectedValue(adapter.getItem(selectedIndex).toString());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        popupWindow.setWidth(MeasureSpec.getSize(widthMeasureSpec));
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
//        D.w("event = " + event.getAction());

        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (!popupWindow.isShowing()) {
                showDropDown();
            } else {
                dismissDropDown();
            }
        }
        return super.onTouchEvent(event);
    }

    private void animateArrow(boolean shouldRotateUp) {
        int start = shouldRotateUp ? 0 : MAX_LEVEL;
        int end = shouldRotateUp ? MAX_LEVEL : 0;
        ObjectAnimator animator = ObjectAnimator.ofInt(drawable, "level", start, end);
        animator.setInterpolator(new LinearOutSlowInInterpolator());
        animator.start();
    }

    public void dismissDropDown() {
        if (!isArrowHide) {
            animateArrow(false);
        }
        popupWindow.dismiss();
    }

    public void showDropDown() {
        if (!isArrowHide) {
            animateArrow(true);
        }

        int[] a = new int[2];
        this.getLocationInWindow(a);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            popupWindow.showAtLocation(((AppCompatActivity) getContext()).getWindow().getDecorView(), Gravity.NO_GRAVITY, a[0], a[1] + this.getHeight());
        else
            popupWindow.showAsDropDown(this);

    }

    public void setTintColor(@ColorRes int resId) {
        if (drawable != null && !isArrowHide) {
            DrawableCompat.setTint(drawable, ContextCompat.getColor(getContext(), resId));
        }
    }

    public boolean isFromChildFragment() {
        return isFromChildFragment;
    }

    public void setFromChildFragment(boolean fromChildFragment) {
        isFromChildFragment = fromChildFragment;
    }

    public interface Callback {
        public void selectSpinnerElement(Object selectedValue);
    }
}
