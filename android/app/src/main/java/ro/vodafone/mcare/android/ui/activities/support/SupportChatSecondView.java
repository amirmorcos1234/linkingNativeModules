package ro.vodafone.mcare.android.ui.activities.support;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.adapters.basic.RecyclerElementsAdapter;
import ro.vodafone.mcare.android.interfaces.factory.InterfaceButtonLayoutFactory;
import ro.vodafone.mcare.android.interfaces.factory.InterfaceTextViewLayoutFactory;
import ro.vodafone.mcare.android.presenter.adapterelements.base.AdapterElement;
import ro.vodafone.mcare.android.presenter.adapterelements.base.DynamicAdapterElement;
import ro.vodafone.mcare.android.presenter.adapterelements.base.ViewStaticAdapterElement;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.ui.utils.Fonts;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.TealiumHelper;

import static ro.vodafone.mcare.android.client.adapters.basic.RecyclerElementsAdapter.BUTTON_TYPE;
import static ro.vodafone.mcare.android.client.adapters.basic.RecyclerElementsAdapter.LIST_TYPE;
import static ro.vodafone.mcare.android.client.adapters.basic.RecyclerElementsAdapter.TEXT_TYPE;

/**
 * Created by cristi on 24/06/2017.
 * .
 */

public class SupportChatSecondView extends MyChatView
        implements InterfaceTextViewLayoutFactory,
        InterfaceButtonLayoutFactory {

    final SupportWindow window;
    int infoSelectedIndex;
    JsonList displayedInfo;

    RecyclerView chatRecyclerView;
    LinearLayout textViewLayout;
    LinearLayout buttonViewLayout;

    public SupportChatSecondView(@NonNull SupportWindow w, int selectedIndex) {
        super(w.getContext());
        this.window = w;
        this.infoSelectedIndex = selectedIndex;

        initViews(selectedIndex);
        initRecyclerView();
        runTracking();
    }

    void initViews(int selectedIndex) {
        try {
            displayedInfo = window.mJsonLists.get(selectedIndex);
        } catch (Exception e) {
            D.e("e = " + e.getMessage());
            displayedInfo = window.mJsonLists.get(0);
        }

        View v = LayoutInflater.from(getContext()).inflate(R.layout.fragment_support_chat_second, this);

        window.views.scrollView.setEnableScrolling(false);
        window.views.scrollView.setVerticalScrollBarEnabled(false);
        window.views.scrollView.setEnabled(false);
        window.hideFaqSearchButton();

        chatRecyclerView = (RecyclerView) v.findViewById(R.id.nseListView);
    }

    void initRecyclerView() {

        HashMap<Integer, AdapterElement> elementsHashMap = new HashMap<>();
        DynamicAdapterElement faqItemElement = new ChatElement(LIST_TYPE, 0, getContext(), getListFromJson());
//        DynamicAdapterElement textItemElement = new ChatElement(TEXT_TYPE, 1, getContext(), getListFromJson());
//        DynamicAdapterElement buttonItemElement = new ChatElement(BUTTON_TYPE, 2, getContext(), getListFromJson());
        elementsHashMap.put(LIST_TYPE, faqItemElement);
        elementsHashMap.put(TEXT_TYPE, getTextItemElement());
        elementsHashMap.put(BUTTON_TYPE, getButtonItemElement());


        RecyclerElementsAdapter recyclerElementsAdapter = new RecyclerElementsAdapter(VodafoneController.currentActivity(), elementsHashMap);
        recyclerElementsAdapter.setNumberOfDynamicElements(1);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(VodafoneController.currentActivity(), LinearLayoutManager.VERTICAL, false);

        chatRecyclerView.setAdapter(recyclerElementsAdapter);
        chatRecyclerView.setLayoutManager(mLayoutManager);
    }

    List<JsonItem> getListFromJson() {

//        D.d("ITEMS  LIST");
//        D.showList(displayedInfo.getItems());

        return displayedInfo.getItems();
    }

    public ViewStaticAdapterElement getTextItemElement() {
        return new ViewStaticAdapterElement(TEXT_TYPE, 1, getTextViewLayout());
    }

    public ViewStaticAdapterElement getButtonItemElement() {
        return new ViewStaticAdapterElement(BUTTON_TYPE, 2, getButtonViewLayout());
    }

    @Override
    public LinearLayout getButtonViewLayout() {

        if (buttonViewLayout == null) {
            buttonViewLayout = new LinearLayout(VodafoneController.currentActivity());
            int bigPadding = ScreenMeasure.dpToPx(16);
            int sideMargins = ScreenMeasure.dpToPx(12);
            buttonViewLayout.setPadding(bigPadding, bigPadding, bigPadding, bigPadding);

            VodafoneButton   button = new VodafoneButton(getContext(), null, R.style.CardPrimaryButton);
            button.setTransformationMethod(null);
            button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
            button.setTextColor(ContextCompat.getColor(getContext(), R.color.white_text_color));
            button.setTypeface(Fonts.getVodafoneRG());
            button.setBackgroundResource(R.drawable.selector_button_background_card_primary);
            button.setGravity(Gravity.CENTER);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            buttonViewLayout.setLayoutParams(params);
            params.setMargins(0, sideMargins, 0, sideMargins);
            button.setLayoutParams(params);

            button.setMinimumHeight(ScreenMeasure.dpToPx(40));

            button.setText("Începe conversația");

            button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Map<String, Object> tealiumMapEvent = new HashMap(6);
                    tealiumMapEvent.put("screen_name", "chat session");
                    tealiumMapEvent.put("event_name", "mcare:chat session:button:contacteaza-ne prin email");
                    tealiumMapEvent.put("user_type", VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
                    TealiumHelper.trackEvent("event_name", tealiumMapEvent);

                    window.setChatSecondView(false);
                    window.setSecondViewIndex(-1);
                    window.currentView = new SlyceMessagingView(window);
                    window.views.scrollView.setEnableScrolling(true);
                    window.inflateLayout();
                }
            });

            buttonViewLayout.addView(button);
        }
        return buttonViewLayout;
    }

    @Override
    public LinearLayout getTextViewLayout() {
        if (textViewLayout == null) {
            textViewLayout = new LinearLayout(VodafoneController.currentActivity());

            int bigPadding = ScreenMeasure.dpToPx(16);
            textViewLayout.setPadding(bigPadding, bigPadding, bigPadding, bigPadding);

            TextView tv = new TextView(VodafoneController.currentActivity());
            tv.setText("Nu ai găsit informația de care ai nevoie în întrebările de mai sus?\n\nAtunci click pe butonul de mai jos pentru a intra în legatură cu unul dintre consultanții noştri.");
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
            tv.setTextColor(ContextCompat.getColor(VodafoneController.currentActivity(), R.color.blackNormal));

            textViewLayout.addView(tv);
        }
        return textViewLayout;
    }

    void runTracking() {
        //Tealium Track View
        Map<String, Object> tealiumMapView = new HashMap(6);
        tealiumMapView.put("screen_name", "chat faq");
        tealiumMapView.put("journey_name", "help&support");
        tealiumMapView.put("user_type", VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
        TealiumHelper.trackView("screen_name", tealiumMapView);

        ChatFaqTrackingEvent event = new ChatFaqTrackingEvent();
        VodafoneController.getInstance().getTrackingService().track(event);
    }

    private static class ChatFaqTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "chat faq";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "chat faq");
            s.pageType = TrackingEvent.PAGE_TYPE_SELF_SERVICE;
            s.getContextData().put(TrackingVariable.P_PAGE_TYPE, TrackingEvent.PAGE_TYPE_SELF_SERVICE);
            s.channel = "help&support";
            s.getContextData().put("&&channel", s.channel);
        }
    }
}
