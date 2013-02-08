package de.caha42.sparqlizer.task;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;

public class SPARQLExecutionTask extends AsyncTask<String, Integer, String> {

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
	protected String doInBackground(String... params) {
		String sparqlEndpointUri = params[0];
		String queryString = params[1];

		// Create a Query instance
		Query query = QueryFactory.create(queryString, Syntax.syntaxARQ);

		// Limit the number of results returned
		// Setting the limit is optional - default is unlimited
		query.setLimit(10);

		// Set the starting record for results returned
		// Setting the limit is optional - default is 1 (and it is 1-based)
		query.setOffset(11);

		// This query uses an external SPARQL endpoint for processing
		// This is the syntax for that type of query
		QueryExecution qe = QueryExecutionFactory.sparqlService(sparqlEndpointUri, query);

		// Execute the query and obtain results
		ResultSet resultSet = qe.execSelect();

		// Get the column names (the aliases supplied in the SELECT clause)
		List<String> columnNames = resultSet.getResultVars();

		// Setup a place to house results for output
		StringBuffer response = new StringBuffer();

		// Iterate through all resulting rows
		while (resultSet.hasNext()) {
			// Get the next result row
			QuerySolution solution = resultSet.next();

			for (String columnName : columnNames) {
				// Data value will be null if optional and not present
				if (solution.get(columnName) == null) {
					response.append("{null}");
					// Test whether the returned value is a literal value
				} else if (solution.get(columnName).isLiteral()) {
					response.append(solution.getLiteral(columnName).toString());
					// Otherwise the returned value is a URI
				} else {
					response.append(solution.getResource(columnName).getURI());
				}
				response.append('\n');
			}
			response.append("-----------------\n");
		}
		// Important - free up resources used running the query
		qe.close();

		return response.toString();
	}

	@Override
	protected void onPostExecute(String response) {
		if (dialog.isShowing()) {
			dialog.dismiss();
		}
	}

}
