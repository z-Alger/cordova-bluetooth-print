package com.ru.cordova.printer.bluetooth;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import POSSDK.POSSDK;


public class TestPrintInfo {
	
	private static final String LOG_TAG = "SNBC_POS";
	
	//Returned Value
	public static final int POS_SUCCESS=1000;		//success	
	public static final int ERR_PROCESSING = 1001;	//processing error
	public static final int ERR_PARAM = 1002;		//parameter error
	private int error_code = 0;
	
	// ---------------------------------------------------------
	// Print mode options.
	private static final int PRINT_MODE_STANDARD = 0;
	private static final int PRINT_MODE_PAGE = 1;
//	private int printMode = PRINT_MODE_STANDARD;
	
	//----------------------------------------------------------
	// thread flag
	private boolean ThreadFlg1 = false;
	private boolean ThreadFlg2 = false;
	private POSSDK thread_sdk = null;

	// ---------------------------------------------------------
	// Font type options.
	private static final int POS_FONT_TYPE_STANDARD   = 0x00;
	private static final int POS_FONT_TYPE_COMPRESSED = 0x01;
	private static final int POS_FONT_TYPE_CHINESE    = 0x03;


	// ---------------------------------------------------------
	// Font style options.
	private static final int POS_FONT_STYLE_NORMAL            =   0x00;
	private static final int POS_FONT_STYLE_BOLD              =   0x08;
	private static final int POS_FONT_STYLE_THIN_UNDERLINE    =   0x80;
	private static final int POS_FONT_STYLE_UPSIDEDOWN        =  0x200;
	private static final int POS_FONT_STYLE_REVERSE           =  0x400;

	// Specify the area direction of paper or lable.
	private static final int POS_AREA_LEFT_TO_RIGHT = 0x00;

	// ---------------------------------------------------------
	// Cut mode options.
	private static final int POS_CUT_MODE_FULL				= 0x41;

	// ---------------------------------------------------------
	// Mode options of printing bit image in RAM or Flash.
	private static final int POS_BITMAP_PRINT_NORMAL        = 0x00;


	// ---------------------------------------------------------
	// Barcode HRI's position.
	private static final int POS_HRI_POSITION_BOTH  = 0x03;


	/**
	 * Name��TestPrintText
	 * 
	 * Function��test print text 
	 * 
	 * Parameter��

	 * 						
	 * Return��
	 * @return SUCCESS��POS_SUCCESS��FAIL��ERR_PROCESSING,ERR_PARAM
	 */
	public int TestPrintText(POSSDK pos_sdk,int printMode,String txtbuf,int DataLength,int FontType, int FontStyle,
			int Alignment,int HorStartingPosition,int VerStartingPosition,int LineHeight,int HorizontalTimes,int VerticalTimes) 
	{ 
		
		//**********************************************************************************************
		//Download file test
//		String str = "/data/bmp/BTP-R980.JK";
//		error_code = pos_sdk.systemDownloadFile(str,5000000);
//		if(error_code == POS_SUCCESS){
//			System.out.println("Download file success!");
//			return error_code;
//		}else{
//			System.out.println("Download file fail!");
//		}
		
		//***********************************************************************************************
		//print variety of data test 
//		int count = 0;
//		String str = "/data/bmp/record_data.txt";
//		while(true){
//			count++;
//			error_code = pos_sdk.systemDownloadFile(str,20000);
//			if(error_code == POS_SUCCESS){
//				System.out.println("Download file success!");
//			}else
//			{
//				System.out.println("Download file fail!");
//				return error_code;
//			}
//			 try {
//				Thread.sleep(5000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//			 if(count > 1000){
//				 break;
//			 }
//		}

		
		//***********************************************************************************************
		//Multithreaded test 
//		thread_sdk = pos_sdk;
//		thread1 tr1 = new thread1();
//		thread2 tr2 = new thread2();
//		long tick,totulTick;
//		tick = System.currentTimeMillis();
//		totulTick = tick+2000;
//		if(ThreadFlg1 == false){
//			tr1.start();//Start a thread 1 
//		}
//		if(ThreadFlg2 == false){
//			tr2.start();//Start a thread 2ss
//		}
//		while(true){
//			tr1.printText();
//			tr2.printText();
//			tick = System.currentTimeMillis();
//			if(tick > totulTick){
//				tr1.stop();
//				tr2.stop();
//				break;
//			}
//		}
		
		
		if(printMode == PRINT_MODE_PAGE)
		{
			//set print area
			error_code = pos_sdk.pageModeSetPrintArea(0, 0, 640, 100, 0);
			if(error_code != POS_SUCCESS)
			{
				return error_code;
			}
			//set print position
			error_code = pos_sdk.pageModeSetStartingPosition(HorStartingPosition,VerStartingPosition); 
			if(error_code != POS_SUCCESS)
			{
				return error_code;
			}
		}else{
			//set the alignment type
			error_code = pos_sdk.textStandardModeAlignment(Alignment);
			if(error_code != POS_SUCCESS)
			{
				return error_code;
			}	
		}
		
		//set the horizontal and vertical motion units 
		error_code = pos_sdk.systemSetMotionUnit(100, 100);
		if(error_code != POS_SUCCESS)
		{
			return error_code;
		}
	
		//set line height
		error_code = pos_sdk.textSetLineHeight(LineHeight);
		if(error_code != POS_SUCCESS)
		{
			return error_code;
		}
		
		//set character font 
		error_code = pos_sdk.textSelectFont(FontType,FontStyle);
		if(error_code != POS_SUCCESS)
		{
			return error_code;
		}
		
		//set character size
		error_code = pos_sdk.textSelectFontMagnifyTimes(HorizontalTimes,VerticalTimes);
		if(error_code != POS_SUCCESS)
		{
			return error_code;
		}
		
		//print text
		try {
			byte []send_buf = txtbuf.getBytes("GB18030");
			error_code = pos_sdk.textPrint(send_buf, send_buf.length);
			send_buf = null;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if(error_code != POS_SUCCESS)
		{
			return error_code;
		}
		
		//feed line
		error_code = pos_sdk.systemFeedLine(1);
		if(error_code != POS_SUCCESS)
		{
			return error_code;
		}
		
		//entry page mode
		if(printMode == PRINT_MODE_PAGE)
		{
    		//******************************************************************************************
    		//print in page mode
			error_code = pos_sdk.pageModePrint();
    		
			//*****************************************************************************************
			//clear buffer 
			error_code = pos_sdk.pageModeClearBuffer();    			
		}
		return error_code;
	}
	
	/**
	 * Name��TestPrintBar
	 * 
	 * Function��test print BarCode
	 * 
	 * Parameter��

	 * 						
	 * Return��
	 * @return SUCCESS��POS_SUCCESS��FAIL��ERR_PROCESSING,ERR_PARAM
	 */
	public int TestPrintBar(POSSDK pos_sdk,int printMode,String pszBuffer,int DataLength,int nType, int nWidthX,
			int nHeight, int nHriFontType, int nHriFontPosition){
			
			if(printMode == PRINT_MODE_PAGE)
			{
				//set print area
				error_code = pos_sdk.pageModeSetPrintArea(0, 0, 640, 200, 0);
				if(error_code != POS_SUCCESS)
				{
					return error_code;
				}
    			//set print position
    			error_code = pos_sdk.pageModeSetStartingPosition(20,100);
				if(error_code != POS_SUCCESS)
				{
					return error_code;
				}
			}
			error_code = pos_sdk.barcodePrint1Dimension(pszBuffer, DataLength,nType, nWidthX, nHeight, nHriFontType, nHriFontPosition);
			if(error_code != POS_SUCCESS)
			{
				return error_code;
			}
			error_code = pos_sdk.systemFeedLine(1);
    		if(printMode == PRINT_MODE_PAGE)
    		{
        		//******************************************************************************************
        		//print in page mode
    			error_code = pos_sdk.pageModePrint();
        		
    			//*****************************************************************************************
    			//clear buffer in page mode
    			error_code = pos_sdk.pageModeClearBuffer();   			
    		}
    		return error_code;
	}

	
	/**
	 * Name��TestPrintPDF417
	 * 
	 * Function��test print PDF417
	 * 
	 * Parameter��

	 * @param nCorrectGrade Correction Grade[0-8]
	 * 
	 * Return��
	 * @return SUCCESS��POS_SUCCESS��FAIL��ERR_PROCESSING,ERR_PARAM
	 *     
	 */
	public int TestPrintPDF417(POSSDK pos_sdk,int printMode,String pszBuffer,int DataLength,
			int AppearanceToHeight,int AppearanceToWidth,int RowsNumber,
			int ColumnsNumber,int Xsize, int LineHeight,int nCorrectGrade) {
		if(printMode == PRINT_MODE_PAGE)
		{
			//set print area
			error_code = pos_sdk.pageModeSetPrintArea(0, 0, 640, 300, 0);
			if(error_code != POS_SUCCESS)
			{
				return error_code;
			}
			//set print position
			error_code = pos_sdk.pageModeSetStartingPosition(20,100); 
			if(error_code != POS_SUCCESS)
			{
				return error_code;
			}
		}
		error_code = pos_sdk.barcodePrintPDF417(pszBuffer, DataLength, AppearanceToHeight, AppearanceToWidth, RowsNumber, ColumnsNumber, Xsize, LineHeight, nCorrectGrade);
		if(error_code != POS_SUCCESS)
		{
			return error_code;
		}
		error_code = pos_sdk.systemFeedLine(1);
		if(printMode == PRINT_MODE_PAGE)
		{
    		//******************************************************************************************
    		//print in page mode
			error_code = pos_sdk.pageModePrint();
    		
			//******************************************************************************************
			//clear buffer in page mode
			error_code = pos_sdk.pageModeClearBuffer();   		
		}
		return error_code;
	}

	/**
	 * Name��TestPrintQR
	 * 
	 * Function�� test print QR
	 * 
	 * Parameter��

	 * 					
	 * Return��
	 * @return SUCCESS��POS_SUCCESS��FAIL��ERR_PROCESSING,ERR_PARAM
	 */
	public int TestPrintQR(POSSDK pos_sdk,int printMode,String pszBuffer,int DataLength,int nOrgx,int iWeigth,int iSymbolType,int iLanguageMode) {
		if(printMode == PRINT_MODE_PAGE)
		{
			//set print area
			error_code = pos_sdk.pageModeSetPrintArea(0, 0, 640, 300, 0);
			if(error_code != POS_SUCCESS)
			{
				return error_code;
			}
			//set print position
			error_code = pos_sdk.pageModeSetStartingPosition(20,100); 
			if(error_code != POS_SUCCESS)
			{
				return error_code;
			}
		}
		error_code = pos_sdk.barcodePrintQR(pszBuffer,DataLength, nOrgx, iWeigth, iSymbolType, iLanguageMode);
		if(error_code != POS_SUCCESS)
		{
			return error_code;
		}
		error_code = pos_sdk.systemFeedLine(2);
		if(printMode == PRINT_MODE_PAGE)
		{
    		//******************************************************************************************
    		//print in page mode
			error_code = pos_sdk.pageModePrint();
    		
			//*****************************************************************************************
			//clear buffer in page mode
			error_code = pos_sdk.pageModeClearBuffer();     			
		}
		return error_code;

	}// end of function TestPrintQR
	
	/**
	 * Name��TestPrintGS1
	 * 
	 * Function��test print GS1
	 * 
	 * Parameter��

	 * 						
	 * Return��
	 * @return SUCCESS��POS_SUCCESS��FAIL��ERR_PROCESSING,ERR_PARAM
	 */
	public int TestPrintGS1(POSSDK pos_sdk,int printMode,String pszBuffer,int DataLength,int BarcodeType,int BasicElementWidth,int BarcodeHeight,
			int BasicElementHeight,int SeparatorHeight,int SegmentHeight,int HRI,int AI){
		if(printMode == PRINT_MODE_PAGE)
		{
			//set print area
			error_code = pos_sdk.pageModeSetPrintArea(0, 0, 640, 300, 0);
			if(error_code != POS_SUCCESS)
			{
				return error_code;
			}
			//set print position
			error_code = pos_sdk.pageModeSetStartingPosition(20,100); 
			if(error_code != POS_SUCCESS)
			{
				return error_code;
			}
		}
		error_code = pos_sdk.barcodePrintGS1DataBar(pszBuffer, DataLength, BarcodeType, 
				BasicElementWidth, BarcodeHeight, BasicElementHeight, SeparatorHeight, SegmentHeight, HRI, AI);
		if(error_code != POS_SUCCESS)
		{
			return error_code;
		}
		
		error_code = pos_sdk.systemFeedLine(1);
		if(printMode == PRINT_MODE_PAGE)
		{
    		//******************************************************************************************
    		//print in page mode
			error_code = pos_sdk.pageModePrint();
    		
			//*****************************************************************************************
			//clear buffer in page mode
			error_code = pos_sdk.pageModeClearBuffer(); 	
		}
		return error_code;
	}
	/**
	 * Name��TestFeedLine
	 * 
	 * Function�� feed line
	 * 
	 * Parameter��

	 * 					
	 * Return��
	 * @return SUCCESS��POS_SUCCESS��FAIL��ERR_PROCESSING,ERR_PARAM
	 */
	public int TestFeedLine(POSSDK pos_sdk,int printMode)
	{
		
		if(printMode == PRINT_MODE_PAGE)
		{ 
			error_code = pos_sdk.systemFeedLine(1);
    		//******************************************************************************************
    		//print in page mode
			error_code = pos_sdk.pageModePrint();
    		
			//*****************************************************************************************
			//clear buffer in page mode
			error_code = pos_sdk.pageModeClearBuffer(); 
		}
		else
		{
			error_code = pos_sdk.systemFeedLine(3);
		}
		return error_code;
	}
	
	/**
	 * Name��TestCutPaper
	 * 
	 * Function��cut paper
	 * 
	 * Parameter��

	 * 					
	 * Return��
	 * @return SUCCESS��POS_SUCCESS��FAIL��ERR_PROCESSING,ERR_PARAM
	 */
	public int TestCutPaper(POSSDK pos_sdk,int printMode)
	{
		if(printMode == PRINT_MODE_PAGE)
		{
    		//******************************************************************************************
    		//print in page mode
			error_code = pos_sdk.pageModePrint();
    		
			error_code = pos_sdk.systemCutPaper(66, 0);	
			
			//*****************************************************************************************
			//clear buffer in page mode
			error_code = pos_sdk.pageModeClearBuffer(); 			
		}else{
			error_code = pos_sdk.systemCutPaper(66, 0);	
		}
		return error_code;
	}
	  public Bitmap getBitmapFromByte(byte[] temp){  
		    if(temp != null){  
		        Bitmap bitmap = BitmapFactory.decodeByteArray(temp, 0, temp.length);  
		        return bitmap;  
		    }else{  
		        return null;  
		    }  
		}  


	/**
	 * Name��TestPrintBitmap
	 * 
	 * Function��Test print bitmap
	 * 
	 * Parameter��

	 * 					
	 * Return��
	 * @return SUCCESS��POS_SUCCESS��FAIL��ERR_PROCESSING,ERR_PARAM
	 */
	public int TestPrintBitmap(POSSDK pos_sdk,int printMode,String img_name,int img_type) {

			FileInputStream temp_stream = null;
			Bitmap image;
			final int PrinterWidth = 640;
			int image_size = 0;
			byte pzsCommand[] = {0x1C,0x71,0x00};
			printMode = 1;
			if(printMode == PRINT_MODE_PAGE)
			{
    			//*****************************************************************************************
    			//set print area
    			error_code = pos_sdk.pageModeSetPrintArea(0,0,640,500,0);
    			if(error_code !=POS_SUCCESS)
    			{
    				return error_code;
    			}
    			
    			//set print position
    			error_code = pos_sdk.pageModeSetStartingPosition(20,200);  
    			if(error_code !=POS_SUCCESS)
    			{
    				return error_code;
    			}
			}

			//*****************************************************************************************
			//download bitmap to RAM and print
			if(img_type == 0)
			{
				//*****************************************************************************************
				//read bitmap data
//				try {
//					temp_stream = new FileInputStream(img_name);
//					
//				} catch (FileNotFoundException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				if(temp_stream == null){
//					return ERR_PROCESSING;
//				}
			
				 //byte[] bytes1 = ".getBytes();
				final String encodedString = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAABS4AAAGPCAYAAABS2U6KAAAgAElEQVR4XuydB7RmNdm2Y0EsIM2R3kRpKlWkD70jHZTepMMwIMLyU5CinwsUkF6EYegfTSnSi/TeFCkiIg4wA9KEAbHiv649f15zMtk72e0t59xZiwWcNzs7uZLd7jzlQ//5z3/+Y1REQAREQAREQAREQAREQAREQAREQAREQAREQAREoI8IfEjCZR/NhroiAiIgAiIgAiIgAiIgAiIgAiIgAiIgAiIgAiKQEZBwqYUgAiIgAiIgAiIgAiIgAiIgAiIgAiIgAiIgAiLQdwQkXPbdlKhDIiACIiACIiACIiACIiACIiACIiACIiACIiACEi61BkRABERABERABERABERABERABERABERABERABPqOgITLvpsSdUgEREAEREAEREAEREAEREAEREAEREAEREAEREDCpdaACIiACIiACIiACIiACIiACIiACIiACIiACIhA3xGQcNl3U6IOiYAIiIAIiIAIiIAIiIAIiIAIiIAIiIAIiIAISLjUGhABERABERABERABERABERABERABERABERABEeg7AhIu+25K1CEREAEREAEREAEREAEREAEREAEREAEREAEREAEJl1oDIiACIiACIiACIiACIiACIiACIiACIiACIiACfUdAwmXfTYk6JAIiIAIiIAIiIAIiIAIiIAIiIAIiIAIiIAIiIOFSa0AEREAEREAEREAEREAEREAEREAEREAEREAERKDvCEi47LspUYdEQAREQAREQAREQAREQAREQAREQAREQAREQAQkXGoNiIAIiIAIiIAIiIAIiIAIiIAIiIAIiIAIiIAI9B0BCZd9NyXqkAiIgAiIgAiIgAiIgAiIgAiIgAiIgAiIgAiIgIRLrQEREAEREAEREAEREAEREAEREAEREAEREAEREIG+IyDhsu+mRB0SAREQAREQAREQAREQAREQAREQAREQAREQARGQcKk1IAIiIAIiIAIiIAIiIAIiIAIiIAIiIAIiIAIi0HcEJFz23ZSoQyIgAiIgAiIgAiIgAiIgAiIgAiIgAiIgAiIgAhIutQZEQAREQAREQAREQAREQAREQAREQAREQAREQAT6joCEy76bEnVIBERABERABERABERABERABERABERABERABERAwqXWgAiIgAiIgAiIgAiIgAiIgAiIgAiIgAiIgAiIQN8RkHDZd1OiDomACIiACIiACIiACIiACIiACIiACIiACIiACEi41BoQAREQAREQAREQAREQAREQAREQAREQAREQARHoOwISLvtuStQhERABERABERABERABERABERABERABERABERABCZdaAyIgAiIgAiIgAiIgAiIgAiIgAiIgAiIgAiIgAn1HQMJl302JOiQCIiACIiACIiACIiACIiACIiACIiACIiACIiDhUmtABERABERABERABERABERABERABERABERABESg7whIuOy7KVGHREAEREAEREAEREAEREAEREAEREAEREAEREAEJFxqDYiACIiACIiACIiACIiACIiACIiACIiACIiACPQdAQmXfTcl6pAIiIAIiIAIiIAIiIAIiIAIiIAIiIAIiIAIiICES60BERABERABERABERABERABERABERABERABERCBviMg4bLvpkQdEgEREAEREAEREAEREAEREAEREAEREAEREAERkHCpNSACIiACIiACIiACIiACIiACIiACIiACIiACItB3BCRc9t2UqEMiIAIiIAIiIAIiIAIiIAIiIAIiIAIiIAIiIAISLrUGREAEREAEREAEREAEREAEREAEREAEREAEREAE+o6AhMu+mxJ1SAREQAREQAREQAREQAREQAREQAREQAREQAREQMKl1oAIiIAIiIAIiIAIiIAIiIAIiIAIiIAIiIAIiEDfEZBw2XdTog6JgAiIgAiIgAiIgAiIgAiIgAiIgAiIgAiIgAhIuNQaEAEREAEREAEREAEREAEREAEREAEREAEREAER6DsCEi77bkrUIREQAREQAREQAREQAREQAREQAREQAREQAREQAQmXWgMiIAIiIAIiIAIiIAIiIAIiIAIiIAIiIAIiIAJ9R0DCZd9NiTokAiIgAiIgAiIgAiIgAiIgAiIgAiIgAiIgAiIg4VJrQAREQAREQAREQAREQAREQAREQAREQAREQAREoO8ISLjsuylRh0RABERABERABERABERABERABERABERABERABCRcag10CPznP/8xH/rQh4YQCf1NyKoREMtq3HSUCIiACIiACIiACIiACIiACIiACIjAyCQw0MLlSBCCGCPFFxRH5nId/FEXrdlezrXbr7b70a8Murm6RsK9q5s8dS4REAEREAEREAEREAEREAEREIHhSWCghcuRIOghWK6wwgrm7rvvbm0F7rzzzmb8+PGF7d9zzz1ZP0Ll/vvvN8svv3zh8TvssIM599xzWxvD6NGjzV133dVa+000PMccc5iXX345t6k555zTTJo0yVjhsIlzlm3jgAMOMMcdd1x2WBVxLUVgLxrfjjvuaM4///yeMijLrEr9P//5z2bUqFFVDtUxIiACIiACIiACIiACIiACIiACIjBiCEi4HICpXnHFFbsiXCI6+aKS/ZuEy/oLJUW4nDhxYv0T1WjBFS6rNNOEcHneeedVOfVAHSPhcqCmS50VAREQAREQAREQAREQAREQARHoEQEJlz0CX+a03RIui/ok4bLMjIXrSricwiVmcSnhsv5aUwsiIAIiIAIiIAIiIAIiIAIiIAIiMBwISLgcgFmUcBmfpEF1FXfdsXEVl8XljkbCZXy9q4YIiIAIiIAIiIAIiIAIiIAIiIAIjAQCEi4HYJYlXMYnaVCFS0ZmxcteCpfWxXvs2LGdGJdx6lPXkKt4GjW5iqdxUi0REAEREAEREAEREAEREAEREIGRTUDC5QDMf0i49BOnVEmkYofeZnIeGyNz++237yTnSRG3UqfFuh2vssoqWXKeUJzO1Lbaroer+EsvvZSdJsSgl8KlHbtiXLa9Cqa0L+GyO5x1FhEQAREQAREQAREQAREQAREQgcEmIOFyAOYvZnFZR7Rk+G0Klxavsoob48a4DM1ZPwmXVhAuKzKn1FeMSwmXA3DbVRdFQAREQAREQAREQAREQAREQAT6gICEyz6YhFgXYsJl7PjY7xIuY4Sa+T0vOY8V8uaaa66exbi0lqq4ih9//PEd9/WyI5dwmUZMFpdpnFRLBERABERABERABERABERABERgZBMY9sLlhz/8YfPZz362b2f5gw8+yNxGi0pMuKwrFtUVLh955BGz4YYb5g4BYe7VV19tfQ5mm2223HNMnjzZvPfee4V9mGmmmcy0005buZ+vvPJK6WPLurYXjTF28r///e/mrbfeClaz/SjbH78xjp911lkLuzJp0qTc33fcMZ6cZ5ZZZjHTTDNNbLg9+/2NN94w//znPwvPL+GyZ9OjE4uACIiACIiACIiACIiACIiACAwQgWEvXCL0FAklvZ6r1157LSqs9rtwGWOIiPOxj30sVq3270UuyAceeGBmSVhUfvnLX5oNNtigcj9SBOTKjf//A4vGGGv72muvLRSYY8en/P7JT34yKhAXtZMiXN53331mueWWS+lOT+pwvd57770SLntCXycVAREQAREQAREQAREQAREQAREYTgQkXPZ4NiVcNjcBEi6LWUq4bG6tFbUk4bI7nHUWERABERABERABERABERABERCB4U9AwmWP51jCZXMTIOFSwmVzq6l6SxIuq7PTkSIgAiIgAiIgAiIgAiIgAiIgAiLgEpBw2eP1IOGyuQmQcCnhsrnVVL0lCZfV2elIERABERABERABERABERABERABEZBw2UdrQMJlc5Mh4VLCZXOrqXpLEi6rs9ORIiACIiACIiACIiACIiACIiACIiDhso/WgITL5iZDwqWEy+ZWU/WWJFxWZ6cjRUAEREAEREAEREAEREAEREAEREDCZR+tAQmXzU1GkXB5wAEHmJ/+9KfBk5ENnGNtVnHbTtks4WXrVxl5t7KKWyZl+6is4sZIuCy7alRfBERABERABERABERABERABERABMIERnyMy2WXXdZMnDix1fWBYLb55psHz9HPwqUrXt1zzz1mhRVWCI7h0UcfNRtvvHEuQ8Q2y7iO8JZ3AisYzjnnnLl9ePvtt83kyZNzf6eNz3zmM2baaafNrXPQQQeZ/fffv7CNVheSMWbuuefORNYq5W9/+5t5/fXXo4faea8iXvZauHz44YfNpptuGh1jnQpwmTBhQm4TEi7r0NWxIiACIiACIiACIiACIiACIiACIvBfAiNeuEQIeumll1pdE+eee67ZYYcdWhMu2+o8AlmKFeH9999vll9++cJuMH44tFVGjx5t7rrrrqmad/s/adIkM+usswa7cMEFF5jtt98++JsV8I444ghz2GGHVR5CCsvKjUcOtGO48MILzTbbbBOs/corr5jZZ589+62KaMlxvRYuEdhXWmmltjB22i0SjyVcto5fJxABERABERABERABERABERABERghBCRcOsJlVbEmtlbaFC6tuJgqMub1Nc892hVo8oS3MsJl3X76/bftucJlaB75WxXh0m2rrnD54Q9/uLK1ZGjeitZrHoPzzz/fbLvttlHhMram834frsKlz1PCZdUVouNEQAREQAREQAREQAREQAREQAREIJ2AhMv/L1y2JVrS7vjx4zNrvpDw14Sr+AcffGAQxeqUIkExJjaWES7r9LHoWN/i0p/PIuGS8WGJmGdxac+LcHnooYcmWaG6fbUiV905KssutKaHs8UlnO+9916z8sorNyoQW+4uTwmXZVej6ouACIiACIiACIiACIiACIiACIhAeQISLgfcVbwpi8u8pRMTLTmuH4XL0HhwhU51FQ+JfnUtLnvpKm554BIvi8vyN0r/CAmX9RmqBREQAREQAREQAREQAREQAREQARGIEZBwOeDCJROcIi7GFkLR77H2U4RLrBnPO++8Ot0oPDZmccnBZYTL0MkGTbhs2uIyxSp5uLqKS7hs7dJVwyIgAiIgAiIgAiIgAiIgAiIgAiKQS0DC5YALl92wuLSrp99iXLpWb6usskonOU+TMS7dK6eOcElfP/KRj7Tiwhy6upuIcWnnu0wW80996lPm3XffHdKlmPDtVt5xxx2jAvd9991nlltuueBNTcl59LQTAREQAREQAREQAREQAREQAREQgeFDQMLlgAuXbS/FFNEpxeKyW1nFi8S2IotLYj9ut912hdm06wiXzJMr/KZYLzY9t5wzJTlPnb7J4tIYZRVveuWqPREQAREQAREQAREQAREQAREQgZFKQMLlMBAuN91009bX749+9COz8MILB8+TIlzOPffcZqmlliqd2CZ1YHfffbd5/fXXC6uvt956Ztpppw3WefHFF80jjzwy7IVL5oC5CJW//e1v5oYbbpiKQRkhE+ESi8uq8TxlcZm64lVPBERABERABERABERABERABERABIY/AQmXw0C4rCoSpSxvK1rhgrv88ssHBakU4bKM+JXSr1CdIpfmAw44wPz0pz9NajrParOuxWXSyVusdO2115oNN9ww6Qx580XG7jvvvDOpDSqlWOy6jUm4TEariiIgAiIgAiIgAiIgAiIgAiIgAiIw7AlIuJRwmbTIES5XWGGFYN0U4TLpJDUrFQmXBx54oDn++OOjZygSWOsIl2UFvGhHS1bg/Nddd13XhcuS3TQSLssSU30REAEREAEREAEREAEREAEREAERGL4EJFxKuExa3SNFuCyCUUe4TILccqUyFpd5XSlrcVl2SBIuyxJT/W4T+Ne//mX+/e9/54ad6HZ/BvV8r732miGZF+Elypb3338/s+YuOpZ5IiFamx4JZfut+iIgAiIgAiIgAiIgAiIgAuUJSLiUcJm0aiRcGiPh0hgJl1MulyLr3pGenOedd94xp512mnnrrbcyVosvvrjZeuutk+4z/Vrp7bffNr/97W/NAw88YP7whz+YOeec04wdO7aS6NavY+xmv/76179moTuILfz5z3/erL766tk6+fCHP5zUjQsuuMAQ13iOOeYwq6yyill22WXNxz/+8ezYf/zjH+bmm282t956a7bulllmmaQ2VUkEREAEREAEREAEREAERKA/CUi4lHCZtDIlXEq4ZKFIuJRwGbth/OUvfzFHH320efPNN7OqX/nKV8xuu+0WO6yvfp88ebL5/e9/bx599FHz1FNPmffee29I/7Di23777bMM8irlCfzmN78xp59+ema5SllyySWzNYKFZKwgIh977LHm1VdfzarOO++8HRGZBGOI5s8880z224wzzmgIEzLrrLPGmtXvIiACIiACIiACIiACIiACfUpAwqWEy6SlKeFSwiULRcLllMtFFpf5t41BEy4/+OADg9vys88+axDUnnvuOYNFYKzMM888Zv/99zfTTTddrKp+dwggVv7sZz8zjz32WPbXaaed1uy9995m4YUXTuJ03333mXPPPbeT+IvwEiSOs+Whhx4y48ePN7iKUxZccEGzzz77dCwyk07SQqU//elP5rbbbmusZdzsSbZWxdW+sU70sCHuwe+++67hfjNp0qROTz7xiU9kFtHTTDNNdm22FSqA9cWGxuuvv27eeOONzvmnn356M/vss2frzVoB9xCTTj3MCbAO77rrLjNx4kSz9tprm1GjRjU2YjaC8Jx4+eWXDc/Jj370o4bn3gwzzJBdX20Xrmss5+eaay6z6qqrNnI6e93Ciw1KyiyzzGI++9nPtnq/aKTzkUbsPfHPf/5z557E/Yj7IffCVI+GbvRV5xABERCBKgQkXEq4TFo3Ei4lXLJQJFxOuVwkXObfNsoIl3z4Yx1nLe+SbkYVKvFhssACC2RH8jH2xBNPmCeffDKzqsQylI+ylPKxj33MfO5znzNf/epXzZe+9KXsAw5357PPPjtrt25B6Nh1113N3HPPXbepvj0eXiRKs1asuIjvvvvu2UdxrOAGfsopp3QsKhGIsKj89Kc/3TmUtYQr+b333pv9DeHqa1/7mll//fVbE7Fi/eZ3rHfPOOOMlKpJdWaeeWZzyCGHZFalI6UgOrC5cMcdd5jnn38+CwtQVFhTXEsIOosttljSGstrj3s+IiXnZi65bxQ9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z9hi8i1R3TAo/rHdEC553yap2yWXXDJEpC0r+vtiNXPOvTZkpS/hMrZK9LsIiEBVAsNeuOSFqihbdVVwTR3HwwBrw6LCg6EoTlhMMEvp60svvdRxefNFMIJLf+c730lpJrdOTPgizoobENtviA8SN6u4/3usfeoXWVyyI8vua1Ehszn/5PGOzUPM4pIP+g8++KCwD6NGjer87s8TlimuBYTf0M0332y23XbbwvbdMbgCY5GoWXZh1BUuEWB54e/XssEGGwxxEQr1E1c3dy77dSxV+uV/eNuPM8SfBx980PzqV7/KRCvWQVmRwPanSeGyjrse/ZGrePoqYc7d5Ae4WyNOcl91C8/F5557zhDLkYQ4vRYuXQvR9NEa4wtC7lrBXZ74mDbLelG7XDtPP/10Zu2J4MVx0003XecQPkI33HDDqTiW6esg1OU95dxzzx0So83vN1x5Z8IqEJEtVuoKl/5Heux8sWQfbQuXof6WES55F+Af912lrAgSYxQSLln3bkzWpsUy+tSmcBma10ETLp988snMctF/V24qRIBdF70QLu25WctkyEbA5J29TGFjinAir776anZYbI22LVyy4YOn1Q033DDkei0r+vO+hefds88+28FRNOcSLsusGtUVAREoQ2DYC5dlYPRr3SaEy/333z93eFgy8FFUVLbaaqssRlmoTJo0yVx66aWFx/OhRWwyv1jxjbhFRYUXANxQ8iwLL7vssiG78aG2iiwuifvE7mFROeKII8xhhx2WW6WucDnnnHNGx5An+vH3I4880hx++OGFY1hnnXWyBBihguh51llndV64QufiRa4omURsHmm8rnDZr9dpmX6NJOFyttlmyyx0CAjvC/NV3TIlXJZZbf1T95lnnsmsddgoKrJEcXvsf/CnCM3uB2FMKIrRQWTnY9S1MOKYUD/8dekLQlgYc4+kXpOFa+yggw5KEkGbPG8v2uL58dRTT2UWRFYgCPWDewsWeXhzFD2bR5pw6YslsCFpCGJNqPj1SeKEKy2b6e6zvEmry5BwSfIPXMXdzdmyAkxsvbYpXIbGVCfJVjctLhHAiAHJprHrWs3awUqRDXHmoqnStHAZchUnURUbQnnFboCQBCzFlRoubnxm2o25YrcpXPLcPO+88wxis1uIRUmIklRLZT98C23NOuusZuzYsdnGc6hIuGzqSlA7IiACPgEJ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W7jkHEXz4AuXISYSLptYKWpDBERABERABERABERABERABERguBOQcBmZYYSaf//737liFRaXRVZ4TS6gNkVMhMuQuFhXtOT4f/3rX1NZXFouTY2pDYtLv29NW1z61oVzzTVXKYvLPIEyb831s3BZZR3kHdMPwuW9995beOlLuGzyzqi2REAEREAEREAEREAEREAEREAEhisBCZcJMxuzuMQKrxuliriT2q+2hEvOH3IVT+1Xar1BFC7t2Oz6ShEui9ai65rstw2ffhYuU+c5pd7JJ59s9tlnn05VV3yPZRVPucZimxlYXEq4TJkp1REBERABERABERABERABERABERCBYgISLj0+IeEiJlxai8sUN96yC9LvT4qwUvYc1M9zFfdd4Ku0HXIVb2McRfEfY/12Y1yG+sbfDj/88ELrWpsJOy9sAK7iL730Uqcrvtt1XVfx2FylCpfEuKT4cST5f8bYxjp356fM2ghdH2Ts3nfffTvJhdy2Weef+MQncpeDbc//t+Vhx1601kKu4n4/ZXEZuyL773dE74985CPmox/9aP91Tj0SgZYJcO979913Mw8UCs+C6aabLjdpX8vdaax53k/eeecd85nPfKaxNodbQ3/9618NnCgkISxKRJg69smTJ2f30qLncWpbqicCIiACIiACIjD8CUi4LJhjK9wUJeLwY1yWEV3KLC/bl5Bo1MQ5207Ogxt6m4JXExaXO+ywQ7CPlm/TruJ2/puyuIytpzLCpS9aWuHOF1tj57S/d0uA53y+xaXbx5jFpR1nbK2WiXHpjt3+t4TL1JXTH/Vef/11c8IJJ2QCx+qrr27WWGONTLRR6S6BF1980Zx99tnZRhtlvvnmMzvvvHNXYih3d6T9dzaYsyn0hz/8IevczDPPbA455BAz44wz9l9nE3rEPfypp54yF110kXn//fczK/0FFlgg4ciRV+VnP/uZefjhh7OBf+1rXytM8hejw7Pv6quvNo888ohZc801zWabbZYbiinWln4XAREQAREQAREYOQRGvHCZJ/q5fydmXl658cYbzS9/+cvchC5NLKU8C8CYuFLm3Mcff3y2+53HY4sttjCzzTZbsMlXXnnFXH755bmnQ/jdf//9y3SnVF3b56J5osFddtnFfPKTnwy2TXKe7bbbrnAeN9hgA7Puuuvm9m3MmDGd30JzM8MMM5gf/vCHuQLu9773PfP2228Xjr1ozh966CHzwAMPZMeH5vHJJ580p59+emH7q622WudDInSu/fbbr9TcpFZuQny3bWy55ZZm9OjRQQbEWyUBUd2COJo3F1xLzz//fOFaGq7CJXzfe++9Vjcp8uYOa0jExKriel67jOnMM880v/71rztVFlxwwUzoaMLyqO5aHEnH//GPf8wEZIQmCkIT913NQ/urYLgJl/fcc4/B08JuTM8666xm7NixmSCrMpRAU8Il99Lx48cb3lUo008/fcacMDkqIiACIiACIiACIlBEYNgLl7yEXnPNNbkMNt10U4OI4JcyQgovv/PPP39rK22llVYqFAI+9rGPmV/96leVz4+Ydt111wWPtxx4yV9++eWDosD999+f/RYq9ngEv6JYoLjXxgp9yCt77723+c1vflPICYGVj5NQee2118zvf//73PYnTJhgtt5660Ixink6+uijc9tgjEUiNK53Rda9NFwkXGIRiju7W/zzff/73zdrr712sI9vvPGG2WijjQqnYfHFFzennnpq4RiL1gG/Fc0jFszXX399YR8Qkb74xS8G6zzxxBNmr732yn5zY36WEfkRQ84777zcPiDuWre5vEpFY+SYZZZZxkwzzTSxJT9wv/vCUjcH0IaIxbrh3sgzxK4hfWx3c1aHnkvCZe/YDzfhEhHt/PPPN7y/2LLccsuZ7bffvlI4CFzon332WYMLdN3yhS98wcw000yZdSvP5SYLGzwLL7yw+dSnPpXcbFPCJSf0r+Fll13W7LjjjlkYDhUREAEREAEREAERyCMw7IVLrAQnTZqUuwLmnnvuIXEHbcUi12y/MSzZFllkkcYtffy+uP/vijIIl9Z1rspSx9WOXfCighCzwgorBKsUCZf2ANywzz333M7xbsIU/phiJeWKT/7xWNjdddddhWMoEi5j3J555plsjosKot+VV16ZO5aUMcb6UVa49NvDsnSbbbYJngY+s88+e2EXVl55ZXPnnXfm1omNMebSzwdMkWjIie+77z7DBybFXwesUwTkohLblPjyl7+cieBucc+DdRfCZdFclBFKY3M+SL8PN+ESyyDujYgcFKzSd9ppp0x4Vuk+AQmX3WduzzjchEvGZUNA2M1rng24Qq+//vpJ7yTubPh86szUHnvsYZZaainjCoZ12nOPJaYkHjD+Zjv3OITSeeeddyoL5phwCT+eicTpjr0D8GzEPd++RxBGiI1nxFQVERABERABERABEcgjIOEyR7gss2QQLhdddNEyh5SqG3sR5MVv0IRLH0BsjNQvEoP6Rbi86qqrcuc2ZYyxhVFXuMQ6eNtttw2ephvCZWweywqX/kBShMsY45Bw6R6DcEmszKIi4XKKKy+hGbg/tVWwbrKiYtMWl7/97W/NuHHjMtd3Sh1Ro63xj7R2JVz2bsaHo3AJTa7z0047rXMfqWpRPajCJV4ed9xxR2ZVThIensG+B02RcMmzjvcKnr2Inl//+tfN5z73ucKFSpLCn/70px3rVDwo9txzT8MmvIoIiIAIiIAIiIAIhAhIuJRwmSU36LbFpYTLajckCZdDLS4lXFZbR20d5QtLdRM5FPXTFwqaFC6xPDrllFM6omVbvGy7eVZQbZ930NrvpXCJRdnTTz8d3bToJtNZZpmlawllui1clrE2rHPtuxaAZBbfddddo8JbaI5dPoQBITzOZz/72U7VF154wdx6663Z//N3fnfDhRDuh9jIFGtxefvtt2eu1XmFEDc2WRLW4Gyg58Xxtm0gDhIuZtSoUZ1mb7755k6c8hDLIuESa8vjjjvOvPXWW1l7xENfa621Ci8D3+qSvhPi5Utf+lI3Lx+dSwREQAREQAREYIAISLiUcCnhMuGCTXUVl8Xlh6I0i8RXWVxG8fV1heEgXL788stZHFfcSLtVJFymke6lcPmXv/wli2H85ptvpnW2C7W+8pWvmN12260LZzKZV0c3s4pXES4Rl7GetCJaKhjiU7777rtZcq+ysRaJ+0z8a5dP6Hp+9NFHzRlnnJF1KSYOWuEy1v/LLrvM3HLLLVk1wh6ReK5M7Erbvis+Mn6sHxdbbLHO6YuES5JT2jjyiKH0AUE9Vl588UVDIjss2olFv/nmmxvWs4oIiIAIiIAIiIAIhAhIuJRwKeEy4d4g4XIKpLoxLmlDwmXCghvQKoMuXL766quGjPGhhG1tTomEyzS6Ei6HcpJwOYWHFQIRD7stLts56LZw+c9//jOzCscKmELcXaxFq4Skse7ed999d9aW77qdJ1y+/fbb5thjjzXcNynrrLOO2WyzzZIuZs5JIj4SJi655JKG5IQqIiACIiACIiACIpBHQMKlhEsJlwn3BwmXEi7tMlGMy/wLZpCFy5ClJVZMJI7AIqip4sfOpF0y62633XaK8RaBLOGyvnB58cUXm1//+tdZQ9zLELtY57HSS4tLrBr9pHK4Xd90000jVrhENDzmmGM6luHEllx99dVj05j7++9//3tz0kknZaEQiEu83377GbKbU/KES+YAq09ESLKg77PPPubaa681uMW3UTgHLuWf/vSn22hebYqACIiACIiACPQxAQmXDQiXfAjMN998rU2zHyjdPxFxkooyPcc69oMf/CB72SwqdbOKr7feeuawww7LPUVsjBxYNzkPY6wqQPzpT38y3/jGNwoZYY3Ih0ReSRljbK7IqJ1Xzj77bHPWWWcVNnH44YdnVhGhggvkBhtsEB1jnaziNF40hiOPPDKzwigqblZxv14TyXmw3iHZQF4hERTWLrE+Fv2+9NJLD4lvFpv3Qfl9UIVLrm/fxXT22Wc3++67ryHuXROF+9eDDz6YrS1cWm1BHEAcjcWma6IPg95GL4XL4RLj0hWhylj69lK4DFmWui7K1uKSRDP83SbUCq33CRMmmIkTJ3Z+It5kLJlM0XVDdu5VV121667ixMM84YQTsvMiQJMpvM44WN9YcLJJS8EKkjAEuI6HhMt33nkni205adKkrD7vFWRjd8MJNH2/4f3tkEMOMTPOOGPTTas9ERABERABERCBPicg4TJHuHTdbch+mFduvPFGc91117U6zbyM8lJKoV++gGd/q9IJO86f/OQnhgDpeWXLLbc0fMiHyv333z9VFsoqfYkdUyRcXnHFFQaLqbzyi1/8whDovkxx1wDn5qU8T/Sj3dg8zDDDDAZhrmrh44EM9r3MWB1zFeejpaj86Ec/MmQvr1PaFi7z+mavPf5ddE/gt6KECrSPK7KbHKEOj346dhCFy5AFZNOiJYIKCTCuvvrqTvZi5k2ipcliC1544YVZYg4EfUSYvFJGuOQ+SRy9xx9/PEuEoozFU6gOZ+GyaO0wduLWIvbZUBDEgxwzZkwjG8+usIvYt8gii2QxM21hY/DZZ5/N/pfM5QsvvPCQeJpYPL7xxhvZ7ykxLtmkO++887L6s802mznooIOydusUnq3nnntu9o4BG+JVYo0bEi59a8sDDzwws4SUcFlnBnSsCIiACIiACIhA7rf4f3qpgjQwL7F4PrzQ2R3h0Ol4KXvppZcKewIi/gmd66ijjiq0JGxgiGbFFVc0d911V27sohiDlD68//77mXsQ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EZe+vtO8nka6UlyGkeVW8TBOPIoESIAESIAESIAESIAESIAESIAEyp0AxWXkDIaKSyRWtwnGtBGX+nCVCCyF7YWhW8UVAzNizRVxqe7ZJj51pi5JOHr06MQcl4UUl2mWF8VlGK2kiEvM/8KFCwuS41L/kgDXYcRl2FzxKBIgARIgARIgARIgARIgARIgARKIIUBxGUNPCJEkLvWIwqSq4pAfqB4ZUpxn/vz51cSnnu8SOZD0/4+8pUynu8Ql8rglJaFX4jE24tIX2egSl2quZs2aJbp3715tu3sWGEm5SH19UVz6CK17HeIyqZKrnuMy77yWuLbqk+IybK54FAmQAAmQAAmQAAmQAAmQAAmQAAnEEKC4jKHnEJd6t4UQl3r/pbJVfMSIEVaathyXZuRllojLNFMXGnEJcakEVZr+zWOzyEuKyzDioeIyrLd0R1FcpuPFo0mABEiABEiABEiABEiABEiABEgghkDFi8vNNttMnH322TGMnOcuXrxYbk91tW7duglEFNrkFqpf3njjjc7zt9tuO3HSSSclCrVx48YV7P5COz7kkEPEXnvtVe1wFZWGfJcTJkxwdrX11luLfv36JR6DvtR92qIrr732WvHuu+/K822vP/XUU+Kf//yntX8lo3r16iVatGhhPT+EA+TrgAEDrJGxIedTXIZQEuKUU04R2267rTX1wqpVq8Tdd98d1lGKo8yUBCNHjkwsvITrYxyu9sknn4hGjRqlGAEPJQESIAESIAESIAESIAESIAESIIG6R6DsxeXHH3/snDUIjlJvgwYNklvOk1oe9/DRRx8l9o9q4D7Zg8I1++67r7WP559/XvTo0aPgmF3bc7t06SKwnd7VXn755URZdP/994thw4ZF3cMxxxwjpk+fnrkP5DuF5E1qELuPPPKIs39sld55550zjQEyrV27ds5zsUU75h4zDUw76eabbxZ/+MMfnN3893//txTIWVuhnzeMC9KyFHLTZmXE80iABEiABEiABEiABEiABEiABEigGATKXlz6IJXCNmrfGCEOx48fn3hYHvfgkn79+/cXkydP9gqxAw880HrMokWLRMeOHX23Gf16rLiEvN1mm22s47jjjjsECijFtFhx6bs2+p85c6bzsNdee020atXK15X1dfBp0qSJ89zOnTuLefPmZeo/j5OQSxXRjq6G6Ntzzz038+UK/bxlHhhPJAESIAESIAESIAESIAESIAESIIE6RoDisgQmnOIybBIoLikuKS7DnhUeRQIkQAIkQAIkQAIkQAIkQAIkQAKVQIDisgRmkeIybBIoLikuKS7DnhUeRQIkQAIkQAIkQAIkQAIkQAIkQAKVQIDisgRmkeIybBIoLikuKS7DnhUeRQIkQAIkQAIkQAIkQAIkQAIkQAKVQIDisgRmkeIybBIoLikuKS7DnhUeRQIkQAIkQAIkQAIkQAIkQAIkQAKVQKBOi0sU4VAyTP/vPCZWFfgwZZt5Hfz/iBEjWJwnALrOEv+tF1EJqSqetThP6NpA8Zxp06ZVG1fAbQUf4irOo8aoF+cxGfkuVOjiPGnHYxuvS1yq9TBhwoSSKM6Tx/365oyvkwAJkAAJkAAJkAAJkAAJkAAJkEAlEyh7cYlq0K6WplJ0qKDKe0F069ZN9O3bN7Fb3z2EjPv2229P7P+mm24S8+fPd97W008/LQpVVTxJ8poDwlzbRDCOGzt2rHjjjTdqvK73kVZchnDV+997773FqFGj8l4eVf1dc8014oUXXnD2n1RVHNxWrVolnnjiicTzv/rqK6/wQ8Xyiy++OLGPDTbYQJx88slWeYsxPPjgg+Jf//qXnKckvn369EmUv5CSiFB2tVKoKu563jD23r17i3r16hVsrbBjEiABEiABEiABEiABEiABEiABEqgEAmUvLvWoO31ClBQJkU+QKWghx6ad9Oeff15ccsklNU5Lc63WrVuLK664IvHSPXv2TDus1ONxicsvv/xS4PWYFnMPiqWax6RxHHHEEWLjjTe2vgwp6hPEkMunnHJK4m3+9re/rXrNta09hpPtHvW1dMghh4jNNtus2iXUWGbPni18nHv16iUGDhyY+Cz4zt90003Ft99+WyUmzXvdY489xCuvvFL1Y1vU888//ywgQG3t3Xfflee7nh9cY8cdd0wcg49/0nuK7zz1esh7zyeffCIaNWoU2mXZHPfdd9+Jn376SY53/fXXF/Xr1y9YBHJtQ/n111/lENZbb73aHgqvTwIkQAIkQAIkQAIkQAIkQAIVS6AixKUuCjBTodu/1Xnq+Dy3dqq+5syZI3r06FEtEjBEbOgSBJGOCxYsSFyEsaIlZHW7xGXI+UnHKPaxH/7BQImELONxiUs1X3/84x/FH/7wh1qdB9d2ed/6nTVrlldcImL06quvriab9OfJt9YgLr/55pvEiMs2bdqIV199tVrEpSkvIb6SxKVvbn0MfOfjdd89hvZhex9S91qp4vLmm28WS5YskYh23nlnmQbDFlm6YsUKceutt4offvhBHosvBbA2khqidP/617+KL774Qh7StWtXcfjhh4dMRcGOWbZsmUC0OiR5hw4dRPv27aWoZSMBEiABEiABEiABEiABEiABEsiPQEWIS4XDlj/SFflmisv8sP5fTxCX3bt3T+w6ZJv0QQcdVCUubWImD9GiD9AWzVYocanPXSx/iMusLJLEpc7isssuE2PGjEkcppKv+ppLE1nru/+0ctZcK2nEJcais1R9+fgi2hPiUm/6OMyIS9s9Q1xuuOGGPhwFe913jyEX9r0X1XVxichZ5Cv9/vvvJc7BgwcLpFpIaojqvvzyy8Xnn38uD0F0sy/6N2SeYo659957xWOPPSa7wHyfffbZznuIuRbPJQESIAESIAESIAESIAESIIG6SqCixGXWSSzUtl70+9BDDznFZciYdXFpOz4P0aL3WwxxaeY4LOWIS8XGJy7zngfbXNvkbGiUYai4RC7NpOa7R7VVPOl8XVwmSV3XVnHVb+g9hzxf5jG+e8zSp3kOxWXtistffvlFTknWyF5siR8/frx4//33ZT9bbrmlGD16tGjcuHEey6NO9YGoW0TSfvjhhzJqHnOy/fbbi4YNGxbkCwzMPUT4Bx98IPDfm2yyiWjevLlo0KBBwbf9414fffRRKewh3vF+mVcDO0QmI5oZ/RfzvsARebKRR/nII4/MPQ0Gfid8+umnVWtk8803F82aNStKKorVq1eLxx9/XF4PqVjybPg9hi/6Vq5cKb7++mu5/po2bSp+85vfFGTt5zl29kUCJEACJEACJEACxSRAcaltLc8bfLmKSxuHmIjLEMkUK4vSRiOa9xiS47IUxKVLsutbk3XBh/8Gn2KKS11M63OL7cDLly+vhj/LVvGQNZX1eY5diyHXpbisPXG5Zs0aceONN8pcsIj0zCKPzIjRvfbaSwwaNEjm9WTzE4BYmzt3rpg3b56UiLaG5xB5YJFqZd99980smdE33i/eeecdMWPGDFnEzZZWBMJ0n332Eccee6zYeuut/TeR8gjIqUmTJkn5ttVWW4mLLrpIbLHFFil7qXk4ZCHe21988UUpYs2G+9pzzz0lx+22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+1trH2vXrhXHHnus8/yQm6utiEtc95tvvhHz5s2LWmuFjLhU2/BvvPFGgSI/trZkyRJx6aWXOu+BxXlCVmL8MYy4jGfIHkiABEiABEiABEiABEiABEiABCqfQNmLS1+U2xZbbCEQMZi1jRs3Tjz//PPO0y+//PJEWQQpeOWVVzrP79atmzj99NOzDjHovD59+iQe179/fzF58mRnP5dcconMnWiTh2+99ZZXiHXu3FlAbsY01z08+uijAjIoph1yyCGJ84jckbvvvntM98InLv/xj3+I7777LvEaV199tXjhhReixuA7ubbF5TPPPCPeeeedxGE+/PDD4vbbb3feBqI2u3btaj0G+VbPPPNM5/nNmzcX//M//5N4DM5HP652xx13OF/v3bt3LukTfPPJ10mABEiABEiABEiABEiABEiABEignAlUvLjcdtttxerVqzPP0XHHHSemTZvmPH/58uWidevW1mPmzJkjunfv7jz/vPPOE+PHj888xtgTQ8Tl008/LQ488EDrpRYtWiQ6duzoHEa/fv3ElClTYodaa+cXQ1z6bu6YY44RM2fO9B0W9Xpti0vf4JFLdeTIkc7Drr32WpmT1dZ+/PFHUa9ePef5bdu2FUuXLk08BuejH1cLKYblu1e+TgIkQAIkQAIkQAIkQAIkQAIkQAJ1nQDFpWcFUFyuA0RxWfiIS9+bEcWlEBSXvlXC10mABEiABEiABEiABEiABEiABEigcghQXFJcCkZc+h9oRlz6GRXjCIrLYlDmNdIS+Omnn8TXX38tttpqK5nLly2MAFJjgB0aIpl90dAhvWIeNthgA7HJJpuEHM5jSIAESIAESIAESIAESIAESpwAxSXFJcVlwENKcRkAqQiHUFwWAXLkJf72t78J5PZFa9++vTj11FMjeyzs6f/85z8FilqhQXYhdUeLFi1SXRSpBVDADOJtn332kelBttxyy1R91MWDb775ZoGiYWgo7hZTTA85jmfMmCFzUh9xxBHi+OOPp0Sui4uK90wCJEACJEACJEACJFBxBCguKS4pLgMea4rLAEhFOITisgiQIy+hy6h9991XDBo0KLLHwp4eKy7Xrl0rcM+qcBYKwo0ePVpss802hR14BfSel7hEsSwUmHvuuecklc0331zmwm3WrFkFUOItkAAJkAAJkAAJkAAJkEDdJkBxmZO4RMVp2xZB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HPISeTNVVKB+zl577SUriOeZXxGRnvfdd5948skn5bOFremnnHKKt2BbyL3EHGPOW0xfOFcVenruuedkNXI0CNoLL7xQRsiGND06FZIa0hMFy9hIgARIgARIgARIgARIgARKlwDFpWduKC7XAaK49D/EFJd+RjiC4pLiUq2UECmJbb1XXHFFVX7IgQMHiv3226/GYksrLs1t4ugQMgsSDAVc0NLKS+RQxLZwvF/ailBBKqI4Tc+ePUX9+vXDHpjAoyBMEb0IKZqnGA28fI3DCiUuUeQHxexwv2nkI6IsEZmKCFW0bbfdVvzud78TyAfKRgIkQAIkQAIkQAIkQAIkULoEKC4pLkUlVRXP+qgxxyVzXGZdO+Z5daGq+Ntvvy0+++yzGsgg67D1WkUx7rTTTuLQQw+1ooUw2n777WXOQvSHhqI3kHp6+/LLL8Xll18uPv/8c/ljbLPee++9a/SZRlxCnuOZX7BgQbV+kOMS/c+fP188//zz8rUQeQlhiIhH5MFEJKevoc8999xTHH300XJreikU7vKNOe3rEIWowo4CSLa2ZMmSalvqMaeISE1qyDmKKu6myA7d7m0WYsL1zj777Ipkn3aueDwJkAAJkAAJkAAJkAAJlDIBikvP7IREXPomGFE2+KCqN3xQxYdn/IMonZiG/F6IbsnaiiEus45NPw850NAUO/013L8twinkukoagON6660Xcor1GEQAuRquY1av1e8F68AXjehi4Ls2tpQi71zapsaIf+MfVzSXj0HaaxfieKz3SZMmJXYdIpFc84Qqx8iF6GqIGkvKk1iIe867Tz2PZda+IaEgjrCt2iUuISwRcfnFF1/IS+UhLpFj9JprrpF9ojCLynmoqopvs802Mhfim2++Ka+J929s8TYjPfFMYa6nTZsm/vWvf1VDgff9o446SkpWRGEiv6Jt3WAdHHjggWL//feXayJk/WVlXirnQfRCWKM6uGo2aW0bb9YCPe+++65AcS/1+zZUeJYKM46DBEiABEiABEiABEiABOoqAYpLz8znIS6nTJki+vXrZ73SmjVrogso1AVxiQ/zSi7YPth36dJFRkllaUrMIf9Zjx49pFzIIg9852y33Xay6IfZlMxo1qyZWLVqlfMWIGd917F1gGvMnj27RjSbj5cuLdFH586dxbx58xJPyzI23xjyfr3Q4jLv8ZZif8UU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bcXSpUtr6Z2BlyUBEiABEiABEiABEiABEiABEiABElAEKC5reS3kJS4LeRtpxWVWeea6ByUu0+S0NPurC+IyZh3URsRlHmtF76PUIy5j5ofnkgAJkAAJkAAJkAAJkAAJkAAJkEBdI0BxWcszXonishBIuVV8HdWYSta+eakNcekbU9rXKS7TEuPxJEACJEACJEACJEACJEACJEACJFC6BCgutbnJI/or7VTnIS4LPe60EZdpGYQcXwxxWWiOSTkuVRRpyPb32hCX+rhiq4qjr7Vr1yZWZg9ZC65jKC5jCfJ8EiABEiABEiABEiABEiABEiABEigdAnVeXC5ZskT8+OOPBZ2RXXfdNbFyeKy4hMhauHBhQcd///33i3HjxjmvgariScV58hhcHuJy5syZYsstt6wxHCUsW7duLbbYYouq19MW04E0c7VOnTpZXw4RlupEl7j84IMPxIoVKzLj/vzzz8UxxxzjPL99+/Zi4sSJicck3aN+woIFCzKP0Xfiyy+/LIYMGeI7zPn6TjvtJKZOnRrVx0EHHSSjY9OuoaiL8mQSIAESIAESIAESIAESIAESIAESqDACdV5cNm/eXKxcuVJOaxqBlGYdTJkyRfTr1896Sqy4RKfrrbdeQbcQh9xrOYhL3308+OCDokePHr7DEl/3SaqGDRuKP/3pT4nnX3zxxeKrr75yXt8lLi+77DJx6aWXZh5/JZxYqGc4LZtCRsamHQuPJwESIAESIAESIAESIAESIAESIIFyJUBxqYnLQk1iocWlT5gV6r70fiku14nvpIbXULH7ww8/rHGIisxr2rSpWL16tVNCU1wWYzXHX0PNE6Mu41myBxIgARIgARIgARIgARIgARIggbpLgOKS4jKX1U9x6RaXgLzddttZxaWaAIjLVatWOeeD4jKX5VrwThhxWXDEvAAJkAAJkAAJkAAJkAAJkAAJkEAdIEBxSXGZyzKHuBw+fHhV1GHekWZ55Lj03Wiht4rnLS5Nxtwq7pvh4r3+66+/yovpW9dLITK6eAR4JRIgARIgARIgARIgARIgARIgARKIJ0BxSXEZv4qEELaIS5+8VK/7jsMAK1lc6lvFQyMubcwoLnNZyrl0YkZchqzxXC7MTkiABEiABEiABEiABEiABEiABEiggghQXFJc5rKclbhMK2iU4PFFo9W2uAy5L989IOJSLwRlgs+6VVwx/OMf/yiL85RKgZpcFlaZdpK0VTxkHZXpLXPYJEACJEACJEACJEACJEACJEACJJA7AYpListcFlWWHJe6xPEJndoWl4DkG2OIuLQV51ETECouk8bBiMtclnJ0J1gHa9eudRZr8q2l6EGwAxIgARIgARIgARIgARIgARIgARKoAAIUlxSXuSzjq6++WgwdOrSarAmVMzhuvfXWExtttFHiWGpTXGJ8yFn4yy+/OFnVq1fP+Tqqir/77rs1jlHReTvvvLOzOA+E2HfffZcoxP785z+LsWPH5jKf7CSOwA8//ODsAGvdJ7rjRsCzSYAESIAESIAESIAESIAESIAESKD8CVBc1hFx2b9//4Kt1jVr1ggUtsm6RRnntW7dWixbtixxjIMHDxY///xz4uuzZ88WH3/8sfMeTznlFLHJJpskHjNy5EjRrl076+uvvfaa2H333Z39N2/eXBxxxBGJx9x22201XjOZuebpscceEytWrHCO4fjjjxcNGza0HoNz0Yer7bHHHmL//ffPvFZs95i5s4QTe/ToIRo3bmx9FWsAa8HVdt11V9GpU6dU85T3PXzyySeiUaNGeXfL/kiABEiABEiABEiABEiABEiABEigoghQXNYRcZmUcy+P1bxo0SLRsWPHqK4gzFzi0td5ly5dxPz5852HffTRR2KbbbbxdWV9PURcHnPMMWL69OmJ/YdE2LnmCf3PnDnTOX6Ms1WrVtZjZs2aJXr27Ok8f9SoUeKaa67JxAgnhdxj5s7/feIzzzwjDjjgAGs3Tz/9tFNK4iTI4UmTJkXNU+w9UFzGEuT5JEACJEACJEACJEACJEACJEACdYEAxSXFZfQ6p7hch5DikuIy9GGiuAwlxeNIgARIgARIgARIgARIgARIgATqMgGKS4rL6PVPcUlxqRYRIy7DHieKyzBOPIoESIAESIAESIAESIAESIAESKBuE6C4pLiMfgIoLikuKS7TPUYUl+l48WgSIAESIAESIAESIAESIAESIIG6SYDikuIyeuVTXFJcUlyme4woLtPx4tEkQAIkQAIkQAIkQAIkQAIkQAJ1kwDFJcVl9MqnuKS4pLhM9xhRXKbjxaNJgARIgARIgARIgARIgARIgATqJgGKS4rL6JWfRlwiB6KtcnabNm3Eyy+/nHksqCq+YMECa9+q07RVxTFOlbNRVRVPGj+ukaY4T1I/lV5V3MUvdPJDqorr1zGvGVpVPI+xJt0TxWXobPM4EiABEiABEiABEiABEiABEiCBukyA4rKOiMvYRf7000+LAw880NrN0qVLRd++fRMvARm3bNmyxNezCqKs5yUN5MEHHxTdu3eXslJJS/Xvd999Vxx77LFOjIcccoj4y1/+Is9db731ahzbrl075/mQoz///LOTEwSvq82YMUPsuOOO1kNmzZolevbs6V0KLq6dO3cW8+bNc47Rd4G2bdv6Dkl8/dNPPxUQ0DbBqyRzkyZNxNZbb23tA+ctX77cKbj1E/NeY6pvisvMS4AnkgAJkAAJkAAJkAAJkAAJkAAJ1CECFJcUl0HL3SUufR1Axm200UbWw2LFUOz5+qAgLnv06CF/pEdb+u4vzet6v+Y1mjZtKlatWuXsThd2accYKi6TBgDWnTp1ihaXrqhSH0uI4ZEjRzoPu+6668SwYcNqHIPr/vTTT6JevXq+yxT8dYrLgiPmBUiABEiABEiABEiABEiABEiABCqAAMUlxWXiMtalYKy43HjjjWtEudkiG2OkVuzzqItLva+0gjBpHLZ+9J81a9ZMfPjhh875+PXXXzOLVZ+4VBGLag7M+cGF84i4jJljJS6ThDV+PmHCBHHuueda5fMPP/wgNt1008SIyzxFuGs9UlzGPq08nwRIgARIgARIgARIgARIgARIoC4QoLikuAwWlx07dqzK+Zjm4UCUG8RlqbckcZlm3DGSExGXq1evdm5jDom4TBqDT1zq95kk8PIQl5CvSpKmYYtj9YhL2xjxs2uvvbZGxKVi8uOPP3ojLoshLyku0848jycBEiABEiABEiABEiABEiABEqiLBCguKS6D1n3WiEsIo19++aVgW8WDBh94kE9cxkhJ3xDQNyIufVvFdeln5uH0XSONuEzqKw9xmUfEpetesVV86NChVXJUn7cQcenjmMfrFJd5UGQfJEACJEACJEACJEACJEACJEAClU6A4vLf4rJQUVbod/LkyeL000+3RpmtWbNGNG7c2LnODjroIFkxO6lljV7zLW5zq3hdiLhUxXlMNoWUlupaoTkufWNJEpoh4tL1HJRLjktEXJpbxdNEXPqeizxep7jMgyL7IAESIAESIAESIAESIAESIAESqHQCFJdaxGWh5OWUKVNEv379rGupVMRl0rZbFR2XNeISN43i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GLaA4HLY+zNa3bRPXLYlGXJwmV/LVgou2+5PH+PzULFrcFn2VvHJ0DJ878RlHzumBwECBAgQIECAAAECBAgQmH8BweXA9zAERqdOncrW1tZKV1p30i8Upvw8RMHlv62p24e+PuPSicvyP1onLgf+D83yCBAgQIAAAQIECBAgQIBAC4G5Di5DGPfp06cWlzt/Q8M1Li0tZfv37y9cfFODlZWVZBcfTsh9+fKlsv/y8nK2Z8+e0jHhLctVwV+4ztXV1U7XEGq/f/+era+vlxqGuffu3Tuyzr/qgsjJZh8/fqxc38LCQnbgwIHagLOsydevX7ONjY1OBn0VBZ99+/Z1bvf58+fs169flfUx92rwCU5D/wr3weLi4tCXaX0ECBAgQIAAAQIECBAgQGCmAnMdXAa5Ni9Pmal0wskZ9IOb2jF1/34U/u8y/ib3VHPoS4AAAQIECBAgQIAAAQIECBCYFJj74NKWEthuAvMagG63fXK9BAgQIECAAAECBAgQIECAQJyA4DLOTzUBAgQIECBAgAABAgQIECBAgAABAgkEBJcJULUkQIAAAQIECBAgQIAAAQIECBAgQCBOQHAZ56eaAAECBAgQIECAAAECBAgQIECAAIEEAoLLBKhaEiBAgAABAgQIECBAgAABAgQIECAQJyC4jPNTTYAAAQIECBAgQIAAAQIECBAgQIBAAgHBZQJULQkQIECAAAECBAgQIECAAAECBAgQiBMQXMb5qSZAgAABAgQIECBAgAABAgQIECBAIIGA4DIBqpYECBAgQIAAAQIECBAgQIAAAQIECMQJCC7j/FQTIECAAAECBAgQIECAAAECBAgQIJBAQHCZAFVLAgQIECBAgAABAgQIECBAgAABAgTiBASXcX6qCRAgQIAAAQIECBAgQIAAAQIECBBIICC4TICqJQECBAgQIECAAAECBAgQIECAAAECcQKCyzg/1QQIECBAgAABAgQIECBAgAABAgQIJBAQXCZA1ZIAAQIECBAgQIAAAQIECBAgQIAAgTgBwWWcn2oCBAgQIECAAAECBAgQIECAAAECBBIICC4ToGpJgAABAgQIECBAgAABAgQIECBAgECcgOAyzk81AQIECBAgQIAAAQIECBAgQIAAAQIJBASXCVC1JECAgkavNQAAAxlJREFUAAECBAgQIECAAAECBAgQIEAgTkBwGeenmgABAgQIECBAgAABAgQIECBAgACBBAKCywSoWhIgQIAAAQIECBAgQIAAAQIECBAgECcguIzzU02AAAECBAgQIECAAAECBAgQIECAQAIBwWUCVC0JECBAgAABAgQIECBAgAABAgQIEIgTEFzG+akmQIAAAQIECBAgQIAAAQIECBAgQCCBgOAyAaqWBAgQIECAAAECBAgQIECAAAECBAjECQgu4/xUEyBAgAABAgQIECBAgAABAgQIECCQQEBwmQBVSwIECBAgQIAAAQIECBAgQIAAAQIE4gQEl3F+qgkQIECAAAECBAgQIECAAAECBAgQSCAguEyAqiUBAgQIECBAgAABAgQIECBAgAABAnECgss4P9UECBAgQIAAAQIECBAgQIAAAQIECCQQEFwmQNWSAAECBAgQIECAAAECBAgQIECAAIE4AcFlnJ9qAgQIECBAgAABAgQIECBAgAABAgQSCAguE6BqSYAAAQIECBAgQIAAAQIECBAgQIBAnIDgMs5PNQECBAgQIECAAAECBAgQIECAAAECCQQElwlQtSRAgAABAgQIECBAgAABAgQIECBAIE5AcBnnp5oAAQIECBAgQIAAAQIECBAgQIAAgQQCgssEqFoSIECAAAECBAgQIECAAAECBAgQIBAnILiM81NNgAABAgQIECBAgAABAgQIECBAgEACAcFlAlQtCRAgQIAAAQIECBAgQIAAAQIECBCIExBcxvmpJkCAAAECBAgQIECAAAECBAgQIEAggYDgMgGqlgQIECBAgAABAgQIECBAgAABAgQIxAkILuP8VBMgQIAAAQIECBAgQIAAAQIECBAgkEBAcJkAVUsCBAgQIECAAAECBAgQIECAAAECBOIEBJdxfqoJECBAgAABAgQIECBAgAABAgQIEEggILhMgKolAQIECBAgQIAAAQIECBAgQIAAAQJxAoLLOD/VBAgQIECAAAECBAgQIECAAAECBAgkEBBcJkDVkgABAgQIECBAgAABAgQIECBAgACBOIG/l8z9hfMhYBkAAAAASUVORK5CYII=";
	
	            final String pureBase64Encoded = encodedString.substring(encodedString.indexOf(",")  + 1);

	            final byte[] decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT);

	            image = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
	//			String base64String="data:image/png;base64,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ERABERABERABERABERCBBAK7Qtv3uI8e92F9+4hrFj7SWmWxIWg1bYpNPcH/7P4v62NM0ezgKn8mG+5stnoBhJnrCgups5I68Wy4UfQbL8P6S7D+MnbzOGy49mpNjHNnwZfKyzSCW4e9sGLZtAS22QgnzgtrLEiXBXFCj2mLscv6WL47ZEWs3nM5hDZvIV17lHpGW0gorfOfELC6QAf2wdh6lj9MVCl1FUC6uE4hMid8H9vPgJucx/NA0QyPM+Rx83EeT3qPbhIBERABERABERABERABERABERABEdhzArtC24/4NDs8gOHT1nKzsdxk4eY20SNmz6x/XmkglcjmhZ5sgDED3zNzGDtzre9ZuB2z+BHfe+GtavlWWfhgqxDVNl8r+vU/YV2G2/qfyoIJ7ry2cwWkseoDu9wi7r1uQaV2DpjXO2KZa20ZbYHlG9OIIs/WH0uxN8Z2Sde7LpfQ5quO1jPaps0+uwhCW0rxiqgI3SXOtX2NU57dvec48Crg+l8Dj5r7fa9LBERABERABERABERABERABERABETgMhDYFdoe4gtYvkjOF4FrLRzBcm2r0OYVp7CI5T4fy8iqJZnhstm82OYy2q6GmVJUK4sfmIUPgRffyuYy2bw1dAPWX4GNolkntnnh7XhhLR2uQr5avriRD1bGGRPamuJZaC1CQlWbBbRL/0l9Nkloa6xFZ/y1+z8oGW0TPGtW3LH5TpFlVo05YROuJ0W2iV9TvCv5+50otll4x+CLH7yD4WfAz8x9/Dz5PbpRBERABERABERABERABERABERABERgTwnsCm3f5yvAVzB82eYcNIYDwIGR3bMmzMT0jU5RrswKcwJbcUZbHwYHoH+g6H0hhJt9b+ZugNmyDdchXwfXbzjb6EtYZx/157O9XZzV5s5vy9exw/VS+7P1I9EKaC1VQYNUG4JH/QywUcZTi9AyIeqNHbjVso61ODvtpecxpnvzaNzau1xG27CsOkpvGZvtw/UX45qoOrr6qLeQRq/I2oXwh+yxY3OuXpJoxa3WOnoWWz3oMqCJZQkE6sad6ny32nvK9TsNnDaGU1geAn5oPsYPL8Z6aUwREAEREAEREAEREAEREAEREAEREIFuAnWh7d8CVZsHqjbuwkywN3af69aDqiBCfxn6S+D62etLce16L7iZeSe63bybreay1up20e33y6qj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 1111";
//				 byte[] bytes1=null;
//				try {
//					bytes1 = decoder.decodeBuffer(base64String);
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}   		
				//image = BitmapFactory.decodeStream(temp_stream);
				//image = getBitmapFromByte(bytes1);
				if(image == null){
					return ERR_PROCESSING;
				}
				

				//download bitmap
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
			}
			//*********************************************************************************************
			//download bitmap to flash and print
			else if(img_type == 1)
			{
				//download bitmap
				String sigPaths[] = img_name.split("@");
//				String sigPaths[] = str_name.split("@");
				int image_num = sigPaths.length;
				Bitmap cg_image[] = new Bitmap[image_num];
				int i = 0;
				
				//*******************************************************************************************
				//read bitmap data
				for(i = 0; i < image_num; i++)
				{
					try {
						temp_stream = new FileInputStream(sigPaths[i]);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					if(temp_stream == null)
					{
						return ERR_PROCESSING;
					}
					//Application of memory can not be more than 32M 
					cg_image[i] = BitmapFactory.decodeStream(temp_stream);
					if(cg_image[i] == null){
						temp_stream = null;
						return ERR_PROCESSING;
					}
					temp_stream = null;
				}
				
				error_code = pos_sdk.imageDownloadToPrinterFlash(image_num, cg_image, PrinterWidth);
				if(error_code != POS_SUCCESS){
					pos_sdk.pos_command.WriteBuffer(pzsCommand, 0, pzsCommand.length, 5000);//Empties the buffer  of FALSH
					return error_code;
				}
				
				//print bitmap
				for(i = 0; i < image_num;i++)
				{
					error_code = pos_sdk.imageFlashPrint(i+1, 0);
					if(error_code != POS_SUCCESS)
					{
						pos_sdk.pos_command.WriteBuffer(pzsCommand, 0, pzsCommand.length, 5000);//Empties the buffer  of FALSH
						return error_code;
					}	
				}
				//destroy bitmap buffer
				for(i = 0; i < image_num; i++)
				{
					cg_image[i].recycle();
				}
			}
		if(printMode == PRINT_MODE_PAGE)
		{
    		//******************************************************************************************
    		//print in page mode
			error_code = pos_sdk.pageModePrint();
    		
			//*****************************************************************************************
			//clear buffer in page mode
			error_code = pos_sdk.pageModeClearBuffer();   			
		}
		temp_stream = null;
		pos_sdk.pos_command.WriteBuffer(pzsCommand, 0, pzsCommand.length, 5000);//Empties the buffer  of FALSH
		return error_code;
	}
	
	/**
	 * Name��TestUserDefinedCharacter
	 * 
	 * Function�� test User-Defined Character
	 * 
	 * Parameter��

	 * 					
	 * Return��
	 * @return SUCCESS��POS_SUCCESS��FAIL��ERR_PROCESSING,ERR_PARAM
	 */
	public int TestUserDefinedCharacter(POSSDK pos_sdk,int printMode)
	{
		String str = "0123456789";
		String path =  
				"/data/bmp/u1.bmp" + "@" + 
			    "/data/bmp/u2.bmp" + "@" + 
			    "/data/bmp/u3.bmp";
		
		FileInputStream temp_stream = null;
		String sigPaths[] = path.split("@");
		int image_num = sigPaths.length;
		Bitmap cg_image[] = new Bitmap[image_num];
		int i = 0;
		
		if(printMode == PRINT_MODE_PAGE)
		{
			//*****************************************************************************************
			//set print area
			error_code = pos_sdk.pageModeSetPrintArea(0,0,640,500,0);
			if(error_code !=POS_SUCCESS)
			{
				return error_code;
			}
			
			//set print position
			error_code = pos_sdk.pageModeSetStartingPosition(20,200);  
			if(error_code !=POS_SUCCESS)
			{
				return error_code;
			}
		}
		
		//*******************************************************************************************
		//read bitmap data
		for(i = 0; i < image_num; i++)
		{
			try {
				temp_stream = new FileInputStream(sigPaths[i]);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(temp_stream == null)
			{
				return ERR_PROCESSING;
			}
			cg_image[i] = BitmapFactory.decodeStream(temp_stream);
			temp_stream = null;
		}
		
		
		//Choose Font User Defined
		error_code = pos_sdk.textUserDefinedCharacterEnable(1);
		if(error_code != POS_SUCCESS){
			return error_code;
		}
		
		error_code = pos_sdk.textUserDefinedCharacterDefine(3, 12, 48, 50, cg_image);
		if(error_code != POS_SUCCESS){
			return error_code;	
		}
		
		error_code = pos_sdk.textSelectFontMagnifyTimes(2,2);
		
		//print text data
		error_code = pos_sdk.textPrint(str.getBytes(), str.getBytes().length);
		if(error_code != POS_SUCCESS)
		{
			return error_code;
		}
		//feed line
		error_code = pos_sdk.systemFeedLine(1);
		
		//Cancel Font User Defined of CharCode
		error_code = pos_sdk.textUserDefinedCharacterCancel(48);
		error_code = pos_sdk.textUserDefinedCharacterCancel(49);
		error_code = pos_sdk.textUserDefinedCharacterCancel(50);
		
		if(printMode == PRINT_MODE_PAGE)
		{
    		//******************************************************************************************
    		//print in page mode
			error_code = pos_sdk.pageModePrint();
    		
			//*****************************************************************************************
			//clear buffer in page mode
			error_code = pos_sdk.pageModeClearBuffer();   			
		}
		return error_code;
	}
	
	/**
	 * Name��TestRasterBitmap
	 * 
	 * Function��Test Raster Bitmap
	 * 
	 * Parameter��

	 * 					
	 * Return��
	 * @return SUCCESS��POS_SUCCESS��FAIL��ERR_PROCESSING,ERR_PARAM
	 */
	public int TestRasterBitmap(POSSDK pos_sdk,int printMode,String str_data,int paintSize,int bold)
	{
		final int PrinterWidth = 640;
		if(printMode == PRINT_MODE_PAGE)
		{
    		//*****************************************************************************************
    		//set print area
    		error_code = pos_sdk.pageModeSetPrintArea(0,0,640,500,0);
    		if(error_code != POS_SUCCESS)
    		{
    			return error_code;
    		}
    		//*****************************************************************************************
    		//set print position
    		error_code = pos_sdk.pageModeSetStartingPosition(20,200);  
    		if(error_code != POS_SUCCESS)
    		{
    			return error_code;
    		}
		}
	
		Bitmap c_image = null;
		
		//set print position
		pos_sdk.standardModeSetStartingPosition(10);
		
		//create raster bitmap
		c_image = pos_sdk.imageCreateRasterBitmap(str_data,paintSize,bold);
		error_code = pos_sdk.imageStandardModeRasterPrint(c_image,PrinterWidth);
		if(error_code != POS_SUCCESS)
		{
			return error_code;
		}	
		
    	if(printMode == PRINT_MODE_PAGE)
    	{
    		//******************************************************************************************
    		//print in page mode
			error_code = pos_sdk.pageModePrint();
    		
			//*****************************************************************************************
			//clear buffer in page mode
			error_code = pos_sdk.pageModeClearBuffer(); 		
    	}
			
		
		return error_code;
	}
	/**
	 * Name��POSNETQueryStatus
	 * 
	 * Function�� Get Printer state
	 * 
	 * Parameter��

	 * 					
	 * Return��
	 * @return SUCCESS��POS_SUCCESS��FAIL��ERR_PROCESSING,ERR_PARAM
	 */
	public int POSNETQueryStatus(POSSDK pos_sdk,byte[] pStatus) {

		int result = POS_SUCCESS;
		int data_size = 0;
		byte[] recbuf = new byte[64];// accept buffer
		
		//Get firmware version 
//		byte pszCommand[] = {0x1D,(byte) 0x99,0x42,0x45,(byte) 0x92,(byte) 0x9A,0x35};
//		pos_sdk.pos_command.WriteBuffer(pszCommand, 0, pszCommand.length, 10000);
//		data_size = pos_sdk.pos_command.ReadBuffer(recbuf, 0, 25, 10000);
//		Log.d(LOG_TAG, "POS_ReadPort---enter,parameter:" + pos_sdk.pos_command.byte2hex(recbuf)+"--"+data_size);
//		if(data_size != -1){
//			return POS_SUCCESS;
//		}
		
		//Query Status
		data_size = pos_sdk.systemQueryStatus(recbuf, 4, 0);
		
//		Log.d(LOG_TAG, "POS_ReadPort---enter,parameter:" + pos_sdk.pos_command.byte2hex(recbuf)+"--"+data_size);
//		if(MainActivity.port_type != MainActivity.BLUETOOTHPORT)
//		{
			if ((recbuf[0] & 0x04) == 0x04) {
				// Drawer open/close signal is HIGH (connector pin 3).
				pStatus[0] |= 0x01;
			} else {
				pStatus[0] &= 0xFE;
			}

			if ((recbuf[0] & 0x08) == 0x08) {
				// Printer is Off-line.
				pStatus[0] |= 0x02;
			} else {
				pStatus[0] &= 0xFD;
			}

			if ((recbuf[0] & 0x20) == 0x20) {
				// Cover is open.
				pStatus[0] |= 0x04;
			} else {
				pStatus[0] &= 0xFB;
			}

			if ((recbuf[0] & 0x40) == 0x40) {
				// Paper is being fed by the FEED button.
				pStatus[0] |= 0x08;
			} else {
				pStatus[0] &= 0xF7;
			}

			if ((recbuf[1] & 0x40) == 0x40) {
				// Error occurs.
				pStatus[0] |= 0x10;
			} else {
				pStatus[0] &= 0xEF;
			}

			if ((recbuf[1] & 0x08) == 0x08) {
				// Auto-cutter error occurs.
				pStatus[0] |= 0x20;
			} else {
				pStatus[0] &= 0xDF;
			}

			if ((recbuf[2] & 0x03) == 0x03) {
				// Paper near-end is detected by the paper roll near-end sensor.
				pStatus[0] |= 0x40;
			} else {
				pStatus[0] &= 0xBF;
			}

			if ((recbuf[2] & 0x0C) == 0x0C) {
				// Paper roll end detected by paper roll sensor.
				pStatus[0] |= 0x80;
			} else {
				pStatus[0] &= 0x7F;
			}	
//		}
//		else //real-time status
//		{
//			if ((recbuf[0] & 0x04) == 0x04) {
//				// Drawer open/close signal is HIGH (connector pin 3).
//				pStatus[0] |= 0x01;
//			} else {
//				pStatus[0] &= 0xFE;
//			}
//
//			if ((recbuf[0] & 0x08) == 0x08) {
//				// Printer is Off-line.
//				pStatus[0] |= 0x02;
//			} else {
//				pStatus[0] &= 0xFD;
//			}
//
//			if ((recbuf[1] & 0x04) == 0x04) {
//				// Cover is open.
//				pStatus[0] |= 0x04;
//			} else {
//				pStatus[0] &= 0xFB;
//			}
//
//			if ((recbuf[1] & 0x08) == 0x08) {
//				// Paper is being fed by the FEED button.
//				pStatus[0] |= 0x08;
//			} else {
//				pStatus[0] &= 0xF7;
//			}
//
//			if ((recbuf[1] & 0x40) == 0x40) {
//				// Error occurs.
//				pStatus[0] |= 0x10;
//			} else {
//				pStatus[0] &= 0xEF;
//			}
//
//			if ((recbuf[2] & 0x08) == 0x08) {
//				// Auto-cutter error occurs.
//				pStatus[0] |= 0x20;
//			} else {
//				pStatus[0] &= 0xDF;
//			}
//
//			if ((recbuf[3] & 0x04) == 0x04 || (recbuf[3] & 0x08) == 0x08) {
//				// Paper near-end is detected by the paper roll near-end sensor.
//				pStatus[0] |= 0x40;
//			} else {
//				pStatus[0] &= 0xBF;
//			}
//
//			if ((recbuf[3] & 0x20) == 0x20 || (recbuf[3] & 0x40) == 0x40) {
//				// Paper roll end detected by paper roll sensor.
//				pStatus[0] |= 0x80;
//			} else {
//				pStatus[0] &= 0x7F;
//			}
//			
//		}

		return result;
	}
	
	//create thread1
	private class thread1 extends Thread{
		private boolean isread = false;
		String str = "1111111111";
		@Override
		public void run() {
			super.run();
			ThreadFlg1 = true;
			isread = true;
			ThreadFlg1 = false;
		}
		public void printText(){
			thread_sdk.textPrint(str.getBytes(), str.getBytes().length);
		}
	}
	
	//create thread2
	private class thread2 extends Thread{
		private boolean isread = false;
		String str ="2222222222";
		@Override
		public void run() {
			super.run();
			ThreadFlg2 = true;
			isread = true;
			ThreadFlg2 = false;
		}
		public void printText(){
			thread_sdk.textPrint(str.getBytes(), str.getBytes().length);
		}
	}
}
