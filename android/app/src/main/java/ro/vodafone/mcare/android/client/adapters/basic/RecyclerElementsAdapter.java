package ro.vodafone.mcare.android.client.adapters.basic;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import io.realm.RealmList;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.presenter.adapterelements.base.AdapterElement;
import ro.vodafone.mcare.android.presenter.adapterelements.base.DynamicAdapterElement;
import ro.vodafone.mcare.android.ui.views.viewholders.general.BasicViewHolder;
import ro.vodafone.mcare.android.ui.views.viewholders.general.DynamicViewHolder;

/**
 * RecyclerElementsAdapter
 * - have a single listType. User ViewHolders in the same LIST_TYPE element to have different Views.
 * - use other types for static elements, that are no lists.
 * Created by Victor Radulescu on 8/25/2017.
 */

public class RecyclerElementsAdapter extends RecyclerView.Adapter<BasicViewHolder> {

    public static final int HEADER_TYPE = 101;
    public static final int TAB_CARD_TYPE = 103;
    public static final int FILTER_AND_SORT_TYPE = 107;
    public static final int SERVICE_CARD_TYPE = 108;
    public static final int TEXT_TYPE = 109;
    public static final int BUTTON_TYPE = 110;
    public static final int SEARCH_TYPE = 111;

    public static final int VIEW_GROUP_TYPE = 200;

    public static final int LIST_TYPE = 100;
    public static final int CARD_ERROR_TYPE = 99;

    public static final int EMPTY_SPACE = 0;

    public Comparator<AdapterElement> elementComparator = new Comparator<AdapterElement>() {
        @Override
        public int compare(AdapterElement o1, AdapterElement o2) {
            if (o1.getOrder() == o2.getOrder()) {
                return 0;
            }
            return o1.getOrder() < o2.getOrder() ? -1 : 1;
        }
    };
    private HashMap<Integer, AdapterElement> elementHashMap;
    private ArrayList<AdapterElement> adapterElements;
    private int numberOfDynamicElements = 1;
    private Activity activity;

    public RecyclerElementsAdapter(Activity activity, HashMap<Integer, AdapterElement> elementHashMap) {
        this.activity = activity;
        this.elementHashMap = elementHashMap;
        adapterElements = new ArrayList<>(elementHashMap.values());
        Collections.sort(adapterElements, elementComparator);
    }

    @Override
    public int getItemCount() {
        return (getHeaderItemsCount() + getListItemsCount());
    }

    public HashMap<Integer, AdapterElement> getElementHashMap() {
        return elementHashMap;
    }

    @Override
    public int getItemViewType(int position) {
        if (isListValueValidAndNotEmpty()) {
            if (isPositionInList(position)) {
                return getDynamicAdapterElement().getItemViewType(position);
            }
            if (position > getStartPositionOfList()) {
                position = position - getListItemsCount();
            }
        }
        return adapterElements.get(position).getType();
    }

    /**
     * TODO to be tested for a position in list and on static element
     * Get Object value at the position in list
     * @param position in the all list (not just in in the list element)
     * @return Object in the List Type
     */
    public Object getValue(int position){
        int itemViewType = this.getItemViewType(position);
        try {
            if (itemViewType == LIST_TYPE) {
               return getValueInList(position);
            }else{
                return elementHashMap.get(itemViewType);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private boolean isPositionInList(int position) {
        if(getDynamicAdapterElement()==null){
            return false;
        }
        int startPositionList = getStartPositionOfList();
        int endPositionList = getEndPositionOfList();
        return position >= startPositionList && position <= endPositionList;
    }

    @Override
    public BasicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(elementHashMap.containsKey(viewType)){
            return elementHashMap.get(viewType).getViewHolder(viewType);

        }else if(getDynamicAdapterElement()!=null){
            return getDynamicAdapterElement().getViewHolder(viewType);
        }else{
            return null;//TODO threat case?
        }
    }

    @Override
    public void onBindViewHolder(BasicViewHolder uncastHolder, final int position) {
        if (uncastHolder instanceof DynamicViewHolder) {
            AdapterElement adapterElement = elementHashMap.get(LIST_TYPE);
            if (isListValueValidAndNotEmpty()) {
                DynamicAdapterElement dynamicAdapterElement = ((DynamicAdapterElement) adapterElement);
                Object data = dynamicAdapterElement.getData(position);
                ((DynamicViewHolder) uncastHolder).setupWithData(activity, data);
                ((DynamicViewHolder) uncastHolder).getView().setBackgroundColor(ContextCompat.getColor(activity, R.color.general_background_light_gray));
            }
        }
    }
    public void updateListType(DynamicAdapterElement adapterElement,List newList){
        if(newList!=null && !newList.isEmpty() && adapterElement!=null){
            adapterElement.setList(newList);
            elementHashMap.put(LIST_TYPE,adapterElement);
        }else{
            DynamicAdapterElement dynamicAdapterElement = (DynamicAdapterElement) getElementHashMap().get(LIST_TYPE);
            if(dynamicAdapterElement!=null){
                elementHashMap.remove(LIST_TYPE);
            }
        }
        notifyElementsDataChange();
    }

    private int getHeaderItemsCount() {
        if (adapterElements.size() > 1)
            return adapterElements != null ? adapterElements.size() : 0;
        else
            return 0;
    }

    private int getListItemsCount() {
        if ( isListValueValidAndNotEmpty()) {
            AdapterElement adapterElement = elementHashMap.get(LIST_TYPE);
            return ((DynamicAdapterElement) adapterElement).getChildrenCount() - getNumberOfDynamicElements();
        }else{
            return 0;
        }
    }

    public int getNumberOfDynamicElements() {
        return numberOfDynamicElements;
    }

    public void setNumberOfDynamicElements(int val) {
        numberOfDynamicElements = val;
    }

    private int getStartPositionOfList() {
        DynamicAdapterElement adapterElement = getDynamicAdapterElement();
        return adapterElement.getOrder();
    }

    private int getEndPositionOfList() {
        DynamicAdapterElement adapterElement = getDynamicAdapterElement();
        return adapterElement.getOrder() + adapterElement.getChildrenCount() - 1;
    }

    public Object getValueInList(int position) throws Exception {
        DynamicAdapterElement dynamicAdapterElement = getDynamicAdapterElement();
        if(!isListValueValid()){
            throw new Exception("No valid DynamicAdapterElement in Hashmap");
        }else if(!isPositionInList(position)){
            throw new Exception("This position is not in the DynamicAdapterElement");
        }else{
            return dynamicAdapterElement.getData(position);
        }
    }

    private DynamicAdapterElement getDynamicAdapterElement() {
        return (DynamicAdapterElement) elementHashMap.get(LIST_TYPE);
    }

    private boolean isListValueValid() {
        AdapterElement adapterElement = elementHashMap.get(LIST_TYPE);
        return adapterElement != null && adapterElement instanceof DynamicAdapterElement;
    }
    private boolean isListValueValidAndNotEmpty() {
        AdapterElement adapterElement = elementHashMap.get(LIST_TYPE);
        boolean dynamicElementExists =  adapterElement != null && adapterElement instanceof DynamicAdapterElement;
        if(!dynamicElementExists){
            return false;
        }
        DynamicAdapterElement dynamicAdapterElement = ((DynamicAdapterElement) adapterElement);
        //List is a valid realm list
        if(dynamicAdapterElement.getList()!=null && dynamicAdapterElement.getList() instanceof RealmList){
            return  ((RealmList) dynamicAdapterElement.getList()).isValid() && !dynamicAdapterElement.getList().isEmpty();
        }else{
            //list is a simple valid list
            return !dynamicAdapterElement.getList().isEmpty();
        }
    }

    @Override
    public long getItemId(int position) {
        if(isPositionInList(position)){
            try {
                return getValueInList(position).hashCode();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            int itemViewType = this.getItemViewType(position);
            if(elementHashMap.containsKey(itemViewType))
                return (elementHashMap.get(itemViewType)).hashCode();
        }
        return super.getItemId(position);
    }

    public void notifyElementsDataChange() {
        activity.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        adapterElements = new ArrayList<>(elementHashMap.values());
                        Collections.sort(adapterElements, elementComparator);
                        notifyDataSetChanged();
                    }
                });
    }
}

