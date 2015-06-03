package org.pondar.fractalwatch;

import org.pondar.fractalwatch.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.wearable.view.DismissOverlayView;
import android.support.wearable.view.WatchViewStub;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

@SuppressLint("ClickableViewAccessibility") public class MainActivity extends Activity {

	private TextView mTextView;
	private final int MYCONSTANT = 4;
	private int[] myArray = new int[10];
	private boolean myTest = true;
	private float t = 1.0f;
	private String name = "martin";
	
	private DismissOverlayView mDismissOverlay;
    private GestureDetectorCompat mDetector;
    Context context;
    Activity activity;
	GameView gameView;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		activity = this;
		setContentView(R.layout.activity_main);
        mDetector = new GestureDetectorCompat(this, new MyGestureListener());

		final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);           
		stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
			@Override
			public void onLayoutInflated(WatchViewStub stub) {
				  
				
			      mTextView = (TextView) stub.findViewById(R.id.text);
			      gameView = (GameView) stub.findViewById(R.id.gameview);
			      gameView.setOnTouchListener(new MyTouch());
			      gameView.setActivity(activity);
			       
			      mDismissOverlay = (DismissOverlayView) stub.findViewById(R.id.dismiss_overlay);
			    //  mDismissOverlay.setIntroText(R.string.long_press_intro);
			   //   mDismissOverlay.showIntroIfNecessary();

			        // Configure a gesture detector
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
		  
		  
		  
		
	} //onCreate finish
	
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
			//System.out.println("motion Event in on touch!");
	        mDetector.onTouchEvent(event);
	        return true;
		}
		
	}
	
	@Override 
    public boolean onTouchEvent(MotionEvent event){ 
		//System.out.println("motion Event!");
        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
    
    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        
        @Override
        public boolean onDown(MotionEvent event) { 
//            System.out.println("onDown: " + event.toString()); 
            return true;
        }
        
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
        	//System.out.println("single tap confirmed");
        	
			System.out.println("running zoom task");
			gameView.toogleZoom();
        	return true;
        }

        
    }
	 

	/** @see FeatureInfo#getGlEsVersion() */
	private static int getMajorVersion(int glEsVersion) {
	    return ((glEsVersion & 0xffff0000) >> 16);
	}
}
