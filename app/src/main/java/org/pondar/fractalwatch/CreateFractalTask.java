package org.pondar.fractalwatch;

import android.graphics.Bitmap;
import android.os.AsyncTask;

public class CreateFractalTask extends AsyncTask<Void, Integer, Bitmap> {

	private Bitmap bitmap = null;
	private GameView view;
	private int[] xadd;
	int w,h,halfw,halfh;
	private float xMin = -2;
	private float yMax = 1; 
	private float xSpan = 3;
	private float ySpan = 2;
	private int maxIter = 30;
	
	
	
	public void setBounds(float xmin, float ymax, float xspan, float yspan)
	{
		this.xMin = xmin;
		this.yMax = ymax;
		this.xSpan = xspan;
		this.ySpan = yspan;
	}
	
	public CreateFractalTask(GameView view,int[] xadd,int w, int h)
	{
		this.view = view;
		this.xadd = xadd;
		this.w = w;
		this.h = h;
		this.halfw = w/2;
		this.halfh = h/2;
	}
	
	ColouringScheme colourer = new DefaultColouringScheme();
	
	int pixelInSet (int xPixel, int yPixel, int maxIterations) {
		boolean inside = true;
		int iterationNr;
		float newx, newy;
		float x, y;
		
		// Set x0 (real part of c)
		float x0 = xMin + ( (float)xPixel / (float)w) * xSpan;
		float y0 = yMax - ( (float)yPixel / (float)h) * ySpan; 
	
		// Start at x0, y0
		x = x0;
		y = y0;
		
		//Run iterations over this point
		for (iterationNr=0; iterationNr<maxIterations; iterationNr++) {
			// z^2 + c
			newx = (x*x) - (y*y) + x0;
			newy = (2 * x * y) + y0;
		
			x = newx;
			y = newy;
		
			// Well known result: if distance is >2, escapes to infinity...
			if ( (x*x + y*y) > 4) {
				inside = false;
				break;
			}
		}
		
		if(inside)
			return  colourer.colourInsidePoint();
		else
			return colourer.colourOutsidePoint(iterationNr, maxIterations);
	}
	
	@Override
	protected void onProgressUpdate(Integer... values) {
		//System.out.println("progres:"+values[0]);
		//super.onProgressUpdate(values);
		if (bitmap!=null)
		{
			//view.setBitmap(bitmap);
			view.invalidate();
		}
	}
	
	
	public void setBitmap(Bitmap bit)
	{
		this.bitmap = bit;
	}
	
	@Override
	protected Bitmap doInBackground(Void... params) {
		Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
		if (bitmap==null)
		{
			bitmap = Bitmap.createBitmap(w, h, conf); // this creates a MUTABLE bitmap
			view.setBitmap(bitmap);
		}//create fratal here.
		System.out.println("DoInBackGround started: w = "+w+
			", h = "+h);
		
    	float[] points = new float[4];

    	int halfx = w/2;
    	System.out.println("Starting loop");
	    for (int y = 0; y<h/2;y++)
	    {
	    	int prog = Math.round(100.0f* (float ) y / (float) (h/2));
	    	//System.out.println("doing Y:"+y);
	    	int xstart = halfx-xadd[y];
	    	int xend = halfx+xadd[y];
	    	if (view.isRound()==false)
	    	{
	    		xstart = 0;
	    		xend = w-1;
	    		//System.out.println("Doing square fractal!!!!");
	    	}
	    	
	    	for (int x = xstart; x<xend;x++)
	    	{
	    		int y2 = h-y;
	    		int color1 = pixelInSet(x, y, maxIter);    		
	    		//paint.setColor(color1);
	    		points[0]=x;
	    		points[1]=y;
	    		points[2]=x;
	    		points[3]=y2;
	    		bitmap.setPixel(x, y, color1);
	    		bitmap.setPixel(x, y2-1, color1);
	    		//canvas.drawPoints(points,paint);  		
	    	}
	    	if (y%2 == 0)
	    		publishProgress(prog);

	    	
	    }
	    System.out.println("Before return");
	    finishedTask(bitmap);
		return bitmap;
	}
	

	
	@Override
	protected void onPostExecute(Bitmap result) {
		//super.onPostExecute(result);
		System.out.println("In onPostExecute");
		view.setBitmap(result);
		view.invalidate();
		finishedTask(bitmap);
	}
	
	protected void finishedTask(Bitmap bitmap)
	{
		
	}

}
