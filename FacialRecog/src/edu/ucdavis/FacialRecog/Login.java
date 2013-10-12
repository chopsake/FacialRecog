package edu.ucdavis.FacialRecog;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity implements OnClickListener {

    private static final String TAG = "Login****";

    private Button buttonLogin, buttonSetup;
    private EditText usernameEditText, passwordEditText;
    private String username, password;
    private Intent i;
    private PostData data = new PostData();

    // Post Variables
    private String URL = Info.ipAddress + "/face/login.php";

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.login);

	buttonLogin = (Button) findViewById(R.id.login_button);
	buttonLogin.setOnClickListener(this);
	buttonSetup = (Button) findViewById(R.id.setup_button);
	buttonSetup.setOnClickListener(this);

	usernameEditText = (EditText) findViewById(R.id.txt_username);
	passwordEditText = (EditText) findViewById(R.id.txt_password);
    }

    @Override
    public void onClick(View v) {
	switch (v.getId()) {
	case R.id.login_button: {
	    Log.d(TAG, "Begin Login");
	    username = usernameEditText.getText().toString();
	    password = passwordEditText.getText().toString();
	    postData();
	}
	    break;
	case R.id.setup_button: {
	    i = new Intent(this, NewUser.class);
	    startActivity(i);
	}
	    break;
	}
    }

    public void postData() {
	// So you can tell it's doing something
	ProgressDialog dialog = ProgressDialog.show(this, "", "Signing In...",
		true);

	// Add your data
	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	nameValuePairs.add(new BasicNameValuePair("key", Info.serverkey));
	nameValuePairs.add(new BasicNameValuePair("name", username.toString()));
	nameValuePairs.add(new BasicNameValuePair("password", password
		.toString()));

	// Get the post data to fill the friends array
	JSONObject result = data.post(nameValuePairs, URL);

	try {
	    // Create the array of names
	    JSONArray res = result.getJSONArray("output");

	    if (res.getString(0).equals("0")) {
		Toast.makeText(Login.this, "Error, Try Again!",
			Toast.LENGTH_SHORT).show();
		dialog.dismiss();
	    } else {
		Info.name = username.toString();
		Info.uid = res.getString(1); // set uid
		Info.status = res.getString(2); // set status
		Log.d(TAG, "Login: " + Info.name.toString());
		Toast.makeText(Login.this, "Success!", Toast.LENGTH_SHORT)
			.show();
		i = new Intent(this, FacialRecog.class);
		startActivityForResult(i, 0);
		dialog.dismiss();
	    }
	} catch (JSONException ex) {
	}
    } // end postData
}
