# BluetoothLe for Android 
蓝牙BLE开发示例
声明所需要的权限
<uses-permission android:name="android.permission.BLUETOOTH"/>
使用蓝牙所需要的权限
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>
使用扫描和设置蓝牙的权限（申明这一个权限必须申明上面一个权限）
<uses-feature android:name="android.hardware.location.gps" />
在 Android 6.0 及以上需要动态设置打开位置权限


连接蓝牙前的初始化工作
private BluetoothAdapter mBluetoothAdapter;
Initializes Bluetooth adapter.final BluetoothManager bluetoothManager =(BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
mBluetoothAdapter = bluetoothManager.getAdapter();

如果检测到蓝牙没有开启，尝试开启蓝牙
if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
         Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
         startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
 }


扫描蓝牙设备
private void scanLeDevice(final boolean enable) {
    if (enable) {
        // Stops scanning after a pre-defined scan period.
        // 预先定义停止蓝牙扫描的时间（因为蓝牙扫描需要消耗较多的电量）
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScanning = false;
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
        }, SCAN_PERIOD);
        mScanning = true;

        // 定义一个回调接口供扫描结束处理
        mBluetoothAdapter.startLeScan(mLeScanCallback);
    } else {
        mScanning = false;
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
    }
}


发现服务
     管理服务的生命周期
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
    
读取数据
BluetoothGattService service = gattt.getService(SERVICE_UUID);
BluetoothGattCharacteristic characteristic = gatt.getCharacteristic(CHARACTER_UUID);
gatt.readCharacteristic();


往蓝牙数据通道的写入数据
BluetoothGattService service = gattt.getService(SERVICE_UUID);
BluetoothGattCharacteristic characteristic = gatt.getCharacteristic(CHARACTER_UUID);
characteristic.setValue(sendValue);
gatt.writeCharacteristic(characteristic);

