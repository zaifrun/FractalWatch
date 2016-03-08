package org.pondar.fractalwatch;

import android.graphics.Bitmap;
import android.os.AsyncTask;

//The task used to create the fractal in the background
public class CreateFractalTask extends AsyncTask<Void, Integer, Bitmap> {

	private Bitmap bitmap = null;
	private GameView view;
	private int[] xadd;
	private int w,h,halfw,halfh;
	//the area to be examined
	//in this case from -2 to +1 on the x-axis (real)
	//and -i to i (imaginary axis) on the y-axis
	private float xMin = -2;
	private float yMax = 1; 
	private float xSpan = 3;
	private float ySpan = 2;
	private int maxIter = 30; //how many iterations to do for each point
	private boolean isRound;
	
	
	public void setBounds(float xmin, float ymax, float xspan, float yspan)
	{
		this.xMin = xmin;
		this.yMax = ymax;
		this.xSpan = xspan;
		this.ySpan = yspan;
	}

	//Constructor
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

	//determine color of one pixel - is the point inside or
	//outside of Mandelbrot.
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
		if (bitmap!=null)
		{
			view.invalidate(); //redraw the view - update is here
		}
	}
	
	
	public void setBitmap(Bitmap bit)
	{
		this.bitmap = bit;
	}


	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types

		if (bitmap==null)
		{
			bitmap = Bitmap.createBitmap(w, h, conf); // this creates a MUTABLE bitmap
			view.setBitmap(bitmap);
		}

		isRound = view.isRound();
	}

	@Override
	protected Bitmap doInBackground(Void... params) {

		System.out.println("DoInBackGround started: w = "+w+
			", h = "+h);
		
    	float[] points = new float[4];

    	int halfx = w/2;
	    for (int y = 0; y<h/2;y++)
	    {
	    	int prog = Math.round(100.0f* (float ) y / (float) (h/2));
	    	int xstart = halfx-xadd[y];
	    	int xend = halfx+xadd[y];
			//modify for square watch
	    	if (isRound==false)
	    	{
	    		xstart = 0;
	    		xend = w-1;
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
			//for every second line, redraw view
	    	if (y%2 == 0)
	    		publishProgress(prog);

	    	
	    }
	    finishedTask(bitmap);
		return bitmap;
	}
	

	
	@Override
	protected void onPostExecute(Bitmap result) {
		System.out.println("In onPostExecute");
		view.setBitmap(result);
		view.invalidate();
		finishedTask(bitmap);
	}

	//method to be overwritten as inner method - so empty now
	protected void finishedTask(Bitmap bitmap)
	{
		
	}

}
