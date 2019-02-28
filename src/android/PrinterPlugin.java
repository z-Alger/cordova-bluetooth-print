package com.wxz;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.Toast;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PermissionHelper;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.wxz.PrintUtils;

import java.util.Iterator;
import java.util.Set;

import POSAPI.POSBluetoothAPI;
import POSAPI.POSInterfaceAPI;

public class PrinterPlugin extends CordovaPlugin {

    private POSBluetoothAPI mBluetoothManager;
    private POSInterfaceAPI interface_blue = null;
    private Activity activity;
    private CallbackContext mCallbackContext;
    private static final int POS_SUCCESS = 1000;
    private String[] permissions = {Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    @Override
    protected void pluginInitialize() {
        super.pluginInitialize();
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        activity = this.cordova.getActivity();
        PluginResult result;
        mBluetoothManager = POSBluetoothAPI.getInstance(activity);
        interface_blue = POSBluetoothAPI.getInstance(activity);
        //本机是否支持蓝牙
        if (!mBluetoothManager.isBTSupport()) {
            result = new PluginResult(PluginResult.Status.ERROR, "设备不支持蓝牙");
            result.setKeepCallback(true);
            callbackContext.sendPluginResult(result);
            return true;
        }
        //蓝牙是否开启
        if (mBluetoothManager.getState() == POSBluetoothAPI.STATE_OFF) {
            //打开蓝牙
            if (!mBluetoothManager.openBluetooth()) {
                result = new PluginResult(PluginResult.Status.ERROR, "打开蓝牙失败");
                result.setKeepCallback(true);
                callbackContext.sendPluginResult(result);
                return true;
            }
        }
        if (!hasPermisssion()) {
            PermissionHelper.requestPermissions(this, 0, permissions);
        }
        if (action.equals("connected")) {
            JSONArray jsonArray = connectedDevices();
            result = new PluginResult(PluginResult.Status.OK, jsonArray);
            result.setKeepCallback(true);
            callbackContext.sendPluginResult(result);
            return true;
        } else if (action.equals("find")) {
            IntentFilter intentfilter = new IntentFilter();
            intentfilter.addAction(BluetoothDevice.ACTION_FOUND);
            intentfilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            activity.registerReceiver(mReceiver, intentfilter);
            mCallbackContext = callbackContext;
            mBluetoothManager.startDiscovery();
            return true;
        } else if (action.equals("connect")) {
            interface_blue.CloseDevice();
            // Stop Search
            if (mBluetoothManager.isDiscovery()) {
                mBluetoothManager.cancelDiscovery();
            }
            if (args != null && args.length() >= 1) {
                // connect devices
                if (interface_blue.OpenDevice(args.get(0).toString()) == POS_SUCCESS) {
                    result = new PluginResult(PluginResult.Status.OK, "连接成功");
                } else {
                    interface_blue.CloseDevice();
                    result = new PluginResult(PluginResult.Status.ERROR, "连接失败");
                }
            } else {
                result = new PluginResult(PluginResult.Status.ERROR, "地址为空");
            }
            result.setKeepCallback(true);
            callbackContext.sendPluginResult(result);
            return true;
        } else if (action.equals("print")) {
            if (args != null && args.length() >= 1) {
                if (mBluetoothManager.getConnectState() != POSBluetoothAPI.STATE_CONNECTED) {
                    result = new PluginResult(PluginResult.Status.ERROR, "未连接");
                } else {
                    Bitmap bitmap = base64ToBitmap(args.get(0).toString());
                    int re = PrintUtils.TestPrintBitmap(activity, bitmap);
                    if (re == POS_SUCCESS) {
                        result = new PluginResult(PluginResult.Status.OK, "打印成功" + re);
                    } else {
                        result = new PluginResult(PluginResult.Status.ERROR, "打印失败");
                    }
                }
            } else {
                result = new PluginResult(PluginResult.Status.ERROR, "没有内容");
            }
            result.setKeepCallback(true);
            callbackContext.sendPluginResult(result);
            return true;
        } else if (action.equals("close")) {
            //关闭端口
            int re = interface_blue.CloseDevice();
            if (re == POS_SUCCESS) {
                result = new PluginResult(PluginResult.Status.OK, "关闭成功");
            } else {
                result = new PluginResult(PluginResult.Status.ERROR, "关闭失败");
            }
            result.setKeepCallback(true);
            callbackContext.sendPluginResult(result);
            return true;
        }
        return false;
    }

    private JSONArray connectedDevices() {
        mBluetoothManager.cancelDiscovery();
        JSONArray jsonArray = new JSONArray();
        Set<BluetoothDevice> bdv = mBluetoothManager
                .getBondedDevices();
        if (bdv.size() > 0) {
            // Iterative search to bluetooth information
            for (Iterator<BluetoothDevice> iterator = bdv
                    .iterator(); iterator.hasNext(); ) {
                BluetoothDevice bluetoothDevice = (BluetoothDevice) iterator
                        .next();
                // To prevent duplication of add
                JSONObject tmpObj = new JSONObject();
                try {
                    tmpObj.put("name", TextUtils.isEmpty(bluetoothDevice.getName()) ? "未知设备" : bluetoothDevice.getName());
                    tmpObj.put("address", bluetoothDevice.getAddress());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jsonArray.put(tmpObj);
            }

        }
        return jsonArray;
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) { // search devices
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                JSONObject tmpObj = new JSONObject();
                try {
                    tmpObj.put("name", TextUtils.isEmpty(device.getName()) ? "未知设备" : device.getName());
                    tmpObj.put("address", device.getAddress());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                PluginResult result = new PluginResult(PluginResult.Status.OK, tmpObj);
                result.setKeepCallback(true);
                mCallbackContext.sendPluginResult(result);
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) { // search end
            }
        }
    };

    private Bitmap base64ToBitmap(String base64Data) {

        Bitmap bitmap = null;

        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(base64Data.split(",")[1], Base64.DEFAULT);
            Bitmap b = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
            bitmap = newBitmap(b);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    private Bitmap newBitmap(Bitmap b) {
        int w = b.getWidth();
        int h = b.getHeight();
        float sx = (float) 490 / w;
        float sy = (float) 255 / h;
        Matrix matrix = new Matrix();
        //按比例来设置放大比例，这样不会是图片压缩
//        float bigerS = Math.max(sx,sx);
//        matrix.postScale(bigerS,bigerS);
        matrix.postScale(sx, sy); // 长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(b, 0, 0, w,
                h, matrix, true);
        return resizeBmp;
    }

    public boolean hasPermisssion() {
        for (String p : permissions) {
            if (!PermissionHelper.hasPermission(this, p)) {
                return false;
            }
        }
        return true;
    }

    public void requestPermissions(int requestCode) {
        PermissionHelper.requestPermissions(this, requestCode, permissions);
    }

    public void onRequestPermissionResult(int requestCode, String[] permissions,
                                          int[] grantResults) throws JSONException {
        //This is important if we're using Cordova without using Cordova, but we have the geolocation plugin installed
        if (activity != null) {
            for (int r : grantResults) {
                if (r == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(activity, "请允许权限", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        }
    }

}
