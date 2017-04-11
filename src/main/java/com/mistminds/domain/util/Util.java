package com.mistminds.domain.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Util {
	
	private final Logger log = LoggerFactory.getLogger(Util.class);
	@SuppressWarnings("unused")
	private static final Util util = new Util();
	
	public static Boolean checkOffensive(String content) {
		StringBuffer builder, sb = null;
		String array = content.replaceAll(" ", "%20");
		try {
			builder = new StringBuffer("http://www.wdylike.appspot.com/?q="
					+ array);
			URL url = new URL(builder.toString());
			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();
			urlConnection.connect();
			InputStream document = (InputStream) urlConnection.getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					document));
			sb = new StringBuffer();
			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			/*JSONObject obj = new JSONObject(sb.toString());
			return obj.getString("response");*/
			return Boolean.valueOf(sb.toString());
		} catch (Exception e) {
			sb.append(e);
			return false;
		}

	}
	
   }
