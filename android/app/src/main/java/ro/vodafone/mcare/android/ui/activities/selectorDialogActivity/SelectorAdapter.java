package ro.vodafone.mcare.android.ui.activities.selectorDialogActivity;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;


import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.realm.hierarchy.Ban;
import ro.vodafone.mcare.android.client.model.realm.hierarchy.Subscriber;
import ro.vodafone.mcare.android.client.model.vodafoneTv.TvHierarchy;
import ro.vodafone.mcare.android.ui.utils.PhoneNumberUtils;


/**
 * Created by Alex on 2/3/2017.
 */

public class SelectorAdapter extends  BaseAdapter{

    private List<Object> items;
    private LayoutInflater inflater;
    private Context context;
    int chechedPosition = 0;
    int preSelectedItem = 0;
    Object selectedValue;
    boolean isMsisdnSelector;

    public static final String TAG = "Selectro List Adapter";

    public SelectorAdapter(Context context, List<Object> items, int preSelectedItem) {
        inflater = LayoutInflater.from(context);
        this.preSelectedItem = preSelectedItem;
        Log.d(TAG, "size of messages list:" + items.size());
        Log.d(TAG, "preSelectedItem is:" + preSelectedItem);
        this.context = context;
        this.items = items;

        if (items instanceof Ban) {
            this.isMsisdnSelector = false;
        }

        if(preSelectedItem != -1 )
            selectedValue = items.get(preSelectedItem);

    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup root) {

        final Object item = getItem(position);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.selector_item, root, false);
        }

        TextView selectroText = (TextView) convertView.findViewById(R.id.select_item_text);
        CircleImageView draweeView = (CircleImageView) convertView.findViewById(R.id.select_item_avater_image);
        final ImageView checkedImage = (ImageView) convertView.findViewById(R.id.select_item_checked_imgae);

        draweeView.setDrawingCacheEnabled(true);

        if (item instanceof Ban) {
            Log.d(TAG, "Ban Selector");
            selectroText.setText(((Ban) item).getNumber());
            draweeView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.default_avatar_icon)); //phone_icon
            }else if (item instanceof TvHierarchy) {
            String avatarUrl = ((TvHierarchy)item).getAvatarUrl();
            String alias = ((TvHierarchy)item).getAlias();
            String serviceId =  ((TvHierarchy)item).getServiceId();

            selectroText.setText(alias!=null ? alias : serviceId.startsWith("4") ? serviceId.substring(1) : serviceId);

            if(((TvHierarchy) item).getServiceType().toLowerCase().contains("fixed")) {
                if(avatarUrl != null) {
                    Glide.with(context)
                            .load(avatarUrl)
                            .placeholder(R.mipmap.ic_fixed_net)
                            //RoundedImageView are known to have issues with TransitionDrawable use .dontAnimate() to fix the issue.
                            .dontAnimate()
                            .error(R.mipmap.ic_fixed_net)
                             .into(draweeView); }
                else
                    Glide.with(context)
                            .load(R.mipmap.ic_fixed_net)
                            .into(draweeView);
            } else {
                if (avatarUrl != null) {
                    Glide.with(context)
                            .load(avatarUrl)
                            .placeholder(R.drawable.phone_icon)
                            //RoundedImageView are known to have issues with TransitionDrawable use .dontAnimate() to fix the issue.
                            .dontAnimate()
                            .error(R.drawable.phone_icon)
                            .into(draweeView);
                } else
                    Glide.with(context)
                            .load(R.drawable.phone_icon)
                            .into(draweeView);
            }
         }
            else{
            draweeView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.phone_icon)); //phone_icon
            String avatarUrl = ((Subscriber) item).getAvatarUrl();
            String alias = ((Subscriber) item).getAlias();
            String msisdn = PhoneNumberUtils.checkNumberMsisdnFormat(((Subscriber) item).getMsisdn());

            selectroText.setText(alias!=null ? alias : msisdn);

            if(avatarUrl!=null) {
                Glide.with(context)
                        .load(avatarUrl)
                        .placeholder(R.drawable.phone_icon)
                        //RoundedImageView are known to have issues with TransitionDrawable use .dontAnimate() to fix the issue.
                        .dontAnimate()
                        .error(R.drawable.phone_icon)
                        .into(draweeView);
            }
        }

        checkedImage.setImageResource(R.drawable.selector_unchecked_image);

        if(getItem(position).equals(selectedValue)){
            checkedImage.setImageResource(R.drawable.selector_checked_image);
        }

        //Set in on click listener
        RelativeLayout itemContainer = (RelativeLayout) convertView.findViewById(R.id.select_item_container);

        itemContainer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "Button Clicked on Position: "+ position );
                chechedPosition = position;
               if(!getItem(position).equals(selectedValue)){
                   checkedImage.setImageResource(R.drawable.selector_checked_image);
                   preSelectedItem = chechedPosition;

                   notifyDataSetChanged();

                   SelectorDialogActivity selectorDialogActivity = (SelectorDialogActivity) VodafoneController.findActivity(SelectorDialogActivity.class);
                   if(selectorDialogActivity != null){
                       selectorDialogActivity.itemSelected(item);
                    }
               }
            }
        });

        return convertView;
    }

}
