package com.example.bluetooth.le.fragment;

import java.util.List;

import com.example.bluetooth.le.MainActivity;
import com.example.bluetooth.le.R;
import com.example.bluetooth.le.SelectTagAdpter;
import com.example.bluetooth.le.TagInfo;
import com.example.bluetooth.le.Util;
import com.jiuray.uhf.command.Tools;
import com.jiuray.uhf.command.UhfCommandHelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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
	
	/*标签列表*/
	private List<TagInfo> listTag ;
	
	/*选中的标签*/
	private TagInfo selectTag ;
	
	/*数据区*/
	private int membank ;
	/*起始地址*/
	private int addr ;
	/*长度*/
	private int len ;
	/*访问密码*/
	private byte[] password ;
	
	
	/** 用于接收主界面返回的数据*/
	private BroadcastReceiver receiver = new BroadcastReceiver(){
		public void onReceive(Context context, android.content.Intent intent) {
			String action = intent.getAction() ;
			String data = intent.getStringExtra("data") ;
			if(MainActivity.ACTION_BLE_RECV_DATA.equals(action)){
//              TagInfo tag = resolveEPCdata(data) ;
//              if(tag != null){
//              	Util.play(1, 0);
//              	//更新列表
//              	addList(tag);
//              }
			}
		};
	};
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
		String[] memArrays = mActivity.getResources().getStringArray(R.array.membank_list) ;
		spData.setAdapter(new ArrayAdapter<String>(mActivity, 
				android.R.layout.simple_spinner_dropdown_item,memArrays));
		//默认选择USER区
		spData.setSelection(3);
		UhfCommandHelper helper = new UhfCommandHelper() ;
		
		helper.read(3, 0, 1, Tools.HexString2Bytes("00000000"));
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
