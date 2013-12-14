package com.imagineteam.dataobjects;

import java.util.ArrayList;

import com.imagineteam.mobilecanberraphase2.R;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

public class MapGalleryAdapter extends BaseAdapter {
	private final Context ctx;
	private final ArrayList<DatasetListObject> datasets;
	LayoutInflater inflater;

	public MapGalleryAdapter(final Context ctx, final ArrayList<DatasetListObject> datasets) {
		this.ctx = ctx;
		this.datasets = datasets;
		inflater = (LayoutInflater) ctx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	@Override
	public int getCount() {

		return datasets.size();
	}

	@Override
	public DatasetListObject getItem(final int position) {

		return datasets.get(position);
	}

	@Override
	public long getItemId(final int position) {

		return position;
	}

	public static class ViewHolder {

		TextView datasetName;
		ImageView datasetColor;
	}

	@Override
	public View getView(final int position, View convertView,
			final ViewGroup arg2) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.mapgallery_item, null,
					false);

			holder.datasetName = (TextView) convertView.findViewById(R.id.datasetrownamemap);
			holder.datasetColor = (ImageView) convertView.findViewById(R.id.datasetcolormap);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		final Typeface mFont = Typeface.createFromAsset(ctx.getAssets(),
				"fonts/Ubuntu-Medium.ttf");
		holder.datasetName.setText(datasets.get(position).getName());
		holder.datasetName.setTypeface(mFont);
	
		if (datasets.get(position).getColor().equalsIgnoreCase("red")){
			GradientDrawable bgShape = (GradientDrawable )holder.datasetColor.getBackground();
			bgShape.setColor(Color.RED);
		}
		else if (datasets.get(position).getColor().equalsIgnoreCase("blue")){
			GradientDrawable bgShape = (GradientDrawable )holder.datasetColor.getBackground();
			bgShape.setColor(Color.BLUE);
		}
		else if (datasets.get(position).getColor().equalsIgnoreCase("yellow")){
			GradientDrawable bgShape = (GradientDrawable )holder.datasetColor.getBackground();
			bgShape.setColor(Color.YELLOW);
		}
		else if (datasets.get(position).getColor().equalsIgnoreCase("orange")){
			GradientDrawable bgShape = (GradientDrawable )holder.datasetColor.getBackground();
			bgShape.setColor(Color.argb(255,253, 149, 65));
		}
		else if (datasets.get(position).getColor().equalsIgnoreCase("lime")){
			GradientDrawable bgShape = (GradientDrawable )holder.datasetColor.getBackground();
			bgShape.setColor(Color.argb(255,120, 254, 11));
		}
		else if (datasets.get(position).getColor().equalsIgnoreCase("cyan")){
			GradientDrawable bgShape = (GradientDrawable )holder.datasetColor.getBackground();
			bgShape.setColor(Color.argb(255,11, 254, 228));
		}
		else if (datasets.get(position).getColor().equalsIgnoreCase("purple")){
			GradientDrawable bgShape = (GradientDrawable )holder.datasetColor.getBackground();
			bgShape.setColor(Color.argb(255,3, 95, 147));
		}
		else if (datasets.get(position).getColor().equalsIgnoreCase("pride")){
			GradientDrawable bgShape = (GradientDrawable )holder.datasetColor.getBackground();
			bgShape.setColor(Color.argb(255,242, 65, 253));
		}
		else if (datasets.get(position).getColor().equalsIgnoreCase("akaroa")){
			GradientDrawable bgShape = (GradientDrawable )holder.datasetColor.getBackground();
			bgShape.setColor(Color.argb(255,212, 196, 168));
		}
		else if (datasets.get(position).getColor().equalsIgnoreCase("amber")){
			GradientDrawable bgShape = (GradientDrawable )holder.datasetColor.getBackground();
			bgShape.setColor(Color.argb(255,255, 191, 0));
		}
		else if (datasets.get(position).getColor().equalsIgnoreCase("amulet")){
			GradientDrawable bgShape = (GradientDrawable )holder.datasetColor.getBackground();
			bgShape.setColor(Color.argb(255,123, 159, 128));
		}
		else if (datasets.get(position).getColor().equalsIgnoreCase("beaver")){
			GradientDrawable bgShape = (GradientDrawable )holder.datasetColor.getBackground();
			bgShape.setColor(Color.argb(255,146, 111, 91));
		}
		
		return convertView;
	}
	
	
	

}
