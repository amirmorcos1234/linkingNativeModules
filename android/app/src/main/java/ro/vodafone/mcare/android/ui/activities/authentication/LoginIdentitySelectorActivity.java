package ro.vodafone.mcare.android.ui.activities.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import retrofit2.adapter.rxjava.HttpException;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.application.controllers.DashboardController;
import ro.vodafone.mcare.android.application.controllers.GdprController;
import ro.vodafone.mcare.android.client.adapters.identity.IdentitySelectorAdapter;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneController;
import ro.vodafone.mcare.android.client.model.identity.BenSuccessEbu;
import ro.vodafone.mcare.android.client.model.identity.CustomerRestrictionsSuccess;
import ro.vodafone.mcare.android.client.model.identity.EntityChildItem;
import ro.vodafone.mcare.android.client.model.identity.EntityDetailsSuccess;
import ro.vodafone.mcare.android.client.model.identity.UserEntitiesSuccess;
import ro.vodafone.mcare.android.client.model.profile.ProfileSubscriptionSuccess;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.hierarchy.UserProfileHierarchy;
import ro.vodafone.mcare.android.client.model.realm.system.AppLabels;
import ro.vodafone.mcare.android.client.model.realm.system.LoginLabels;
import ro.vodafone.mcare.android.client.model.users.NonVodafoneUser;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.AuthorisedPersonUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.rest.ServiceGenerator;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.service.AuthenticationService;
import ro.vodafone.mcare.android.service.UserDataService;
import ro.vodafone.mcare.android.service.UserIdentitiesService;
import ro.vodafone.mcare.android.ui.activities.BaseActivity;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.ui.views.viewholders.identity.ExpandableListTree;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.EbuMigratedIdentityController;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;
import ro.vodafone.mcare.android.widget.dialogs.VodafoneDialog;
import rx.Observer;
import rx.Subscription;

import static ro.vodafone.mcare.android.ui.header.NavigationHeader.GO_TO_DASHBOARD_WHEN_CLOSE;

/**
 * Created by Victor Radulescu on 7/10/2017.
 */

public class LoginIdentitySelectorActivity extends BaseActivity {

    public final static String IDENTITY_SELECTOR_TYPE_LOGIN = "identitySelectorTypeLogin";
    public final static String IDENTITY_SELECTOR_TYPE_DASHBOARD = "identitySelectorTypeDashboard";
    public final static String IDENTITY_SELECTOR_TYPE_DEFAULT = IDENTITY_SELECTOR_TYPE_LOGIN;
    public final static String IDENTITY_SELECTOR_TYPE_KEY = "identitySelectorTypeKey";
    private final static String TAG = LoginIdentitySelectorActivity.class.getSimpleName();
    @BindView(R.id.identity_selector_recyclerview)
    RecyclerView recyclerView;

    @BindView(R.id.select_identity_title)
    VodafoneTextView selectorTitleTexView;

    @BindView(R.id.select_identity_button)
    VodafoneButton selectIdentityButton;

    @BindView(R.id.cancel_button)
    VodafoneButton cancelButton;

    @BindView(R.id.identity_selector_main_layout)
    RelativeLayout mainActivityLayout;

    @BindView(R.id.warning_textview)
    VodafoneTextView warningTextView;

    @BindView(R.id.no_identities_warning_layout)
    RelativeLayout noIdentitiesWarningLayout;
    boolean gotoDashboard = true;
    private RecyclerView.LayoutManager mLayoutManager;
    private VodafoneDialog vodafone2Dialog = null;
    private String selectedEntityId;
    private ExpandableListTree expandableListTreeIdentities;
    private String identitySelectorType = IDENTITY_SELECTOR_TYPE_LOGIN;
    private Realm realm;

    @OnClick(R.id.cancel_button)
    public void cancelAction() {
        if (IDENTITY_SELECTOR_TYPE_LOGIN.equals(identitySelectorType)) {
            loadLogOutDialog();
        } else if (IDENTITY_SELECTOR_TYPE_DASHBOARD.equals(identitySelectorType)) {
            if (gotoDashboard)
                new NavigationAction(this).startAction(IntentActionName.DASHBOARD, true);
            else {
                setResult(1010);
                finish();
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
        setContentView(R.layout.activity_identity_selector);
        ButterKnife.bind(this);
        setupBundleData();
        setVodafoneBackgroundOnWindowIfWindowBackgroundNotSetted();

        mainActivityLayout.setVisibility(View.INVISIBLE);

        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        selectIdentityButton.setEnabled(false);
        //TODO: remove hardcoded strings and add to labels
        selectorTitleTexView.setText("Selectează identitatea");
        selectIdentityButton.setText("Selectează identitatea");
        cancelButton.setText("Renunţă");
        setupIdentitySelector();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    private void setupBundleData() {
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            identitySelectorType = intent.getExtras().getString(IDENTITY_SELECTOR_TYPE_KEY, IDENTITY_SELECTOR_TYPE_DEFAULT);
            gotoDashboard = intent.getExtras().getBoolean(GO_TO_DASHBOARD_WHEN_CLOSE, false);

        }
    }

    @OnClick(R.id.select_identity_button)
    public void sendIdentity() {
        EbuMigratedIdentityController.getInstance().cleanEntityData();
        putSelectedIdentityId(selectedEntityId);
    }

    private void bindDataToAdapter(ExpandableListTree realmList, String defaultEntity) {
        // Bind adapter to recycler view object
        recyclerView.setAdapter(new IdentitySelectorAdapter(this, realmList,defaultEntity));
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    public void setEntityId(@Nullable String selectedEntityId) {
        this.selectedEntityId = selectedEntityId;

        if (selectedEntityId != null) {
            selectIdentityButton.setEnabled(true);
            Log.d(TAG, selectedEntityId);
        } else {
            selectIdentityButton.setEnabled(false);
        }
    }

    public String getSelectedEntityId() {
        return selectedEntityId;
    }

    private void setupIdentitySelector() {
        Log.d(TAG, "getUserIdentities: ");
        showLoadingDialog();
        UserIdentitiesService userIdentitiesService = new UserIdentitiesService(this);

        User user = VodafoneController.getInstance().getUser();
        if (user == null) {
            new CustomToast.Builder(LoginIdentitySelectorActivity.this).message(LoginLabels.getLoginFailedApiCall()).success(false).show();
            return;
        }

        String vfContactID = user.getUserProfile().getContactId();
        boolean vfEBUMigrated = user.getUserProfile().isMigrated();
        String vfSsoUserRole = user.getUserProfile().getUserRoleString();
        Log.d(TAG, "vfContactID = " + vfContactID);
        Log.d(TAG, "vfSsoUserRole = " + vfSsoUserRole);
        if(Objects.equals(identitySelectorType, IDENTITY_SELECTOR_TYPE_DASHBOARD)){
            showCacheIdentities();
        }else{
            userIdentitiesService.getUserDetails(vfContactID, vfEBUMigrated, vfSsoUserRole, null)
                    .subscribe(new Observer<GeneralResponse<UserEntitiesSuccess>>() {

                        @Override
                        public void onCompleted() {
                            Log.d(TAG, "getUserIdentities onCompleted");
                        }

                        @Override
                        public void onNext(GeneralResponse<UserEntitiesSuccess> response) {
                            //super.onNext(response);
                            if (response != null && response.getTransactionStatus() == 0) {
                                if (response.getTransactionSuccess() != null) {
                                    if (IDENTITY_SELECTOR_TYPE_LOGIN.equals(identitySelectorType)) {
                                        RequestSaveRealmObserver.save(response);
                                    }
                                    if (response.getTransactionSuccess() == null || response.getTransactionSuccess().getEntitiesList() == null
                                            || response.getTransactionSuccess().getEntitiesList().size() == 0) {
                                        //authenticate user as nonvodafone user
                                        //if there is no entity (role) for him
                                        noIdentitiesFound(response.getTransactionSuccess().getContactFirstName());
                                    } else {
                                        setupIdentityList(response.getTransactionSuccess());

                                    }

                                }
                            } else {
                                new CustomToast.Builder(LoginIdentitySelectorActivity.this).message(LoginLabels.getLoginFailedApiCall()).success(false).show();
                                logoutUserService();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d(TAG, e.getMessage());
                            stopLoadingDialog();
                            e.printStackTrace();

                            boolean isVersionInterceptorErrorCode = false;

                            try {
                                if(((HttpException)e).code() == ServiceGenerator.VERSION_INTERCEPTOR_ERROR_CODE)
									isVersionInterceptorErrorCode = true;
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }


                            if(!isVersionInterceptorErrorCode) {
                                new CustomToast.Builder(LoginIdentitySelectorActivity.this).message(LoginLabels.getLoginFailedApiCall()).success(false).show();
                                logoutUserService();
                            }
                        }
                    });
        }

    }

    private void setupIdentityList(UserEntitiesSuccess response) {
        RealmList<EntityChildItem> realmList = response.getEntitiesList();
        //if there is a single selectable identity then auto chose it for the user

        String singleIdentityId = searchForSingleIdentity();
        EbuMigratedIdentityController.getInstance().setHasOneIdentity(singleIdentityId);
        if (singleIdentityId != null) {
            putSelectedIdentityId(singleIdentityId);
        } else {
            expandableListTreeIdentities = new ExpandableListTree(realmList);
            //if default entity is not null
            //chose that entity for the user
            if (response.getDefaultEntity() != null) {

                if (IDENTITY_SELECTOR_TYPE_LOGIN.equals(identitySelectorType)) {
                    logWithDefaultIdentitiy(response.getDefaultEntity());
                } else if (IDENTITY_SELECTOR_TYPE_DASHBOARD.equals(identitySelectorType)) {
                    showAdapterElements(EbuMigratedIdentityController.getInstance().getSelectedIdentity().getEntityId());
                }
            }
            //else show the identity selector
            else {
                showAdapterElements(null);
            }

        }
    }

    private void showCacheIdentities(){
        UserEntitiesSuccess userEntitiesSuccess = (UserEntitiesSuccess) RealmManager.getRealmObject(realm, UserEntitiesSuccess.class);
        if(userEntitiesSuccess==null){
            //TODO throw exception
            return;
        }
        setupIdentityList(userEntitiesSuccess);
    }

    private void logWithDefaultIdentitiy(String defaultEntity) {
        putSelectedIdentityId(defaultEntity);
    }

    private void showAdapterElements(String defaultEntity) {
        stopLoadingDialog();
        if (mainActivityLayout != null) {
            mainActivityLayout.setVisibility(View.VISIBLE);
            bindDataToAdapter(expandableListTreeIdentities,defaultEntity);
        }
    }

    private void putSelectedIdentityId(final String selectedEntityId) {
        Log.d(TAG, "putSelectedIdentityId: ");
        showLoadingDialog();
        UserIdentitiesService userIdentitiesService = new UserIdentitiesService(this);

        User user = VodafoneController.getInstance().getUser();
        if (user == null) {
            enterDashboardErrorCase();
            return;
        }

        EbuMigratedIdentityController.getInstance().saveToRealmIdentity(selectedEntityId);
        getInverseHierarchyIfNeeded();

        addToActivityCompositeSubcription(userIdentitiesService.putDefaultUserIdentity(selectedEntityId)
                .subscribe(new RequestSessionObserver<GeneralResponse<UserEntitiesSuccess>>() {

                    @Override
                    public void onNext(GeneralResponse<UserEntitiesSuccess> userEntitiesSuccessGeneralResponse) {

                    }
                }));

    }

    private void enterDashboard() {
        Log.e(TAG, "enterDashboard ");
        UserSelectedMsisdnBanController.getInstance().resetSelectSubscriberAndBan();
        VoiceOfVodafoneController.getInstance().clearVoiceOfVodafone();

        if(VodafoneController.getInstance().getUser() == null) {
            return;
        }

        if(Objects.equals(identitySelectorType, IDENTITY_SELECTOR_TYPE_DASHBOARD)) DashboardController.reloadDashboardOnResume();
        //hide loading spinner

        if (!getProgressDialog().isShowing())
            showLoadingDialog();

        if (GdprController.shoudPerformGetPermissionsForEbuMigrated())
            GdprController.getPermissionsAfterLogin(false);

        stopLoadingDialog();
        new NavigationAction(this).startAction(IntentActionName.DASHBOARD, true);
//        finish();
    }

    private void showErrorMessage() {
        new CustomToast.Builder(this).message(AppLabels.getToastErrorSomeInfoNotLoaded()).success(false).show();

    }

    private void getInverseHierarchyIfNeeded() {
        EbuMigratedIdentityController controller = EbuMigratedIdentityController.getInstance();
        EntityChildItem selectedEntity = controller.getSelectedIdentity();

        if (selectedEntity == null) {
            enterDashboardErrorCase();
            return;
        }

        if (!(controller.isIdentityBillingCustomer() ||
                controller.isIdentityFinancialAccount() ||
                controller.isIdentitySubscriber())) {
            getFraudCheckIfNeeded(selectedEntity);
            return;
        }

        String entityId = selectedEntity.getEntityId();
        String entityType = selectedEntity.getEntityType();
        String vfOdsPhoneNumber = controller.isIdentitySubscriber()? selectedEntity.getVfOdsPhoneNumber():null;

        Subscription subscribe = new UserIdentitiesService(this).getInverseHierarchy(entityId,entityType,vfOdsPhoneNumber).subscribe(new RequestSaveRealmObserver<GeneralResponse<EntityChildItem>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                enterDashboardErrorCase();
            }

            @Override
            public void onNext(GeneralResponse<EntityChildItem> entityChildItemGeneralResponse) {
                if (entityChildItemGeneralResponse.getTransactionStatus() == 0 && entityChildItemGeneralResponse.getTransactionSuccess() != null) {
                    super.onNext(entityChildItemGeneralResponse);
                    EbuMigratedIdentityController.getInstance().updateCurrentEntityChildItem(entityChildItemGeneralResponse.getTransactionSuccess());
                    getFraudCheckIfNeeded(entityChildItemGeneralResponse.getTransactionSuccess());
                } else {
                    enterDashboardErrorCase();
                }
            }
        });
        addToActivityCompositeSubcription(subscribe);
    }

    private void getFraudCheckIfNeeded(final EntityChildItem entityChildItem) {
        EntityChildItem selectedEntity = EbuMigratedIdentityController.getInstance().getSelectedIdentity();
        if (selectedEntity == null || entityChildItem == null) {
            enterDashboardErrorCase();
            return;
        }
        String vfOdsSsn = entityChildItem.getVfOdsSsn();
        D.w("Get fraud check ");
        new UserIdentitiesService(this).getIdentityCostumerRestriction(vfOdsSsn).subscribe(new RequestSaveRealmObserver<GeneralResponse<CustomerRestrictionsSuccess>>() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                enterDashboardErrorCase();
            }

            @Override
            public void onNext(GeneralResponse<CustomerRestrictionsSuccess> response) {
                if (response.getTransactionStatus() == 0 && response.getTransactionSuccess() != null) {
                    super.onNext(response);
                    if(VodafoneController.getInstance().getUser() instanceof AuthorisedPersonUser) {
                        getBenAuthorisedPerson();
                    } else {
                        getUserProfileHierarchyIfNeeded();
                    }
                } else {
                    enterDashboardErrorCase();
                }
            }
        });
    }

    private void getBenAuthorisedPerson() {
        final EntityChildItem selectedEntity = EbuMigratedIdentityController.getInstance().getSelectedIdentity();
        String vfOdsBan = selectedEntity.getVfOdsBan();
        new UserIdentitiesService(this).getEbuBen(vfOdsBan).subscribe(new RequestSessionObserver<GeneralResponse<BenSuccessEbu>>() {
            @Override
            public void onNext(GeneralResponse<BenSuccessEbu> response) {
                if(response != null && response.getTransactionStatus() == 0 && response.getTransactionSuccess() != null &&
                        response.getTransactionSuccess().getVfOdsBen() != null) {
                    String vfOdsBen = response.getTransactionSuccess().getVfOdsBen();
                    Realm realm = Realm.getDefaultInstance();
                    selectedEntity.setVfOdsBen(vfOdsBen);
                    RealmManager.update(realm, selectedEntity);
                    realm.close();
                    getUserProfileHierarchyIfNeeded();
                } else {
                    enterDashboardErrorCase();
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                enterDashboardErrorCase();
            }
        });
    }

    private void getUserProfileHierarchyIfNeeded() {
        User user = VodafoneController.getInstance().getUser();
        String vfOdsBan = EbuMigratedIdentityController.getInstance().getSelectedIdentity().getVfOdsBan();
        String vfodsCid = EbuMigratedIdentityController.getInstance().getSelectedIdentity().getVfOdsCid();
//        enterDashboardEntityVerifiedCase();
        if (user instanceof EbuMigrated) {
            if (((EbuMigrated) user).isSubscriber()) {
                getUserDetails(vfodsCid);
            } else {
                if (EbuMigratedIdentityController.getInstance().isIdentityBillingCustomer()) {
                    List<EntityChildItem> entityChildItems = EbuMigratedIdentityController.getInstance().getSelectedIdentity().getChildList();
                    if (entityChildItems != null && !entityChildItems.isEmpty()) {
                        EntityChildItem firstChild = entityChildItems.get(0);
                        vfOdsBan = firstChild.getVfOdsBan();
                    }
                }
                final String finalVfodsCid = vfodsCid;
                new UserDataService(this).reloadSubscriberHierarchy(vfOdsBan).subscribe(
                        new RequestSaveRealmObserver<GeneralResponse<UserProfileHierarchy>>() {
                            @Override
                            public void onCompleted() {
                                super.onCompleted();
                                getUserDetails(finalVfodsCid);
                            }

                            @Override
                            public void onError(Throwable e) {
                                super.onError(e);
                                getUserDetails(finalVfodsCid);
                            }

                            @Override
                            public void onNext(GeneralResponse<UserProfileHierarchy> response) {
                                if (response.getTransactionStatus() == 0 && response.getTransactionSuccess() != null) {
                                    super.onNext(response);
                                }else{
                                    showErrorMessage();
                                }
                            }
                        });
            }
        }
    }

    private void getUserDetails(String vfodsCid) {

        addToActivityCompositeSubcription(new UserIdentitiesService(this).getUserDetails(vfodsCid).subscribe(new RequestSaveRealmObserver<GeneralResponse<EntityDetailsSuccess>>() {
            @Override
            public void onCompleted() {
                super.onCompleted();
                enterDashboardEntityVerifiedCase();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                enterDashboardEntityVerifiedCase();
            }

            @Override
            public void onNext(GeneralResponse<EntityDetailsSuccess> response) {
                if (response != null && response.getTransactionStatus() == 0 && response.getTransactionSuccess() != null) {
                    super.onNext(response);
                } else {
                    showErrorMessage();
                }
            }
        }));
    }

    private void enterDashboardErrorCase() {
        showErrorMessage();
        EbuMigratedIdentityController.setEbuMigratedUserFailedCase();
        checkVdfSubscriptionEnterDashboard();
    }
    private void enterDashboardErrorCaseJustNonVodafoneUser() {
        showErrorMessage();
        VodafoneController.getInstance().setUser(new NonVodafoneUser());
        enterDashboard();
    }

    private void enterDashboardEntityVerifiedCase() {
        if(VodafoneController.getInstance().getUser() == null) {
            return;
        }
        ((EbuMigrated) VodafoneController.getInstance().getUser()).setEntityVerifyed(true);
        checkVdfSubscriptionEnterDashboard();
    }

    private String searchForSingleIdentity() {

        RealmResults<EntityChildItem> realmResults = RealmManager.getAllRealmObject(realm, EntityChildItem.class);
        int counter = 0;
        String entityId = null;
        EntityChildItem rootEntityIfIsSingleChildItem = EbuMigratedIdentityController.getInstance().getRootEntityIfIsSingle();
        if (rootEntityIfIsSingleChildItem != null) {
            return rootEntityIfIsSingleChildItem.getEntityId();
        }
        for (int j = 0; j < realmResults.size(); j++) {
            Log.d(TAG, "Realmresults: " + realmResults.get(j).getEntityId());
            String type = realmResults.get(j).getEntityType();
            if (type != null && (type.equals("Subscriber") || type.equals("BillingCustomer"))) {
                counter++;
                entityId = realmResults.get(j).getEntityId();
            }
            if (counter > 1) {
                return null;
            }
        }

        if (counter == 1)
            return entityId;

        return null;
    }

    private void checkVdfSubscriptionEnterDashboard() {
        User user = VodafoneController.getInstance().getUser();
        if(!(((EbuMigrated)user).isAuthorisedPerson()
                || ((EbuMigrated)user).isChooser()
                || ((EbuMigrated)user).isDelagatedChooser())){
            enterDashboard();
            return;
        }

        String msisdn = user.getUserProfile().getMsisdn();

        UserDataService userDataService = new UserDataService(this);
        userDataService.getVdfSubscription(msisdn).subscribe(new RequestSaveRealmObserver<GeneralResponse<ProfileSubscriptionSuccess>>() {
            @Override
            public void onNext(GeneralResponse<ProfileSubscriptionSuccess> response) {
                super.onNext(response);
                enterDashboard();
            }

            @Override
            public void onCompleted() {
                super.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                enterDashboard();
            }
        });
    }

    private void noIdentitiesFound(String contactFirstName) {
        stopLoadingDialog();
        warningTextView.setText("Contul cu care te-ai autentificat nu este asociat unui client Vodafone");
        if (contactFirstName != null) {
            selectorTitleTexView.setText("Bine ai venit, " + contactFirstName);
        } else {
            selectorTitleTexView.setText("Bine ai venit");
        }

        recyclerView.setVisibility(View.GONE);
        mainActivityLayout.setVisibility(View.VISIBLE);
        noIdentitiesWarningLayout.setVisibility(View.VISIBLE);

        selectIdentityButton.setEnabled(true);
        selectIdentityButton.setText("Continuă");
        selectIdentityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterDashboardErrorCaseJustNonVodafoneUser();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (IDENTITY_SELECTOR_TYPE_LOGIN == identitySelectorType) {
            onBackPressedLoginType();
        } else if (IDENTITY_SELECTOR_TYPE_DASHBOARD == identitySelectorType) {
            onBackPressedLoginType();
        }
    }


    void loadLogOutDialog() {
        vodafone2Dialog = new VodafoneDialog(this, getString(R.string.activate_account_dialog_logout_message))
                .setPositiveMessage(getString(R.string.activate_account_dialog_logout_negative))
                .setNegativeMessage(getString(R.string.activate_account_dialog_logout_positive))
                .setPositiveAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vodafone2Dialog.dismiss();
                    }
                })
                .setNegativeAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clearActivityCompositeSubscription();
                        vodafone2Dialog.dismiss();
                        logoutUserService();
                    }
                });
        vodafone2Dialog.setCancelable(true);
        vodafone2Dialog.show();
    }


    private void onBackPressedLoginType() {
        loadLogOutDialog();
    }

    private void onBackPressedDashboardType() {
        LoginIdentitySelectorActivity.this.finish();
    }

}
