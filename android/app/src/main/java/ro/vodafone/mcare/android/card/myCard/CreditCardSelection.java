package ro.vodafone.mcare.android.card.myCard;

import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.client.model.myCards.Card;

public class CreditCardSelection extends CreditCard {
    @BindView(R.id.radio_button)
    RadioButton radioButton;
    private Card card;
    @BindView(R.id.root)
    RelativeLayout root;
    public CreditCardSelection(Context context) {
        super(context);
        root.setBackground(ResourcesCompat.getDrawable(getResources(), R.color.white, null));
        getCardView().setBackground(ResourcesCompat.getDrawable(getResources(), R.color.white, null));
        radioButton.setVisibility(VISIBLE);
        getDeleteText().setVisibility(GONE);
        getExpirationDate().setTextSize(14);
    }

    public CreditCardSelection selectCard(boolean flag){
        radioButton.setChecked(flag);
        if(flag){

            root.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.turqoise_border_with_gray_background, null));
        }
        else{
            root.setBackground(ResourcesCompat.getDrawable(getResources(), R.color.white, null));
        }
        return this;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
        setCreditCardExpirationDate(new SimpleDateFormat("MMMM yyyy",new Locale("ro","RO"))
                .format(new Date(card.getCardExpirationDate())));
        setCreditCardNumber(card.getCardType() + " " + card.getCardNumberMasked());
        setCardTypeImage(card.getCardType());
    }
}
