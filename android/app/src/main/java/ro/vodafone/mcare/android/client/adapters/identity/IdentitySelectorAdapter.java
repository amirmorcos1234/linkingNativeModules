package ro.vodafone.mcare.android.client.adapters.identity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.CompoundButton;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.ui.activities.authentication.LoginIdentitySelectorActivity;
import ro.vodafone.mcare.android.ui.views.buttons.checkboxes.GetterCheckedListenerAppCompatCheckBox;
import ro.vodafone.mcare.android.ui.views.viewholders.identity.AccountCuiViewHolder;
import ro.vodafone.mcare.android.ui.views.viewholders.identity.AccountHoldingViewHolder;
import ro.vodafone.mcare.android.ui.views.viewholders.identity.BillingCustomerViewHolder;
import ro.vodafone.mcare.android.ui.views.viewholders.identity.ExpandableListTree;
import ro.vodafone.mcare.android.ui.views.viewholders.identity.FinancialAccountViewHolder;
import ro.vodafone.mcare.android.ui.views.viewholders.identity.GroupIdentityViewHolder;
import ro.vodafone.mcare.android.ui.views.viewholders.identity.SubscriberEntityViewHolder;
import ro.vodafone.mcare.android.utils.D;

/**
 * Created by Victor Radulescu on 7/10/2017.
 */

public class IdentitySelectorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Animation.AnimationListener {

    private static final String TAG = IdentitySelectorAdapter.class.getCanonicalName();
    private Context context;
    private RecyclerView mRecyclerView;
    private String defaultEntityId;
    private ExpandableListTree entities;
    List<WeakReference<GetterCheckedListenerAppCompatCheckBox>> selectedCheckboxes = new ArrayList<>();
    private String entityIdGroupToAnimate = null;

    private final int Subscriber = 0, AccountCui = 1, AccountHolding = 2, BillingCustomer = 3 , FinancialAccount = 4;

    public IdentitySelectorAdapter(Context context, ExpandableListTree entities, String defaultEntityId) {
        this.context = context;
        this.entities = entities;
        this.defaultEntityId = defaultEntityId;
        ((LoginIdentitySelectorActivity) context).setEntityId(defaultEntityId);
        setCheckBoxIfIsDefaultCheckbox();
        //entities.get(position) => case class Node(item: EntityChildItem, expanded: Boolean)
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        //Log.d(TAG, "inflate layout for: " + viewType);
        switch (viewType) {
            case Subscriber:
                View subscriberView = inflater.inflate(R.layout.identity_item_layout, parent, false);
                viewHolder = new SubscriberEntityViewHolder(subscriberView, context);
                break;
            case AccountCui:
                View accountCuiView = inflater.inflate(R.layout.identity_item_layout, parent, false);
                viewHolder = new AccountCuiViewHolder(accountCuiView, context);
                break;
            case AccountHolding:
                View accountHoldingView = inflater.inflate(R.layout.identity_item_layout, parent, false);
                viewHolder = new AccountHoldingViewHolder(accountHoldingView, context);
                break;
            case BillingCustomer:
                View billingCustomerView = inflater.inflate(R.layout.identity_item_layout, parent, false);
                viewHolder = new BillingCustomerViewHolder(billingCustomerView, context);
                break;
            case FinancialAccount:
                View financialAccountView = inflater.inflate(R.layout.identity_item_layout, parent, false);
                viewHolder = new FinancialAccountViewHolder(financialAccountView, context);
                break;
            default:
                //TODO change default
                View v = inflater.inflate(R.layout.identity_item_layout, parent, false);
                viewHolder = new SubscriberEntityViewHolder(v, context);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case Subscriber:
                SubscriberEntityViewHolder subscriberEntityViewHolder = (SubscriberEntityViewHolder) viewHolder;
                configureSubscriberViewHolder(subscriberEntityViewHolder, position);
                break;
            case AccountCui:
                AccountCuiViewHolder accountCuiViewHolder = (AccountCuiViewHolder) viewHolder;
                configureAccountCuiViewHolder(accountCuiViewHolder, position);
                break;
            case AccountHolding:
                AccountHoldingViewHolder accountHoldingViewHolder = (AccountHoldingViewHolder) viewHolder;
                configureAccountHoldingViewHolder(accountHoldingViewHolder, position);
                break;
            case BillingCustomer:
                BillingCustomerViewHolder billingCustomerViewHolder = (BillingCustomerViewHolder) viewHolder;
                configureBillingCustomerViewHolder(billingCustomerViewHolder, position);
                break;
            case FinancialAccount:
                FinancialAccountViewHolder financialAccountViewHolder = (FinancialAccountViewHolder) viewHolder;
                configureFinancialAccountViewHolder(financialAccountViewHolder, position);
                break;

            default:
                SubscriberEntityViewHolder vh = (SubscriberEntityViewHolder) viewHolder;
                configureDefaultViewHolder(vh, position);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(entities==null){
            return -1;
        }
        String itemViewType = entities.getTreeItem(position).getItem().getEntityType();

        if (itemViewType.equals(context.getString(R.string.ebu_migrated_entity_subscriber))) {
            return 0;
        } else if (itemViewType.equals(context.getString(R.string.ebu_migrated_entity_account_cui))) {
            return 1;
        }
        else if (itemViewType.equals(context.getString(R.string.ebu_migrated_entity_account_holding))) {
            return 2;
        }
        else if (itemViewType.equals(context.getString(R.string.ebu_migrated_entity_billing_customer))) {
            return 3;
        }
        else if (itemViewType.equals(context.getString(R.string.ebu_migrated_entity_financial_account))) {
            return 4;
        }

        return -1;
    }



    @Override
    public int getItemCount() {
        return entities!=null ? entities.getTreeSize():0;
    }

    //TODO: CR: when is this detached? We're storing a reference to a view but never releasing it. Wouldn't it lead to possible leaks?
    //		is there a corresponding onDetach method that we could use to release the reference?
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        mRecyclerView = recyclerView;
    }


    private void configureDefaultViewHolder(RecyclerView.ViewHolder vh, int position) {
    }
    private void configureAccountCuiViewHolder(final GroupIdentityViewHolder vh, int position) {
        configureGroupViewHolder(vh, position);
    }

    private void configureAccountHoldingViewHolder(final GroupIdentityViewHolder vh, final int position) {
        configureGroupViewHolder(vh, position);
    }

    private void configureGroupViewHolder(final GroupIdentityViewHolder vh, int position) {
        try {
            ExpandableListTree.Node dataNode = entities.getTreeItem(position);
            vh.setData(dataNode);
            if(dataNode.getChildren().size() > 0) {
                animateGroupIfShould(vh);
                vh.itemView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        entityIdGroupToAnimate = vh.getNode().getItem().getEntityId();
                        vh.toggleExpanded();
                        notifyDataSetChanged();
                    }
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void animateGroupIfShould(GroupIdentityViewHolder vh){
        if(entityIdGroupToAnimate!=null && Objects.equals(entityIdGroupToAnimate, vh.getNode().getItem().getEntityId())){
            vh.animateArrowState(this);
        }
    }
    private void configureSubscriberViewHolder(final SubscriberEntityViewHolder vh, final int position) {
        try {
            final ExpandableListTree.Node dataNode = entities.getTreeItem(position);
            vh.setData(dataNode);
            final GetterCheckedListenerAppCompatCheckBox checkBox = vh.getCheckBox();
            setClickListenerForSelectableView(vh.itemView,checkBox);
            setCheckBoxForSelectableIdentities(position, dataNode, checkBox);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void configureBillingCustomerViewHolder(final BillingCustomerViewHolder vh, final int position) {
        try {
            final ExpandableListTree.Node dataNode = entities.getTreeItem(position);
            vh.setData(dataNode);
            final GetterCheckedListenerAppCompatCheckBox checkBox = vh.getCheckBox();
            setClickListenerForSelectableView(vh.itemView,checkBox);
            setCheckBoxForSelectableIdentities(position, dataNode, checkBox);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void configureFinancialAccountViewHolder(final FinancialAccountViewHolder vh, final int position) {
        try {
            final ExpandableListTree.Node dataNode = entities.getTreeItem(position);
            vh.setData(dataNode);
            final GetterCheckedListenerAppCompatCheckBox checkBox = vh.getCheckBox();
            setClickListenerForSelectableView(vh.itemView,checkBox);
            setCheckBoxForSelectableIdentities(position, dataNode, checkBox);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void setClickListenerForSelectableView(View view, final GetterCheckedListenerAppCompatCheckBox checkBox){
        if(view!=null){
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(checkBox!=null){
                        checkBox.setChecked(!checkBox.isChecked());
                    }
                }
            });
        }
    }
    private void setCheckBoxForSelectableIdentities(final int position, final ExpandableListTree.Node dataNode, final GetterCheckedListenerAppCompatCheckBox checkBox) {
        CompoundButton.OnCheckedChangeListener onCheckedChangeListener = checkBox.getOnCheckedChangeListener();
        checkBox.setOnCheckedChangeListener(null);
        if(dataNode.isSelected()){
            checkBox.setChecked(true);
            checkBox.setTag("checkedBox");
            selectedCheckboxes.add(new WeakReference<>(checkBox));
        }else{
            checkBox.setChecked(false);
            checkBox.setTag(null);
        }
        checkBox.setOnCheckedChangeListener(onCheckedChangeListener);
        checkBox.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                D.w("\n");

                D.w(((LoginIdentitySelectorActivity) context).getSelectedEntityId());
                D.w("Node have chidrens "+ (dataNode.getChildren()!=null && !dataNode.getChildren().isEmpty()));
                D.w("OnCheckedChanged "+isChecked+ " position "+position+" node id"+dataNode.getItem().getEntityId());
                entities.updateSelectedStates();
                entities.getTreeItem(position).setSelected(isChecked);

                if(isChecked){
                    GetterCheckedListenerAppCompatCheckBox previousCheckBox = mRecyclerView.findViewWithTag("checkedBox");
                    resetAllSelectedCheckboxes();

                    if(previousCheckBox != null) {
                       // previousCheckBox.setOnCheckedChangeListener(null);
                        //previousCheckBox.setChecked(false);
                        previousCheckBox.setTag(null);
                    }
                    checkBox.setTag("checkedBox");
                    selectedCheckboxes.add(new WeakReference<>(checkBox));
                    ((LoginIdentitySelectorActivity) context).setEntityId(dataNode.getItem().getEntityId());
                }else{
                    GetterCheckedListenerAppCompatCheckBox previousCheckBox = mRecyclerView.findViewWithTag("checkedBox");
                    if(previousCheckBox != null) {
                        previousCheckBox.setTag(null);
                    }
                    resetAllSelectedCheckboxes();
                    ((LoginIdentitySelectorActivity) context).setEntityId(null);
                }

            }
        });
    }

    private void resetAllSelectedCheckboxes() {
        for (WeakReference<GetterCheckedListenerAppCompatCheckBox> checkBoxWeakReference :
                selectedCheckboxes) {
            if (checkBoxWeakReference.get() != null) {
                CompoundButton.OnCheckedChangeListener onCheckedChangeListener = checkBoxWeakReference.get().getOnCheckedChangeListener();
                checkBoxWeakReference.get().setOnCheckedChangeListener(null);
                checkBoxWeakReference.get().setChecked(false);
                checkBoxWeakReference.get().setOnCheckedChangeListener(onCheckedChangeListener);
            }
        }
        selectedCheckboxes = new ArrayList<>();
    }

    private void setCheckBoxIfIsDefaultCheckbox(){
        if(defaultEntityId!=null){
            entities.setNodesSelectedAfterEntityId(defaultEntityId);
        }

    }


    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        entityIdGroupToAnimate = null;
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
