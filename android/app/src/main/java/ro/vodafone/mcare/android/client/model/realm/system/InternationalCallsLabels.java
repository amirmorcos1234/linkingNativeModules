package ro.vodafone.mcare.android.client.model.realm.system;

public class InternationalCallsLabels extends AppLabels {

    public static String internationalCallsCardTitle() {
        return getLabelWithPrimaryKey("international_calls_card_title","Apeluri Internaţionale");
    }

    public static String yourAccountCardTitle() {
        return getLabelWithPrimaryKey("international_calls_your_account_title","Contul tău");
    }

    public static String yourAccountCardSubtitle() {
        return getLabelWithPrimaryKey("international_calls_your_account_subtitle","Vezi tarifele internaţionale pentru numărul tău de telefon.");
    }

    public static String otherMsisdnCardTitle() {
        return getLabelWithPrimaryKey("international_calls_other_msisdn_title","Alt număr");
    }

    public static String otherMsisdnCardSubtitle() {
        return getLabelWithPrimaryKey("international_calls_other_msisdn_subtitle","Vezi tarifele internaţionale pentru alt număr de telefon.");
    }

    public static String recipientNumber() {
        return getLabelWithPrimaryKey("international_calls_recipient_number","Număr destinatar");
    }

    public static String recipient() {
        return getLabelWithPrimaryKey("international_calls_recipient","Destinatar");
    }

    public static String callerNumber() {
        return getLabelWithPrimaryKey("international_calls_caller_number","Număr apelant");
    }

    public static String showFee() {
        return getLabelWithPrimaryKey("international_calls_show_fee","Afișează tarif");
    }

    public static String enterSmsToken() {
        return getLabelWithPrimaryKey("international_calls_enter_sms_token","Te rugăm să introduci tokenul primit prin SMS.");
    }

    public static String invalidSmsToken() {
        return getLabelWithPrimaryKey("international_calls_invalid_sms_token","Te rugăm să introduci un SMS cod valid.");
    }

    public static String smsCode() {
        return getLabelWithPrimaryKey("international_calls_sms_code","SMS Cod");
    }

    public static String resendToken() {
        return getLabelWithPrimaryKey("international_calls_resend_token","Retrimite token");
    }

    public static String getFee() {
        return getLabelWithPrimaryKey("international_calls_fee","Tarif");
    }

    public static String getMinute() {
        return getLabelWithPrimaryKey("international_calls_minute","minut");
    }

    public static String feeExplanation() {
        return getLabelWithPrimaryKey("international_calls_fee_explanation","Este posibil ca împărţirea pe zone și tarifele mai sus menţionate să nu fie aplicabile în cazul în care numărul internaţional apelat a fost portat în altă reţea.");
    }

    public static String notInVodafone() {
        return getLabelWithPrimaryKey("international_calls_not_in_vodafone","Numărul introdus nu este în reţeaua Vodafone.");
    }

    public static String smsSent() {
        return getLabelWithPrimaryKey("international_calls_sms_sent","Codul de activare a fost trimis prin SMS.");
    }

    public static String smsStillValid() {
        return getLabelWithPrimaryKey("international_calls_sms_still_valid","Codul de activare primit anterior pentru acest număr de telefon este încă valid.");
    }

    public static String invalidPrefixTooltip() {
        return getLabelWithPrimaryKey("international_calls_invalid_prefix","Te rugăm să introduci un număr ce începe cu 00.");
    }

    public static String invalidNumberTooltip() {
        return getLabelWithPrimaryKey("international_calls_invalid_number","Te rugăm să introduci un număr de telefon valid.");
    }
}
