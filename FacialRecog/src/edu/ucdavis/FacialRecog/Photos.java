package edu.ucdavis.FacialRecog;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Photos extends Activity {
    private final String URL = Info.ipAddress + "/face/addfriend.php";

    private static final String TAG = "Photos***";

    private TextView result;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.photos);

	result = (TextView) findViewById(R.id.photosResult);
	result.setText("");

	((Button) findViewById(R.id.TakePhotoButton))
		.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
			v.setEnabled(false);
			takePhotosActivity();
			v.setEnabled(true);
		    }
		});
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
	    Intent intent) {
	super.onActivityResult(requestCode, resultCode, intent);
	if (resultCode == RESULT_OK) {
	    final String friendName = intent.getExtras().getString("HTML");
	    AlertDialog.Builder alert = new AlertDialog.Builder(this);
	    alert.setTitle("New Friend");
	    alert.setMessage("Add " + friendName + "?");
	    alert.setPositiveButton("Add",
		    new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			    addFriend(friendName);
			}
		    });
	    alert.setNegativeButton("Cancel",
		    new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		    });

	    alert.show();
	    // result.setText(intent.getExtras().getString("HTML"));
	} else {
	    result.setText("Can't find this dude!");
	}
    }

    private void takePhotosActivity() {
	startActivityForResult(new Intent(this, TakePhotos.class), 0);
    }

    private void addFriend(String friendName) {
	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	nameValuePairs.add(new BasicNameValuePair("key", Info.serverkey));
	nameValuePairs.add(new BasicNameValuePair("uid", Info.uid));
	nameValuePairs.add(new BasicNameValuePair("friendName", friendName));

	PostData data = new PostData();
	JSONObject result = data.post(nameValuePairs, URL);

	try {
	    JSONArray info = result.getJSONArray("output");
	    if (info.getString(0).equals("0")) // success
	    {
		Log.d(TAG, "AddFriend: " + Info.name + " added " + friendName);
		Toast.makeText(Photos.this, "Added " + friendName,
			Toast.LENGTH_SHORT).show();
	    } else {
		Log.d(TAG, "AddFriend: Cannot add " + friendName + " to "
			+ Info.name);
		Toast.makeText(Photos.this,
			"Error: " + friendName + " Not Added",
			Toast.LENGTH_SHORT).show();
	    }
	} catch (JSONException ex) {
	}
    }
}