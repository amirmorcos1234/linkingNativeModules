package ro.vodafone.mcare.android.ui.views;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ro.vodafone.mcare.android.R;

/**
 * Created by Alex on 3/16/2017.
 */

public class SmallErrorView extends LinearLayout {

    private Context context;

    private View view;
    TextView errorText;
    private ErrorEventHandler errorEventHandler = null;
    RelativeLayout smallErrorConatiner;

    private Object target;
    boolean refresh;

    public SmallErrorView(Context context) {
        super(context);
    }

    public SmallErrorView(Context context, String text, Object target, boolean refreshable) {
        super(context);
        this.context = context;
        refresh = refreshable;
        init(text, target, refresh);
    }

    private void init(String text, Object target, boolean refresh) {
        view = inflate(context, R.layout.small_error_layout, null);

        this.errorText = (TextView) view.findViewById(R.id.error_text);
        smallErrorConatiner = (RelativeLayout) view.findViewById(R.id.small_error_conatiner);

        if (refresh) {
            smallErrorConatiner.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (errorEventHandler != null)
                        errorEventHandler.onErrorPress();
                }
            });
        }


        if (text != null) {
            this.errorText.setVisibility(VISIBLE);
            this.errorText.setText(text);
        }

        setTarget(target);
    }

    public void addSmallErrorView(ViewGroup viewGroup, LayoutParams layoutParams) {
        viewGroup.addView(view, layoutParams);
    }

    public View getView() {
        return view;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public void setErrorViewHandler(ErrorEventHandler eventHandler) {
        this.errorEventHandler = eventHandler;
    }

    public interface ErrorEventHandler {
        void onErrorPress();
    }
}
