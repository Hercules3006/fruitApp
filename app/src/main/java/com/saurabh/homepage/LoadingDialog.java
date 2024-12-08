package com.saurabh.homepage;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

public class LoadingDialog {

    private Dialog dialog;
    private Runnable run;
    private Handler handler;

    public LoadingDialog(Context context) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_loading);
        Window window = dialog.getWindow();

        if (window != null) {
            window.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT
            );
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.gravity = Gravity.CENTER;
            window.setAttributes(layoutParams);

            dialog.setCancelable(false);

            handler = new Handler();
            run = new Runnable() {
                @Override
                public void run() {
                    try {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.hide();
                        }
                    } catch (Exception e) {
                        // Handle the exception, log if needed
                    }
                }
            };
        }
    }

    public void show() {
        dialog.show();
        if (run != null && handler != null) {
            handler.postDelayed(run, 90000); // 90 seconds delay
        }
    }

    public void hide() {
        dialog.dismiss();
        try {
            if (run != null && handler != null) {
                handler.removeCallbacks(run);
            }
        } catch (Exception e) {
            // Handle the exception, log if needed
        }
    }
}
