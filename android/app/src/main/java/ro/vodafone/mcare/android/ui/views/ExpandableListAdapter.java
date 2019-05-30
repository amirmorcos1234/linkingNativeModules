package ro.vodafone.mcare.android.ui.views;


import android.animation.ObjectAnimator;
import android.content.Context;
import android.database.DataSetObserver;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidUser;
import ro.vodafone.mcare.android.ui.DynamicColorImageView;
import ro.vodafone.mcare.android.ui.fragments.callDetails.Category;
import ro.vodafone.mcare.android.utils.Logger;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;
import ro.vodafone.mcare.android.widget.balancecards.BalanceCardDetail;
import ro.vodafone.mcare.android.widget.balancecards.BalanceCardHeader;

/**
 * Created by Anca on 10.02.2017.
 */


public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<BalanceCardHeader> _listDataHeader;
    private HashMap<BalanceCardHeader, List<BalanceCardDetail>> _listDataChild;

    public static Logger LOGGER = Logger.getInstance(ExpandableListAdapter.class);

    private OnGroupExpandedListener onGroupExpandedListener;

    private HashMap<Integer, View> arrowToAnimated;

    public ExpandableListAdapter(Context context, List<BalanceCardHeader> listDataHeader,
                                 HashMap<BalanceCardHeader, List<BalanceCardDetail>> listChildData, OnGroupExpandedListener onGroupExpandedListener) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        arrowToAnimated = new HashMap<>();
        this.onGroupExpandedListener = onGroupExpandedListener;
    }

    private boolean isAnimating = false;

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);

    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        BalanceCardHeader balanceCardHeader = (BalanceCardHeader) getGroup(groupPosition);

        LayoutInflater infalInflater = (LayoutInflater) this._context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (childPosition > 0 && childPosition < getChildrenCount(groupPosition) - 1) {
            BalanceCardDetail cardDetail = (BalanceCardDetail) getChild(groupPosition, childPosition - 1);
            convertView = infalInflater.inflate(R.layout.expandable_card_item, null);

            TextView textLeft = (TextView) convertView.findViewById(R.id.textLeft);
            textLeft.setText(cardDetail.getLeftText());

            TextView textRight = (TextView) convertView.findViewById(R.id.textRight);
            textRight.setText(cardDetail.getRightText());

            ImageView icon = (ImageView) convertView.findViewById(R.id.image);
            icon.setBackgroundResource(cardDetail.getIcon());

            //TODO just for sprint 10 , remove if for 
            if (!balanceCardHeader.shouldHideFooter()) {
                setClickListner(icon, cardDetail.getCategory());
            }

            View line = convertView.findViewById(R.id.purpleline);
            line.setVisibility(View.VISIBLE);
            line.setBackgroundColor(ContextCompat.getColor(_context, R.color.gray_button_text_color));
        }
        if (childPosition == getChildrenCount(groupPosition) - 1 && getChildrenCount(groupPosition) > 2) {
            convertView = infalInflater.inflate(R.layout.expandable_card_item_footer, null);
            Button footerButton = (Button) convertView.findViewById(R.id.expandable_card_item_footer_layout_white_btn);
            if (!balanceCardHeader.shouldHideFooter()) {
                footerButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (VodafoneController.getInstance().getUser() instanceof PrepaidUser) {
                            new NavigationAction(_context).startAction(IntentActionName.CALL_DETAILS_PREPAID);
                        } else {
                            new NavigationAction(_context).startAction(IntentActionName.CALL_DETAILS_UNBILLED);
                        }
                    }
                });
            } else {
                footerButton.setVisibility(View.INVISIBLE);
            }

            if (balanceCardHeader.showSecondaryFooterButton()) {
                Button grayFooterButton = convertView.findViewById(R.id.expandable_card_item_footer_layout_gray_button);
                grayFooterButton.setVisibility(View.VISIBLE);
                grayFooterButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new NavigationAction(_context).startAction(IntentActionName.BILLING_OVERVIEW);
                    }
                });
            }
        }
        if (childPosition == 0 && getChildrenCount(groupPosition) > 2) {
            convertView = infalInflater.inflate(R.layout.expandable_card_item_header, null);


            TextView textIcon = (TextView) convertView.findViewById(R.id.textIcon);
            textIcon.setText(balanceCardHeader.getTextMiddle());

            setHeaderTitleText(convertView, balanceCardHeader.getTextTitle());

        }
        if (childPosition == getChildrenCount(groupPosition) - 2 && getChildrenCount(groupPosition) > 2) {

            BalanceCardDetail cardDetail = (BalanceCardDetail) getChild(groupPosition, childPosition - 1);
            convertView = infalInflater.inflate(R.layout.expandable_card_item, null);

            TextView textLeft = (TextView) convertView.findViewById(R.id.textLeft);
            textLeft.setText(cardDetail.getLeftText());

            TextView textRight = (TextView) convertView.findViewById(R.id.textRight);
            textRight.setText(cardDetail.getRightText());

            ImageView icon = (ImageView) convertView.findViewById(R.id.image);
            icon.setBackgroundResource(cardDetail.getIcon());

            if (!balanceCardHeader.shouldHideFooter()) {
                setClickListner(icon, cardDetail.getCategory());
            }

            View line = (View) convertView.findViewById(R.id.purpleline);
            line.setBackgroundColor(_context.getResources().getColor(R.color.gray_button_text_color));

            ViewGroup.LayoutParams layoutParams = line.getLayoutParams();
            layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, _context.getResources().getDisplayMetrics());

            line.setLayoutParams(layoutParams);

        }

        return convertView;
    }

    private void setHeaderTitleText(View convertView, String text) {
        if (convertView != null) {
            TextView titleView = (TextView) convertView.findViewById(R.id.textTop);
            titleView.setText(text);
        }
    }


    @Override
    public int getChildrenCount(int groupPosition) {

        if (this._listDataChild.get(this._listDataHeader.get(groupPosition)) != null && this._listDataChild.get(this._listDataHeader.get(groupPosition)).size() != 0) {

            return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                    .size() + 2;
        }
        return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return _listDataHeader != null ? this._listDataHeader.size() : 0;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, final boolean isExpanded,
                             View convertView, ViewGroup parent) {

        final RotateAnimation animRotateUp = new RotateAnimation(0.0f, 0f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        animRotateUp.setAnimationListener(animationListener);
        animRotateUp.setFillAfter(true);
        animRotateUp.setDuration(120);

        final RotateAnimation animRotateDown = new RotateAnimation(0, 180f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        animRotateDown.setAnimationListener(animationListener);
        animRotateDown.setFillAfter(true);
        animRotateDown.setDuration(120);

        final RotateAnimation animRotateRight = new RotateAnimation(0.0f, -90.0f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);

        animRotateRight.setFillAfter(true);
        animRotateRight.setDuration(120);

        BalanceCardHeader headerTitle = (BalanceCardHeader) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.expandable_card_view, null);
        }

        DynamicColorImageView currentarrow = (DynamicColorImageView) convertView.findViewById(R.id.arrow_indicator);
        if (getChildrenCount(groupPosition) != 0) {
            currentarrow.setAnimation(animRotateUp);

            arrowToAnimated.put(groupPosition, currentarrow);
        } else {
            currentarrow.setImageDrawable(ContextCompat.getDrawable(_context, R.drawable.chevron_right_48));
            currentarrow.setDrawableColor(R.color.white);
        }

        if (!headerTitle.isArrowVisible()) {
            currentarrow.setVisibility(View.GONE);
        }
        setHighText(convertView, headerTitle, headerTitle.showJustHighText());
        setMiddleText(convertView, headerTitle);
        setLowText(convertView, headerTitle);

        return convertView;
    }

    private void setHighText(View convertView, BalanceCardHeader headerTitle) {
        TextView textHigh = (TextView) convertView.findViewById(R.id.highTextView);
        textHigh.setText(headerTitle.getTextHigh());
        hideTextIfNullOrEmpty(textHigh, headerTitle.getTextHigh());
    }

    private void setHighText(View convertView, BalanceCardHeader headerTitle, boolean centerVertical) {
        TextView textHigh = (TextView) convertView.findViewById(R.id.highTextView);
        textHigh.setText(headerTitle.getTextHigh());
        if (centerVertical) {
            RelativeLayout.LayoutParams textHighLayoutParams = (RelativeLayout.LayoutParams) textHigh.getLayoutParams();
            textHighLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
            textHigh.setLayoutParams(textHighLayoutParams);
        }

        hideTextIfNullOrEmpty(textHigh, headerTitle.getTextHigh());
    }

    private void setMiddleText(View convertView, BalanceCardHeader headerTitle) {
        TextView textMiddle = (TextView) convertView.findViewById(R.id.priceTextView);
        textMiddle.setText(headerTitle.getTextMiddle());
        hideTextIfNullOrEmpty(textMiddle, headerTitle.getTextMiddle());

    }

    private void setLowText(View convertView, BalanceCardHeader headerTitle) {
        TextView textLow = (TextView) convertView.findViewById(R.id.lowTextView);
        textLow.setText(headerTitle.getTextLow());
        hideTextIfNullOrEmpty(textLow, headerTitle.getTextLow());

    }

    private void hideTextIfNullOrEmpty(TextView textView, String text) {
        if (textView != null) {
            if (text == null || text.isEmpty()) {
                textView.setVisibility(View.GONE);
            } else {
                textView.setVisibility(View.VISIBLE);
            }
        }
    }

    private void hideTextIfNullOrEmpty(TextView textView, SpannableString text) {
        if (textView != null) {
            if (text == null || text.toString().isEmpty()) {
                textView.setVisibility(View.GONE);
            } else {
                textView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }


    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);
    }


    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);

        View view = arrowToAnimated.get(groupPosition);
        if (view == null) {
            return;
        }
        ObjectAnimator floatingButtonAnimator = ObjectAnimator.ofFloat(view,
                "rotation", 0, 180);

        floatingButtonAnimator.setDuration(120);

        floatingButtonAnimator.start();
        if (onGroupExpandedListener != null) {
            onGroupExpandedListener.OnExpanded(groupPosition, view);
        }
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);

        View view = arrowToAnimated.get(groupPosition);

        if (view == null) {
            return;
        }
        ObjectAnimator floatingButtonAnimator = ObjectAnimator.ofFloat(view,
                "rotation", 180, 0f);
        floatingButtonAnimator.setDuration(120);
        floatingButtonAnimator.start();
        if (onGroupExpandedListener != null) {
            onGroupExpandedListener.OnCollapse(groupPosition, view);
        }
    }

    Animation.AnimationListener animationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            isAnimating = false;
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    public interface OnGroupExpandedListener {
        void OnExpanded(int groupPosition, View arrowView);

        void OnCollapse(int groupPosition, View arrowView);
    }

    private void setClickListner(ImageView imageView, Category category) {
        Log.d("ExpandableListAdapter", "setClickListner()");
        switch (category) {
            case DATE:
                imageView.setOnClickListener(dateSectionListner);
                break;
            case VOCE:
                imageView.setOnClickListener(voiceSectionListner);
                break;
            case SMS:
                imageView.setOnClickListener(smsSectionListner);
                break;
            case OTHER:
                Log.d("ExpandableListAdapter", "set other click listne");
                imageView.setOnClickListener(otherSectionListner);
                break;
        }
    }

    private View.OnClickListener voiceSectionListner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            navigateToCallDetails(Category.VOCE);
        }
    };

    private View.OnClickListener dateSectionListner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            navigateToCallDetails(Category.DATE);
        }
    };

    private View.OnClickListener smsSectionListner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            navigateToCallDetails(Category.SMS);
        }
    };

    private View.OnClickListener otherSectionListner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            navigateToCallDetails(Category.OTHER);
        }
    };

    private void navigateToCallDetails(Category category) {
        Log.d("ExpandableListAdapter", "navigateToCallDetails");

        IntentActionName i;
        Class c;

        if (VodafoneController.getInstance().getUser() instanceof PrepaidUser) {
            i = IntentActionName.CALL_DETAILS_PREPAID;
            c = IntentActionName.CALL_DETAILS_PREPAID.getClass();
        } else {
            i = IntentActionName.CALL_DETAILS_UNBILLED;
            c = IntentActionName.CALL_DETAILS_UNBILLED.getClass();
        }

        try {
            Method method;
            method = c.getMethod("setCategory", Category.class);
            method.invoke(i, category);
            new NavigationAction(_context).startAction(i);

        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }

    }

}
