package com.imagineteam.dataobjects;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.content.Intent;

import com.google.android.gms.maps.model.Marker;
import com.imagineteam.mobilecanberraphase2.MapActivity;
import com.twotoasters.clusterkraf.ClusterPoint;
import com.twotoasters.clusterkraf.OnMarkerClickDownstreamListener;

public class ToastedOnMarkerClickDownstreamListener implements OnMarkerClickDownstreamListener {

	private final MapActivity context;

	public ToastedOnMarkerClickDownstreamListener(MapActivity context) {
		this.context =context;
	}

	@Override
	public boolean onMarkerClick(Marker marker, ClusterPoint clusterPoint) {
		
		if (context != null && marker != null && clusterPoint != null && clusterPoint.size() == 1
				) {
			/*Intent i = new Intent(context, TwoToastersActivity.class);
			context.startActivity(i);*/
			MarkerData test = (MarkerData)clusterPoint.getPointAtOffset(0).getTag();
			
			context.bringInDialog(test);
			return true;
		}
		return false;
	}

}
