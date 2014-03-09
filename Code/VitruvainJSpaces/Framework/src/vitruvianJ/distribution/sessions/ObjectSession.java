package vitruvianJ.distribution.sessions;

import vitruvianJ.communication.session.Session;
import vitruvianJ.communication.session.protocols.*;
import vitruvianJ.distribution.session.processors.ExecuteMethodProcessor;
import vitruvianJ.distribution.session.processors.ExecuteSyncPatternMethodProcessor;
import vitruvianJ.distribution.session.processors.InitializationProcessor;
import vitruvianJ.distribution.session.processors.ObjectInitializationProcessor;
import vitruvianJ.distribution.session.processors.ServicesProcessor;
import vitruvianJ.distribution.sessions.messages.*;
import vitruvianJ.distribution.encoders.*;
import vitruvianJ.logging.*;
import vitruvianJ.serialization.formatters.*;
import vitruvianJ.serialization.*;
import vitruvianJ.services.*;
//import vitruvianJ.distribution.processors.*;
//import vitruvianJ.distribution.messages.*;
//import vitruvianJ.distribution.encoders.*;
//import vitruvianJ.distribution.proxies.*;
import java.lang.reflect.*;
import java.util.List;

//import vitruvianJ.distribution.protocols.*;

	/// <summary>
	/// Session that distributes objects.
	/// </summary>
	public class ObjectSession extends ProtocolSession
	{
        private JLogger _logger =  new JLogger(ObjectSession.class);

        private InitializationProcessor _initProcessor = null;
        private ObjectInitializationProcessor _objectInitProcessor = null;
        private ExecuteMethodProcessor _methodProcessor = null;
        private ExecuteSyncPatternMethodProcessor _syncPatternMethodProcessor = null;
        private ServicesProcessor _servicesProcessor = null;

        private JGUID _brokerId = new JGUID();
        private List<IService> _servicesToDistribute = null;

        private Object _startSyncObject = new Object();

        /// <summary>
        /// The broker id
        /// </summary>
        public JGUID getBrokerId()
        {
            	return _brokerId; 
        }
        
        public void  setBrokerId(JGUID value)
        {
                _brokerId = value;
                ((ObjectEncoder)_encoder).setBrokerId(value);        
        }

		/// <summary>
		/// A list of services to distribute.
		/// </summary>
        public List<IService> getServicesToDistribute()
		{
			return _servicesToDistribute; 
		}
        
		public void	setServicesToDistribute(List<IService> value) 
		{ 
			_servicesToDistribute = value;
		}

		public Session getSession()
		{
			return this;
		}
		
        /// <summary>
        /// The default timeout for messages.
        /// </summary>
		
		@Serialize//(getName = "get")
        public int getMessageTimeout()
        {
            return ObjectSession.TIMEOUT; 
        }
		
		@Serialize//(getName = "set")
        public void setMessageTimeout(int value) 
        { 
        	ObjectSession.TIMEOUT = value;
        }

        
        public void AddService(IService service)
        {
            Send(new RequestAddService(service));
        }

        public void RemoveService(IService service)
        {
            Send(new RequestRemoveService(service));
        }

		/// <summary>
		/// Start the session.
		/// </summary>
		public void Start()
		{
            synchronized (_startSyncObject)
            {
                if (!_isRunning)
                {
                    _initProcessor = new InitializationProcessor();
                    _initProcessor.setServicesToDistribute(_servicesToDistribute);
                    SetupRoute(MessageIds.REQUEST_BROKER_ID, _initProcessor);
                    SetupRoute(MessageIds.REPLY_BROKER_ID, _initProcessor);
                    SetupRoute(MessageIds.REQUEST_KNOWN_OBJECT_IDS, _initProcessor);
                    SetupRoute(MessageIds.REPLY_KNOWN_OBJECT_IDS, _initProcessor);
                    SetupRoute(MessageIds.REQUEST_SERVICES, _initProcessor);
                    SetupRoute(MessageIds.REPLY_SERVICES, _initProcessor);

                    _objectInitProcessor = new ObjectInitializationProcessor();
                    SetupRoute(MessageIds.REQUEST_INIT_OBJECT, _objectInitProcessor);

                    _methodProcessor = new ExecuteMethodProcessor();
                    SetupRoute(MessageIds.REQUEST_EXECUTE_METHOD, _methodProcessor);                    

                    _syncPatternMethodProcessor = new ExecuteSyncPatternMethodProcessor();
                    SetupRoute(MessageIds.REQUEST_EXECUTE_SYNC_PATTERN_METHOD, _syncPatternMethodProcessor);

                    _servicesProcessor = new ServicesProcessor();
                    SetupRoute(MessageIds.REQUEST_ADD_SERVICE, _servicesProcessor);
                    SetupRoute(MessageIds.REQUEST_REMOVE_SERVICE, _servicesProcessor);
                  // ((ObjectEncoder)_encoder).Init();
                    super.Start();
                }
            }
		}

		/// <summary>
		/// Stop the session.
		/// </summary>
		public void Stop()
		{
            synchronized(_startSyncObject)
            {
                if (_isRunning)
                {
                    super.Stop();
                }
            }
		}
	}
