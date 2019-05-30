package ro.vodafone.mcare.android.ui.views;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.daimajia.swipe.adapters.BaseSwipeAdapter;

import org.apache.commons.lang3.text.WordUtils;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.billRecharges.BillRechargesSuccess;
import ro.vodafone.mcare.android.client.model.billRecharges.MonthlyRecharge;
import ro.vodafone.mcare.android.client.model.billRecharges.OneTimeRecharge;
import ro.vodafone.mcare.android.client.model.billRecharges.WeeklyRecharge;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.system.TopUPLabels;
import ro.vodafone.mcare.android.service.RechargeService;
import ro.vodafone.mcare.android.ui.activities.TopUpActivity;
import ro.vodafone.mcare.android.ui.fragments.topUp.TopUpRecurrentRechargesFragment;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.utils.NumbersUtils;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.DateUtils;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;
import ro.vodafone.mcare.android.widget.dialogs.VodafoneDialog;
import rx.Observer;

import static ro.vodafone.mcare.android.rest.requests.BillRechargeRequest.RechargeTypeEnum.emr;
import static ro.vodafone.mcare.android.rest.requests.BillRechargeRequest.RechargeTypeEnum.ewr;
import static ro.vodafone.mcare.android.rest.requests.BillRechargeRequest.RechargeTypeEnum.otr;


public class ListViewSwipeAdapter<T> extends BaseSwipeAdapter {

    public static final String WEEKLY_RECHARGE = "weeklyRecharge";
    public static final String MONTHLY_RECHARGE = "monthlyRecharge";
    public static final String ONDATE_RECHARGE = "onDateRecharge";
    public static final String DATE = "DATE";
    public static final String DAY = "DAY";
    public static final String AMOUNT = "AMOUNT";
    public static final String MSISDN = "MSISDN";
    private String TAG = "ListViewSwipeAdapter";
    protected List<T> list;
    protected Context mContext;
    private String type;
    private ListView listView;
    private VodafoneDialog vodafoneDialog;
    private LinearLayout categoryLayout;
    private Fragment instance;

    public ListViewSwipeAdapter(Context mContext, List<T> list, String type, ListView currentListView, LinearLayout categoryLayout, Fragment instance) {
        this.mContext = mContext;
        this.list = list;
        this.type = type;
        this.listView = currentListView;
        this.categoryLayout = categoryLayout;
        this.instance = instance;
    }


    @Override
    public int getSwipeLayoutResourceId(int position) {
        Log.d(TAG, "getSwipeLayoutResourceId");
        return R.id.swipe_layout;
    }

    @Override
    public View generateView(final int position, ViewGroup parent) {
        Log.d(TAG, "generateView");

        View v = LayoutInflater.from(mContext).inflate(R.layout.top_up_recurent_item, parent,false);

        v.findViewById(R.id.recurent_delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Tealium Track Event
                Map<String, Object> tealiumMapEvent = new HashMap(6);
                tealiumMapEvent.put("screen_name","recurrent top-ups");
                tealiumMapEvent.put("event_name","mcare:recurrent top-ups: button:sterge");
                tealiumMapEvent.put("user_type",VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
                TealiumHelper.trackEvent("event_name", tealiumMapEvent);

                loadDeleteDialog(position, type);

            }
        });

        return v;
    }

    @Override
    public void fillValues(int position, View convertView) {

        Log.d(TAG, "fillValues for position : " + position);

        //if not item is found, break all logic
        if (getItem(position) == null) {
            Log.d(TAG, "fillValues getItem returned null");
            return;
        }

        VodafoneTextView recurrent_evt_name = (VodafoneTextView) convertView.findViewById(R.id.recurent_event_name);
        VodafoneTextView recurrent_beneficiar = (VodafoneTextView) convertView.findViewById(R.id.recurent_beneficiar);
        VodafoneTextView recurrent_amount = (VodafoneTextView) convertView.findViewById(R.id.recurrent_amount);

        if (type.equals(WEEKLY_RECHARGE)) {
            WeeklyRecharge weeklyRecharge = (WeeklyRecharge) getItem(position);

            recurrent_amount.setText(mContext.getString(R.string.recurrent_recharge_amout)
                    .replace(AMOUNT, NumbersUtils.twoDigitsAfterDecimal(weeklyRecharge.getScheduledAmount())));

            String msisdn = weeklyRecharge.getMsisdn();
            if (msisdn.startsWith("7")) {
                msisdn = msisdn.replaceFirst("7", "07");
            }

            recurrent_beneficiar.setText(mContext.getString(R.string.recurrent_recharge_To)
                    .replace(MSISDN, getFormatedMsisdn(msisdn)));

            recurrent_evt_name.setText(mContext.getString(R.string.weekly_recharge_item_header)

                    .replace(DAY, String.valueOf(DaysOfWeek.getDaysFromId(weeklyRecharge.getWeeklyCycle()))));

        }

        if (type.equals(MONTHLY_RECHARGE)) {

            MonthlyRecharge monthlyRecharge = (MonthlyRecharge) getItem(position);

            recurrent_amount.setText(mContext.getString(R.string.recurrent_recharge_amout)
                    .replace(AMOUNT, NumbersUtils.twoDigitsAfterDecimal(monthlyRecharge.getScheduledAmount())));

            recurrent_beneficiar.setText(mContext.getString(R.string.recurrent_recharge_To)
                    .replace(MSISDN, String.valueOf(getFormatedMsisdn(monthlyRecharge.getMsisdn()))));

            recurrent_evt_name.setText(mContext.getString(R.string.monthly_recharge_item_header)
                    .replace(DATE, String.valueOf(monthlyRecharge.getMonthlyCycle())));
        }

        if (type.equals(ONDATE_RECHARGE)) {
            OneTimeRecharge oneTimeRecharge = (OneTimeRecharge) getItem(position);

            recurrent_beneficiar.setText(mContext.getString(R.string.recurrent_recharge_To)
                    .replace(MSISDN, String.valueOf(getFormatedMsisdn(oneTimeRecharge.getMsisdn()))));

            recurrent_amount.setText(mContext.getString(R.string.recurrent_recharge_amout)
                    .replace(AMOUNT, NumbersUtils.twoDigitsAfterDecimal(oneTimeRecharge.getScheduledAmount())));

            recurrent_evt_name.setText(WordUtils.capitalize(DateUtils.getDate(String.valueOf(oneTimeRecharge.getOneTimeDate()), new SimpleDateFormat("dd MMMM", new Locale("RO", "RO")))));

        }
    }

    private String getFormatedMsisdn(String msisdn) {
        if (msisdn != null) {
            if (msisdn.startsWith("4")) {
                msisdn = msisdn.substring(1);
            }

            if (msisdn.length() == 9) {
                msisdn = "0" + msisdn;
            }
        }
        return msisdn;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        ScreenMeasure.setListHeight(listView, this);
    }

    @Override
    public long getItemId(int position) {


        return position;
    }

    @Override
    public T getItem(int position) {
        Log.d(TAG, "Item position - " + position);
        Log.d(TAG, "List size - " + list.size());

        return list.get(position);
    }

    private void loadDeleteDialog(final int position, final String type) {
        Log.d(TAG, "loadDeleteDialog");
        Log.d(TAG, " position " + position);
        Log.d(TAG, "type" + type);
        TopUpActivity topUpActivity = (TopUpActivity) VodafoneController.findActivity(TopUpActivity.class);
        if (topUpActivity != null){
            vodafoneDialog = new VodafoneDialog(topUpActivity, mContext.getResources().getString(R.string.recurrent_delete_popup_message), "Șterge", "Anulează");
            vodafoneDialog.setDismissActionOnPositive();
            vodafoneDialog.setNegativeAction(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Negative2");
                    deleteRecurrentRecharge(position, type);
                }
            });
            vodafoneDialog.setCancelable(true);
            vodafoneDialog.show();
        }
    }

    public enum DaysOfWeek {
        Luni(1), Marti(2), Miercuri(3), Joi(4), Vineri(5), Sâmbătă(6), Duminică(7);

        private int id;

        DaysOfWeek(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static DaysOfWeek getDaysFromId(int id) {
            for (DaysOfWeek ts : values()) {
                if (ts.id == id) return ts;
            }
            throw new IllegalArgumentException();
        }
    }

    private void deleteRecurrentRecharge(final int position, String type) {
        switch (type) {
            case WEEKLY_RECHARGE: {
                RechargeService rechargeService = new RechargeService(mContext);

                WeeklyRecharge weeklyRecharge = (WeeklyRecharge) getItem(position);
                Integer scheduleID = weeklyRecharge.getScheduleId();
                Float scheduleAmount = weeklyRecharge.getScheduledAmount();
                Integer daysInWeek = weeklyRecharge.getWeeklyCycle();
                String ban = UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan();
                String msisdn = VodafoneController.getInstance().getUserProfile().getMsisdn();

                rechargeService.deleteRecurrentWeeklyRecharge(scheduleID, scheduleAmount, ewr, daysInWeek, ban, msisdn)
                        .subscribe(new Observer<GeneralResponse<BillRechargesSuccess>>() {

                            @Override
                            public void onNext(GeneralResponse<BillRechargesSuccess> billRechargesSuccessGeneralResponse) {
                                if (billRechargesSuccessGeneralResponse.getTransactionStatus() == 0) {
                                    list.remove(position);
                                    notifyDataSetChanged();
                                    if (list.isEmpty()) {
                                        categoryLayout.setVisibility(View.GONE);
                                        ((TopUpRecurrentRechargesFragment) instance).checkIfListsAreEmpty();
                                    }

                                    closeAllItems();
                                    new CustomToast.Builder(mContext).message(TopUPLabels.getTop_up_recurrent_positive_delete_toast()).success(true).show();
//                                CustomToast customToast = new CustomToast(TopUpActivity.mInstance, mContext, TopUPLabels.getTop_up_recurrent_positive_delete_toast(), true);
//                                customToast.show();
                                }
                            }

                            @Override
                            public void onCompleted() {
                                vodafoneDialog.dismiss();
                            }

                            @Override
                            public void onError(Throwable e) {
                                new CustomToast.Builder(mContext).message(TopUPLabels.getTop_up_recurrent_negative_delete_toast()).success(false).show();
//                            CustomToast customToast = new CustomToast(TopUpActivity.mInstance, mContext, TopUPLabels.getTop_up_recurrent_negative_delete_toast(), false);
//                            customToast.show();
                            }
                        });
                break;
            }
            case ListViewSwipeAdapter.MONTHLY_RECHARGE: {
                RechargeService rechargeService = new RechargeService(mContext);

                MonthlyRecharge monthlyRecharge = (MonthlyRecharge) getItem(position);
                Integer scheduleID = monthlyRecharge.getScheduleId();
                Float scheduleAmount = monthlyRecharge.getScheduledAmount();
                Integer daysInMonth = monthlyRecharge.getMonthlyCycle();
                String ban = UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan();
                String msisdn = VodafoneController.getInstance().getUserProfile().getMsisdn();

                rechargeService.deleteRecurrentMonthlyRecharge(scheduleID, scheduleAmount, emr, daysInMonth, ban, msisdn)
                        .subscribe(new Observer<GeneralResponse<BillRechargesSuccess>>() {

                            @Override
                            public void onNext(GeneralResponse<BillRechargesSuccess> billRechargesSuccessGeneralResponse) {
                                list.remove(position);
                                notifyDataSetChanged();
                                if (list.isEmpty()) {
                                    categoryLayout.setVisibility(View.GONE);
                                    ((TopUpRecurrentRechargesFragment) instance).checkIfListsAreEmpty();
                                }

                                closeAllItems();
                                new CustomToast.Builder(mContext).message(R.string.recurrent_positive_delete_toast).success(true).show();
//                            CustomToast customToast = new CustomToast(TopUpActivity.mInstance, mContext, mContext.getResources().getString(R.string.recurrent_positive_delete_toast), true);
//                            customToast.show();
                            }


                            @Override
                            public void onCompleted() {
                                vodafoneDialog.dismiss();
                            }

                            @Override
                            public void onError(Throwable e) {
                                new CustomToast.Builder(mContext).message(R.string.recurrent_negative_delete_toast).success(false).show();
//                            CustomToast customToast = new CustomToast(TopUpActivity.mInstance, mContext, mContext.getResources().getString(R.string.recurrent_negative_delete_toast), false);
//                            customToast.show();
                            }
                        });

                break;
            }
            case ListViewSwipeAdapter.ONDATE_RECHARGE: {
                RechargeService rechargeService = new RechargeService(mContext);

                OneTimeRecharge oneTimeRecharge = (OneTimeRecharge) getItem(position);
                Integer scheduleID = oneTimeRecharge.getScheduleId();
                Float scheduleAmount = oneTimeRecharge.getScheduledAmount();
                Long oneTimeDate = oneTimeRecharge.getOneTimeDate();
                String ban = UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan();
                final String msisdn = VodafoneController.getInstance().getUserProfile().getMsisdn();

                rechargeService.deleteRecurrentDateRecharge(scheduleID, scheduleAmount, otr, oneTimeDate, ban, msisdn)
                        .subscribe(new Observer<GeneralResponse<BillRechargesSuccess>>() {

                            @Override
                            public void onNext(GeneralResponse<BillRechargesSuccess> billRechargesSuccessGeneralResponse) {
                                list.remove(position);
                                notifyDataSetChanged();
                                if (list.isEmpty()) {
                                    categoryLayout.setVisibility(View.GONE);
                                    ((TopUpRecurrentRechargesFragment) instance).checkIfListsAreEmpty();
                                }

                                closeAllItems();
                                new CustomToast.Builder(mContext).message(R.string.recurrent_positive_delete_toast).success(true).show();
//                            CustomToast customToast = new CustomToast(TopUpActivity.mInstance, mContext, mContext.getResources().getString(R.string.recurrent_positive_delete_toast), true);
//                            customToast.show();
                            }

                            @Override
                            public void onCompleted() {
                                vodafoneDialog.dismiss();
                            }

                            @Override
                            public void onError(Throwable e) {
                                new CustomToast.Builder(mContext).message(R.string.recurrent_negative_delete_toast).success(false).show();
//                            CustomToast customToast = new CustomToast(TopUpActivity.mInstance, mContext, mContext.getResources().getString(R.string.recurrent_negative_delete_toast), false);
//                            customToast.show();
                            }
                        });
                break;
            }
        }
    }

}
