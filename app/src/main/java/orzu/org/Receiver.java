package orzu.org;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.analytics.CampaignTrackingReceiver;


public class Receiver extends BroadcastReceiver {

    // The name of the referrer string broadcast by Google Play Store.
    private static final String PLAY_STORE_REFERRER_KEY = "referrer";

    @Override
    public void onReceive(Context context, Intent intent) {

        Common.referrer = intent.getStringExtra(PLAY_STORE_REFERRER_KEY);
        if(Common.referrer!=null&&Common.referrer.length()>15){
            Common.referrer = "";
        }
        // Do something with the referrer.
        // When you're done, pass the intent to the Google Analytics Campaign Tracking Receiver.
        new CampaignTrackingReceiver().onReceive(context, intent);

    }
}