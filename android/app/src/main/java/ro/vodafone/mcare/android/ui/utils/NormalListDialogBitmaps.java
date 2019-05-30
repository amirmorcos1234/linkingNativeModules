package ro.vodafone.mcare.android.ui.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.utils.CornerUtils;
import com.flyco.dialog.widget.base.BaseDialog;

import java.util.ArrayList;

/**
 * Created by Andrei DOLTU on 12/7/2016.
 */

public class NormalListDialogBitmaps extends BaseDialog<NormalListDialogBitmaps> {
    /** ListView */
    private ListView mLv;
    /** title */
    private TextView mTvTitle;
    /** corner radius,dp() */
    private float mCornerRadius = 5;
    /** title background color() */
    private int mTitleBgColor = Color.parseColor("#303030");
    /** title text() */
    private String mTitle = "Title";
    /** title textcolor() */
    private int mTitleTextColor = Color.parseColor("#ffffff");
    /** title textsize() */
    private float mTitleTextSize = 16.5f;
    /** ListView background color(ListView) */
    private int mLvBgColor = Color.parseColor("#ffffff");
    /** divider color(ListView divider) */
    private int mDividerColor = Color.LTGRAY;
    /** divider height(ListView divider) */
    private float mDividerHeight = 0.8f;
    /** item press color(ListView item) */
    private int mItemPressColor = Color.parseColor("#ffcccccc");
    /** item textcolor(ListView item) */
    private int mItemTextColor = Color.parseColor("#303030");
    /** item textsize(ListView item) */
    private float mItemTextSize = 15f;
    /** item extra padding(ListView item padding) */
    private int mItemExtraLeft;
    private int mItemExtraTop;
    private int mItemExtraRight;
    private int mItemExtraBottom;
    /** enable title show() */
    private boolean mIsTitleShow = true;
    /** adapter() */
    private BaseAdapter mAdapter;
    /** operation items(items) */
    private ArrayList<DialogMenuItemBitmaps> mContents = new ArrayList<>();
    private OnOperItemClickL mOnOperItemClickL;
    private LayoutAnimationController mLac;

    public void setOnOperItemClickL(OnOperItemClickL onOperItemClickL) {
        mOnOperItemClickL = onOperItemClickL;
    }


    public NormalListDialogBitmaps(Context context, ArrayList<DialogMenuItemBitmaps> baseItems) {
        super(context);
        mContents.addAll(baseItems);
        init();
    }

    public NormalListDialogBitmaps(Context context, BaseAdapter adapter) {
        super(context);
        mAdapter = adapter;
        init();
    }

    private void init() {
        widthScale(0.8f);

        /** LayoutAnimation */
        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 2f, Animation.RELATIVE_TO_SELF,
                0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.setDuration(550);

        mLac = new LayoutAnimationController(animation, 0.12f);
        mLac.setInterpolator(new DecelerateInterpolator());
    }

    @Override
    public View onCreateView() {
        LinearLayout ll_container = new LinearLayout(mContext);
        ll_container.setOrientation(LinearLayout.VERTICAL);

        /** title */
        mTvTitle = new TextView(mContext);
        mTvTitle.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        mTvTitle.setSingleLine(true);
        mTvTitle.setPadding(dp2px(18), dp2px(10), 0, dp2px(10));

        ll_container.addView(mTvTitle);

        /** listview */
        mLv = new ListView(mContext);
        mLv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        mLv.setCacheColorHint(Color.TRANSPARENT);
        mLv.setFadingEdgeLength(0);
        mLv.setVerticalScrollBarEnabled(false);
        mLv.setSelector(new ColorDrawable(Color.TRANSPARENT));

        ll_container.addView(mLv);

        return ll_container;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setUiBeforShow() {
        /** title */
        float radius = dp2px(mCornerRadius);
        mTvTitle.setBackgroundDrawable(CornerUtils.cornerDrawable(mTitleBgColor, new float[]{radius, radius, radius,
                radius, 0, 0, 0, 0}));
        mTvTitle.setText(mTitle);
        mTvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTitleTextSize);
        mTvTitle.setTextColor(mTitleTextColor);
        mTvTitle.setVisibility(mIsTitleShow ? View.VISIBLE : View.GONE);

        /** listview */
        mLv.setDivider(new ColorDrawable(mDividerColor));
        mLv.setDividerHeight(dp2px(mDividerHeight));

        if (mIsTitleShow) {
            mLv.setBackgroundDrawable(CornerUtils.cornerDrawable(mLvBgColor, new float[]{0, 0, 0, 0, radius, radius, radius,
                    radius}));
        } else {
            mLv.setBackgroundDrawable(CornerUtils.cornerDrawable(mLvBgColor, radius));
        }

        if (mAdapter == null) {
            mAdapter = new NormalListDialogBitmaps.ListDialogAdapter();
        }

        mLv.setAdapter(mAdapter);
        mLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mOnOperItemClickL != null) {
                    mOnOperItemClickL.onOperItemClick(parent, view, position, id);
                }
            }
        });

        mLv.setLayoutAnimation(mLac);
    }

    /** set title background color() @return NormalListDialog */
    public NormalListDialogBitmaps titleBgColor(int titleBgColor) {
        mTitleBgColor = titleBgColor;
        return this;
    }

    /** set title text() */
    public NormalListDialogBitmaps title(String title) {
        mTitle = title;
        return this;
    }

    /** set title textsize() */
    public NormalListDialogBitmaps titleTextSize_SP(float titleTextSize_SP) {
        mTitleTextSize = titleTextSize_SP;
        return this;
    }

    /** set title textcolor() */
    public NormalListDialogBitmaps titleTextColor(int titleTextColor) {
        mTitleTextColor = titleTextColor;
        return this;
    }

    /*** enable title show() */
    public NormalListDialogBitmaps isTitleShow(boolean isTitleShow) {
        mIsTitleShow = isTitleShow;
        return this;
    }

    /** set ListView background color(ListView) */
    public NormalListDialogBitmaps lvBgColor(int lvBgColor) {
        mLvBgColor = lvBgColor;
        return this;
    }

    /** set corner radius() */
    public NormalListDialogBitmaps cornerRadius(float cornerRadius_DP) {
        mCornerRadius = cornerRadius_DP;
        return this;
    }

    /** set divider color(ListView divider) */
    public NormalListDialogBitmaps dividerColor(int dividerColor) {
        mDividerColor = dividerColor;
        return this;
    }

    /** set divider height(ListView divider) */
    public NormalListDialogBitmaps dividerHeight(float dividerHeight_DP) {
        mDividerHeight = dividerHeight_DP;
        return this;
    }

    /** set item press color(item) */
    public NormalListDialogBitmaps itemPressColor(int itemPressColor) {
        mItemPressColor = itemPressColor;
        return this;
    }

    /** set item textcolor(item) */
    public NormalListDialogBitmaps itemTextColor(int itemTextColor) {
        mItemTextColor = itemTextColor;
        return this;
    }

    /** set item textsize(item) */
    public NormalListDialogBitmaps itemTextSize(float itemTextSize_SP) {
        mItemTextSize = itemTextSize_SP;
        return this;
    }

    /** set item height(item) */
    public NormalListDialogBitmaps setItemExtraPadding(int itemLeft, int itemTop, int itemRight, int itemBottom) {
        mItemExtraLeft = dp2px(itemLeft);
        mItemExtraTop = dp2px(itemTop);
        mItemExtraRight = dp2px(itemRight);
        mItemExtraBottom = dp2px(itemBottom);

        return this;
    }

    /** set layoutAnimation(layout ,null layout) */
    public NormalListDialogBitmaps layoutAnimation(LayoutAnimationController lac) {
        mLac = lac;
        return this;
    }

    class ListDialogAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mContents.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressWarnings("deprecation")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final DialogMenuItemBitmaps item = mContents.get(position);

            LinearLayout llItem = new LinearLayout(mContext);
            llItem.setOrientation(LinearLayout.HORIZONTAL);
            llItem.setGravity(Gravity.CENTER_VERTICAL);

            ImageView ivItem = new ImageView(mContext);
            ivItem.setPadding(0, 0, dp2px(15), 0);
            llItem.addView(ivItem);

            TextView tvItem = new TextView(mContext);
            tvItem.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            tvItem.setSingleLine(true);
            tvItem.setTextColor(mItemTextColor);
            tvItem.setTextSize(TypedValue.COMPLEX_UNIT_SP, mItemTextSize);

            llItem.addView(tvItem);
            float radius = dp2px(mCornerRadius);
            if (mIsTitleShow) {
                llItem.setBackgroundDrawable((CornerUtils.listItemSelector(radius, Color.TRANSPARENT, mItemPressColor,
                        position == mContents.size() - 1)));
            } else {
                llItem.setBackgroundDrawable(CornerUtils.listItemSelector(radius, Color.TRANSPARENT, mItemPressColor,
                        mContents.size(), position));
            }

            int left = dp2px(16);
            int top = dp2px(10);
            int right = 0;
            int bottom = dp2px(10);
            llItem.setPadding(left + mItemExtraLeft, top + mItemExtraTop, right + mItemExtraRight, bottom + mItemExtraBottom);

            ivItem.setImageBitmap(item.mResBitmap);
            tvItem.setText(item.mOperName);
            ivItem.setVisibility(View.VISIBLE);

            return llItem;
        }
    }
}