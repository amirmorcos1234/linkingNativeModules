package ro.vodafone.mcare.android.client.model.myCards;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class CardsResponse {

    @SerializedName("cards")
    private List<Card> cardList;


    @SerializedName("maxReached")
    private Boolean maxReached;

    public CardsResponse() {
        cardList = new ArrayList<>();
    }

    public List<Card> getCardList() {
        return cardList;
    }

    public void setCardList(List<Card> cardList) {
        this.cardList = cardList;
    }


    public Boolean getMaxReached() {
        return maxReached;
    }

    public void setMaxReached(Boolean maxReached) {
        this.maxReached = maxReached;
    }
}
