package de.caha42.sparqlizer;

import java.util.ArrayList;
import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import com.hp.hpl.jena.query.ResultSet;

import de.caha42.sparqlizer.task.SPARQLExecutionTask;
import de.caha42.sparqlizer.utils.DatabaseUtils;
import de.caha42.sparqlizer.utils.SPARQLUtils;

public class QueryActivity extends Activity {

	public final static String SPARQL_RESULT = "de.caha42.sparqlizer.SPARQLRESULT";

	private EditText endpointUri;
	private Spinner defaultGraph;
	private MultiAutoCompleteTextView sparqlQuery;

	private Button sendGraphQuery;
	private Button sendPredicateQuery;
	private Button sendSparqlQuery;
	private Button storeSparqlQuery;
	private Button loadSparqlQuery;

	private String graph;
	private Vector<String> suggestions = new Vector<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_query);

		// Textfields
		endpointUri = (EditText) findViewById(R.id.endpoint);
		endpointUri.setText("http://ops-virtuoso.scai.fraunhofer.de:8892/sparql");

		defaultGraph = (Spinner) findViewById(R.id.graph);
		defaultGraph.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				graph = (String) parent.getItemAtPosition(pos);
			}
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		sparqlQuery = (MultiAutoCompleteTextView) findViewById(R.id.sparql_query);
		sparqlQuery.setText("SELECT * WHERE {?s ?p ?o .} LIMIT 5");
		sparqlQuery.addTextChangedListener(new SPARQLTextWatcher(this));

		// Buttons		
		sendGraphQuery = (Button) findViewById(R.id.graph_query_send);
		sendGraphQuery.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				ResultSet resultSet = runSparqlQuery(endpointUri.getText().toString(), "SELECT DISTINCT ?g WHERE { GRAPH ?g { ?s ?p ?o } }");
				String[] graphs = SPARQLUtils.formatResultSetAsStringArray(resultSet);
				String[] graphsAndNull = new String[graphs.length+1];
				graphsAndNull[0] = "ALL";
				for (int i = 0; i < graphs.length; i++) {
					graphsAndNull[i+1] = graphs[i];
				}
				ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(v.getContext(),
						android.R.layout.simple_spinner_item, 
						graphsAndNull);
				spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				defaultGraph.setAdapter(spinnerAdapter);
			}
		});

		sendPredicateQuery = (Button) findViewById(R.id.predicate_query_send);
		sendPredicateQuery.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				ResultSet resultSet = runSparqlQuery(endpointUri.getText().toString(), "SELECT DISTINCT ?p WHERE {?s ?p ?o}");
				sparqlQuery.setAdapter(new ArrayAdapter<String>(v.getContext(),
						android.R.layout.simple_dropdown_item_1line, 
						SPARQLUtils.formatResultSetAsStringArray(resultSet)));
				sparqlQuery.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
			}
		});

		sendSparqlQuery = (Button) findViewById(R.id.sparql_query_send);
		sendSparqlQuery.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				ResultSet resultSet = runSparqlQuery(endpointUri.getText().toString(), graph, sparqlQuery.getText().toString());
				String response = SPARQLUtils.formatResultSetAsString(resultSet);
				Intent intent = new Intent(v.getContext(), ResultActivity.class);
				intent.putExtra(SPARQL_RESULT, response);
				startActivity(intent);
			}
		});

		storeSparqlQuery = (Button) findViewById(R.id.sparql_query_save);
		storeSparqlQuery.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				DatabaseUtils.getInstance(v.getContext()).storeSPARQLQuery(endpointUri.getText().toString(), graph, sparqlQuery.getText().toString(), "tmp", "tmp");
			}
		});

		loadSparqlQuery = (Button) findViewById(R.id.sparql_query_load);
		loadSparqlQuery.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), EndpointListActivity.class);
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

	
	private class SPARQLTextWatcher implements TextWatcher {

		private Context context;
		private String remember = "";
		private boolean inWord = false;
		
		public SPARQLTextWatcher(Context context) {
			this.context = context;
		}
		
		@Override
		public void afterTextChanged(Editable s) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// Instead try overwriting tokenizer
			if (inWord && s.charAt(start) == ' ') {
				suggestions.add(remember);
				sparqlQuery.setAdapter(new ArrayAdapter<String>(context, 
						android.R.layout.simple_dropdown_item_1line,
						suggestions.toArray(new String[suggestions.size()])));
				inWord = false;
				remember = "";
			} else if (s.charAt(start) == '?' || inWord) {
				inWord = true;
				remember += s.subSequence(start, start+count);
			} 
		}

	}
}
