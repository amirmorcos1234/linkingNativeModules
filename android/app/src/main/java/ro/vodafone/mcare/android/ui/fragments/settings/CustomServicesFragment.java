package ro.vodafone.mcare.android.ui.fragments.settings;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.application.controllers.DashboardController;
import ro.vodafone.mcare.android.card.offers.GeneralCardsWithTitleBodyAndTwoButtons;
import ro.vodafone.mcare.android.client.model.profile.ProfileSuccess;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.hierarchy.Subscriber;
import ro.vodafone.mcare.android.client.model.realm.hierarchy.UserProfileHierarchy;
import ro.vodafone.mcare.android.client.model.realm.profile.Profile;
import ro.vodafone.mcare.android.client.model.realm.system.AppLabels;
import ro.vodafone.mcare.android.client.model.realm.system.SettingsLabels;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.client.model.users.postpaid.PostPaidUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.custom.CustomEditTextCompat;
import ro.vodafone.mcare.android.interfaces.OnErrorIconClickListener;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.service.UserDataService;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.DynamicColorImageView;
import ro.vodafone.mcare.android.ui.activities.settings.SettingsActivity;
import ro.vodafone.mcare.android.ui.header.NavigationHeader;
import ro.vodafone.mcare.android.ui.utils.CropActivity;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.views.TooltipError;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.PhotoUtils;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.support.annotation.Dimension.SP;
import static ro.vodafone.mcare.android.ui.activities.selectorDialogActivity.SelectorDialogActivity.RESULT_SELECTOR_UPDATED;

/**
 * Created by bogdan marica on 4/9/2017.
 */

public class CustomServicesFragment extends SettingsFragment implements OnErrorIconClickListener {

    public static final int MAX_CHARS = 15;
    static final String ALIAS_REGEX = "[a-zA-Z0-9 ]*";
    NavigationHeader navigationHeader;
    CustomEditTextCompat new_alias_field;
    RelativeLayout alias_container;
    VodafoneTextView alias_characters_count;
    int count = 0;
    Uri newFileUri;
    boolean isSelectPic;
    ViewGroup rootview;
    UserDataService userDataService;
    boolean callOnce;

    Subscriber selectedSubscriber = null;
    SettingsActivity activity;
    String alias;
    boolean unhandledError = false;
    boolean clickedOnce = true;
    boolean callOnce2 = true;
    TooltipError tooltipError;
    String mCurrentPhotoPath;

    boolean initialSetter;
    boolean goWithTheFlow;
    String beforeChange;

    boolean before;
    boolean after = true;
    String selectedMsisdn;
    boolean headerInited = false;
    private String PREFS_FILE_NAME;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isSettingsFragment = false;
        activity = (SettingsActivity) getActivity();
        userDataService = activity.getUserDataService();

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        rootview = container;
        if (callOnce2) {
            init();
            reloadProfileAndUpdateUi();
            callOnce2 = false;

        }
        return emptyLayout();

    }

    @Override
    public void makeAdobeRequest() {
        callForAdobeTarget(AdobePageNamesConstants.SETTINGS_PERSONALISE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        D.d(requestCode+" "+resultCode+ " data " + data);
        if (requestCode == 232) {
            return;
        }

        try {
            if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
                D.d("TAKE PIC");
                isSelectPic = false;

                Uri uri = Uri.fromFile(new File(mCurrentPhotoPath));
                if (uri == null)
                    uri = getUriFromDataBundleBitmap(data);

                Intent i = new Intent(getActivity(), CropActivity.class);
                i.putExtra("photoUri", uri);
                i.addFlags(FLAG_GRANT_READ_URI_PERMISSION);
                acceptUri(i,newFileUri);
                startSafeActivityForResult(i,151);
            } else if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
                D.d("SELECT PIC");
                isSelectPic = true;

                Uri uri = (Uri) data.getData();
                Intent i = new Intent(getActivity(), CropActivity.class);
                i.addFlags(FLAG_GRANT_READ_URI_PERMISSION);
                i.putExtra("photoUri", uri);
                acceptUri(i,newFileUri);
                startSafeActivityForResult(i, 151);

            } else if (requestCode == 151 && resultCode == Activity.RESULT_OK) {
                D.d("PIC EDITED");
                Bundle bundle = data.getExtras();
                newFileUri = bundle.getParcelable("imageUri");
                displayConfirmationLayout();
            } else if (requestCode == RESULT_SELECTOR_UPDATED && resultCode == RESULT_SELECTOR_UPDATED) {
                updateScreenOnSelectedSubscriberChanged();
            }
        } catch (Exception e) {
            D.e("EXCEPTION + " + e);
            e.printStackTrace();
        }
    }

    public boolean getHeaderInited() {
        return headerInited;
    }
    private void startSafeActivityForResult(Intent i, int requestCode){
        if(getActivity()!=null){
            i.addFlags(FLAG_GRANT_READ_URI_PERMISSION);
            try {
                startActivityForResult(i,requestCode);
            } catch (Exception e) {
                Log.d(TAG, "There are no Apps installed on this device to handle the intent!");
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        unsubscribeAll();
        clearHeader();

        try {
            navigationHeader.showBalance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        rootview.removeAllViews();
        ((SettingsActivity) getActivity()).setTitle();

        clearHeader();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        switch (requestCode) {
            case 3: {
                D.w();
                D.w("grantResults = " + Arrays.toString(grantResults));
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    D.w("permision aquired");
                    takePhoto();
                } else
                    D.e("permission not granted");
            }
            case 101: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    after = shouldShowRequestPermissionRationale(Manifest.permission.CAMERA);
                }
                return;
            }
            case 102: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startSafeActivityForResult(intent, 2);
                } else {
                    after = shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE);
                }
                return;
            }
        }
        D.w();
    }

    private void acceptUri(Intent i,Uri uri){
        if(uri==null || i==null){
            return;
        }
        //getActivity().grantUriPermission("ro.vodafone.mcare.android", uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        List<ResolveInfo> resInfoList = getActivity().getPackageManager().queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            getActivity().grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
    }
    void init() {

        selectedMsisdn = UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn();
        //rootview.addView(inflateLayout(true));
        intializeNavigationHeaderInfoLess();

        //Tealium Track View
        Map<String, Object> tealiumMapView = new HashMap(6);
        tealiumMapView.put("screen_name", "personalise your service");
        tealiumMapView.put("journey_name", "settings");
        tealiumMapView.put("user_type", VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
        TealiumHelper.trackView("screen_name", tealiumMapView);

        CustomServicesTrackingEvent event = new CustomServicesTrackingEvent();
        VodafoneController.getInstance().getTrackingService().track(event);
    }

    private void intializeNavigationHeaderInfoLess() {
        if (navigationHeader != null)
            navigationHeader.removeViewFromContainer();
        else
            navigationHeader = ((SettingsActivity) getActivity()).getNavigationHeader();

        navigationHeader.displayDefaultHeader();
        navigationHeader.hideSelectorView();
    }

    void reloadProfileAndUpdateUi() {
        showLoading();
        unhandledError = true;
        userDataService.getUserProfile(true).subscribe(new RequestSaveRealmObserver<GeneralResponse<Profile>>() {

            @Override
            public void onCompleted() {
                D.w();
            }

            @Override
            public void onNext(GeneralResponse<Profile> response) {
                unhandledError = false;
                stopLoadingAfterDuration(1);
                if (response.getTransactionStatus() == 0 && response.getTransactionSuccess() != null && response.getTransactionStatus() != 2) {
                    super.onNext(response);
                    VodafoneController.setApi10Failed(false);
                    if (getActivity() != null)
                        rootview.addView(inflateLayout(true));
                } else {
                    VodafoneController.setApi10Failed(true);
                    if (getActivity() != null)
                        rootview.addView(inflateErrorMessage());

                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                stopLoadingDialog();
                VodafoneController.setApi10Failed(true);
                if (unhandledError && getActivity() != null)
                    rootview.addView(inflateErrorMessage());
            }
        });
    }
    private void showLoading(){
        if(rootview!=null && getContext()!=null){
            rootview.removeAllViews();
            LinearLayout viewGroup = new LinearLayout(getContext());
            viewGroup.setBackgroundResource(R.color.general_background_light_gray);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            GeneralCardsWithTitleBodyAndTwoButtons loadingCard= new GeneralCardsWithTitleBodyAndTwoButtons(getContext());
            viewGroup.addView(loadingCard);
            rootview.addView(viewGroup,layoutParams);
            loadingCard.showLoading(true);
        }
    }

    public View inflateErrorMessage() {
        rootview.removeAllViews();
        clickedOnce = true;
        stopLoadingAfterDuration(1);

        if (getActivity() == null)
            return null;

        View v = View.inflate(getActivity(), R.layout.fragment_ebill_error, null);

        v.findViewById(R.id.refreshLayout).setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
        ((TextView) v.findViewById(R.id.electronicBillBody)).setText(SettingsLabels.getRetryButton());

        DynamicColorImageView error_circle = (DynamicColorImageView) v.findViewById(R.id.error_circle);
        error_circle.setDrawableColor(R.color.widget_warning_icon_color);

        VodafoneTextView electronicBillBody = (VodafoneTextView) v.findViewById(R.id.electronicBillBody);
        electronicBillBody.setText(SettingsLabels.getRetryButton());
        electronicBillBody.setTextSize(SP, 14);

        LinearLayout refreshLayout = (LinearLayout) v.findViewById(R.id.refreshLayout);
        refreshLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickedOnce) {
                    clickedOnce = false;
                    showLoadingDialog();
                    reloadProfileAndUpdateUi();
                }
            }
        });

        return v;
    }

    View emptyLayout() {
        View v = new View(getContext());
        v.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        return v;
    }

    public String getTitle() {
        return "Personalizează-ți conturile";
    }

    public void updateText() {

        int value = MAX_CHARS - count;
        alias_characters_count.setText(String.valueOf(value));

        if (value > 0)
            alias_characters_count.setTextColor(Color.CYAN);
        else
            alias_characters_count.setTextColor(Color.RED);

        alias_characters_count.setTypeface(Typeface.DEFAULT_BOLD);
    }

    public void updateTextWhenDefaultInserted(String msisdn) {
        count = msisdn.length() - 1;

        int value = MAX_CHARS - count;
        alias_characters_count.setText(String.valueOf(value));

        if (value > 0)
            alias_characters_count.setTextColor(Color.CYAN);
        else
            alias_characters_count.setTextColor(Color.RED);

        alias_characters_count.setTypeface(Typeface.DEFAULT_BOLD);

    }

    public void addNavigationView() {
        final View v = View.inflate(getContext(), R.layout.fragment_settings_custom_navigation, null);

        VodafoneButton changePicture = (VodafoneButton) v.findViewById(R.id.changePicture);

        changePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Tealium Track Event
                Map<String, Object> tealiumMapEvent = new HashMap(6);
                tealiumMapEvent.put("screen_name", "personalise your service");
                tealiumMapEvent.put("event_name", "mcare:personalise your service:button:schimba poza");
                tealiumMapEvent.put("user_type", VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
                TealiumHelper.trackEvent("event_name", tealiumMapEvent);

                final Dialog overlyDialog = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar);
                overlyDialog.setContentView(R.layout.custom_services_dialog);
                overlyDialog.getWindow().setBackgroundDrawableResource(R.color.black_opacity_25);

                DynamicColorImageView overlayDismissButton = (DynamicColorImageView) overlyDialog.findViewById(R.id.overlayDismissButton);
                overlayDismissButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        overlyDialog.dismiss();
                    }
                });

                selectedSubscriber = UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber();

                RelativeLayout layout = (RelativeLayout) overlyDialog.findViewById(R.id.mainLayout);
                TranslateAnimation slideUp = new TranslateAnimation(0, 0, 0, layout.getHeight());
                slideUp.setDuration(1000);
                slideUp.setFillAfter(true);
                layout.setAnimation(slideUp);

                Button newPic = (Button) overlyDialog.findViewById(R.id.newPic);
                newPic.setText(SettingsLabels.getNewPicButton());
                Button selectPic = (Button) overlyDialog.findViewById(R.id.selectPic);
                selectPic.setText(SettingsLabels.getSelectPicButton());
                Button resetPic = (Button) overlyDialog.findViewById(R.id.resetPic);
                resetPic.setText(SettingsLabels.getResetPicButton());

                Profile profile = (Profile) RealmManager.getRealmObject(Profile.class);
                if (profile != null) {
                    if (profile.getAvatarUrl() != null) {
                        if (profile.isTobe()) {
                            D.d("IS TOBE");
                            if (profile.getAvatarUrl().contains("tobe_default") || profile.getAvatarUrl() == null)
                                resetPic.setVisibility(View.GONE);
                            else if (profile.getAvatarUrl().contains("msisdn_default"))
                                resetPic.setVisibility(View.GONE);
                            else
                                resetPic.setVisibility(View.VISIBLE);
                        } else if (profile.isVMB()) {
                            D.d("IS VMB");
                            if (profile.getAvatarUrl().contains("vmb_default") || profile.getAvatarUrl() == null)
                                resetPic.setVisibility(View.GONE);
                            else if (profile.getAvatarUrl().contains("msisdn_default"))
                                resetPic.setVisibility(View.GONE);
                            else
                                resetPic.setVisibility(View.VISIBLE);
                        } else {
                            D.e("NOT TOBE  /  NOT VMB");
                            if (isDefaultAvatarNull())
                                resetPic.setVisibility(View.GONE);
                            else
                                resetPic.setVisibility(View.VISIBLE);
                        }
                    } else {
                        resetPic.setVisibility(View.VISIBLE);
                    }
                }


                overlyDialog.show();

                newPic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dispatchTakePictureIntent();
                        overlyDialog.dismiss();
                    }
                });


                selectPic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectPhoto();
                        overlyDialog.dismiss();
                    }
                });

                resetPic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        resetPicture();
                        overlyDialog.dismiss();
                    }
                });
            }
        });

        navigationHeader.removeViewFromContainer();
        navigationHeader.showTriangleView();
        if (VodafoneController.getInstance().getUser() instanceof PostPaidUser) {
            navigationHeader.setAlias();
        } else {
            try {
                String newAlias = ((Profile) RealmManager.getRealmObject(Profile.class)).getAlias();
//                String newAlias = profile.getAlias();
                navigationHeader.setAlias(newAlias);
            } catch (Exception e) {
                Log.d(TAG, "Profile is null :" + e);
                navigationHeader.setAlias();
            }
        }
        navigationHeader.setBottomMargin();
        navigationHeader.addViewToContainer(v);

    }

    private boolean isDefaultAvatarNull() {

        if(selectedSubscriber == null) {
            return true;
        }

        if(VodafoneController.getInstance().getUser() instanceof EbuMigrated){

            Profile profile = (Profile) RealmManager.getRealmObject(Profile.class);
            if((profile.getAvatarUrl() == null || profile.getAvatarUrl().contains("msisdn_default"))){
                return true;
            }
            else{
                return false;
            }
        }else if ((selectedSubscriber.getAvatarUrl()!= null && selectedSubscriber.getAvatarUrl().contains("msisdn_default")) || selectedSubscriber.getAvatarUrl() == null){
            return true;
        }

        return false;
    }

    public void updateScreenOnSelectedSubscriberChanged() {
        setAlias();
    }

    public Uri getUriFromDataBundleBitmap(Intent data) {
        Bundle bundle = data.getExtras();
        Object bundleObject = null;
        Uri uri;

        for (String key : bundle.keySet()) {
            bundleObject = bundle.get(key);
            break;
        }

        if (bundleObject instanceof Uri) {
            uri = (Uri) bundleObject;
            return uri;
        } else if (bundleObject instanceof Bitmap) {
            return Uri.parse(PhotoUtils.storeImage(getActivity().getContentResolver(), ((Bitmap) bundleObject), ("VDF_IMG_" + String.valueOf(System.currentTimeMillis())), ""));
        }

        return null;
    }

    public void displayConfirmationLayout() {
        navigationHeader.removeViewFromContainer();

        View v = View.inflate(getContext(), R.layout.fragment_settings_custom_navigation2, null);
        Button done = (Button) v.findViewById(R.id.doneButton);
        Button changePicButton = (Button) v.findViewById(R.id.changePicButton);


        if (isSelectPic)
            changePicButton.setText(SettingsLabels.getCustomServicesChangePicButtonChoose());
        else
            changePicButton.setText(SettingsLabels.getCustomServicesChangePicButtonNew());

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigationHeader.removeViewFromContainer();
                rootview.addView(inflateLayout(false));
                navigationHeader.setUsersIconFromUri(newFileUri);
                // new CustomToast.Builder(VodafoneController.currentActivity()).message("Ți-ai schimbat poza de profil.").success(true).show();
                try {
                    pictureSelected();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        changePicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isSelectPic)
                    selectPhoto();
                else
                    takePhoto();
            }
        });
        navigationHeader.addViewToContainer(v);

        inflateEmptyLayout();

        navigationHeader.removeTriangleView();
        navigationHeader.hideBalance();

        navigationHeader.hideAliasAndMsisdn();
        navigationHeader.hideArrowRight();
        navigationHeader.setUsersIconFromUri(newFileUri);


    }

    public void inflateEmptyLayout() {
        D.w();
        rootview.removeAllViews();
        rootview.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.transparent));
        // NavigationHeader.setSelectedSubscriber(selectedSubscriber.getMsisdn());
        UserSelectedMsisdnBanController.getInstance().setSelectedSubscriber(selectedSubscriber);
    }

    public View inflateLayout(boolean initHeader) {
        rootview.removeAllViews();
        headerInited = !initHeader;
        View v = View.inflate(getContext(), R.layout.fragment_settings_custom_services, null);
        v.findViewById(R.id.customSecondId).setVisibility(View.VISIBLE);

        if (initHeader) {
            if (navigationHeader != null)
                navigationHeader.removeViewFromContainer();
            else
                navigationHeader = ((SettingsActivity) getActivity()).getNavigationHeader();

            navigationHeader.displayDefaultHeader();
            navigationHeader.buildMsisdnSelectorHeader();
            navigationHeader.setDisplaySelector(true);
            navigationHeader.displaySelectorView();
        }
        navigationHeader.showAliasAndMsisdn();
        resetNavigationHeaderText();
        navigationHeader.showArrowRight();
        navigationHeader.hideBalance();
        checkIfAliasIsAvailable();

        if (selectedSubscriber != null) {
            D.w("Should display : " + selectedSubscriber.getMsisdn());
            D.w("Should display : " + selectedSubscriber.getAlias());
            // NavigationHeader.setSelectedSubscriber(selectedSubscriber.getMsisdn());
            UserSelectedMsisdnBanController.getInstance().setSelectedSubscriber(selectedSubscriber);
        }

        new_alias_field = (CustomEditTextCompat) v.findViewById(R.id.newServiceName);
        alias_container = (RelativeLayout) v.findViewById(R.id.relativeEditText);
        tooltipError = (TooltipError) v.findViewById(R.id.tooltipError);

        tooltipError.setText(R.string.register_error_message_invalid_characters_username);

        alias_characters_count = (VodafoneTextView) v.findViewById(R.id.charCount);
        new_alias_field.setDisplayBorder(true);

        initialSetter = true;

        new_alias_field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int length, int i1, int i2) {

                if (charSequence.length() == 15) {
                    beforeChange = charSequence.toString();
                    goWithTheFlow = false;
                } else
                    goWithTheFlow = true;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int length, int i1, int i2) {
                if (goWithTheFlow) {
                    if (charSequence.length() > 0) {    // we dont have null string

                        if (!charSequence.toString().matches(ALIAS_REGEX)) { //and string not matches pattern

                            String butLastChar = (charSequence.toString().substring(0, charSequence.length() - 1));

                            if (butLastChar.matches(ALIAS_REGEX)) {// butLastCharMatches means last char might be enter
                                if (charSequence.charAt(charSequence.length() - 1) == '\n') {
                                    onEnterPressed(KeyEvent.KEYCODE_ENTER);
                                } else
                                    //  displayEditTextError(alias_container, tooltipError, false); //DISPLAY ERROR - only our last char not matches


                                    displayEditTextErrorHideCounter(new_alias_field, alias_characters_count, tooltipError, false);
                            } else {
                                // displayEditTextError(alias_container, tooltipError, false);//DISPLAY ERROR - MORE CHARS NOT MATCHING
                                displayEditTextErrorHideCounter(new_alias_field, alias_characters_count, tooltipError, false);
                                if (charSequence.charAt(charSequence.length() - 1) == '\n') {//DO NOT DISPLAY \N AS A NEW ROW, REPLACE WITH ""
                                    new_alias_field.setText(charSequence.toString().replaceAll("\n", ""));
                                    new_alias_field.setSelection(new_alias_field.getText().length());
                                }
                            }
                        } else {
                            showDefaultEditText();

                        }
                        // displayEditTextError(alias_container, tooltipError, true);//HIDE ERROR

                        if (charSequence.charAt(charSequence.length() - 1) == '\n')
                            count = charSequence.length() - 1;
                        else
                            count = charSequence.length();
                    } else
//                        displayEditTextError(alias_container, tooltipError, true);
                        displayEditTextErrorHideCounter(new_alias_field, alias_characters_count, tooltipError, true);

                    updateText();
                } else {
                    if (charSequence.length() > 0 && charSequence.charAt(charSequence.length() - 1) == '\n')
                        onEnterPressed(KeyEvent.KEYCODE_ENTER);
                    else {
                        if (charSequence.length() > 0)
                            if (charSequence.length() > beforeChange.length()) {
                                new_alias_field.setText(beforeChange);
                                new_alias_field.setSelection(new_alias_field.getText().length());
                            } else {
                                count = charSequence.length();
                                updateText();
                            }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                initialSetter = false;
                if (editable.length() == 1 && editable.charAt(0) == '\n') {
                    updateTextWhenDefaultInserted(selectedMsisdn);
                }
            }
        });

        new_alias_field.setOnErrorIconClickListener(this);

        new_alias_field.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    callOnce = true;
                    alias_container.setBackgroundResource(R.drawable.onfocus_input_border);
                } else
                    alias_container.setBackgroundResource(R.drawable.gray_input_border);
            }
        });

        new_alias_field.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {


                return keyEvent.getAction() != KeyEvent.ACTION_DOWN || onEnterPressed(keyCode);


            }

        });

        setAlias();
        addNavigationView();
        if(VodafoneController.getInstance().getUser() instanceof EbuMigrated){
           navigationHeader.setDefaultAvatarWithUserDataAndNoIdentity();
           checkIfAliasIsAvailable();
        }
        return v;
    }

    private void checkIfAliasIsAvailable(){
        User user = VodafoneController.getInstance().getUser();
        String alias = null;
        Profile profile = (Profile) RealmManager.getRealmObject(Profile.class);

        if(user instanceof EbuMigrated) {
            if(profile != null){
                alias = profile.getAlias();
            }
        } else
            alias = UserSelectedMsisdnBanController.getInstance().getSubscriberAlias();

        if(alias == null || alias.equals(""))
            navigationHeader.getClientCodeLabel().setVisibility(View.INVISIBLE);
    }

    private void setAlias() {
        Subscriber selectedSubscriber = UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber() ;
        if(VodafoneController.getInstance().getUser() instanceof EbuMigrated){
            setAliasFieldFromProfile();
        }
        else if (selectedSubscriber!= null) {
            if (selectedSubscriber.getAlias() != null && !selectedSubscriber.getAlias().equals("")) {
                new_alias_field.setText(selectedSubscriber.getAliasWithout4Prefix());
            } else{
                new_alias_field.setText(selectedSubscriber.getMsisdnWithout4Prefix());
            }
        }else{
            setAliasFieldFromProfile();
        }
    }

    private void setAliasFieldFromProfile() {
        if(new_alias_field==null){
            return;
        }
        Profile profile = (Profile) RealmManager.getRealmObject(Profile.class);
        if (profile != null) {
            if (profile.getAlias() != null && !profile.getAlias().equals("")){
                new_alias_field.setText(((Profile) RealmManager.getRealmObject(Profile.class)).getAliasWithout4Prefix());
            }else{
                new_alias_field.setText(((Profile) RealmManager.getRealmObject(Profile.class)).getHomeMsisdnWithout4Prefix());
            }
        }
    }

    void resetNavigationHeaderText() {
        if (navigationHeader.getClientCodeLabel().getText().toString().equals("Cont Client"))
            navigationHeader.getClientCodeLabel().setText(navigationHeader.getClientCode().getText());
    }

    boolean onEnterPressed(int keyCode) {

        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            D.w("we got ENTER");

            if (validInput(new_alias_field.getText().toString())) {

                selectedMsisdn = VodafoneController.getInstance().getUser() instanceof EbuMigrated
                        ? UserSelectedMsisdnBanController.getInstance().getMsisdnFromUserDataProfile()
                        : UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn();

                alias = new_alias_field.getText().toString().trim();

                //TODO NEED TO CHECK NAVIGATION HEADER WITH MULTIPLE SUBSCRIBERS

                if (alias.equals(""))
                    alias = null;

                String type = "alias";

                showLoadingDialog();
                userDataService.postProfileAvatar(selectedMsisdn, alias, type, null).subscribe(new RequestSessionObserver<GeneralResponse<ProfileSuccess>>() {
                    @Override
                    public void onNext(GeneralResponse<ProfileSuccess> profileSuccessGeneralResponse) {
                        stopLoadingDialog();
                        if (profileSuccessGeneralResponse.getTransactionStatus() == 0) {

                            tryUpdateProfile(alias, null);
                            tryUpdateProfileHierarchy(alias, null);

                            navigationHeader.setAlias(alias, false);
                            checkIfAliasIsAvailable();

                            if (VodafoneController.currentActivity() != null) {
                                new CustomToast.Builder(VodafoneController.currentActivity()).message("Ți-ai schimbat numele serviciului.").success(true).show();
                            }
                        } else {
                            if (VodafoneController.currentActivity() != null) {
                                new CustomToast.Builder(VodafoneController.currentActivity()).message(AppLabels.getToastErrorMessage()).success(false).show();
                            }
                        }
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        D.w();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        stopLoadingDialog();
                        D.e("e = " + e);
                        if (getContext() != null)
                            new CustomToast.Builder(VodafoneController.currentActivity()).message(AppLabels.getToastErrorMessage()).success(false).show();

                    }
                });

                if (alias == null) {
                    //  new_alias_field.setVisibility(View.INVISIBLE);
                    if (selectedMsisdn.startsWith("4"))
                        new_alias_field.setText(selectedMsisdn.substring(1));
                    else
                        new_alias_field.setText(selectedMsisdn);

                    updateTextWhenDefaultInserted(selectedMsisdn);
                } else
                    new_alias_field.setText(alias);


                hideKeyboard();
                return true;
            } else
                // displayEditTextError(alias_container, tooltipError, false);
                displayEditTextErrorHideCounter(new_alias_field, alias_characters_count, tooltipError, false);
        }
        return false;
    }

    private void resetCurrentSubscriber(){
        UserSelectedMsisdnBanController.getInstance().resetSelectSubscriberAndBan();
    }

    void tryUpdateProfileHierarchy(String alias, String avatarUrl) {
        UserProfileHierarchy userProfileHierarchy = (UserProfileHierarchy) RealmManager.getRealmObject(UserProfileHierarchy.class);
        D.w();
        if (userProfileHierarchy != null) {
            RealmManager.startTransaction();

            int possition = -1;

            String selectedMsisdnNo4;
            if (selectedMsisdn.startsWith("4"))
                selectedMsisdnNo4 = selectedMsisdn.substring(1);
            else
                selectedMsisdnNo4 = selectedMsisdn;

            if (!(VodafoneController.getInstance().getUser() instanceof EbuMigrated)) {
                updateCebuUserProfileHierarchy(alias, avatarUrl, userProfileHierarchy, possition, selectedMsisdnNo4);
            }

        } else {

            //UserSelectedMsisdnBanController.getInstance().updateSelectedSubscriber(userProfileHierarchy.getSubscriberList());
        }
        resetCurrentSubscriber();
    }

    private void updateCebuUserProfileHierarchy(String alias, String avatarUrl, UserProfileHierarchy userProfileHierarchy, int possition, String selectedMsisdnNo4) {
        for (int i = 0; i < userProfileHierarchy.getSubscriberList().size(); i++) {
            if (userProfileHierarchy.getSubscriberList().get(i).getMsisdn().equals(selectedMsisdnNo4)
                    || userProfileHierarchy.getSubscriberList().get(i).getMsisdn().substring(1).equals(selectedMsisdnNo4))
                possition = i;
        }

        try {
            if (!"thisIsAhardcodedStringLongerThanSixteen".equals(alias))
                userProfileHierarchy.getSubscriberList().get(possition).setAlias(alias);
            if (avatarUrl != null)
                userProfileHierarchy.getSubscriberList().get(possition).setAvatarUrl(avatarUrl);
        } catch (NullPointerException npe) {
            D.e("weird case when userProfile is null");
        }
        RealmManager.update(userProfileHierarchy);
    }

    void tryUpdateProfile(String alias, String avatar) {
        Profile profile = (Profile) RealmManager.getRealmObject(Profile.class);
        RealmManager.startTransaction();
        try {
            if (!"thisIsAhardcodedStringLongerThanSixteen".equals(alias))
                profile.setAlias(alias);
            if (avatar != null)
                profile.setAvatarUrl(avatar);
        } catch (NullPointerException npe) {
            D.e("weird case when profile is null");
        }
        resetCurrentSubscriber();
        RealmManager.update(profile);
    }

    public void takePhoto() {
        if (checkAndRequestPermissions(Manifest.permission.CAMERA)) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e(TAG, ex.getMessage());

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(),
                        "ro.vodafone.mcare.android.provider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startSafeActivityForResult(takePictureIntent, 1);
            }
        }
    }

    boolean validInput(String charSequence) {

//        if (charSequence.length() == 15) {
//            new_alias_field.setText(charSequence.subSequence(0, charSequence.length() - 1));
////                    n=charSequence.subSequence(0,length-1);
//            return false;
//        }

        if (charSequence.length() > 0) {    // we dont have null string

            if (!charSequence.matches(ALIAS_REGEX)) { //and string not matches pattern

                String butLastChar = (charSequence.substring(0, charSequence.length() - 1));

                if (butLastChar.matches(ALIAS_REGEX)) {// butLastCharMatches means last char might be enter
                    //DISPLAY ERROR - only our last char not matches
                    return charSequence.charAt(charSequence.length() - 1) == '\n';
                } else {
                    if (charSequence.charAt(charSequence.length() - 1) == '\n') {//DO NOT DISPLAY \N AS A NEW ROW, REPLACE WITH ""
                        new_alias_field.setText(charSequence.replaceAll("\n", ""));
                        new_alias_field.setSelection(new_alias_field.getText().length());
                    }
                    return false;//DISPLAY ERROR - MORE CHARS NOT MATCHING

                }
            } else
                return true;//HIDE ERROR
        } else
            return true;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public boolean checkAndRequestPermissions(String permission) {

        int readExternal = ContextCompat.checkSelfPermission(getActivity(), permission);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (readExternal != PackageManager.PERMISSION_GRANTED)
            listPermissionsNeeded.add(permission);

        if (!listPermissionsNeeded.isEmpty()) {
            if (permission.equals(Manifest.permission.CAMERA))
                requestPermissions(listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 101);
            else
                requestPermissions(listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 102);

            return false;
        }
        return true;
    }

    public void selectPhoto() {
        if (checkAndRequestPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startSafeActivityForResult(intent, 2);
        }
    }

    public void resetPicture() {
        String selectedMsisdn;
        if(VodafoneController.getInstance().getUser() instanceof EbuMigrated) {
            selectedMsisdn = UserSelectedMsisdnBanController.getInstance().getMsisdnFromUserDataProfile();
        } else {
            selectedMsisdn = UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn() != null
                    ? UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn()
                    : UserSelectedMsisdnBanController.getInstance().getMsisdnFromUserDataProfile();
        }

        String type = "avatar";

        showLoadingDialog();
        userDataService.deleteAvatar(selectedMsisdn, type).subscribe(new RequestSessionObserver<GeneralResponse<ProfileSuccess>>() {
            @Override
            public void onNext(GeneralResponse<ProfileSuccess> profileSuccessGeneralResponse) {
                stopLoadingDialog();

                if (profileSuccessGeneralResponse.getTransactionStatus() == 0) {
                    if (profileSuccessGeneralResponse.getTransactionSuccess() != null) {
                        if (profileSuccessGeneralResponse.getTransactionSuccess().getAvatarUrl() != null) {
                            tryUpdateProfile("thisIsAhardcodedStringLongerThanSixteen", profileSuccessGeneralResponse.getTransactionSuccess().getAvatarUrl());
                            tryUpdateProfileHierarchy("thisIsAhardcodedStringLongerThanSixteen", profileSuccessGeneralResponse.getTransactionSuccess().getAvatarUrl());

                            navigationHeader.setAvatar(profileSuccessGeneralResponse.getTransactionSuccess().getAvatarUrl());
                            new CustomToast.Builder(VodafoneController.currentActivity()).message(SettingsLabels.getChangeAvatarPic()).success(true).show();
                            DashboardController.reloadDashboardOnResume();
                        }
                    }

                } else {
                    if (profileSuccessGeneralResponse.getTransactionFault() != null) {
                        new CustomToast.Builder(VodafoneController.currentActivity()).message(AppLabels.getToastErrorMessage()).success(false).show();
                        navigationHeader.setAvatar();
                    }
                }


            }

            @Override
            public void onCompleted() {
                super.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                stopLoadingDialog();
                e.printStackTrace();
                new CustomToast.Builder(VodafoneController.currentActivity()).message(AppLabels.getToastErrorMessage()).success(false).show();
            }
        });
    }

    public void pictureSelected() throws URISyntaxException {

        File file = resizedPicture(getPath(getContext(), newFileUri));// UrisyntaxException

        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse(getActivity().getContentResolver().getType(newFileUri)), file);

        MultipartBody.Part messagePart = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        boolean isMigrated  = VodafoneController.getInstance().getUser() instanceof EbuMigrated;
        final String selectedMsisdn = isMigrated ? VodafoneController.getInstance().getUserProfile().getMsisdn() : UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn();

        showLoadingDialog();
        userDataService.postProfileAvatar(selectedMsisdn, "", "avatar", messagePart).subscribe(new RequestSessionObserver<GeneralResponse<ProfileSuccess>>() {
            @Override
            public void onNext(GeneralResponse<ProfileSuccess> profileSuccessGeneralResponse) {
                stopLoadingDialog();

                if (profileSuccessGeneralResponse.getTransactionStatus() == 0) {
                    if (profileSuccessGeneralResponse.getTransactionSuccess() != null) {

                        if (profileSuccessGeneralResponse.getTransactionSuccess().getAvatarUrl() != null) {

                            Subscriber selectedSubscriber = UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber();


                            if (!(VodafoneController.getInstance().getUser() instanceof EbuMigrated) && selectedSubscriber != null) {
                                String avatarUrl = profileSuccessGeneralResponse.getTransactionSuccess().getAvatarUrl();
                                updateProfileAvatarForCBU(avatarUrl);
                            } else {
                                String avatarUrl = profileSuccessGeneralResponse.getTransactionSuccess().getAvatarUrl();
                                updateProfileAvatarForEBU(avatarUrl);
                            }

                            RealmManager.commitTransaction();
                            new CustomToast.Builder(VodafoneController.currentActivity()).message(SettingsLabels.getChangeAvatarPic()).success(true).show();
                        }
                    }

                } else {
                    if (profileSuccessGeneralResponse.getTransactionFault() != null) {
                        new CustomToast.Builder(VodafoneController.currentActivity()).message(AppLabels.getToastErrorMessage()).success(false).show();
                        navigationHeader.setAvatar();
                    }
                }
            }

            @Override
            public void onCompleted() {
                super.onCompleted();
                D.w();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                stopLoadingDialog();
                new CustomToast.Builder(VodafoneController.currentActivity()).message(AppLabels.getToastErrorMessage()).success(false).show();
                navigationHeader.setAvatar();
            }
        });

    }

    private void updateProfileAvatarForCBU(String avatarUrl) {
        RealmManager.startTransaction();
        Profile profile = (Profile) RealmManager.getRealmObject(Profile.class);
        profile.setAvatarUrl(avatarUrl);
        navigationHeader.setAlias(selectedSubscriber.getAlias());
        RealmManager.update(profile);
        selectedSubscriber.setAvatarUrl(avatarUrl);
    }

    private void updateProfileAvatarForEBU(String avatarUrl) {
        RealmManager.startTransaction();
        Profile profile = (Profile) RealmManager.getRealmObject(Profile.class);
        profile.setAvatarUrl(avatarUrl);
        navigationHeader.setAlias(profile.getAlias());
        RealmManager.update(profile);
    }

    public File resizedPicture(String filePath) throws URISyntaxException {

        Bitmap b = BitmapFactory.decodeFile(filePath);
        D.w("width  = " + b.getWidth());
        D.w("height = " + b.getHeight());

        int width = b.getWidth() >= 640 ? 640 : b.getWidth();
        int height = b.getHeight() >= 640 ? 640 : b.getHeight();

        D.w("width  = " + width);
        D.w("height = " + height);

        Bitmap b2 = Bitmap.createScaledBitmap(b, width, height, false);
        return new File(getPath(getContext(), Uri.parse(PhotoUtils.storeImage(getActivity().getContentResolver(), b2, ("VDF_IMG_" + String.valueOf(System.currentTimeMillis())), ""))));
    }

    void unsubscribeAll() {

    }

    private void clearHeader() {
        try {
            navigationHeader.hideSelectorViewWithoutTriangle();
            navigationHeader.removeViewFromContainer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = getActivity().getCurrentFocus();
        if (view == null) {
            view = new View(getActivity());
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        Observable.timer(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        ((SettingsActivity) getContext()).getToolbar().showToolBar();
                    }
                });

    }

    public void displayEditTextError(View target, TooltipError errorLayout, boolean visibility) {

        Context context = getContext() != null ? getContext() : VodafoneController.getInstance();
        if (context == null) {
            return;
        }
        if (!visibility)
            errorLayout.setVisibility(View.VISIBLE);
        else
            errorLayout.setVisibility(View.GONE);

        if (target.hasFocus())
            if (!visibility)
                target.setBackground(ContextCompat.getDrawable(context, R.drawable.red_error_input_border));
            else
                target.setBackground(ContextCompat.getDrawable(context, R.drawable.default_input_border));
        else
            target.setBackground(ContextCompat.getDrawable(context, R.drawable.default_input_border));
    }

    private void showDefaultEditText() {

        if (getContext() != null) {

            if (alias_container != null) {
                alias_container.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.default_input_border));
                alias_characters_count.setVisibility(View.VISIBLE);
                tooltipError.setVisibility(View.GONE);
            }

            if (new_alias_field != null)
                new_alias_field.setBackgroundresourceAndFieldIcon(R.drawable.default_input_border);

            if (tooltipError != null) tooltipError.setVisibility(View.GONE);

        }
    }

    public void displayEditTextErrorHideCounter(CustomEditTextCompat target, VodafoneTextView textView, LinearLayout errorLayout, boolean visibility) {

        if (!visibility)
            errorLayout.setVisibility(View.VISIBLE);
        else
            errorLayout.setVisibility(View.GONE);
        textView.setVisibility(View.GONE);

        if (target.hasFocus()) {
            if (!visibility) {
                target.setBackgroundresourceAndFieldIcon(R.drawable.red_error_input_border, R.drawable.error_icon);
            } else
                target.setBackgroundresourceAndFieldIcon(R.drawable.default_input_border);
        } else
            target.setBackgroundresourceAndFieldIcon(R.drawable.default_input_border);

    }

    private void dispatchTakePictureIntent() {
//        if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {//permisia nu e acordata
            D.e("permission not granted");
            before = shouldShowRequestPermissionRationale(Manifest.permission.CAMERA);
            if (isFirstTimeAskingPermission(getContext(), Manifest.permission.CAMERA)) {
                firstTimeAskingPermission(getContext(), Manifest.permission.CAMERA, false);
                displayCameraPermissionOverlay();
            } else if (!before && !after) {
                D.w("Checked Don't Ask Again Flag");
                displayGoToSettingsDialog();
            } else {
                D.d("Unchecked Don't Ask Again Flag");
                displayCameraPermissionOverlay();
            }

/*            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {
                D.e("DO NOT ASK WAS CHECKED GO TO SETTINGS AFTER ASKING");
                displayGoToSettingsDialog();
            }*/
        } else {//permisia e acordata
            takePhoto();
        }
//        else {//nu trebuie sa afisez permission dialog,trebuie sa merg la setari
//            D.e("DO NOT ASK WAS CHECKED GO TO SETTINGS AFTER ASKING");
//
//            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
//                displayGoToSettingsDialog();
//            else
//                takePhoto();
//        }
    }

    private void displayCameraPermissionOverlay() {
        final Dialog permissionDialog = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        permissionDialog.setContentView(R.layout.overlay_dialog_notifications);
        VodafoneTextView overlayTitle = (VodafoneTextView) permissionDialog.findViewById(R.id.overlayTitle);
        overlayTitle.setText(SettingsLabels.getCustomServicesOverlayTitle());
        VodafoneTextView overlaySubtext = (VodafoneTextView) permissionDialog.findViewById(R.id.overlaySubtext);
        overlaySubtext.setText(SettingsLabels.getCustomServicesOverlaySubtext());

        Button buttonActivate = (Button) permissionDialog.findViewById(R.id.buttonKeepOn);
        buttonActivate.setText(SettingsLabels.getCustomServicesButtonActivate());
        Button buttonRefuze = (Button) permissionDialog.findViewById(R.id.buttonTurnOff);
        buttonRefuze.setText(SettingsLabels.getCustomsServicesButtonRefuze());
        ImageView overlayDismissButton = (ImageView) permissionDialog.findViewById(R.id.overlayDismissButton);

        overlayDismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permissionDialog.dismiss();
            }
        });

        buttonRefuze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permissionDialog.dismiss();
            }
        });

        buttonActivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                D.w("granting");
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 3);
                permissionDialog.dismiss();
            }
        });

        permissionDialog.show();
    }

    public void displayGoToSettingsDialog() {

        final Dialog permissionDialog = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        permissionDialog.setContentView(R.layout.overlay_dialog_notifications);

        VodafoneTextView overlayTitle = (VodafoneTextView) permissionDialog.findViewById(R.id.overlayTitle);
        overlayTitle.setText(SettingsLabels.getCustomServicesOverlayTitle());//todo remove before commit
        VodafoneTextView overlaySubtext = (VodafoneTextView) permissionDialog.findViewById(R.id.overlaySubtext);
        overlaySubtext.setText(SettingsLabels.getCustomServicesCheckedFlagOverlaySubtext());

        Button buttonActivate = (Button) permissionDialog.findViewById(R.id.buttonKeepOn);
        buttonActivate.setText("OK");
        Button buttonRefuze = (Button) permissionDialog.findViewById(R.id.buttonTurnOff);
        buttonRefuze.setText("Mai tarziu");
        ImageView overlayDismissButton = (ImageView) permissionDialog.findViewById(R.id.overlayDismissButton);

        overlayDismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permissionDialog.dismiss();
            }
        });

        buttonRefuze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permissionDialog.dismiss();
            }
        });

        buttonRefuze.setVisibility(View.GONE);

        buttonActivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permissionDialog.dismiss();
            }
        });
        permissionDialog.show();
    }

    public String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
                cursor.close();
            } catch (Exception e) {
                // Eat it
                D.e("e = " + e);
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    private boolean isFirstTimeAskingPermission(Context context, String permission) {
        return context.getSharedPreferences(PREFS_FILE_NAME, MODE_PRIVATE).getBoolean(permission, true);
    }

    private void firstTimeAskingPermission(Context context, String permission, boolean isFirstTime) {
        SharedPreferences sharedPreference = context.getSharedPreferences(PREFS_FILE_NAME, MODE_PRIVATE);
        sharedPreference.edit().putBoolean(permission, isFirstTime).apply();
    }

    @Override
    public void onErrorIconClickListener() {
        if(new_alias_field.isErrorIconTap())
            displayEditTextErrorHideCounter(new_alias_field, alias_characters_count, tooltipError, true);
    }



    private static class CustomServicesTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "personalise your service";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "personalise your service");


            s.channel = "settings";
            s.getContextData().put("&&channel", s.channel);
        }
    }
}


