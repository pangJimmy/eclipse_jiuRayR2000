package com.example.bluetooth.le.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.example.bluetooth.le.DeviceControlActivity;
import com.example.bluetooth.le.MainActivity;
import com.example.bluetooth.le.R;
import com.example.bluetooth.le.TagInfo;
import com.example.bluetooth.le.Tools;
import com.example.bluetooth.le.Util;

import android.support.v4.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 盘存界面
 * @author 
 *
 */
public class InventoryFragment extends Fragment implements OnClickListener{
	
	/*listview 显示标签EPC信息*/
	private ListView lv ;
	private TextView tvCount ;
	private Button btnStart ;
	private Button btnClear ;
	
	private List<TagInfo> listTag = new ArrayList<TagInfo>();
	private List<Map<String, Object>> listMap ;
	private Context mContext ;
	/*主界面提供一些公用的变量和函数*/
	private MainActivity mActivity ;


	/** 用于接收主界面返回的数据*/
	private BroadcastReceiver receiver = new BroadcastReceiver(){
		public void onReceive(Context context, android.content.Intent intent) {
			String action = intent.getAction() ;
			String data = intent.getStringExtra("data") ;
			if(MainActivity.ACTION_BLE_RECV_DATA.equals(action)){
              TagInfo tag = resolveEPCdata(data) ;
              if(tag != null){
              	Util.play(1, 0);
              	//更新列表
              	addList(tag);
              }
			}
		};
	};
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.inventory_fragment, null);
		lv = (ListView) view.findViewById(R.id.lv_epc) ;
		btnStart = (Button) view.findViewById(R.id.tx_button) ;
		btnClear = (Button) view.findViewById(R.id.clear_button) ;
		tvCount = (TextView) view.findViewById(R.id.tv_tag_count) ;
		mActivity = (MainActivity) getActivity() ;
		mContext = mActivity ;
		btnStart.setOnClickListener(this);
		btnClear.setOnClickListener(this);
		Util.initSoundPool(mActivity);
		
		Log.e("onResume", "onResume") ;
		//重新加载时的操作
		IntentFilter filter = new IntentFilter() ;
		filter.addAction(MainActivity.ACTION_BLE_RECV_DATA);
		mActivity.registerReceiver(receiver, filter) ;
		
		isRunning = true;
	    isStart = false ;
	    //启动盘存线程
	    new Thread(inventoryTask).start(); 
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
//		isRunning = true;
//	    isStart = false ;
//	    //启动盘存线程
//	    new Thread(inventoryTask).start(); 
//		super.onResume();
//	}
	
//	@Override
//	public void onPause() {
//		Log.e("onPause", "onPause") ;
//		//切换和暂停时的操作
//		isStart = false ;
//		btnStart.setText("开始");
//		
//		mActivity.unregisterReceiver(receiver);
//		super.onPause();
//	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		Log.e("onHiddenChanged", "onHiddenChanged = " + hidden) ;
		if(hidden){

		    
			//切换和暂停时的操作
			isStart = false ;
			btnStart.setText("开始");
			
			mActivity.unregisterReceiver(receiver);
		}else{
			//重新加载时的操作
			IntentFilter filter = new IntentFilter() ;
			filter.addAction(MainActivity.ACTION_BLE_RECV_DATA);
			mActivity.registerReceiver(receiver, filter) ;
			
			isRunning = true;
		    isStart = false ;
		    //启动盘存线程
		    new Thread(inventoryTask).start(); 
		}
	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.tx_button://启动控制
    		if( mActivity.getConnectStatus() ){
        		if(isStart){
        			isStart = false ;
        			btnStart.setText("开始");
        		}else{
        			btnStart.setText("停止");
        			isStart = true ;
        		}
  		  }else{
  			  //Toast.makeText(this, "Deleted Successfully!", Toast.LENGTH_LONG).show(); 
  			  Toast toast = Toast.makeText(mActivity, "设备没有连接！", Toast.LENGTH_SHORT); 
  			  toast.show(); 
  		  }
			break;
		case R.id.clear_button://清空
    		tvCount.setText("");
    		listTag = new ArrayList<TagInfo>();
    		setTag = new HashSet<String>();
    		lv.setAdapter(null);
			break;

		default:
			break;
		}
		
	}
	
    private boolean isRunning = true;
    private boolean isStart = false ;
    /**
     * 盘存线程
     */
    private Runnable inventoryTask = new Runnable() {
		
		@Override
		public void run() {
			while(isRunning){
				if(isStart){
					
	        		if( mActivity.getConnectStatus() )
	        		  {
	        			//实时盘存指令
	              		String tx_string="AA04FFEF0163";
	              		mActivity.getBLEService().txxx(tx_string);
	        		  }else{
	        			  //Toast.makeText(this, "Deleted Successfully!", Toast.LENGTH_LONG).show(); 
//	        			  Toast toast = Toast.makeText(DeviceControlActivity.this, "设备没有连接！", Toast.LENGTH_SHORT); 
//	        			  toast.show(); 
	        		  }
	        		
	        		try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
			
		}
	};
	////////////////////////////////
	
	//标签集合
	private Set<String> setTag = new HashSet<String>() ;
	/**
	 * 将标签加入列表
	 */
	private void addList(TagInfo tag){
		int count = 1;
		TagInfo mmTag = new TagInfo();
		if(setTag.isEmpty()){
			
			//首次添加

			mmTag.setCount(count);
			mmTag.setRssi(tag.getRssi());
			mmTag.setPc(tag.getPc());
			mmTag.setEpc(tag.getEpc());
			
			setTag.add(mmTag.getEpc());
			listTag.add(mmTag) ;
		}else{
			if(setTag.contains(tag.getEpc())){
				//集合中包含
//				count += tag.getCount() ;
//				tag.setCount(count);
			}else{
				//集合中没有
				//首次添加
				mmTag.setCount(count);
				mmTag.setRssi(tag.getRssi());
				mmTag.setPc(tag.getPc());
				mmTag.setEpc(tag.getEpc());
				
				setTag.add(mmTag.getEpc());
				listTag.add(mmTag) ;
			}
		}
		//设置标签列表
    	mActivity.setListTag(listTag);
    	
    	listMap = new ArrayList<Map<String,Object>>();
    	for( int i = 0 ; i < listTag.size(); i++ ){
    		TagInfo mTag = listTag.get(i) ;
    		if(mTag.getEpc().equals(tag.getEpc())){
    			mTag.setCount((count+mTag.getCount()));
    			mTag.setRssi(tag.getRssi());
//    			listTag.set(i, mTag) ;
    		}
    		Map<String, Object> map = new HashMap<String, Object>() ;
    		map.put("id", i) ;
    		map.put("epc", mTag.getEpc()) ;
    		map.put("pc", mTag.getPc()) ;
    		map.put("rssi", mTag.getRssi()) ;
    		map.put("cnt", mTag.getCount()) ;
    		listMap.add(map) ;
    		
    	}
    	//更新次数
    	tvCount.setText("" + setTag.size());
    	//刷新列表
    	lv.setAdapter(new SimpleAdapter(mContext,
				listMap, R.layout.item_tag, new String[] { 
						"id", "epc","pc","rssi","cnt"  }, new int[] {
						R.id.tv_id, R.id.tv_epc,
						R.id.tv_pc,R.id.tv_rssi, R.id.tv_cnt }));
    
	}
		
	
	////////////////////////////////
	byte START = (byte )0xFA ;
	byte ADDR = (byte )0xFF ;
	//用于存储临时数据，当数据接收不完整时
	private StringBuilder sb = new StringBuilder() ;
	private StringBuffer tempData = new StringBuffer();
	private TagInfo resolveEPCdata(String data){
		tempData.append(data) ;
		Log.e("D", "temp-->" + tempData.toString());
		TagInfo tag = null ;
		byte[] epcBytes = Tools.HexString2Bytes(tempData.toString()) ;
		int len ;
		if(epcBytes != null && epcBytes.length > 5){
			//判断起始位FA
			if(START != epcBytes[0]){
				tempData = new StringBuffer();
				return tag;
			}
			//长度
			len = epcBytes[1]&0xff  ;
			//判断指令是否完整接收,(完整指令长度为len + 2)
			if(epcBytes.length < (len + 2)){
				
				return tag;
			}
			//取出完整数据
			byte[] realData = new byte[len + 2] ;
			//将完整数据拷出来
			System.arraycopy(epcBytes, 0, realData, 0, realData.length);
			//先清空buffer,剩下数据继续存在buffer中
			tempData = new StringBuffer();
			if((epcBytes.length - len -2) != 0){
				byte[] mData = new byte[epcBytes.length - len -2] ;
				System.arraycopy(epcBytes, len + 2, mData, 0, mData.length) ;
				tempData.append(Tools.Bytes2HexString(mData, mData.length));
			}
			
			//是否是盘存指令
			if(realData[3] != (byte)0xEF ){
				
				return tag ;
			}
			//校验位
			byte crc = Tools.checkSum(realData, 0, realData.length - 1) ;
			if(crc != realData[realData.length - 1]){
				
				return tag;
			}
			Log.e("D", "real--->" + Tools.Bytes2HexString(realData, realData.length));
			//取数据
			//有标签FA 13 FF EF D8 34 00 20 18 05 16 00 05 37 02 95 FF FF FF 67 6F
			//无标签FA 0A FF EF 00 00 00 00 00 00 00 0E
			if(len > 10){
				tag = new TagInfo() ;
				byte[] pc = {realData[5] , realData[6]} ;
				byte[] epc = new byte[len - 7] ;
				System.arraycopy(realData, 7, epc, 0, epc.length);
				tag.setPc(Tools.Bytes2HexString(pc, pc.length));
				tag.setEpc(Tools.Bytes2HexString(epc, epc.length));
				int rssi = (realData[realData.length - 2] - 129) ;
				tag.setRssi(rssi);
				//Log.e("D", "rssi--->" + rssi);
			}
		}
		
		return tag ;
	}



	//更新数据
	class Madapter extends BaseAdapter{
		
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listTag.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return listTag.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int arg0, View view, ViewGroup arg2) {
			ViewHolder holder ;
			if(view == null){
				view = LayoutInflater.from(mContext).inflate(R.layout.item_tag, null) ;
				holder = new ViewHolder() ;
				holder.tvID = (TextView) view.findViewById(R.id.tv_id) ;
				holder.tvEpc = (TextView) view.findViewById(R.id.tv_epc) ;
				holder.tvPC = (TextView) view.findViewById(R.id.tv_pc) ;
				holder.tvRssi = (TextView) view.findViewById(R.id.tv_rssi) ;
				holder.tvCount = (TextView) view.findViewById(R.id.tv_cnt) ;
				view.setTag(holder);
			}else{
				holder = (ViewHolder) view.getTag() ;
			}
			TagInfo tag = listTag.get(arg0) ;
			holder.tvID.setTag(arg0 + "");
			holder.tvEpc.setTag(tag.getEpc());
			holder.tvPC.setTag(tag.getPc());
			holder.tvRssi.setTag(tag.getRssi() + "");
			holder.tvCount.setTag(arg0 + "");
			
			Log.e("D", "Adapter EPC--->" + tag.getEpc());
			return view;
		}
		
		class ViewHolder{
			TextView tvID ;
			TextView tvEpc ;
			TextView tvPC ;
			TextView tvRssi ;
			TextView tvCount ;
		}
		
	}
   ////////////////////////////////////////////////////////////////////// 

}
