package com.example.weihong.dynamicloadbyclassloader;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.io.File;

import static com.example.weihong.dynamicloadbyclassloader.PluginConstant.EXTRA_DEX_PATH_KEY;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProxyActivity.class);
                intent.putExtra(EXTRA_DEX_PATH_KEY, getCacheDir() + File.separator + "pluginDemo-debug.apk");
                startActivity(intent);
            }
        });
    }
}
