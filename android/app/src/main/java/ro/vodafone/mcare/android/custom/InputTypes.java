package ro.vodafone.mcare.android.custom;

public enum InputTypes {
    SIMPLE_TEXT(0), LOGIN(1), PASSWORD(2), CONFIRM_PASSWORD(3), NEW_PASSWORD(4), EMAIL(5), VF_PHONE(6), USERNAME(7), CONTACT_PHONE(8), UNIQUE_CODE(9), PHONE(10), BILL_AMOUNT(11),CALL_DETAILS_EMAIL(12), RECHARGE_VALUE(13), VOUCHER_CODE(14);
    private int id;

    InputTypes(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static InputTypes fromId(int id) {
        for (InputTypes ts : values()) {
            if (ts.id == id) return ts;
        }
        throw new IllegalArgumentException();
    }
}
