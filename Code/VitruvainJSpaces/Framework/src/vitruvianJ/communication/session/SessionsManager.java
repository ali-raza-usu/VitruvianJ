package vitruvianJ.communication.session;

import java.util.ArrayList;
import java.util.List;

import vitruvianJ.communication.channels.ChannelEventArgs;
import vitruvianJ.communication.channels.ChannelProviderList;
import vitruvianJ.core.ClassFactory;
import vitruvianJ.delegate.IDelegate;
import vitruvianJ.eventargs.EventArgs;
import vitruvianJ.events.Delegate;
import vitruvianJ.events.Event;
import vitruvianJ.events.EventRegistry;
import vitruvianJ.events.IEventSubject;
//import vitruvianJ.delegate.Delegator;
//import vitruvianJ.delegate.IDelegate;
import vitruvianJ.logging.JGUID;
import vitruvianJ.logging.JLogger;
import vitruvianJ.serialization.Serialize;
import vitruvianJ.services.IService;



    

    /// <summary>
    /// Class that opens ChannelProviders, and creates Sessions when Channels are opened.
    /// </summary>
    /// <typeparam name="T"></typeparam>
    public class SessionsManager<T extends Session> implements IService, IEventSubject// where T : Session, new()
    {
    	//public Delegate SessionEventHandler;//(SessionEventArgs args);
    	T t;
        static private JLogger _logger = new JLogger(SessionsManager.class);//<T>);

        private String _channelsContentPath = "";
        private String _sessionsContentPath = "";
        private String _context = "";

        protected ChannelProviderList _providers = null;
        protected List<Session> _sessions = new ArrayList<Session>();

        /// <summary>
        /// Event called when a session is added
        /// </summary>
        public Event SessionAdded = new Event(this);

        /// <summary>
        /// Event called when a session is removed
        /// </summary>
        public Event SessionRemoved = new Event(this);
        

        public SessionsManager(T t)
        {
        	this.t = t;
        	EventRegistry.getInstance().addEvent("SessionAdded", SessionAdded);
        	EventRegistry.getInstance().addEvent("SessionRemoved", SessionRemoved);
        }
        /// <summary>
        /// Initialize the manager.
        /// </summary>
        public void Start()
        {        	
            ConfigureProviders();

            	
            for (int i = 0; i < _providers.size(); i++)
            {
                _providers.get(i).getChannelAvailable().addObservers(new ChannelOpened());
                if (!_providers.get(i).Start())
                { }
            }
        }

        /// <summary>
        /// Cleanup the manager.
        /// </summary>
        public void Stop()
        {
            if (_logger.IsDebugEnabled())
                _logger.Debug("Closing Providers");

            for (int i = 0; i < _providers.size(); i++)
            {
            	_providers.get(i).getChannelAvailable().removeObservers(new ChannelOpened());
                //_providers.get(i).setChannelAvailable(SessionEventHandler.build(this,"ChannelOpened"));
                _providers.get(i).Stop();
            }
            _providers.clear();

            if (_logger.IsDebugEnabled())
                _logger.Debug("Stopping Sessions");

            for (int i = 0; i < _sessions.size(); i++)
            {
                _sessions.get(i).SessionClosed.removeObservers(new SessionClosed());// = null;// -= SessionClosed;
                _sessions.get(i).Stop();
            }
            _sessions.clear();
        }

        /// <summary>
        /// The persistence id used to configure the channels
        /// </summary>
        @Serialize
        public String getChannelsContentPath()
        {
            return _channelsContentPath; 
        }
        @Serialize
        public void setgetChannelsContentPath(String value) 
        { 
        	_channelsContentPath = value; 
        }
        

        /// <summary>
        /// The persistence id used to configure the session.
        /// </summary>
        @Serialize
        public String getSessionContentPath()
        {
            return _sessionsContentPath; 
        }
        @Serialize
        public void setSessionContentPath(String value) 
        { 
        	_sessionsContentPath = value; 
        }
        

        /// <summary>
        /// The list of open Sessions.
        /// </summary>
        public List<Session> getSessions()
        {
            return _sessions; 
        }

        /// <summary>
        /// Handler for the channel opened event.
        /// </summary>
        /// <param name="args">The arguments containing the opened Channel.</param>
        protected class ChannelOpened implements Delegate{
        	
        	 protected void getChannelOpened(ChannelEventArgs args) throws Exception
             {
             	Session session  = (Session) ClassFactory.CreateObject(t.getClass());
             	//session =  ClassFactory.CreateObject(t);// t.getClass() new T();
                 //T session =  new t();//null; // = new Session();
                 session.setChannel(args.getChannel());
                 ConfigureSession(session);

                 try
                 {
                     session.Start();
                     session.SessionClosed.addObservers(this);// = new Delegator();// += SessionClosed;
                     _sessions.add(session);

                     if (SessionAdded != null)
                         SessionAdded.RaiseEvent();//invoke(new SessionEventArgs(session));

//                     if (_logger.IsInfoEnabled())
//                         _logger.InfoFormat("Session Added :  " +  args.getChannel().toString());
                 }
                 catch (Exception ex)
                 {
                     session.Stop();

                     if (_logger.IsErrorEnabled())
                         _logger.Error(ex);
                 }
             }

		@Override
		public void invoke(EventArgs args) {
			try {
				getChannelOpened((ChannelEventArgs)args);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		
        }

       
        protected void ConfigureProviders()
        {
            _providers = new ChannelProviderList(_channelsContentPath);
        }

        protected void ConfigureSession(Session session)
        {
            session.Configure(_sessionsContentPath);
        }

        /// <summary>
        /// Handler for the session closed event.
        /// </summary>
        /// <param name="args">The arguments containing the closed Session.</param>
        protected class SessionClosed implements Delegate
        {
        	
	        private void SessionClosed(SessionEventArgs args)
	        {
	            Session session = (Session)args.getSession();
	
	            session.SessionClosed.removeObservers(this);
	            _sessions.remove(session);
	            EventArgs sessionArgs = new SessionEventArgs(session);
	            if (SessionRemoved != null)
	                SessionRemoved.RaiseEvent();//invoke(sessionArgs);
	
	            if (_logger.IsInfoEnabled())
	                _logger.InfoFormat("Session Removed : %1s ", args.getSession().getChannel().toString());
	        }

		@Override
		public void invoke(EventArgs args) {
			SessionClosed((SessionEventArgs)args);			
		}
        }
       
        private JGUID _id =  new JGUID();

        
    	
    	
    	public JGUID getId()
        {
            return _id; 
        }

        public String getName()
        {
            return "Sessions Manager"; 
        }

        public void Init()
        {
            Start();
        }

        public void Cleanup()
        {
            Stop();
        }

		@Override
		public EventArgs getEventArgs() {
			// TODO Auto-generated method stub
			return null;
		}
	

    }