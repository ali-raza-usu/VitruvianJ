package vitruvianJ.communication.session.sockets;

import java.net.*;
import java.util.*;


    /// <summary>
    /// Class that holds the bytes and endpoint of socket.
    /// </summary>
    public class UdpMessage
    {
        protected byte[] _msgBytes = null;

        private int _localChannelId = -1;
        private int _remoteChannelId = -1;

        public UdpMessage()
        {
        }

        /// <summary>
        /// Construct the udp message object.
        /// </summary>
        /// <param name="channel"></param>
        public UdpMessage(int localChannelId, int remoteChannelId)
        {
            _localChannelId = localChannelId;
            _remoteChannelId = remoteChannelId;
        }

        /// <summary>
        /// The id local to this application.
        /// </summary>
        public int getLocalChannelId()
        {
            return _localChannelId; 
        }
        
        public void setLocalChannelId(int value) 
        { 
        	_localChannelId = value; 
        }

        /// <summary>
        /// The id remote to this application.
        /// </summary>
        public int getRemoteChannelId()
        {
            return _remoteChannelId; 
        }
        
        public void setRemoteChannelId(int value) 
        {
        	_remoteChannelId = value; 
        }

        /// <summary>
        /// The bytes contained in the message.
        /// </summary>
        public byte[] getMsgBytes()
        {
            return _msgBytes; 
        }
        
        public void setMsgBytes(byte[] value) 
        { 
        	_msgBytes = value; 
        }

        public static byte[] intToByteArray(int value) {
            byte[] b = new byte[4];
            for (int i = 0; i < 4; i++) {
                int offset = (b.length - 1 - i) * 8;
                b[i] = (byte) ((value >>> offset) & 0xFF);
            }
            return b;
        }

        public static int byteArrayToInt(byte[] b, int offset) {
            int value = 0;
            for (int i = 0; i < 4; i++) {
                int shift = (4 - 1 - i) * 8;
                value += (b[i + offset] & 0x000000FF) << shift;
            }
            return value;
        }

        
        /// <summary>
        /// Convert the message into bytes.
        /// </summary>
        /// <returns></returns>
        public byte[] ToBytes()
        {
            byte[] bytes = new byte[_msgBytes.length + 8];
            System.arraycopy(intToByteArray(_localChannelId), 0, bytes, 0, 4);
            System.arraycopy(intToByteArray(_remoteChannelId), 0, bytes, 4, 4);
            System.arraycopy(_msgBytes, 0, bytes, 8, _msgBytes.length);
            return bytes;
        }

        /// <summary>
        /// Create a message from bytes.
        /// </summary>
        /// <param name="bytes"></param>
        /// <param name="length"></param>
        public void FromBytes(byte[] bytes, int length)
        {
            _localChannelId = byteArrayToInt(bytes, 0);
            _remoteChannelId = byteArrayToInt(bytes, 4);
            _msgBytes = new byte[length - 8];
            System.arraycopy(bytes, 8, _msgBytes, 0, _msgBytes.length);
        }
    }

