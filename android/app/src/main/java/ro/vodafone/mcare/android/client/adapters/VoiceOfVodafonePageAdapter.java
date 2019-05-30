package ro.vodafone.mcare.android.client.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafone;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneController;
import ro.vodafone.mcare.android.ui.views.ResizeViewPager;
import ro.vodafone.mcare.android.widget.voiceofvodafone.VoiceOfVodafoneBasic;
import ro.vodafone.mcare.android.widget.voiceofvodafone.VoiceOfVodafoneDialog;
import ro.vodafone.mcare.android.widget.voiceofvodafone.VoiceOfVodafoneNotification;

/**
 * Created by Victor Radulescu on 1/26/2017.
 */

public class VoiceOfVodafonePageAdapter extends PagerAdapter  {


    private ViewGroup container;
    private LayoutInflater layoutInflater;
    private ArrayList<VoiceOfVodafoneBasic> items;
    private Context context;


    public VoiceOfVodafonePageAdapter(Context context, List<VoiceOfVodafone> listOfItems) {
        this.context = context;
        this.items = getViewsFromPojos(listOfItems);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(items.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        this.container = container;
        View v = items.get(position);
        container.addView(v);
        return v;
    }

    private synchronized ArrayList<VoiceOfVodafoneBasic> getViewsFromPojos(List<VoiceOfVodafone> voiceOfVodafones) {
        ArrayList<VoiceOfVodafoneBasic> voiceOfVodafoneBasics = new ArrayList<>();
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (int i = 0; i < voiceOfVodafones.size(); i++) {
            voiceOfVodafoneBasics.add(i, createView(voiceOfVodafones.get(i), i));
        }
        return voiceOfVodafoneBasics;
    }

    private synchronized VoiceOfVodafoneBasic createView(VoiceOfVodafone voiceOfVodafone, int position) {
        switch (voiceOfVodafone.getVovType()) {
            case Notification:
                VoiceOfVodafoneNotification vovNotification = setNotificationVOV(voiceOfVodafone, position, container);
                vovNotification.setVoiceOfVodafone(voiceOfVodafone);
                vovNotification.setTag("vov" + position);
                return vovNotification;

            case Dialog:
                VoiceOfVodafoneDialog vovDialog = setDialogVOV(voiceOfVodafone, position, container);
                vovDialog.setVoiceOfVodafone(voiceOfVodafone);
                vovDialog.setTag("vov" + position);
                return vovDialog;
        }

        return null;
    }


    @Override
    public synchronized int getCount() {
        return items.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }

    public synchronized int removeViewNoTransation(ViewPager pager, int position) {
        try {
            // will in turn cause a null pointer ref.
            if (position >= 0) {
                pager.setAdapter(null);
                items.remove(position);
                pager.setAdapter(this);
                notifyDataSetChanged();
                VoiceOfVodafoneController.getInstance().removeVoiceOfVodafone(items.get(position).getVoiceOfVodafone(), false,false);
                //pager.setCurrentItem(positionNext);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return position;
    }
    public synchronized void  removeViewNoTransation(ViewPager pager, VoiceOfVodafone voiceOfVodafone) {
        try {
            // will in turn cause a null pointer ref.
            if (voiceOfVodafone != null) {
                pager.setAdapter(null);
                items.remove(voiceOfVodafone);
                pager.setAdapter(this);
                notifyDataSetChanged();
                //pager.setCurrentItem(positionNext);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //-----------------------------------------------------------------------------
    // Removes the "view" at "position" from "views".
    // Retuns position of removed view.
    // The app should call this to remove pages; not used by ViewPager.
    public synchronized int removeView(ViewPager pager, int position, int positionNext) {
        // ViewPager doesn't have a delete method; the closest is to set the adapter
        // again.  When doing so, it deletes all its views.  Then we can delete the view
        // from from the adapter and finally set the adapter to the pager again.  Note
        // that we set the adapter to null before removing the view from "views" - that's
        // because while ViewPager deletes all its views, it will call destroyItem which
        try {
            // will in turn cause a null pointer ref.
            if (position >= 0) {
                pager.setAdapter(null);
                VoiceOfVodafone voiceOfVodafoneToRemove = items.get(position).getVoiceOfVodafone();
                items.remove(position);
                pager.setAdapter(this);

                notifyDataSetChanged();

                pager.setCurrentItem(positionNext);
                VoiceOfVodafoneController.getInstance().removeVoiceOfVodafone(voiceOfVodafoneToRemove, true,false);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return position;
    }


    @Override
    public int getItemPosition(Object object) {
        int index = items.indexOf(object);
        if (index == -1)
            return POSITION_NONE;
        else
            return index;
    }

    /**
     * @param viewPager
     * @param voiceOfVodafone
     * @param positionToBeAdded
     * @return Current postion new position
     */
    public synchronized int add(ViewPager viewPager, final VoiceOfVodafone voiceOfVodafone, int positionToBeAdded) {
        int currentItem = viewPager.getCurrentItem();
        //VoiceOfVodafoneBasic currentVOV = getView(currentItem);
        final VoiceOfVodafoneBasic voiceOfVodafoneBasic = createView(voiceOfVodafone, positionToBeAdded);

        viewPager.setAdapter(null);
        items.add(positionToBeAdded, voiceOfVodafoneBasic);
        viewPager.setAdapter(this);

        Log.d("VOV", "Added value comed from" + currentItem + " new position" + positionToBeAdded);
        notifyDataSetChanged();
        VoiceOfVodafoneController.getInstance().adddVoiceOfVodafone(positionToBeAdded, voiceOfVodafone);
        int currentItemNewPostion = getNewPostionAfterAdd(currentItem, positionToBeAdded);
        viewPager.setCurrentItem(currentItemNewPostion, true);

        return currentItemNewPostion;
    }

    public synchronized void replace(VoiceOfVodafone voiceOfVodafone, int positionToReplace) {
        VoiceOfVodafoneBasic newVovView = createView(voiceOfVodafone, positionToReplace);
        //viewPager.setAdapter(null);
        items.remove(positionToReplace);
        notifyDataSetChanged();
        items.add(positionToReplace, newVovView);
        //viewPager.setAdapter(this);
        notifyDataSetChanged();
    }

    public VoiceOfVodafoneBasic getView(int position) {
        return items.get(position);
    }

    public ArrayList<VoiceOfVodafoneBasic> getItems() {
        return items;
    }

    public void setItems(ArrayList<VoiceOfVodafoneBasic> items) {
        this.items = items;
    }

    private int getNewPostionAfterAdd(int olderPosition, int positionAdded) {
        if (positionAdded == 0) {
            if (olderPosition == 0) {
                return 1;
            } else if (olderPosition == 1) {
                return 0;
            }
        } else if (positionAdded == 1) {
            if (olderPosition == 1) {
                return 0;
            } else if (olderPosition == 0) {
                return 0;
            }
        }
        return olderPosition;
    }


    private VoiceOfVodafoneNotification setNotificationVOV(VoiceOfVodafone voiceOfVodafone, int position, ViewGroup container) {
        VoiceOfVodafoneNotification voiceOfVodafoneNotification = (VoiceOfVodafoneNotification) layoutInflater.inflate(voiceOfVodafone.getLayout(), container, false);

        if (voiceOfVodafoneNotification.getHeadline() != null) {
            voiceOfVodafoneNotification.setHeadline(voiceOfVodafone.getTitle());
        } else {
            voiceOfVodafoneNotification.hideHeadline();
        }
        voiceOfVodafoneNotification.setMessage(voiceOfVodafone.getMessage());


        return voiceOfVodafoneNotification;
    }

    private VoiceOfVodafoneDialog setDialogVOV(VoiceOfVodafone voiceOfVodafone, int position, ViewGroup container) {
        VoiceOfVodafoneDialog voiceOfVodafoneDialog = (VoiceOfVodafoneDialog) layoutInflater.inflate(voiceOfVodafone.getLayout(), container, false);
        if (voiceOfVodafoneDialog.getHeadline() != null) {
            voiceOfVodafoneDialog.setHeadline(voiceOfVodafone.getTitle());
        } else {
            voiceOfVodafoneDialog.hideHeadline();
        }
        voiceOfVodafoneDialog.setMessage(voiceOfVodafone.getMessage());

        voiceOfVodafoneDialog.showLeftButton(voiceOfVodafone.isShowLeftBtn());
        voiceOfVodafoneDialog.showRightButton(voiceOfVodafone.isShowRightBtn());

        voiceOfVodafoneDialog.setButtonLeftText(voiceOfVodafone.getLeftButtonTitle());
        voiceOfVodafoneDialog.setButtonRighttText(voiceOfVodafone.getRightButtonTitle());

        View.OnClickListener leftClickListener = VoiceOfVodafoneController.getInstance().getClickListenerByAction(context,voiceOfVodafone.getLeftAction(), voiceOfVodafone.getParameter(),voiceOfVodafone, true);
        View.OnClickListener rightClickListener = VoiceOfVodafoneController.getInstance().getClickListenerByAction(context,voiceOfVodafone.getRightAction(), voiceOfVodafone.getParameter(),voiceOfVodafone, false);

        voiceOfVodafoneDialog.setOnClickLeftButtonListener(leftClickListener);
        voiceOfVodafoneDialog.setOnClickRightButtonListener(rightClickListener);

        return voiceOfVodafoneDialog;
    }
    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        ResizeViewPager pager = (ResizeViewPager) container;
        pager.requestLayout();
    }


}
