package de.caha42.sparqlizer.utils;

import java.util.List;
import java.util.Vector;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

public class SPARQLUtils {

	public static String formatResultSetAsString(ResultSet resultSet) {
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
		return response.toString();
	}

	public static String[] formatResultSetAsStringArray(ResultSet resultSet) {
		// Get the column names (the aliases supplied in the SELECT clause)
		String columnName = resultSet.getResultVars().get(0);

		// Setup a place to house results for output
		Vector<String> v = new Vector<String>();

		// Iterate through all resulting rows
		while (resultSet.hasNext()) {
			// Get the next result row
			RDFNode solution = resultSet.next().get(columnName);

			// Data value will be null if optional and not present
			if (solution == null) {
				// Test whether the returned value is a literal value
			} else if (solution.isLiteral()) {
				v.add(((Literal) solution).toString());
				// Otherwise the returned value is a URI
			} else {
				v.add(((Resource) solution).getURI());
			}
		}
		
		return v.toArray(new String[v.size()]);
	}

}
