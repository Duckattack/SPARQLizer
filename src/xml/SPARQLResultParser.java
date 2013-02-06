package xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class SPARQLResultParser {
	// We don't use namespaces
	private static final String ns = null;

	public List parse(InputStream in) throws XmlPullParserException, IOException {
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();
			return readFeed(parser);
		} finally {
			in.close();
		}
	}

	private List readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
		List entries = new ArrayList();

		parser.require(XmlPullParser.START_TAG, ns, "sparql");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			// Starts by looking for the entry tag
			if (name.equals("entry")) {
				entries.add(readEntry(parser));
			} else {
				skip(parser);
			}
		}  
		return entries;
	}

	private HashMap<String, String> readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, "result");
		
		HashMap<String, String> map = new HashMap<String, String>();
		String key = "";
		String value = "";
		
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals("binding")) {
				key = readBinding(parser);
			} else if (name.equals("uri")) {
				value = readUri(parser);
				map.put(key, value);
			} else {
				skip(parser);
			}
		}
		return map;
	}

	private String readBinding(XmlPullParser parser) throws IOException, XmlPullParserException {
	    parser.require(XmlPullParser.START_TAG, ns, "binding");
	    String binding = parser.getAttributeValue(null, "name");  
	    parser.require(XmlPullParser.END_TAG, ns, "binding");
	    return binding;
	}
	
	private String readUri(XmlPullParser parser) throws IOException, XmlPullParserException {
	    parser.require(XmlPullParser.START_TAG, ns, "uri");
	    String uri = readText(parser);
	    parser.require(XmlPullParser.END_TAG, ns, "uri");
	    return uri;
	}

	private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
	    String result = "";
	    if (parser.next() == XmlPullParser.TEXT) {
	        result = parser.getText();
	        parser.nextTag();
	    }
	    return result;
	}

	private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
	    if (parser.getEventType() != XmlPullParser.START_TAG) {
	        throw new IllegalStateException();
	    }
	    int depth = 1;
	    while (depth != 0) {
	        switch (parser.next()) {
	        case XmlPullParser.END_TAG:
	            depth--;
	            break;
	        case XmlPullParser.START_TAG:
	            depth++;
	            break;
	        }
	    }
	 }
}
