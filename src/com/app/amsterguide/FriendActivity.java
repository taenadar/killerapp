package com.app.amsterguide;

import java.util.List;
import java.util.regex.Pattern;


import com.app.amsterguide.adapters.FriendRowAdapter;
import com.app.amsterguide.loaders.FriendLoader;

import core.connection.RESTSocialService;
import core.models.Friendship;
import core.models.User;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.app.amsterguide.adapters.FriendRowAdapter;
import com.app.amsterguide.loaders.FriendLoader;
import com.app.killerapp.FriendShipRequestsActivity;
import com.app.killerapp.R;

import core.models.User;

public class FriendActivity extends FragmentActivity implements
		LoaderCallbacks<List<Friendship>> {

	private FriendRowAdapter adapter;
	private static FriendActivity selfReferance = null;

	private FragmentActivity GetThis() {
		return this;
	}

	AddCompanionDialog dialig;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activityfriend);
		selfReferance = this;
		dialig = new AddCompanionDialog(this);
		
		SharedPreferences settings = getSharedPreferences("LocalPrefs", 0);
		long userId = Long.valueOf(settings.getString("userID", "0"))
				.longValue();
		
		adapter = new FriendRowAdapter(this, userId);

		ListView listView = (ListView) findViewById(R.id.friendlist);
		listView.setAdapter(adapter);
		getSupportLoaderManager().initLoader(0, null, this);

		// get the ActionBar from our layout
		/*
		ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
		// set its title
		actionBar.setTitle("");
		// set what the home action does
		actionBar.setHomeAction(new IntentAction(this, FriendActivity
				.createIntent(this), R.drawable.ic_custom_launcher));
		// add the refresh action
<<<<<<< Updated upstream:src/com/app/amsterguide/FriendActivity.java
		actionBar.addAction(new SaveAction());
		*/

		//actionBar.addAction(new ViewRequestsAction());

		// add the save action

		//actionBar.addAction(new SaveAction());

		// add a listener to the title
		/*
		 * actionBar.setOnTitleClickListener(new onclickListener() { public void
		 * onclick(View v) { makeToast("Title clicked..."); }
		 */
	}

	private void startFriendShipRequestsActivity() {
		Intent intent = new Intent(this, FriendShipRequestsActivity.class);
		startActivity(intent);
	}

	// the save action
	/*
	private class SaveAction implements Action {

		@Override
		public int getDrawable() {
			return R.drawable.actionbar_btn_normal;
		}

		@Override
		public void performAction(View view) {
			dialig.show();
			// makeToast("Saving...");
		}

	}

	// the save action
	private class ViewRequestsAction implements Action {

		@Override
		public int getDrawable() {
			return R.drawable.actionbar_back_indicator;
		}

		@Override
		public void performAction(View view) {
			startFriendShipRequestsActivity();
			// makeToast("Saving...");
		}

	}*/

	public static Context getContext() {
		if (selfReferance != null) {
			return selfReferance.getApplicationContext();
		}
		return null;
	}

	@Override
	public Loader<List<Friendship>> onCreateLoader(int id, Bundle args) {
		SharedPreferences settings = getSharedPreferences("LocalPrefs", 0);
		long userId = Long.valueOf(settings.getString("userID", "0"))
				.longValue();
		String authToken = settings.getString("token", "letmein");
		Log.d("realauthtoken", authToken);
		Log.d("realID", String.valueOf(userId));
		return new FriendLoader(getApplicationContext(), userId, authToken, "APPROVED");
	}

	@Override
	public void onLoadFinished(Loader<List<Friendship>> loader, List<Friendship> result) {
		adapter.setList(result);
	}

	@Override
	public void onLoaderReset(Loader<List<Friendship>> arg0) {
		// TODO Auto-generated method stub

	}

	// a simple method to create an intent
	public static Intent createIntent(Context context) {
		Intent i = new Intent(context, MainActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		return i;
	}

	public void makeToast(String message) {
		// with jam obviously
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

	class AddCompanionDialog implements OnDismissListener, OnCancelListener {
		final private EditText editText;
		final private AlertDialog alertDialog;

		private Boolean canceled;

		AddCompanionDialog(Context context) {
			editText = new EditText(context);
			alertDialog = buildAlertDialog(context);
			alertDialog.setOnDismissListener(this);
			alertDialog.setOnCancelListener(this);

		}

		private AlertDialog buildAlertDialog(Context context) {
			return new AlertDialog.Builder(context).setTitle("Title")
					.setMessage("Enter email address").setView(editText)
					.setNeutralButton("Submit", null)
					.setNegativeButton("Cancel", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.cancel();
						}
					}).create();
		}

		public void show() {
			canceled = false;
			alertDialog.show();
		}

		@Override
		public void onDismiss(DialogInterface dialog) {
			if (!canceled) {
				final String name = editText.getText().toString();
				if (!rfc2822.matcher(name).matches()) {
					editText.setError("Email address is not valid");
					show();
				} else {
					canceled = true;
					Log.d("Send Mail", "Sending add request");
					SharedPreferences settings = getSharedPreferences("LocalPrefs", 0);
					long userId = Long.valueOf(settings.getString("userID", "0"))
							.longValue();
					String authToken = settings.getString("token", "letmein");
					RESTSocialService socialService = new RESTSocialService();
					socialService.AddFriendship(userId, authToken, name);
					// Send request to server
				}
			}
		}

		@Override
		public void onCancel(DialogInterface dialog) {
			canceled = true;
		}

		private final Pattern rfc2822 = Pattern
				.compile("^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$");
	}
}