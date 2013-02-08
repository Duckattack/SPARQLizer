package de.caha42.sparqlizer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hp.hpl.jena.query.ResultSet;

import de.caha42.sparqlizer.task.SPARQLExecutionTask;

public class QueryActivity extends Activity {

	public final static String SPARQL_RESULT = "de.caha42.sparqlizer.SPARQLRESULT";
	
	private EditText endpointUri;
	private EditText sparqlQuery;
	private View sendSparqlQuery;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_query);

		endpointUri = (EditText) findViewById(R.id.endpoint);
		endpointUri.setText("http://ops-virtuoso.scai.fraunhofer.de:8893/sparql");
		sparqlQuery = (EditText) findViewById(R.id.sparql_query);
		sparqlQuery.setText("SELECT * WHERE {?s ?p ?o .} LIMIT 5");

		sendSparqlQuery = (Button) findViewById(R.id.sparql_query_send);
		sendSparqlQuery.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				runSparqlQuery(endpointUri.getText().toString(), sparqlQuery.getText().toString());
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_query, menu);
		return true;
	}

	/**
	 * Execute SPARQL query
	 * @param endpoint 
	 * @param queryString
	 * @return
	 * @throws Exception 
	 */
	private void runSparqlQuery(String endpoint, String queryString) {
		SPARQLExecutionTask sparqlExecutionTask = new SPARQLExecutionTask(this);
		sparqlExecutionTask.execute(endpoint, queryString);

		String response = null;
		try {
			response = sparqlExecutionTask.get();
		} catch (Exception e) {
			Toast.makeText(this, "An error occured: " + e.getMessage(), Toast.LENGTH_LONG).show();
		}
		
		Intent intent = new Intent(this, ResultActivity.class);
		intent.putExtra(SPARQL_RESULT, response);
		startActivity(intent);
	}
}
