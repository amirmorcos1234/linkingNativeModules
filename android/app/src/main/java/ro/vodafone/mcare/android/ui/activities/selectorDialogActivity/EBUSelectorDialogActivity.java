package ro.vodafone.mcare.android.ui.activities.selectorDialogActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmList;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.controllers.DashboardController;
import ro.vodafone.mcare.android.client.model.identity.EntityChildItem;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.hierarchy.Ban;
import ro.vodafone.mcare.android.client.model.realm.hierarchy.Subscriber;
import ro.vodafone.mcare.android.client.model.realm.hierarchy.UserProfileHierarchy;
import ro.vodafone.mcare.android.client.model.realm.system.AppLabels;
import ro.vodafone.mcare.android.custom.CustomEditTextCompat;
import ro.vodafone.mcare.android.custom.ProgressDialogHandler;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.service.UserDataService;
import ro.vodafone.mcare.android.service.tracking.adobe.target.AdobeTargetController;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.activities.authentication.LoginIdentitySelectorActivity;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.views.CardErrorLayout;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.EbuMigratedIdentityController;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;

import static android.view.View.VISIBLE;
import static ro.vodafone.mcare.android.ui.activities.authentication.LoginIdentitySelectorActivity.IDENTITY_SELECTOR_TYPE_DASHBOARD;
import static ro.vodafone.mcare.android.ui.activities.authentication.LoginIdentitySelectorActivity.IDENTITY_SELECTOR_TYPE_KEY;
import static ro.vodafone.mcare.android.ui.header.NavigationHeader.GO_TO_DASHBOARD_WHEN_CLOSE;
import static ro.vodafone.mcare.android.ui.header.NavigationHeader.HAVE_SINGLE_MSISDN;
import static ro.vodafone.mcare.android.ui.header.NavigationHeader.IS_ONLY_BAN_SELECTOR;

/**
 * Created by Bogdan Marica on 10/24/2017.
 */

public class EBUSelectorDialogActivity extends AppCompatActivity {

    public static String TAG = "EBUSelectorDialogActivity";

    public static int RESULT_SELECTOR_UPDATED = 9;
    public boolean haveSingleMsisdn;
    List<Ban> banList = new ArrayList<>();
    @BindView(R.id.ebu_selector_back_button)
    ImageView ebu_selector_back_button;
    @BindView(R.id.ebu_selector_title)
    TextView ebu_selector_title;
    @BindView(R.id.close_selector_image_image)
    ImageView close_selector_image;
    @BindView(R.id.select_dialog_search_image)
    ImageView select_dialog_search_image;
    @BindView(R.id.select_dialog_search_container)
    RelativeLayout select_dialog_search_container;
    @BindView(R.id.select_dialog_error_layout)
    RelativeLayout select_dialog_error_layout;
    @BindView(R.id.select_dialog_search_input)
    CustomEditTextCompat select_dialog_search_input;
    @BindView(R.id.ebu_selector_recyclerView)
    RecyclerView ebu_selector_recyclerView;
    @BindView(R.id.ebu_selector_dialog_header)
    RelativeLayout ebu_selector_dialog_header;
    @BindView(R.id.ebu_selector_change_identity_button)
    VodafoneButton ebu_selector_change_identity_button;
    Thread sThread = new Thread(new Runnable() {
        @Override
        public void run() {

        }
    });
    String toBeSearched = "";
    Ban selectedBan;
    boolean unhandledError = true;
    String banNumber = "";
    ProgressDialog progressDialog;
    boolean isOnlyBanSelector = false;
    boolean startedFromDashboard = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ebu_selector_dialog_activity);
        ButterKnife.bind(this);
        progressDialog = ProgressDialogHandler.progressDialogConstructor(this, AppLabels.getLoadingSpinnerOneMoment());
        setupBundle();
        initPage();
    }

    private void setupBundle() {
        Intent i = getIntent();
        if (i != null) {
            Bundle b = i.getExtras();
            if (b != null) {
                D.w("!! = " + (b.getBoolean(IS_ONLY_BAN_SELECTOR)));
                isOnlyBanSelector = b.getBoolean(IS_ONLY_BAN_SELECTOR);
                startedFromDashboard = b.getBoolean(GO_TO_DASHBOARD_WHEN_CLOSE, false);
                haveSingleMsisdn = b.getBoolean(HAVE_SINGLE_MSISDN, false);

            }
        }
    }

    public void showLoadingDialog() {
        try {
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopLoadingDialog() {
        try {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void initPage() {
        initBanList();
        deflateErrorLayout();
        initViews();
        initRecyclerView();
        initChangeIdentityButton();
    }

    void initBanList() {
        banList = UserSelectedMsisdnBanController.getInstance().getBanList();
        if (banList == null) {
            banList = new ArrayList<>();
            getBansFromEntityChildList(EbuMigratedIdentityController.getInstance().getSelectedIdentity().getChildList());
        }
        if (banList.size() == 0 || (banList.size() == 1 && (banList.get(0).getNumber() == null || banList.get(0).getNumber().equals(""))))
            banList.add(getBanFromEntityChild(EbuMigratedIdentityController.getInstance().getSelectedIdentity()));
    }

    void getBansFromEntityChildList(RealmList<EntityChildItem> childItems) {
        for (EntityChildItem eci : childItems) {
            banList.add(getBanFromEntityChild(eci));
        }
    }

    Ban getBanFromEntityChild(EntityChildItem eci) {
        Ban b = new Ban();
        b.setNumber(eci.getVfOdsBan());
        b.setSubscriberList(null);
        return b;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        callProfileHierarchyOnBanChangeOrClose(true);
    }

    void initViews() {
        ebu_selector_back_button.setVisibility(View.GONE);

        ebu_selector_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (select_dialog_error_layout.getVisibility() == VISIBLE) {
                    select_dialog_error_layout.setVisibility(View.GONE);
                    ebu_selector_recyclerView.setVisibility(View.VISIBLE);
                }
                hideSearchView();
                initSearchView();
                initSearchViewVisibility();
                displayBanSelector();
            }
        });

        initSearchView();
        initSearchViewVisibility();

        close_selector_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callProfileHierarchyOnBanChangeOrClose(true);
                EBUSelectorDialogActivity.this.finish();
            }
        });
    }

    void displayBanSelector() {
        ebu_selector_back_button.setVisibility(View.GONE);
        ebu_selector_title.setText("Selectează identitatea");
        initRecyclerView();
    }

    void displayBackButton() {
        ebu_selector_back_button.setVisibility(View.VISIBLE);
    }

    void setPageTitle(String title) {
        ebu_selector_title.setText(title);
    }

    void initRecyclerView() {
        if (!isOnlyBanSelector) {//user selects MSISDN aswell as BAN
            EBUSelectorRecyclerAdapter adapter = new EBUSelectorRecyclerAdapter(this, banList);
            ebu_selector_recyclerView.setAdapter(adapter);
        } else {//user Selects ONLY BAN
            //code for case : user selects only msisdn, not ban
            EBUSelectorRecyclerAdapter adapter = new EBUSelectorRecyclerAdapter(this, banList, true);
            ebu_selector_recyclerView.setAdapter(adapter);
//            Ban selectedBan = UserSelectedMsisdnBanController.getInstance().getSelectedBan();
//            if (selectedBan.getSubscriberList() == null || selectedBan.getSubscriberList().size() == 0)
//                callApi19(selectedBan.getNumber());
//            else {
//                EBUSelectorRecyclerAdapter adapter = new EBUSelectorRecyclerAdapter(EBUSelectorDialogActivity.this, selectedBan.getSubscriberList(), false, false);
//                ebu_selector_recyclerView.setAdapter(adapter);
//            }
        }
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        ebu_selector_recyclerView.setLayoutManager(mLayoutManager);
    }

    void initSearchViewVisibility() {
        if (banList.size() > 10)
            displaySearchIcon(true);
        else
            displaySearchIcon(false);
    }

    void hideSearchView() {
        try {//sometimes crashes in loophole .. dont know why
            select_dialog_search_container.setVisibility(View.GONE);
            if(select_dialog_search_input.getText()!= null && !select_dialog_search_input.getText().toString().equals(""))
                select_dialog_search_input.setText("");
            ebu_selector_dialog_header.setPadding(ebu_selector_dialog_header.getPaddingLeft(), ebu_selector_dialog_header.getPaddingTop(), ebu_selector_dialog_header.getPaddingRight(), ScreenMeasure.dpToPx(12));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void showSearchView() {
        try {//sometimes crashes in loophole .. dont know why
            select_dialog_search_container.setVisibility(View.VISIBLE);
            if(select_dialog_search_input.getText()!= null && !select_dialog_search_input.getText().toString().equals(""))
               select_dialog_search_input.setText("");
            ebu_selector_dialog_header.setPadding(ebu_selector_dialog_header.getPaddingLeft(), ebu_selector_dialog_header.getPaddingTop(), ebu_selector_dialog_header.getPaddingRight(), ScreenMeasure.dpToPx(12));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    void displaySearchIcon(boolean showSearchIcon) {
        if (showSearchIcon)
            select_dialog_search_image.setVisibility(View.VISIBLE);
        else
            select_dialog_search_image.setVisibility(View.GONE);


    }

    void initSearchView() {
        select_dialog_search_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select_dialog_search_container.setVisibility(select_dialog_search_container.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);

                if (select_dialog_search_container.getVisibility() != View.GONE)
                    ebu_selector_dialog_header.setPadding(ebu_selector_dialog_header.getPaddingLeft(), ebu_selector_dialog_header.getPaddingTop(), ebu_selector_dialog_header.getPaddingRight(), ScreenMeasure.dpToPx(0));
                else
                    ebu_selector_dialog_header.setPadding(ebu_selector_dialog_header.getPaddingLeft(), ebu_selector_dialog_header.getPaddingTop(), ebu_selector_dialog_header.getPaddingRight(), 12);


            }
        });

        select_dialog_search_input.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                toBeSearched = s.toString();
                if(select_dialog_search_input.hasFocus()){
                    if (searchStringIsNull())
                        resetAdapterIfSearchStringEmpty();
                    else
                        updateRecyclerViewAsync();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                displayClearTextIcon(s.toString());
            }
        });
    }
    private void displayClearTextIcon(String text){
        if(text!=null && text.length()==1){
            addCloseButtonIconInsideEditText(select_dialog_search_input);
        }else if(text==null|| text.isEmpty()){
            removeButtonIconInsideEditText(select_dialog_search_input);
        }
    }

    void updateRecyclerViewAsync() {
        try {
            sThread.interrupt();

            sThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    sortListBySearch();
                }
            });
            sThread.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    boolean searchStringIsNull() {
        return toBeSearched.equals("") || toBeSearched.replaceAll(" ", "").equals("");
    }

    void resetAdapterIfSearchStringEmpty() {
        if (((EBUSelectorRecyclerAdapter) ebu_selector_recyclerView.getAdapter()).getIsBan())
            ebu_selector_recyclerView.setAdapter(new EBUSelectorRecyclerAdapter(EBUSelectorDialogActivity.this, banList));
        else
            ebu_selector_recyclerView.setAdapter(new EBUSelectorRecyclerAdapter(EBUSelectorDialogActivity.this, selectedBan.getSubscriberList(), false, false));
    }

    void inflateSubscribersErrorLayout() {
        ebu_selector_recyclerView.setVisibility(View.GONE);
        select_dialog_error_layout.setVisibility(View.VISIBLE);

        setChangeIdentityButton();
        //TODO no need for reload when single msisdn, when api 19 should not be called
        ((CardErrorLayout) select_dialog_error_layout.findViewById(R.id.error_card)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                select_dialog_error_layout.setVisibility(View.GONE);
                ebu_selector_recyclerView.setVisibility(View.VISIBLE);
                callApi19(banNumber);
            }
        });

        hideSearchView();

        close_selector_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callProfileHierarchyOnBanChangeOrClose(true);
                EBUSelectorDialogActivity.this.finish();
            }
        });
    }

    void deflateErrorLayout() {
        ebu_selector_recyclerView.setVisibility(View.VISIBLE);
        select_dialog_error_layout.setVisibility(View.GONE);
    }

    void sortListBySearch() {
        if (((EBUSelectorRecyclerAdapter) ebu_selector_recyclerView.getAdapter()).getIsBan()) {
            List<Ban> customBanList = new ArrayList<>();
            for (Ban ban : banList) {
                if (ban.getNumber().contains(toBeSearched))
                    customBanList.add(ban);
            }
            ebu_selector_recyclerView.setAdapter(new EBUSelectorRecyclerAdapter(EBUSelectorDialogActivity.this, customBanList));
        } else {
            List<Subscriber> fullSubscriberList = selectedBan.getSubscriberList();
            List<Subscriber> customSubscriberList = new ArrayList<>();

            for (Subscriber subscriber : fullSubscriberList) {
                if (subscriber.getMsisdn().contains(toBeSearched))
                    customSubscriberList.add(subscriber);
            }
            ebu_selector_recyclerView.setAdapter(new EBUSelectorRecyclerAdapter(EBUSelectorDialogActivity.this, customSubscriberList, false, true));
        }
    }

    void initChangeIdentityButton() {
        if (getIdentitiesNumber() > 1 && startedFromDashboard) {
            ebu_selector_change_identity_button.setVisibility(View.VISIBLE);
            setChangeIdentityButton();

        } else
            ebu_selector_change_identity_button.setVisibility(View.GONE);

    }

    void setChangeIdentityButton() {
        ebu_selector_change_identity_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callProfileHierarchyOnBanChangeOrClose(true);
                startIdentitySelectorActivity();
            }
        });
    }

    public void callApi19(String banNumber) {
        if (banNumber == null) inflateSubscribersErrorLayout();
        unhandledError = true;
        showLoadingDialog();
        new UserDataService(this).reloadSessionSubscriberHierarchy(banNumber).subscribe(new RequestSaveRealmObserver<GeneralResponse<UserProfileHierarchy>>() {
            @Override
            public void onNext(GeneralResponse<UserProfileHierarchy> response) {
                super.onNext(response);
                unhandledError = false;
                stopLoadingDialog();
                showLogs(response);
                if (response.getTransactionStatus() == 0) {
                    D.d("response.getTransactionStatus() == 0");
                    setSelectedBan(response.getTransactionSuccess().getBan());
                    ebu_selector_recyclerView.setAdapter(new EBUSelectorRecyclerAdapter(EBUSelectorDialogActivity.this, response.getTransactionSuccess().getBan().getSubscriberList(), false, false));
                } else
                    inflateSubscribersErrorLayout();
            }

            @Override
            public void onCompleted() {
                D.w();
                super.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                D.e("error :" + e);
                stopLoadingDialog();
                inflateSubscribersErrorLayout();
            }
        });
    }

    void showLogs(GeneralResponse<UserProfileHierarchy> response) {
        D.d(" ~~~~ " + response);
        D.d(" ~~~~ " + response.getTransactionStatus());
        D.d(" ~~~~ " + response.getTransactionSuccess());
        if (response.getTransactionSuccess() != null) {
            D.d(" ~~~~ " + response.getTransactionSuccess().getBan());
            D.d(" ~~~~ " + response.getTransactionSuccess().getBanList());
            D.d(" ~~~~ " + response.getTransactionSuccess().getSubscriberList());
        }
        D.d(" ~~~~ " + response.getTransactionFault());
    }

    void setSelectedSubscriber(Subscriber selectedSubscriber) {
        Intent intent = new Intent();

        UserSelectedMsisdnBanController.getInstance().setSelectedSubscriber(selectedSubscriber);

        setResult(RESULT_SELECTOR_UPDATED, intent);

        DashboardController.reloadDashboardOnResume();
        EBUSelectorDialogActivity.this.finish();
    }

    void setOnlySelectedBan(Ban selectedBan) {
        Intent intent = new Intent();

        UserSelectedMsisdnBanController.getInstance().setSelectedNumberBan(selectedBan.getNumber());

        setResult(RESULT_SELECTOR_UPDATED, intent);
        DashboardController.reloadDashboardOnResume();
        EBUSelectorDialogActivity.this.finish();
    }

    void setSelectedBan(Ban selectedBan) {
        this.selectedBan = selectedBan;
    }

    public void updateSelectedBanInController(String selectedBanNumber){
        UserSelectedMsisdnBanController.getInstance().setSelectedNumberBan(selectedBanNumber);
    }

    void startIdentitySelectorActivity() {

        Intent intent = new Intent(EBUSelectorDialogActivity.this, LoginIdentitySelectorActivity.class);
        intent.putExtra(IDENTITY_SELECTOR_TYPE_KEY, IDENTITY_SELECTOR_TYPE_DASHBOARD);
        intent.putExtra(GO_TO_DASHBOARD_WHEN_CLOSE, startedFromDashboard);

        startActivityForResult(intent, 1010);

//        VodafoneController.getInstance().getDashboardActivity().finish();
        EBUSelectorDialogActivity.this.finish();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1010 && resultCode == 1010)//user pressed renunta button
            this.finish();
    }

    int getIdentitiesNumber() {
        if (EbuMigratedIdentityController.getInstance().getHasOneIdentity() == null)
            return 2;
        else
            return 1;
    }
    public void addCloseButtonIconInsideEditText(EditText searchField) {
        if(searchField==null){
            return;
        }
        Drawable image = getResources().getDrawable(R.drawable.close_icon);
        int h = 30;
        int w = 30;
        image.setBounds(0, 0, w, h);
        searchField.setCompoundDrawables(null, null, image, null);
        searchField.setOnTouchListener(clearIconButtonListner);
    }

    private void removeButtonIconInsideEditText(CustomEditTextCompat searchField) {
        if(searchField==null){
            return;
        }
        searchField.setCompoundDrawables(null,null,null,null);
        searchField.setOnTouchListener(null);
    }
    private View.OnTouchListener clearIconButtonListner = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            EditText searchField = (EditText) v;
            final int DRAWABLE_RIGHT = 2;
            System.out.println("Error on touch ");
            if (null != searchField.getCompoundDrawables()[DRAWABLE_RIGHT]) {

                if (event.getRawX() >= (searchField.getRight() - searchField.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    System.out.println("Clear Icon Ontouch");
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_UP:
                            searchField.getText().clear();
                            return false;
                    }
                }
            } else {
                System.out.println("Field on touch");
            }

            return onTouchEvent(event);
        }
    };

    public void callProfileHierarchyOnBanChangeOrClose(final boolean isOnClose){
            if(isOnClose){
                if(selectedBan != null && selectedBan.getNumber().equals(UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan()))
                    return;
                else if(selectedBan == null)
                    return;
            }
            new UserDataService(EBUSelectorDialogActivity.this)
                    .reloadSessionSubscriberHierarchy(UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan())
                    .subscribe(new RequestSaveRealmObserver<GeneralResponse<UserProfileHierarchy>>(){
                        @Override
                        public void onNext(GeneralResponse<UserProfileHierarchy> response) {
                            super.onNext(response);
                            if(response.getTransactionStatus() == 0 && !isOnClose){
                                UserSelectedMsisdnBanController.getInstance().
                                        setSelectedSubscriber(response.getTransactionSuccess().getBan().getSubscriberList().get(0));
                            }
                        }
                    });
    }

  @Override
    protected void onStart() {
        super.onStart();
        new AdobeTargetController().trackPage(this, AdobePageNamesConstants.PG_SELECTOR);
    }

}
