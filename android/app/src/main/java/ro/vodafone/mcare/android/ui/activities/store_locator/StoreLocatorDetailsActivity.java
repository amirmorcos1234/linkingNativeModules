package ro.vodafone.mcare.android.ui.activities.store_locator;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.client.model.stores.VodafoneJsonShop;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.ui.activities.MenuActivity;
import ro.vodafone.mcare.android.ui.activities.support.StoreLocatorService;
import ro.vodafone.mcare.android.ui.header.NavigationHeader;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.D;

/**
 * Created by Bogdan Marica on 7/17/2017.
 */

public class StoreLocatorDetailsActivity extends MenuActivity implements OnMapReadyCallback {

    @BindView(R.id.adress_text)
    VodafoneTextView adress_text1;

    @BindView(R.id.days_labels)
    VodafoneTextView days_labels;

    @BindView(R.id.program_values)
    VodafoneTextView program_values1;

    @BindView(R.id.permission_granted_distance_eta)
    VodafoneTextView permission_granted_distance_eta1;

    @BindView(R.id.go_maps_button)
    Button go_maps_button1;

    @BindView(R.id.container)
    RelativeLayout container;

    @BindView(R.id.unmutable_map_view)
    MapView unmutable_map_view;

    @BindView(R.id.navigation_header)
    NavigationHeader navigationHeader;

    VodafoneJsonShop store;
    GoogleMap map;

    String passedAdress = "";
    String la;
    String lo;

    @Override
    protected int setContent() {
        return R.layout.activity_store_locator_details_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        D.d("StoreLocatorDetailsActivity onCreate");

        ButterKnife.bind(this);
        initNavigationFragment();

        container.setVisibility(View.GONE);
        String serializedObject = "";

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        getListItem();

        serializedObject = bundle.getString("extras");
        if (serializedObject != null) {
            getDataFromString(savedInstanceState, serializedObject);
        } else
            throw new Error("should never be reached");

    }

    @Override
    protected void onResume() {
        super.onResume();
        getToolbar().showToolBar();
        getToolbar().showToolbarWithoutAnimation();

    }

    void getDataFromString(Bundle savedState, String serializedObject) {

        String[] objects = serializedObject.split("~~~");

        String permission = objects[0].split("=")[1];

        String n = objects[1].split("=")[1];
        String a = objects[4].split("=")[1];
        String p = objects[5].split("=")[1];
        la = objects[6].split("=")[1];
        lo = objects[7].split("=")[1];
        String s = objects[8].split("=")[1];
        String d = objects[10].split("=")[1];

        initMapView(s, savedState);

        navigationHeader.setTitle(n);
        adress_text1.setText(a);

        for (int i = 0; i < p.split(", ").length; i++) {
            days_labels.setText(days_labels.getText().toString() + p.split(", ")[i].split(" ")[0] + ":\n");
            program_values1.setText(program_values1.getText().toString() + p.split(", ")[i].split(" ")[1] + "\n");
        }

        days_labels.setTypeface(null, Typeface.BOLD);

        if (permission.equals("false"))
            permission_granted_distance_eta1.setVisibility(View.GONE);
        else {
            permission_granted_distance_eta1.setVisibility(View.VISIBLE);
            permission_granted_distance_eta1.setText("Distanța până la magazin:  " + d + "km");
        }

        go_maps_button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + la + "," + lo + "&mode=d");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });

        getToolbar().showToolBar();
    }

    private void initNavigationFragment() {
//        navigationHeader.buildMsisdnSelectorHeader();
        navigationHeader.displayDefaultHeader();
        navigationHeader.setActivity(this);
        navigationHeader.hideAliasAndMsisdn();
        navigationHeader.hideBalance();
        navigationHeader.hideArrowRight();
    }

    public void setTitle(String text) {
        try {
            navigationHeader.setTitle(text);
        } catch (Exception e) {
            D.e(TAG + " problems with getTitle");

        }
    }

    void getListItem() {
        showLoadingDialog();
        new StoreLocatorService(this).getLocationsList().subscribe(new RequestSessionObserver<String>() {
            @Override
            public void onNext(String s) {
                parseStringToJson(s);
            }

            @Override
            public void onError(Throwable e) {
                stopLoadingDialog();
                container.setVisibility(View.VISIBLE);

                super.onError(e);
                throw new Error("errrooooooor case not managed");
            }
        });
    }

    void parseStringToJson(String jsonLists) {
        try {
            List<VodafoneJsonShop> vfJSONShops = null;
            JSONObject obj = new JSONObject(Html.fromHtml(jsonLists.substring(jsonLists.indexOf("{"), jsonLists.lastIndexOf("}") + 1)).toString());
            TypeToken<List<VodafoneJsonShop>> token = new TypeToken<List<VodafoneJsonShop>>() {
            };
            vfJSONShops = new Gson().fromJson(String.valueOf((JSONArray) obj.get("markers")), token.getType());

            for (VodafoneJsonShop shop :
                    vfJSONShops) {
                if (shop.getA().equals(passedAdress))
                    store = shop;
            }
            stopLoadingDialog();
            D.d("SUCCESS");
            container.setVisibility(View.VISIBLE);
        } catch (Throwable t) {
            D.d("failed to parse - " + t);
            stopLoadingDialog();
            container.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void switchFragmentOnCreate(String fragment, @Nullable String extraParameter) throws Exception {

    }

    void initMapView(final String s, Bundle savedState) {
//        unmutable_map_view = new MapView(this);
        unmutable_map_view.onCreate(savedState);

        // INIT MAP
        unmutable_map_view.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;

                CameraUpdate cameraUpdate;

                try {
                    MapsInitializer.initialize(StoreLocatorDetailsActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                int hasFineLocationPermission = ContextCompat.checkSelfPermission(StoreLocatorDetailsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
                int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(StoreLocatorDetailsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
                List<String> listPermissionsNeeded = new ArrayList<>();

                if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED)
                    listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);

                if (hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED)
                    listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);

                if (!listPermissionsNeeded.isEmpty())
                    ActivityCompat.requestPermissions(StoreLocatorDetailsActivity.this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 101);
                else
                    map.setMyLocationEnabled(true);

                map.getUiSettings().setMyLocationButtonEnabled(false);
                map.getUiSettings().setAllGesturesEnabled(true);

//                LatLngBounds.Builder builder = new LatLngBounds.Builder();
//                builder.include(new LatLng(Double.valueOf(la), Double.valueOf(lo)));
//                if (map.getMyLocation() != null) builder.include(new LatLng(map.getMyLocation().getLatitude(), map.getMyLocation().getLongitude()));

                cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(Double.valueOf(la), Double.valueOf(lo)), 14);

//                Display display = getWindowManager().getDefaultDisplay();
//                Point size = new Point();
//                display.getSize(size);
//                int width = size.x;
//
//                cameraUpdate = CameraUpdateFactory.newLatLngBounds(builder.build(),ViewGroup.LayoutParams.MATCH_PARENT, width / 2, 18);

                map.animateCamera(cameraUpdate);
                map.moveCamera(cameraUpdate);

                MarkerOptions mo = new MarkerOptions();
                mo.draggable(false);

                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(s.equals("1") ? R.drawable.rsz_1red_pointer : R.drawable.rsz_1white_pointer);
                mo.icon(icon);

                mo.position(new LatLng(Double.valueOf(la), Double.valueOf(lo)));
                map.addMarker(mo);

                unmutable_map_view.onResume();
            }
        });

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, width / 2);
        unmutable_map_view.setLayoutParams(lp);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                map.setMyLocationEnabled(true);
                break;
            default:
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
    }
}
