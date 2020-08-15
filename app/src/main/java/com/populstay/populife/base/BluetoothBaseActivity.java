package com.populstay.populife.base;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.populstay.populife.util.log.PeachLogger;

import java.lang.ref.SoftReference;

public abstract class BluetoothBaseActivity extends BaseActivity {

    protected BluetoothStateBroadcastReceiver mBluetoothStateBroadcastReceiver;
    protected LocationProviderChangedReceiver mLocationProviderChangedReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBluetoothReceiver();
        registerLocationProviderChangedReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterBluetoothReceiver();
        unregisterLocationProviderChangedReceiver();
    }

    public abstract void onBluetoothStateChanged(boolean isOpen);
    public abstract void onLocationStateChanged(boolean isOpen);

    private void registerBluetoothReceiver(){
        if(mBluetoothStateBroadcastReceiver == null){
            mBluetoothStateBroadcastReceiver = new BluetoothStateBroadcastReceiver(this);
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        intentFilter.addAction("android.bluetooth.BluetoothAdapter.STATE_OFF");
        intentFilter.addAction("android.bluetooth.BluetoothAdapter.STATE_ON");
        registerReceiver(mBluetoothStateBroadcastReceiver, intentFilter);
    }

    private void unregisterBluetoothReceiver(){
        if(mBluetoothStateBroadcastReceiver != null){
            unregisterReceiver(mBluetoothStateBroadcastReceiver);
            mBluetoothStateBroadcastReceiver = null;
        }
    }

    static class BluetoothStateBroadcastReceiver extends BroadcastReceiver {

        SoftReference<BluetoothBaseActivity> softReference;
        public BluetoothStateBroadcastReceiver(BluetoothBaseActivity bluetoothBaseActivity){
            softReference = new SoftReference(bluetoothBaseActivity);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (null == softReference){
                return;
            }
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            switch (action) {
                case BluetoothDevice.ACTION_ACL_CONNECTED:
                    //"蓝牙设备:" + device.getName() + "已链接"
                    break;
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    //"蓝牙设备:" + device.getName() + "已断开"
                    break;
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    PeachLogger.d("BroadcastReceiver-->onBluetoothStateChanged=" + (BluetoothAdapter.STATE_ON == blueState));
                    switch (blueState) {
                        // 蓝牙已关闭
                        case BluetoothAdapter.STATE_OFF:
                            softReference.get().onBluetoothStateChanged(false);
                            break;
                        //蓝牙已开启
                        case BluetoothAdapter.STATE_ON:
                            softReference.get().onBluetoothStateChanged(true);
                            break;
                    }
                    break;
            }
        }
    }

    private void registerLocationProviderChangedReceiver(){
        if(mLocationProviderChangedReceiver == null){
            mLocationProviderChangedReceiver = new LocationProviderChangedReceiver(this);
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.location.PROVIDERS_CHANGED");
        registerReceiver(mLocationProviderChangedReceiver, intentFilter);
    }

    private void unregisterLocationProviderChangedReceiver(){
        if(mLocationProviderChangedReceiver != null){
            unregisterReceiver(mLocationProviderChangedReceiver);
            mLocationProviderChangedReceiver = null;
        }
    }


    static class LocationProviderChangedReceiver extends BroadcastReceiver {
        boolean isGpsEnabled;
        boolean isNetworkEnabled;

        SoftReference<BluetoothBaseActivity> softReference;
        public LocationProviderChangedReceiver(BluetoothBaseActivity bluetoothBaseActivity){
            softReference = new SoftReference(bluetoothBaseActivity);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (null == softReference){
                return;
            }
            if (intent.getAction().matches("android.location.PROVIDERS_CHANGED"))
            {
                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                PeachLogger.d("BroadcastReceiver-->onLocationStateChanged-->isGpsEnabled=" + isGpsEnabled + ",isNetworkEnabled=" + isNetworkEnabled);
                softReference.get().onLocationStateChanged(isGpsEnabled || isNetworkEnabled);
            }
        }
    }

}
