package com.dreamwalker.diabetesfits.activity.sync.bsm;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.adapter.isens.BSMSyncAdapter;
import com.dreamwalker.diabetesfits.consts.isens.PremierNConst;
import com.dreamwalker.diabetesfits.device.isens.GlucoseRecord;
import com.dreamwalker.diabetesfits.device.isens.ScannerServiceParser;
import com.dreamwalker.diabetesfits.fragment.isens.ScannerFragment;
import com.dreamwalker.diabetesfits.model.isens.BloodSugar;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.paperdb.Paper;

import static com.dreamwalker.diabetesfits.consts.IntentConst.SYNC_BSM_DEVICE;


/**
 * 동기화하여 데이터를 처리하는 액티비티
 * 박제창
 */
public class SyncBSMDataActivity extends AppCompatActivity {
    private static final String TAG = "SyncBSMDataActivity";

    //    @BindView(R.id.serial_num)
//    TextView _serial_num;
//    @BindView(R.id.count_txt)
//    TextView _count;
    @BindView(R.id.result)
    TextView _result;
//    @BindView(R.id.version)
//    TextView version;
//    @BindView(R.id.bsm_recycler_view)
//    RecyclerView recyclerView;

    @BindView(R.id.animation_view)
    LottieAnimationView animationView;

    @BindView(R.id.home)
    ImageView backImageView;

    private static final int REQUEST_ENABLE_BT = 2;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;

    ArrayList<BloodSugar> mBSList;
    RecyclerView.LayoutManager layoutManager;
    BSMSyncAdapter adapter;

    private final static int OP_CODE_REPORT_STORED_RECORDS = 1;
    private final static int OP_CODE_DELETE_STORED_RECORDS = 2;
    private final static int OP_CODE_REPORT_NUMBER_OF_RECORDS = 4;
    private final static int OP_CODE_NUMBER_OF_STORED_RECORDS_RESPONSE = 5;
    private final static int OP_CODE_RESPONSE_CODE = 6;
    private final static int COMPLETE_RESULT_FROM_METER = 192;

    private final static int OPERATOR_ALL_RECORDS = 1;
    private final static int OPERATOR_GREATER_THAN_EQUAL = 3;

    private final static int FILTER_TYPE_SEQUENCE_NUMBER = 1;

    private final static int RESPONSE_SUCCESS = 1;
    private final static int RESPONSE_OP_CODE_NOT_SUPPORTED = 2;
    private final static int RESPONSE_NO_RECORDS_FOUND = 6;
    private final static int RESPONSE_ABORT_UNSUCCESSFUL = 7;
    private final static int RESPONSE_PROCEDURE_NOT_COMPLETED = 8;

    private final static int SOFTWARE_REVISION_BASE = 1, SOFTWARE_REVISION_1 = 1, SOFTWARE_REVISION_2 = 0; //base: custom profile version

    private BluetoothGattCharacteristic mGlucoseMeasurementCharacteristic;
    private BluetoothGattCharacteristic mGlucoseMeasurementContextCharacteristic;
    private BluetoothGattCharacteristic mRACPCharacteristic;
    private BluetoothGattCharacteristic mDeviceSerialCharacteristic;
    private BluetoothGattCharacteristic mDeviceSoftwareRevisionCharacteristic;
    private BluetoothGattCharacteristic mCustomTimeCharacteristic;

    private String _serial_text;

    private Handler mHandler;
    //    private static final ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();
    private final SparseArray<GlucoseRecord> mRecords = new SparseArray<GlucoseRecord>();

    private boolean _isScanning = false;

    private boolean bolBroacastRegistred;

    private boolean bondingCheckFlag = false;

    String deviceAddress;

    private void initCharacteristics() {
        mGlucoseMeasurementCharacteristic = null;
        mGlucoseMeasurementContextCharacteristic = null;
        mRACPCharacteristic = null;
        mDeviceSerialCharacteristic = null;
        mDeviceSoftwareRevisionCharacteristic = null;
        mCustomTimeCharacteristic = null;
    }

    @OnClick(R.id.home)
    public void homeImageButtonClicked(View v) {
        mBluetoothGatt.disconnect();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_bsm_data);
        setTitle("혈당계 동기화");

        deviceAddress = getIntent().getStringExtra(SYNC_BSM_DEVICE);

        Paper.init(this);
        ButterKnife.bind(this);

//        PackageInfo info = null;
//        try {
//            info = getPackageManager().getPackageInfo(getPackageName(), 0);
//            version.setText(info.versionName);
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
        boolean isBleAvailable = getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE) ? true : false;

        if (isBleAvailable && runningOnKitkatOrHigher()) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = mBluetoothManager.getAdapter();
            if (mBluetoothAdapter == null) {
                Toast.makeText(this, R.string.ValidationWarningPopup_31, Toast.LENGTH_SHORT).show();
            }
            registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        }

        init();

        connect(deviceAddress);
    }

    public void init() {

        mBSList = new ArrayList<>();
//        recyclerView.setHasFixedSize(true);
//        layoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(layoutManager);
//        adapter = new BSMSyncAdapter(getApplicationContext(), mBSList);
//        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        boolean isBleAvailable = getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE) ? true : false;
        if (isBleAvailable && runningOnKitkatOrHigher() && mBluetoothAdapter.isEnabled() == true) {
            final Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
            for (BluetoothDevice device : devices) {
//                    Util.log("####Resume()- Device name:" + device.getName() +", bond status: " + device.getBondState());
                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    startScan();
                }
            }
        }
    }

    private ScanCallback scanCallback;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initCallbackLollipop() {
        if (scanCallback != null) return;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            this.scanCallback = new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    //Do whatever you want

                    if (result != null) {
                        try {
                            if (ScannerServiceParser.decodeDeviceAdvData(result.getScanRecord().getBytes())) {
                                if (result.getDevice().getBondState() == BluetoothDevice.BOND_BONDED) {
//                                    connect(result.getDevice().toString());
                                    connect(deviceAddress);
                                }
                            }
                        } catch (Exception e) {
//						DebugLogger.e(TAG, "Invalid data in Advertisement packet " + e.toString());
                        }
                    }
                }

                @Override
                public void onBatchScanResults(List<ScanResult> results) {
                    super.onBatchScanResults(results);
                }

                @Override
                public void onScanFailed(int errorCode) {
                    super.onScanFailed(errorCode);
                }
            };
        }
    }

    private BluetoothAdapter.LeScanCallback mLEScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (device != null) {
                try {
                    if (ScannerServiceParser.decodeDeviceAdvData(scanRecord)) {
                        if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                            connect(deviceAddress);
                        }
                    }
                } catch (Exception e) {
                }
            }
        }
    };

    public void startScan() {
        if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
            // TODO: 2018-02-19 롤리팝보다 버전이 크다면
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (scanCallback == null) {
                    initCallbackLollipop();
                }

                List<ScanFilter> filters = new ArrayList<ScanFilter>();

                ScanSettings settings = new ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_BALANCED)// or BALANCED previously
                        .setReportDelay(500L).build();

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mBluetoothAdapter.getBluetoothLeScanner().flushPendingScanResults(scanCallback);
                    mBluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallback);
                    mBluetoothAdapter.getBluetoothLeScanner().startScan(filters, settings, scanCallback);
                }

            } else {
                // Samsung Note II with Android 4.3 build JSS15J.N7100XXUEMK9 is not filtering by UUID at all. We have to disable it
                mBluetoothAdapter.startLeScan(mLEScanCallback);
            }
        }
        _isScanning = true;
    }

    public void stopScan() {
        if (_isScanning) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // Stop scan and flush pending scan
                mBluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallback);
            } else {
                mBluetoothAdapter.stopLeScan(mLEScanCallback);
            }
            _isScanning = false;
        }

    }


    @Override
    protected void onPause() {
        if (bolBroacastRegistred) {
            unregisterReceiver(mBondingBroadcastReceiver);
            bolBroacastRegistred = false;
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mGattUpdateReceiver); //ble
        //등록되지 않은 리시버라는데 어떻게 처리해야 좋을까?


        Log.d("-- close", " -- destroy");
        close();
    }

//    public void onSearchClick(View v) {
//        Log.e(TAG, "onSearchClick: " + "스캔버튼 눌렀어요 ");
//        _serial_num.setText("");
//        _count.setText("");
//        //_result.setText("");
//        //mBSList.clear();
//
//        String[] permission = new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION};
//        if (isBLEEnabled() && checkPermission(permission)) {
//            Log.e(TAG, "onSearchClick: " + " 다이얼로그 들어옴 ");
//            showDeviceScanningDialog();
//        } else if (!isBLEEnabled()) {
//            Log.e(TAG, "onSearchClick: " + " 블루투스 사용불가  ");
//            showBLEDialog();
//        } else if (!checkPermission(permission)) {
//            Log.e(TAG, "onSearchClick: " + " 퍼미션 문제  ");
//            requestPermissions(permission);
//        }
//    }

    public boolean checkPermission(String[] permission) {
        for (int i = 0; i < permission.length; i++) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), permission[i]) < 0) {
                return false;
            }
        }
        return true;
    }

    public final static int PERMISSION_REQUEST_CODE = 100;

    public void requestPermissions(String[] permission) {
        for (int i = 0; i < permission.length; i++) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(SyncBSMDataActivity.this, permission[i]) == false) {
                ActivityCompat.requestPermissions(SyncBSMDataActivity.this, permission, PERMISSION_REQUEST_CODE);
                return;
            }
        }
        Toast.makeText(SyncBSMDataActivity.this, "Call camera allows us to access.", Toast.LENGTH_SHORT).show();
    }

//    public void onClearClick(View v) {
//        _serial_num.setText("");
//        _count.setText("");
//        //_result.setText("");
//        mBSList.clear();
//        adapter.notifyDataSetChanged();
//    }

    private boolean isBLEEnabled() {
        final BluetoothAdapter adapter = mBluetoothManager.getAdapter();
        return adapter != null && adapter.isEnabled();
    }

    private void showDeviceScanningDialog() {
        final ScannerFragment dialog = ScannerFragment.getInstance(getApplicationContext());
        dialog.setCancelable(false);
        dialog.show(getFragmentManager(), "bsm_scan_fragment");
    }

    private void showBLEDialog() {
        final Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
    }

    private static boolean runningOnKitkatOrHigher() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            final String extraData = intent.getStringExtra(PremierNConst.INTENT_BLE_EXTRA_DATA);

            switch (action) {
                case PremierNConst.INTENT_BLE_CONNECTED_DEVICE:
                    if (extraData != "") {
                        connect(extraData);
                    }
                    break;
                case PremierNConst.INTENT_BLE_BOND_NONE:
                    showToast(R.string.bond_none);
                    break;
                case PremierNConst.INTENT_BLE_DEVICECONNECTED:
                    break;
                case PremierNConst.INTENT_BLE_DEVICEDISCONNECTED:
                    break;
                case PremierNConst.INTENT_BLE_SERVICEDISCOVERED:
                    clear();
                    break;
                case PremierNConst.INTENT_BLE_ERROR:
                    Toast.makeText(SyncBSMDataActivity.this, extraData, Toast.LENGTH_SHORT).show();
                    break;
                case PremierNConst.INTENT_BLE_DEVICENOTSUPPORTED:
                    showToast(R.string.not_supported);
                    break;
                case PremierNConst.INTENT_BLE_OPERATESTARTED:
                case PremierNConst.INTENT_BLE_OPERATECOMPLETED:
                    break;
                case PremierNConst.INTENT_BLE_OPERATEFAILED:
                    showToast(R.string.gls_operation_failed);
                    break;
                case PremierNConst.INTENT_BLE_OPERATENOTSUPPORTED:
                    showToast(R.string.gls_operation_not_supported);
                    break;
                case PremierNConst.INTENT_BLE_DATASETCHANGED:
                    break;
                case PremierNConst.INTENT_BLE_READ_MANUFACTURER:
                    break;
                case PremierNConst.INTENT_BLE_READ_SOFTWARE_REV:
                    showToast(R.string.error_software_revision);
                    break;
                case PremierNConst.INTENT_BLE_READ_SERIALNUMBER:
                    break;
                case PremierNConst.INTENT_BLE_RACPINDICATIONENABLED:
                    if (getCustomTimeSync() == false) {
                        try {
                            Thread.sleep(500);
                            getCustomTimeSync();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case PremierNConst.INTENT_BLE_REQUEST_COUNT:
                    if (getSequenceNumber() == false) {
                        try {
                            Thread.sleep(500);
                            getSequenceNumber();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case PremierNConst.INTENT_BLE_SEQUENCECOMPLETED:
                    if (getSerialNumber() == null) return;
                    // TODO: 2018-01-29  가져올 순서 번호가 확인되면 모든 ble 데이터를 요청
                    requestBleAll();
                    break;
                case PremierNConst.INTENT_BLE_READCOMPLETED:
                    bondingCheckFlag = true;
                    disconnect();
                    break;
            }
        }
    };

    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            return;
        }
        Log.e(TAG, "disconnect: disconnect 호출됬어요 ");
        mBluetoothGatt.disconnect();
    }

    public String getSerialNumber() {
        return _serial_text;
    }

    /**
     * 브로드 캐스트의 인텐트를 등록하기위한 Intentfilter
     *
     * @return
     */

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PremierNConst.INTENT_BLE_EXTRA_DATA);
        intentFilter.addAction(PremierNConst.INTENT_BLE_CONNECTED_DEVICE);
        intentFilter.addAction(PremierNConst.INTENT_BLE_BOND_NONE);
        intentFilter.addAction(PremierNConst.INTENT_BLE_BONDED);
        intentFilter.addAction(PremierNConst.INTENT_BLE_DEVICECONNECTED);
        intentFilter.addAction(PremierNConst.INTENT_BLE_DEVICEDISCONNECTED);
        intentFilter.addAction(PremierNConst.INTENT_BLE_SERVICEDISCOVERED);
        intentFilter.addAction(PremierNConst.INTENT_BLE_ERROR);
        intentFilter.addAction(PremierNConst.INTENT_BLE_DEVICENOTSUPPORTED);
        intentFilter.addAction(PremierNConst.INTENT_BLE_OPERATESTARTED);
        intentFilter.addAction(PremierNConst.INTENT_BLE_OPERATECOMPLETED);
        intentFilter.addAction(PremierNConst.INTENT_BLE_OPERATEFAILED);
        intentFilter.addAction(PremierNConst.INTENT_BLE_OPERATENOTSUPPORTED);
        intentFilter.addAction(PremierNConst.INTENT_BLE_DATASETCHANGED);
        intentFilter.addAction(PremierNConst.INTENT_BLE_RACPINDICATIONENABLED);
        intentFilter.addAction(PremierNConst.INTENT_BLE_READ_MANUFACTURER);
        intentFilter.addAction(PremierNConst.INTENT_BLE_READ_SOFTWARE_REV);
        intentFilter.addAction(PremierNConst.INTENT_BLE_READ_SERIALNUMBER);
        intentFilter.addAction(PremierNConst.INTENT_BLE_READCOMPLETED);
        intentFilter.addAction(PremierNConst.INTENT_BLE_SEQUENCECOMPLETED);
        intentFilter.addAction(PremierNConst.INTENT_BLE_REQUEST_COUNT);
        return intentFilter;
    }

    //[2016.06.10][leenain] clear gatt cache
    private boolean refreshDeviceCache(BluetoothGatt gatt) {
        try {
            BluetoothGatt localBluetoothGatt = gatt;
            Method localMethod = localBluetoothGatt.getClass().getMethod("refresh", new Class[0]);
            if (localMethod != null) {
                boolean bool = ((Boolean) localMethod.invoke(localBluetoothGatt, new Object[0])).booleanValue();
                return bool;
            }
        } catch (Exception localException) {
            Log.d("exception", "refreshing device");
        }
        return false;
    }

    public boolean connect(final String address) {
        Log.e(TAG, "connect: 호출됬어요 ");

        if (mBluetoothAdapter == null || address == null) {
            return false;
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            return false;
        }


        if (mBluetoothManager != null && mBluetoothManager.getConnectionState(device, BluetoothProfile.GATT) == BluetoothProfile.STATE_CONNECTED) {
            return false;
        }

//        if(mBluetoothManager != null && mBluetoothManager.getConnectedDevices(BluetoothProfile.GATT).contains(device) == true) return false;

        Log.e("---connect", address);
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress) && mBluetoothGatt != null) {
            if (mBluetoothGatt.connect()) {
                return true;
            } else {
                return false;
            }
        }

        if (mHandler == null) {
            mHandler = new Handler();
        }

        final IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        bolBroacastRegistred = true;
        registerReceiver(mBondingBroadcastReceiver, filter);

        mBluetoothGatt = device.connectGatt(this, true, mGattCallback);
        //[2016.06.10][leenain] clear gatt cache
        refreshDeviceCache(mBluetoothGatt);
        mBluetoothDeviceAddress = address;

        return true;
    }

    public void clear() {
        mRecords.clear();
        broadcastUpdate(PremierNConst.INTENT_BLE_DATASETCHANGED, "");
    }

    public void close() {
        Log.e("-- close", " -- close");
        if (mBluetoothGatt != null) mBluetoothGatt.close();
        if (mRecords != null) mRecords.clear();

        mGlucoseMeasurementCharacteristic = null;
        mGlucoseMeasurementContextCharacteristic = null;
        mRACPCharacteristic = null;
        mDeviceSerialCharacteristic = null;
        mBluetoothGatt = null;

    }

    private BroadcastReceiver testReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };

    private BroadcastReceiver mBondingBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            // TODO: 2018-02-06 연결된 디바이스의 정보와 연결상태를 받아온다.
            final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            final int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1);
            // TODO: 2018-02-06 디바이스가 없다면 리턴
            if (device == null || mBluetoothGatt == null) return;

            // skip other devices
            if (!device.getAddress().equals(mBluetoothGatt.getDevice().getAddress())) {
                return;
            }

            if (mBluetoothGatt == null) return;

            if (bondState == BluetoothDevice.BOND_BONDING) {
            } else if (bondState == BluetoothDevice.BOND_BONDED) {

                bondingCheckFlag = true;
                // TODO: 2018-02-06 연결이되면 서비스를 찾으려고 한다.
                broadcastUpdate(PremierNConst.INTENT_BLE_BONDED, device.getAddress());
                //[2016.06.10][leenain] After bonded, discover services.
                mBluetoothGatt.discoverServices();
            } else if (bondState == BluetoothDevice.BOND_NONE) {
                bondingCheckFlag = false;
                // TODO: 2018-07-23 프로토콜상 페어링 필수 - 박제창 .
                AlertDialog.Builder builder = new AlertDialog.Builder(SyncBSMDataActivity.this);
                builder.setTitle("Error");
                builder.setMessage("데이터 동기화를 위해서 혈당계와 페어링 과정이 필요합니다. \n 1. S 버튼을 길게 눌러 설정으로 가세요.\n 2. 블루투스 페어링을 진행하세요" +
                        " \n\n또는 설정 -> 블루투스 에 들어가 혈당계와 페어링을 진행하세요.");
                builder.setCancelable(false);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //device.createBond();
                        finish();
                    }
                });
                builder.setNeutralButton("블루투스 설정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                        startActivity(intent);
                        finish();
                    }
                });
                builder.show();

                //broadcastUpdate(PremierNConst.INTENT_BLE_BOND_NONE, "");
                Log.e("-- close", " -- non bond");
                //close();
            }
        }
    };

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                stopScan();
                mBluetoothGatt = gatt;
                broadcastUpdate(PremierNConst.INTENT_BLE_DEVICECONNECTED, "");
                gatt.discoverServices();

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                if (bondingCheckFlag) {
                    //TODO : result data list show
                    Log.e(TAG, "onConnectionStateChange: " + "데이터 다 받아와서 연결을 끊었어요.");
                    if (Paper.book("syncBms").read("data") == null) {
                        Paper.book("syncBms").write("data", mBSList);
                        startActivity(new Intent(SyncBSMDataActivity.this, SyncBMSResultActivity.class));
                        finish();
                    } else {
                        Paper.book("syncBms").delete("data");
                        Paper.book("syncBms").write("data", mBSList);
                        startActivity(new Intent(SyncBSMDataActivity.this, SyncBMSResultActivity.class));
                        finish();
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SyncBSMDataActivity.this);
                    builder.setTitle("Error");
                    builder.setMessage("데이터 동기화를 위해서 혈당계와 페어링 과정이 필요합니다. \n 1. S 버튼을 길게 눌러 설정으로 가세요.\n 2. 블루투스 페어링을 진행하세요" +
                            " \n\n또는 설정 -> 블루투스 에 들어가 혈당계와 페어링을 진행하세요.");
                    builder.setCancelable(false);
                    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //device.createBond();
                            finish();
                        }
                    });
                    builder.setNeutralButton("블루투스 설정", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                            startActivity(intent);
                            finish();
                        }
                    });
                    builder.show();

                    Log.e(TAG, "onConnectionStateChange: " + " 페어링 되어 있지 않아 서 연결 종료 ");
                }


                //                ArrayList<BloodSugar> mBusinesses2 = mBSList;
//                mBSList.clear();
//                mBSList.addAll(mBusinesses2);
//                for (int i = 0; i < mBusinesses2.size(); i++) {
//                    Log.e(TAG, "mBSList - : " + mBusinesses2.get(i).getBsValue());
//                }
                //recyclerView.setAdapter(new BSMSyncAdapter(getApplicationContext(), mBusinesses2));
                //mHandler.post(() -> adapter.notifyDataSetChanged());

                // TODO: 2018-07-23 전송이 완료되면 필요 없기 때문에 지운다.
                //startScan();
                broadcastUpdate(PremierNConst.INTENT_BLE_DEVICEDISCONNECTED, "");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                initCharacteristics();
                broadcastUpdate(PremierNConst.INTENT_BLE_SERVICEDISCOVERED, "");
                for (BluetoothGattService service : gatt.getServices()) {
                    if (PremierNConst.BLE_SERVICE_GLUCOSE.equals(service.getUuid())) { //1808
                        mGlucoseMeasurementCharacteristic = service.getCharacteristic(PremierNConst.BLE_CHAR_GLUCOSE_MEASUREMENT); //2A18
                        mGlucoseMeasurementContextCharacteristic = service.getCharacteristic(PremierNConst.BLE_CHAR_GLUCOSE_CONTEXT); //2A34
                        mRACPCharacteristic = service.getCharacteristic(PremierNConst.BLE_CHAR_GLUCOSE_RACP);//2A52
                    } else if (PremierNConst.BLE_SERVICE_DEVICE_INFO.equals(service.getUuid())) { //180A
                        mDeviceSerialCharacteristic = service.getCharacteristic(PremierNConst.BLE_CHAR_GLUCOSE_SERIALNUM);//2A25
                        mDeviceSoftwareRevisionCharacteristic = service.getCharacteristic(PremierNConst.BLE_CHAR_SOFTWARE_REVISION); //2A28
                    } else if (PremierNConst.BLE_SERVICE_CUSTOM.equals(service.getUuid())) {//FFF0
                        mCustomTimeCharacteristic = service.getCharacteristic(PremierNConst.BLE_CHAR_CUSTOM_TIME);//FFF1
                        if (mCustomTimeCharacteristic != null){
                            gatt.setCharacteristicNotification(mCustomTimeCharacteristic, true);
                        }

                    }
                }
                // Validate the device for required characteristics
                if (mGlucoseMeasurementCharacteristic == null || mRACPCharacteristic == null) {
                    broadcastUpdate(PremierNConst.INTENT_BLE_DEVICENOTSUPPORTED, "");
                    return;
                }

                if (mDeviceSoftwareRevisionCharacteristic != null) {
                    readDeviceSoftwareRevision(gatt);
                }

            } else {
                broadcastUpdate(PremierNConst.INTENT_BLE_ERROR, getResources().getString(R.string.ERROR_DISCOVERY_SERVICE) + " (" + status + ")");
            }
        }

        ;

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (PremierNConst.BLE_CHAR_SOFTWARE_REVISION.equals(characteristic.getUuid())) { // 2A28
                    String[] revisions = characteristic.getStringValue(0).split("\\.");
                    Log.e("onCharacteristicRead", "revisions: " + revisions);
                    int version_1 = Integer.parseInt(revisions[0]);
                    if (version_1 > SOFTWARE_REVISION_1) { //지원하는 버전보다 큰 경우 예외 처리 후 완료
                        broadcastUpdate(PremierNConst.INTENT_BLE_READ_SOFTWARE_REV, "");
                        Log.e("-- disconnect", " -- revision");
                        gatt.disconnect();
                        return;
                    } else if ((version_1 >= SOFTWARE_REVISION_BASE) && (version_1 == SOFTWARE_REVISION_1)) {// meter software version check: current version - 1.0.00
                        if (mCustomTimeCharacteristic == null) {
                            broadcastUpdate(PremierNConst.INTENT_BLE_READ_SOFTWARE_REV, characteristic.getStringValue(0)); //version 일치하는 경우는 custom이 반드시 있어야 함. 없으면 종료
                            Log.e("-- disconnect", " -- custom null");
                            gatt.disconnect();
                            return;
                        }
//                        else if(Integer.parseInt(revisions[1]) > SOFTWARE_REVISION_2){
//                            showToast(R.string.error_software_revision);
//                        }
                    }

                    if (mDeviceSerialCharacteristic != null) {
                        readDeviceSerial(gatt);
                    }
                } else if (PremierNConst.BLE_CHAR_GLUCOSE_SERIALNUM.equals(characteristic.getUuid())) { //2A25
                    _serial_text = characteristic.getStringValue(0);
                    Log.e(TAG, " characteristic.getStringValue(0): " + characteristic.getStringValue(0));
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            //_serial_num.setText(_serial_text);
                            _result.setText("연결 성공");
                        }
                    });
                    enableRecordAccessControlPointIndication(gatt);
                }
            }
        }



        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (PremierNConst.BLE_CHAR_GLUCOSE_MEASUREMENT.equals(descriptor.getCharacteristic().getUuid())) { //2A18
                    enableGlucoseContextNotification(gatt);
                }
                if (PremierNConst.BLE_CHAR_GLUCOSE_CONTEXT.equals(descriptor.getCharacteristic().getUuid())) { //2A34
                    enableTimeSyncIndication(gatt);
                }
                if (PremierNConst.BLE_CHAR_GLUCOSE_RACP.equals(descriptor.getCharacteristic().getUuid())) { //2A52
                    enableGlucoseMeasurementNotification(gatt);
                }
                if (PremierNConst.BLE_CHAR_CUSTOM_TIME.equals(descriptor.getCharacteristic().getUuid())) { //FFF1
                    broadcastUpdate(PremierNConst.INTENT_BLE_RACPINDICATIONENABLED, "Custom_Flag");
                    getCustomTimeSync();

                }
            } else if (status == BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION) {
                if (gatt.getDevice().getBondState() != BluetoothDevice.BOND_NONE) {
                    broadcastUpdate(PremierNConst.INTENT_BLE_ERROR, getResources().getString(R.string.ERROR_AUTH_ERROR_WHILE_BONDED) + " (" + status + ")");
                }
            }
        }

        ;

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            final UUID uuid = characteristic.getUuid();

            Log.e("--  char", characteristic.getUuid().toString());
            Log.e("--  value", characteristic.getValue().toString());
            if (PremierNConst.BLE_CHAR_CUSTOM_TIME.equals(uuid)) { //FFF1
                int offset = 0;
                final int opCode = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset);
                offset += 2; // skip the operator

                if (opCode == OP_CODE_NUMBER_OF_STORED_RECORDS_RESPONSE) { // 05: time result
                    broadcastUpdate(PremierNConst.INTENT_BLE_REQUEST_COUNT, "");
                    Log.e("-- request sequece", characteristic.getUuid().toString());
                }
            } else if (PremierNConst.BLE_CHAR_GLUCOSE_MEASUREMENT.equals(uuid)) { //2A18
                int offset = 0;

                //Log.e(TAG, "onCharacteristicChanged: Row :  " +  characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8,i));
                final int flags = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset);
                offset += 1;

                final boolean timeOffsetPresent = (flags & 0x01) > 0;
                final boolean typeAndLocationPresent = (flags & 0x02) > 0;
                final boolean sensorStatusAnnunciationPresent = (flags & 0x08) > 0;
                final boolean contextInfoFollows = (flags & 0x10) > 0;

                final GlucoseRecord record = new GlucoseRecord();
                record.sequenceNumber = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset);
                record.flag_context = 0;
                offset += 2;

                final int year = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset + 0); //3
                final int month = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 2);  //5
                final int day = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 3);  //6
                final int hours = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 4);
                final int minutes = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 5);
                final int seconds = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 6); //9
                offset += 7;  // 9 + 7 = 16

                final Calendar calendar = Calendar.getInstance();
                calendar.set(year, month - 1, day, hours, minutes, seconds);
                record.time = calendar.getTimeInMillis() / 1000;

                if (timeOffsetPresent) {
                    record.timeoffset = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_SINT16, offset);
                    record.time = record.time + (record.timeoffset * 60);
                    offset += 2; // 16 + 2 == 18
                }

                if (typeAndLocationPresent) {
                    byte[] value = characteristic.getValue();
                    record.glucoseData = bytesToFloat(value[offset], value[offset + 1]);
                    final int typeAndLocation = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 2);
                    int type = (typeAndLocation & 0xF0) >> 4;
                    record.flag_cs = type == 10 ? 1 : 0;
                    offset += 3;
                }

                if (sensorStatusAnnunciationPresent) {
                    int hilow = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset);
                    if (hilow == 64) record.flag_hilow = -1;//lo
                    if (hilow == 32) record.flag_hilow = 1;//hi

                    offset += 2;
                }

                if (contextInfoFollows == false) {
                    record.flag_context = 1;
                }

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mRecords.put(record.sequenceNumber, record);
                        if (!contextInfoFollows) {
                            String value = String.valueOf(record.glucoseData);
                            _result.setText("동기화 중");
                            broadcastUpdate(PremierNConst.INTENT_BLE_DATASETCHANGED, "");
                            //_result.append(record.sequenceNumber + " : " + record.glucoseData + " , " + getDateTime(record.time) + "\n");
                            mBSList.add(new BloodSugar(value, getDateTime(record.time), 0));
                        }
                    }
                });
                //adapter.notifyDataSetChanged();
            } else if (PremierNConst.BLE_CHAR_GLUCOSE_CONTEXT.equals(uuid)) { //2A34
                Log.e(TAG, "BLE_CHAR_GLUCOSE_CONTEXT : 들어옴");
                int offset = 0;
                final int flags = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset);
                offset += 1;

                final boolean carbohydratePresent = (flags & 0x01) > 0;
                final boolean mealPresent = (flags & 0x02) > 0;
                final boolean moreFlagsPresent = (flags & 0x80) > 0;

                final int sequenceNumber = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset);
                offset += 2;

                if (moreFlagsPresent) offset += 1;

                if (carbohydratePresent) offset += 3;

                Log.e(TAG, "sequenceNumber : " + sequenceNumber);
                Log.e(TAG, "mealPresent value : " + mealPresent);
                final int meal = mealPresent == true ? characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset) : -1;
                Log.e(TAG, "meal value : " + meal);
                Log.e(TAG, "meal value Hex: " + String.format("0x%02x", meal));
                // data set modifications must be done in UI thread
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        final GlucoseRecord record = mRecords.get(sequenceNumber);
                        if (record == null || mealPresent == false) return;
                        record.flag_context = 1;
                        String value = String.valueOf(record.glucoseData);
                        _result.setText("동기화 중");
                        //_result.append(sequenceNumber + " : " + record.glucoseData + " , " + getDateTime(record.time) + ", " + meal + "\n");
                        //_result.append(sequenceNumber + " : " + record.glucoseData + " , " + getDateTime(record.time) + "\n");
                        mBSList.add(new BloodSugar(value, getDateTime(record.time), meal));
                        switch (meal) {
                            case 0:
                                if (record.flag_cs == 0)
                                    record.flag_nomark = 1;
                                break;
                            case 1:
                                record.flag_meal = -1;
                                break;
                            case 2:
                                record.flag_meal = 1;
                                break;
                            case 3:
                                record.flag_fasting = 1;
                                break;
                            case 6:
                                record.flag_ketone = 1;
                                break;
                        }
                        broadcastUpdate(PremierNConst.INTENT_BLE_DATASETCHANGED, "");
                    }
                });
                //adapter.notifyDataSetChanged();
            } else if (PremierNConst.BLE_CHAR_GLUCOSE_RACP.equals(uuid)) { // Record Access Control Point characteristic 2A52
                int offset = 0;
                final int opCode = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset);
                offset += 2; // skip the operator

                if (opCode == OP_CODE_NUMBER_OF_STORED_RECORDS_RESPONSE) { // 05
                    if (mBluetoothGatt == null || mRACPCharacteristic == null) {
                        broadcastUpdate(PremierNConst.INTENT_BLE_ERROR, getResources().getString(R.string.ERROR_CONNECTION_STATE_CHANGE));
                        return;
                    }

                    clear();
                    broadcastUpdate(PremierNConst.INTENT_BLE_OPERATESTARTED, "");

                    final int number = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset);
                    offset += 2;

                    // TODO: 2018-01-29  number 개수가져오는 곳?
                    // number를 문자열로 변환하여 던진다.M
                    broadcastUpdate(PremierNConst.INTENT_BLE_SEQUENCECOMPLETED, Integer.toString(number));

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            //_count.setText(Integer.toString(number));
                        }
                    });

                    Log.e("-- request all data", characteristic.getUuid().toString());
                } else if (opCode == OP_CODE_RESPONSE_CODE) { // 06
                    final int subResponseCode = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset);
                    final int responseCode = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 1);
                    offset += 2;

                    switch (responseCode) {
                        case RESPONSE_SUCCESS:
                            break;
                        case RESPONSE_NO_RECORDS_FOUND: //06000106
                            // android 6.0.1 issue - app disconnect send
                            //mBluetoothGatt.writeCharacteristic(characteristic);
                            broadcastUpdate(PremierNConst.INTENT_BLE_READCOMPLETED, "");

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                try {
                                    Thread.sleep(100);
                                    mBluetoothGatt.disconnect();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    Thread.sleep(100);
                                    mBluetoothGatt.writeCharacteristic(characteristic);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            break;
                        case RESPONSE_OP_CODE_NOT_SUPPORTED:
                            broadcastUpdate(PremierNConst.INTENT_BLE_OPERATENOTSUPPORTED, "");
                            break;
                        case RESPONSE_PROCEDURE_NOT_COMPLETED:
                        case RESPONSE_ABORT_UNSUCCESSFUL:
                        default:
                            broadcastUpdate(PremierNConst.INTENT_BLE_OPERATEFAILED, "");
                            break;
                    }
                }
            }
        }


        private void readDeviceSoftwareRevision(final BluetoothGatt gatt) {
            gatt.readCharacteristic(mDeviceSoftwareRevisionCharacteristic);
        }

        private void readDeviceSerial(final BluetoothGatt gatt) {
            gatt.readCharacteristic(mDeviceSerialCharacteristic);
        }
    };

    private void enableGlucoseMeasurementNotification(final BluetoothGatt gatt) {
        if (mGlucoseMeasurementCharacteristic == null) return;

        gatt.setCharacteristicNotification(mGlucoseMeasurementCharacteristic, true);
        final BluetoothGattDescriptor descriptor = mGlucoseMeasurementCharacteristic.getDescriptor(PremierNConst.BLE_DESCRIPTOR_DESCRIPTOR);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        gatt.writeDescriptor(descriptor);
    }

    private void enableGlucoseContextNotification(final BluetoothGatt gatt) {
        if (mGlucoseMeasurementContextCharacteristic == null) return;

        gatt.setCharacteristicNotification(mGlucoseMeasurementContextCharacteristic, true);
        final BluetoothGattDescriptor descriptor = mGlucoseMeasurementContextCharacteristic.getDescriptor(PremierNConst.BLE_DESCRIPTOR_DESCRIPTOR);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        gatt.writeDescriptor(descriptor);
    }

    private void enableRecordAccessControlPointIndication(final BluetoothGatt gatt) {
        if (mRACPCharacteristic == null) return;

        gatt.setCharacteristicNotification(mRACPCharacteristic, true);
        final BluetoothGattDescriptor descriptor = mRACPCharacteristic.getDescriptor(PremierNConst.BLE_DESCRIPTOR_DESCRIPTOR);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
        gatt.writeDescriptor(descriptor);
    }

    private void enableTimeSyncIndication(final BluetoothGatt gatt) {
        if (mCustomTimeCharacteristic == null) return;

        gatt.setCharacteristicNotification(mCustomTimeCharacteristic, true);
        final BluetoothGattDescriptor descriptor = mCustomTimeCharacteristic.getDescriptor(PremierNConst.BLE_DESCRIPTOR_DESCRIPTOR);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        gatt.writeDescriptor(descriptor);
    }

    private boolean getCustomTimeSync() {
        if (mBluetoothGatt == null || mCustomTimeCharacteristic == null) {
            broadcastUpdate(PremierNConst.INTENT_BLE_ERROR, getResources().getString(R.string.ERROR_CONNECTION_STATE_CHANGE) + "null");
            return false;
        }

        clear();
        broadcastUpdate(PremierNConst.INTENT_BLE_OPERATESTARTED, "");

        setCustomTimeSync(mCustomTimeCharacteristic);
        return mBluetoothGatt.writeCharacteristic(mCustomTimeCharacteristic);

    }

    private boolean getSequenceNumber() {
        if (mBluetoothGatt == null || mRACPCharacteristic == null) {
            broadcastUpdate(PremierNConst.INTENT_BLE_ERROR, getResources().getString(R.string.ERROR_CONNECTION_STATE_CHANGE) + "null");
            return false;
        }

        clear();
        broadcastUpdate(PremierNConst.INTENT_BLE_OPERATESTARTED, "");

        setOpCode(mRACPCharacteristic, OP_CODE_REPORT_NUMBER_OF_RECORDS, OPERATOR_ALL_RECORDS);
        return mBluetoothGatt.writeCharacteristic(mRACPCharacteristic);

    }

    private boolean getAllRecords() {
        if (mBluetoothGatt == null || mRACPCharacteristic == null) {
            broadcastUpdate(PremierNConst.INTENT_BLE_ERROR, getResources().getString(R.string.ERROR_CONNECTION_STATE_CHANGE) + "null");
            return false;
        }

        clear();
        broadcastUpdate(PremierNConst.INTENT_BLE_OPERATESTARTED, "");

        Log.e("-- all records", "data");
        // TODO: 2018-01-29 opcode = 1,  operator = 1
        setOpCode(mRACPCharacteristic, OP_CODE_REPORT_STORED_RECORDS, OPERATOR_ALL_RECORDS);
        //setOpCode(mRACPCharacteristic, OP_CODE_REPORT_STORED_RECORDS, OPERATOR_GREATER_THAN_EQUAL,60);
        //setOpCode(mRACPCharacteristic, OP_CODE_REPORT_STORED_RECORDS, OPERATOR_GREATER_THAN_EQUAL,0);
        return mBluetoothGatt.writeCharacteristic(mRACPCharacteristic);

    }

    private void setCustomTimeSync(final BluetoothGattCharacteristic characteristic) {
        if (characteristic == null) return;

        Calendar currCal = new GregorianCalendar();

        byte bCurrYear1 = (byte) (currCal.get(Calendar.YEAR) & 0xff);
        byte bCurrYear2 = (byte) ((currCal.get(Calendar.YEAR) >> 8) & 0xff);
        byte bCurrMonth = (byte) ((currCal.get(Calendar.MONTH) + 1) & 0xff);
        byte bCurrDay = (byte) (currCal.get(Calendar.DAY_OF_MONTH) & 0xff);
        byte bCurrHour = (byte) (currCal.get(Calendar.HOUR_OF_DAY) & 0xff);
        byte bCurrMin = (byte) (currCal.get(Calendar.MINUTE) & 0xff);
        byte bCurrSec = (byte) (currCal.get(Calendar.SECOND) & 0xff);

        byte op_code_1 = (byte) ((byte) COMPLETE_RESULT_FROM_METER & 0xff);
        byte[] data = {op_code_1, 0x03, 0x01, 0x00, bCurrYear1, bCurrYear2, bCurrMonth, bCurrDay, bCurrHour, bCurrMin, bCurrSec};

        characteristic.setValue(new byte[data.length]);

        for (int i = 0; i < data.length; i++) {
            characteristic.setValue(data);
        }
    }

    private void setOpCode(final BluetoothGattCharacteristic characteristic, final int opCode, final int operator, final Integer... params) {
        if (characteristic == null) return;

        final int size = 2 + ((params.length > 0) ? 1 : 0) + params.length * 2; // 1 byte for opCode, 1 for operator, 1 for filter type (if parameters exists) and 2 for each parameter
        characteristic.setValue(new byte[size]);

        int offset = 0;
        characteristic.setValue(opCode, BluetoothGattCharacteristic.FORMAT_UINT8, offset);
        offset += 1;

        characteristic.setValue(operator, BluetoothGattCharacteristic.FORMAT_UINT8, offset);
        offset += 1;

        if (params.length > 0) {
            characteristic.setValue(FILTER_TYPE_SEQUENCE_NUMBER, BluetoothGattCharacteristic.FORMAT_UINT8, offset);
            offset += 1;

            for (final Integer i : params) {
                characteristic.setValue(i, BluetoothGattCharacteristic.FORMAT_UINT16, offset);
                offset += 2;
            }
        }
    }

    private void setOpCode_ex(final BluetoothGattCharacteristic characteristic, final Integer... params) {
        final int size = params.length;
        characteristic.setValue(new byte[size]);

        int offset = 0;

        if (params.length > 0) {
            for (final Integer i : params) {
                characteristic.setValue(i, BluetoothGattCharacteristic.FORMAT_UINT8, offset);
                offset += 1;
            }
        }
    }

    public String getDateTime(long t) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm:ss");
        Log.e(TAG, "simpleDateFormat: t - - > " + simpleDateFormat.format(t * 1000));
        String strDate = simpleDateFormat.format(t * 1000);
        strDate += "," + simpleTimeFormat.format(t * 1000);
        //simpleDateFormat.format(t * 1000);
        //simpleTimeFormat.format(t * 1000);

        //java.text.DateFormat df = DateFormat.getDateFormat(this);// getDateFormat(context);
        //Log.e(TAG, "getDateTime: t - - > " + t );
        //String strDate = df.format(t * 1000);

        //Log.e(TAG, "getDateTime: " + strDate);
        //df = DateFormat.getTimeFormat(this);
        // TODO: 2018-02-27 기존코드는 아래
        //strDate += " " + df.format(t * 1000);
        // TODO: 2018-02-27 trim하기위한것
        //strDate += "," + df.format(t * 1000);

        Log.e(TAG, "getDateTime2: " + strDate);

        return strDate;
    }

    private float bytesToFloat(byte b0, byte b1) {
//        Log.e(TAG, "b0: " + b0 + " b1: " +  b1 );
//        Log.e(TAG, "unsignedByteToInt(b0) : " + unsignedByteToInt(b0) );
//        Log.e(TAG, "(unsignedByteToInt(b1) & 0x0F) : " + (unsignedByteToInt(b1) & 0x0F) );
//        Log.e(TAG, "((unsignedByteToInt(b1) & 0x0F) << 8) : " + ((unsignedByteToInt(b1) & 0x0F) << 8) );
//        Log.e(TAG, "bytesToFloat: " + (float)unsignedByteToInt(b0) + ((unsignedByteToInt(b1) & 0x0F) << 8) );
        return (float) unsignedByteToInt(b0) + ((unsignedByteToInt(b1) & 0x0F) << 8);
    }

    private int unsignedByteToInt(byte b) {
        //Log.e(TAG, "unsignedByteToInt: " + (b & 0xFF) );
        return b & 0xFF;
    }

    private void showToast(final int messageResId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SyncBSMDataActivity.this, messageResId, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void broadcastUpdate(final String action, final String data) {
        final Intent intent = new Intent(action);
        if (data != "")
            intent.putExtra(PremierNConst.INTENT_BLE_EXTRA_DATA, data);
        sendBroadcast(intent);
    }

    private void requestBleAll() {
        if (getAllRecords() == false) {
            try {
                Thread.sleep(500);
                getAllRecords();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
