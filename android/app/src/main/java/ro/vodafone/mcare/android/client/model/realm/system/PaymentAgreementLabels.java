package ro.vodafone.mcare.android.client.model.realm.system;

/**
 * Created by user1 on 10/26/2017.
 */

public class PaymentAgreementLabels extends AppLabels {

    public static String getPaymentAgreementTitle() {
        return getLabelWithPrimaryKey("settings_payment_agreement_title", "Amânare de plată");
    }

    public static String getPaymentAgreementPayBillNow() {
        return getLabelWithPrimaryKey("settings_payment_agreement_pay_bill_now", "Plătește factura");
    }

    public static String getPaymentAgreementUserNotEligible() {
        return getLabelWithPrimaryKey("settings_payment_agreement_user_not_eligible", "Ne pare rău, nu sunteți eligibil pentru amânare plată! Te rugăm să contactezi serviciul clienți la *222.");
    }

    public static String getPaymentAgreementErrorCouldntPostpone() {
        return getLabelWithPrimaryKey("settings_payment_agreement_error_postpone", "Ne pare rău, data scadentă a plății nu a putut fi amânată. Pentru suport, te rugăm să contactezi serviciul relații cu clienții la *222.");
    }

    public static String getPaymentAgreementNotEligibleMonth() {
        return getLabelWithPrimaryKey("settings_payment_agreement_not_eligible_this_month", "Ne pare rău, nu ești eligibil de amânare plată.");
    }

    public static String getPaymentAgreementBillExpired() {
        return getLabelWithPrimaryKey("settings_payment_agreement_bill_expired", "Scadența facturii tale a expirat și nu se mai poate înregistra o amânare de plată.");
    }

    public static String getPaymentAgreementAlreadyPostponed() {
        return getLabelWithPrimaryKey("settings_payment_agreement_already_postponed", "Data scadentă a plății a fost deja amânată.");
    }

    public static String getPaymentAgreementSelectDate() {
        return getLabelWithPrimaryKey("settings_payment_agreement_select_date", "Selectează data");
    }

    public static String getPaymentAgreementNoDaysPostpone() {
        return getLabelWithPrimaryKey("settings_payment_agreement_no_days_postpone", "Poți amâna data scadentă a facturii cu până la ");
    }

    public static String getPaymentAgreementOverlayDetails() {
        return getLabelWithPrimaryKey("settings_payment_agreement_overlay_details", "Dorești amânare de plată la data selectată?");
    }

    public static String getPaymentAgreementVovMessage() {
        return getLabelWithPrimaryKey("payment_agreement_vov_message", "Ai amânat plata până pe data de ");
    }

    public static String getPaymentAgreementVovMessageEmailSent(){
        return getLabelWithPrimaryKey("payment_agreement_vov_message_email_sent","Solicitarea a fost trimisă către specialistul de plăți. Te vom contacta în maxim 2 zile lucrătoare.");
    }

    public static String getPaymentAgreementToastMessage(){
        return getLabelWithPrimaryKey("payment_agreement_toast_message", "Solicitare trimisă!");
    }

    public static String getPaymentAgreementOnErrorToastMessage(){
        return getLabelWithPrimaryKey("payment_agreement_on_error_toast_message","Ne pare rău, solicitarea nu a fost trimisă către specialistul de plăţi. Pentru suport, te rugăm să contactezi serviciul de relaţii cu clienţii la numarul *222.");
    }

    public static String getPaymentAgreementInformativeMessageCard(){
        return getLabelWithPrimaryKey("payment_agreement_informative_message_card","Trimite o solicitare către specialistul de plăţi.\nTe vom contacta în maximum 2 zile lucrătoare");
    }

    public static String getPaymentAgreementBillDelayMessage(){
        return getLabelWithPrimaryKey("payment_agreemnt_bill_delay_message","Pentru amânare de plată, vă rugăm să contactaţi specialistul dvs de plăţi, program de lucru L-V, 09:00-18:00.");
    }

}
