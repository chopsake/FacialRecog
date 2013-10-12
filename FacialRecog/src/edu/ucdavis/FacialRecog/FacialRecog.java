package edu.ucdavis.FacialRecog;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class FacialRecog extends TabActivity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.main);

	Resources res = getResources();
	TabHost tabHost = getTabHost();

	Intent intent1 = new Intent(this, Profile.class);
	Intent intent2 = new Intent(this, Friends.class);
	Intent intent3 = new Intent(this, Photos.class);

	tabHost.addTab(tabHost.newTabSpec("tab1")
		.setIndicator("", res.getDrawable(R.drawable.profile))
		.setContent(intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
	tabHost.addTab(tabHost.newTabSpec("tab2")
		.setIndicator("", res.getDrawable(R.drawable.friends))
		.setContent(intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
	tabHost.addTab(tabHost.newTabSpec("tab3")
		.setIndicator("", res.getDrawable(R.drawable.search))
		.setContent(intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
    }
}