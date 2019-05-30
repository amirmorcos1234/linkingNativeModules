package ro.vodafone.mcare.android.ui.fragments.yourProfile.UserRequests;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.client.model.realm.system.AppLabels;
import ro.vodafone.mcare.android.client.model.userRequest.UserRequest;
import ro.vodafone.mcare.android.utils.DateUtils;
import ro.vodafone.mcare.android.utils.StringUtils;

/**
 * Created by alexandrulepadatu on 3/6/18.
 */

public class PendingRequestsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private List<Object> listData;

    private int indexSelected = 0;

    private static final int CELL_TYPE_HEADER       = 0;
    private static final int CELL_TYPE_USER_REQUEST = 1;

    PendingRequestsAdapter(List<Object> listData)
    {
        super();

        this.listData = listData;
    }

    int getIndexSelected()
    {
        return indexSelected;
    }

    void setIndexSelected(int indexSelected)
    {
        this.indexSelected = indexSelected;
    }

    @Override
    public int getItemCount()
    {
        return listData != null ? listData.size() : 0;
    }

    @Override
    public int getItemViewType(int position)
    {
        if (position >= 0 && listData.get(position) instanceof String)
            return CELL_TYPE_HEADER;
        return CELL_TYPE_USER_REQUEST;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == CELL_TYPE_HEADER)
            return new HeaderViewHolder(inflater.inflate(R.layout.header_timed_user_request, parent, false));

        return new RequestViewHolder(inflater.inflate(R.layout.list_item_user_requests_pending, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        if (getItemViewType(position) == CELL_TYPE_HEADER)
        {
            HeaderViewHolder headerViewHolder = ((HeaderViewHolder) holder);

            headerViewHolder.txtHeader.setText((CharSequence) listData.get(position));
            headerViewHolder.separator.setVisibility(position == 0 ? View.INVISIBLE : View.VISIBLE);
            return;
        }

        // CELL_TYPE_USER_REQUEST
        RequestViewHolder requestViewHolder = (RequestViewHolder) holder;
        UserRequest request = (UserRequest) listData.get(position);

        requestViewHolder.txtTitle.setText(Html.fromHtml(request.getRequestDescription()));

        String requestTime = StringUtils.capitalizeFirstLetter(DateUtils.translateDateStringToRo(request.getRequestTime(), "d MMM yyyy"));

        String subtitle = String.format("%s %s %s %s",
                    AppLabels.getFromLabel(), request.getRequesterPhoneNumber(),
                    AppLabels.getOnLabel(), requestTime);
        requestViewHolder.txtSubtitle.setText(subtitle);

        if (request.getStatus() == UserRequest.Status.PENDING.getValue())
            requestViewHolder.imgActivate.setChecked(position == indexSelected);
        else
            requestViewHolder.imgActivate.setVisibility(View.GONE);

        if (position != 0)          // draw the separator
        {
            requestViewHolder.separator.setVisibility(View.VISIBLE);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) requestViewHolder.separator.getLayoutParams();
            int margin = getItemViewType(position - 1) == CELL_TYPE_HEADER ? 0: (int) requestViewHolder.separator.getResources().getDimension(R.dimen.activity_horizontal_margin);
            params.setMargins(margin, 0, margin, 0);
            params.setMarginStart(margin);
            params.setMarginEnd(margin);
            requestViewHolder.separator.setLayoutParams(params);
        }
        else
            requestViewHolder.separator.setVisibility(View.INVISIBLE);
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder
    {
        TextView txtHeader;
        View separator;

        HeaderViewHolder(View itemView)
        {
            super(itemView);

            txtHeader = itemView.findViewById(R.id.txtHeader);
            separator = itemView.findViewById(R.id.separator);
        }
    }

    private class RequestViewHolder extends RecyclerView.ViewHolder
    {
        CheckBox imgActivate;
        TextView txtTitle;
        TextView txtSubtitle;
        View separator;

        RequestViewHolder(View itemView)
        {
            super(itemView);

            imgActivate = itemView.findViewById(R.id.imgActivate);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtSubtitle = itemView.findViewById(R.id.txtSubtitle);
            separator = itemView.findViewById(R.id.separator);
        }
    }
}
