package org.pondar.fractalwatch;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowInsets;

public class GameView extends View{

	//width and height
	private int w = 0, h = 0;
	private int[] xadd;
	private Bitmap bitmap = null;
	private Paint paint = new Paint();
	//we make the fractal as a taks
	private CreateFractalTask task = null;
	private boolean isZooming = false;
	private int taskStarted = 0;
	private Activity activity;

	private boolean isRound;
	
	public boolean isRound()
	{
		return isRound;
	}

	//This method can be used to detect if the watch is
	//round or not
	@Override
	public WindowInsets onApplyWindowInsets(WindowInsets insets) {
		if (insets.isRound())
		{
			this.isRound = true;
		}
		else
		{
			this.isRound = false;
		}
		return super.onApplyWindowInsets(insets);
	}
	

	
	
	public void setActivity(Activity activity)
	{
		this.activity = activity;
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
	}
	
	@Override
	public void invalidate() {
		super.invalidate();
	}
	
	public void toogleZoom()
	{
		if (isZooming)
			stopZoom();
		else
		{
			zoomRunner = new Thread() {
				public void run() {
					startZoom();
				};
				
			};
			zoomRunner.start(); //startZoom();
			
		}
	}
	
	public Thread zoomRunner;
	
	//Start to do a zoom of the fractal
	public void startZoom()
	{
		isZooming = true;
		System.out.println("Start Zoom");
		float xMin = -2;
		float yMax = 1; 
		float xSpan = 3;
		float ySpan = 2;
		taskStarted = 0;
		CreateFractalTask zoomTask;	
		final GameView view = this;
		while (isZooming)
		{		
			if (zoomRunner.isInterrupted())
				return;
			if (taskStarted<1)
			{
				taskStarted++;
				System.out.println("start new task");
				xSpan = xSpan - 0.1f;
				ySpan = ySpan - 0.1f;
				xMin = xMin - 0.1f;
				//yMax = yMax - 0.2f;
			
				
				zoomTask = new CreateFractalTask(this, xadd, w, h) {
					
					@Override
					protected void finishedTask(Bitmap bitmap) {
						System.out.println("task finished");
						taskStarted--;
					
					}			
				};
				
				if (bitmap!=null)
				{
					System.out.println("Setting bitmap");
					zoomTask.setBitmap(bitmap);
				}
				zoomTask.setBounds(xMin, yMax, xSpan, ySpan);
				zoomTask.execute();
				
			}
		}

	}
	
	//Stop the zoom - after pressing the watch
	public void stopZoom()
	{
		if (isZooming)
		{
			//pause the zoom thread
			zoomRunner.interrupt();
		}
		isZooming = false;
		System.out.println("Stop Zoom");
	}
	
	public void setBitmap(Bitmap bitmap)
	{
		this.bitmap = bitmap;
	}
	
	//This methods is used to improve performance
	//On a round watch we do not need to calculate
	//all pixels, as some of them are outside of the
	//view
	public void calculateoffsets()
	{
		System.out.println("Calculating offsets");
        int halfh = h/2;
	
		for (int y = 0; y<halfh;y++)
		{
			double vi = Math.asin((double) y / (double) halfh);
			double x = Math.cos(vi)*160.0d;
			int index = halfh-y-1;
			xadd[index] = (int) Math.round(x);	

		}
		
		xadd[halfh]=w/2; //all width		
		
	}
	
	//This method is called when the view is created
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		System.out.println("in onsize changed");
		this.h = h;
		this.w = w;
		xadd = new int[h/2+1];
		calculateoffsets();
		if (task==null)
		{
			System.out.println("Starting Task");
			task = new CreateFractalTask(this, xadd, w, h);
			task.execute();
		}

	}
	
	
	//experimet - not used
	public void drawHalfScreens(Canvas canvas,Paint paint)
	{
		  int halfx = w/2;
		  paint.setColor(Color.parseColor("#CD5C5C"));
	      for (int y = 0; y<=h/2;y++)
	      {
	    	  canvas.drawLine(halfx-xadd[y], y, halfx+xadd[y], y, paint);
	      }
	      
	      paint.setColor(Color.parseColor("#0000FF"));
	      for (int y = 0; y<=h/2;y++)
	      {
	    	  canvas.drawLine(halfx-xadd[h/2-y], h/2+y, halfx+xadd[h/2-y], h/2+y, paint);
	      }
	}
	
	
	@Override
	protected void onDraw(Canvas canvas) {

		  if (canvas.isHardwareAccelerated())
		  {
			 // System.out.println("Canvas is hardware accelerated");
		  }
		  if (bitmap==null)
		  {
			  Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
			  bitmap = Bitmap.createBitmap(w, h, conf); // this creates a MUTABLE bitmap				
		  }
		  if (bitmap!=null)
			  canvas.drawBitmap(bitmap,0,0,paint);
	     
	      //drawHalfScreens(canvas,paint);
	     
	      //drawMandel(canvas,paint);

	     
	}


	//The following 3 constructors are neccesary to implement
	//for the Android View system to initialize the view
	public GameView(Context context) {
		super(context);
	}
	
	public GameView(Context context, AttributeSet attrs)
	{
		super(context,attrs);
	}
	
	public GameView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context,attrs,defStyleAttr);
	}
	

}
