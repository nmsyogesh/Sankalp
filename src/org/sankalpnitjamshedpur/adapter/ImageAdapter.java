package org.sankalpnitjamshedpur.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
	private Context mContext;

	// Keep all Images in array
	public ArrayList<Bitmap> pictureList;

	// Constructor
	public ImageAdapter(Context c, ArrayList<Uri> pictureList) {
		mContext = c;
		// bitmap factory
		BitmapFactory.Options options = new BitmapFactory.Options();
		this.pictureList = new ArrayList<Bitmap>();
		// downsizing image as it throws OutOfMemory Exception for larger
		// images
		options.inSampleSize = 8;
		if (pictureList != null && pictureList.size() != 0) {
			for (Uri picUri : pictureList) {
				this.pictureList.add(BitmapFactory.decodeFile(picUri.getPath(),
						options));
			}
		}
	}

	@Override
	public int getCount() {
		return pictureList.size();
	}

	@Override
	public Object getItem(int position) {
		return (pictureList.size() != 0) ? pictureList.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView = new ImageView(mContext);
		if (getItem(position) == null)
			return null;
		imageView.setImageBitmap((Bitmap) getItem(position));
		imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
		imageView.setLayoutParams(new GridView.LayoutParams(170, 170));
		return imageView;
	}

}