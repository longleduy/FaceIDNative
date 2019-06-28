package com.faceidnative.faceidapplication;
import com.faceidnative.R;
import com.neurotec.face.verification.client.NCapturePreview;
import com.neurotec.face.verification.client.NCapturePreviewEvent;
import com.neurotec.face.verification.client.NCapturePreviewListener;
import com.neurotec.face.verification.client.NFaceVerificationClient;
import com.neurotec.face.verification.client.NOperationResult;
import com.neurotec.face.verification.client.NStatus;
import com.neurotec.face.verification.server.rest.ApiClient;
import com.neurotec.face.verification.server.rest.ApiException;
import com.neurotec.face.verification.server.rest.api.OperationApi;
import com.faceidnative.faceidapplication.gui.EnrollmentDialogFragment;
import com.faceidnative.faceidapplication.gui.NFaceVerificationClientView;
import com.faceidnative.faceidapplication.gui.SettingsActivity;
import com.faceidnative.faceidapplication.gui.SettingsFragment;
import com.faceidnative.faceidapplication.gui.SubjectListFragment;
import com.faceidnative.faceidapplication.utils.BaseActivity;
import com.faceidnative.faceidapplication.utils.FVDatabaseHelper;
import android.util.Base64;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import java.io.ByteArrayOutputStream;
import android.os.AsyncTask;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FaceVerificationApplication extends BaseActivity implements SubjectListFragment.SubjectSelectionListener, EnrollmentDialogFragment.EnrollmentDialogListener {

	// ===========================================================
	// Private fields
	// ===========================================================

	private static final String TAG = "FaceVerificationApp";
	private static final String EXTRA_REQUEST_CODE = "request_code";
	private static final int VERIFICATION_REQUEST_CODE = 1;
	private static final int REQUEST_CAMERA_PERMISSION = 10;
	private boolean mAppClosing;
	private NFaceVerificationClientView mFaceView;
	private OperationApi mOperationApi;
	private byte[] mTemplateBuffer = null;
	private FVDatabaseHelper mDBHelper;
	private NFaceVerificationClient mNFV = null;
	private Map<String, Integer> mPermissions = new HashMap<String, Integer>();

	private ImageButton mEnrollButton = null;
	private ImageButton mForceButton = null;
	private ImageButton mCancelButton = null;
	private Button mVerifyButton = null;
	private Button mCheckLivenessButton = null;
	View nFaceIcon;

	// ===========================================================
	// Protected methods
	// ===========================================================

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nlvdemo);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		NFaceVerificationClient.setEnableLogging(true);
		// on application start you must set NCore context
		NFaceVerificationClient.setContext(this);
		mDBHelper = new FVDatabaseHelper(this);
		mFaceView = (NFaceVerificationClientView) findViewById(R.id.nFaceView);
		 nFaceIcon = (View) findViewById(R.id.nFaceIcon);
		// button implementations
		mEnrollButton = (ImageButton) findViewById(R.id.button_enroll);
		mEnrollButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				nFaceIcon.setVisibility(View.GONE);
				mFaceView.setVisibility(View.VISIBLE);
				createTemplate();
			}
		});

		mForceButton = (ImageButton) findViewById(R.id.button_force);
		mForceButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mNFV != null) {
					onResume();
				} else {
					showError("Face verification client was not initialised");
				}
			}
		});

		mCancelButton = (ImageButton) findViewById(R.id.button_cancel);
		mCancelButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mNFV != null) {
					showProgress(R.string.msg_cancelling);
					mNFV.cancel();
					hideProgress();
					finish();
				} else {
					showError("Face verification client was not initialised");
				}
			}
		});

		setControllsEnabled(false);


		String[] neededPermissions = getNotGrantedPermissions();
		if(neededPermissions.length == 0) {
			new InitializationTask().execute();
		} else {
			requestPermissions(neededPermissions);
		}
	}

	private void setControllsEnabled(boolean enable) {
		mEnrollButton.setEnabled(enable);
		mCancelButton.setEnabled(enable);
		mForceButton.setEnabled(enable);
	}

	private String[] getNotGrantedPermissions() {
		List<String> neededPermissions = new ArrayList<String>();
		int cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

		if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
			neededPermissions.add(Manifest.permission.CAMERA);
		}
		return neededPermissions.toArray(new String[neededPermissions.size()]);
	}

	private void requestPermissions(String[] permissions) {
		ActivityCompat.requestPermissions(this, permissions,REQUEST_CAMERA_PERMISSION);
	}

	public void onRequestPermissionsResult(int requestCode, final String permissions[], int[] grantResults) {
		switch (requestCode) {
			case REQUEST_CAMERA_PERMISSION: {
				// Initialize the map with permissions
				mPermissions.clear();
				// Fill with actual results from user
				if (grantResults.length > 0) {
					for (int i = 0; i < permissions.length; i++) {
						mPermissions.put(permissions[i], grantResults[i]);
					}
					// Check if at least one is not granted
					if (mPermissions.get(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
						showError("Permission not granted", true);
					} else {
						Log.i(TAG, "Permission granted");
						new InitializationTask().execute();
					}
				}
			} break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public void onResume() {
		super.onResume();
		mAppClosing = false;
		try {
			if ((mOperationApi == null) || (SettingsFragment.isUpdateClientNeeded())) {
				Log.i("TEST", "Update client");
				ApiClient client = new ApiClient();
				client.setConnectTimeout(60000);
				SettingsFragment.updateClientAuthentification(client);
				mOperationApi = new OperationApi(client);
			}
			setControllsEnabled(true);
		} catch (Exception e) {
			e.printStackTrace();
			setControllsEnabled(false);
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		mAppClosing = true;
	}

	public void createTemplate() {
		Log.i("TEST", "createTemplate");
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					// cancel in there are any other operations in progress
					NFV.getInstance().cancel();
					byte[] registrationKey = NFV.getInstance().startCreateTemplate();
					byte[] serverKey = mOperationApi.validate(registrationKey);
					NOperationResult result = NFV.getInstance().finishOperation(serverKey);
					if (!mAppClosing) {
						mFaceView.setEventInfo(result);
						if (result.getStatus() == NStatus.SUCCESS) {
							mTemplateBuffer = result.getTemplate();
							Bitmap immagex = result.getImage();
                            //String a = result.getTokenImage();
							String imgStr = Base64.encodeToString(mTemplateBuffer,Base64.DEFAULT);
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            immagex.compress(Bitmap.CompressFormat.PNG,100,baos);
                            byte[] b = baos.toByteArray();
							//String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
							//showInfo(imageEncoded);
							Intent returnIntent = new Intent();
							returnIntent.putExtra("result", b);
							setResult(Activity.RESULT_OK, returnIntent);
							finish();
						} else {
							showInfo(String.format(getString(R.string.msg_operation_status), result.getStatus().toString().toLowerCase()));
						}
					}
				} catch (ApiException e) {
					showError(e);
				} catch (Exception e) {
					showError(e);
				}
			}
		}).start();
	};

	public void checkLiveness() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					// cancel in there are any other operations in progress
					NFV.getInstance().cancel();
					byte[] registrationKey = NFV.getInstance().startCheckLiveness();
					byte[] serverKey = mOperationApi.validate(registrationKey);
					NOperationResult result = NFV.getInstance().finishOperation(serverKey);
					if (!mAppClosing) {
						showInfo(String.format(getString(R.string.msg_operation_status), result.getStatus().toString().toLowerCase()));
						if (result.getStatus() == NStatus.SUCCESS) {
							mFaceView.setEventInfo(result);
						}
					}
				} catch (ApiException e) {
					showError(e);
				} catch (Throwable e) {
					showError(e);
				}
			}
		}).start();
	};

	public void verify(final byte[] template) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					// cancel in there are any other operations in progress
					NFV.getInstance().cancel();
					if (template == null) {
						if (!mAppClosing) showInfo(getString(R.string.msg_buffer_is_null));
						return;
					}
					NOperationResult result = NFV.getInstance().verify(template);
					if (!mAppClosing) {
						mFaceView.setEventInfo(result);
						if (result.getStatus() == NStatus.SUCCESS) {
							showInfo(String.format(getString(R.string.msg_operation_status), String.format(getString(R.string.msg_verification_succeeded))));
						} else {
							showInfo(String.format(getString(R.string.msg_operation_status), String.format(getString(R.string.msg_verification_failed) + result.getStatus().toString().toLowerCase())));
						}
					}
				} catch (Throwable e) {
					showError(e);
				}
			}
		}).start();
	};

	@Override
	protected void onStop() {
		mAppClosing = true;
		try {
			NFV.getInstance().cancel();
		} catch (SecurityException e) {
			Log.e(TAG, e.getMessage(), e);
		}
		super.onStop();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i(TAG, "onOptionsItemSelected" + item.getTitle());
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;
		}
		if (id == R.id.action_clear_db) {
			Log.i(TAG, "action_clear_db");
			mDBHelper.clearTable();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onSubjectSelected(String subjectID, Bundle bundle) {
		if (bundle.getInt(EXTRA_REQUEST_CODE) == VERIFICATION_REQUEST_CODE) {
			byte[] template = mDBHelper.getTemplate(subjectID);
			verify(template);
		}
	}

	@Override
	public void onEnrollmentIDProvided(String id) {
		try {
				createTemplate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	final class InitializationTask extends AsyncTask<Object, Boolean, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgress(R.string.msg_initialising);
		}

		@Override
		protected Boolean doInBackground(Object... params) {
			try {
				// get NFV for the first time
				mNFV = NFV.getInstance();

				// load settings
				SettingsFragment.loadSettings();

				mNFV.setCapturePreviewListener(new NCapturePreviewListener() {

					@Override
					public void capturePreview(NCapturePreviewEvent nCapturePreviewEvent) {

						mFaceView.setEvent(nCapturePreviewEvent);
					}
				});
			} catch (Exception e) {
				Log.e(FaceVerificationApplication.this.TAG, e.getMessage(), e);
				return false;
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			hideProgress();
		}
	}
}
