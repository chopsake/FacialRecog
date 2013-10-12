package edu.ucdavis.FacialRecog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class PostData {
    private static final int HTTP_STATUS_OK = 200;

    public JSONObject post(List<NameValuePair> values, String postURL) {
	// Create a new HttpClient and Post Header
	HttpClient httpclient = new DefaultHttpClient();
	HttpPost httppost = new HttpPost(postURL);

	try {
	    httppost.setEntity(new UrlEncodedFormEntity(values));

	    // Execute HTTP Post Request
	    HttpResponse response = httpclient.execute(httppost);

	    // Check for a good connect and whether is has been established
	    if (response.getStatusLine().getStatusCode() == HTTP_STATUS_OK) {
		HttpEntity entity = response.getEntity();

		if (entity != null) {
		    InputStream instream = entity.getContent();

		    // Load the page converted to a string into a JSONObject
		    JSONObject myAwway = new JSONObject(
			    convertStreamToString(instream));

		    instream.close();

		    return myAwway;

		} // end if
	    } // end if
	} catch (ClientProtocolException e) {
	    // TODO Auto-generated catch block
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	} catch (JSONException ex) {
	}
	JSONObject ret = new JSONObject();
	return ret;
    }

    public JSONObject post(MultipartEntity entity, String postURL) {
	// Create a new HttpClient and Post Header
	HttpClient httpclient = new DefaultHttpClient();
	HttpPost httppost = new HttpPost(postURL);
	Log.d("$$$$$$$$$$$$$$$$$$$$", "made all the http stuff");

	try {
	    httppost.setEntity(entity);
	    Log.d("$$$$$$$$$$$$$$$$$$$$", "set entity");
	    // Execute HTTP Post Request
	    HttpResponse response = httpclient.execute(httppost);

	    Log.d("$$$$$$$$$$$$$$$$$$$$", "" + response.getStatusLine());
	    // Check for a good connect and whether is has been established
	    if (response.getStatusLine().getStatusCode() == HTTP_STATUS_OK) {
		HttpEntity responseEntity = response.getEntity();
		if (responseEntity != null) {
		    InputStream instream = responseEntity.getContent();
		    Log.d("$$$$$$$$$$$$$$$$$$$$", "response entity is not null");
		    // Load the page converted to a string into a JSONObject
		    JSONObject jsonObj = new JSONObject(
			    convertStreamToString(instream));
		    instream.close();
		    return jsonObj;
		} // end if
	    } // end if
	} catch (ClientProtocolException e) {
	    // TODO Auto-generated catch block
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	} catch (JSONException ex) {
	}
	return new JSONObject();
    }

    private static String convertStreamToString(InputStream is) {
	/*
	 * To convert the InputStream to String we use the
	 * BufferedReader.readLine() method. We iterate until the BufferedReader
	 * return null which means there's no more data to read. Each line will
	 * appended to a StringBuilder and returned as String.
	 */
	BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	StringBuilder sb = new StringBuilder();

	String line = null;
	try {
	    while ((line = reader.readLine()) != null) {
		sb.append(line + "\n");
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	} finally {
	    try {
		is.close();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
	return sb.toString();
    }
}
