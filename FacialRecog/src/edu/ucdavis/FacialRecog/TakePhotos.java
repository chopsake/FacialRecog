package edu.ucdavis.FacialRecog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

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

public class TakePhotos extends Activity {
    private static final String TAG = "TakePhotos******";

    private static final String QUERY_ADDRESS = Info.ipAddress
	    + "/face/query.php";

    private Uri image_;
    private File imageFile_;
    private ByteArrayBody[] bab = new ByteArrayBody[3];
    private int imageCount_ = 0;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	capturePhotoActivity();
    }

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
	    } else {
		imageCount_ = 0;
		performPost();
	    }
	} else {
	    returnToPrev(RESULT_CANCELED, null);
	}
    }

    private void capturePhotoActivity() {
	Log.d(TAG, "capturing photo#" + imageCount_ + "...." + "input"
		+ imageCount_ + ".jpg");
	Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	imageFile_ = new File(Environment.getExternalStorageDirectory(),
		"input" + imageCount_ + ".jpg");
	image_ = Uri.fromFile(imageFile_);
	intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, image_);
	startActivityForResult(intentCamera, 0);
    }

    private void performPost() {
	HttpClient httpclient = new DefaultHttpClient();
	HttpPost httppost = new HttpPost(QUERY_ADDRESS);
	Log.d(TAG, "posting to " + QUERY_ADDRESS);

	try {
	    // Add data
	    MultipartEntity entity = new MultipartEntity(
		    HttpMultipartMode.BROWSER_COMPATIBLE);
	    entity.addPart("search", new StringBody("1"));
	    Log.d(TAG, "added search");

	    for (int i = 0; i < 3; i++) {
		entity.addPart("input" + i, bab[i]);
		Log.d(TAG, "added " + "input" + i);
	    }

	    httppost.setEntity(entity);
	    Log.d(TAG, "entity set");

	    // Execute HTTP Post Request
	    HttpResponse response = httpclient.execute(httppost);
	    Log.d(TAG, "post executed");

	    String html = new String(EntityUtils.toString(response.getEntity()));
	    Log.d(TAG, "got html!!");
	    returnToPrev(RESULT_OK, html);
	} catch (ClientProtocolException e) {
	} catch (IOException e) {
	}
    }

    private void returnToPrev(int result, String html) {
	Log.d(TAG, "returning to previous intent!");
	Bundle extras = new Bundle();
	Intent intentReturnToPrev = new Intent();
	if (html != null) {
	    extras.putString("HTML", html);
	    intentReturnToPrev.putExtras(extras);
	}
	setResult(result, intentReturnToPrev);
	finish();
    }
}
