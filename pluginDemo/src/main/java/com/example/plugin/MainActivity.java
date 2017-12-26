package com.example.plugin;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends BaseActivity {

    private static final String TAG = "Client-MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProxyActivity.setContentView(R.layout.activity_main);
        mProxyActivity.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(mProxyActivity, "you clicked button",
//                        Toast.LENGTH_SHORT).show();
                startActivityByProxy(TestActivity.class.getName());
            }
        });
    }

    @Override
    protected void onResume() {
        if (mFrom == FROM_INTERNAL) {
            super.onResume();
        }
        Log.d(TAG, "MainActivity onResume");
        Toast.makeText(mProxyActivity, "MainActivity onResume", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        if (mFrom == FROM_INTERNAL) {
            super.onPause();
        }
        Log.d(TAG, "MainActivity onPause");
        Toast.makeText(mProxyActivity, "MainActivity onPause", Toast.LENGTH_SHORT).show();
    }
}