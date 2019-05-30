package ro.vodafone.mcare.android.utils;

import org.w3c.dom.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.realm.appconfig.AppConfiguration;
import ro.vodafone.mcare.android.custom.InputTypes;
import ro.vodafone.mcare.android.ui.activities.LoginActivity;
import ro.vodafone.mcare.android.ui.utils.TextUtils;

/**
 * Created by user on 02.12.2016.
 */


public class Validator {

    public static int EMPTY_FIELD_STATUS = 0;
    public static int VALIDE_FIELD_STATUS = 1;
    public static int INVALID_FORMAT_FIELD_STATUS = 2;
    public static int INVALID_FORMAT_FIELD_SECOND_STATUS = 3;

    public int isInputValid(int inputType, String value, Boolean isGeoNumber){
        int isValid = EMPTY_FIELD_STATUS;
        switch (InputTypes.fromId(inputType)){
            case  SIMPLE_TEXT:
                return isNotEmptyInput(value);
            case LOGIN:
                return isLoginValid(value);
            case PASSWORD:
                return isPasswordValid(value);
            case CONFIRM_PASSWORD:
                return isConfirmPasswordValid(value);
            case NEW_PASSWORD:
                return isNewPasswordValid(value);
            case EMAIL:
                return isEmailValid(value);
            case VF_PHONE:
                return isVodafonePhoneValid(value, isGeoNumber);
            case USERNAME:
                return isUserNameValid(value);
            case CONTACT_PHONE:
                return isContactPhoneValid(value);
            case PHONE:
                return isPhoneValid(value);
            case BILL_AMOUNT:
                return isBillAmountValid(value);
            case CALL_DETAILS_EMAIL:
                return isCallDetailsEmailValid(value);
            case RECHARGE_VALUE:
                return isRechargeValueValide(value);
            case VOUCHER_CODE:
                return isVoucherCodeValide(value);
        }

        return isValid;

    }

    public int isNotEmptyInput(String value){
        int isValid = EMPTY_FIELD_STATUS;
        System.out.println("Not Emty Validation: " + value);
        if(null != value.toString() && !"".equals(value.toString())){
            System.out.println("Not Emty : " + value);
            isValid = VALIDE_FIELD_STATUS;
        }

        return isValid;
    }

    public int isLoginValid(String value){
        int isValid = EMPTY_FIELD_STATUS;
        System.out.println("Not Emty Validation: " + value);
        if(null != value.toString() && !"".equals(value.toString())){
            System.out.println("Not Emty : " + value);
            isValid = VALIDE_FIELD_STATUS;
        }

        return isValid;
    }


    public int isNewPasswordValid(String value){
        System.out.println("isNewPasswordValid: " + value);
        int isValid = EMPTY_FIELD_STATUS;

        LoginActivity loginActivity = (LoginActivity) VodafoneController.findActivity(LoginActivity.class);

        if(loginActivity == null){
            return isValid;
        }

        if(null != value.toString() && !"".equals(value.toString())){
            Pattern pattern;
            Matcher matcher;
            if (value.toString().indexOf(" ") >=0 ) {
                isValid = R.string.register_error_message_register_password;
                return isValid;
            }
            String PASSWORD_PATTERN ="^(?=.*[A-Z])(?=.*[0-9])(?=.*[a-z]).{8,30}$";

            pattern = Pattern.compile(PASSWORD_PATTERN);
            matcher = pattern.matcher(value);
            if (matcher.matches()) {
                isValid = VALIDE_FIELD_STATUS;
                loginActivity.setNewPassword(value);
            }else{
                isValid = INVALID_FORMAT_FIELD_STATUS;
            }
        }else{
            isValid = INVALID_FORMAT_FIELD_STATUS;
        }
        return isValid;
    }

    public int isConfirmPasswordValid(String value){

        LoginActivity loginActivity = (LoginActivity) VodafoneController.findActivity(LoginActivity.class);

        if(loginActivity == null){
            return 0;
        }

        String newPassword = loginActivity.getNewPassword();

        int isValid = R.string.register_error_message_confirm_password;

        if(null != value.toString() && !"".equals(value.toString())) {
            if (value.toString().indexOf(" ") >=0 ) {
                isValid = R.string.register_error_message_register_password;
                return isValid;
            }
            if (value.equals(newPassword)) {
                isValid = 1;
            }
        }

        if ("".equals(value.toString()))
            isValid = 1;


        return isValid;
    }

    public int isPasswordValid(String value){
        int isValid = EMPTY_FIELD_STATUS;
        if(null != value.toString() && !"".equals(value.toString())){
            isValid = VALIDE_FIELD_STATUS;
        }
        return isValid;
    }

    public int isEmailValid(String email){
        System.out.println("Email validation()");
        int isValid = EMPTY_FIELD_STATUS;

        if(null != email.toString() ){
            Pattern pattern;
            Matcher matcher;
            System.out.println("Email validation not empty");
            matcher = TextUtils.EMAIL_PATTERN.matcher(email);

            if (matcher.matches()) {
                System.out.println("Email validation Matchaes");
                isValid = VALIDE_FIELD_STATUS;
            }else{
                System.out.println("Email validation Not Match - Invalid");
                isValid = INVALID_FORMAT_FIELD_STATUS;//R.string.register_error_message_email_address
            }
        }

        return isValid;
    }

    public int isVodafonePhoneValid(String value, boolean isGeoNumber){
        System.out.println("isPhoneValid: " + value);
        int isValid = INVALID_FORMAT_FIELD_STATUS;

        Pattern pattern = Pattern.compile("^07[0-9]{8}");
        Matcher matcher = pattern.matcher(value);

        Pattern pattern2 = Pattern.compile("^0[0-9]{9}");
        Matcher matcher2 = pattern2.matcher(value);

        if(value != null ){
            if (matcher.matches()) {
                System.out.println("Phone Number Valid");
                isValid = VALIDE_FIELD_STATUS;
            }else if (matcher2.matches()){
                if(isGeoNumber){
                    System.out.println("Phone Number is GeoNumber");
                    isValid = VALIDE_FIELD_STATUS;
                }else {
                    System.out.println("Phone Number don't mach 07XXXX");
                    isValid = INVALID_FORMAT_FIELD_SECOND_STATUS;
                }
            }
        }else{
            isValid = EMPTY_FIELD_STATUS;
        }


        return isValid;
    }


    public int isPhoneValid(String value){
        System.out.println("isPhoneValid: " + value);
        int isValid = EMPTY_FIELD_STATUS;

        Pattern pattern = Pattern.compile("^0[0-9]{9}");
        Matcher matcher = pattern.matcher(value);

        if(value != null ){
            if (matcher.matches()) {
                System.out.println("Phone Number Valid");
                isValid = VALIDE_FIELD_STATUS;
            }else{
                isValid = INVALID_FORMAT_FIELD_STATUS;
            }
        }

        return isValid;
    }


    public int isUserNameValid(String value){
        System.out.println("Username Validation: "+ value);
        int isValid = EMPTY_FIELD_STATUS;
        if(null == value.toString() && "".equals(value.toString())){
            System.out.println("Username empty");
            return isValid = EMPTY_FIELD_STATUS;
        }if(value.length() < 4){
            System.out.println("Less then 4 characters");
            return R.string.register_error_message_min_characters_username;//INVALID_FORMAT_FIELD_STATUS;
        } if(value.length() > 60){
            return isValid = INVALID_FORMAT_FIELD_STATUS;
        }

        else if (!value.matches("[ a-zA-Z0-9]+")){
            System.out.println("Don't mach a-zA-Z");
            return isValid = R.string.register_error_message_invalid_characters_username;
        }
        else{
            System.out.println("UserName is Valid");
            return isValid = VALIDE_FIELD_STATUS;
        }

    }

    public int validateConfirmPassword(String newpassword, String confirmPassword){
        int validateResponse = EMPTY_FIELD_STATUS;

        if(confirmPassword != null && confirmPassword.equals("")){
            validateResponse = EMPTY_FIELD_STATUS;
        }else if(newpassword != null && confirmPassword != null && confirmPassword.equals(newpassword)){
            validateResponse = VALIDE_FIELD_STATUS;
        }else {
            validateResponse = INVALID_FORMAT_FIELD_STATUS;
        }
        return validateResponse;
    }




    public int isContactPhoneValid(String value) {
        System.out.println("isContactPhoneValid: " + value);
        int isValid = EMPTY_FIELD_STATUS;
        if(null != value.toString() && !"".equals(value.toString())) {

            Pattern pattern = Pattern.compile("^0[0-9]{9}");
            Matcher matcher = pattern.matcher(value);

            if (matcher.matches()) {
                System.out.println("Contact Phone Number Valid");
                isValid = 1;
            } else {
                System.out.println("Contact Phone Number NOT Valid");
                isValid = R.string.register_error_message_invalid_phone_number;
            }
        }
        return isValid;
    }

    public int isBillAmountValid(String value){
        System.out.println("isBillAmountValid: " + value);


        int isValid = EMPTY_FIELD_STATUS;

        if(value != null){
            if(!value.startsWith("0") && !value.isEmpty()&& !value.equals(".") && Double.parseDouble(value) >= 1 && !value.startsWith(".")){
                isValid = VALIDE_FIELD_STATUS;
            }else{
                isValid = INVALID_FORMAT_FIELD_STATUS;
            }
        }
        return isValid;
    }




    public int isCallDetailsEmailValid(String email){
        System.out.println("Call Details Email Valid() " + email );
        int isValid = EMPTY_FIELD_STATUS;
        String  userEmail = VodafoneController.getInstance().getUserProfile().getEmail();
        System.out.println("User Email: " + userEmail);
        if(null != email.toString() && !"".equals(email.toString())){
            Pattern pattern;
            Matcher matcher;
            System.out.println("Email validation not empty");
            matcher = TextUtils.EMAIL_PATTERN.matcher(email);

            if (matcher.matches()) {
                System.out.println("Email validation Matchaes pattern");
                if(userEmail.equals(email)) {
                    System.out.println("Both Email are equal");
                    isValid = VALIDE_FIELD_STATUS;
                }else{
                    System.out.println("Both Email are NOT equal");
                    isValid = INVALID_FORMAT_FIELD_STATUS;
                }
            }else{
                System.out.println("Email validation Not Match - Invalid");
                isValid = INVALID_FORMAT_FIELD_STATUS;//R.string.register_error_message_email_address
            }
        }


        return isValid;
    }

    public int isRechargeValueValide(String value){
        int isValid = EMPTY_FIELD_STATUS;

        try{
            if(value != null){
                if(Integer.valueOf(value) >= Integer.valueOf(AppConfiguration.getMinTopUpAmountForVerify()) && Integer.valueOf(value) <= 100){
                    isValid = VALIDE_FIELD_STATUS;
                }else{
                    isValid = INVALID_FORMAT_FIELD_STATUS;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            isValid = INVALID_FORMAT_FIELD_STATUS;
        }

        return isValid;
    }

    public int isVoucherCodeValide(String value){
        int isValid = EMPTY_FIELD_STATUS;
        if(value!=null && !value.equals("")) {

            Pattern pattern = Pattern.compile("^[0-9]{14}$");
            Matcher matcher = pattern.matcher(value);

            if (matcher.matches()) {
                System.out.println("Voucher Code Valid");
                isValid = 1;
            } else {
                System.out.println("Voucher Code NOT Valid");
                isValid = INVALID_FORMAT_FIELD_STATUS;
            }
        }
        return isValid;
    }

    public int isICRToeknValid(String value){
        System.out.println("isPhoneValid: " + value);
        int isValid = EMPTY_FIELD_STATUS;

        Pattern pattern = Pattern.compile("^[0-9a-zA-Z]+");
        Matcher matcher = pattern.matcher(value);

        if(value != null ){
            if (matcher.matches()) {
                System.out.println("Phone Number Valid");
                isValid = VALIDE_FIELD_STATUS;
            }else{
                isValid = INVALID_FORMAT_FIELD_STATUS;
            }
        }

        return isValid;
    }
}
