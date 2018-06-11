package com.example.prave.bleadvertise;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;


public  class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView mText;
    private Button mAdvertiseButton;
    private Button mDiscoverButton;

    private BluetoothLeScanner mBluetoothLeScanner;

    private Handler mHandler = new Handler();

    public ScanCallback mScanCallBack;
    private ScanCallback mScanCallback;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mText = (TextView) findViewById(R.id.text);
        mDiscoverButton = (Button) findViewById(R.id.discover_btn);
        mAdvertiseButton = (Button) findViewById(R.id.advertise_btn);
        mDiscoverButton.setOnClickListener(this);
        mAdvertiseButton.setOnClickListener(this);
        mBluetoothLeScanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
        if (!BluetoothAdapter.getDefaultAdapter().isMultipleAdvertisementSupported()) {
            Toast.makeText(this, "Multiple advertisement not supported", Toast.LENGTH_SHORT).show();
            mAdvertiseButton.setEnabled(false);
            mDiscoverButton.setEnabled(false);

        }


    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void discover() {


        ScanFilter filter = new ScanFilter.Builder()
                .setServiceUuid(new ParcelUuid(UUID.fromString(getString(R.string.ble_uuid))))
                .build();
        List<ScanFilter> filters = new ArrayList<ScanFilter>();
        filters.add(filter);

        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
                .build();

        mScanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                Toast.makeText(getApplicationContext(), "onScanResult is called", Toast.LENGTH_SHORT).show();
                if (result == null
                        || result.getDevice() == null
                        || TextUtils.isEmpty(result.getDevice().getName())) {
                    mText.setText(" ");
                } else {
                    mText.setText(mText.getText().toString() + "\n" + result.getDevice().getName() + ":" + result.getRssi());
                }
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
                Toast.makeText(getApplicationContext(), "onBatchScanResult is called", Toast.LENGTH_SHORT).show();
                Iterator<ScanResult> ite = results.iterator();
                while (ite.hasNext()) {
                    Toast.makeText(getApplicationContext(), ite.next().getDevice().getName() + " " + ite.next().getRssi() + "", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onScanFailed(int errorCode) {
                Log.e("BLE", "Discovery onScanFailed: " + errorCode);
                super.onScanFailed(errorCode);
            }
        };

        mBluetoothLeScanner.startScan(mScanCallback);
        Toast.makeText(this, "Scan has started", Toast.LENGTH_SHORT).show();

    }




    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void advertise(){
        BluetoothLeAdvertiser advertise=BluetoothAdapter.getDefaultAdapter().getBluetoothLeAdvertiser();
        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode( AdvertiseSettings.ADVERTISE_MODE_BALANCED )
                .setTxPowerLevel( AdvertiseSettings.ADVERTISE_TX_POWER_HIGH )
                .setConnectable(false)
                .build();
        ParcelUuid pUuid = new ParcelUuid( UUID.fromString( getString( R.string.ble_uuid ) ) );
        AdvertiseData data=new AdvertiseData.Builder().setIncludeDeviceName(false).addServiceUuid(pUuid).addServiceData(pUuid,"Data".getBytes(Charset.forName("UTF-8"))).build();
        AdvertiseCallback advertiseCallback=new AdvertiseCallback() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                super.onStartSuccess(settingsInEffect);
                Toast.makeText(getApplicationContext(),"CallBack called",Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onStartFailure(int errorCode) {
                Log.e( "BLE", "Advertising onStartFailure: " + errorCode );
                super.onStartFailure(errorCode);
            }
        };
        advertise.startAdvertising(settings,data,advertiseCallback);
        Toast.makeText(this,"Advertising Started",Toast.LENGTH_SHORT);

    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        if( v.getId() == R.id.discover_btn ) {
            discover();
        } else if( v.getId() == R.id.advertise_btn ) {
            advertise();
        }
    }




}
