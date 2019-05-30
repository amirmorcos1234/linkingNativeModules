package ro.vodafone.mcare.android.ui.views;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.client.model.card.CardHeaderModel;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;

/**
 * Created by Bivol Pavel on 22.03.2017.
 */

public class CardHeader extends RelativeLayout {

    @BindView(R.id.header_icon)
    ImageView header_icon;
    @BindView(R.id.header_title)
    VodafoneTextView header_title;
    @BindView(R.id.header_subTitle)
    VodafoneTextView header_subTitle;
    @BindView(R.id.header_category)
    VodafoneTextView header_category;
    @BindView(R.id.header_bottom_category)
    VodafoneTextView header_bottom_category;
    @BindView(R.id.header_extraParameter)
    VodafoneTextView header_extraParameter;
    private Context mContext;

    public CardHeader(Context context) {
        super(context);
        mContext = context;
        initHeader();
    }

    private void initHeader() {
        inflate(mContext, R.layout.card_header, this);
        ButterKnife.bind(this);
    }

    public CardHeader buildHeader(CardHeaderModel cardHeaderModel) {


        if (cardHeaderModel.getHeader_title() != null && !cardHeaderModel.getHeader_title().equals("")) {
            header_title.setVisibility(VISIBLE);
            header_title.setText(cardHeaderModel.getHeader_title());
        }

        if (cardHeaderModel.getHeader_icon() != null || cardHeaderModel.getHeaderIconPlaceholder() != null) {
            header_icon.setVisibility(VISIBLE);
            try {
                Glide.with(mContext)
                        .load(cardHeaderModel.getHeader_icon())
                        .placeholder(cardHeaderModel.getHeaderIconPlaceholder())
                        //.error(drawableResId)
                        .into(header_icon);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (cardHeaderModel.getHeader_subTitle() != null && !cardHeaderModel.getHeader_subTitle().equals("")) {
            header_subTitle.setVisibility(VISIBLE);
            header_subTitle.setText(cardHeaderModel.getHeader_subTitle());
        }

        if (cardHeaderModel.getHeader_category() != null && !cardHeaderModel.getHeader_category().equals("")) {
            header_category.setVisibility(VISIBLE);
            header_category.setText(cardHeaderModel.getHeader_category());
        }

        if (cardHeaderModel.getHeader_bottom_category() != null && !cardHeaderModel.getHeader_bottom_category().equals("")) {
            header_bottom_category.setVisibility(VISIBLE);
            header_bottom_category.setText(cardHeaderModel.getHeader_bottom_category());
        }

        if (cardHeaderModel.getHeader_extraParameter() != null && !cardHeaderModel.getHeader_extraParameter().equals("")) {
            header_extraParameter.setVisibility(VISIBLE);
            header_extraParameter.setText(cardHeaderModel.getHeader_extraParameter());
        }

        //Todo - Insetead this condition send null ass paramter for category
        /*if (!(VodafoneController.getInstance().getUser() instanceof PostPaidUser))
            header_category.setVisibility(GONE);*/

        return this;
    }

    public CardHeader setHeaderTitleVisibility(int visibility){
        header_title.setVisibility(visibility);
        return this;
    }

}
