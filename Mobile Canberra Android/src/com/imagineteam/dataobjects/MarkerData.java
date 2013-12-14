package com.imagineteam.dataobjects;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

/**
 *
 */
public class MarkerData implements Parcelable {

	private final LatLng latLng;
	private final String pointname;
	private final String pointtype;
	private final String color;
	

	public MarkerData(LatLng latLng, String pointname, String pointtype, String color) {
		this.latLng = latLng;
		this.pointname = pointname;
		this.pointtype = pointtype;
		this.color= color;
	}

	/**
	 * @return the latLng
	 */
	public LatLng getLatLng() {
		return latLng;
	}

	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		dest.writeParcelable(latLng, 0);
		dest.writeString(pointname);
		dest.writeString(pointtype);
		dest.writeString(color);
	}

	public String getPointname() {
		return pointname;
	}

	public String getPointtype() {
		return pointtype;
	}

	public String getColor() {
		return color;
	}

}
