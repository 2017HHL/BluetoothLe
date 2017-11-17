/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.bluetooth.le;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * 对于一个BLE设备，该activity向用户提供设备连接，显示数据，显示GATT服务和设备的字符串支持等界面，
 * 另外这个activity还与BluetoothLeService通讯，反过来与Bluetooth LE API进行通讯
 */
public class DeviceControlActivity extends Activity {
    private final static String TAG = DeviceControlActivity.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private ArrayList<BluetoothGattCharacteristic> myCharas = new ArrayList<BluetoothGattCharacteristic>();
    //连接状态
    private TextView mConnectionState;
    private EditText mDataField;
    private String mDeviceName;
    private String mDeviceAddress;

    private Button button_send_value; // 发送按钮
    private Button jiemian; // 发送按钮
    private EditText edittext_input_value; // 数据在这里输入

    private BluetoothLeService mBluetoothLeService;

    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";
    public final static UUID LSD_WRITE_MEASUREMENT = UUID
            .fromString("0000ff01-0000-1000-8000-00805f9b34fb");
    public final static UUID HAVE_THE_SERVER = UUID
            .fromString("0000fff0-0000-1000-8000-00805f9b34fb");
    //写数据
    private BluetoothGattCharacteristic characteristic;
    private BluetoothGattService mnotyGattService;
    ;
    //读数据
    private BluetoothGattCharacteristic readCharacteristic;
    private BluetoothGattService readMnotyGattService;
    byte[] WriteBytes = new byte[20];
    String data="";
    // 管理服务的生命周期
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    private byte[] dataF;
    private byte[] dataF2;
    // Handles various events fired by the Service.处理服务所激发的各种事件
    // ACTION_GATT_CONNECTED: connected to a GATT server.连接一个GATT服务
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.从GATT服务中断开连接
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.查找GATT服务
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.从服务中接受数据
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                clearUI();
            }
            //发现有可支持的服务
            else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                //写数据的服务和characteristic
                mnotyGattService = mBluetoothLeService.getSupportedGattServices(UUID.fromString("0000ff12-0000-1000-8000-00805f9b34fb"));
                characteristic = mnotyGattService.getCharacteristic(UUID.fromString("0000ff01-0000-1000-8000-00805f9b34fb"));
                //读数据的服务和characteristic
//                readMnotyGattService = mBluetoothLeService.getSupportedGattServices(UUID.fromString("0000ff12-0000-1000-8000-00805f9b34fb"));
//                readCharacteristic = readMnotyGattService.getCharacteristic(UUID.fromString("0000ff02-0000-1000-8000-00805f9b34fb"));
//                readMnotyGattService = mBluetoothLeService.getSupportedGattServices(UUID.fromString("0000ff12-0000-1000-8000-00805f9b34fb"));
//                readCharacteristic = readMnotyGattService.getCharacteristic(UUID.fromString("0000ff02-0000-1000-8000-00805f9b34fb"));
                //获取全部uuid
                displayGattServices(mBluetoothLeService
                        .getSupportedGattServices());
//                initProcess();
            }
            //显示数据
            else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                //将数据显示在mDataField上
                data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                dataF=intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATAF);
                dataF2=intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATAF2);
                if(data.length()>10){
                sanddata(dataF2);sanddata(dataF);
                ShuJuBean shuJuBean = new ShuJuBean();
                shuJuBean.setJulidanwei(Integer.parseInt(data.substring(8,10),16));
                shuJuBean.setJuli(Integer.parseInt(data.substring(10,14),16));
                shuJuBean.setFuyang(Integer.parseInt(data.substring(16,18),16));
                shuJuBean.setFangwei(Integer.parseInt(data.substring(18,22),16));
                shuJuBean.setJingdudanwei(Integer.parseInt(data.substring(24,26),16));
                shuJuBean.setJingdu(Integer.parseInt(data.substring(26,32),16));
                shuJuBean.setWeidudanwei(Integer.parseInt(data.substring(32,34),16));
                shuJuBean.setWeidu(Integer.parseInt(data.substring(34,38),16));
                shuJuBean.setId(Integer.parseInt(data.substring(40,46),16));
                shuJuBean.setMoshi(Integer.parseInt(data.substring(48,52),16));
                shuJuBean.setBaoliu(Integer.parseInt(data.substring(54,58),16));
                shuJuBean.setBaoliu2(Integer.parseInt(data.substring(60,64),16));
                shuJuBean.setBaoliu3(Integer.parseInt(data.substring(66,70),16));
                }
                displayData(data);
                System.out.println("data----" + data);
                edittext_input_value.setText(data);
            }
        }
    };

    private void clearUI() {
        //mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
        mDataField.setText(R.string.no_data);
    }
    public void sanddata(byte[] s){
        if (characteristic != null) {
            final int charaProp = characteristic.getProperties();
            //如果该char可写
            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                // If there is an active notification on a characteristic, clear
                // it first so it doesn't update the data field on the user interface.
                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService.setCharacteristicNotification(mNotifyCharacteristic, false);
                    mNotifyCharacteristic = null;
                }
                //读取数据,数据将在回调函数中
//                    mBluetoothLeService.readCharacteristic(characteristic);
                byte[] value = new byte[20];
                value[0] = (byte) 0x00;
                    WriteBytes = s;
                    characteristic.setValue(value[0], BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                    characteristic.setValue(WriteBytes);
                    mBluetoothLeService.writeCharacteristic(characteristic);
                    Toast.makeText(getApplicationContext(), "写入成功！", Toast.LENGTH_SHORT).show();

            }
            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                mNotifyCharacteristic = characteristic;
                mBluetoothLeService.setCharacteristicNotification(characteristic, true);
            }
        }
        new Thread()
        {
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gatt_services_characteristics);

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        // Sets up UI references.
        ((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);
        mConnectionState = (TextView) findViewById(R.id.connection_state);
        mDataField = (EditText) findViewById(R.id.data_value);

        jiemian = (Button) findViewById(R.id.jiemian);
        button_send_value = (Button) findViewById(R.id.button_send_value);
        edittext_input_value = (EditText) findViewById(R.id.edittext_input_value);
        jiemian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(DeviceControlActivity.this, DataActivity.class);
                startActivity(intent1);
            }
        });
        button_send_value.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        getActionBar().setTitle(mDeviceName);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    /*
     * **************************************************************
	 * *****************************读函数*****************************
	 */
    private void read() {
        if (mGattCharacteristics != null) {
            final BluetoothGattCharacteristic characteristic = mGattCharacteristics
                    .get(3).get(1);
            mBluetoothLeService.setCharacteristicServerNotification(
                    characteristic, true);
            mBluetoothLeService.setCharacteristicNotification(
                    characteristic, true);
        }
//    	mBluetoothLeService.readCharacteristic(readCharacteristic);
//    	//readCharacteristic的数据发生变化，发出通知
//        mBluetoothLeService.setCharacteristicNotification(readCharacteristic, true);
//    	Toast.makeText(this, "读成功", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_connect:
                mBluetoothLeService.connect(mDeviceAddress);
                return true;
            case R.id.menu_disconnect:
                mBluetoothLeService.disconnect();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }

    private void displayData(String data) {
        if (data != null) {
            mDataField.setText(data);
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null)
            return;
        String uuid = null;
        final char[] chr = new char[4];// =======================jerryWang add
        String unknownServiceString = getResources().getString(
                R.string.unknown_service);
        String unknownCharaString = getResources().getString(
                R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
        // haveMyCharacteristic
        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(LIST_NAME,
                    SampleGattAttributes.lookup(uuid, unknownServiceString));
            // ===================================
            // original
            // currentServiceData.put(LIST_UUID, uuid);
            // JerryWang modification
            uuid.getChars(4, 8, chr, 0);
            currentServiceData.put(LIST_UUID, "UUID: 0x"
                    + (String.valueOf(chr)).toUpperCase());
            gattServiceData.add(currentServiceData);

            if (HAVE_THE_SERVER.equals(gattService.getUuid())) {
                // theMyBleServer = gattService;
            }

            // theBleServer

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData = new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService
                    .getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas = new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                myCharas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(LIST_NAME,
                        SampleGattAttributes.lookup(uuid, unknownCharaString));

                // ===================================
                // original:
                // currentCharaData.put(LIST_UUID, uuid);
                // JerryWang modification:
                uuid.getChars(4, 8, chr, 0);
                currentCharaData.put(LIST_UUID,
                        "UUID: 0x" + (String.valueOf(chr)).toUpperCase());
                gattCharacteristicGroupData.add(currentCharaData);
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);


        }

        SimpleExpandableListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(
                this, gattServiceData,
                android.R.layout.simple_expandable_list_item_2, new String[]{
                LIST_NAME, LIST_UUID}, new int[]{android.R.id.text1,
                android.R.id.text2}, gattCharacteristicData,
                android.R.layout.simple_expandable_list_item_2, new String[]{
                LIST_NAME, LIST_UUID}, new int[]{android.R.id.text1,
                android.R.id.text2});
//        mGattServicesList.setAdapter(gattServiceAdapter);
        read();
    }

}

