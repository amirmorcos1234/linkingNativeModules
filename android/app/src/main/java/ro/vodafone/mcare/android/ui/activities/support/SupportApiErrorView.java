package ro.vodafone.mcare.android.ui.activities.support;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.utils.D;

/**
 * Created by cristi on 24/06/2017.
 * .
 */

public class SupportApiErrorView extends MyChatView {

    private final SupportWindow window;
    String errorMessage = "";


    public SupportApiErrorView(@NonNull SupportWindow w, String errorMessage, View.OnClickListener clickListener,boolean showEmailButton) {
        super(w.getContext());
        this.window = w;
        window.findViewById(R.id.slyceInputID).setVisibility(GONE);
        if (errorMessage != null) {
            this.errorMessage = errorMessage;
        }

        D.w();
        View v = LayoutInflater.from(getContext()).inflate(R.layout.fragment_support_chat_error, this);
        D.w();
        Button gotoEmail = (Button) v.findViewById(R.id.nextStep);

        if(!showEmailButton)
            gotoEmail.setVisibility(GONE);


        v.findViewById(R.id.refreshLayout).setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
        v.findViewById(R.id.refreshLayout).setOnClickListener(clickListener);
        if (errorMessage == null || errorMessage.isEmpty()) {
            ((TextView) v.findViewById(R.id.errorMessage)).setText("Momentan serviciul de chat este indisponibil din motive tehnice. Lucrăm la remedierea lor.");
        } else {
            ((TextView) v.findViewById(R.id.errorMessage)).setText(errorMessage);
            ((TextView) v.findViewById(R.id.errorMessage)).setTextSize(TypedValue.COMPLEX_UNIT_SP,14f);
        }
        gotoEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                window.inflateEmailLayout();
            }
        });
    }

    public SupportApiErrorView(@NonNull SupportWindow w, String errorMessage) {
        super(w.getContext());
        this.window = w;
        window.findViewById(R.id.slyceInputID).setVisibility(GONE);
        if (errorMessage != null) {
            this.errorMessage = errorMessage;
        }

        D.w();
        View v = LayoutInflater.from(getContext()).inflate(R.layout.fragment_support_chat_error, this);
        D.w();
        Button gotoEmail = (Button) v.findViewById(R.id.nextStep);

        v.findViewById(R.id.refreshLayout).setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));

        if (errorMessage == null || errorMessage.isEmpty()) {
            ((TextView) v.findViewById(R.id.errorMessage)).setText("Momentan serviciul de chat este indisponibil din motive tehnice. Lucrăm la remedierea lor.");
        } else {
            ((TextView) v.findViewById(R.id.errorMessage)).setText(errorMessage);
            ((TextView) v.findViewById(R.id.errorMessage)).setTextSize(TypedValue.COMPLEX_UNIT_SP,14f);
        }
        gotoEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                window.inflateEmailLayout();
            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        //VNM-8346
        //window.views.window_title.setText("Support 24/7");
    }
}
