package mayank.example.zendor;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by mayan on 1/29/2018.
 */

public class ShowToast {

    private Toast toast;
    private Snackbar snackbar;


    public ShowToast(Context context, String text){
       setToast(context, text);
    }

    private void setToast(Context context, String text){


        snackbar = Snackbar.make(MainActivity.view, text, Snackbar.LENGTH_SHORT);

        toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        View view = toast.getView();
        view.setBackgroundResource(R.drawable.toast_background);
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        v.setTextColor(Color.parseColor("#166e5e"));
    }

    public void showToast(){
        snackbar.show();
    }

    public void dismissToast(){
        Log.e("oh yeah", "askjdkasjdkjsa");
        snackbar.dismiss();
    }



}
