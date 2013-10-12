package edu.ucdavis.FacialRecog;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ParseException;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class Friends extends ListActivity {
    private CustomAdapter adapter;
    private LayoutInflater mInflater;
    private Vector<RowData> data;
    RowData rd;

    private String[] name;
    private String[] info;
    private Integer[] id;
    private Bitmap[] image;

    private static final String TAG = "Friends******";

    // Post Variables
    private static final String URL = Info.ipAddress + "/face/friends.php";
    private static final String REMOVE_URL = Info.ipAddress
	    + "/face/removefriend.php";
    private PostData postData = new PostData();

    /*
     * getImage() passes a url and returns the corresponding image. This image
     * is converted to a bitmap and resized before being put into the friends
     * list.
     */
    public void getImage(int index, int id) {
	URL picUrl = null;
	Bitmap picBitmap = null;
	try {
	    picUrl = new URL(Info.ipAddress + "/face/profile_pics/" + id
		    + "/profile.jpg");
	    picBitmap = BitmapFactory.decodeStream(picUrl.openConnection()
		    .getInputStream());
	} catch (MalformedURLException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}

	image[index] = picBitmap;
    }

    public boolean post() {
	Log.d(TAG, "starting post");

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
		Log.d(TAG, "You have no friends!");
		return false;
	    } else {
		Log.d(TAG, "Starting friends loop");

		name = new String[count];
		info = new String[count];
		id = new Integer[count];
		image = new Bitmap[count];

		int counter = 1;
		for (int i = 0; i < count; i++) {
		    id[i] = res.getInt(counter++);
		    name[i] = res.getString(counter++);
		    info[i] = res.getString(counter++);

		    getImage(i, id[i]);

		    Log.d(TAG, "id: " + id[i]);
		    Log.d(TAG, "name: " + name[i]);
		    Log.d(TAG, "info: " + info[i]);
		}
	    }

	} catch (JSONException ex) {
	    Log.d(TAG, "Exception caught");
	}
	return true;
    }

    /*
     * getFriendData() needs to retrieve the friend data from the server. Each
     * array will be filled with the correct information (index 0 corresponds to
     * the first friend's name, status update, and mysql id. The mysql id will
     * be used in the formulation of the url to grab the user's profile pic.
     */
    void getFriendData() {
	name = new String[] { "Alice", "Bob", "Charlie", "Dave" };
	info = new String[] { "omg so drunk", "midterms today, fml", "WOOOOO!",
		"updateupdateupdate" };
	id = new Integer[] { 0, 1, 2, 3 };

	image = new Bitmap[name.length];
	for (int i = 0; i < name.length; i++) {
	    getImage(i, id[i]);
	}
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.friends);

	// getFriendData();
	if (post()) { // have friends
	    mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	    data = new Vector<RowData>();
	    for (int i = 0; i < name.length; i++) {
		try {
		    rd = new RowData(i, name[i], info[i]);
		} catch (ParseException e) {
		    e.printStackTrace();
		}
		data.add(rd);
	    }
	    adapter = new CustomAdapter(this, R.id.list, R.id.name, data);
	    setListAdapter(adapter);
	    getListView().setTextFilterEnabled(true);
	    registerForContextMenu(getListView()); // hold down menu
	} // end if post
    } // end onCreate

    // menu items
    private static final int REMOVE_ID = Menu.FIRST;
    private static final int CANCEL_ID = Menu.FIRST + 1;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
	    ContextMenuInfo menuInfo) {
	// When you hold down on an item
	super.onCreateContextMenu(menu, v, menuInfo);
	menu.setHeaderTitle("Remove Friend?");
	menu.add(0, REMOVE_ID, 0, "Yes");
	menu.add(0, CANCEL_ID, 0, "No");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
	switch (item.getItemId()) {
	case REMOVE_ID:
	    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
		    .getMenuInfo();
	    removeFriend(info.position);
	    return true;
	}
	return super.onContextItemSelected(item);
    }

    private void wallPost(String msg, String id) {

	Log.d(TAG, "starting wall post");
	String localURL = Info.ipAddress + "/face/sendmessage.php";
	PostData postData = new PostData();
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	Date date = new Date();
	// Add your data
	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	nameValuePairs.add(new BasicNameValuePair("key", Info.serverkey));
	nameValuePairs.add(new BasicNameValuePair("uid", Info.uid));
	nameValuePairs.add(new BasicNameValuePair("id", id));
	nameValuePairs.add(new BasicNameValuePair("msg", msg));
	nameValuePairs.add(new BasicNameValuePair("date", dateFormat.format(
		date).toString()));

	// Get the post data to fill the friends array
	JSONObject result = postData.post(nameValuePairs, localURL);
	Log.d(TAG, result.toString());

    }

    /*
     * Specifies the action to be performed on a click of a list row. This
     * should be modified to open up a new window (create a new intent for a new
     * activity) and show that person's profile.
     */
    @Override
    public void onListItemClick(ListView parent, View v, int position,
	    long clickid) {
	// Toast.makeText(getApplicationContext(),
	// "You have selected "+name[position], Toast.LENGTH_SHORT).show();

	super.onListItemClick(parent, v, position, clickid);
	final String clickedId = Integer.toString(id[position]);
	AlertDialog.Builder alert = new AlertDialog.Builder(this);
	alert.setTitle("Wall Post");
	alert.setMessage("Enter a message:");
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
		String msg = message.getText().toString();
		if (!msg.isEmpty())
		    wallPost(msg, clickedId);
		else {
		    Toast.makeText(Friends.this, "Can't send blank message",
			    Toast.LENGTH_SHORT).show();
		}
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

    private class RowData {
	protected int mId;
	protected String mName;
	protected String mStatus;

	RowData(int id, String name, String status) {
	    mId = id;
	    mName = name;
	    mStatus = status;
	}

	@Override
	public String toString() {
	    return mId + " " + mName + " " + mStatus;
	}
    }

    private class CustomAdapter extends ArrayAdapter<RowData> {
	public CustomAdapter(Context context, int resource,
		int textViewResourceId, List<RowData> objects) {
	    super(context, resource, textViewResourceId, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	    ViewHolder holder = null;
	    TextView name = null;
	    TextView status = null;
	    ImageView i11 = null;
	    RowData rowData = getItem(position);
	    if (null == convertView) {
		convertView = mInflater.inflate(R.layout.list, null);
		holder = new ViewHolder(convertView);
		convertView.setTag(holder);
	    }
	    holder = (ViewHolder) convertView.getTag();
	    name = holder.gettitle();
	    name.setText(rowData.mName);
	    status = holder.getdetail();
	    status.setText(rowData.mStatus);
	    i11 = holder.getImage();
	    // i11.setImageResource(imgid[rowData.mId]);
	    i11.setImageBitmap(image[rowData.mId]);
	    i11.getLayoutParams().width = 200;
	    i11.getLayoutParams().height = 200;

	    // i11.setMaxWidth(65);
	    return convertView;
	}

	private class ViewHolder {
	    private View mRow;
	    private TextView name = null;
	    private TextView status = null;
	    private ImageView i11 = null;

	    public ViewHolder(View row) {
		mRow = row;
	    }

	    public TextView gettitle() {
		if (null == name) {
		    name = (TextView) mRow.findViewById(R.id.name);
		}
		return name;
	    }

	    public TextView getdetail() {
		if (null == status) {
		    status = (TextView) mRow.findViewById(R.id.status);
		}
		return status;
	    }

	    public ImageView getImage() {
		if (null == i11) {
		    i11 = (ImageView) mRow.findViewById(R.id.img);
		}
		return i11;
	    }
	}
    }

    private void removeFriend(int position) {
	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	nameValuePairs.add(new BasicNameValuePair("key", Info.serverkey));
	nameValuePairs.add(new BasicNameValuePair("uid", Info.uid));
	nameValuePairs.add(new BasicNameValuePair("friendid", id[position]
		.toString()));

	String delname = name[position];
	PostData pd = new PostData();
	pd.post(nameValuePairs, REMOVE_URL);

	adapter.clear();
	post();
	if (post()) { // have friends
	    mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	    data = new Vector<RowData>();
	    for (int i = 0; i < name.length; i++) {
		try {
		    rd = new RowData(i, name[i], info[i]);
		} catch (ParseException e) {
		    e.printStackTrace();
		}
		data.add(rd);
	    }
	    adapter = new CustomAdapter(this, R.id.list, R.id.name, data);
	    setListAdapter(adapter);
	    getListView().setTextFilterEnabled(true);
	}

	Log.d(TAG, "RemoveFriend: " + Info.name + " removed " + delname);
	Toast.makeText(Friends.this, "Removed " + delname, Toast.LENGTH_SHORT)
		.show();
    }
}
