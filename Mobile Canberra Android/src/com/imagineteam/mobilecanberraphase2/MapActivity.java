package com.imagineteam.mobilecanberraphase2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.agimind.widget.SlideHolder;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.maps.GeoPoint;
import com.twotoasters.clusterkraf.Clusterkraf;
import com.twotoasters.clusterkraf.Clusterkraf.ProcessingListener;
import com.twotoasters.clusterkraf.InputPoint;
import com.twotoasters.clusterkraf.Options.ClusterClickBehavior;
import com.twotoasters.clusterkraf.Options.ClusterInfoWindowClickBehavior;
import com.twotoasters.clusterkraf.Options.SinglePointClickBehavior;

import com.imagineteam.dataobjects.DatasetListObject;
import com.imagineteam.dataobjects.DirectionsJSONParser;
import com.imagineteam.dataobjects.MapGalleryAdapter;
import com.imagineteam.dataobjects.MarkerData;
import com.imagineteam.dataobjects.ServerInterface;
import com.imagineteam.dataobjects.TutorialGalleryAdapter;

import com.imagineteam.dataobjects.ToastedMarkerOptionsChooser;
import com.imagineteam.dataobjects.ToastedOnMarkerClickDownstreamListener;
import com.loopj.android.http.JsonHttpResponseHandler;

public class MapActivity extends FragmentActivity implements
		ProcessingListener, AnimationListener, LocationListener {

	public static final String EXTRA_OPTIONS = "options";

	private static final String KEY_CAMERA_POSITION = "camera position";

	private Options options;
	com.twotoasters.clusterkraf.Options optionstest;
	private GoogleMap map;
	private Clusterkraf clusterkraf;
	private ArrayList<InputPoint> inputPoints;
	ProgressDialog loading;
	RelativeLayout dialogholder;
	Animation dialogOut;
	Animation dialogIn;
	View opacityMask;
	SlideHolder mSlideHolder;
	ListView menulist;
	MarkerData clicked;
	Gallery datasetGallery;
	MapGalleryAdapter galleryAdapter;
	HashMap<String, String> selectedDatasets;
	EditText searchEditText;
	LocationManager locationManager;
	AppDelegate ad;
	LatLng currentLoc;
	Polyline lineDrawn;
	Boolean aroundMeSent;
	int drawn;
	Gallery tutorialGallery;
	TutorialGalleryAdapter tutorialAdapter;
	RelativeLayout tutorialholder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setProgressBarIndeterminate(true);

		setContentView(R.layout.activity_map);
		tutorialGallery = (Gallery) findViewById(R.id.mapTutorialGallery);
		tutorialholder = (RelativeLayout) findViewById(R.id.mapTutorialholder);
		locationManager = (LocationManager) getSystemService(getApplicationContext().LOCATION_SERVICE);
		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, (long) 100.0, (float) 500.0,
				this);
		searchEditText = (EditText) findViewById(R.id.searchAddress);
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
		optionstest = new com.twotoasters.clusterkraf.Options();
		selectedDatasets = new HashMap<String, String>();
		Bundle extras = getIntent().getExtras();
		String id = null;
		aroundMeSent = false;
		if (extras != null) {
			aroundMeSent= extras.getBoolean("aroundMe");
			id = extras.getString("dataid");

		}
		drawn = 0;
		inputPoints = new ArrayList<InputPoint>();
		datasetGallery = (Gallery) findViewById(R.id.datasetgallery);
		ad = (AppDelegate) getApplication();
		galleryAdapter = new MapGalleryAdapter(this, ad.getDatasets());
		datasetGallery.setAdapter(galleryAdapter);
		datasetGallery.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(final AdapterView<?> arg0, final View view,
					final int position, final long arg3) {

				final DatasetListObject dataset = ((DatasetListObject) datasetGallery
						.getAdapter().getItem(position));

				if (selectedDatasets.containsKey(dataset.getDatasetId())) {
					new AlertDialog.Builder(MapActivity.this)
							.setTitle("Sorry")
							.setMessage("You've already selected this dataset")
							.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {

										}
									}).show();
				} else {
					loading.setTitle("Please wait");
					loading.setMessage("Loading...");
					loading.show();
					clusterkraf.clear();
					getPoints(dataset.getDatasetId(), 0, false);
				}

			}
		});
		mSlideHolder = (SlideHolder) findViewById(R.id.maproot);
		mSlideHolder.setAllowInterceptTouch(false);
		menulist = (ListView) findViewById(R.id.menulist);
		// Defined Array values to show in ListView
		String[] values = new String[] { "How To Use", 
				"Privacy Policy", "About", "Give Feedback" };
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
					titles.add("Map Screen Explanation");
					content.add("Click on a marker to see the details of the point of interest. Once a marker is clicked, the details will be displayed. You will also have the option to obtain directions to the marker, and add the service to which the marker belongs to your favourites. Swipe right to see the rest of the tutorial->");
					ids.add(R.drawable.pindetails);
					
					titles.add("Map Screen Explanation");
					content.add("When zoomed out, the markers will cluster together. Zoom in to see individual markers");
					ids.add(0);
					
					titles.add("Map Screen Explanation");
					content.add("Press the magnifying glass icon on the side to bring out a search entry, where you can look up service locations around a particular address. Press the triangle icon to zoom to your current location");
					ids.add(0);
					
					titles.add("Map Screen Explanation");
					content.add("Click datasets in the gallery at the bottom to load additional services onto the map.");
					ids.add(0);
					tutorialAdapter = new TutorialGalleryAdapter(MapActivity.this,titles, content, ids);
					tutorialGallery.setAdapter(tutorialAdapter);
					tutorialholder.setVisibility(View.VISIBLE);
					map.getUiSettings().setScrollGesturesEnabled(false);
					
					datasetGallery.setOnTouchListener(new OnTouchListener() {

						  @Override
						  public boolean onTouch(View v, MotionEvent event) {

						     return true;
						  }
						 });
					
				}
				else if (itemPosition==1){
					titles.add("Privacy Policy");
					content.add("Your privacy is very important to us. Accordingly, we have developed this policy in order for you to understand how we collect, use, communicate and disclose and make use of personal information. \nWe will only retain  information as long as necessary for the fulfillment of those purposes.\nWe will collect personal information by lawful and fair means and, where appropriate, with the knowledge or consent of the individual concerned.\nWe will protect personal information by reasonable security safeguards against loss or theft, as well as unauthorized access, disclosure, copying, use or modification. We will make readily available to customers information about our policies and practices relating to the management of personal information.");
					ids.add(0);
					tutorialAdapter = new TutorialGalleryAdapter(MapActivity.this,titles, content, ids);
					tutorialGallery.setAdapter(tutorialAdapter);
					tutorialholder.setVisibility(View.VISIBLE);
					map.getUiSettings().setScrollGesturesEnabled(false);
					
					datasetGallery.setOnTouchListener(new OnTouchListener() {

						  @Override
						  public boolean onTouch(View v, MotionEvent event) {

						     return true;
						  }
						 });
				}
				else if (itemPosition==2){
					titles.add("About");
					content.add("An ACT Government Initiative\n\nDeveloped by the Imagine Team Pty Ltd, designed by Zoo Advertising Pty Ltd\n\nPlease be advised that in some rare instances, some service locations may be inaccurate.\n\nLibraries Used:\n    MyBlurIntroductionView\n    iCarousel\n    KPClustering\n    SVProgessHUD\n    RESSideMenu\n    ACParallax\n\nPlease email support@imagineteamsolutions.com for more details.");
					ids.add(0);
					tutorialAdapter = new TutorialGalleryAdapter(MapActivity.this,titles, content, ids);
					tutorialGallery.setAdapter(tutorialAdapter);
					tutorialholder.setVisibility(View.VISIBLE);
					map.getUiSettings().setScrollGesturesEnabled(false);
					
					datasetGallery.setOnTouchListener(new OnTouchListener() {

						  @Override
						  public boolean onTouch(View v, MotionEvent event) {

						     return true;
						  }
						 });
				}
				else if (itemPosition==3){
					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://imagineteamsolutions.com/mobile-canberra-app-survey/"));
					startActivity(browserIntent);
				}
				
				
				mSlideHolder.toggle();

			}

		});
		dialogholder = (RelativeLayout) findViewById(R.id.dialogholder);
		opacityMask = (View) findViewById(R.id.opacityMask);
		loading = new ProgressDialog(this);
		
		loading.setCancelable(false);
		loading.setCanceledOnTouchOutside(false);
		
		options = new Options();
		if (aroundMeSent==false){
			loading.setTitle("Please wait");
			loading.setMessage("Loading...");
			getPoints(id, 0, false);
		}
		else{
			aroundMe(null);
		}
		loading.show();

	}

	public void skipTutorial(View v){
		tutorialholder.setVisibility(View.GONE);
		map.getUiSettings().setScrollGesturesEnabled(true);
		datasetGallery.setOnTouchListener(new OnTouchListener() {

			  @Override
			  public boolean onTouch(View v, MotionEvent event) {

			     return false;
			  }
			 });
	}
	
	public void searchLoc(View v) {
		performSearch();
	}

	public void aroundMe(View v) {
		for (int i = 0; i < ad.getDatasets().size(); i++) {

			loading.setTitle("Please wait for up to a minute");
			loading.setMessage("Loading all services around you...");
			loading.show();

			if (selectedDatasets.containsKey(ad.getDatasets().get(i)
					.getDatasetId()) == false) {
				if (clusterkraf!=null){
					clusterkraf.clear();
				}
				if (i == ad.getDatasets().size() - 1) {
					getPoints(ad.getDatasets().get(i).getDatasetId(), 1, false);
				} else {
					getPoints(ad.getDatasets().get(i).getDatasetId(), 1, true);
				}

				break;
			}

		}

	}

	public void favourites(View v) {
		
		if (clusterkraf!=null){
			clusterkraf.clear();
		}
		
		if (drawn==0){
			
			inputPoints.clear();
			selectedDatasets.clear();
		}
		final SharedPreferences settings = getSharedPreferences("SAVED", 0);
		int numberOfFavs = settings.getInt("numberOfFavs", 0);
		
		if (numberOfFavs != 0) {
			loading.setTitle("Please wait for up to a minute");
			loading.setMessage("Loading all your favourite services...");
			loading.show();
			
			
			for (int i = 0; i < ad.getDatasets().size(); i++) {
				
				if (settings.contains(ad.getDatasets().get(i).getColor())&& selectedDatasets.containsKey(ad.getDatasets().get(i).getDatasetId())==false) {
				
					if (drawn == numberOfFavs - 1) {
						
						drawn =0;
						getPoints(ad.getDatasets().get(i).getDatasetId(), 2,
								false);
						
					} else {
						getPoints(ad.getDatasets().get(i).getDatasetId(), 2,
								true);
						drawn++;
					}
					
					break;
				}

			}
		}
	}

	public void centerToLoc(View v) {
		locationManager = (LocationManager) getSystemService(getApplicationContext().LOCATION_SERVICE);
		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, (long) 100.0, (float) 500.0,
				this);
	}

	public void takeMeThere(View v) {
		if (currentLoc != null) {
			closeDialog(null);
			loading.setTitle("Please wait");
			loading.setMessage("While we look for directions..");
			loading.show();
			String url = getDirectionsUrl(currentLoc, clicked.getLatLng());
			DownloadTask downloadTask = new DownloadTask();
			downloadTask.execute(url);
		} else {
			new AlertDialog.Builder(MapActivity.this)
					.setTitle("Sorry")
					.setMessage("Your current location is unknown")
					.setNegativeButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// do nothing
								}
							}).show();
		}
	}

	private String getDirectionsUrl(LatLng origin, LatLng dest) {

		// Origin of route
		String str_origin = "origin=" + origin.latitude + ","
				+ origin.longitude;

		// Destination of route
		String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

		// Sensor enabled
		String sensor = "sensor=false";

		// Building the parameters to the web service
		String parameters = str_origin + "&" + str_dest + "&" + sensor;

		// Output format
		String output = "json";

		// Building the url to the web service
		String url = "https://maps.googleapis.com/maps/api/directions/"
				+ output + "?" + parameters;

		return url;
	}

	/** A method to download json data from url */
	private String downloadUrl(String strUrl) throws IOException {
		String data = "";
		InputStream iStream = null;
		HttpURLConnection urlConnection = null;
		try {
			URL url = new URL(strUrl);

			// Creating an http connection to communicate with url
			urlConnection = (HttpURLConnection) url.openConnection();

			// Connecting to url
			urlConnection.connect();

			// Reading data from url
			iStream = urlConnection.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					iStream));

			StringBuffer sb = new StringBuffer();

			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			data = sb.toString();

			br.close();

		} catch (Exception e) {
			Log.d("Exception while downloading url", e.toString());
		} finally {
			iStream.close();
			urlConnection.disconnect();
		}
		return data;
	}

	/** A class to download data from Google Directions URL */
	private class DownloadTask extends AsyncTask<String, Void, String> {

		// Downloading data in non-ui thread
		@Override
		protected String doInBackground(String... url) {

			// For storing data from web service
			String data = "";

			try {
				// Fetching the data from web service
				data = downloadUrl(url[0]);
			} catch (Exception e) {
				data = "";
				Log.d("Background Task", e.toString());
			}
			return data;
		}

		// Executes in UI thread, after the execution of
		// doInBackground()
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			if (result.contentEquals("")) {
				loading.dismiss();
				new AlertDialog.Builder(MapActivity.this)
						.setTitle("Sorry")
						.setMessage(
								"Something went wrong. Please try again later")
						.setNegativeButton("OK",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										// do nothing
									}
								}).show();
			} else {
				ParserTask parserTask = new ParserTask();

				// Invokes the thread for parsing the JSON data
				parserTask.execute(result);
			}
		}
	}

	/** A class to parse the Google Directions in JSON format */
	private class ParserTask extends
			AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

		// Parsing the data in non-ui thread
		@Override
		protected List<List<HashMap<String, String>>> doInBackground(
				String... jsonData) {

			JSONObject jObject;
			List<List<HashMap<String, String>>> routes = null;

			try {
				jObject = new JSONObject(jsonData[0]);
				DirectionsJSONParser parser = new DirectionsJSONParser();

				// Starts parsing data
				routes = parser.parse(jObject);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return routes;
		}

		// Executes in UI thread, after the parsing process
		@Override
		protected void onPostExecute(List<List<HashMap<String, String>>> result) {
			ArrayList<LatLng> points = null;
			PolylineOptions lineOptions = null;

			// Traversing through all the routes
			for (int i = 0; i < result.size(); i++) {
				points = new ArrayList<LatLng>();
				lineOptions = new PolylineOptions();

				// Fetching i-th route
				List<HashMap<String, String>> path = result.get(i);

				// Fetching all the points in i-th route
				for (int j = 0; j < path.size(); j++) {
					HashMap<String, String> point = path.get(j);

					double lat = Double.parseDouble(point.get("lat"));
					double lng = Double.parseDouble(point.get("lng"));
					LatLng position = new LatLng(lat, lng);

					points.add(position);
				}

				// Adding all the points in the route to LineOptions
				lineOptions.addAll(points);
				lineOptions.width(2);
				lineOptions.color(Color.RED);
			}

			if (lineDrawn != null) {
				lineDrawn.remove();
			}
			lineDrawn = map.addPolyline(lineOptions);
			loading.dismiss();
		}
	}

	public void performSearch() {
		
		if (searchEditText.getText().toString().length() == 0) {
			new AlertDialog.Builder(MapActivity.this)
					.setTitle("Sorry")
					.setMessage("You must enter a valid address")
					.setNegativeButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// do nothing
								}
							}).show();
		} else {
			loading = new ProgressDialog(this);
			loading.setTitle("Please wait");
			loading.setMessage("Loading...");
			loading.setCancelable(false);
			loading.setCanceledOnTouchOutside(false);
			loading.show();
			Geocoder geoCoder = new Geocoder(this);
			List<Address> addresses = null;
			try {
				addresses = geoCoder.getFromLocationName(searchEditText
						.getText().toString() + " Canberra", 5);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (addresses.size() > 0) {
				LatLng Addr = new LatLng(addresses.get(0).getLatitude(),
						addresses.get(0).getLongitude());
				
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(Addr, 16));

			} else {
				new AlertDialog.Builder(MapActivity.this)
						.setTitle("Sorry")
						.setMessage("Address not found")
						.setNegativeButton("OK",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										// do nothing
									}
								}).show();
			}
			loading.dismiss();

		}

	}

	public void menuPress(View v) {
		mSlideHolder.toggle();
	}

	public void addToFavs(View v) {

		new AlertDialog.Builder(MapActivity.this)
				.setTitle("Confirmation")
				.setMessage(
						"Are you sure you want to add this to your favourites?")
				.setNegativeButton("Cancle",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								
							}
						})
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						final SharedPreferences settings = getSharedPreferences(
								"SAVED", 0);
						final SharedPreferences.Editor editor = settings.edit();
						int numberOfFavs = settings.getInt("numberOfFavs", 0);
						editor.putInt("numberOfFavs", numberOfFavs + 1);
						editor.putString(clicked.getColor(), "favourited");
						editor.commit();
					}
				}).show();
	}

	public void getPoints(final String id, final int senderId,
			final boolean finalDataset) {
		ServerInterface.get("AroundMe?" + id + "=YES", null,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(JSONObject response) {
						try {

							
							Boolean success;
							success = response.getBoolean("success");
							if (success == true) {
								JSONArray responselist = response
										.getJSONArray(id);

								for (int i = 0; i < responselist.length(); i++) {
									JSONObject jo = responselist
											.getJSONObject(i);

									MarkerData data = new MarkerData(
											new LatLng(jo.getDouble("lat"), jo
													.getDouble("lon")), jo
													.getString("pointname"), jo
													.getString("pointtype"), jo
													.getString("color"));

									InputPoint point = new InputPoint(data
											.getLatLng(), data);
									inputPoints.add(point);

								}

								selectedDatasets.put(id, "added");
								if (senderId == 0) {
									initMap();
									loading.dismiss();
								} else if (senderId == 1) {
									if (finalDataset == true) {
										aroundMe(null);
									} else {
										initMap();
										loading.dismiss();
									}
								} else if (senderId == 2) {
									if (finalDataset == true) {
										favourites(null);
									} else {
										initMap();
										loading.dismiss();
									}
								}
							}
						} catch (Exception e) {
							loading.dismiss();
							showErrorDialog();
						}

					}
				});
	}

	public void showErrorDialog() {
		new AlertDialog.Builder(this).setTitle("Sorry!")
				.setMessage("Something went wrong- please try again later")
				.setNegativeButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// do nothing
					}
				}).show();
	}

	@Override
	protected void onPause() {
		super.onPause();
		/**
		 * When pausing, we clear all of the clusterkraf's markers in order to
		 * conserve memory. When (if) we resume, we can rebuild from where we
		 * left off.
		 */
		if (clusterkraf != null) {
			clusterkraf.clear();
			clusterkraf = null;

		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		initMap();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (map != null) {
			CameraPosition cameraPosition = map.getCameraPosition();
			if (cameraPosition != null) {
				outState.putParcelable(KEY_CAMERA_POSITION, cameraPosition);
			}
		}
	}

	public void bringInDialog(MarkerData data) {

		clicked = data;
		dialogIn = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.dialog_slide_in_from_top);
		opacityMask.setVisibility(View.VISIBLE);

		TextView dialogtitle = (TextView) findViewById(R.id.dialogtitle);
		TextView dialoginfo = (TextView) findViewById(R.id.dialoginfo);
		dialogtitle.setText(data.getPointtype());

		dialoginfo.setText(data.getPointname());
		dialoginfo.setMovementMethod(new ScrollingMovementMethod());

		if (data.getColor().equalsIgnoreCase("red")) {
			ImageView dialogbk = (ImageView) findViewById(R.id.dialogbk);
			ImageView dialogtakemethere = (ImageView) findViewById(R.id.dialogtakemethere);
			ImageView dialogclose = (ImageView) findViewById(R.id.dialogclose);
			dialogbk.setImageResource(R.drawable.redlargepinbk);
			dialogtakemethere
					.setImageResource(R.drawable.redtakemetherebtn_press);
			dialogclose.setImageResource(R.drawable.closemarkerred_press);
		} else if ((data.getColor().equalsIgnoreCase("blue"))) {
			ImageView dialogbk = (ImageView) findViewById(R.id.dialogbk);
			ImageView dialogtakemethere = (ImageView) findViewById(R.id.dialogtakemethere);
			ImageView dialogclose = (ImageView) findViewById(R.id.dialogclose);
			dialogbk.setImageResource(R.drawable.bluelargepinbk);
			dialogtakemethere
					.setImageResource(R.drawable.bluetakemethere_press);
			dialogclose.setImageResource(R.drawable.closemarkerblue_press);
		} else if ((data.getColor().equalsIgnoreCase("yellow"))) {
			ImageView dialogbk = (ImageView) findViewById(R.id.dialogbk);
			ImageView dialogtakemethere = (ImageView) findViewById(R.id.dialogtakemethere);
			ImageView dialogclose = (ImageView) findViewById(R.id.dialogclose);
			dialogbk.setImageResource(R.drawable.yellowlargepinbk);
			dialogtakemethere
					.setImageResource(R.drawable.yellowtakemetherebtn_press);
			dialogclose.setImageResource(R.drawable.closemarkeryellow_press);
		} else if ((data.getColor().equalsIgnoreCase("orange"))) {
			ImageView dialogbk = (ImageView) findViewById(R.id.dialogbk);
			ImageView dialogtakemethere = (ImageView) findViewById(R.id.dialogtakemethere);
			ImageView dialogclose = (ImageView) findViewById(R.id.dialogclose);
			dialogbk.setImageResource(R.drawable.orangelargepinbk);
			dialogtakemethere.setImageResource(R.drawable.orangetakemetherebtn);
			dialogclose.setImageResource(R.drawable.closemarkerorangebtn);
		} else if ((data.getColor().equalsIgnoreCase("lime"))) {
			ImageView dialogbk = (ImageView) findViewById(R.id.dialogbk);
			ImageView dialogtakemethere = (ImageView) findViewById(R.id.dialogtakemethere);
			ImageView dialogclose = (ImageView) findViewById(R.id.dialogclose);
			dialogbk.setImageResource(R.drawable.limelargepinbk);
			dialogtakemethere.setImageResource(R.drawable.limetakemetherebtn);
			dialogclose.setImageResource(R.drawable.markerlimebtn);
		} else if ((data.getColor().equalsIgnoreCase("cyan"))) {
			ImageView dialogbk = (ImageView) findViewById(R.id.dialogbk);
			ImageView dialogtakemethere = (ImageView) findViewById(R.id.dialogtakemethere);
			ImageView dialogclose = (ImageView) findViewById(R.id.dialogclose);
			dialogbk.setImageResource(R.drawable.cyanlargepinbk);
			dialogtakemethere.setImageResource(R.drawable.cyantakemetherebtn);
			dialogclose.setImageResource(R.drawable.closemarkercyanbtn);
		} else if ((data.getColor().equalsIgnoreCase("purple"))) {
			ImageView dialogbk = (ImageView) findViewById(R.id.dialogbk);
			ImageView dialogtakemethere = (ImageView) findViewById(R.id.dialogtakemethere);
			ImageView dialogclose = (ImageView) findViewById(R.id.dialogclose);
			dialogbk.setImageResource(R.drawable.purplelargepinbk);
			dialogtakemethere.setImageResource(R.drawable.purpletakemetherebtn);
			dialogclose.setImageResource(R.drawable.closemarkerpurplebtn);
		} else if ((data.getColor().equalsIgnoreCase("pride"))) {
			ImageView dialogbk = (ImageView) findViewById(R.id.dialogbk);
			ImageView dialogtakemethere = (ImageView) findViewById(R.id.dialogtakemethere);
			ImageView dialogclose = (ImageView) findViewById(R.id.dialogclose);
			dialogbk.setImageResource(R.drawable.pridelargepinbk);
			dialogtakemethere.setImageResource(R.drawable.pridetakemetherebtn);
			dialogclose.setImageResource(R.drawable.closemarkerpridebtn);
		}
		else if ((data.getColor().equalsIgnoreCase("akaroa"))) {
			ImageView dialogbk = (ImageView) findViewById(R.id.dialogbk);
			ImageView dialogtakemethere = (ImageView) findViewById(R.id.dialogtakemethere);
			ImageView dialogclose = (ImageView) findViewById(R.id.dialogclose);
			dialogbk.setImageResource(R.drawable.akaroalargepinbk);
			dialogtakemethere.setImageResource(R.drawable.akaroatakemetherebtn);
			dialogclose.setImageResource(R.drawable.closemarkerakaroabtn);
		}
		else if ((data.getColor().equalsIgnoreCase("amber"))) {
			ImageView dialogbk = (ImageView) findViewById(R.id.dialogbk);
			ImageView dialogtakemethere = (ImageView) findViewById(R.id.dialogtakemethere);
			ImageView dialogclose = (ImageView) findViewById(R.id.dialogclose);
			dialogbk.setImageResource(R.drawable.amberlargepinbk);
			dialogtakemethere.setImageResource(R.drawable.ambertakemetherebtn);
			dialogclose.setImageResource(R.drawable.closemarkeramberbtn);
		}
		else if ((data.getColor().equalsIgnoreCase("amulet"))) {
			ImageView dialogbk = (ImageView) findViewById(R.id.dialogbk);
			ImageView dialogtakemethere = (ImageView) findViewById(R.id.dialogtakemethere);
			ImageView dialogclose = (ImageView) findViewById(R.id.dialogclose);
			dialogbk.setImageResource(R.drawable.amuletlargepinbk);
			dialogtakemethere.setImageResource(R.drawable.amulettakemetherebtn);
			dialogclose.setImageResource(R.drawable.closemarkeramuletbtn);
		}
		else if ((data.getColor().equalsIgnoreCase("beaver"))) {
			ImageView dialogbk = (ImageView) findViewById(R.id.dialogbk);
			ImageView dialogtakemethere = (ImageView) findViewById(R.id.dialogtakemethere);
			ImageView dialogclose = (ImageView) findViewById(R.id.dialogclose);
			dialogbk.setImageResource(R.drawable.beaverlargepinbk);
			dialogtakemethere.setImageResource(R.drawable.beavertakemetherebtn);
			dialogclose.setImageResource(R.drawable.closemarkerbeaverbtn);
		}

		dialogholder.startAnimation(dialogIn);
		dialogholder.setVisibility(View.VISIBLE);
		dialogIn.setAnimationListener(this);
	}

	public void closeDialog(View v) {
		opacityMask.setVisibility(View.GONE);
		dialogOut = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.dialog_slide_out);
		dialogOut.setAnimationListener(this);
		dialogholder.startAnimation(dialogOut);

	}

	private void initMap() {
		if (map == null) {
			SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map);
			if (mapFragment != null) {
				map = mapFragment.getMap();
				if (map != null) {
					map.setMyLocationEnabled(true);
					UiSettings uiSettings = map.getUiSettings();
					uiSettings.setAllGesturesEnabled(false);
					uiSettings.setScrollGesturesEnabled(true);
					uiSettings.setZoomGesturesEnabled(true);
					uiSettings.setMyLocationButtonEnabled(false);
					uiSettings.setCompassEnabled(false);
					map.setOnCameraChangeListener(new OnCameraChangeListener() {
						@Override
						public void onCameraChange(CameraPosition arg0) {
							moveMapCameraToBoundsAndInitClusterkraf();
						}
					});
				}
			}
		} else {
			moveMapCameraToBoundsAndInitClusterkraf();
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		currentLoc = new LatLng(location.getLatitude(), location.getLongitude());
		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
				currentLoc, 17);
		map.animateCamera(cameraUpdate);
		locationManager.removeUpdates(this);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	private void moveMapCameraToBoundsAndInitClusterkraf() {
		if (map != null && options != null && inputPoints != null) {
			try {

				initClusterkraf();
			} catch (IllegalStateException ise) {
				// no-op
			}
		}
	}

	private void initClusterkraf() {
		if (map != null && inputPoints != null && inputPoints.size() > 0) {
			
			applyDemoOptionsToClusterkrafOptions(optionstest);
			
			clusterkraf = new Clusterkraf(map, optionstest, inputPoints);
		}
	}

	/**
	 * Applies the sample.SampleActivity.Options chosen in Normal or Advanced
	 * mode menus to the clusterkraf.Options which will be used to construct our
	 * Clusterkraf instance
	 * 
	 * @param options
	 */
	private void applyDemoOptionsToClusterkrafOptions(
			com.twotoasters.clusterkraf.Options options) {
		options.setTransitionDuration(this.options.transitionDuration);

		/**
		 * this is probably not how you would set an interpolator in your own
		 * app. You would probably have just one that you wanted to hard code in
		 * your app (show me the mobile app user who actually wants to fiddle
		 * with the interpolator used in their animations), so you would do
		 * something more like `options.setInterpolator(new
		 * DecelerateInterpolator());` rather than mess around with reflection.
		 */
		Interpolator interpolator = null;
		try {
			interpolator = (Interpolator) Class.forName(
					this.options.transitionInterpolator).newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		options.setTransitionInterpolator(interpolator);

		/**
		 * Clusterkraf calculates whether InputPoint objects should join a
		 * cluster based on their pixel proximity. If you want to offer your app
		 * on devices with different screen densities, you should identify a
		 * Device Independent Pixel measurement and convert it to pixels based
		 * on the device's screen density at runtime.
		 */
		options.setPixelDistanceToJoinCluster(getPixelDistanceToJoinCluster());

		options.setZoomToBoundsAnimationDuration(this.options.zoomToBoundsAnimationDuration);
		options.setShowInfoWindowAnimationDuration(this.options.showInfoWindowAnimationDuration);
		options.setExpandBoundsFactor(this.options.expandBoundsFactor);
		options.setSinglePointClickBehavior(this.options.singlePointClickBehavior);
		options.setClusterClickBehavior(this.options.clusterClickBehavior);
		options.setClusterInfoWindowClickBehavior(this.options.clusterInfoWindowClickBehavior);

		/**
		 * Device Independent Pixel measurement should be converted to pixels
		 * here too. In this case, we cheat a little by using a Drawable's
		 * height. It's only cheating because we don't offer a variant for that
		 * Drawable for every density (xxhdpi, tvdpi, others?).
		 */
		options.setZoomToBoundsPadding(getResources().getDrawable(
				R.drawable.ic_map_pin_cluster).getIntrinsicHeight());

		options.setMarkerOptionsChooser(new ToastedMarkerOptionsChooser(this,
				inputPoints.get(0)));
		options.setOnMarkerClickDownstreamListener(new ToastedOnMarkerClickDownstreamListener(
				this));
		options.setProcessingListener(this);
	}

	private int getPixelDistanceToJoinCluster() {
		return convertDeviceIndependentPixelsToPixels(this.options.dipDistanceToJoinCluster);
	}

	private int convertDeviceIndependentPixelsToPixels(int dip) {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		return Math.round(displayMetrics.density * dip);
	}

	static class Options implements Serializable {

		private static final long serialVersionUID = 7492713360265465944L;

		// clusterkraf library options
		int transitionDuration = 500;
		String transitionInterpolator = LinearInterpolator.class
				.getCanonicalName();
		int dipDistanceToJoinCluster = 100;
		int zoomToBoundsAnimationDuration = 500;
		int showInfoWindowAnimationDuration = 500;
		double expandBoundsFactor = 0.5d;
		SinglePointClickBehavior singlePointClickBehavior = SinglePointClickBehavior.SHOW_INFO_WINDOW;
		ClusterClickBehavior clusterClickBehavior = ClusterClickBehavior.ZOOM_TO_BOUNDS;
		ClusterInfoWindowClickBehavior clusterInfoWindowClickBehavior = ClusterInfoWindowClickBehavior.ZOOM_TO_BOUNDS;
	}

	@Override
	public void onClusteringStarted() {
	
	}

	@Override
	public void onClusteringFinished() {
		
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		if (animation == dialogOut) {
			dialogholder.setVisibility(View.GONE);

			opacityMask.setVisibility(View.GONE);
		}

	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub

	}

}
