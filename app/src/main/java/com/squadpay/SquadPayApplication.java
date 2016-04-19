package com.squadpay;

import com.firebase.client.Firebase;

/**
 * @author Chris Sekira
 * @since 04/18/16
 *
 * Initialize Firebase with the application context. This must happen before the client is used.
 */
public class SquadPayApplication extends android.app.Application{
    @Override
    public void onCreate(){
        super.onCreate();
        Firebase.setAndroidContext(this);
    }

}
