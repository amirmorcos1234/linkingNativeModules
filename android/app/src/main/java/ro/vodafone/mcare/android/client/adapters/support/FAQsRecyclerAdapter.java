package ro.vodafone.mcare.android.client.adapters.support;

import android.animation.ObjectAnimator;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.ui.activities.support.SupportWindow;
import ro.vodafone.mcare.android.ui.activities.support.supportModels.Content;
import ro.vodafone.mcare.android.ui.activities.support.supportModels.FaqsList;

/**
 * Created by Bogdan Marica on 9/1/2017.
 */

public class FAQsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int FAQ_ENTRY_TYPE = 103;
    private SupportWindow window;
    private List<FaqsList> itemsList;

    public FAQsRecyclerAdapter(SupportWindow window) {
        this.window = window;
        this.itemsList = new ArrayList<>();
    }

    public void setItemsList(List<FaqsList> itemsList) {
        this.itemsList.clear();
        if (itemsList != null)
            this.itemsList.addAll(itemsList);

        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            default:    //  FAQ_ENTRY_TYPE
                View faqItemView = LayoutInflater.from(window.activity).inflate(R.layout.faq_expandable_recyclerview_item, null);
                return new FAQsRecyclerAdapter.FaqEntryViewholder(faqItemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder uncastHolder, int position) {
        if (uncastHolder instanceof FAQsRecyclerAdapter.FaqEntryViewholder)
            bindFAQsEntryView(uncastHolder, position);
    }

    @Override
    public int getItemCount() {
            return (itemsList.size() + getHeaderItemsCount());
    }

    private void bindFAQsEntryView(RecyclerView.ViewHolder uncastHolder, final int position) {
        FaqEntryViewholder faqHolder = (FaqEntryViewholder) uncastHolder;
        FaqsList faq = itemsList.get(position - getHeaderItemsCount());

        ImageView expandButton = faqHolder.expandButton;

        expandButton.getDrawable().mutate();

        final TextView optionName = faqHolder.optionName;
        final LinearLayout contentContainer = faqHolder.contentContainer;
        RelativeLayout recycler_item_click_Receiver = faqHolder.recycler_item_click_Receiver;

        optionName.setText(faq.getTitle());

        contentContainer.setVisibility(faq.getHasContentExpanded() ? View.VISIBLE : View.GONE);
        displayFullTitleIfNeed(faq, optionName);

        recycler_item_click_Receiver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FaqsList clickedFAQ = itemsList.get(position - getHeaderItemsCount());

                contentContainer.setVisibility(contentContainer.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                for (FaqsList faq : itemsList) {
                    faq.setHasContentExpanded(false);
                    faq.setArrowUp(false);
                    faq.setExecuteAnimation(false);
                }
                clickedFAQ.setHasContentExpanded(contentContainer.getVisibility() == View.VISIBLE);
                clickedFAQ.setArrowUp(contentContainer.getVisibility() == View.VISIBLE);
                clickedFAQ.rotateToInit = contentContainer.getVisibility() == View.VISIBLE;
                clickedFAQ.setExecuteAnimation(true);
                displayFullTitleIfNeed(clickedFAQ, optionName);
            }
        });

        if (faq.getHasContentExpanded())
            populateExpandedSection(contentContainer, faq);
        else {
            contentContainer.removeAllViews();
        }
        arrowAnimationLogic(faq, expandButton);
    }

    private void arrowAnimationLogic(FaqsList faq, final ImageView expandButton) {
        if (faq.isArrowUp()) {
            if (faq.getExecuteAnimation()) {
//                D.d("animate Arrow UP");
                ObjectAnimator animator = ObjectAnimator.ofFloat(expandButton, "rotation", 0, 180);
                animator.setDuration(120);
                animator.start();
                faq.setExecuteAnimation(false);
            } else {
//                D.i("set Arrow UP");
                ObjectAnimator animator = ObjectAnimator.ofFloat(expandButton, "rotation", 0, 180);
                animator.setDuration(0);
                animator.start();
            }
        } else if (faq.getExecuteAnimation()) {
//            D.d("animate Arrow DOWN");
            ObjectAnimator animator = ObjectAnimator.ofFloat(expandButton, "rotation", 180, 0);
            animator.setDuration(120);
            animator.start();
            faq.setExecuteAnimation(false);
        } else {
//            D.i("set Arrow DOWN");
            expandButton.setImageResource(R.drawable.chevron_down_48_red);
            ObjectAnimator animator = ObjectAnimator.ofFloat(expandButton, "rotation", 180, 0);
            animator.setDuration(0);
            animator.start();
        }
    }

    private void displayFullTitleIfNeed(FaqsList faq, TextView optionName) {
        if (faq.getHasContentExpanded()) {
            optionName.setMaxLines(50);
            optionName.setEllipsize(null);
        } else {
            optionName.setMaxLines(2);
            optionName.setEllipsize(TextUtils.TruncateAt.END);
        }
        try {   //  this might be called before onMeasure is finished , and will throw ISE( ex. when page is inited), but we need it when we click, so we have to call notify
            notifyDataSetChanged();
        } catch (IllegalStateException ise) {
            ise.printStackTrace();
        }
    }



    private void populateExpandedSection(ViewGroup contentContainer, final FaqsList faq) {
        contentContainer.removeAllViews();

        for (Content content : faq.getContent()) {
            switch (content.getType()) {
                case "text":
                    contentContainer.addView(window.getFAQView().getFAQTextModel(content.getValue()));
                    break;
                case "image":
                    contentContainer.addView(window.getFAQView().getFAQImageModel(content.getValue()));
                    break;
                case "video":
                    contentContainer.addView(window.getFAQView().getFAQVideoModel(content.getValue()));
                    break;
                case "hiperlink"://TODO DROP2
//                    contentContainer.addView(window.getFAQView().getFAQVideoModel(content.getValue()));
                    break;
                default:
                    break;
            }
        }

        addRelatedQuestionsSection(faq.getRelatedQuestionIds());
        addEmailButton(contentContainer, faq);
    }

    /**
     * TODO DROP2
     */
    private void addRelatedQuestionsSection(List<Integer> relatedQuestionsIds) {
//        if (relatedQuestionsIds != null && relatedQuestionsIds.size() > 0)
//            for (Integer id : relatedQuestionsIds) {
//
//            }
    }

    private void addEmailButton(ViewGroup contentContainer, final FaqsList faq) {
        if (faq.getHasEmailButton()) {
            View buttonView = window.getFAQView().getFAQButtonModel();
            buttonView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    window.inflateEmailLayoutFromFaq(faq.getTitle());
                    window.getFAQView().pushEmailToStack();
                }
            });
            contentContainer.addView(buttonView);
        }
    }

    private int getHeaderItemsCount() {
        return 0;
    }

    public int getItemViewType(int position) {
//        switch (position) {
//            default:
        return FAQ_ENTRY_TYPE;
//        }
    }

    private class FaqEntryViewholder extends RecyclerView.ViewHolder {

        TextView optionName;
        ImageView expandButton;
        LinearLayout contentContainer;
        RelativeLayout recycler_item_click_Receiver;

        FaqEntryViewholder(View itemView) {
            super(itemView);
            optionName = (TextView) itemView.findViewById(R.id.optionName);
            expandButton = (ImageView) itemView.findViewById(R.id.expandButton);
            contentContainer = (LinearLayout) itemView.findViewById(R.id.contentContainer);
            recycler_item_click_Receiver = (RelativeLayout) itemView.findViewById(R.id.recycler_item_click_Receiver);

            expandButton.setImageDrawable(ContextCompat.getDrawable(VodafoneController.currentActivity(), R.drawable.chevron_down_48_red));
        }
    }

    private class EmptyViewHolder extends RecyclerView.ViewHolder {

        View customView;

        EmptyViewHolder(View itemView) {
            super(itemView);
            customView = itemView;
        }
    }
}
