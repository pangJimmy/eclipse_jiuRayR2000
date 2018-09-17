package com.jiuray.uhf.command;

import android.util.Log;

/**
 * Created by jj on 2018/8/19.
 * 生成UHF指令，并解析指令
 */

public class UhfCommandHelper {

    private final String TAG = "UhfCommandHelper";

//////////////////系统指令//////////////////////////////////////////////
    public byte[] reset(){
        byte[] cmd = genCmd(UhfCommand.CMD_RESET, null) ;
        if (cmd != null) {
            Log.e(TAG, Tools.Bytes2HexString(cmd, cmd.length)) ;
        }
        return cmd ;
    }

    /**获取硬件版本号*/
    public byte[] getFirwaremVersion(){
        byte[] cmd = genCmd(UhfCommand.CMD_GET_FIRMWARE_VERSION, null) ;
        if (cmd != null) {
            Log.e(TAG, Tools.Bytes2HexString(cmd, cmd.length)) ;
        }
        return cmd ;
    }

    public byte[] getOutPower(){
        byte[] cmd = genCmd(UhfCommand.CMD_GET_OUTPUT_POWER, null) ;
        if (cmd != null) {
            Log.e(TAG, Tools.Bytes2HexString(cmd, cmd.length)) ;
        }
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
        if (cmd != null) {
            Log.e(TAG, Tools.Bytes2HexString(cmd, cmd.length)) ;
        }
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
        if (cmd != null) {
            Log.e(TAG, Tools.Bytes2HexString(cmd, cmd.length)) ;
        }
    	return null ;
    }

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
