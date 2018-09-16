package com.example.bluetooth.le;


public class Tools {


		public static String Bytes2HexString(byte[] b, int size) {
		    String ret = "";
		    for (int i = 0; i < size; i++) {
		      String hex = Integer.toHexString(b[i] & 0xFF);
		      if (hex.length() == 1) {
		        hex = "0" + hex;
		      }
		      ret += hex.toUpperCase();
		    }
		    return ret;
		  }
		
		public static byte uniteBytes(byte src0, byte src1) {
		    byte _b0 = Byte.decode("0x" + new String(new byte[]{src0})).byteValue();
		    _b0 = (byte)(_b0 << 4);
		    byte _b1 = Byte.decode("0x" + new String(new byte[]{src1})).byteValue();
		    byte ret = (byte)(_b0 ^ _b1);
		    return ret;
		  }
		
		//十六进制字符串转byte[]
		public static byte[] HexString2Bytes(String src) {
			int len = src.length() / 2;
			byte[] ret = new byte[len];
			byte[] tmp = src.getBytes();

			for (int i = 0; i < len; i++) {
				ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
			}
			return ret;
		}
		
		/* byte[]תInt */
		public static int bytesToInt(byte[] bytes)
		{
			int addr = bytes[0] & 0xFF;
			addr |= ((bytes[1] << 8) & 0xFF00);
			addr |= ((bytes[2] << 16) & 0xFF0000);
			addr |= ((bytes[3] << 25) & 0xFF000000);
			return addr;

		}

		/* Intתbyte[] */
		public static byte[] intToByte(int i)
		{
			byte[] abyte0 = new byte[4];
			abyte0[0] = (byte) (0xff & i);
			abyte0[1] = (byte) ((0xff00 & i) >> 8);
			abyte0[2] = (byte) ((0xff0000 & i) >> 16);
			abyte0[3] = (byte) ((0xff000000 & i) >> 24);
			return abyte0;
		}
		
		
	    public static String bin2hex(String bin) {
	        char[] digital = "0123456789ABCDEF".toCharArray();
	        StringBuffer sb = new StringBuffer("");
	        byte[] bs = bin.getBytes();
	        int bit;
	        for (int i = 0; i < bs.length; i++) {
	            bit = (bs[i] & 0x0f0) >> 4;
	            sb.append(digital[bit]);
	            bit = bs[i] & 0x0f;
	            sb.append(digital[bit]);
	        }
	        return sb.toString();
	    }
	    public static byte[] hex2byte(byte[] b) {
	        if ((b.length % 2) != 0) {
	            throw new IllegalArgumentException("���Ȳ���ż��");
	        }
	        byte[] b2 = new byte[b.length / 2];
	        for (int n = 0; n < b.length; n += 2) {
	            String item = new String(b, n, 2);
	            // ��λһ�飬��ʾһ���ֽ�,�������ʾ��16�����ַ���ԭ��һ�������ֽ�
	            b2[n / 2] = (byte) Integer.parseInt(item, 16);
	        }
	        b = null;
	        return b2;
	    }
		
		
	    
		/**
		 * 计算校验位
		 * @param btAryBuffer
		 * @param nStartPos
		 * @param nLen
		 * @return
		 */
		public static  byte checkSum(byte[] btAryBuffer, int nStartPos, int nLen) {
	        byte btSum = 0x00;

	        for (int nloop = nStartPos; nloop < nStartPos + nLen; nloop++ ) {
	            btSum += btAryBuffer[nloop];
	        }

	        return (byte)(((~btSum) + 1) & 0xFF);
	    }

}
