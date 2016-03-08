package org.pondar.fractalwatch;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.wearable.view.WatchViewStub;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

@SuppressLint("ClickableViewAccessibility")
public class MainActivity extends Activity {
	
    private GestureDetectorCompat mDetector;
    private Activity activity;
	private GameView gameView;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;
		setContentView(R.layout.activity_main);
        mDetector = new GestureDetectorCompat(this, new MyGestureListener());

		final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);           
		stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
			@Override
			public void onLayoutInflated(WatchViewStub stub) {
			      gameView = (GameView) stub.findViewById(R.id.gameview);
			      gameView.setOnTouchListener(new MyTouch());
			      gameView.setActivity(activity);
			}
		});
		
		final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
		final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;
		System.out.println("ES version:"+configurationInfo.reqGlEsVersion);
		    if (supportsEs2)
		    {
		    	System.out.println("ES2.0 support");
		    }
		    else System.out.println("ES2.0 is not supported");
		int version = getVersionFromPackageManager(this);
		System.out.println("Version: "+version);

	} //onCreate finished
	
	private static int getVersionFromPackageManager(Context context) {
	    PackageManager packageManager = context.getPackageManager();
	    FeatureInfo[] featureInfos = packageManager.getSystemAvailableFeatures();
	    if (featureInfos != null && featureInfos.length > 0) {
	        for (FeatureInfo featureInfo : featureInfos) {
	            // Null feature name means this feature is the open gl es version feature.
	            if (featureInfo.name == null) {
	                if (featureInfo.reqGlEsVersion != FeatureInfo.GL_ES_VERSION_UNDEFINED) {
	                    return getMajorVersion(featureInfo.reqGlEsVersion);
	                } else {
	                    return 1; // Lack of property means OpenGL ES version 1
	                }
	            }
	        }
	    }
	    return 1;
	}
	
	class MyTouch implements View.OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
	        mDetector.onTouchEvent(event);
	        return true;
		}
		
	}

	//any touch events
	@Override 
    public boolean onTouchEvent(MotionEvent event){ 
        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
    
    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

		//do nothing
        @Override
        public boolean onDown(MotionEvent event) { 
            return true;
        }

		//single tap - we want to toogle the current zoom
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
			gameView.toogleZoom();
        	return true;
        }

        
    }
	 

	/** @see FeatureInfo#getGlEsVersion() */
	private static int getMajorVersion(int glEsVersion) {
	    return ((glEsVersion & 0xffff0000) >> 16);
	}
}
