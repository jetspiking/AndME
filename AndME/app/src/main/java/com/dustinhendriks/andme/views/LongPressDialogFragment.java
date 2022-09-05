package com.dustinhendriks.andme.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;

import com.dustinhendriks.andme.R;

public class LongPressDialogFragment {
    private AlertDialog dialog;
    private TextView option1;
    private TextView option2;

    public LongPressDialogFragment(Context context, String opt1, String opt2) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.item_dialog_longpress, null);
        option1 = (TextView) view.findViewById(R.id.item_dialog_longpress_tv_opt1);
        option2 = (TextView) view.findViewById(R.id.item_dialog_longpress_tv_opt2);
        setOption1Text(opt1);
        setOption2Text(opt2);
        alertDialogBuilder.setView(view);
        dialog = alertDialogBuilder.create();
    }

    public void show() {
        dialog.show();
    }

    public void setOption1Text(String text) {
        option1.setText(text);
    }

    public void setOption2Text(String text) {
        option2.setText(text);
    }

    public void subscribeOption1(View.OnClickListener onClickListener) {
        option1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onClick(v);
                dialog.dismiss();
            }
        });
    }

    public void hideOption2() {
        option2.setVisibility(View.INVISIBLE);
    }

    public void hideOption1() {
        option1.setVisibility(View.INVISIBLE);
    }

    public void subscribeOption2(View.OnClickListener onClickListener) {
        option2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onClick(v);
                dialog.dismiss();
            }
        });
    }

    public void subscribeCancelListener(DialogInterface.OnCancelListener onCancelListener) {
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                onCancelListener.onCancel(dialog);
                dialog.cancel();
            }
        });
    }
}
