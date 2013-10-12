package edu.ucdavis.FacialRecog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NewUser extends Activity implements OnClickListener {
    private EditText name, password;
    private Button submit;
    private PostData data = new PostData();

    private String URL = Info.ipAddress + "/face/register.php";

    private static final String TAG = "NewUser****";

    private Uri image_;
    private File imageFile_;
    private ByteArrayBody[] bab = new ByteArrayBody[3];
    private int imageCount_ = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.new_user);

	name = (EditText) findViewById(R.id.txt_username);
	password = (EditText) findViewById(R.id.txt_password);
	submit = (Button) findViewById(R.id.setup_button);
	submit.setOnClickListener(this);

	name.setEnabled(false);
	password.setEnabled(false);
	submit.setEnabled(false);

	capturePhotoActivity();
    }

    @Override
    public void onClick(View v) {
	register();
    }

    public void register() {
	Log.d(TAG, "Begin register new user");
	// Checks if everything is filled in
	if (name.getText().toString().equals("")
		|| password.getText().toString().equals("")) {
	    Toast.makeText(NewUser.this, "Missing information!",
		    Toast.LENGTH_SHORT).show();
	} else {
	    try {
		MultipartEntity entity = new MultipartEntity(
			HttpMultipartMode.BROWSER_COMPATIBLE);
		entity.addPart("key", new StringBody(Info.serverkey));
		entity.addPart("name",
			new StringBody(name.getText().toString()));
		entity.addPart("password", new StringBody(password.getText()
			.toString()));
		for (int i = 0; i < 3; i++) {
		    entity.addPart("input" + i, bab[i]);
		}

		Log.d(TAG, "Contacting server");

		// Get the post data to fill the friends array
		JSONObject result = data.post(entity, URL);

		// Create the array of names
		JSONArray info = result.getJSONArray("output");

		if (info.getString(0).equals("0")) // Register is a success
		{
		    Log.d(TAG, "Registered: " + name.getText().toString());
		    Toast.makeText(NewUser.this, "Registration Complete",
			    Toast.LENGTH_LONG).show();
		    finish(); // back to login screen
		} else {
		    Toast.makeText(NewUser.this,
			    "Registration Error, Please Try Again!",
			    Toast.LENGTH_SHORT).show();
		}
	    } catch (JSONException ex) {
	    } catch (IOException e) {
	    }
	}
    } // end register()

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
	    Intent intent) {
	super.onActivityResult(requestCode, resultCode, intent);

	// if full image
	if (resultCode == RESULT_OK && intent == null) {
	    Bitmap bm = BitmapFactory.decodeFile(imageFile_.getAbsolutePath());
	    ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    bm.compress(CompressFormat.JPEG, 75, bos);
	    bm.recycle();
	    byte[] data = bos.toByteArray();
	    bab[imageCount_] = new ByteArrayBody(data, "input" + imageCount_
		    + ".jpg");

	    Log.d(TAG, "###########got photo: " + "input" + imageCount_
		    + ".jpg");

	    imageFile_.delete();

	    if (imageCount_ < 2) {
		imageCount_++;
		capturePhotoActivity();
	    } else { // last image
		     // enable views
		name.setEnabled(true);
		password.setEnabled(true);
		submit.setEnabled(true);
	    }
	} else {
	    // exit app
	}
    }

    private void capturePhotoActivity() {
	Toast.makeText(NewUser.this, "Capturing Images for New User...",
		Toast.LENGTH_LONG).show();
	Log.d(TAG, "capturing photo#" + imageCount_ + "...." + "input"
		+ imageCount_ + ".jpg");
	Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	imageFile_ = new File(Environment.getExternalStorageDirectory(),
		"input" + imageCount_ + ".jpg");
	image_ = Uri.fromFile(imageFile_);
	intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, image_);
	startActivityForResult(intentCamera, 0);
    }
}
