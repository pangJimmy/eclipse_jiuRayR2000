package com.example.bluetooth.le;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SelectTagAdpter extends BaseAdapter {

	private List<TagInfo> list ;
	private Context context ;
	
	public SelectTagAdpter(Context context , List<TagInfo> list){
		this.list = list ;
		this.context = context ;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convert, ViewGroup arg2) {
		ViewHolder holder ;
		if(convert == null){
			convert = LayoutInflater.from(context).inflate(R.layout.select_tag_item, null) ;
			holder = new ViewHolder() ;
			holder.tvTag = (TextView) convert.findViewById(R.id.textView_select) ;
			convert.setTag(holder);
		}else{
			holder = (ViewHolder) convert.getTag() ;
		}
		holder.tvTag.setText(list.get(position).getEpc());
		return convert;
	}
	
	private class ViewHolder{
		TextView tvTag ;
	}

}
