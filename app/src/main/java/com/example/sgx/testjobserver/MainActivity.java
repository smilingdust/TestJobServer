package com.example.sgx.testjobserver;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * @author sgx
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startDaemonService();
    }

    public void startDaemonService() {
        Intent intent = new Intent(this, DaemonService.class);
        startService(intent);
    }

}
