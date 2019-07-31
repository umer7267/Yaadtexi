package com.travel.taxi.Utils;

import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.WindowManager;

public class DefaultApplication extends Application {
    // Gloabl declaration of variable to use in whole app


    public static boolean activityVisible; // Variable that will check the
    // current activity state
    public static boolean conected;
    Context context = getApplicationContext();

    public static boolean isActivityVisible() {
        return activityVisible; // return true or false
    }

    public static void activityResumed() {
        activityVisible = true;// this will set true when activity resumed

    }

    public static void activityPaused() {
        activityVisible = false;// this will set false when activity paused

    }

    /**
     * show example alertdialog on context -method could be moved to other class
     * (eg. MyClass) or marked as static & used by MyClas.showAlertDialog(Context)
     * context is obtained via getApplicationContext()
     */
    public static void showAlertDialog(Context context) {
        /** define onClickListener for dialog */
        DialogInterface.OnClickListener listener
                = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // do some stuff eg: context.onCreate(super)
            }
        };

        /** create builder for dialog */
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setCancelable(false)

                .setTitle("No INternet connection")
                .setPositiveButton("OK", listener);
        /** create dialog & set builder on it */
        Dialog dialog = builder.create();
        /** this required special permission but u can use aplication context */
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        /** show dialog */
        dialog.show();
    }

    public void show() {
        showAlertDialog(context);
    }

}