package ro.vodafone.mcare.android.ui.activities.selectorDialogActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.RealmObject;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.realm.hierarchy.Ban;
import ro.vodafone.mcare.android.client.model.realm.hierarchy.Subscriber;
import ro.vodafone.mcare.android.client.model.realm.hierarchy.UserProfileHierarchy;
import ro.vodafone.mcare.android.client.model.vodafoneTv.TvHierarchy;
import ro.vodafone.mcare.android.client.model.vodafoneTv.TvHierarchyResponse;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.activities.BaseActivity;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;


/**
 * Created by Alex on 2/2/2017.
 */
public class SelectorDialogActivity extends BaseActivity {


    private static final String SELECTOR_VARIABLE_KEY = "selector_variable_key";
    public static String TAG = "SelectorDialogActivity";

    public static String SELECT_TYPE_BAN = "ban";
    public static String SELECT_TYPE_MSISDN = "msisdn";

    public static int RESULT_SELECTOR_UPDATED = 9;

    List<Ban> banList = new ArrayList<>();
    List<Subscriber> subscriberList = new ArrayList<>();
    List<TvHierarchy> fixedNetSubscribtion = new ArrayList<>();

    List<Object> copyForSearchOfList;
    List<? extends RealmObject> items;

    Ban selectedBan;

    String selectedBanNumber;
    String selectedType;

    RelativeLayout layoutBackground;
    LinearLayout serviceSelectorContainer;

    EditText searchField;

    ImageView searchButton;
    ImageView closeButton;

    ListView selectorListView;

    int currentSelectedPosition = -1;

    boolean searchToggle = true;

    SelectorAdapter selectorListAdadapter;

    String vodafoneTvSelector;

    private static Object selectedObject;


    public List<Ban> getBanList() {
        return banList;
    }

    public Ban getSelectedBan() {
        return selectedBan;
    }

    public void setSelectedBan(Ban selectedBan) {
        this.selectedBan = selectedBan;
    }

    public List<Subscriber> getSubscriberList() {
        return subscriberList;
    }

    public void setSelectedBanNumber(String selectedBanNumber) {
        this.selectedBanNumber = selectedBanNumber;
    }

    public List<TvHierarchy> getFixedNetSubscribtion() {
        return fixedNetSubscribtion;
    }

    public void setFixedNetSubscribtion(List<TvHierarchy> fixedNetSubscribtion) {
        this.fixedNetSubscribtion = fixedNetSubscribtion;
    }

    View.OnClickListener searchButtonListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "Search button Pressed");
            if (searchToggle) {
                searchToggle = !searchToggle;
                layoutBackground.setVisibility(View.VISIBLE);
            } else {
                searchToggle = !searchToggle;
                layoutBackground.setVisibility(View.GONE);
            }
        }
    };

    View.OnClickListener closeButtonListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "Close button Pressed");
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();
        }
    };

    private View.OnTouchListener clearIconButtonListner = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
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

    private TextWatcher searchFieldTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().equals("")) {
                // reset listview
                copyForSearchOfList.clear();
                copyForSearchOfList.addAll(items);
                selectorListAdadapter.notifyDataSetChanged();
            } else {
                // perform search
                searchItem(s.toString());
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    public List<RealmObject> getBanListNoPrefix() {
        List<Ban> itemsLocal = RealmManager.getDefaultInstance().copyFromRealm(getBanList());
        List<RealmObject> result = new ArrayList();
        result.addAll(itemsLocal);

        return result;

    }
    public List<RealmObject> getSubscriberListNoPrefix() {
        List<Subscriber> itemsLocal = RealmManager.getDefaultInstance().copyFromRealm(getSubscriberList());
        List<RealmObject> result = new ArrayList();
//        for (int i = 0; i < itemsLocal.size(); i++) {
//            final Subscriber func =  itemsLocal.get(i);
//            if (func.getMsisdn().substring(0, 1).equals("4")) {
//                func.setMsisdn(func.getMsisdn().substring(1));
//            }
//            result.add(func);
//        }
//        initFixedNetSubscriptions();
//        if(!getFixedNetSubscribtion().isEmpty())
//            result.addAll(getFixedNetSubscribtion());

        result.addAll(itemsLocal);

        return result;
    }

    public List<RealmObject> getFixedNetSubscriptionNoPrefix() {
        List<TvHierarchy> itemsLocal = RealmManager.getDefaultInstance().copyFromRealm(getFixedNetSubscribtion());
        List<RealmObject> result = new ArrayList();
        result.addAll(itemsLocal);
        return result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        setContentView(R.layout.select_dialog_activity);

        serviceSelectorContainer = (LinearLayout) findViewById(R.id.service_selector_container);

        layoutBackground = (RelativeLayout) findViewById(R.id.select_dialog_search_container);

        searchField = (EditText) findViewById(R.id.select_dialog_search_input);
        searchField.setBackgroundResource(R.drawable.onfocus_input_border);

        selectorListView = (ListView) findViewById(R.id.selector_list);
        selectorListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        searchButton = (ImageView) findViewById(R.id.select_dialog_search_image);
        searchButton.setOnClickListener(searchButtonListner);

        closeButton = (ImageView) findViewById(R.id.close_dialog_search_image);
        closeButton.setOnClickListener(closeButtonListner);

        Intent i = getIntent();

        selectedType = i.getStringExtra(SELECTOR_VARIABLE_KEY);
        vodafoneTvSelector = i.getStringExtra("VodafoneTvSelector");

        setItemsList();//msisdn list ok

        copyForSearchOfList = new ArrayList<>();
        copyForSearchOfList.addAll(items);

        //if tv selector
        if (vodafoneTvSelector != null && !vodafoneTvSelector.isEmpty()) {
            initFixedNetSubscriptions();
            if (fixedNetSubscribtion != null && !fixedNetSubscribtion.isEmpty()) {
                appendFixedNetSubscriptions();
            }
            getCurrentlySelectedItemTvSelector();
        } //if generic selector
        else {
            getCurrentlySelectedItem();
        }

        if (selectedType.equals(SELECT_TYPE_MSISDN)) {
            sortSubscriberList(copyForSearchOfList);
            for (Object object : copyForSearchOfList) {//todo ????
                if (object instanceof Subscriber) {
                    Log.d(TAG, "alias -" + ((Subscriber) object).getAlias());
                    Log.d(TAG, "msisdn - " + ((Subscriber) object).getMsisdn());
                }
            }
        }


        if (currentSelectedPosition != -1) {
            setSelectedElementAtFirstPosition();
        }

        initListAdapter();
        displayServiceSelector();

        searchField.addTextChangedListener(searchFieldTextWatcher);

        addButtonIconInsideEditText();
        searchField.setOnTouchListener(clearIconButtonListner);

        trackView();
    }

    private void appendFixedNetSubscriptions() {
        if (!getFixedNetSubscriptionNoPrefix().isEmpty()) {
            List<RealmObject> result = new ArrayList();
            items.clear();
            result.addAll(getFixedNetSubscriptionNoPrefix());
            items = result;
            copyForSearchOfList.clear();
            copyForSearchOfList.addAll(items);
        }
    }

    private void getCurrentlySelectedItemTvSelector() {
        //to handle changes from other service selectors
        if ((selectedObject == null && !UserSelectedMsisdnBanController.getInstance().tvServiceSelector())
                || (selectedObject instanceof Subscriber && !((Subscriber) selectedObject).getMsisdn().equals(UserSelectedMsisdnBanController.getInstance().getTvSelectedMsisdn()))
                || (UserSelectedMsisdnBanController.getInstance().getTvSelectedMsisdn() != null)
            //    || (selectedObject instanceof TvHierarchy && !((TvHierarchy) selectedObject).getServiceId().equals(UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber().getMsisdn()))
        ) {
            //get selected subscriber if selected from tv
            Subscriber s = UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber(UserSelectedMsisdnBanController.getInstance().getTvSelectedMsisdn());
            currentSelectedPosition = getObjectIndex(s);
            selectedObject = s;
        } else {
            currentSelectedPosition = getObjectIndex(selectedObject);
        }

    }

    private int getObjectIndex(Object o) {
        for (int index = 0; index < copyForSearchOfList.size(); index++) {
            if (o instanceof Subscriber) {
                if(copyForSearchOfList.get(index) instanceof  Subscriber &&
                        ((Subscriber) copyForSearchOfList.get(index)).getMsisdn().equals(((Subscriber) o).getMsisdn())) {
                    return index;
                }
               else  if (copyForSearchOfList.get(index) instanceof  TvHierarchy &&
                        ((TvHierarchy) copyForSearchOfList.get(index)).getServiceId().equals(((Subscriber) o).getMsisdn()))
                    return index;
            }
            if (o instanceof TvHierarchy) {
                if(copyForSearchOfList.get(index) instanceof  Subscriber &&
                        ((Subscriber) copyForSearchOfList.get(index)).getMsisdn().equals(((TvHierarchy) o).getServiceId())) {
                    return index;
                }
                if (copyForSearchOfList.get(index) instanceof  TvHierarchy &&
                        ((TvHierarchy) copyForSearchOfList.get(index)).getServiceId().equals(((TvHierarchy) o).getServiceId()))
                    return index;
            }
        }
        return -1;
    }

    /*
     *Tealium Track View
     */
    private void trackView() {
        Map<String, Object> tealiumMapView = new HashMap(6);
        tealiumMapView.put("screen_name", "service selector");
        tealiumMapView.put("journey_name", "service selector");
        tealiumMapView.put("user_type", VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
        TealiumHelper.trackView("screen_name", tealiumMapView);

        SelectorTrackingEvent event = new SelectorTrackingEvent();
        VodafoneController.getInstance().getTrackingService().track(event);
    }

    private static class SelectorTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "service selector";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "service selector");


            s.channel = "service selector";
            s.getContextData().put("&&channel", s.channel);
        }
    }

    private void initBanList() {
        UserProfileHierarchy userProfileHierarchy = (UserProfileHierarchy) RealmManager.getRealmObject(UserProfileHierarchy.class);
        if (userProfileHierarchy != null) {
            banList = userProfileHierarchy.getBanList();
        }
    }

    private void initSubscriberList() {
        UserProfileHierarchy userProfileHierarchy = (UserProfileHierarchy) RealmManager.getRealmObject(UserProfileHierarchy.class);
        if (userProfileHierarchy != null) {
            subscriberList = userProfileHierarchy.getSubscriberList();
        }
    }

    private void initFixedNetSubscriptions() {
        TvHierarchyResponse tvHierarchyResponse = (TvHierarchyResponse) RealmManager.getRealmObject(TvHierarchyResponse.class);
        if (tvHierarchyResponse != null && !tvHierarchyResponse.getActiveVtvList().isEmpty())
            fixedNetSubscribtion = tvHierarchyResponse.getActiveVtvList();
    }

    public void initListAdapter() {
        Log.d(TAG, "initListAdapter()");

        selectorListAdadapter = new SelectorAdapter(getApplicationContext(), copyForSearchOfList, currentSelectedPosition);
        selectorListView.setAdapter(selectorListAdadapter);
    }

    public void displayServiceSelector() {
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fab_slide_in_from_right);
        serviceSelectorContainer.startAnimation(anim);
        serviceSelectorContainer.setVisibility(View.VISIBLE);
    }

    public void searchItem(String textToSearch) {
        Log.d(TAG, "Charatcer to rearch: " + textToSearch);
        copyForSearchOfList.clear();
        synchronized (copyForSearchOfList) {
            for (Object item : items) {

                if (item instanceof Ban) {
                    Log.d(TAG, "Ban Selector");

                    if (((Ban) item).getNumber().contains(textToSearch))
                        copyForSearchOfList.add(item);
                }
                if (item instanceof TvHierarchy) {
                    if (((TvHierarchy) item).getServiceId().toLowerCase().contains(textToSearch.toLowerCase()))
                        copyForSearchOfList.add(item);
                } else {
                    if (((Subscriber) item).getMsisdn().contains(textToSearch) ||
                            (((Subscriber) item).getAlias() != null && ((Subscriber) item).getAlias().toLowerCase().contains(textToSearch.toLowerCase())))

                        copyForSearchOfList.add(item);
                }

                selectorListAdadapter.notifyDataSetChanged();
            }
        }
    }

    public void itemSelected(Object o) {

        Intent intent = new Intent();
        if (o instanceof Ban) {
            Log.d(TAG, "Ban Selector");
            setSelectedBan((Ban) o);
            setSelectedBanNumber(((Ban) o).getNumber());
            UserSelectedMsisdnBanController.getInstance().setSelectedNumberBan(((Ban) o).getNumber());
        }
       else if (o instanceof TvHierarchy) {
            selectedObject = o;
            //to check if subscriber is returned from profile api
            if(((TvHierarchy) o).getServiceType().toLowerCase().equals("msisdn") &&
                    UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber(((TvHierarchy) o).getServiceId()) != null){

                Subscriber s = UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber(((TvHierarchy) o).getServiceId());
                UserSelectedMsisdnBanController.getInstance().setSelectedSubscriberId(null);
                UserSelectedMsisdnBanController.getInstance().setSelectedSubscriber(s);
                UserSelectedMsisdnBanController.getInstance().setSelectedFixedNet((TvHierarchy) o);
            }
            //if not found don't make selection propagate to other screens
            else
            {
                UserSelectedMsisdnBanController.getInstance().setTvSelectedMsisdn(null);
                UserSelectedMsisdnBanController.getInstance().setSelectedFixedNet((TvHierarchy) o);
            }


        } else {
            Log.d(TAG, "selector subscriber ---" + ((Subscriber) o).getMsisdn());
            selectedObject = o;
            UserSelectedMsisdnBanController.getInstance().setSelectedSubscriberId(null);
            UserSelectedMsisdnBanController.getInstance().setSelectedSubscriber((Subscriber) o);
            //UserSelectedMsisdnBanController.getInstance().setSelectedMsisdn(((Subscriber) o).getMsisdn());
            UserSelectedMsisdnBanController.getInstance().updateSelectedSubscriber(subscriberList);//todo not sure
        }

        setResult(RESULT_SELECTOR_UPDATED, intent);
        finish();
    }

    public void addButtonIconInsideEditText() {
        Drawable image = getResources().getDrawable(R.drawable.close_icon);
        int h = 30;
        int w = 30;
        image.setBounds(0, 0, w, h);
        searchField.setCompoundDrawables(null, null, image, null);
    }


    private void setItemsList() {

        items = new ArrayList();

        if (selectedType.equals(SELECT_TYPE_BAN)) {
            initBanList();
            items = getBanListNoPrefix();
        } else {
            initSubscriberList();
            items = getSubscriberListNoPrefix();
        }
    }

    private void getCurrentlySelectedItem() {
        copyForSearchOfList = new ArrayList<>();
        copyForSearchOfList.addAll(items);
        try {
            if (selectedType.equals(SELECT_TYPE_BAN)) {
                for (int banIndex = 0; banIndex < copyForSearchOfList.size(); banIndex++) {
                    if (((Ban) copyForSearchOfList.get(banIndex)).getNumber().equals(UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan())) {
                        currentSelectedPosition = banIndex;
                        break;
                    }
                }
            } else {//selectedType.equals(SELECT_TYPE_BAN)

                for (int subscriberIndex = 0; subscriberIndex < copyForSearchOfList.size(); subscriberIndex++) {
                    if (((Subscriber) copyForSearchOfList.get(subscriberIndex)).getMsisdn().equals(UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn())
                            || ((Subscriber) copyForSearchOfList.get(subscriberIndex)).getMsisdn().equals(UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn().substring(1))
                            || ((Subscriber) copyForSearchOfList.get(subscriberIndex)).getMsisdn().substring(1).equals(UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn())
                    ) {
                        currentSelectedPosition = subscriberIndex;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error occured" + e);
        }
    }

    private void setSelectedElementAtFirstPosition() {

        Log.d(TAG, "setSelectedElementAtFirstPosition");
        Log.d(TAG, "List Size: " + copyForSearchOfList.size() + " Selected position is: " + currentSelectedPosition);
        //Copy Selected Element as First Element
        int currentSelectedItemIndexInSortedList = copyForSearchOfList.indexOf(items.get(currentSelectedPosition));
        //Remove Selected Element from it's old position
        copyForSearchOfList.remove(currentSelectedItemIndexInSortedList);
        //Put currentSelectedPosition as 0 cause the selected element will always be 0
        copyForSearchOfList.add(0, items.get(currentSelectedPosition));
        currentSelectedPosition = 0;

    }

    private void sortSubscriberList(List<Object> list) {
        Log.d(TAG, "sortSubscriberList()");
        try {
            Collections.sort(list, getSubcriberComparator());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Comparator<Object> getSubcriberComparator() {
        return new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {

                Subscriber s1 = (Subscriber) o1;
                Subscriber s2 = (Subscriber) o2;

                if (s1.getAlias() != null && s2.getAlias() != null) {
                    return s1.getAlias().compareTo(s2.getAlias());
                } else {
                    if (s1.getAlias() != null && s2.getAlias() == null) {
                        return Integer.MIN_VALUE;
                    } else {
                        if (s1.getAlias() == null && s2.getAlias() != null) {
                            return Integer.MAX_VALUE;
                        } else {
                            return s1.getMsisdn().compareTo(s2.getMsisdn());
                        }
                    }
                }
            }
        };
    }


    public static Object getSelectedObject() {
        return selectedObject;
    }

    public static void setSelectedObject(Object selectedObject) {
        SelectorDialogActivity.selectedObject = selectedObject;
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume()");
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState()");
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.d(TAG, "onRestoreInstanceState()");
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause()");
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        callForAdobeTarget(AdobePageNamesConstants.PG_SELECTOR);
    }

    @Override
    public void finish() {
        super.finish();
    }
}

