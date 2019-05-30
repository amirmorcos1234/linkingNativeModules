package ro.vodafone.mcare.android.card.settings;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.card.VodafoneAbstractCard;
import ro.vodafone.mcare.android.ui.utils.TextUtils;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;

/**
 * Created by user2 on 5/9/2017.
 */

public class PrivacyPolicyCard extends VodafoneAbstractCard {

    Context mContext;

    public PrivacyPolicyCard(Context context) {
        super(context);
        mContext = context;
        init(null);
    }

    public PrivacyPolicyCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(attrs);
    }

    public PrivacyPolicyCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init(attrs);
    }

    private void init(AttributeSet attributeSet) {
        ButterKnife.bind(this);
        //setStartPadding(0);
        setAttributes(attributeSet);
    }

    private void setAttributes(AttributeSet attributeSet) {
    }


    @Override
    protected int setContent() {
        return R.layout.card_privacy_policy;
    }


    public PrivacyPolicyCard addExpandableTextGroup(boolean isLast, String title, ArrayList<String> content, ArrayList<String> contentType, List<String> bulletListTexts) {
        View extraView = LayoutInflater.from(getContext()).inflate(R.layout.expandable_simple_text, null);

        LinearLayout containerLinearLayout = (LinearLayout) extraView.findViewById(R.id.container);

        VodafoneTextView vodafoneTitleView = (VodafoneTextView) extraView.findViewById(R.id.expandable_simple_title);
        vodafoneTitleView.setTextColor(ContextCompat.getColor(mContext, R.color.black_opacity_90));
        vodafoneTitleView.setText(title);

        final ImageView expandIcon = (ImageView) extraView.findViewById(R.id.expand_icon);
        final LinearLayout toBeShown = (LinearLayout) extraView.findViewById(R.id.toBeShown);
        View separatorLine = extraView.findViewById(R.id.separator_view);

        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);


        for (int i = 0 ; i < contentType.size(); i++) {
            String type = contentType.get(i);
            if (type.equals("title")) {
                llp.setMargins(0, 15, 0, 8); // llp.setMargins(left, top, right, bottom);

                VodafoneTextView expandedtitle = new VodafoneTextView(mContext);
                expandedtitle.setTypeface(null, Typeface.BOLD);
                expandedtitle.setText(content.get(i));
                expandedtitle.setLayoutParams(llp);
                toBeShown.addView(expandedtitle);
            } else if (type.equals("simple_text")) {
                llp.setMargins(0, 8, 0, 8); // llp.setMargins(left, top, right, bottom);

                VodafoneTextView expandedSimple = new VodafoneTextView(mContext);
                expandedSimple.setText(content.get(i));
                expandedSimple.setLayoutParams(llp);
                toBeShown.addView(expandedSimple);
            } else if (type.equals("list")) {
                llp.setMargins(0, 8, 0, 8); // llp.setMargins(left, top, right, bottom);

                for (int j = 0; j < bulletListTexts.size(); j++) {
                    VodafoneTextView expandedListItem = new VodafoneTextView(mContext);
                    Spanned bullet = TextUtils.fromHtml("&#8226;");
                    String textWithBullet = bullet + "  " + bulletListTexts.get(j);
                    expandedListItem.setText(textWithBullet);
                    expandedListItem.setLayoutParams(llp);
                    toBeShown.addView(expandedListItem);
                }
            }
        }


        if (isLast) {
            separatorLine.setVisibility(GONE);
        }

        expandIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toBeShown.getVisibility() == View.GONE) {
                    toBeShown.setVisibility(View.VISIBLE);
                    expandIcon.setImageResource(R.drawable.right_red_arrow_up);
                } else {
                    toBeShown.setVisibility(View.GONE);
                    expandIcon.setImageResource(R.drawable.right_red_arrow_down);
                }
            }
        });

        vodafoneTitleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toBeShown.getVisibility() == View.GONE) {
                    toBeShown.setVisibility(View.VISIBLE);
                    expandIcon.setImageResource(R.drawable.right_red_arrow_up);
                } else {
                    toBeShown.setVisibility(View.GONE);
                    expandIcon.setImageResource(R.drawable.right_red_arrow_down);
                }
            }
        });


        getContentGroupView().addView(extraView);
        return this;
    }

    private LinearLayout getContentGroupView() {
        return (LinearLayout) findViewById(R.id.privacy_policy_card_layout);
    }

    public void hideLoading() {
        super.hideLoading();
    }

    public void showError() {
        clearContent();
        super.showError(true);
    }

    @Override
    public PrivacyPolicyCard showLoading(boolean hideContent) {
        clearContent();
        super.showLoading(true);
        return this;
    }
}