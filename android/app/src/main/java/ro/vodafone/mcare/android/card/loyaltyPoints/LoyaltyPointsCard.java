package ro.vodafone.mcare.android.card.loyaltyPoints;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;

import org.apache.commons.lang3.text.WordUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.card.VodafoneAbstractCard;
import ro.vodafone.mcare.android.card.loyaltyPoints.expandableList.LoyaltyExpandableListAdapter;
import ro.vodafone.mcare.android.card.loyaltyPoints.expandableList.LoyaltyPointsListItem;
import ro.vodafone.mcare.android.client.model.loyaltyProgram.LPSMessage;
import ro.vodafone.mcare.android.ui.views.ExpandableAdapterBackedLinearLayout;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;

/**
 * Created by User on 20.04.2017.
 */

public class LoyaltyPointsCard extends VodafoneAbstractCard {
    private RelativeLayout expandButton;
    private ExpandableAdapterBackedLinearLayout pointsListView;
    private LoyaltyExpandableListAdapter loyaltyPointsAdapter;
    List<LoyaltyPointsListItem> listDataHeader;
    HashMap<LoyaltyPointsListItem, List<LoyaltyPointsListItem>> listDataChild;
    private int totalReceivedPoints;

    public LoyaltyPointsCard(Context context) {
        super(context);
        init(null);
    }

    public LoyaltyPointsCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public LoyaltyPointsCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @Override
    protected int setContent() {
        return R.layout.card_loyalty_points;
    }

    private void init(AttributeSet attrs) {
        pointsListView = (ExpandableAdapterBackedLinearLayout) findViewById(R.id.loyalty_points_list_view);
        setupController();
    }

    public void buildCard(String points, String lastUpdate, List<LPSMessage> messagesList, String segment){

        VodafoneTextView loyaltyLastUpdate = (VodafoneTextView) findViewById(R.id.loyalty_total_points_date);
        try {
            loyaltyLastUpdate.setText(changeDateFormat(lastUpdate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        
        VodafoneTextView loyaltyPoints = (VodafoneTextView) findViewById(R.id.loyalty_points);
        loyaltyPoints.setText(points + "p");

        Log.d(TAG, "buildCBUPricePlanCard: ");
        listDataHeader = new ArrayList<LoyaltyPointsListItem>();
        listDataChild = new HashMap<LoyaltyPointsListItem, List<LoyaltyPointsListItem>>();

        List<LoyaltyPointsListItem> items = new ArrayList<LoyaltyPointsListItem>();

        listDataHeader.add(new LoyaltyPointsListItem("Puncte acumulate",null, "0", null));
        listDataHeader.add(new LoyaltyPointsListItem("Puncte care au expirat luna aceasta",null, "0", null));
        listDataHeader.add(new LoyaltyPointsListItem("Puncte care vor expira la următoarea factură", null, "0",null));

        items.add(new LoyaltyPointsListItem("Puncte pentru factura precedentă", null,  "0", null));
        items.add(new LoyaltyPointsListItem("Bonus grupa loialitate", segment, "0", null));

        if(messagesList.size()!=0){
            for(LPSMessage message : messagesList) {
                if (message.getMessageType() != null) {
                    if (message.getMessageType().equals("3")) {
                        items.get(1).set_itemParameter(message.getLpAmount());
                    } else if (message.getMessageType().equals("4")) {
                        items.get(0).set_itemParameter(message.getLpAmount());
                    }
                }
            }

            totalReceivedPoints = Integer.valueOf(items.get(0).get_itemParameter()) + Integer.valueOf(items.get(1).get_itemParameter());


            for (Iterator<LoyaltyPointsListItem> iterator = items.iterator(); iterator.hasNext(); ) {
                LoyaltyPointsListItem value = iterator.next();
                if (value.get_itemParameter().equals("0")){
                    iterator.remove();
                }
            }

            listDataHeader.get(0).set_itemParameter(String.valueOf(totalReceivedPoints));

            for(LPSMessage message : messagesList){
                if(message.getMessageType() != null) {
                    if (message.getMessageType().equals("1")) {
                        listDataHeader.get(1).set_itemParameter(message.getLpAmount());
                    } else if (message.getMessageType().equals("2")) {
                        listDataHeader.get(2).set_itemParameter(message.getLpAmount());
                    }
                }
            }

            for (Iterator<LoyaltyPointsListItem> iterator = listDataHeader.iterator(); iterator.hasNext(); ) {
                LoyaltyPointsListItem value = iterator.next();
                if (value.get_itemParameter().equals("0")){
                    iterator.remove();
                }
            }

            if(listDataHeader.size() > 0)
            listDataChild.put(listDataHeader.get(0),items);
            setupListView();
        }
    }

    private void setupController() {
        LoyaltyPointsCardController.getInstance().setup(this).requestData();
    }

    private void setupListView() {
        loyaltyPointsAdapter = new LoyaltyExpandableListAdapter(getContext(),listDataHeader,listDataChild, pointsListView);
        pointsListView.setAdapter(loyaltyPointsAdapter);
    }

    private String changeDateFormat(String date) throws ParseException {
        DateFormat fromFormat = new SimpleDateFormat("dd.MM.yyyy", new Locale("RO", "RO"));
        DateFormat toFormat = new SimpleDateFormat("dd MMMM yyyy", new Locale("RO", "RO"));
        Date formattedDate = fromFormat.parse(date);


        return WordUtils.capitalize(toFormat.format(formattedDate));
    }
}
