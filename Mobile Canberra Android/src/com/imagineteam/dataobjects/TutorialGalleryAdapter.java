package com.imagineteam.dataobjects;

import java.util.ArrayList;

import com.imagineteam.mobilecanberraphase2.R;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

public class TutorialGalleryAdapter extends BaseAdapter {
	private final Context ctx;
	private final ArrayList<String> titles;
	private final ArrayList<String> content;
	private final ArrayList<Integer> ids;
	LayoutInflater inflater;

	public TutorialGalleryAdapter(final Context ctx, final ArrayList<String> titles,  final ArrayList<String> content, final ArrayList<Integer> ids) {
		this.ctx = ctx;
		this.titles = titles;
		this.content = content;
		this.ids = ids;
		inflater = (LayoutInflater) ctx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {

		return titles.size();
	}

	@Override
	public String getItem(final int position) {

		return titles.get(position);
	}

	@Override
	public long getItemId(final int position) {

		return position;
	}

	public static class ViewHolder {

		TextView titlecontent;
		ImageView titleimagetutorial;
		TextView titletext;
	}

	@Override
	public View getView(final int position, View convertView,
			final ViewGroup arg2) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.tutorialgallery_item, null,
					false);
			holder.titlecontent = (TextView) convertView.findViewById(R.id.titlecontent);
			holder.titletext = (TextView) convertView.findViewById(R.id.titletext);
			holder.titleimagetutorial = (ImageView) convertView.findViewById(R.id.titleimagetutorial);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		final Typeface mFont = Typeface.createFromAsset(ctx.getAssets(),
				"fonts/Ubuntu-Medium.ttf");
		holder.titlecontent.setText(content.get(position));
		holder.titletext.setText(titles.get(position));
		holder.titlecontent.setMovementMethod(new ScrollingMovementMethod());
		holder.titletext.setTypeface(mFont);
		holder.titlecontent.setTypeface(mFont);
		if (ids.get(position)!=0){
			holder.titleimagetutorial.setImageDrawable(ctx.getResources().getDrawable(ids.get(position)));
		}
		
		return convertView;
	}
	
	
	

}
