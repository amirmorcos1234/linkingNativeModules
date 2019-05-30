package ro.vodafone.mcare.android.widget.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ro.vodafone.mcare.android.R;

/**
 * Created by alexandrulepadatu on 3/16/18.
 */

public class VodafoneFullscreenDialog extends Dialog
{
    private static final int VDF_THEME = android.R.style.Theme_Black_NoTitleBar;

    private Unbinder unbinder;

    @BindView(R.id.overlayTitle)
    protected TextView overlayTitle;

    @BindView(R.id.viewExtraContent)
    protected LinearLayout viewExtraContent;

    @BindView(R.id.overlaySubtext)
    protected TextView overlaySubtext;

    @BindView(R.id.overlaySubtext2)
    protected TextView overlaySubtext2;

    @BindView(R.id.buttonKeepOn)
    protected TextView buttonKeepOn;

    @BindView(R.id.buttonTurnOff)
    protected TextView buttonTurnOff;

    @BindView(R.id.overlayDismissButton)
    protected View overlayDismissButton;

    public VodafoneFullscreenDialog(@NonNull Context context)
    {
        super(context, VDF_THEME);
    }

    private VodafoneFullscreenDialog(@NonNull Context context, int themeResId)
    {
        super(context, themeResId);
    }

    {
        init();
    }

    private void init()
    {
        setContentView(R.layout.overlay_dialog_notifications);

        unbinder = ButterKnife.bind(this);
    }

    @OnClick(R.id.overlayDismissButton)
    @Override
    public void dismiss()
    {
        super.dismiss();

        unbinder.unbind();
    }


    public static class Builder
    {
        private final VodafoneFullscreenDialog dialog;

        public Builder(Context context)
        {
            dialog = new VodafoneFullscreenDialog(context, VDF_THEME);
        }

        public Builder(Context context, int themeRes)
        {
            dialog = new VodafoneFullscreenDialog(context, themeRes);
        }

        public VodafoneFullscreenDialog.Builder setTitle(int titleId)
        {
            dialog.overlayTitle.setText(titleId);
            return this;
        }

        public VodafoneFullscreenDialog.Builder setTitle(CharSequence title)
        {
            dialog.overlayTitle.setText(title);
            return this;
        }

        public VodafoneFullscreenDialog.Builder setMessage(int messageId)
        {
            dialog.overlaySubtext.setText(messageId);
            return this;
        }

        public VodafoneFullscreenDialog.Builder setMessage(CharSequence message)
        {
            dialog.overlaySubtext.setText(message);
            return this;
        }

        public VodafoneFullscreenDialog.Builder setSubtext2(int messageId)
        {
            dialog.overlaySubtext2.setText(messageId);
            dialog.overlaySubtext2.setVisibility(View.VISIBLE);
            return this;
        }

        public VodafoneFullscreenDialog.Builder setSubtext2(CharSequence message)
        {
            dialog.overlaySubtext2.setText(message);
            dialog.overlaySubtext2.setVisibility(View.VISIBLE);
            return this;
        }

        public VodafoneFullscreenDialog.Builder setPositiveButton(int textId, final OnClickListener listener)
        {
            setPositiveButton(dialog.getContext().getText(textId), listener);

            return this;
        }

        public VodafoneFullscreenDialog.Builder setPositiveButton(CharSequence text, final OnClickListener listener)
        {
            dialog.buttonKeepOn.setText(text);
            dialog.buttonKeepOn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null)
                        listener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);

                    dialog.dismiss();
                }
            });

            return this;
        }

        public VodafoneFullscreenDialog.Builder setNeutralButton(final OnClickListener listener)
        {
            dialog.overlayDismissButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null)
                        listener.onClick(dialog, DialogInterface.BUTTON_NEUTRAL);

                    dialog.dismiss();
                }
            });

            return this;
        }

        public VodafoneFullscreenDialog.Builder setNegativeButton(int textId, final OnClickListener listener)
        {
            setNegativeButton(dialog.getContext().getText(textId), listener);

            return this;
        }

        public VodafoneFullscreenDialog.Builder setNegativeButton(CharSequence text, final OnClickListener listener)
        {
            dialog.buttonTurnOff.setText(text);
            dialog.buttonTurnOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null)
                        listener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);

                    dialog.dismiss();
                }
            });

            return this;
        }

        public VodafoneFullscreenDialog.Builder setView(View view)
        {
            dialog.viewExtraContent.addView(view);

            return this;
        }

        public VodafoneFullscreenDialog show()
        {
            dialog.show();
            return dialog;
        }

        public VodafoneFullscreenDialog create()
        {
            return dialog;
        }
    }
}
