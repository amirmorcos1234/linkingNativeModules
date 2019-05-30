package ro.vodafone.mcare.android.client.model.realm.system;

/**
 * Created by user1 on 7/28/2017.
 */

public class LoginLabels extends AppLabels {

    public static String getLoginForgotLabel(){
        return getLabelWithPrimaryKey("login_forgot_label" ,"Ai uitat parola sau numele de utilizator?");
    }

    public static String getForgotPasswordText(){
        return getLabelWithPrimaryKey("forgotPasswordText","<u>parola</u>\\u00A0");
    }

    public static String getForgotUsernameText(){
        return getLabelWithPrimaryKey("forgotUsernameText","<u>nume de utilizator</u>\\u00A0?");
    }

    public static String getOrLabel(){
        return getLabelWithPrimaryKey("orLabel","sau\\u00A0");
    }

    public static String getLoginRememberMe (){
        return getLabelWithPrimaryKey("rememberMe" ,"Păstrează-mă autentificat");
    }

    public static String getLoginToMyVodafone(){
        return getLabelWithPrimaryKey("loginToMyVodafone","Autentifică-te în \nMy Vodafone");
    }

    public static String getLoginUserNameInputLabel(){
        return getLabelWithPrimaryKey("userNameInputLabel","Nume utilizator");
    }

    public static String getLoginUserNameInputHint(){
        return getLabelWithPrimaryKey("userNameInputHint","nume utilizator");
    }

    public static String getPasswordInputLabel(){
        return getLabelWithPrimaryKey("passwordInputLabel","Parola");
    }

    public static String getPasswordInputHint(){
        return getLabelWithPrimaryKey("passwordInputHint","parola");
    }

    public static String getLoginButtonLabel(){
        return getLabelWithPrimaryKey("loginButtonLabel","Autentificare");
    }

    public static String getLoginRegisterButton (){
        return getLabelWithPrimaryKey("registerNowButtonLabel","înregistrează-te acum");
    }

    public static String getResetAccountButtonLabel(){
        return getLabelWithPrimaryKey("reset_account_button_label","Reset your account now");
    }

    public static String getNotRegisteredLabel(){
        return getLabelWithPrimaryKey("notRegisteredLabel","Nu ești încă înregistrat cu \\nMy Vodafone?");
    }

    public static String getInvalidPasswordMessage(){
        return getLabelWithPrimaryKey("invalid_password_message","Ai introdus o parolă greșită.");
    }

    public static String getInvalidUsernameMessage(){
        return getLabelWithPrimaryKey("invalid_username_message","Numele de utilizator este incorect.");
    }

    public static String getLoginFailedApiCall(){
        return getLabelWithPrimaryKey("login_failed_api_call","Serviciu momentan indisponibil!");
    }

    public static String getLoginFailedApiCall2(){
        return getLabelWithPrimaryKey("login_failed_api_call","Autentificare nereusită");
    }

    public static String getSeamlessLoginFailedApiCall(){
        return getLabelWithPrimaryKey("seamless_login_failed_api_call","Conectarea rapidă a eșuat.");
    }

    public static String getFaqs(){
        return getLabelWithPrimaryKey("faqs","FAQs");
    }

    public static String getInternetLeftSubTitle(){
        return getLabelWithPrimaryKey("internetLeftSubTitle","Adaugă una dintre cele două opțiuni pentru a beneficia în continuare de cel mai rapid Internet.");
    }

    public static String getInternetLeft(){
        return getLabelWithPrimaryKey("internetLeft","Ți-au rămas puține date și mai ai 9 zile până la reînnoirea abonamentului.");
    }

    public static String getChat(){
        return getLabelWithPrimaryKey("chat","Chat");
    }

    public static String getRequesterId(){
        return getLabelWithPrimaryKey("requesterId","mcare");
    }

    public static String getRequesterPassword(){
        return getLabelWithPrimaryKey("requesterPassword","mcare");
    }

    public static String getTitle(){
        return getLabelWithPrimaryKey("title","Bine ai venit în MyVodafone!");
    }

    public static String getPayBill(){
        return getLabelWithPrimaryKey("paybill","Plătește factura");
    }

    public static String getClientCode(){
        return getLabelWithPrimaryKey("clientcode","Cod client:");
    }

    public static String getRechargeList(){
        return getLabelWithPrimaryKey("rechargeList","Reîncarcă");
    }

    public static String getAnuleaza(){
        return getLabelWithPrimaryKey("anuleaza","Anulează");
    }

    public static String getCostControl(){
        return getLabelWithPrimaryKey("costcontrol","Cost Control");
    }

    public static String getEmail(){
        return getLabelWithPrimaryKey("email","Email");
    }

    public static String getYesLabel(){
        return getLabelWithPrimaryKey("da","Da");
    }

    public static String getRecharge(){
        return getLabelWithPrimaryKey("recharge","Reîncarcă cartela");
    }
}
