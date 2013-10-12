package edu.ucdavis.FacialRecog;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class Profile extends ListActivity {
    private final String URL = Info.ipAddress + "/face/profile.php";
    protected String username = Info.name;

    private final String PIC_URL = Info.ipAddress + "/face/profile_pics/"
	    + Info.uid + "/profile.jpg";

    private final String FRIEND_NAME = "FRIEND_NAME";
    private final String WALL_MESSAGE = "WALL_MESSAGE";
    private final String MESSAGE_DATE = "MESSAGE_DATE";
    private MatrixCursor cursor = new MatrixCursor(new String[] { "_id",
	    FRIEND_NAME, WALL_MESSAGE, MESSAGE_DATE });

    private static final String TAG = "Profile******";
    private TextView nameText;
    private TextView statusText;
    private ImageView img;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.profile);

	nameText = (TextView) findViewById(R.id.profileInfo);
	nameText.setText(Info.name + "'s Profile");
	statusText = (TextView) findViewById(R.id.profileStatus);
	statusText.setText(Info.status);
	img = (ImageView) findViewById(R.id.profileImage);
	img.setImageBitmap(getProfilePic());

	// loadCursor();

	performPost();

	startManagingCursor(cursor);
	setListAdapter(new SimpleCursorAdapter(this,
		R.layout.profile_list_item, cursor, new String[] { FRIEND_NAME,
			WALL_MESSAGE, MESSAGE_DATE }, new int[] {
			R.id.wallPostUsername, R.id.wallPostWhatTheySaid,
			R.id.wallPostWhenTheySaidIt }));

	// performPost();

	// query for user photo
	// ((ImageView) findViewById(R.id.profileImage))
	// .setImageDrawable(getResources().getDrawable(R.drawable.icon));

	// query for user information
	// ((TextView) findViewById(R.id.profileInfo))
	// .setText("SOME PROFILE INFORMATIOn");

	// query for wall stuff
	// parse and insert into matrixCursor

	// startManagingCursor(cursor);
	/*-		setListAdapter(new SimpleCursorAdapter(this,
	 R.layout.profile_list_item, cursor, new String[] {
	 "username", "wallPost" }, new int[] {
	 R.id.wallPostUsername, R.id.wallPostWhatTheySaid }));*/
    }

    /*
     * @Override protected void onListItemClick(ListView l, View v, int
     * position, long id) { super.onListItemClick(l, v, position, id); final int
     * localPosition = position; AlertDialog.Builder alert = new
     * AlertDialog.Builder(this); alert.setTitle("Wall Post");
     * alert.setMessage("Enter a message:"); final EditText message = new
     * EditText(this); message .setFilters(new InputFilter[] { new
     * InputFilter.LengthFilter( 140) }); message.setOnKeyListener(new
     * View.OnKeyListener() {
     * 
     * @Override public boolean onKey(View v, int keyCode, KeyEvent event) {
     * if(keyCode == KeyEvent.KEYCODE_ENTER) { wallPost(localPosition,
     * message.getText().toString()); } return false; } });
     * 
     * alert.setView(message); alert.setPositiveButton("OK", new
     * DialogInterface.OnClickListener() {
     * 
     * @Override public void onClick(DialogInterface dialog, int which) {
     * wallPost(localPosition, message.getText().toString()); } });
     * alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
     * 
     * @Override public void onClick(DialogInterface dialog, int which) { } });
     * 
     * alert.show(); }
     */
    private Bitmap getProfilePic() {
	URL picUrl = null;
	Bitmap picBitmap = null;
	try {
	    picUrl = new URL(PIC_URL);
	    picBitmap = BitmapFactory.decodeStream(picUrl.openConnection()
		    .getInputStream());
	} catch (MalformedURLException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}

	return picBitmap;
    }

    private void performPost() {

	Log.d(TAG, "starting post");
	PostData postData = new PostData();

	// Add your data
	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	nameValuePairs.add(new BasicNameValuePair("key", Info.serverkey));
	nameValuePairs.add(new BasicNameValuePair("uid", Info.uid));

	// Get the post data to fill the friends array
	JSONObject result = postData.post(nameValuePairs, URL);
	Log.d(TAG, result.toString());

	try {
	    // Create the array of names
	    JSONArray res = result.getJSONArray("output");
	    int count = res.getInt(0);

	    if (count == 0) {
		Log.d(TAG, "You have no posts!");
		// dialog.dismiss();
	    } else {

		Log.d(TAG, "Starting wall post loop");

		int counter = 1;
		for (int i = 0; i < count; i++) {
		    String name = res.getString(counter++);
		    String date = res.getString(counter++);
		    String text = res.getString(counter++);

		    cursor.addRow(new Object[] { i, name, text, date });

		}
		// dialog.dismiss();
	    }

	} catch (JSONException ex) {
	    Log.d(TAG, "Exception caught");
	}
    }

    // private void wallPost(int uid, String message) {
    // Toast.makeText(this, uid + " said: " + message, Toast.LENGTH_SHORT)
    // .show();
    // // perform http post
    // }
    //
    // private void loadCursor() {
    // int i = 0;
    // cursor.addRow(new Object[] { i++, "Ken",
    // "This is a great project guys, keep up the amazing work! ;)" });
    // cursor.addRow(new Object[] { i++, "Lori",
    // "Read my emails, all of them! =)" });
    // cursor.addRow(new Object[] { i++, "Larry & Sergey",
    // "Can we buy this social network from you folks?" });
    // cursor.addRow(new Object[] { i++, "Hao",
    // "Still need those 50 push ups." });
    // cursor.addRow(new Object[] { i++, "Mark",
    // "You guys will never defeat us, we will conquer you!" });
    // cursor.addRow(new Object[] { i++, "Gusfield",
    // "Don't forget to #include<nothing.h>" });
    // cursor.addRow(new Object[] { i++, "Wu",
    // "You can DOWN-LO your faaaaavorite datastructure - the i-node!" });
    // cursor.addRow(new Object[] { i++, "Marcel (154b TA)",
    // "My Quartus doesn't work.." });
    // cursor.addRow(new Object[] { i++, "Hao", "50 push ups." });
    // cursor.addRow(new Object[] { i++, "Viet Tung",
    // "That's not a proof, it's a poof!" });
    // }

    private void updateStatusAlert() {

	AlertDialog.Builder alert = new AlertDialog.Builder(this);
	alert.setTitle("Change status");
	alert.setMessage("Enter your new status:");
	final EditText message = new EditText(this);
	message.setFilters(new InputFilter[] { new InputFilter.LengthFilter(140) });
	message.setOnKeyListener(new View.OnKeyListener() {
	    @Override
	    public boolean onKey(View v, int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_ENTER) {
		    // wallPost(localPosition, message.getText().toString());
		}
		return false;
	    }
	});

	alert.setView(message);
	alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	    @Override
	    public void onClick(DialogInterface dialog, int which) {
		Info.status = message.getText().toString();
		postStatus();
		statusText.setText(Info.status);
		Toast.makeText(getApplicationContext(), "Status Updated",
			Toast.LENGTH_SHORT).show();
	    }
	});
	alert.setNegativeButton("Cancel",
		new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		    }
		});

	alert.show();
    }

    private void postStatus() {
	String localURL = Info.ipAddress + "/face/updatestatus.php";
	PostData postData = new PostData();

	// Add your data
	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	nameValuePairs.add(new BasicNameValuePair("key", Info.serverkey));
	nameValuePairs.add(new BasicNameValuePair("uid", Info.uid));
	nameValuePairs.add(new BasicNameValuePair("status", Info.status));

	// Get the post data to fill the friends array
	postData.post(nameValuePairs, localURL);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	MenuInflater inflater = getMenuInflater();
	inflater.inflate(R.menu.profile_menu, menu);
	return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	switch (item.getItemId()) {
	case R.id.change_status: {
	    updateStatusAlert();
	}
	    break;
	}
	return true;
    }
}
