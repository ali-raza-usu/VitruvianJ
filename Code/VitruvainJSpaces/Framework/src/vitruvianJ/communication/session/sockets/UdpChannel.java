package vitruvianJ.communication.session.sockets;

import java.util.*;
import java.net.*;

import vitruvianJ.communication.Heartbeat;
import vitruvianJ.communication.channels.*;
import vitruvianJ.logging.*;
import vitruvianJ.serialization.*;
import vitruvianJ.communication.session.*;

import vitruvianJ.eventargs.EventArgs;
import vitruvianJ.events.Event;



	/// <summary>
	/// Channel that provides communication through a UdpProvider.
	/// </summary>
	public class UdpChannel extends BaseChannel
	{
        /// <summary>
        /// Get a channel id based on a DateTime random seed.
        /// </summary>
        /// <returns></returns>
        static public int GetChannelId()
        {
            Date date = new Date();
            int seed = date.getSeconds()*1000 + date.getSeconds() + date.getMinutes() + date.getHours() + date.getDay() + date.getMonth() + date.getYear();
            Random rand = new Random(seed);
            return rand.nextInt();
        }

        private final int PAIRING = -1;

        private JLogger _logger = new JLogger(UdpChannel.class);

        private BaseUdpProvider _provider = null;

        private int _localChannelId = PAIRING;
        private int _remoteChannelId = PAIRING;

        private IPEndPoint _remoteEndPoint = null;

        /// <summary>
        /// Event called when a message should be sent
        /// </summary>
        public Event SendMessage;

        /// <summary>
        /// Construct the channel.
        /// </summary>
        /// <param name="provider"></param>
        /// <param name="heartbeatFrequency"></param>
        /// <param name="heartbeatTimeout"></param>
        public UdpChannel(BaseUdpProvider provider, IPEndPoint remoteEndPoint, int heartbeatFrequency, int heartbeatTimeout)
        {
        	super(heartbeatFrequency, heartbeatTimeout, _encoder);
            _provider = provider;
            _remoteEndPoint = remoteEndPoint;
            _localChannelId = GetChannelId();
        }

        public IPEndPoint getRemoteEndPoint()
        {
            return _remoteEndPoint; 
        }

        /// <summary>
        /// Is the channel paired.
        /// </summary>
        private boolean IsLocalPaired()
        {
            return _remoteChannelId != PAIRING;
        }

        /// <summary>
        /// Determine if this pipe is dead.
        /// </summary>
        /// <param name="remotePipeId"></param>
        /// <returns></returns>
        private boolean IsChannelDead(int localChannelId, int remoteChannelId)
        {
            boolean isDead = false;

            boolean isLocalPaired = IsLocalPaired();
            boolean isRemotePaired = localChannelId != PAIRING;

            if (isLocalPaired && isRemotePaired)
            {
                if (localChannelId != _localChannelId || remoteChannelId != _remoteChannelId)
                    isDead = true;
            }
            else if (isLocalPaired)
            {
                if (_remoteChannelId != remoteChannelId)
                    isDead = true;
            }
            else if (isRemotePaired)
            {
                if (_localChannelId != localChannelId)
                    isDead = true;
            }

            if (isDead)
            {
                if (_logger.IsDebugEnabled())
                    _logger.DebugFormat("Invalid pair received -> Local {0} : Remote {1}", localChannelId, remoteChannelId);
            }
            else
            {
                if (_logger.IsDebugEnabled())
                    _logger.DebugFormat("Valid pair received -> Local {0} : Remote {1}", localChannelId, remoteChannelId);
            }

            return isDead;
        }

        private void Pair(int remoteChannelId)
        {
            if (_remoteChannelId == PAIRING)
            {
                _remoteChannelId = remoteChannelId;

                if (_logger.IsDebugEnabled())
                    _logger.DebugFormat("Pairing with remote id -> Local {0} : Remote {1}", _localChannelId, _remoteChannelId);
            }
        }

        /// <summary>
        /// The local id of this channel.
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
        /// The remote id of this channel.
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
        /// Event called when the heartbeat determines the connection is dead.
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void HeartbeatConnectionDead(Object sender, EventArgs e)
        {
            if (IsLocalPaired())
                Close();
        }

        /// <summary>
        /// Receive a message.
        /// </summary>
        /// <param name="message"></param>
        public void Receive(UdpMessage udpMessage)
        {
            // switch the ids
            int localChannelId = udpMessage.getRemoteChannelId();
            int remoteChannelId = udpMessage.getLocalChannelId();

            if (IsChannelDead(localChannelId, remoteChannelId))
            {
                Close();
                return;
            }

            Pair(remoteChannelId);

            byte[] bytes = udpMessage.getMsgBytes();
            message = _encoder.ToObject(bytes);//, 0, bytes.length);

            if (message.getClass().equals(Heartbeat.class) )
                _monitor.HeartbeatReceived();
            else
            {
                if (getMessageReceived()!= null)
                    getMessageReceived().RaiseEvent();//(new MessageEventArgs(message));
            }
        }

       



        public String ToString()
        {
            return String.format("Local :{0} Remote:{1}", _localChannelId, _remoteChannelId);
        }

      /// <summary>
        /// Send a message.
        /// </summary>
        /// <param name="message"></param>
		@Override
		public void Send(Object message) {
			// TODO Auto-generated method stub
			boolean sendMessage = true;

            UdpMessage udpMessage = new UdpMessage(_localChannelId, _remoteChannelId);
            udpMessage.setMsgBytes(_encoder.ToBytes(message));

            if (sendMessage)
                _provider.Send((IPEndPoint)_remoteEndPoint, udpMessage);
			
		}

		@Override
		public void Close() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Event getChannelMessageReceived() {
			// TODO Auto-generated method stub
			return getMessageReceived();
		}

		@Override
		public void setMessageReceived(Event delegator) {
			setMessageReceived(delegator);
			
		}

		
		
    }
