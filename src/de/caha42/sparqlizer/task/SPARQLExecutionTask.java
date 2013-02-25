package de.caha42.sparqlizer.task;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;

public class SPARQLExecutionTask extends AsyncTask<String, Integer, ResultSet> {

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
	protected ResultSet doInBackground(String... params) {
		String sparqlEndpointUri = params[0];
		String defaultGraph = params[1];
		String queryString = params[2];

		// Create a Query instance
		Query query = QueryFactory.create(queryString, Syntax.syntaxARQ);

		// Limit the number of results returned
		// Setting the limit is optional - default is unlimited
		//		query.setLimit(10);

		// Set the starting record for results returned
		// Setting the limit is optional - default is 1 (and it is 1-based)
		//		query.setOffset(11);

		// This query uses an external SPARQL endpoint for processing
		// This is the syntax for that type of query
		QueryExecution qe;
		if (defaultGraph == null 
				|| defaultGraph.trim().length() == 0 
				|| defaultGraph.equals("ALL")) {
			qe = QueryExecutionFactory.sparqlService(sparqlEndpointUri, query);
		} else {
			qe = QueryExecutionFactory.sparqlService(sparqlEndpointUri, query, defaultGraph);
		}
		// Execute the query and obtain results
		ResultSet resultSet = qe.execSelect();

		// Important - free up resources used running the query
		qe.close();

		return resultSet;
	}


	@Override
	protected void onPostExecute(ResultSet resultSet) {
		if (dialog.isShowing()) {
			dialog.dismiss();
		}
	}

}
