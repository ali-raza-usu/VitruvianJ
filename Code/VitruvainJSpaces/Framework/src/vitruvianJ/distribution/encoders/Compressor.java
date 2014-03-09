package vitruvianJ.distribution.encoders;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import vitruvianJ.logging.JLogger;

public class Compressor
{
    static private JLogger _logger = new JLogger(Compressor.class);

    /// <summary>
    /// Compress the stream using .NET's GZip Compression
    /// </summary>
    /// <param name="bytes"></param>
    /// <returns></returns>
    public static byte[] Compress(byte[] bytes)
    {
        //Profiler.Start(_logger, "Compressing {0} bytes", bytes.Length);

    	try{
        ByteArrayInputStream zipStream = new ByteArrayInputStream(bytes);        
        ByteArrayOutputStream zipOutStream = new ByteArrayOutputStream(bytes.length);
        GZIPOutputStream zip = new GZIPOutputStream(zipOutStream);
        zip.write(bytes, 0, bytes.length); //These bytes are the compressed form of data
        zip.close();
        zipStream.mark(0);
 
        //MemoryStream outStream = new MemoryStream();

        byte[] compressed = new byte[bytes.length]; //So bytes are of small size they are compressed now
        zipStream.read(compressed, 0, compressed.length);  //How zipStream can read using the compressed data structures

        byte[] result = new byte[compressed.length + 4]; //Why +4 byte
        System.arraycopy(compressed, 0, result, 4, compressed.length); //
        System.arraycopy(bytes, 0, result, 0, 4); //copy +4 byte from bytes array to result

        
       // Profiler.Stop(_logger);

        return result;
    	}catch(Exception ex)
    	{
    		ex.printStackTrace();
    		return null;
    	}
    }

    public static int toInt( byte[] bytes ) {
        int result = 0;
        for (int i=0; i<4; i++) {
          result = ( result << 8 ) - Byte.MIN_VALUE + (int) bytes[i];
        }
        return result;
      }
    /// <summary>
    /// Decompress the bytes using .NET's GZip Compression.
    /// </summary>
    /// <param name="bytes"></param>
    /// <returns></returns>
    public static byte[] Decompress(byte[] bytes) throws IOException
    {
      //  Profiler.Start(_logger, "Decompressing {0} bytes", bytes.Length);

        int msgLength =  toInt(bytes);

        ByteArrayOutputStream zipStream = new ByteArrayOutputStream();
        zipStream.write(bytes, 4, bytes.length - 4); //Why -4 bytes are off from bytes
        zipStream.reset();

        ByteArrayInputStream zipInputStream = new ByteArrayInputStream(zipStream.toByteArray());
        GZIPInputStream zip = new GZIPInputStream(zipInputStream);

        byte[] result = new byte[msgLength];
        zip.read(result, 0, result.length);
        //Profiler.Stop(_logger);
        return result;
    }
}
