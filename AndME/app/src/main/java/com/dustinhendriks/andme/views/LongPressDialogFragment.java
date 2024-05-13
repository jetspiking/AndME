package com.dustinhendriks.andme.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.dustinhendriks.andme.R;
import com.dustinhendriks.andme.utils.AppMiscDefaults;

/**
 * Creates a dialog with button that should be used as confirmation or settings popup.
 */
public class LongPressDialogFragment {
    private View mView;
    private AlertDialog mDialog;
    private TextView mText;

    /**
     * Create a dialog.
     * @param context Application context.
     * @param text String to display.
     */
    public LongPressDialogFragment(Context context, String text) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.DialogTheme);
        mView = View.inflate(context, R.layout.item_dialog_longpress, null);
        mText = mView.findViewById(R.id.item_dialog_longpress_tv_opt1);
        mText.setTextColor(AppMiscDefaults.ACCENT_COLOR);
        mView.setBackgroundColor(AppMiscDefaults.TEXT_COLOR);
        setDialogOptionText(text);
        alertDialogBuilder.setView(mView);
        mDialog = alertDialogBuilder.create();
    }

    /**
     * Show the dialog window at position y on the display.
     * @param y Position on the vertical axis to display the dialog on.
     */
    public void show(int y) {
        mDialog.show();
        Window window = mDialog.getWindow();
        assert window != null;
        window.setGravity(Gravity.TOP);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.y = y;
        window.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
    }

    /**
     * Close the dialog.
     */
    public void dismiss() {
        mDialog.dismiss();
    }

    /**
     * Set the dialog text.
     * @param text String to display.
     */
    public void setDialogOptionText(String text) {
        mText.setText(text);
    }

    /**
     * Set listener for on click event.
     * @param onClickListener Listener.
     */
    public void subscribeOption1(View.OnClickListener onClickListener) {
        mText.setOnClickListener(v -> {
            onClickListener.onClick(v);
            mDialog.dismiss();
        });
    }

    /**
     * Set listener for cancel event.
     * @param onCancelListener Listener.
     */
    public void subscribeCancelListener(DialogInterface.OnCancelListener onCancelListener) {
        mDialog.setOnCancelListener(dialog -> {
            onCancelListener.onCancel(dialog);
            dialog.cancel();
        });
    }
}
