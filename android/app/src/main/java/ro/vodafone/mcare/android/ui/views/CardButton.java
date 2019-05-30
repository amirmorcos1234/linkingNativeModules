package ro.vodafone.mcare.android.ui.views;

import android.content.Context;
import android.graphics.Color;
import android.widget.ImageView;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.client.model.card.CardButtonModel;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;

/**
 * Created by user on 22.03.2017.
 */
public class CardButton extends LinearLayout {

    private Context mContext;

    @BindView(R.id.button_label)
    VodafoneTextView buttonLabel;

    @BindView(R.id.right_arrow)
    ImageView arrow;


    public CardButton(Context context) {
        super(context);
        mContext = context;
        init();
    }

    private void init(){
        inflate(mContext, R.layout.card_button, this);
        ButterKnife.bind(this);
    }

    public CardButton buildButton(CardButtonModel cardButtonModel){

        this.buttonLabel.setText(cardButtonModel.getButton_name());
        this.setOnClickListener(cardButtonModel.getButton_onClickListner());

        /*
        If arrow drawable from object is null then show default drawable
        */
        if(cardButtonModel.getButton_arrow() != null){
            arrow.setVisibility(VISIBLE);
            arrow.setImageDrawable(cardButtonModel.getButton_arrow());
        }else{
            arrow.setColorFilter(Color.parseColor("#E60000"));
        }

        return this;
    }
}
