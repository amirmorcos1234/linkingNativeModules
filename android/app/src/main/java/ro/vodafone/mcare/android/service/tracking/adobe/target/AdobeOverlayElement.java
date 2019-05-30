package ro.vodafone.mcare.android.service.tracking.adobe.target;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.TargetElementsConstants;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.ui.webviews.WebviewActivity;
import ro.vodafone.mcare.android.utils.navigation.notification.LinkDispatcherActivity;
import rx.Subscriber;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static ro.vodafone.mcare.android.service.tracking.adobe.target.constants.TargetElementsConstants.OVERLAY_2;
import static ro.vodafone.mcare.android.service.tracking.adobe.target.constants.TargetElementsConstants.OVERLAY_2_UAT;

/**
 * Created by Prodan Pavel on 22.04.2018.
 */

public class AdobeOverlayElement extends AdobeBaseElement {
    private Dialog overlay;
    private LinearLayout contextLayout;

    public AdobeOverlayElement(AdobeTargetResponseElement adobeTargetResponseElement) {
        super(adobeTargetResponseElement);
    }

    @Override
    public void display() {
        initOverlayElement();
    }

    private void initOverlayElement() {
        overlay = new Dialog(VodafoneController.currentActivity(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        overlay.setContentView(R.layout.overlay_dialog_notifications);

        VodafoneTextView overlayTitle = overlay.findViewById(R.id.overlayTitle);
        VodafoneTextView overlaySubtext = overlay.findViewById(R.id.overlaySubtext);
        VodafoneButton buttonKeepOn = overlay.findViewById(R.id.buttonKeepOn);
        VodafoneButton buttonTurnOff = overlay.findViewById(R.id.buttonTurnOff);
        ImageView dismissButton = overlay.findViewById(R.id.overlayDismissButton);
        contextLayout = overlay.findViewById(R.id.viewExtraContent);

        overlayTitle.setText(adobeTargetResponseElement.getTitle());
        overlaySubtext.setText(adobeTargetResponseElement.getContent());
        buttonKeepOn.setText(adobeTargetResponseElement.getButton1label());
        buttonTurnOff.setText(adobeTargetResponseElement.getButton2label());

        setupButtonVisibility(adobeTargetResponseElement.isXButtonDisplayed(), dismissButton);
        setupButtonVisibility(adobeTargetResponseElement.isButton1Displayed(), buttonKeepOn);
        setupButtonVisibility(adobeTargetResponseElement.isButton2Displayed(), buttonTurnOff);

        setupButtonStyle(buttonKeepOn, adobeTargetResponseElement.isButton1TypePrimary());
        setupButtonStyle(buttonTurnOff, adobeTargetResponseElement.isButton2TypePrimary());

        buttonKeepOn.setOnClickListener(createButtonsOnClickListener(adobeTargetResponseElement.getButton1urltype(),
                adobeTargetResponseElement.getButton1action(),true));
        buttonTurnOff.setOnClickListener(createButtonsOnClickListener(adobeTargetResponseElement.getButton2urltype(),
                adobeTargetResponseElement.getButton2action(),true));
        dismissButton.setOnClickListener(dismissClickListener);

        Log.d("", "initOverlayElement: ");
        bindImageIfAvailable();

        overlay.show();
    }

    private void setupButtonVisibility(boolean isVisible, View view){
        if(isVisible)
            view.setVisibility(View.VISIBLE);
        else
            view.setVisibility(View.GONE);
    }

    private void setupButtonStyle(VodafoneButton button, boolean isPrimary){
        if(isPrimary) {
            button.setBackgroundDrawable(ContextCompat.getDrawable(VodafoneController.currentActivity(), R.drawable.selector_button_background_overlay_primary));
            button.setTextColor(ContextCompat.getColor(VodafoneController.currentActivity(), R.color.white_text_color));
        } else {
            button.setBackgroundDrawable(ContextCompat.getDrawable(VodafoneController.currentActivity(), R.drawable.selector_button_background_overlay_secondary));
            button.setTextColor(ContextCompat.getColor(VodafoneController.currentActivity(), R.color.dark_gray_text_color));
        }
    }

    private View.OnClickListener dismissClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            overlay.dismiss();
        }
    };

    private void bindImageIfAvailable(){
        String overlayType = TargetElementsConstants.getTargetElement();
        switch (overlayType){
            case OVERLAY_2:case OVERLAY_2_UAT:
                if(adobeTargetResponseElement.getBannerImage() != null && !adobeTargetResponseElement.getBannerImage().isEmpty()){
                    ImageView bannerImageView = createAndConfigureImageView();
                    Glide.with(VodafoneController.currentActivity())
                            .load(adobeTargetResponseElement.getBannerImage())
                            .dontTransform()
                            .into(bannerImageView);
                    contextLayout.addView(bannerImageView);

                    overlay.findViewById(R.id.overlaySubtext2).setVisibility(View.GONE);
                }
                break;
        }
    }

    private ImageView createAndConfigureImageView(){
        ImageView imageView = new ImageView(VodafoneController.currentActivity());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);

        imageView.setLayoutParams(params);
        imageView.setPadding(ScreenMeasure.dpToPx(1),0, ScreenMeasure.dpToPx(1), ScreenMeasure.dpToPx(10));
        imageView.setAdjustViewBounds(true);
        imageView.setOnClickListener(createButtonsOnClickListener(adobeTargetResponseElement.getBannerredirecturltype(),
                adobeTargetResponseElement.getBannerRedirectUrl(), false));

        return imageView;
    }

    private View.OnClickListener createButtonsOnClickListener(String urlType, String urlToRedirect, final boolean isFromButtonClick){
        Intent intent = null;

        if(isUrlTypeOrUrlValid(urlType) && isUrlTypeOrUrlValid(urlToRedirect)){
            switch (urlType){
                case "intern":
                    intent = new Intent(VodafoneController.currentActivity(), LinkDispatcherActivity.class)
                            .setData(Uri.parse(urlToRedirect));
                    break;
                case "extern":
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlToRedirect));
                    break;
                case "webview":
                    intent  = new Intent(VodafoneController.currentActivity(), WebviewActivity.class);
                    intent.putExtra(WebviewActivity.KEY_URL, urlToRedirect);
                    break;
                case "close":default:
                    intent = null;
                    break;
            }
        }

        final Intent finalIntent = intent;

        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(finalIntent != null){

                    VodafoneController.getCurrentActivityObservable().subscribe(new Subscriber<Activity>() {
                        @Override
                        public void onCompleted() {}

                        @Override
                        public void onError(Throwable e) {}

                        @Override
                        public void onNext(Activity activity) {
                            overlay.dismiss();
                            unsubscribe();
                        }
                    });

                    VodafoneController.currentActivity().startActivity(finalIntent);

                } else {
                    if(isFromButtonClick)
                        overlay.dismiss();
                }
            }
        };
    }

    private boolean isUrlTypeOrUrlValid(String urlTypeOrUrl){
        return urlTypeOrUrl!=null && !urlTypeOrUrl.isEmpty();
    }
}
