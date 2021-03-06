package xyz.sanyabeast.lure;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.gms.common.api.ApiException;

/**
 * Created by Sanyabeast on 19.03.2018.
 */

public class RootActivity extends FragmentActivity {
    private String TAG = "Jive/RootActivity";

    public WebViewManager mWebViewManager;
    public GoogleServicesManager mGoogleServicesManager;
    public Storage mStorage;
    public AdsManager mAdsManager;
    public UITools mUITools;

    public int waitingKeyPressCode = 0;

    @Override
    public void onCreate(Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mUITools = new UITools(this);
        mUITools.hideNavigation();

        mAdsManager = new AdsManager(this);
        mStorage = new Storage(this);
        mGoogleServicesManager = new GoogleServicesManager(this);
        mWebViewManager = new WebViewManager(this);
        mWebViewManager.open("file:///android_asset/index.html");
        mWebViewManager.set("activity", mWebViewManager);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            mUITools.hideNavigation();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        Log.d(TAG, "Processing intent result: requestCode - " + requestCode + ", resultCode - " + resultCode);
        if (mGoogleServicesManager.checkRequestCode(requestCode)){
            mGoogleServicesManager.processRequestCode(requestCode, resultCode, intent);
            return;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Boolean preventDefault = keyCode == waitingKeyPressCode;
        waitingKeyPressCode = 0;

        mWebViewManager.send(new Envelope("android.button.pressed", event));
        mWebViewManager.set("lastKeyEvent", event);
        return preventDefault;
    }

    public void handleException(Exception e, String details) {
        int status = 0;

        if (e instanceof ApiException) {
            ApiException apiException = (ApiException) e;
            status = apiException.getStatusCode();
        }

        String message = getString(R.string.status_exception_error, details, status, e);

        new AlertDialog.Builder(RootActivity.this)
                .setMessage(message)
                .setNeutralButton(android.R.string.ok, null)
                .show();
    }

    public void runOnMainUIThread(Runnable runnable){
        new Handler(Looper.getMainLooper()).post(runnable);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        mWebViewManager.log(event);

        int action = MotionEventCompat.getActionMasked(event);

        switch(action) {
            case (MotionEvent.ACTION_DOWN) :
                Log.d(TAG,"Action was DOWN");
                return true;
            case (MotionEvent.ACTION_MOVE) :
                Log.d(TAG,"Action was MOVE");
                return true;
            case (MotionEvent.ACTION_UP) :
                Log.d(TAG,"Action was UP");
                return true;
            case (MotionEvent.ACTION_CANCEL) :
                Log.d(TAG,"Action was CANCEL");
                return true;
            case (MotionEvent.ACTION_OUTSIDE) :
                Log.d(TAG,"Movement occurred outside bounds " +
                        "of current screen element");
                return true;
            default :
                return super.onTouchEvent(event);
        }
    }



}
