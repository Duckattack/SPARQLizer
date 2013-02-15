package de.caha42.sparqlizer;

import com.hp.hpl.jena.query.ResultSet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import de.caha42.sparqlizer.task.SPARQLExecutionTask;
import de.caha42.sparqlizer.utils.SPARQLUtils;

public class QueryActivity extends Activity {

	public final static String SPARQL_RESULT = "de.caha42.sparqlizer.SPARQLRESULT";
	
	private EditText endpointUri;
	private AutoCompleteTextView defaultGraph;
	private MultiAutoCompleteTextView sparqlQuery;
	
	private Button sendGraphQuery;
	private Button sendPredicateQuery;
	private Button sendSparqlQuery;

	private String[] graphs = new String[0];
	private String[] predicates = new String[0];
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_query);

		// Textfields
		endpointUri = (EditText) findViewById(R.id.endpoint);
		endpointUri.setText("http://ops-virtuoso.scai.fraunhofer.de:8892/sparql");
		
		defaultGraph = (AutoCompleteTextView) findViewById(R.id.graph);
		
		sparqlQuery = (MultiAutoCompleteTextView) findViewById(R.id.sparql_query);
		sparqlQuery.setText("SELECT * WHERE {?s ?p ?o .} LIMIT 5");

		// Buttons		
		sendGraphQuery = (Button) findViewById(R.id.graph_query_send);
		sendGraphQuery.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				ResultSet resultSet = runSparqlQuery(endpointUri.getText().toString(), "SELECT DISTINCT ?g WHERE { GRAPH ?g { ?s ?p ?o } }");
				graphs = SPARQLUtils.formatResultSetAsStringArray(resultSet);
				defaultGraph.setAdapter(new ArrayAdapter<String>(v.getContext(),
		                android.R.layout.simple_dropdown_item_1line, graphs));
			}
		});
		
		sendPredicateQuery = (Button) findViewById(R.id.predicate_query_send);
		sendPredicateQuery.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				ResultSet resultSet = runSparqlQuery(endpointUri.getText().toString(), "SELECT DISTINCT ?p WHERE {?s ?p ?o}");
				predicates = SPARQLUtils.formatResultSetAsStringArray(resultSet);
				sparqlQuery.setAdapter(new ArrayAdapter<String>(v.getContext(),
		                 android.R.layout.simple_dropdown_item_1line, predicates));
				sparqlQuery.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
			}
		});
		

		sendSparqlQuery = (Button) findViewById(R.id.sparql_query_send);
		sendSparqlQuery.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				ResultSet resultSet = runSparqlQuery(endpointUri.getText().toString(), defaultGraph.getText().toString(), sparqlQuery.getText().toString());
				String response = SPARQLUtils.formatResultSetAsString(resultSet);
				Intent intent = new Intent(v.getContext(), ResultActivity.class);
				intent.putExtra(SPARQL_RESULT, response);
				startActivity(intent);
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
	private ResultSet runSparqlQuery(String endpoint, String queryString) {
		return runSparqlQuery(endpoint, "", queryString);
	}
	
	/**
	 * Execute SPARQL query
	 * @param endpoint 
	 * @param defaultGraph 
	 * @param queryString
	 * @return
	 * @throws Exception 
	 */
	private ResultSet runSparqlQuery(String endpoint, String defaultGraph, String queryString) {
		SPARQLExecutionTask sparqlExecutionTask = new SPARQLExecutionTask(this);
		sparqlExecutionTask.execute(endpoint, defaultGraph, queryString);

		ResultSet response = null;
		try {
			response = sparqlExecutionTask.get();
		} catch (Exception e) {
			Toast.makeText(this, "An error occured: " + e.getMessage(), Toast.LENGTH_LONG).show();
		}
		
		return response;
	}
	
}
