package com.example.plugin;


import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by weihong on 17-12-19.
 */

public class TestActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Button button = new Button(mProxyActivity);
        button.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        button.setBackgroundColor(Color.YELLOW);
        button.setText(R.string.plugin_test);
        setContentView(button);
    }

    @Override
    protected void onResume() {
        if (mFrom == FROM_INTERNAL) {
            super.onResume();
        }
        Log.d(TAG, "TestActivity onResume");
        Toast.makeText(mProxyActivity, "TestActivity onResume", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        if (mFrom == FROM_INTERNAL) {
            super.onPause();
        }
        Log.d(TAG, "TestActivity onPause");
        Toast.makeText(mProxyActivity, "TestActivity onPause", Toast.LENGTH_SHORT).show();
    }
}