package com.jiuray.uhf.command;

import com.example.bluetooth.le.BluetoothLeService;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by jj on 2018/8/19.
 * 生成UHF指令，并解析指令
 */

public class UhfCommandHelper {

    private final String TAG = "UhfCommandHelper";
    
	private Context context ;
	private BluetoothGattCharacteristic character ;
	private BluetoothLeService bleService ;
    
    public UhfCommandHelper(){} 
    
    
    public UhfCommandHelper(Context context,BluetoothLeService service){
		this.context = context ;

		this.bleService = service ;
    }

    
	public void registerReceiver(){
		context.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
	}
	
	public void unregisterReceiver(){
		context.unregisterReceiver(mGattUpdateReceiver);
	}
	
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
    
    //接收蓝牙模块返回
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.e(TAG, "action = " + action) ;
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                //mConnected = true;
                
                
//                connect_status_bit=true;
//               
//                invalidateOptionsMenu();
    			//实时盘存指令

            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
//                mConnected = false;
//                
//                //updateConnectionState(R.string.disconnected);
//                connect_status_bit=false;
//                //show_view(false);
//                invalidateOptionsMenu();
                //clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
//                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
            	//获取返回数据
            	//Toast.makeText(context, intent.getStringExtra(BluetoothLeService.EXTRA_DATA), 0).show();
            	//将数据再以广播的形式传出去,下发给fragment
            	//在此处将数据返回去
//                String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
//                Intent i = new Intent() ;
//                i.setAction(ACTION_BLE_RECV_DATA) ;
//                i.putExtra("data", data) ;
//                sendBroadcast(i);
            }
        }
    };
    
    
//////////////////系统指令//////////////////////////////////////////////
    public byte[] reset(){
        byte[] cmd = genCmd(UhfCommand.CMD_RESET, null) ;
    	LogCMD(cmd) ;
        return cmd ;
    }

    /**获取硬件版本号*/
    public byte[] getFirwaremVersion(){
        byte[] cmd = genCmd(UhfCommand.CMD_GET_FIRMWARE_VERSION, null) ;
    	LogCMD(cmd) ;
        return cmd ;
    }

    public byte[] getOutPower(){
        byte[] cmd = genCmd(UhfCommand.CMD_GET_OUTPUT_POWER, null) ;
    	LogCMD(cmd) ;
        return cmd ;
    }

////////////////////6C指令/////////////////////////////////
    /**
     * 盘存指令(一般不用,正常使用实时盘存)
     * @param repeat 盘存过程重复的次数，一般为0xff
     * @return
     */
    public byte[] inventory(int repeat) {
        byte [] data = {(byte)repeat};
        byte[] cmd = genCmd(UhfCommand.CMD_INVENTORY, data) ;
    	LogCMD(cmd) ;
        return cmd ;
    }
    
    /**
     * 读标签数据
     * @param memBank 数据区0x00	RESERVED； 0x01	EPC；0X02	TID；0X03	USER
     * @param addr  读取数据首地址（WORD）	取值范围请参考标签规格。
     * @param length 读取数据长度（word ）
     * @param password  标签访问密码，4字节
     * @return
     */
    public byte[] read(int memBank, int addr, int length, byte[] password){
    	byte[] data  ;
    	if(password[0] == 0 && password[1] == 0&& password[2] == 0&& password[3] == 0){
    		data = new byte[3] ;
    		data[0] = (byte)memBank;
    		data[1] = (byte)addr;
    		data[2] = (byte)length;
    		
    	}else{
    		data = new byte[7] ;
    		data[0] = (byte)memBank;
    		data[1] = (byte)addr;
    		data[2] = (byte)length;
    		System.arraycopy(password, 0, data, 3, 4);
    	}
    	byte[] cmd = genCmd(UhfCommand.CMD_READ, data) ;
    	LogCMD(cmd) ;
    	return cmd ;
    }
    
    /**
     * 选定标签  匹配ACCESS操作的EPC号
     * @param mode 匹配模式， 0x00	EPC匹配一直有效，直到下一次刷新。0x01	清除EPC匹配。
     * @param epc  选定的EPC
     * @return
     */
    public byte[] matchByEPC(int mode ,byte[] epc){
    	byte[] cmd = null ;
    	byte[] data = new byte[ 2+ epc.length] ;
    	data[0] = (byte) mode ;
    	data[1] = (byte) epc.length ;
    	System.arraycopy(epc, 0, data, 2, epc.length);
    	cmd = genCmd(UhfCommand.CMD_SET_ACCESS_EPC_MATCH, data) ;
    	LogCMD(cmd) ;
    	return cmd ;
    }
/////////////////////////////////////////////////////////
    /**
     * Head  	Len 	Address	  Cmd 	 Data	 Check
     * 1Byte   1 Byte	 1 Byte	1 Byte	N Bytes	 1 Byte
     * 生成指令
     * @param cmdCode
     * @param data
     * @return
     */
    private byte[] genCmd(byte cmdCode, byte[] data){
        byte[] cmd  = null ;
        if (data != null) {
            cmd = new byte[5+ data.length] ;
            System.arraycopy(data, 0, cmd, 4, data.length);
        }else{
            cmd = new byte[5] ;
        }
        cmd[0] = UhfCommand.CMD_HEAD ;
        cmd[1] = (byte)(cmd.length - 2) ;
        cmd[2] = UhfCommand.ADDR ;
        cmd[3] = cmdCode ;
        cmd[cmd.length - 1] = checkSum(cmd, 0, cmd.length - 1);
         return cmd ;
    }

    //打印log
    private void LogCMD(byte[] cmd ){
        if (cmd != null) {
            Log.e(TAG, Tools.Bytes2HexString(cmd, cmd.length)) ;
        }
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
}
