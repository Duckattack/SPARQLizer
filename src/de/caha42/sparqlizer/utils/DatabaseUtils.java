package de.caha42.sparqlizer.utils;

import java.util.Vector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseUtils extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "SPARQLizerStorage";
    private static final String SPARQL_TABLE_NAME = "sparql";
    private static final String SPARQL_COLUMN_URI = "endpointURI";
    private static final String SPARQL_COLUMN_GRAPH = "graph";
    private static final String SPARQL_COLUMN_QUERY = "query";
    private static final String SPARQL_COLUMN_QUERY_NAME = "query_name";
    private static final String SPARQL_COLUMN_QUERY_DESCRIPTION = "query_description";
    
    private static final String DICTIONARY_TABLE_CREATE =
    		"CREATE TABLE " + SPARQL_TABLE_NAME + " (" +
    				"id INTEGER PRIMARY KEY AUTOINCREMENT, " +
    				SPARQL_COLUMN_URI + " TEXT NOT NULL, " +
    				SPARQL_COLUMN_GRAPH + " TEXT, " +
    				SPARQL_COLUMN_QUERY_NAME + " TEXT NOT NULL, " +
    				SPARQL_COLUMN_QUERY_DESCRIPTION + " TEXT, " +
    				SPARQL_COLUMN_QUERY + " TEXT NOT NULL);";

    public DatabaseUtils(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DICTIONARY_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	/**
	 * Store SPARQL query
	 * @param endpoint
	 * @param graph
	 * @param query
	 * @param name
	 * @param description
	 */
	public void storeSPARQLQuery(String endpoint, String graph, String query, String name, String description) {
		ContentValues newCon = new ContentValues();
		newCon.put(SPARQL_COLUMN_URI, endpoint);
		newCon.put(SPARQL_COLUMN_GRAPH, graph);
		newCon.put(SPARQL_COLUMN_QUERY, query);
		newCon.put(SPARQL_COLUMN_QUERY_NAME, name);
		newCon.put(SPARQL_COLUMN_QUERY_DESCRIPTION, description);
		SQLiteDatabase db = getWritableDatabase();
		db.insert(SPARQL_TABLE_NAME, null, newCon);
		db.close();
	}
	
	/**
	 * Load all endpoints
	 * @return
	 */
	public Cursor loadAllEndpoints() {
		SQLiteDatabase db = getReadableDatabase();
		return db.query(true, 
				SPARQL_TABLE_NAME, 
				new String[] {SPARQL_COLUMN_URI}, 
				null, null, null, null, null, SPARQL_COLUMN_URI);
	}
	
	public static String[] formatCursorAsStringArray(Cursor cursor) {
		if (cursor.getColumnCount() != 1) {
			return new String[0];
		}
		Vector<String> v = new Vector<String>();
		cursor.moveToFirst();
		do {
			v.add(cursor.getString(0));
		} while (cursor.moveToNext());
		return (String[]) v.toArray();
	}
	
}
