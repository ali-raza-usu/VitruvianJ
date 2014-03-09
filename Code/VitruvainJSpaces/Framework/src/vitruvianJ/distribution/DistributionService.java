package vitruvianJ.distribution;

import java.util.*;

import vitruvianJ.communication.session.*;
import vitruvianJ.communication.session.sockets.BaseChannel;
import vitruvianJ.services.*;
import vitruvianJ.communication.channels.*;
import vitruvianJ.serialization.*;
import vitruvianJ.logging.*;
import vitruvianJ.distribution.sessions.*;
import vitruvianJ.distribution.encoders.*;


    public class DistributionService extends SessionsManager<ObjectSession>
    {
        public DistributionService(ObjectSession t) {
			super(t);
			// TODO Auto-generated constructor stub
		}

       
		private static JLogger _logger = new JLogger(DistributionService.class);

        private List<IService> _services = new ArrayList<IService>();
        private List<IChannelProvider> _channelProviders = new ArrayList<IChannelProvider>();
        private ObjectEncoder _encoder = null;

        private int _messageTimeout = 1000;

        @Serialize//(getName = "get")
        public List<IService> getServices()
        {
            return _services; 
        }
            
        @Serialize//(getName = "set")
        public void setServices(List<IService> value) 
        { 
        	_services = value;
        }

        
        @Serialize//(getName = "get")
        public List<IChannelProvider> getProviders()
        {
            return _channelProviders; 
        }
            
        @Serialize//(getName = "set")
        public void setProviders(List<IChannelProvider> value) 
        { 
        	_channelProviders = value; 
        }

        
        @Serialize//(getName = "get")
        public int getMessageTimeout()
        {
            return _messageTimeout; 
        }
        
        @Serialize//(getName = "set")
        public void setMessageTimeout(int value) 
        { 
        	_messageTimeout = value;
        }

        @Serialize//(getName = "get")
        public ObjectEncoder getEncoder()
        {
            return _encoder; 
        }
        
        @Serialize//(getName = "set")
        public void setEncoder(ObjectEncoder value) 
        { 
        	_encoder = value;
        }

        
        protected void ConfigureProviders()
        {
            _providers = new ChannelProviderList();

            for (IChannelProvider provider : _channelProviders)
            {
                _providers.add(provider);
            }
        }

        @Override
        protected void ConfigureSession(Session session_p)
        {        
        	ObjectSession session = (ObjectSession)session_p;
            session.setEncoder( (IEncoder)_encoder.clone() );
            session.setMessageTimeout( _messageTimeout );
            session.setServicesToDistribute( _services );
            BaseChannel._encoder = session.getEncoder();
            session_p = session;
        }
    }


