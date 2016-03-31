package com.squadpay;

import android.app.Activity;
import android.os.Bundle;

// usually extends AppCompatActivity but I changed it to activity so that there would
// be no action bar. May be a better way to fix this
public class MainActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
