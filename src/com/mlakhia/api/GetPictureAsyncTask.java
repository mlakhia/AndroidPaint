package com.mlakhia.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import com.mlakhia.draw.ToolBox;
import com.mlakhia.draw.shapes.Shape;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class GetPictureAsyncTask extends AsyncTask<String, String, String> {

	private static final String SERVER_IP = "192.168.1.116";
	//private static final String SERVER_IP = "10.0.2.2";
	private static final String SERVER_PORT = "9999";
	
	private final Context context;
	private final ToolBox toolbox;
	private final String userName;
	private final String pictureName;
	
	public GetPictureAsyncTask(Context context, ToolBox toolbox, String userName, String pictureName) {
		this.context = context;
		this.toolbox = toolbox;
		this.userName = userName;
		this.pictureName = pictureName;
	}

	@Override
	protected String doInBackground(String... params) {
		try {
			// Build
			URL baseURL = new URL("http://"+SERVER_IP+":"+SERVER_PORT+"/");
			URL userURL = new URL(baseURL, userName+"/"+pictureName);
			
			//this.publishProgress(userURL.toExternalForm());
			
			URLConnection conn = userURL.openConnection();
			conn.setDoInput(true);
			
			// Get
			ObjectInputStream ois = new ObjectInputStream(conn.getInputStream());
			Object o = ois.readObject();
			ois.close(); // close the stream
			
			ArrayList<Shape> shapes = (o != null) ? (ArrayList<Shape>) o : new ArrayList<Shape>();
			
			toolbox.getPicture().erase();
			toolbox.getPicture().getShapes().addAll(shapes);
			
			// Response
		    String response;
		    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		    while ((response = reader.readLine()) != null) {
		      System.out.println(response);
		    }
		    reader.close();
			
		} catch (MalformedURLException e) {
			// new URL() failed
			e.printStackTrace();
		} catch (IOException e) {
			// openConnection() failed
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} 
		
		return params[0];
	}
	
	@Override
	protected void onProgressUpdate(String... params) {
		super.onProgressUpdate(params);
		// invoked every time publishProgress() is called.
		Toast.makeText(context, params[0], Toast.LENGTH_LONG).show();
	}
	
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		// is invoked after the execution 		
		toolbox.getDrawingView().invalidate();
		Toast.makeText(context, "Picture Loaded!", Toast.LENGTH_LONG).show();
	}

	@Override
	protected void onCancelled() {
		Toast.makeText(context, "Server Save Cancelled!", Toast.LENGTH_LONG).show();
	}
}
