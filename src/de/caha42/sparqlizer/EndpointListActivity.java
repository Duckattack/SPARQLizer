package de.caha42.sparqlizer;

import de.caha42.sparqlizer.utils.DatabaseUtils;
import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.ListAdapter;

public class EndpointListActivity extends ListActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_endpointlist);

        Cursor cursor = DatabaseUtils.getInstance(this).loadAllEndpoints();

        ListAdapter adapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_1,
                cursor,
                new String[] {DatabaseUtils.SPARQL_COLUMN_URI},
                new int[] {android.R.id.text1}); 

        // Bind to our new adapter.
        setListAdapter(adapter);
	}
}
