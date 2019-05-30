package ro.vodafone.mcare.android.constants;

public enum ErrorCodes {


    //GENERIC ERRORS
    INTERNAL_ERROR(Constants.EC, 99, "Internal Error"),

    //API-1
    API1_MISSING_USERNAME(Constants.EC, 101, "Field validation - Username blank"),
    API1_MISSING_PASSWORD(Constants.EC, 102, "Field validation - Password blank"),
    API1_MISSING_REQUESTER_ID(Constants.EC, 103, "Field validation – Vendor id blank"),
    API1_MISSING_REQUESTER_PASSWORD(Constants.EC, 104, "Field validation – Vendor password blank"),
    API1_SYSTEM_NOT_AUTHORIZED(Constants.EC, 105, "System not authorized"),
    API1_AUTHORIZATION_FAILED(Constants.EC, 106, "Authorization failed"),
    API1_INVALID_CREDENTIALS(Constants.EC, 107, "Invalid credentials"),
    API1_LOCKED_ACCOUNT(Constants.WC, 108, "The account is locked <x>"),
    API1_PENDING_ACCOUNT(Constants.WC, 109, "The accound is pending"),
    API1_DISABLED_ACCOUNT(Constants.WC, 110, "The account is disabled"),
    API1_INACTIVE_ACCOUNT(Constants.WC, 111, "The account is inactive"),
    API1_INCONSISTENT_ACCOUND(Constants.WC, 112, "The accound is inconsistent"),
    API1_NOT_ALLOWED_ACCOUNT(Constants.WC, 113, "The account is not allowed"),


    //API-2
    API2_MISSING_REQUESTER_ID(Constants.EC, 203, "Field validation – Vendor id blank"),
    API2_MISSING_REQUESTER_PASSWORD(Constants.EC, 204, "Field validation – Vendor password blank"),
    API2_SYSTEM_NOT_AUTHORIZED(Constants.EC, 205, "System not authorized"),
    API2_AUTHORIZATION_FAILED(Constants.EC, 206, "Authorization failed"),

    //API-3
    API3_MISSING_SEAMLESS_MSISDN(Constants.EC, 301, "Field validation - Seamless MSISDN is blank"),
    API3_MISSING_INITIAL_TOKEN(Constants.EC, 302, "Field validation - Initial token is blank"),
    API3_AUTHORIZATION_FAILED(Constants.EC, 306, "Authorization failed"),
    API3_SEAMLESS_FLAG_NOT_SET(Constants.WC, 307, "Username does not have flag set"),

    //API-4
    API4_MISSING_INITIAL_TOKEN(Constants.EC, 402, "Field validation - Initial token is blank"),
    API4_MISSING_REQUESTER_ID(Constants.EC, 403, "Field validation – Vendor id blank"),
    API4_MISSING_REQUESTER_PASSWORD(Constants.EC, 404, "Field validation – Vendor password blank"),
    API4_SYSTEM_NOT_AUTHORIZED(Constants.EC, 405, "System not authorized"),
    API4_AUTHORIZATION_FAILED(Constants.EC, 406, "Authorization failed"),

    //API-5
    API5_MISSING_TOKEN(Constants.EC, 502, "Field validation - Token is blank"),
    API5_MISSING_REQUESTER_ID(Constants.EC, 503, "Field validation – Vendor id blank"),
    API5_MISSING_REQUESTER_PASSWORD(Constants.EC, 504, "Field validation – Vendor password blank"),
    API5_SYSTEM_NOT_AUTHORIZED(Constants.EC, 505, "System not authorized"),
    API5_AUTHORIZATION_FAILED(Constants.EC, 506, "Authorization failed"),

    //API-9
    API9_MISSING_MSISDN(Constants.EC, 901, "Field validation - MSISDN is blank"),
    API9_MISSING_SEAMLESS_FLAG(Constants.EC, 902, "Field validation - Seamless flag is blank"),
    API9_SQL_EXCEPTION(Constants.EC, 903, "API Gateway could not perform database query"),

    //API-11
    API11_INVALID_MSISDN(Constants.EC, 1101, "Invalid msisdn"),
    API11_EBU_USER(Constants.EC, 1102, "EBU user"),
    API11_ACCOUNT_IS_ALREADY_REGISTERED(Constants.EC, 1103, "Account is already registered for this msisdn"),

    //API-12
    API12_MANDATORY_FIELD(Constants.EC, 1201, "Mandatory field"),
    API12_INVALID_EMAIL_FORMAT(Constants.EC, 1202, "Invalid email format"),
    API12_INVALID_PHONE_FORMAT(Constants.EC, 1203, "Invalid format for contact phone"),
    API12_ILLEGAL_CHARACTERS_USERNAME(Constants.EC, 1204, "Illegal characters in username"),
    API12_USERNAME_ALREADY_EXISTS(Constants.EC, 1205, "Username already exists"),
    API12_INTERNAL_ERROR(Constants.EC, 505, "Internal error"),
    API12_ACCOUNT_REGISTERED(Constants.EC, 1206, "Account is already registered"),
    API12_MULTIPLE_CONTACT_IDS(Constants.EC, 1207, "Multiple contact ids in CRM on creating account with status pending + #listOfContactIds"),
    API12_NO_CONTACT_ID_IN_CRM(Constants.EC, 1208, "Error Self Enrollment No Contact Id in CRM with id for username"),
    API12_MORE_ACCOUNTS_WITH_SAME_EMAIL_AND_MSISDN(Constants.EC, 1209, "There are more than one account with same emalil & MSISDN"),
    API12_SINGLE_ACCOUNT_SAME_MSISDN(Constants.EC, 1210, "There is already a single account with same msisdn"),
    API12_CONFIRM_UPDATE_ACCOUNT(Constants.EC, 1211, "Account is alreaty registered for this msisdn. Confirm update profile"),
    API12_GEONUM_AND_BLACKLISTED_EMAIL(Constants.EC, 1212, "Is GeoNum and email in blacklist"),

    //API-13
    API13_MANDATORY_FIELD(Constants.EC, 1301, "Mandatory field"),
    API13_PASSWORD_MIN_LENGTH(Constants.EC, 1302, "Password must contain at least 8 characters"),
    API13_PASSWORD_MATCH(Constants.EC, 1303, "Password and confirmPassword do not match"),
    API13_CODE_EXPIRED(Constants.EC, 1304, "Activation code has expired"),
    API13_CODE_INCORRECT(Constants.EC, 1305, "Activation code is incorrect"),
    API13_IDM_ERROR(Constants.EC, 1306, "IDM error"),
    API13_ACTIVATION_MULTIPLE_CONTACT_ID(Constants.EC, 1307, "Multiple contact IDS on activate account"),
    API13_NO_STATUS_ERROR(Constants.EC, 1398, "Connectivity error"),

    //API-14
    API14_ERROR_CODE_SEND(Constants.EC, 1402, "Codul de activare nu a fost trimis. Te rugam sa revii mai tarziu."),

    //API-15
    API15_ERROR_INCORRECT_PHONE_NUMBER_FORMAT(Constants.EC, 1501, "Incorrect phone number format"),
    API15_ERROR_INCORRECT_EMAIL_FORMAT(Constants.EC, 1502, "Incorrect email format"),
    API15_ERROR_NOT_RECOVER_USERNAME(Constants.EC, 1503, "Could not recover username"),
    API15_ERROR_GENERAL_ERROR(Constants.EC, 1504, "General error"),
    API15_ERROR_NOT_RECOVER_USERNAME_NONVODAFONE(Constants.EC, 1505, "Could not recover username for nonvodafone role"),
    API15_ERROR_EMAIL_AND_USER_EMAIL_NOT_MATCH(Constants.EC, 1506, "Email and user email do not match"),

    //API-16
    API16_INVALID_PHONE_FORMAT(Constants.EC, 1601, "Incorrect phone number format"),
    API16_INVALID_EMAIL_FORMAT(Constants.EC, 1602, "Incorrect email format"),
    API16_PASSWORD_GENERATE_ERROR(Constants.EC, 1603, "Could not regenerate password"),
    API16_GENERAL_ERROR(Constants.EC, 1604, "General error"),
    API16_NONVODAFONE_ROLE_PASS_GEN_ERR(Constants.EC, 1605, "Could not regenerate password for nonvodafone role"),
    API16_EMAIL_NOT_MATCH_ERROR(Constants.EC, 1606, "Email and user email do not match"),
    API16_USER_NOT_MATCH_ERROR(Constants.EC, 1607, "User do not match"),

    //API-17
    API17_VALIDATION_ERROR(Constants.EC, 1701, "Validation error-"),
    API17_EMAIL_NOT_MATCH_ERROR(Constants.EC, 1702, "Email and user email do not match"),
    API17_CONTACT_CUSTOMER_SERVICES_ERROR(Constants.EC, 1703, "Contact customer services"),
    API17_CONTACT_ACCOUNT_ADMIN_ERROR(Constants.EC, 1704, "Contact account admin"),
    API17_ACCOUNT_IS_ALREADY_REGISTERED_ERROR(Constants.EC, 1705, "Account is already registered"),
    API17_UNABLE_TO_UPDATE_PROFILE_ERROR(Constants.EC, 1706, "Unable to update profile"),

    //API-18
    API18_VALIDATION_ERROR(Constants.EC, 1801, "Validation error"),
    API18_PASSWORD_MATCH(Constants.EC, 1802, "Password and confirmPassword do not match"),
    API18_IDM_ERROR(Constants.EC, 1803, "IDM error"),

    //API-23
    API23_MISSING_BAN_ERROR(Constants.EC, 2301, "Invalid msisdn and ban"),
    API23_WS_IF90_FAIL(Constants.EC, 2302, "GetInvoiceDetails call failed"),
    API23_SUBSCRIBER_NOT_FOUND(Constants.EC, 2303, "Subscriber not in Capone"),
    API23_INVOICE_NOT_AVAILABLE(Constants.WC, 2304, "Invoice not available"),
    API23_INVOICE_ALREADY_PAID(Constants.WC, 2305, "Invoice already paid"),
    API23_INVOICE_NOT_ISSUED(Constants.WC, 2306, "No invoice issued"),
    API23_DISTRIBUTIONS_ON_MULTIPLE_ACCOUNTS(Constants.WC, 2307, "If the call to GetInvoiceDetails returns resultCode=5"),

    //API-30
    API30_VALIDATE_INPUTS_ERROR(Constants.EC, 00004, "API Gateway Web Service call is not success"),
    API30_PREPAID_NUMBER_ERROR(Constants.WC, 3001, "UserType is not prepaid"),

    //API-31
    API31_USER_NOT_ELIGIBLE(Constants.WC, 3101, "User is not eligible for invoice recharges"),
    API31_AGE_NOT_ELIGIBLE(Constants.WC, 3102, "Account age doesn’t complies with eligibilities rules"),
    API31_PLAN_NOT_ELIGIBLE(Constants.WC, 3103, "Collection plan doesn’t complies with eligibilities rules"),
    API31_SUBSCRIBER_RESTRICTION(Constants.WC, 3104, "Subscriber has restriction or barring offer "),
    API31_SUBSCRIBER_NOT_FOUND(Constants.WC, 3105, "Subscriber not found"),
    API31_NO_ELIGIBLE_BAN(Constants.WC, 3106, "User has no eligible invoice for recharge"),
    API31_ACCOUNT_LIST_IS_NULL(Constants.EC, 0004, "Account List is null"),

    //API-34
    API34_VOUCHER_USED(Constants.EC, 3401, "Voucher used"),
    API34_VOUCHER_EXPIRED(Constants.EC, 3402, "Voucher expired"),
    API34_VOUCHER_INVALID(Constants.EC, 3403, "Voucher invalid"),

    //API-38
    API38_USER_HAS_CREDIT_IN_ADVANCE(Constants.WC, 3801, "User has cred in advance"),
    API38_USER_HAS_OFFER_IN_PENDING(Constants.WC, 3802, "User has offer in pending"),

    //API-41
    API41_PRICE_PLAN_NO_FOUND(Constants.EC, 4101,  "No price plan found for subscriber"),
    API41_OFFER_INCOMPATIBLE_WITH_CURRENT_PRICE_PLAN(Constants.EC, 4102, "User has cred in advance"),
    API41_HAS_PENDING_REQUESTS(Constants.EC, 4103, "User has pending requests"),
    API41_NOT_ALLOWED_SUBUSER(Constants.EC, 4104, "User is not allowed to delete the promotion"),

    API45_BILL_MEDIA_TYPE_7(Constants.EC, 4501, "User cannot activate ebill"),
    API45_EDIT_NOT_ALLOWED(Constants.EC, 4502, "User role does not allow bill activation"),

    //API-64
    API_64_USER_IS_NOT_ELIGIBLE(Constants.EC, 6402, "API Gateway communication with web service is successful, but status in response is -1"),
    API_64_CREDIT_IN_ADVANCE_108_ERROR(Constants.EC, 6408, "Failure code: 108."),

    // API-67
    API_67_ICR_NOT_VDF_SUBSCRIBER(Constants.EC, 6701, "The number entered is not in the Vodafone network."),
    API_67_ICR_POSTPAID_PHONE_NUMBER(Constants.EC, 6702, "Postpaid phone number. It needs to be introduced smsCode"),
    API_67_ICR_SEND_SMS_FAILED(Constants.EC, 6703, "Send sms failed"),
    API_67_ICR_INVALID_SMS_CODE(Constants.EC, 6704, "Invalid SMS code"),
    API_67_ICR_SMS_SENT_STILL_VALID(Constants.EC, 6705, "Sms has been sent before"),

    //API-70
    API70_SID_ERROR(Constants.EC, 7001, "Error GDPR pemissions not found"),

    TO_BE_REPLACED("REPLACE", 99999, "Mesaj temporar, trebuie sters!"),//TODO

    //API-72
    API72_USER_NOT_EXISTS(Constants.WC, 7202, "User does not exists in Kaltura"),

    // API-74
    API74_SAME_NUMBERS(Constants.WC, 7401, "Donor's and recipient's numbers are the same"),
    API74_AMOUNT_INVALID(Constants.WC, 7402, "Amont is invalid"),
    API74_SIM_BARRED(Constants.EC, 7403, "SIM status is barred"),
    API74_SIM_SUSPENDED(Constants.EC, 7404, "SIM status is suspended"),
    API74_CREDIT_EXPIRED(Constants.EC, 7405, "Credit is expired or suspended"),
    API74_BALANCE_INSUFFICIENT(Constants.EC, 7406, "Your balance is insufficient to make a transfer"),
    API74_NUMBER_INVALID(Constants.EC, 7407, "Recipient's number is invalid"),
    API74_PENDING_TRANSACTION(Constants.EC, 7408, "Aveti o cerere in curs de procesare, vă rugăm reveniti.User accessed againg the Credit Transfer page immediately (configurable timeframe – default 5 seconds) after a Credit transfer request was successfully submitted.");


    private String type;
    private int errorCode;
    private String errorMessage;
    private static final String ERROR_CODE_FORMAT = "%05d";

    public String getErrorCode() {
        return type + String.format(ERROR_CODE_FORMAT, errorCode);
    }

    public int getIntErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    private ErrorCodes(String type, int code, String message) {
        this.type = type;
        this.errorCode = code;
        this.errorMessage = message;
    }

    @Override
    public String toString() {
        return "{ " + type + errorCode + ", " + errorMessage + "}";
    }

    class Constants {
        private static final String EC = "EC";
        private static final String WC = "WC";
    }

}