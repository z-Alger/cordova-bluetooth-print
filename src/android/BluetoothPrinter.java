package com.ru.cordova.printer.bluetooth;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Set;
import java.util.UUID;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.util.Xml.Encoding;
import android.util.Base64;

import java.util.ArrayList;
import java.util.List;

public class BluetoothPrinter extends CordovaPlugin {

    private static final String LOG_TAG = "BluetoothPrinter";
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;
    Bitmap bitmap;

    public BluetoothPrinter() {
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("list")) {
            listBT(callbackContext);
            return true;
        } else if (action.equals("connect")) {
            String name = args.getString(0);
            if (findBT(callbackContext, name)) {
                try {
                    connectBT(callbackContext);
                } catch (IOException e) {
                    Log.e(LOG_TAG, e.getMessage());
                    e.printStackTrace();
                }
            } else {
                callbackContext.error("Bluetooth Device Not Found: " + name);
            }
            return true;
        } else if (action.equals("disconnect")) {
            try {
                disconnectBT(callbackContext);
            } catch (IOException e) {
                Log.e(LOG_TAG, e.getMessage());
                e.printStackTrace();
            }
            return true;
        } else if (action.equals("print") || action.equals("printImage")) {
            try {
                String msg = args.getString(0);
                printImage(callbackContext, msg);
            } catch (IOException e) {
                Log.e(LOG_TAG, e.getMessage());
                e.printStackTrace();
            }
            return true;
        } else if (action.equals("printText")) {
            try {
                String msg = args.getString(0);
                printText(callbackContext, msg);
            } catch (IOException e) {
                Log.e(LOG_TAG, e.getMessage());
                e.printStackTrace();
            }
            return true;
        } else if (action.equals("printPOSCommand")) {
            try {
                String msg = args.getString(0);
                printPOSCommand(callbackContext, hexStringToBytes(msg));
            } catch (IOException e) {
                Log.e(LOG_TAG, e.getMessage());
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    //This will return the array list of paired bluetooth printers
    void listBT(CallbackContext callbackContext) {
        BluetoothAdapter mBluetoothAdapter = null;
        String errMsg = null;
        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                errMsg = "No bluetooth adapter available";
                Log.e(LOG_TAG, errMsg);
                callbackContext.error(errMsg);
                return;
            }
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                this.cordova.getActivity().startActivityForResult(enableBluetooth, 0);
            }
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                JSONArray json = new JSONArray();
                for (BluetoothDevice device : pairedDevices) {
                    /*
                     Hashtable map = new Hashtable();
                     map.put("type", device.getType());
                     map.put("address", device.getAddress());
                     map.put("name", device.getName());
                     JSONObject jObj = new JSONObject(map);
                     */
                    json.put(device.getName());
                }
                callbackContext.success(json);
            } else {
                callbackContext.error("No Bluetooth Device Found");
            }
            //Log.d(LOG_TAG, "Bluetooth Device Found: " + mmDevice.getName());
        } catch (Exception e) {
            errMsg = e.getMessage();
            Log.e(LOG_TAG, errMsg);
            e.printStackTrace();
            callbackContext.error(errMsg);
        }
    }

    // This will find a bluetooth printer device
    boolean findBT(CallbackContext callbackContext, String name) {
        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                Log.e(LOG_TAG, "No bluetooth adapter available");
            }
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                this.cordova.getActivity().startActivityForResult(enableBluetooth, 0);
            }
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    if (device.getName().equalsIgnoreCase(name)) {
                        mmDevice = device;
                        return true;
                    }
                }
            }
            Log.d(LOG_TAG, "Bluetooth Device Found: " + mmDevice.getName());
        } catch (Exception e) {
            String errMsg = e.getMessage();
            Log.e(LOG_TAG, errMsg);
            e.printStackTrace();
            callbackContext.error(errMsg);
        }
        return false;
    }

    // Tries to open a connection to the bluetooth printer device
    boolean connectBT(CallbackContext callbackContext) throws IOException {
        try {
            // Standard SerialPortService ID
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();
            beginListenForData();
            //Log.d(LOG_TAG, "Bluetooth Opened: " + mmDevice.getName());
            callbackContext.success("Bluetooth Opened: " + mmDevice.getName());
            return true;
        } catch (Exception e) {
            String errMsg = e.getMessage();
            Log.e(LOG_TAG, errMsg);
            e.printStackTrace();
            callbackContext.error(errMsg);
        }
        return false;
    }

    // After opening a connection to bluetooth printer device,
    // we have to listen and check if a data were sent to be printed.
    void beginListenForData() {
        try {
            final Handler handler = new Handler();
            // This is the ASCII code for a newline character
            final byte delimiter = 10;
            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];
            workerThread = new Thread(new Runnable() {
                public void run() {
                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                        try {
                            int bytesAvailable = mmInputStream.available();
                            if (bytesAvailable > 0) {
                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);
                                for (int i = 0; i < bytesAvailable; i++) {
                                    byte b = packetBytes[i];
                                    if (b == delimiter) {
                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                        /*
                                         final String data = new String(encodedBytes, "US-ASCII");
                                         readBufferPosition = 0;
                                         handler.post(new Runnable() {
                                         public void run() {
                                         myLabel.setText(data);
                                         }
                                         });
                                         */
                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }
                        } catch (IOException ex) {
                            stopWorker = true;
                        }
                    }
                }
            });
            workerThread.start();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //This will send data to bluetooth printer
    boolean printText(CallbackContext callbackContext, String msg) throws IOException {
        try {
            mmOutputStream.write(msg.getBytes());
            // tell the user data were sent
            //Log.d(LOG_TAG, "Data Sent");
            callbackContext.success("Data Sent");
            return true;

        } catch (Exception e) {
            String errMsg = e.getMessage();
            Log.e(LOG_TAG, errMsg);
            e.printStackTrace();
            callbackContext.error(errMsg);
        }
        return false;
    }

    //This will send data to bluetooth printer
    boolean printImage(CallbackContext callbackContext, String msg) throws IOException {
        try {

//            final String encodedString = msg;
//            final String pureBase64Encoded = encodedString.substring(encodedString.indexOf(",") + 1);
//            final byte[] decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
//
//            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        	final String encodedString = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAlgAAADhCAYAAAAUPMtIAAAgAElEQVR4Xu29B7QtRZn+3fMXxRlF0EEQAxeQKEGQnC45J4neEVCGDJIV0KWgBANBMkiQARGQHCVnJKoMIHJJQ1QBEQOIwwii3/qV593fe+p2dVf37pM2z7sWi3P3rq6ueqp619Nv/Jd//OMf/ygkQkAICAEhIASEgBAQAp0h8C8iWJ1hqY6EgBAQAkJACAgBIRAQEMHSRhACQkAICAEhIASEQMcIiGB1DKi6EwJCQAgIASEgBISACJb2gBAQAkJACAgBISAEOkZABKtjQNWdEBACQkAICAEhIAREsLQHhIAQEAJCQAgIASHQMQIiWB0Dqu6EgBAQAkJACAgBISCCpT0gBISAEBACQkAICIGOERDB6hhQdScEhIAQEAJCQAgIAREs7QEhIASEgBAQAkJACHSMgAhWx4CqOyEgBISAEBACQkAIiGBpDwgBISAEhIAQEAJCoGMERLA6BlTdCQEhIASEgBAQAkJABEt7QAgIASEgBISAEBACHSMggtUxoOpOCAgBISAEhIAQEAIiWNoDQkAICAEhIASEgBDoGAERrI4BVXdCQAgIASEgBISAEBDB0h4QAkJACAgBISAEhEDHCIhgdQyouhMCQkAICAEhIASEgAiW9oAQEAJCQAgIASEgBDpGQASrY0DVnRAQAkJACAgBISAERLC0B4SAEBACQkAICAEh0DECIlgdA6ruhIAQEAJCQAgIASEggqU9IASEgBAQAkJACAiBjhEQweoYUHUnBISAEBACQkAICAERLO0BISAEhIAQEAJCQAh0jIAIVseAqjshIASEgBAQAkJACIhgaQ8IASEgBISAEBACQqBjBESwOgZU3QkBISAEhIAQEAJCQARLe0AICAEhIASEgBAQAh0jIILVMaDqTggIASEgBISAEBACIljaA0JACAgBISAEhIAQ6BgBEayOAVV3QkAICAEhIASEgBAQwdIeEAJCQAgIASEgBIRAxwiIYHUMqLoTAkJACAgBISAEhIAI1tAe+Jd/+ZcR2Q3/8R//UZx77rm1fX/3u98tvvSlL03T7qMf/Wjxq1/9qvZ63+B///d/i/e85z29j/7xj3+UXv8///M/xTzzzNOo76rG/j65eKau+cMf/lC8//3v793O95eaz9xzz108+eSTtfMBn3/913+dpt1//ud/FmeeeWYpbqzDb37zm9q+2zRIzadNX7pGCAgBISAExgcCIlgiWJ3tRBGsdlCKYLXDTVcJASEgBMYzAiJYIlid7U8RrHZQimC1w01XCQEhIATGMwIiWCUE65prrimWW2651ut2yCGHFEceeWS4vspEuOuuuxbnnHNOaPfGG28U//d//xf+Xn311YuLL744/P3CCy8USy21VG8sr7zySum47rvvvmLVVVcN33Fg//nPf+618wf4VVddVXz2s58N3/39738vXnvttdK+Z5xxxsbz9/d59dVXh91/pplmKu0vdc0MM8xQeLOg//t973tfaV933HFHMWnSpNpxYxY1rH3j119/vXjzzTdLcfMmwksvvbSHde3NShocfvjhxTe/+c3S+7TpbzSu+dvf/lb85S9/Kaaffvri3e9+92jcUvcQAkJACExoBESwhpbPH+C33XZbMXny5NYL+7Wvfa13gFYRrG233bY444wzprnP2muvXUDykF//+tfFxz72sdrD+Kc//Wmx9NJL15KYyy67rNh4441r2+X6UPmOUpoYPv9//+//1d6zCvCc8eBT9vGPf7x23f7t3/6tgEzViZ+PJ1jXXXddseaaa9ZdnvweAn7ggQfWrmnrG4zAhU8//XRx7LHHBvK//vrrV97hT3/6U3HYYYcVc801V7HDDjtUtv3xj39c3HjjjcWee+5ZzDnnnCMw8vwub7rppuInP/lJsd122w175vCBPP3004sVV1yxWG211UKHEPQykm53g4SKiOZjr5ZCYBAREMESwSo96HMITfxAiGDl/USMJcFCs/jYY48Vb731VvHss88WOPz//ve/L15++eWgoYJI7rHHHtMEAYwngsWYIWaMt40QAAJJhGh7SZG9srnT9sorr0zefoMNNqglojljh8Q9+OCDvTV7xzveUcw333zFJz/5yVoC99vf/ra48847C9N6zzvvvMXiiy9eex3j4ll+4IEHCjTjW2+9ddBcpoS2L774YnH33XeHezUZYw4Gvs1TTz1VoIX//Oc/X6Q02UaA2+LWdEzsQywRWD0WWmih5OVYC37+858X7CdkJHFqOge1HxkERLBqCNYRRxxRYBKqk+985zs9rVeuBouIt5deemmarjGnLbDAAuHzWIO17LLLlg5l9tlnD1oA+3ExcyH/9tfwA0C0nPW9xRZb9PrzBIkf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SggIASEgBISAEBhDBESwxhB83VoICAEhIASEgBAYTAREsAZzXTUrISAEhIAQEAJCYAwREMEaQ/B1ayEgBISAEBACQmAwERDBGsx11ayEgBAQAkJACAiBMURABGsMwdethYAQEAJCQAgIgcFEQARrMNdVsxICQkAICAEhIATGEAERrDEEX7cWAkJACAgBISAEBhMBEazBXFfNSggIASEgBISAEBhDBESwxhB83VoICAEhIASEgBAYTAREsAZzXTUrISAEhIAQEAJCYAwREMEaQ/B1ayEgBISAEBACQmAwERDBGsx11ayEgBAQAkJACAiBMURABGsMwdethYAQEAJCQAgIgcFEQARrMNdVsxICQkAICAEhIATGEAERrDEEX7cWAkJACAgBISAEBhMBEazBXFfNSggIASEgBISAEBhDBESwxhB83VoICAEhIASEgBAYTAREsAZzXTUrISAEhIAQEAJCYAwREMEaQ/B1ayEgBISAEBACQmAwERDBGsx11ayEgBAQAkJACAiBMURABGsMwdethYAQEAJCQAgIgcFEQARrMNdVsxICQkAICAEhIATGEAERrDEEX7cWAkJACAgBISAEBhOB/w8ihR/gRbXOXQAAAABJRU5ErkJggg==";
    		final String pureBase64Encoded = encodedString.substring(encodedString.indexOf(",")  + 1);

            final byte[] decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT);

            Bitmap image = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            int mHeight = image.getHeight();
            //image = resizeImage(image, 48 * 8, mHeight);//348
            image = resizeImage(image, 500, mHeight);

            byte[] bt = decodeBitmap(image);

            mmOutputStream.write(bt);
            // tell the user data were sent
            //Log.d(LOG_TAG, "Data Sent");
            callbackContext.success("Data Sent");
            return true;

        } catch (Exception e) {
            String errMsg = e.getMessage();
            Log.e(LOG_TAG, errMsg);
            e.printStackTrace();
            callbackContext.error(errMsg);
        }
        return false;
    }

    boolean printPOSCommand(CallbackContext callbackContext, byte[] buffer) throws IOException {
        try {
            mmOutputStream.write(buffer);
            // tell the user data were sent
            Log.d(LOG_TAG, "Data Sent");
            callbackContext.success("Data Sent");
            return true;
        } catch (Exception e) {
            String errMsg = e.getMessage();
            Log.e(LOG_TAG, errMsg);
            e.printStackTrace();
            callbackContext.error(errMsg);
        }
        return false;
    }

    // disconnect bluetooth printer.
    boolean disconnectBT(CallbackContext callbackContext) throws IOException {
        try {
            stopWorker = true;
            mmOutputStream.close();
            mmInputStream.close();
            mmSocket.close();
            callbackContext.success("Bluetooth Disconnect");
            return true;
        } catch (Exception e) {
            String errMsg = e.getMessage();
            Log.e(LOG_TAG, errMsg);
            e.printStackTrace();
            callbackContext.error(errMsg);
        }
        return false;
    }

    //New implementation, change old
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    //New implementation
    private static Bitmap resizeImage(Bitmap bitmap, int w, int h) {
        Bitmap BitmapOrg = bitmap;
        //int width = BitmapOrg.getWidth();
        //int height = BitmapOrg.getHeight();
        // int width = 150;
        // int height = 230;
			 int height = 200;
	         h = height;
	         int width = 500;
	         w = 384;
        //if (width > w) {
            //float scaleWidth = ((float) w) / width;
            //float scaleHeight = ((float) h) / (height + 24);
			float scaleWidth = 0.85f;
	        float scaleHeight = 0.915f;
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
                    height, matrix, true);
            return resizedBitmap;
        //} else {
       //     Bitmap resizedBitmap = Bitmap.createBitmap(w, height + 24, Config.RGB_565);
        //    Canvas canvas = new Canvas(resizedBitmap);
        //    Paint paint = new Paint();
        //    canvas.drawColor(Color.WHITE);
        //    canvas.drawBitmap(bitmap, (w - width) / 2, 0, paint);
        //    return resizedBitmap;
        //}
    }

    private static String hexStr = "0123456789ABCDEF";

    private static String[] binaryArray = {"0000", "0001", "0010", "0011",
        "0100", "0101", "0110", "0111", "1000", "1001", "1010", "1011",
        "1100", "1101", "1110", "1111"};

    public static byte[] decodeBitmap(Bitmap bmp) {
        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();
        List<String> list = new ArrayList<String>(); //binaryString list
        StringBuffer sb;
        int bitLen = bmpWidth / 8;
        int zeroCount = bmpWidth % 8;
        String zeroStr = "";
        if (zeroCount > 0) {
            bitLen = bmpWidth / 8 + 1;
            for (int i = 0; i < (8 - zeroCount); i++) {
                zeroStr = zeroStr + "0";
            }
        }

        for (int i = 0; i < bmpHeight; i++) {
            sb = new StringBuffer();
            for (int j = 0; j < bmpWidth; j++) {
                int color = bmp.getPixel(j, i);

                int r = (color >> 16) & 0xff;
                int g = (color >> 8) & 0xff;
                int b = color & 0xff;
                // if color close to white，bit='0', else bit='1'
                if (r > 160 && g > 160 && b > 160) {
                    sb.append("0");
                } else {
                    sb.append("1");
                }
            }
            if (zeroCount > 0) {
                sb.append(zeroStr);
            }
            list.add(sb.toString());
        }

        List<String> bmpHexList = binaryListToHexStringList(list);
        String commandHexString = "1D763000";
        String widthHexString = Integer.toHexString(bmpWidth % 8 == 0 ? bmpWidth / 8 : (bmpWidth / 8 + 1));
        if (widthHexString.length() > 2) {
            Log.d(LOG_TAG, "DECODEBITMAP ERROR : width is too large");
            return null;
        } else if (widthHexString.length() == 1) {
            widthHexString = "0" + widthHexString;
        }
        widthHexString = widthHexString + "00";

        String heightHexString = Integer.toHexString(bmpHeight);
        if (heightHexString.length() > 2) {
            Log.d(LOG_TAG, "DECODEBITMAP ERROR : height is too large");
            return null;
        } else if (heightHexString.length() == 1) {
            heightHexString = "0" + heightHexString;
        }
        heightHexString = heightHexString + "00";

        List<String> commandList = new ArrayList<String>();
        commandList.add(commandHexString + widthHexString + heightHexString);
        commandList.addAll(bmpHexList);

        return hexList2Byte(commandList);
    }

    public static List<String> binaryListToHexStringList(List<String> list) {
        List<String> hexList = new ArrayList<String>();
        for (String binaryStr : list) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < binaryStr.length(); i += 8) {
                String str = binaryStr.substring(i, i + 8);

                String hexString = myBinaryStrToHexString(str);
                sb.append(hexString);
            }
            hexList.add(sb.toString());
        }
        return hexList;

    }

    public static String myBinaryStrToHexString(String binaryStr) {
        String hex = "";
        String f4 = binaryStr.substring(0, 4);
        String b4 = binaryStr.substring(4, 8);
        for (int i = 0; i < binaryArray.length; i++) {
            if (f4.equals(binaryArray[i])) {
                hex += hexStr.substring(i, i + 1);
            }
        }
        for (int i = 0; i < binaryArray.length; i++) {
            if (b4.equals(binaryArray[i])) {
                hex += hexStr.substring(i, i + 1);
            }
        }

        return hex;
    }

    public static byte[] hexList2Byte(List<String> list) {
        List<byte[]> commandList = new ArrayList<byte[]>();

        for (String hexStr : list) {
            commandList.add(hexStringToBytes(hexStr));
        }
        byte[] bytes = sysCopy(commandList);
        return bytes;
    }

    public static byte[] sysCopy(List<byte[]> srcArrays) {
        int len = 0;
        for (byte[] srcArray : srcArrays) {
            len += srcArray.length;
        }
        byte[] destArray = new byte[len];
        int destLen = 0;
        for (byte[] srcArray : srcArrays) {
            System.arraycopy(srcArray, 0, destArray, destLen, srcArray.length);
            destLen += srcArray.length;
        }
        return destArray;
    }

}
