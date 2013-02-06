package de.caha42.sparqlizer.task;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

public class SPARQLExecutionTask extends AsyncTask<String, Integer, InputStream> {

	private Activity context;
	private ProgressDialog dialog;
	
	public SPARQLExecutionTask(Activity activity) {
		context = activity;
		dialog = new ProgressDialog(context);
	}
	
	@Override
	protected void onPreExecute() {
		dialog.setMessage("Please wait...");
		dialog.show();
	} 

	@Override
	protected InputStream doInBackground(String... params) {
		InputStream response = null;
		try {
			// Construct data
			String data = URLEncoder.encode("query", "UTF-8") + "=" +
					URLEncoder.encode(params[1], "UTF-8");

			// Send data
			URL url = new URL(params[0]);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
//			conn.setRequestProperty("query", params[1]);		

			DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
			wr.write(data.getBytes());
			wr.flush();
			wr.close();
			
			// Get the response
			response = conn.getInputStream();		
		} catch (Exception e) {
			Toast.makeText(context, "An error occured: " + e.getMessage(), Toast.LENGTH_LONG).show();
		}
		return response;
	}

	@Override
	protected void onPostExecute(InputStream response) {
		if (dialog.isShowing()) {
            dialog.dismiss();
        }
	}

}
