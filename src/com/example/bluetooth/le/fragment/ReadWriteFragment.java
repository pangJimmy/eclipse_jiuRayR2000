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
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
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
	private byte[] password = Tools.HexString2Bytes("00000000");
	private byte[] epc  ;
	
	private String TAG = "ReadWriteFragment" ;
	/** 用于接收主界面返回的数据*/
	private BroadcastReceiver receiver = new BroadcastReceiver(){
		public void onReceive(Context context, android.content.Intent intent) {
			String action = intent.getAction() ;
			String data = intent.getStringExtra("data") ;
			if(MainActivity.ACTION_BLE_RECV_DATA.equals(action)){
				
				Log.e(TAG, "data = " + data) ; 
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
		membank = 3 ;
		spData.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				membank = arg2 ;
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		epc = Tools.HexString2Bytes(listTag.get(0).getEpc()) ;
		spSelectTag.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				//选择的EPC
				epc = Tools.HexString2Bytes(listTag.get(arg2).getEpc()) ;
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		UhfCommandHelper helper = new UhfCommandHelper() ;
		
//		helper.read(1, 0, 4, Tools.HexString2Bytes("00000000"));
		helper.matchByEPC(1, Tools.HexString2Bytes("201805160005370295FFFFFF")) ;
		
		IntentFilter filter = new IntentFilter() ;
		filter.addAction(MainActivity.ACTION_BLE_RECV_DATA);
		mActivity.registerReceiver(receiver, filter) ;
		return view;
	}
	
//	@Override
//	public void onResume() {
//		Log.e("onResume", "onResume") ;
//		//重新加载时的操作
//		IntentFilter filter = new IntentFilter() ;
//		filter.addAction(MainActivity.ACTION_BLE_RECV_DATA);
//		mActivity.registerReceiver(receiver, filter) ;
//		
//		super.onResume();
//	}
	
//	@Override
//	public void onPause() {
//		//切换和暂停时的操作
//		
//		mActivity.unregisterReceiver(receiver);
//		super.onPause();
//	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if(hidden){
			//切换和暂停时的操作
			mActivity.unregisterReceiver(receiver);
		}else{
			//重新加载时的操作
			IntentFilter filter = new IntentFilter() ;
			filter.addAction(MainActivity.ACTION_BLE_RECV_DATA);
			mActivity.registerReceiver(receiver, filter) ;
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.button_read://读标签
			byte[] readCmdByte = mActivity.uhfHelper.read(1, 0, 4, Tools.HexString2Bytes("00000000"));
			String readCmd = Tools.Bytes2HexString(readCmdByte, readCmdByte.length) ;
			mActivity.getBLEService().txxx(readCmd); 
			//temp-->FA21FFEC000118341100002B7E1770567817705CB50BC70BC7341100002B7E085C010F
			break;
			
		case R.id.button_write://写标签
			byte[] selectByte= mActivity.uhfHelper.matchByEPC(0, epc) ;
			mActivity.getBLEService().txxx(Tools.Bytes2HexString(selectByte, selectByte.length)); 
			break;

		default:
			break;
		}
		
	}
}
