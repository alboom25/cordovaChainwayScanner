package com.uwezo.chainScanner;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.zebra.adc.decoder.Barcode2DWithSoft;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.BroadcastReceiver;
import android.media.AudioManager;
import android.media.ToneGenerator;

import androidx.appcompat.app.AppCompatActivity;

import java.io.UnsupportedEncodingException;

public class chainScanner extends CordovaPlugin {
    PluginResult result = null;
    AppCompatActivity cordovaActivity;
    String barCode = "";
    Barcode2DWithSoft barcode2DWithSoft = null;
    String seldata = "ASCII";
    HomeKeyEventBroadCastReceiver receiver;
    private CallbackContext callbackContext = null;

    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;
        cordovaActivity = this.cordova.getActivity();
        PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
        result.setKeepCallback(true);
       if (action.equals("startListening")) {
            ScanBarcode();
            return true;
        }else{
           returnFailedInitData("Invalid call method");
           return true;
       }
    }

    protected void returnScannedData(String barcode) {
        result = new PluginResult(PluginResult.Status.OK, barcode);
        result.setKeepCallback(true);
        if (callbackContext != null) {
            callbackContext.sendPluginResult(result);            
        }
    }

    protected void returnFailedInitData(String message) {
        result = new PluginResult(PluginResult.Status.ERROR, message);
        result.setKeepCallback(true);
        if (callbackContext != null) {
            callbackContext.sendPluginResult(result);            
        }
    }

    private void ScanBarcode() {
        if (barcode2DWithSoft == null) {
            barcode2DWithSoft = Barcode2DWithSoft.getInstance();
            receiver = new HomeKeyEventBroadCastReceiver();
            this.cordova.getContext().registerReceiver(receiver, new IntentFilter("com.rscja.android.KEY_DOWN"));
            boolean opened =  barcode2DWithSoft.open(this.cordova.getActivity());
            if(opened){
                barcode2DWithSoft.setParameter(6, 1);
                barcode2DWithSoft.setParameter(22, 0);
                barcode2DWithSoft.setParameter(23, 55);
                barcode2DWithSoft.setParameter(402, 1);
                barcode2DWithSoft.scan();
                barcode2DWithSoft.setScanCallback(ScanBack);
            }else{
                returnFailedInitData("Unable to start barcode scanner");
            }
        } else {
            barcode2DWithSoft.scan();
            barcode2DWithSoft.setScanCallback(ScanBack);
        }
    }

    public Barcode2DWithSoft.ScanCallback ScanBack = new Barcode2DWithSoft.ScanCallback() {
        @Override
        public void onScanComplete(int i, int length, byte[] bytes) {
            if (length < 1) {
                if (length == -1) {
                    returnFailedInitData("Barcode scan was cancelled");
                } else if (length == 0) {
                    returnFailedInitData("Barcode scan timed out");
                } else {
                    returnFailedInitData("Barcode scan failed");
                }
            } else {
                barCode = "";
                ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
                try {
                    barCode = new String(bytes, 0, length, seldata);
                } catch (UnsupportedEncodingException ex) {}
                returnScannedData(barCode);
            }
        }
    };

    class HomeKeyEventBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("com.rscja.android.KEY_DOWN")) {
                int reason = intent.getIntExtra("Keycode", 0);
                if (reason == 280 || reason == 66) {
                    ScanBarcode();
                }
            }
        }
    }

}