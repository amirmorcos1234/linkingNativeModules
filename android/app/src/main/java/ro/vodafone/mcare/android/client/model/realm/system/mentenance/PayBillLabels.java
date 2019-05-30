package ro.vodafone.mcare.android.client.model.realm.system.mentenance;

import ro.vodafone.mcare.android.client.model.realm.system.AppLabels;

/**
 * Created by user1 on 7/20/2017.
 */

public class PayBillLabels extends AppLabels {

    public static String getPayBillTitle() {
        return getLabelWithPrimaryKey("pay_bill_title","Plată Factură");
    }

    public static String getPayOwnBillButtonDescription(){
        return  getLabelWithPrimaryKey("pay_own_bill_button_description","Plătește factura ta cu cardul");
    }

    public static String getPayAnotherBillButtonDescription(){
        return getLabelWithPrimaryKey("pay_another_bill_button_description","Plătește o altă factură cu cardul");
    }

    public static String getPayBillAmount(){
        return getLabelWithPrimaryKey("pay_bill_amount","Total de plată");
    }

    public static String getBillTva(){
        return getLabelWithPrimaryKey("pay_bill_tva","(TVA inclus)");
    }

    public static String getPayBillClientCode(){
        return getLabelWithPrimaryKey("pay_bill_client_code","Cont Client");
    }

    public static String getPayBillNumber(){
        return getLabelWithPrimaryKey("pay_bill_number","Număr factură");
    }

    public static String getPayBillDueDate(){
        return getLabelWithPrimaryKey("pay_bill_due_date","Scadență factură");
    }

    public static String getPayBillSecurely(){
        return getLabelWithPrimaryKey("pay_bill_securely","Scadență factură");
    }

    public static String getPayBillDoYouWantPayForAnother(){
        return getLabelWithPrimaryKey("pay_bill_do_you_want_pay_for_another","Dorești să plătești factura altcuiva?");
    }

    public static String getPayBillDoYouWantPayYourBill(){
   return getLabelWithPrimaryKey("pay_bill_do_you_want_pay_your_bill","Dorești să plătești factura ta?");
    }

    public static String getPayBillSeries(){
    return getLabelWithPrimaryKey("pay_bill_series","VFRO");
    }

    public static String getPayBillButton(){
        return getLabelWithPrimaryKey("pay_bill_button","Plătește factura");
    }

    public static String getPayBillTelephoneNumber(){
        return  getLabelWithPrimaryKey("pay_bill_telephone_number","Număr de telefon");
    }

    public static String getPayBillTelephoneNumberHint(){
        return getLabelWithPrimaryKey("pay_bill_telephone_number_hint","07XXXXXXXX");
    }

    public static String getPayBillEmail(){
        return getLabelWithPrimaryKey("pay_bill_email","Adresă e-mail");
    }

    public static String getPayBillEmailHint(){
        return getLabelWithPrimaryKey("pay_bill_email_hint","nume@exemplu.com");
    }

    public static String getPayBillInvoiceValue(){
        return getLabelWithPrimaryKey("pay_bill_invoice_value","Valoarea facturii (RON)");
    }

    public static String getPayBillInvoiceValueHint(){
        return getLabelWithPrimaryKey("pay_bill_invoice_value_hint","0");
    }

    public static String getPayBillAllBillIsPaid(){
        return getLabelWithPrimaryKey("pay_bill_all_bill_is_paid","Nu ai facturi de plătit. Mulțumim!");
    }

    public static String getPayBillNoBillIssued(){
        return  getLabelWithPrimaryKey("pay_bill_no_bill_issued","Nu ai facturi emise. Mulțumim!");
    }

    public static String getPayBillAfterDueDateMessage(){
        return getLabelWithPrimaryKey("pay_bill_after_due_date_message","Plătește factura la timp pentru a evita suspendarea abonamentului tău.");
    }

    public static String getPayBillNextInvoiceWillBeIssuedOn(){
        return getLabelWithPrimaryKey("pay_bill_next_invoice_will_be_issued_on","Următoarea factură va fi emisă pe");
    }

    public static String getPayBillFailedApiCall(){
        return getLabelWithPrimaryKey("pay_bill_failed_api_call","Serviciu momentan indisponibil!");
    }

    public static String getPayAnotherBillButton(){
        return getLabelWithPrimaryKey("pay_another_bill_button","Plătește factura altcuiva");
    }

    public static String getPayOwnBillButton(){
        return getLabelWithPrimaryKey("pay_own_bill_button","Plătește factura ta");
    }

    public static String getPayBillInvalidPhoneNumber(){
        return getLabelWithPrimaryKey("pay_bill_invalid_phone_number","Te rugăm să introduci un număr de telefon valid.");
    }

    public static String getPayBillInvalidEmail(){
        return getLabelWithPrimaryKey("pay_bill_invalid_email","Te rugăm să introduci o adresă de e-mail validă");
    }

    public static String getPayBillInvalidInvoiceValue(){
        return getLabelWithPrimaryKey("pay_bill_invalid_invoice_value","Te rugăm să introduci o valoare mai mare sau egală cu 1.");
    }

    public static String getPayBillMsisdnNotRelatedToGsmSubscription(){
        return getLabelWithPrimaryKey("pay_bill_msisdn_not_related_to_gsm_subscription","Numărul de telefon introdus nu corespunde unui abonament Vodafone.");
    }

    public static String getPayBillInvoiceNotAvailable(){
        return getLabelWithPrimaryKey("pay_bill_invoice_not_available","Factura nu este momentan disponibilă.");
    }

    public static String getPayBillInvoiceAlreadyPaid(){
        return getLabelWithPrimaryKey("pay_bill_invoice_already_paid","Factura a fost deja achitată.");
    }

    public static String getNetopiaWebViewTittle(){
        return getLabelWithPrimaryKey("netopia_web_view_tittle","Plată Factură");
    }

    public static String getServiceSelectorTittle(){
        return getLabelWithPrimaryKey("service_selector_tittle","Selectează un serviciu");
    }

    public static String getPayOwnBillAnonymouslyMessage(){
        return getLabelWithPrimaryKey("pay_own_bill_anonymously_message", "Nu am reuşit să încărcăm datele facturii. Pentru a putea plăti manual introdu adresa de email și valoarea facturii în câmpurile de mai jos.");
    }

    public static String getPayBillDistributionOnMultipleAccountsError(){
        return getLabelWithPrimaryKey("distribution_on_multiple_accounts_error", "Factura nu este momentan disponibilă.");
    }

    // in case this text becomes configurable
    public static String getPayBillSafe() {
        return getLabelWithPrimaryKey("pay_bill_safe", "Plătește în siguranță");
    }

}
