/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.bluetooth.le;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with {@code BluetoothLeService}, which in turn interacts with the
 * Bluetooth LE API.
 */
public class DeviceControlActivity extends Activity {
    private final static String TAG = DeviceControlActivity.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private TextView mConnectionState;
    private TextView mDataField;
    private String mDeviceName;
    private String mDeviceAddress;
    private ExpandableListView mGattServicesList;
    private BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    
    
    boolean connect_status_bit=false;
    
    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    private Handler mHandler;

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 1000;
    
    private int i = 0;  
    private int TIME = 1000; 
    

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                //mConnected = true;
                
                
                connect_status_bit=true;
               
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                
                updateConnectionState(R.string.disconnected);
                connect_status_bit=false;
                show_view(false);
                invalidateOptionsMenu();
                //clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
            	//获取返回数据
            	//Toast.makeText(getApplicationContext(), intent.getStringExtra(BluetoothLeService.EXTRA_DATA), 0).show();
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                TagInfo tag = resolveEPCdata(data) ;
                if(tag != null){
                	Util.play(1, 0);
                	//更新列表
                	addList(tag);
                }

            }
        }
    };


    //开始盘存
    Button send_button;
    Button enable_button;
    Button IBeacon_set_button;
    
    EditText txd_txt,uuid_1001_ed,rx_data_id_1;
    
    EditText ibeacon_uuid;
    EditText mayjor_txt,minor_txt;
    
    EditText dev_Name;
    Button name_button;
    
    EditText password_ed;//密码值
    Button password_enable_bt;//密码开关
    Button password_wrt;//密码写入Button
    
    Button adv_time1,adv_time2,adv_time3,adv_time4;
    
    boolean pass_en=false;
    
    Button clear_button;
    
    private ListView lv ;//标签列表
    private Button IO_H_button,IO_L_button;//out io
    Timer timer = new Timer();  

    
    void show_view( boolean p )
    {
    	if(p){
    		send_button.setEnabled(true);
    	}else{
    		send_button.setEnabled(false);
    	}
    }
    
    public void delay(int ms){
		try {
            Thread.currentThread();
			Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } 
	 }	
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gatt_services_characteristics);
        Util.initSoundPool(this);
        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
        lv = (ListView) findViewById(R.id.lv) ;
        mContext = this ;
        // Sets up UI references.
        ((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);
        //mGattServicesList = (ExpandableListView) findViewById(R.id.gatt_services_list);
       // mGattServicesList.setOnChildClickListener(servicesListClickListner);
        mConnectionState = (TextView) findViewById(R.id.connection_state);
        mDataField = (TextView) findViewById(R.id.data_value);


        
        send_button=(Button)findViewById(R.id.tx_button);//send data 1002
        send_button.setOnClickListener(listener);//设置监听  
        
        clear_button=(Button)findViewById(R.id.clear_button);//send data 1002
        clear_button.setOnClickListener(listener);//设置监听  
        
        txd_txt=(EditText)findViewById(R.id.tx_text);//1002 data
//        txd_txt.setText("AA04FF0A0049");//设置工作天为1
        //快速多天线盘存
        txd_txt.setText("AA0EFF42F40001010102010301070002");
        rx_data_id_1=(EditText)findViewById(R.id.rx_data_id_1);//1002 data
        rx_data_id_1.setText("");

        
        
        show_view(false);
        mHandler = new Handler();
        
        timer.schedule(task, 1000, 1000); // 1s后执行task,经过1s再次执行  
        
        boolean sg;
        getActionBar().setTitle(mDeviceName);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        sg = bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        //getActionBar().setTitle( "="+BluetoothLeService );
        //mDataField.setText("="+sg );
        updateConnectionState(R.string.connecting);
        //开启线程
        new Thread(inventoryTask).start();
    }
    
    Handler handler = new Handler() {  
        public void handleMessage(Message msg) {  
        	if (msg.what == 1) {  
                //tvShow.setText(Integer.toString(i++));  
            	//scanLeDevice(true);
            	if (mBluetoothLeService != null) {
                	if( mConnected==false )
                	{
                		updateConnectionState(R.string.connecting);
                		final boolean result = mBluetoothLeService.connect(mDeviceAddress);
                		Log.d(TAG, "Connect request result=" + result);
                	}
                }
            }  
            super.handleMessage(msg);  
        };  
    };  
    TimerTask task = new TimerTask() {  
    	  
        @Override  
        public void run() {  
            // 需要做的事:发送消息  
            Message message = new Message();  
            message.what = 1;  
            handler.sendMessage(message);  
        }  
    }; 

    
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
					
	        		if( connect_status_bit )
	        		  {
	        			//实时盘存指令
	              		String tx_string="AA04FFEF0163";
	              		mBluetoothLeService.txxx(tx_string);
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
	//////////////////////////////////////////////////////////////////////
	private List<TagInfo> listTag = new ArrayList<TagInfo>();
	private List<Map<String, Object>> listMap ;
	private Context mContext ;
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
    	mDataField.setText("" + setTag.size());
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
			byte crc = checkSum(realData, 0, realData.length - 1) ;
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
				Log.e("D", "rssi--->" + rssi);
			}
		}
		
		return tag ;
	}

	/**
	 * 计算校验位
	 * @param btAryBuffer
	 * @param nStartPos
	 * @param nLen
	 * @return
	 */
	private  byte checkSum(byte[] btAryBuffer, int nStartPos, int nLen) {
        byte btSum = 0x00;

        for (int nloop = nStartPos; nloop < nStartPos + nLen; nloop++ ) {
            btSum += btAryBuffer[nloop];
        }

        return (byte)(((~btSum) + 1) & 0xFF);
    }
	
    /**
     * 监听按键
     */
    Button.OnClickListener listener = new Button.OnClickListener(){//创建监听对象    
        public void onClick(View v){    
            //String strTmp="点击Button02";    
            //Ev1.setText(strTmp);   
        	switch( v.getId())
        	{
        	case R.id.tx_button ://uuid1002 数传通道发送数据
        		if( connect_status_bit )
      		  {
//            		String tx_string=txd_txt.getText().toString().trim();

            		if(isStart){
            			isStart = false ;
            			send_button.setText("开始");
            		}else{
            			send_button.setText("停止");
            			//设置多天线盘存
            			String tx_string="AA0EFF42F40001010102010301070002" ;
                		mBluetoothLeService.txxx(tx_string);
            			isStart = true ;
            		}
      		  }else{
      			  //Toast.makeText(this, "Deleted Successfully!", Toast.LENGTH_LONG).show(); 
      			  Toast toast = Toast.makeText(DeviceControlActivity.this, "设备没有连接！", Toast.LENGTH_SHORT); 
      			  toast.show(); 
      		  }
        		break;
        	case R.id.clear_button:
        	{

        		mDataField.setText("");
        		listTag = new ArrayList<TagInfo>();
        		setTag = new HashSet<String>();
        		lv.setAdapter(null);
	    		len_g =0;
	    		da = "";
	    		rx_data_id_1.setText( da );
//	    		mDataField.setText( ""+len_g );
        	}break;
        		default :
        			break;
        	}
        }    
  
    };  
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
        	
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
		isStart = false ;
		send_button.setText("开始");
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
        timer.cancel();
        timer=null;
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if(keyCode == KeyEvent.KEYCODE_BACK){
    		isStart = false ;
    		isRunning = false ;
    	}
    	return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    } 
 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_connect:
                mBluetoothLeService.connect(mDeviceAddress);
                return true;
            case R.id.menu_disconnect:
                mBluetoothLeService.disconnect();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }
String da="";
int len_g = 0;
    private void displayData( String data1 ) {
		//String head1,data_0;
		/*
		head1=data1.substring(0,2);
		data_0=data1.substring(2);
		*/
    	//da = da+data1+"\n";
    	if( data1!=null&&data1.length()>0)
    	{
			//mDataField.setText( data1 );
    		len_g += data1.length()/2;
    		//da = data1+da;
    		
    		rx_data_id_1.setText( data1 );
//    		mDataField.setText( ""+len_g );
    		
    		//rx_data_id_1.setGravity(Gravity.BOTTOM);
    		//rx_data_id_1.setSelection(rx_data_id_1.getText().length());
    		
    		
    		
    	}
    	
    }

    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView
    // on the UI.
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        

        if( gattServices.size()>0&&mBluetoothLeService.get_connected_status( gattServices )>=4 )
        {
	        if( connect_status_bit )
			  {
	        	mConnected = true;
	        	show_view( true );
				mBluetoothLeService.enable_JDY_ble(true);
				 try {  
			            Thread.currentThread();  
			            Thread.sleep(100);  
			        } catch (InterruptedException e) {  
			            e.printStackTrace();  
			        }  
				 mBluetoothLeService.enable_JDY_ble(true);
				 updateConnectionState(R.string.connected);
			  }else{
				  //Toast.makeText(this, "Deleted Successfully!", Toast.LENGTH_LONG).show(); 
				  Toast toast = Toast.makeText(DeviceControlActivity.this, "设备没有连接！", Toast.LENGTH_SHORT); 
				  toast.show(); 
			  }
        }
        
        
//        SimpleExpandableListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(
//                this,
//                gattServiceData,
//                android.R.layout.simple_expandable_list_item_2,
//                new String[] {LIST_NAME, LIST_UUID},
//                new int[] { android.R.id.text1, android.R.id.text2 },
//                gattCharacteristicData,
//                android.R.layout.simple_expandable_list_item_2,
//                new String[] {LIST_NAME, LIST_UUID},
//                new int[] { android.R.id.text1, android.R.id.text2 }
//        );
//        
//        mGattServicesList.setAdapter(gattServiceAdapter);
        
    }
 
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
}