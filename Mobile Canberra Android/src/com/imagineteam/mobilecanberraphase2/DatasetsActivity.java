package com.imagineteam.mobilecanberraphase2;

import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.agimind.widget.SlideHolder;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.imagineteam.dataobjects.DatasetListObject;
import com.imagineteam.dataobjects.MainScreenRow;
import com.imagineteam.dataobjects.MapGalleryAdapter;
import com.imagineteam.dataobjects.ServerInterface;
import com.imagineteam.dataobjects.TutorialGalleryAdapter;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class DatasetsActivity extends Activity {

	SlideHolder mSlideHolder;
	ListView datasetlist;
	ProgressDialog loading;
	MainScreenRow adapter;
	ListView menulist;
	EditText searchEditText;
	ArrayList<DatasetListObject> datasets;
	ArrayList<DatasetListObject> originalDatasets;
	Gallery tutorialGallery;
	TutorialGalleryAdapter galleryAdapter;
	RelativeLayout tutorialholder;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_datasets);
		final Typeface mFont = Typeface.createFromAsset(getAssets(),
				"fonts/Ubuntu-Medium.ttf");
		TextView helpmefind = (TextView) findViewById(R.id.helpmefindtitle);
		helpmefind.setTypeface(mFont);
		tutorialGallery = (Gallery) findViewById(R.id.tutorialGallery);
		tutorialholder = (RelativeLayout) findViewById(R.id.tutorialholder);
		
		
		datasetlist = (ListView) findViewById(R.id.datasetlist);
		menulist = (ListView) findViewById(R.id.menulist);
		// Defined Array values to show in ListView
		String[] values = new String[] { "How To Use",
				"Privacy Policy", "About","Give Feedback" };
		searchEditText = (EditText) findViewById(R.id.search);
		searchEditText
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (actionId == KeyEvent.KEYCODE_ENTER
								|| actionId == EditorInfo.IME_ACTION_SEARCH) {

							performSearch();
							return true;
						}
						return false;

					}
				});
		ArrayAdapter<String> menuadapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1, values);
		menulist.setAdapter(menuadapter);
		// ListView Item Click Listener
		menulist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				// ListView Clicked item index
				int itemPosition = position;

				// ListView Clicked item value
				String itemValue = (String) menulist
						.getItemAtPosition(position);
				ArrayList<String> titles = new ArrayList<String>();
				ArrayList<String> content = new ArrayList<String>();
				ArrayList<Integer> ids = new ArrayList<Integer>();
				if (itemPosition==0){
					titles.add("Welcome to Mobile Canberra!");
					content.add("Mobile Canberra is a powerful platform for showing points of interest and services around Canberra. Swipe right to begin the tutorial->");
					ids.add(R.drawable.searchbtn);
					
					titles.add("Main Screen Explanation");
					content.add("The main screen shows you a list of available services. This list will be updated as more services come online. To search for specific services, enter a search term into the search bar labeled 'I'm looking for..'. Example searches for the 'Bus Stops' service are 'Action', 'Buses', and 'Bus Stops'.");
					ids.add(R.drawable.aroundmesearch);
					
					titles.add("Main Screen Explanation");
					content.add("Each service will be assigned a color, which you can reference when looking at several services on the Map Screen");
					ids.add(R.drawable.datasetscolor);
					
					titles.add("Main Screen Explanation");
					content.add("Click the favourites button on the top right to show your favourite services. You will be able to add services to your favourites on the Map Screen");
					ids.add(R.drawable.headerfavourites);
					
					titles.add("Main Screen Explanation");
					content.add("Not particularly fussed on a particular dataset, but want to see what's around you generally? Click the 'Around Me' button on the top right to see all the services around you");
					ids.add(R.drawable.headeraroundme);
					galleryAdapter = new TutorialGalleryAdapter(DatasetsActivity.this,titles, content, ids);
					tutorialGallery.setAdapter(galleryAdapter);
					tutorialholder.setVisibility(View.VISIBLE);
					datasetlist.setEnabled(false);
					mSlideHolder.toggle();
					mSlideHolder.setAllowInterceptTouch(false);
					
				}
				else if (itemPosition==1){
					titles.add("Privacy Policy");
					content.add("Your privacy is very important to us. Accordingly, we have developed this policy in order for you to understand how we collect, use, communicate and disclose and make use of personal information. \nWe will only retain  information as long as necessary for the fulfillment of those purposes.\nWe will collect personal information by lawful and fair means and, where appropriate, with the knowledge or consent of the individual concerned.\nWe will protect personal information by reasonable security safeguards against loss or theft, as well as unauthorized access, disclosure, copying, use or modification. We will make readily available to customers information about our policies and practices relating to the management of personal information.");
					ids.add(0);
					galleryAdapter = new TutorialGalleryAdapter(DatasetsActivity.this,titles, content, ids);
					tutorialGallery.setAdapter(galleryAdapter);
					tutorialholder.setVisibility(View.VISIBLE);
					datasetlist.setEnabled(false);
					mSlideHolder.toggle();
					mSlideHolder.setAllowInterceptTouch(false);
				}
				else if (itemPosition==2){
					titles.add("About");
					content.add("An ACT Government Initiative\n\nDeveloped by the Imagine Team Pty Ltd, designed by Zoo Advertising Pty Ltd\n\nPlease be advised that in some rare instances, some service locations may be inaccurate.\n\nLibraries Used:\n    MyBlurIntroductionView\n    iCarousel\n    KPClustering\n    SVProgessHUD\n    RESSideMenu\n    ACParallax\n\nPlease email support@imagineteamsolutions.com for more details.");
					ids.add(0);
					galleryAdapter = new TutorialGalleryAdapter(DatasetsActivity.this,titles, content, ids);
					tutorialGallery.setAdapter(galleryAdapter);
					tutorialholder.setVisibility(View.VISIBLE);
					datasetlist.setEnabled(false);
					mSlideHolder.toggle();
					mSlideHolder.setAllowInterceptTouch(false);
				}
				else if (itemPosition==3){
					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://imagineteamsolutions.com/mobile-canberra-app-survey/"));
					startActivity(browserIntent);
				}
				
				

			}

		});

		mSlideHolder = (SlideHolder) findViewById(R.id.mainscreenroot);
		datasetlist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				Integer resultCode = GooglePlayServicesUtil
						.isGooglePlayServicesAvailable(DatasetsActivity.this);
				if (resultCode == ConnectionResult.SUCCESS) {
					DatasetListObject data = adapter.getItem(position);

					final Intent intent = new Intent(getApplicationContext(),
							MapActivity.class);
					intent.putExtra("dataid", data.getDatasetId());
					intent.putExtra("aroundMe", false);
					startActivity(intent);
				} else {
					Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
							resultCode, DatasetsActivity.this, 0);
					if (dialog != null) {
						// This dialog will help the user update to the latest
						// GooglePlayServices
						dialog.show();
					}
				}

			}
		});
		loading = new ProgressDialog(this);
		loading.setTitle("Please wait");
		loading.setMessage("Loading...");
		loading.setCancelable(false);
		loading.setCanceledOnTouchOutside(false);
		loading.show();
		try {
			getDatasets();
		} catch (Exception e) {
			loading.dismiss();
			showErrorDialog();
		}

	}
	
	public void skipTutorial(View v){
		tutorialholder.setVisibility(View.GONE);
		datasetlist.setEnabled(true);
		mSlideHolder.setAllowInterceptTouch(true);
	}

	public void performSearch() {

		datasets.clear();
		for (int i = 0; i < originalDatasets.size(); i++) {
			datasets.add(originalDatasets.get(i));
		}
		if (searchEditText.getText().toString().length() == 0) {
			new AlertDialog.Builder(DatasetsActivity.this)
					.setTitle("Sorry")
					.setMessage("You must enter a valid search term")
					.setNegativeButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// do nothing
								}
							}).show();
			adapter = new MainScreenRow(getApplicationContext(), datasets);
			datasetlist.setAdapter(adapter);
			
		} else {
			loading = new ProgressDialog(this);
			loading.setTitle("Please wait");
			loading.setMessage("Loading...");
			loading.setCancelable(false);
			loading.setCanceledOnTouchOutside(false);
			loading.show();
			ServerInterface.get(
					"SearchDatasets?searchterm="
							+ URLEncoder.encode(searchEditText.getText()
									.toString()), null,
					new JsonHttpResponseHandler() {
						@Override
						public void onSuccess(JSONObject response) {
							try {
								
								Boolean success;
								success = response.getBoolean("success");
								if (success == true) {
									JSONArray responselist = response
											.getJSONArray("results");
									String jo = null;
									for (int i = 0; i < responselist.length(); i++) {
										jo = responselist.getString(i);
									}
									DatasetListObject foundData = null;
									for (int i = 0; i < originalDatasets.size(); i++) {

										DatasetListObject data = originalDatasets
												.get(i);
										
										if (data.getName().equalsIgnoreCase(jo)) {
											
											foundData = data;
										}
									}
									if (foundData == null) {
										new AlertDialog.Builder(
												DatasetsActivity.this)
												.setTitle("Sorry")
												.setMessage(
														"No results were found for your query")
												.setNegativeButton(
														"OK",
														new DialogInterface.OnClickListener() {
															public void onClick(
																	DialogInterface dialog,
																	int which) {
																// do nothing
															}

														}).show();
										adapter = new MainScreenRow(
												getApplicationContext(),
												datasets);
										datasetlist.setAdapter(adapter);
									} else {
										datasets.clear();
										datasets.add(foundData);
										adapter = new MainScreenRow(
												getApplicationContext(),
												datasets);
										datasetlist.setAdapter(adapter);
									}
									loading.dismiss();
								}
							} catch (Exception e) {
								loading.dismiss();
								showErrorDialog();
							}

						}

					});
		}
	}


	public void getDatasets() {
		ServerInterface.get("GetListOfDatasets", null,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(JSONObject response) {
						try {
						
							Boolean success;
							success = response.getBoolean("success");
							if (success == true) {
								JSONArray responselist = response
										.getJSONArray("listOfDatasets");
								datasets = new ArrayList<DatasetListObject>();
								originalDatasets = new ArrayList<DatasetListObject>();
								for (int i = 0; i < responselist.length(); i++) {
									JSONObject jo = responselist
											.getJSONObject(i);
									DatasetListObject data = new DatasetListObject(
											jo.getString("name"), jo
													.getString("color"), jo
													.getString("id"));
									datasets.add(data);
									originalDatasets.add(data);
								}
								AppDelegate ad = (AppDelegate) getApplication();
								ad.setDatasets(datasets);
								adapter = new MainScreenRow(
										getApplicationContext(), datasets);
								datasetlist.setAdapter(adapter);
								loading.dismiss();
								
							}
						} catch (Exception e) {
							loading.dismiss();
							showErrorDialog();
						}

					}
				});
	}

	public void filterfavs(View v) {
		if (datasets.size() == originalDatasets.size()) {
			datasets.clear();
			final SharedPreferences settings = getSharedPreferences("SAVED", 0);
			for (int i = 0; i < originalDatasets.size(); i++) {

				DatasetListObject data = originalDatasets.get(i);
			
				if (settings.contains(data.getColor())) {
					datasets.add(data);
				}
			}
			adapter = new MainScreenRow(getApplicationContext(), datasets);
			datasetlist.setAdapter(adapter);
		} else {
			datasets.clear();
			for (int i = 0; i < originalDatasets.size(); i++) {
				datasets.add(originalDatasets.get(i));
			}
			adapter = new MainScreenRow(getApplicationContext(), datasets);
			datasetlist.setAdapter(adapter);
		}

	}
	
	public void aroundMe(View v){
		final Intent intent = new Intent(getApplicationContext(),
				MapActivity.class);
		intent.putExtra("dataid", datasets.get(0).getDatasetId());
		intent.putExtra("aroundMe", true);
		startActivity(intent);
	}

	public void showErrorDialog() {
		new AlertDialog.Builder(DatasetsActivity.this).setTitle("Sorry!")
				.setMessage("Something went wrong- please try again later")
				.setNegativeButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// do nothing
					}
				}).show();
	}

	public void menuPress(View v) {

		mSlideHolder.toggle();
	}

}
