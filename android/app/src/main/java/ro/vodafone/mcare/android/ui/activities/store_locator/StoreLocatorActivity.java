package ro.vodafone.mcare.android.ui.activities.store_locator;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.store.StoreLocatorLocationCard;
import ro.vodafone.mcare.android.card.store.StoreLocatorLocationSpinnerCard;
import ro.vodafone.mcare.android.client.model.realm.system.StoreLocatorLabels;
import ro.vodafone.mcare.android.client.model.stores.VodafoneJsonShop;
import ro.vodafone.mcare.android.custom.CustomEditTextCompat;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.ui.activities.support.StoreLocatorService;
import ro.vodafone.mcare.android.ui.header.NavigationHeader;
import ro.vodafone.mcare.android.ui.utils.KeyboardHelper;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.utils.ViewUtils;
import ro.vodafone.mcare.android.ui.utils.listeners.adapter.item_click.TabAdapterOnItemClickListener;
import ro.vodafone.mcare.android.ui.views.recyclerview.ReverseOrderRecyclerView;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.widget.TabMenu.TabAdapter;
import ro.vodafone.mcare.android.widget.TabMenu.TabCard;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Bogdan Marica on 7/10/2017.
 */

public class StoreLocatorActivity extends RecyclerViewMenuActivity implements
        InterfaceNavigationHeaderViewFactory,
        InterfaceLocationCardFactory,
        InterfaceTabCardFactory,
        InterfaceEmptyViewFactory,
        InterfaceMapCardFactory,
        OnMapReadyCallback {

    final static double ROMANIA_CENTER_LAT = 46.005879;
    final static double ROMANIA_CENTER_LNG = 25.019506;
    static final double AREA = 20000;
    private static String PREFS_FILE_NAME;

    @BindView(R.id.background_view)
    LinearLayout background_view;
    @BindView(R.id.activity_recycler_view_content)
    ReverseOrderRecyclerView activity_recycler_view_content;

    NavigationHeader navigationHeader;
    StoreLocatorLocationCard locationCardView;
    TabCard tabCardView;
    RecyclerViewMapView mapCardView;
    StoreLocatorLocationSpinnerCard emptyCardView;

    GoogleMap map;
    LocationManager geoLocationService;
    String disabledProviders = "";

    CustomEditTextCompat locations_search_edit_text;
    LinearLayout location_search_button;
    LinearLayout your_location_button;
    ActivityRecyclerAdapter activityRecyclerAdapter;
    List<VodafoneJsonShop> vfJSONShops;
    List<Marker> markers = new ArrayList<>();

    Boolean permisionsGranted = false;
    boolean gotlocationOnce = true;
    boolean refreshInfo;
    boolean providerIsEnabled;
    boolean searching = false;
    boolean userCanSearch = false;
    double userlat = -1;
    double userlng = -1;

    Thread sThread = new Thread(new Runnable() {
        @Override
        public void run() {

        }
    });

    @SuppressWarnings({"MissingPermission"})
    android.location.LocationListener locationChangedListener = new android.location.LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            activityOnLocationChanged(location);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            D.d("onStatusChanged");
        }

        @Override
        public void onProviderEnabled(String s) {
            D.d("onProviderEnabled = " + s);
            if (s.equals("gps")) {
                disabledProviders = "";
                providerIsEnabled = true;
                locationCardView.showLoading(true);

                if (userlng != -1) {
                    getAddress(userlat, userlng);
                    if (vfJSONShops != null)
                        showInUserRange(userlat, userlng);
                } else
                    gotlocationOnce = true;

                activityRecyclerAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onProviderDisabled(String s) {
            gotlocationOnce = false;
            disabledProviders += " " + s;
            if (disabledProviders.equals(" network gps") || disabledProviders.equals(" gps network") || disabledProviders.equals(" gps")) {
                providerIsEnabled = false;
                hideLocationLoadingCard();
                if (locationCardView != null && activityRecyclerAdapter != null) {
                    showBasicErrorCard();
                    activityRecyclerAdapter.setStoreList(vfJSONShops);
//                showInUserRange();
                    addPins(vfJSONShops, true);
                    updateMap(false);
                    activityRecyclerAdapter.notifyDataSetChanged();
                }
            }
        }
    };
    String toBeSearched;
    GoogleApiClient googleApiClient;
    LocationRequest gmsRequest;

    @SuppressWarnings({"MissingPermission"})
    void activityOnLocationChanged(Location location) {
        StoreLocatorActivity.this.stopLoadingDialog();

        if (locationCardView != null && gotlocationOnce && location != null && !disabledProviders.equals(" gps")) {
            locationCardView.showLoading(true);
            gotlocationOnce = false;
            D.v("LOCATION CHANGED");
            userlat = location.getLatitude();
            userlng = location.getLongitude();

            if (vfJSONShops != null)
                showInUserRange(userlat, userlng);

            getAddress(userlat, userlng);

            try {
                map.getUiSettings().setMyLocationButtonEnabled(false);
                map.setMyLocationEnabled(true);
            } catch (Exception e) {
                D.e("error = " + e);

            }
            updateMap(true);

            activityRecyclerAdapter.notifyDataSetChanged();

        } else {

            if (location == null)
                D.e("LOCATION == NULL ");
        }
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected int getContentLayoutResource() {
        return R.layout.activity_recycler_view_store_locator;
    }

    @Override
//    @SuppressAjWarnings("MissingPermission")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        refreshInfo = false;

        geoLocationService = (LocationManager) getSystemService(LOCATION_SERVICE);
        providerIsEnabled = geoLocationService.isProviderEnabled(LocationManager.GPS_PROVIDER);

        gmsRequest = new LocationRequest();

        int priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
        gmsRequest.setPriority(priority);

        gmsRequest.setFastestInterval(10);
        gmsRequest.setInterval(10);


        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {

                        if (ContextCompat.checkSelfPermission(StoreLocatorActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(StoreLocatorActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling

                            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, gmsRequest, new LocationListener() {
                                @Override
                                public void onLocationChanged(Location location) {
                                    activityOnLocationChanged(location);
                                }
                            });
                        }
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        D.e("onConnectionSuspended");

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        D.e("onConnectionFailed");
                    }
                })
                .build();
        googleApiClient.connect();

        SharedPreferences sharedPreference = this.getSharedPreferences(PREFS_FILE_NAME, MODE_PRIVATE);

        if (this.getSharedPreferences(PREFS_FILE_NAME, MODE_PRIVATE).getBoolean("isFirstTime", true)) {
            showDataConsumptionDialog();
            sharedPreference.edit().putBoolean("isFirstTime", false).apply();
        } else
            checkAndAskForPermisionIfNeed();

        initPage();
        initMapView(savedInstanceState);
        showLoadingDialog();
        initListItems();

        StoreLocatorTrackingEvent event = new StoreLocatorTrackingEvent();
		VodafoneController.getInstance().getTrackingService().track(event);
    }

    void showDataConsumptionDialog() {
        final Dialog overlayDialog = new Dialog(StoreLocatorActivity.this, android.R.style.Theme_Black_NoTitleBar);
        overlayDialog.setContentView(R.layout.overlay_dialog_notifications);

        VodafoneTextView overlayTitle = (VodafoneTextView) overlayDialog.findViewById(R.id.overlayTitle);
        VodafoneTextView overlaySubtext = (VodafoneTextView) overlayDialog.findViewById(R.id.overlaySubtext);
        Button acceptDataConsumption = (Button) overlayDialog.findViewById(R.id.buttonKeepOn);
        Button overlayCancelButton = (Button) overlayDialog.findViewById(R.id.buttonTurnOff);
        ImageView overlayDismissButton = (ImageView) overlayDialog.findViewById(R.id.overlayDismissButton);

        overlayTitle.setText(StoreLocatorLabels.getStoreLocatorLoadingMapTitle());
        overlaySubtext.setText(StoreLocatorLabels.getStoreLocatorLoadingMapSubtext());
        acceptDataConsumption.setText(StoreLocatorLabels.getAcceptDataConsumption());

        overlayCancelButton.setVisibility(View.GONE);
        acceptDataConsumption.setBackgroundColor(ContextCompat.getColor(StoreLocatorActivity.this, R.color.red_button_color));

        acceptDataConsumption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlayDialog.dismiss();
                VodafoneController.getInstance().setStoreLocatorAplicationFreshInstalled(true);
                checkAndAskForPermisionIfNeed();
            }
        });

        overlayDismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlayDialog.dismiss();//this is fix for 7687
                VodafoneController.getInstance().setStoreLocatorAplicationFreshInstalled(true);
                checkAndAskForPermisionIfNeed();
            }
        });

        overlayDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                overlayDialog.dismiss();//this is fix for 7687
                VodafoneController.getInstance().setStoreLocatorAplicationFreshInstalled(true);
                checkAndAskForPermisionIfNeed();
            }
        });
        overlayDialog.show();
    }

    void showMissingProviderDialog() {
        final Dialog overlayDialog = new Dialog(StoreLocatorActivity.this, android.R.style.Theme_Black_NoTitleBar);
        overlayDialog.setContentView(R.layout.overlay_dialog_notifications);

        overlayDialog.show();

        VodafoneTextView overlayTitle = (VodafoneTextView) overlayDialog.findViewById(R.id.overlayTitle);
        VodafoneTextView overlaySubtext = (VodafoneTextView) overlayDialog.findViewById(R.id.overlaySubtext);

        overlayTitle.setText(" Access locație");
        overlaySubtext.setText(" Ca să calculăm distanța dintre tine și cel mai apropiat magazin avem nevoie de permisiunea de GPS.");

        Button enableGpsProvider = (Button) overlayDialog.findViewById(R.id.buttonKeepOn);
        Button overlayCancelButton = (Button) overlayDialog.findViewById(R.id.buttonTurnOff);

        ImageView overlayDismissButton = (ImageView) overlayDialog.findViewById(R.id.overlayDismissButton);

        enableGpsProvider.setText("Modificare permisiune");
        overlayCancelButton.setText("Continuă fără localizare");

        enableGpsProvider.setBackgroundColor(ContextCompat.getColor(StoreLocatorActivity.this, R.color.red_button_color));
        overlayCancelButton.setBackgroundColor(ContextCompat.getColor(StoreLocatorActivity.this, R.color.grey_button_color));


        enableGpsProvider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlayDialog.dismiss();
                gotoSettingsGps();
            }
        });

        overlayDismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBasicErrorCard();
                sortListBySearch(toBeSearched);
                overlayDialog.dismiss();
            }
        });

        overlayCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBasicErrorCard();
                sortListBySearch(toBeSearched);
                overlayDialog.dismiss();
            }
        });

        overlayDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                showBasicErrorCard();
                sortListBySearch(toBeSearched);
                overlayDialog.dismiss();
            }
        });


    }

    private void showBasicErrorCard() {
        if (locationCardView != null) {
            locationCardView.showNewBasicErrorCard(true, StoreLocatorLabels.getStoreLocatorShowBasicErrorCard(), ContextCompat.getDrawable(StoreLocatorActivity.this, R.drawable.yellow_error_triangle));
        }
    }

    void checkAndAskForPermisionIfNeed() {
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED)
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);

        if (hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED)
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        if (!listPermissionsNeeded.isEmpty()) {
            permisionsGranted = false;
            displayPermissionOverlay(listPermissionsNeeded);
        } else {
            permisionsGranted = true;

            if (providerIsEnabled)
                stepIntoPermissionGrantedFlow();
            else {
                geoLocationService.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 0, locationChangedListener);
                geoLocationService.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10, 0, locationChangedListener);
                geoLocationService.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 10, 0, locationChangedListener);

                if (locationCardView != null)
                    locationCardView.showBasicErrorCard(true, StoreLocatorLabels.getStoreLocatorShowBasicErrorCard(), ContextCompat.getDrawable(StoreLocatorActivity.this, R.drawable.yellow_error_triangle));

                showMissingProviderDialog();
            }
        }
    }

    /**
     * intializing recyclerview and adapter + grey background
     */
    void initPage() {
        activityRecyclerAdapter = new ActivityRecyclerAdapter(StoreLocatorActivity.this, vfJSONShops);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(StoreLocatorActivity.this, LinearLayoutManager.VERTICAL, false);
        setBackgroundViewColor();
        activity_recycler_view_content.setAdapter(activityRecyclerAdapter);

        activity_recycler_view_content.addItemDecoration(new OverlapTabCardItemDecoration());
        activity_recycler_view_content.setLayoutManager(mLayoutManager);
    }

    void setBackgroundViewColor() {
        VodafoneController.getInstance().handler.post(new Runnable() {
            @Override
            public void run() {

                if (getNavigationHeader() == null) {
                    return;
                }

                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, (ViewUtils.getWindowHeight() - getNavigationHeader().getHeight()));

                lp.height = (ViewUtils.getWindowHeight() - getNavigationHeader().getHeight());
                lp.gravity = Gravity.BOTTOM;
                background_view.setGravity(Gravity.BOTTOM);
                background_view.setLayoutParams(lp);
                background_view.setBackgroundColor(ContextCompat.getColor(StoreLocatorActivity.this, R.color.general_background_light_gray));
            }
        });
    }

    void initMapView(Bundle savedState) {
        if (mapCardView == null) {
            mapCardView = new RecyclerViewMapView(this);
            mapCardView.onCreate(savedState);
            mapCardView.setFocusable(true);
            mapCardView.setEnabled(true);
            mapCardView.getMapAsync(this);
        }

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, width);
        int defaultMargin = ScreenMeasure.dpToPx(12);
        mapCardView.setLayoutParams(lp);
        mapCardView.setBackgroundColor(ContextCompat.getColor(this, R.color.card_background_gray));
        mapCardView.setPadding(defaultMargin, 0, defaultMargin, 0);
    }

    void addPins(List<VodafoneJsonShop> displayedPins, boolean addToMarkersList) {
        try {
            map.clear();
            if (displayedPins != null && displayedPins.size() > 0)
                for (int i = 0; i < displayedPins.size(); i++) {
                    MarkerOptions mo = new MarkerOptions();
                    mo.draggable(false);

                    BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(displayedPins.get(i).getS().equals("1") ? R.drawable.rsz_1red_pointer : R.drawable.rsz_1white_pointer);
                    mo.icon(icon);

                    mo.position(new LatLng(Double.valueOf(displayedPins.get(i).getLa()), Double.valueOf(displayedPins.get(i).getLo())));
                    Marker m = map.addMarker(mo);
                    m.setTag("permission=" + getPermissionGranted() + "~~~" + displayedPins.get(i).toString());
                    if (addToMarkersList)
                        markers.add(m);
                }
        } catch (NullPointerException npe) {
            D.e("npe = " + npe);
            npe.printStackTrace();
        }
    }

    void initListItems() {

        new StoreLocatorService(this).getLocationsList().subscribe(new RequestSessionObserver<String>() {
            @Override
            public void onNext(String s) {
                parseStringToJson(s);
                stopLoadingDialog();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                showError();
                activityRecyclerAdapter.notifyDataSetChanged();
                stopLoadingDialog();
            }
        });
    }

    private void showError() {
        if (emptyCardView != null) {
            emptyCardView.hideLoading();
            emptyCardView.showError(true, StoreLocatorLabels.getStoreLocatorShowError(), ContextCompat.getDrawable(StoreLocatorActivity.this, R.drawable.yellow_error_triangle));
            emptyCardView.setOnErrorClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    D.e("ERROR CASE : REinit items list");
                    initListItems();
                }
            });
        }
    }

    @Override
    @SuppressWarnings({"MissingPermission"})
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        try {
            MapsInitializer.initialize(StoreLocatorActivity.this);
        } catch (Exception e) {
            D.e("Error = " + e);
            e.printStackTrace();
        }

        if (permisGranted() && providerIsEnabled) {
            map.getUiSettings().setMyLocationButtonEnabled(false);
            map.setMyLocationEnabled(true);
            updateMap(true);
        } else {
            updateMap(false);
        }

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                Intent intent = new Intent(StoreLocatorActivity.this, StoreLocatorDetailsActivity.class);
                intent.putExtra("extras", marker.getTag().toString());
                StoreLocatorActivity.this.startActivityForResult(intent, 102);

                return false;
            }
        });

        mapCardView.onResume();
        activityRecyclerAdapter.notifyDataSetChanged();
    }

    @SuppressWarnings({"MissingPermission"})
    void stepIntoPermissionGrantedFlow() {

        gotlocationOnce = true;
        initGoogleLocationApi();

        //TODO : move this lines so that listener is set even if we dont have location YET
        geoLocationService.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 0, locationChangedListener);
        geoLocationService.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10, 0, locationChangedListener);
        geoLocationService.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 10, 0, locationChangedListener);
    }

    @SuppressWarnings({"MissingPermission"})
    void initGoogleLocationApi() {
        LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
    }

    void displayPermissionOverlay(final List<String> listPermissionsNeeded) {
//        D.d("GIVE PERMISSION");

        final Dialog overlayDialog = new Dialog(StoreLocatorActivity.this, android.R.style.Theme_Black_NoTitleBar);
        overlayDialog.setContentView(R.layout.overlay_dialog_notifications);

        overlayDialog.show();

        VodafoneTextView overlayTitle = (VodafoneTextView) overlayDialog.findViewById(R.id.overlayTitle);
        VodafoneTextView overlaySubtext = (VodafoneTextView) overlayDialog.findViewById(R.id.overlaySubtext);

        overlayTitle.setText(StoreLocatorLabels.getStoreLocatorLocationAccessTitle());
        overlaySubtext.setText(StoreLocatorLabels.getStoreLocatorLocationAccessSubtext());

        Button permitLocattions = (Button) overlayDialog.findViewById(R.id.buttonKeepOn);
        Button overlayCancelButton = (Button) overlayDialog.findViewById(R.id.buttonTurnOff);

        ImageView overlayDismissButton = (ImageView) overlayDialog.findViewById(R.id.overlayDismissButton);

        permitLocattions.setText(StoreLocatorLabels.getPermitLocationsButton());
        overlayCancelButton.setText(StoreLocatorLabels.getCancelButton());

        permitLocattions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(StoreLocatorActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        && ActivityCompat.shouldShowRequestPermissionRationale(StoreLocatorActivity.this, Manifest.permission.ACCESS_FINE_LOCATION))
                    ActivityCompat.requestPermissions(StoreLocatorActivity.this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 101);
                else {
                    D.e("GOTO SETTINGS");
                    gotoSettingsPermission();
                }
                overlayDialog.dismiss();
            }
        });

        overlayDismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permisionsGranted = false;
                hideLocationLoadingCard();
                activityRecyclerAdapter.notifyDataSetChanged();
                overlayDialog.dismiss();
            }
        });

        overlayCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permisionsGranted = false;
                activityRecyclerAdapter.notifyDataSetChanged();
                overlayDialog.dismiss();
            }
        });

        overlayDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                permisionsGranted = false;
                activityRecyclerAdapter.notifyDataSetChanged();
                overlayDialog.dismiss();
            }
        });
    }

    void gotoSettingsPermission() {
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(i);
    }

    void gotoSettingsGps() {
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        startActivityForResult(i, 104);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (refreshInfo) {
            int hasFineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            List<String> listPermissionsNeeded = new ArrayList<>();

            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED)
                listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);

            if (hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED)
                listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);

            if (!permisGranted() && listPermissionsNeeded.isEmpty()) {
                permisionsGranted = true;

                locationCardView.showLoading(true);
                emptyCardView.showLoading(true);
                activityRecyclerAdapter.setStoreList(null);
                activityRecyclerAdapter.notifyDataSetChanged();

                stepIntoPermissionGrantedFlow();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case 102:
                activityRecyclerAdapter.notifyDataSetChanged();
                break;
            case 104:
                if (geoLocationService.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    providerIsEnabled = true;
                    stepIntoPermissionGrantedFlow();
                } else {
                    showMissingProviderDialog();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 101: {
                permisionsGranted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;

                if (permisGranted()) {
                    permisionsGranted = true;

                    locationCardView.showLoading(true);
                    emptyCardView.showLoading(true);
                    activityRecyclerAdapter.setStoreList(null);
                    activityRecyclerAdapter.notifyDataSetChanged();

                    if (providerIsEnabled)
                        stepIntoPermissionGrantedFlow();
                    else {
                        D.e("DISPLAY OVERLAY FOR PROVIDER DISABLED");
                        showMissingProviderDialog();
                    }
                }

                break;
            }
        }
    }

    void parseStringToJson(String jsonLists) {
        try {
            vfJSONShops = null;
            JSONObject obj = new JSONObject(Html.fromHtml(jsonLists.substring(jsonLists.indexOf("{"), jsonLists.lastIndexOf("}") + 1)).toString());
            TypeToken<List<VodafoneJsonShop>> token = new TypeToken<List<VodafoneJsonShop>>() {
            };
            vfJSONShops = new Gson().fromJson(String.valueOf((JSONArray) obj.get("markers")), token.getType());

            Collections.sort(vfJSONShops, new Comparator<VodafoneJsonShop>() {
                @Override
                public int compare(VodafoneJsonShop vjs1, VodafoneJsonShop vjs2) {
                    if (vjs1.getA().compareToIgnoreCase(vjs2.getA()) > 0)
                        return 1;
                    else
                        return -1;
                }
            });

            activityRecyclerAdapter.setStoreList(vfJSONShops);
            addPins(vfJSONShops, false);
            hideLocationLoadingCard();

            if (permisGranted()) {
                try {
                    setUserLocation(geoLocationService.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER));
                    setUserLocation(geoLocationService.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
                    setUserLocation(geoLocationService.getLastKnownLocation(LocationManager.GPS_PROVIDER));
                } catch (SecurityException se) {
                    //shouldn't happen as we already have permission here
                    //however, if it DOES happen, then we do a check further down the line to make sure that
                    //we're not sorting based on -1,-1 coordinates
                }

                emptyCardView.hideLoading();
                userCanSearch = true;
                if (userlng != -1) {
                    if (vfJSONShops != null)
                        showInUserRange(userlat, userlng);
                } else {
                    D.e("LOCATION NOT CALLED ? WORKS????");//todo what tf to do now?????????
                    updateMap(false);
                }
            }
            activityRecyclerAdapter.notifyDataSetChanged();

        } catch (Throwable t) {
            D.d("failed to parse - " + t);
        }
    }

    private void setUserLocation(Location location) {
        if (location != null) {
            userlat = location.getLatitude();
            userlng = location.getLongitude();
        }
    }

    public NavigationHeader getNavigationHeader() {
        return navigationHeader;
    }

    @Override
    public void switchFragmentOnCreate(String fragmentName, String extraParameter) {
//        attachFragment((StoreLocatorFragment) FragmentUtils.newInstanceByClassName(fragmentName));
    }

    public void setTitle() {
        navigationHeader.setTitle(StoreLocatorLabels.getStoreLocatorNavigationHeaderTitle());
    }

    public void setTitle(String text) {
        try {
            navigationHeader.setTitle(text);
        } catch (Exception e) {
            Log.e(TAG, "problems with getTitle");

        }
    }

    @Override
    public NavigationHeader getNavigationView() {

        if (navigationHeader == null) {

            navigationHeader = new NavigationHeader(this);
            navigationHeader.setId(R.id.navigation_header);
            navigationHeader.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            navigationHeader.displayDefaultHeader();
            navigationHeader.setActivity(this);
            setTitle();

            navigationHeader.removeViewFromContainer();

            final View v = View.inflate(this, R.layout.fragment_store_locator_custom_header, null);

            locations_search_edit_text = (CustomEditTextCompat) v.findViewById(R.id.adress_input);
            location_search_button = (LinearLayout) v.findViewById(R.id.adress_search_button);
            your_location_button = (LinearLayout) v.findViewById(R.id.get_location_button);


            locations_search_edit_text.clearFocus();
            v.findViewById(R.id.container).requestFocus();

            locations_search_edit_text.setImeOptions(EditorInfo.IME_ACTION_DONE);

            locations_search_edit_text.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    D.w();
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    toBeSearched = charSequence.toString();
                    if (userCanSearch) {
                        if (charSequence.length() > 0) {
                            if (charSequence.toString().charAt(charSequence.length() - 1) == '\n') {
                                D.e("this is enter");
                                KeyboardHelper.hideKeyboard(StoreLocatorActivity.this);
                                locations_search_edit_text.setText(charSequence.toString().substring(0, charSequence.length() - 1));
                                locations_search_edit_text.setSelection(locations_search_edit_text.getText().length());
                                toBeSearched = charSequence.toString().substring(0, charSequence.length() - 1);
                            }
                        }

                        try {
                            sThread.interrupt();

                            searching = charSequence.length() > 0;

                            sThread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    sortListBySearch(toBeSearched);
                                }
                            });
                            sThread.run();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    D.w();

                }
            });

            location_search_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (userCanSearch)
                        sortListBySearch(locations_search_edit_text.getText().toString());
                }
            });

            locations_search_edit_text.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent keyEvent) {
                    if (keyEvent.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER)
                        KeyboardHelper.hideKeyboard(StoreLocatorActivity.this);


                    return false;
                }
            });

            your_location_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //todo center map on current location or ask for permission
                    setupLocationButtonTrackingEvent();
                    if (permisGranted()) {
                        //we just set the text to "" because if permission granted required operations will be made in locations_search_edit_text's textWatcher

                        if (providerIsEnabled)
                            locations_search_edit_text.setText("");
                        else {
                            showMissingProviderDialog();
                        }
                    } else {
                        int hasFineLocationPermission = ContextCompat.checkSelfPermission(StoreLocatorActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
                        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(StoreLocatorActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
                        List<String> listPermissionsNeeded = new ArrayList<>();

                        if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED)
                            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);

                        if (hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED)
                            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);

                        if (!listPermissionsNeeded.isEmpty())
                            displayPermissionOverlay(listPermissionsNeeded);
                    }
                }
            });

            try {
                navigationHeader.hideAliasAndMsisdn();
                navigationHeader.hideBalance();
                navigationHeader.hideArrowRight();
            } catch (Exception e) {
                D.e("CASE WE CANNOT DO THIS OPERATION IF HEADER IS NOT BUILT YET");
                e.printStackTrace();
            }
            navigationHeader.addViewToContainer(v);
        }

        return navigationHeader;
    }

    boolean permisGranted() {
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(StoreLocatorActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(StoreLocatorActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED)
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);

        if (hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED)
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        return listPermissionsNeeded.isEmpty() && permisionsGranted;
    }

    void sortListBySearch(final String querryString) {

        emptyCardView.showLoading(true);

        activityRecyclerAdapter.setStoreList(null);
        activityRecyclerAdapter.notifyDataSetChanged();

        List<VodafoneJsonShop> unsortedList = new ArrayList<>();

        if (querryString != null && !querryString.equals("")) {

            for (VodafoneJsonShop vfs : vfJSONShops) {
                if (vfs.getA().toLowerCase().contains(querryString.toLowerCase()))
                    unsortedList.add(vfs);
            }

            Collections.sort(unsortedList, new Comparator<VodafoneJsonShop>() {
                public int compare(VodafoneJsonShop m1, VodafoneJsonShop m2) {
                    return Double.compare(m1.getDistanceToUser(), m2.getDistanceToUser());
                }
            });

            activityRecyclerAdapter.setStoreList(unsortedList);
            addPins(unsortedList, false);
            updateMap(false);

        } else if (permisGranted() && providerIsEnabled && userlat != -1) {
            showInUserRange(userlat, userlng);
            updateMap(true);
        } else {
            activityRecyclerAdapter.setStoreList(vfJSONShops);
            addPins(vfJSONShops, false);
            updateMap(false);
        }

        emptyCardView.hideLoading();
        activityRecyclerAdapter.notifyDataSetChanged();
    }

    void updateMap(final boolean centerUser) {
        VodafoneController.getInstance().handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!centerUser) {
                        D.d("SHOW ALL POINTS");
                        CameraUpdate cameraUpdate;
                        cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(ROMANIA_CENTER_LAT, ROMANIA_CENTER_LNG), 5.8f);
                        setLoadedCallback(cameraUpdate);
//                      map.moveCamera(cameraUpdate);
                    } else if (!providerIsEnabled) {
                        D.d("SHOW ALL POINTS");
                        CameraUpdate cameraUpdate;
                        cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(ROMANIA_CENTER_LAT, ROMANIA_CENTER_LNG), 5.8f);
                        setLoadedCallback(cameraUpdate);
                    } else {
                        D.d("SHOW AROUND USER");
                        LatLngBounds.Builder b = new LatLngBounds.Builder();
                        for (Marker m : markers) {
                            b.include(m.getPosition());
                        }
                        LatLngBounds bounds = b.build();
                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 100);
                        setLoadedCallback(cu);
                    }
                } catch (Exception e) {
                    D.e("cannot update map yet, but we need it for futher calls");
                    D.e("e = " + e);
                    e.printStackTrace();
                }
            }
        });
    }

    void setLoadedCallback(final CameraUpdate cameraUpdate) {
        map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                map.animateCamera(cameraUpdate);
            }
        });
    }

    void showInUserRange(double userlat, double userlong) {

        Location location = new Location("");
        location.setLatitude(userlat);
        location.setLongitude(userlong);

        List<VodafoneJsonShop> storesList = new ArrayList<>();

        for (VodafoneJsonShop shop : vfJSONShops) {

            Location l = new Location("");
            l.setLatitude(Double.valueOf(shop.getLa()));
            l.setLongitude(Double.valueOf(shop.getLo()));

            shop.setDistanceToUser(location.distanceTo(l));

            if (((double) location.distanceTo(l)) <= AREA)
                storesList.add(shop);
        }

        Collections.sort(storesList, new Comparator<VodafoneJsonShop>() {
            public int compare(VodafoneJsonShop m1, VodafoneJsonShop m2) {
                return Double.compare(m1.getDistanceToUser(), m2.getDistanceToUser());
            }
        });

        activityRecyclerAdapter.setStoreList(storesList);
        addPins(storesList, true);
        activityRecyclerAdapter.notifyDataSetChanged();

//        locationCardView.hideLoading();
        userCanSearch = true;
    }

    @Override
    public RecyclerViewMapView getMapCardView() {

        return mapCardView;
    }

    @Override
    public TabCard getTabCardView() {
        if (tabCardView == null) {
            tabCardView = new TabCard(this);

            ArrayList<String> tabArray = new ArrayList<>();
            tabArray.add("Listă");
            tabArray.add("Hartă");

            TabAdapter adapter = new TabAdapter(this, tabArray);
            tabCardView.setAdapter(adapter);

            tabCardView.unselectAll();
            tabCardView.setHighlighted(0, true);

            tabCardView.setOnItemClickListener(new TabAdapterOnItemClickListener(tabCardView) {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    super.onItemClick(adapterView, view, i, l);
                    tabCardView.unselectAll();
                    tabCardView.setHighlighted(i, true);
                    if (i == 0)
                        activityRecyclerAdapter.setMapShown(false);
                    else {
                        activityRecyclerAdapter.setMapShown(true);

                        if (searching) D.w("SEARCH IN PROGRESS .. DONT UPDATE MAP");
                        else {
                            if (permisGranted())
                                updateMap(true);
                            else
                                updateMap(false);

                            rx.Observable.timer(1, TimeUnit.SECONDS)
                                    .subscribeOn(Schedulers.newThread())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Action1<Long>() {
                                        @Override
                                        public void call(Long aLong) {

                                            if (permisGranted())
                                                updateMap(true);
                                            else
                                                updateMap(false);

                                        }
                                    });
                        }
                    }
                    setupTabCardsTrackingEvent(i);
                }
            });
            tabCardView.setGreyMargins();
        }

        tabCardView.bringToFront();
        activity_recycler_view_content.setChildDrawingOrderCallback(new RecyclerView.ChildDrawingOrderCallback() {
            @Override
            public int onGetChildDrawingOrder(int childCount, int i) {
                return 0;
            }
        });
        return tabCardView;
    }

    @Override
    public StoreLocatorLocationCard getLocationCardView() {
        if (locationCardView == null) {
            locationCardView = new StoreLocatorLocationCard(this);
            // INIT LOCATION CARD
            if (permisGranted())
                locationCardView.showLoading(true);
            else if (!permisGranted()) {
                locationCardView.showBasicErrorCard(true, StoreLocatorLabels.getStoreLocatorShowBasicErrorCard(), ContextCompat.getDrawable(this, R.drawable.yellow_error_triangle));
            } else {
                initPermissionGrantedLocationCard();
            }
        }
        locationCardView.setCardMargins(0, 0, 0, 0);

        return locationCardView;
    }

    void initPermissionGrantedLocationCard() {
        locationCardView.showLoading(true);

        locationCardView.getCardView().setBackgroundColor(ContextCompat.getColor(this, R.color.card_background_gray));
        locationCardView.setBackgroundColor(ContextCompat.getColor(this, R.color.card_background_gray));
    }

    @Override
    public StoreLocatorLocationSpinnerCard getEmptyViewFillScreen() {
        if (emptyCardView == null) {
            emptyCardView = new StoreLocatorLocationSpinnerCard(this);
            emptyCardView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            emptyCardView.setBackgroundColor(ContextCompat.getColor(this, R.color.card_background_gray));

            emptyCardView.showLoading(true);
        }

        return emptyCardView;
    }

    public boolean getPermissionGranted() {
        return permisionsGranted != null && permisionsGranted && providerIsEnabled && permisGranted();
    }

    public void getAddress(final double lat, final double lng) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                List<Address> addresses;
                Geocoder geocoder = new Geocoder(StoreLocatorActivity.this, Locale.getDefault());
                try {
                    addresses = geocoder.getFromLocation(lat, lng, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                    final String addressLine = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String streetName = addresses.get(0).getThoroughfare();
                    String number = addresses.get(0).getSubThoroughfare();
                    String city = addresses.get(0).getLocality();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();

                    final String address = ((streetName != null && !streetName.equals("")) ? streetName : "")
                            + ((number != null && !number.equals("")) ? (" nr. " + number) : "")
                            + ((city != null && !city.equals("")) ? (", " + city) : "")
                            + ((postalCode != null && !postalCode.equals("")) ? (", " + postalCode) : (", " + country));

                    VodafoneController.getInstance().handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (addressLine != null && !addressLine.isEmpty()) {
                                locationCardView.setCurrentAdress((address != null && !address.isEmpty()) ? address : addressLine);
                                hideLocationLoadingCard();
                                locationCardView.hideError();
                            } else {
                                hideLocationLoadingCard();
                                showBasicErrorCard();
                            }
                        }
                    });
                } catch (IOException e) {
                    D.e("ERROR GETTING ADRES FROM LOCATION");
                    e.printStackTrace();
                    VodafoneController.getInstance().handler.post(new Runnable() {
                        @Override
                        public void run() {
                            hideLocationLoadingCard();
                            showBasicErrorCard();
                        }
                    });
                }
                return null;
            }
        }.execute((Void[]) null);
    }

    private void hideLocationLoadingCard() {
        if (locationCardView != null) {
            locationCardView.hideLoading();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapCardView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapCardView.onLowMemory();
    }

    @Override
    public void onResume() {
        mapCardView.onResume();
        refreshInfo = true;
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapCardView.onPause();
    }

    private void setupTabCardsTrackingEvent(int position){
        StoreLocatorTrackingEvent event = new StoreLocatorTrackingEvent();
        TrackingAppMeasurement journey = new TrackingAppMeasurement();
        switch (position){
            case 0:
                journey.event65 = "List";
                journey.eVar82 = "mcare:store locator:button:List";
                break;
            case 1:
                journey.event65 = "map";
                journey.eVar82 = "mcare:store locator:button:map";
                break;
        }
        event.defineTrackingProperties(journey);
        VodafoneController.getInstance().getTrackingService().trackCustom(event);
    }

    private void setupLocationButtonTrackingEvent(){
        StoreLocatorTrackingEvent event = new StoreLocatorTrackingEvent();
        TrackingAppMeasurement journey = new TrackingAppMeasurement();
        journey.event65 = "location";
        journey.eVar82 = "mcare:store locator:button:location";
        journey.getContextData().put("eVar82", journey.eVar82);
        journey.getContextData().put("event65", journey.event65);
        event.defineTrackingProperties(journey);
        VodafoneController.getInstance().getTrackingService().trackCustom(event);
    }

    public static class StoreLocatorTrackingEvent extends TrackingEvent{
        @Override
        protected void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);
            if (getErrorMessage() != null) {
                s.event11 = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "store locator";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "store locator");
            s.channel = "store locator";
            s.getContextData().put("&&channel", s.channel);
            s.prop21 = "mcare:" + "store locator";
            s.getContextData().put("prop21", s.prop21);
            s.eVar5 = "content";
            s.getContextData().put("eVar5", s.eVar5);
            // TODO: 31.07.2018 check this
            s.event1 = "event1";
            s.getContextData().put("event1", s.event1);
        }
    }

}