package com.wxz;

import android.content.Context;
import android.graphics.Bitmap;

import POSAPI.POSBluetoothAPI;
import POSSDK.POSSDK;

public class PrintUtils {

    //Returned Value
    public static final int POS_SUCCESS=1000;		//success
    public static final int ERR_PROCESSING = 1001;
    private static int error_code = 0;

    public static int TestPrintBitmap(Context context, Bitmap image) {
        POSSDK pos_sdk = new POSSDK(POSBluetoothAPI.getInstance(context));
        final int PrinterWidth = 560;
        byte pzsCommand[] = {0x1C,0x71,0x00};

        if(image == null){
            return ERR_PROCESSING;
        }

        try{
            image.getWidth();
            image.getHeight();
        }catch(Exception e) {
            return ERR_PROCESSING;
        }

        //设置横向和纵向可移动单位
//        error_code = pos_sdk.systemSetMotionUnit(0, 0);
//        if(error_code != POS_SUCCESS)
//        {
//            return error_code;
//        }

        //标准模式下设置打印机起始点
//        pos_sdk.standardModeSetStartingPosition(0);

        //设置标准模式下打印宽度
        pos_sdk.standardModeSetPrintAreaWidth(-100 , PrinterWidth);

        error_code = pos_sdk.imageDownloadToPrinterRAM(2, image, PrinterWidth);
        if(error_code !=POS_SUCCESS)
        {
            return error_code;
        }

        //print bitmap
        error_code = pos_sdk.imageRAMPrint(2,0);
        if(error_code !=POS_SUCCESS)
        {
            return error_code;
        }

        pos_sdk.pos_command.WriteBuffer(pzsCommand, 0, pzsCommand.length, 5000);//Empties the buffer  of FALSH
        return error_code;
    }

}
