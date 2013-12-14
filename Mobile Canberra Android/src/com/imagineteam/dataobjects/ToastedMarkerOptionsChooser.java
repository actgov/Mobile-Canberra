package com.imagineteam.dataobjects;

import java.lang.ref.WeakReference;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.imagineteam.mobilecanberraphase2.R;
import com.twotoasters.clusterkraf.ClusterPoint;
import com.twotoasters.clusterkraf.InputPoint;
import com.twotoasters.clusterkraf.MarkerOptionsChooser;

public class ToastedMarkerOptionsChooser extends MarkerOptionsChooser {

	private final WeakReference<Context> contextRef;
	private final InputPoint twoToasters;
	private final Paint clusterPaintLarge;
	private final Paint clusterPaintMedium;
	private final Paint clusterPaintSmall;

	public ToastedMarkerOptionsChooser(Context context, InputPoint twoToasters) {
		this.contextRef = new WeakReference<Context>(context);
		this.twoToasters = twoToasters;

		Resources res = context.getResources();

		clusterPaintMedium = new Paint();
		clusterPaintMedium.setColor(Color.WHITE);
		clusterPaintMedium.setAlpha(255);
		clusterPaintMedium.setTextAlign(Paint.Align.CENTER);
		// clusterPaintMedium.setTypeface(Typeface.create(Typeface.SANS_SERIF,
		// Typeface.BOLD_ITALIC));
		clusterPaintMedium.setTextSize(res
				.getDimension(R.dimen.cluster_text_size_medium));

		clusterPaintSmall = new Paint(clusterPaintMedium);
		clusterPaintSmall.setTextSize(res
				.getDimension(R.dimen.cluster_text_size_small));

		clusterPaintLarge = new Paint(clusterPaintMedium);
		clusterPaintLarge.setTextSize(res
				.getDimension(R.dimen.cluster_text_size_large));
	}

	@Override
	public void choose(MarkerOptions markerOptions, ClusterPoint clusterPoint) {
		Context context = contextRef.get();
		if (context != null) {
			Resources res = context.getResources();
			boolean isCluster = clusterPoint.size() > 1;
			boolean hasTwoToasters = clusterPoint
					.containsInputPoint(twoToasters);
			BitmapDescriptor icon;
			String title;
			if (isCluster) {
				title = res.getQuantityString(R.plurals.count_points,
						clusterPoint.size(), clusterPoint.size());
				int clusterSize = clusterPoint.size();

				icon = BitmapDescriptorFactory.fromBitmap(getClusterBitmap(res,
						R.drawable.ic_map_pin_cluster, clusterSize));
				title = res.getQuantityString(R.plurals.count_points,
						clusterSize, clusterSize);

			} else {
				MarkerData data = (MarkerData) clusterPoint.getPointAtOffset(0)
						.getTag();
				icon = BitmapDescriptorFactory.fromResource(R.drawable.redpin);
				if (data.getColor().equalsIgnoreCase("red")) {
					icon = BitmapDescriptorFactory
							.fromResource(R.drawable.redpin);
				} else if (data.getColor().equalsIgnoreCase("blue")) {
					icon = BitmapDescriptorFactory
							.fromResource(R.drawable.bluepin);
				} else if (data.getColor().equalsIgnoreCase("yellow")) {
					icon = BitmapDescriptorFactory
							.fromResource(R.drawable.yellowpin);
				}
				else if (data.getColor().equalsIgnoreCase("orange")) {
					icon = BitmapDescriptorFactory
							.fromResource(R.drawable.orangepin);
				}
				else if (data.getColor().equalsIgnoreCase("lime")) {
					icon = BitmapDescriptorFactory
							.fromResource(R.drawable.limepin);
				}
				else if (data.getColor().equalsIgnoreCase("cyan")) {
					icon = BitmapDescriptorFactory
							.fromResource(R.drawable.cyanpin);
				}
				else if (data.getColor().equalsIgnoreCase("purple")) {
					icon = BitmapDescriptorFactory
							.fromResource(R.drawable.purplepin);
				}
				else if (data.getColor().equalsIgnoreCase("pride")) {
					icon = BitmapDescriptorFactory
							.fromResource(R.drawable.pridepin);
				}
				else if (data.getColor().equalsIgnoreCase("akaroa")) {
					icon = BitmapDescriptorFactory
							.fromResource(R.drawable.akaroapin);
				}
				else if (data.getColor().equalsIgnoreCase("amber")) {
					icon = BitmapDescriptorFactory
							.fromResource(R.drawable.amberpin);
				}
				else if (data.getColor().equalsIgnoreCase("amulet")) {
					icon = BitmapDescriptorFactory
							.fromResource(R.drawable.amuletpin);
				}
				else if (data.getColor().equalsIgnoreCase("beaver")) {
					icon = BitmapDescriptorFactory
							.fromResource(R.drawable.beaverpin);
				}
				title = data.getPointname();

			}
			markerOptions.icon(icon);
			markerOptions.title(title);
			markerOptions.anchor(0.5f, 1.0f);
		}
	}

	@SuppressLint("NewApi")
	private Bitmap getClusterBitmap(Resources res, int resourceId,
			int clusterSize) {
		BitmapFactory.Options options = new BitmapFactory.Options();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			options.inMutable = true;
		}
		Bitmap bitmap = BitmapFactory.decodeResource(res, resourceId, options);
		if (bitmap.isMutable() == false) {
			bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
		}

		Canvas canvas = new Canvas(bitmap);

		Paint paint = null;
		float originY;
		if (clusterSize < 100) {
			paint = clusterPaintLarge;
			originY = bitmap.getHeight() * 0.64f;
		} else if (clusterSize < 1000) {
			paint = clusterPaintMedium;
			originY = bitmap.getHeight() * 0.6f;
		} else {
			paint = clusterPaintSmall;
			originY = bitmap.getHeight() * 0.56f;
		}

		canvas.drawText(String.valueOf(clusterSize), bitmap.getWidth() * 0.5f,
				originY, paint);

		return bitmap;
	}
}