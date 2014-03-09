package vitruvianJ.binary;

public class ByteConvertor {
	
	public static int byteArrayToInt(byte[] b, int start, int length) {
        int dt = 0;
        if ((b[start] & 0x80) != 0)
            dt = Integer.MAX_VALUE;
        for (int i = 0; i < length; i++)
            dt = (dt << 8) + (b[start++] & 255);
        return dt;
    }
	
	 public static byte[] intToByteArray(int x) {
		 
		 return new byte[] {				   
	               (byte)(x >>> 24),
	               (byte)(x >>> 16),
	               (byte)(x >>> 8),
	               (byte)x
	                };
	    }
	
	public static byte[] shuffleBytes(byte[] bytes)
	{
		byte[] result = new byte[bytes.length];
		for(int i =0; i<bytes.length; i++)
		{			
			result[bytes.length-i-1] = bytes[i]; 
		}
		return result;
	}
	
	
	public static void main(String args[])
	{
		int length = 706;
		
		byte[] bytes = shuffleBytes(new byte[]{-62,2,0,0});//intToByteArray(length));
		int temp_length = byteArrayToInt(bytes, 0, bytes.length);
		
	}

}
