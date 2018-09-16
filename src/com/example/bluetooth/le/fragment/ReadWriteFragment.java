package com.example.bluetooth.le.fragment;

import java.util.List;

import com.example.bluetooth.le.MainActivity;
import com.example.bluetooth.le.R;
import com.example.bluetooth.le.SelectTagAdpter;
import com.example.bluetooth.le.TagInfo;
import com.example.bluetooth.le.Util;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * 读写界面
 */
public class ReadWriteFragment extends Fragment implements OnClickListener{

	
	private Context mContext ;
	/*主界面提供一些公用的变量和函数*/
	private MainActivity mActivity ;
	
	/*选择标签*/
	private Spinner spSelectTag ;
	/*选择数据区*/
	private Spinner spData ;
	/*访问密码*/
	private EditText editPassword ;
	/*起始地址*/
	private EditText editAddr ;
	/*读取数据长度*/
	private EditText editLen ;
	/*写入数据*/
	private EditText editWriteData ;
	/*读取数据*/
	private EditText editReadData ;
	/*读按键*/
	private Button btnRead ;
	/*写按键*/
	private Button btnWrite ;
	
	private List<TagInfo> listTag ;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.read_write_fragment, null);
		spSelectTag = (Spinner) view.findViewById(R.id.spinner_select_tag) ;
		spData = (Spinner) view.findViewById(R.id.spinner_data) ;
		editPassword = (EditText) view.findViewById(R.id.editText_password) ;
		editAddr = (EditText) view.findViewById(R.id.editText_addr) ;
		editLen = (EditText) view.findViewById(R.id.editText_len) ;
		editWriteData = (EditText) view.findViewById(R.id.editText_read) ;
		editReadData = (EditText) view.findViewById(R.id.editText_write) ;
		btnRead = (Button) view.findViewById(R.id.button_read) ;
		btnWrite = (Button) view.findViewById(R.id.button_write) ;
		mActivity = (MainActivity) getActivity() ;
		mContext = mActivity ;
		btnRead.setOnClickListener(this);
		btnWrite.setOnClickListener(this);
		Util.initSoundPool(mActivity);
		listTag = mActivity.getListTag(); 
		if(listTag != null){
			spSelectTag.setAdapter(new SelectTagAdpter(mActivity, listTag));
		}

		return view;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.button_read://读标签
			
			break;
			
		case R.id.button_write://写标签
			
			break;

		default:
			break;
		}
		
	}
}