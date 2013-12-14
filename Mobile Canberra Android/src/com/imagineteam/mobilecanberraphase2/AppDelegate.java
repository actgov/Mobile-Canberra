package com.imagineteam.mobilecanberraphase2;

import java.util.ArrayList;

import com.imagineteam.dataobjects.DatasetListObject;

import android.app.Application;

/**
 * This is a global POJO that we attach data to which we want to use across the
 * application
 * 
 */
public class AppDelegate extends Application {
	private ArrayList<DatasetListObject> datasets;

	public ArrayList<DatasetListObject> getDatasets() {
		return datasets;
	}

	public void setDatasets(ArrayList<DatasetListObject> datasets) {
		this.datasets = datasets;
	}
}
