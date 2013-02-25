package de.caha42.sparqlizer;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.TextView;

public class ResultActivity extends Activity {

	private TextView sparqlResult;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);
		
		Intent intent = getIntent();
		String message = intent.getStringExtra(QueryActivity.SPARQL_RESULT);

		sparqlResult = (TextView) findViewById(R.id.result);
		sparqlResult.setText(message);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_result, menu);
		return true;
	}

}
