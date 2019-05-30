package ro.vodafone.mcare.android.ui.fragments.yourProfile.internationalCalls.selectMSISDN;

import java.util.List;

import ro.vodafone.mcare.android.interfaces.MvpContract;
import ro.vodafone.mcare.android.ui.fragments.yourProfile.UserRequests.UserRequestContract;

public interface IcrContract
{
    interface View extends MvpContract.View
    {
        void setupView(List<InternationalCallsMsisdnFragment.NumberType> numberTypes);

        void showCosts(String callerPhone, String calledPhone, Float rate, boolean isOwn);

        void showToken(String callerPhone, String calledPhone);

        void showToast(String message, boolean success, boolean error);

        void showCallerError(String message);

        void showSMSTokenError(String message);

        void trackView(boolean own, boolean other);
    }

    interface Presenter extends MvpContract.Presenter<UserRequestContract.View>
    {
        void getRatesOwn(String phone);

        void getRatesOther(String caller, String called);

        void resendToken(String phone);

        void getRates(String callerPhone, String calledPhone, String smsCode);
    }
}
