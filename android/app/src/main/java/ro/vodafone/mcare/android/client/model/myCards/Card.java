package ro.vodafone.mcare.android.client.model.myCards;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.annotations.PrimaryKey;

public class Card implements Serializable {
    @SerializedName("cardExpirationDate")
    private Long cardExpirationDate;
    @SerializedName("cardType")
    private String cardType;
    @SerializedName("cardNumberMasked")
    private String cardNumberMasked;
    @PrimaryKey
    @NonNull
    @SerializedName("token")
    private String token;

    public Long getCardExpirationDate() {
        return cardExpirationDate;
    }

    public void setCardExpirationDate(Long cardExpirationDate) {
        this.cardExpirationDate = cardExpirationDate;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardNumberMasked() {
        return cardNumberMasked;
    }

    public void setCardNumberMasked(String cardNumberMasked) {
        this.cardNumberMasked = cardNumberMasked;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
