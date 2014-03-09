package vitruvianJ.distribution.session.processors;

import java.util.ArrayList;
import java.util.List;

import vitruvianJ.communication.session.RequestMessage;

import vitruvianJ.communication.session.Message;
import vitruvianJ.communication.session.protocols.IProcessor;
import vitruvianJ.communication.session.protocols.ProtocolSession;
import vitruvianJ.core.SignalSyncThread;
import vitruvianJ.distribution.*;
import vitruvianJ.communication.session.ResponseMessage;
import vitruvianJ.distribution.proxies.IRemoteSyncProxy;
import vitruvianJ.distribution.sessions.ObjectSession;
import vitruvianJ.distribution.sessions.messages.ReplyBrokerId;
import vitruvianJ.distribution.sessions.messages.ReplyKnownObjectIds;
import vitruvianJ.distribution.sessions.messages.ReplyServices;
import vitruvianJ.distribution.sessions.messages.RequestBrokerId;
import vitruvianJ.distribution.sessions.messages.RequestKnownObjectIds;
import vitruvianJ.distribution.sessions.messages.RequestServices;
import vitruvianJ.eventargs.EventArgs;
import vitruvianJ.events.Delegate;
import vitruvianJ.events.ReturnDelegate;
import vitruvianJ.logging.JGUID;
import vitruvianJ.logging.JLogger;
import vitruvianJ.services.IService;
import vitruvianJ.services.ServiceRegistry;

public class InitializationProcessor implements IProcessor
{
    private enum InitState
    {
        GetBrokerId,
        GetKnownObjectIds,
        GetServices,
        Finished,
    }

    private final int REQUEST_FREQUENCY = 1000;

    private JLogger _logger = new JLogger(InitializationProcessor.class);

    private ObjectSession _session = null;
    private SignalSyncThread _requestThread = null;
    private InitState _state = InitState.GetBrokerId;

    private Object _stateSync = new Object();

    private List<IService> _servicesToDistribute = new ArrayList<IService>();

    public InitializationProcessor()
    {
        _requestThread = new SignalSyncThread("Object Session : Repeat Request Thread", new RepeatRequest(), true);
        
       // _logger.Debug("Repeat Request " + _requestThread);
    }

    public List<IService> getServicesToDistribute()
    {
        return _servicesToDistribute; 
    }
        
    public void setServicesToDistribute(List<IService> value) 
    { 
    	_servicesToDistribute = value; 
    }
    

	

	public boolean getInitialized()
	{
		return _session != null;
	}

	public void Init(ProtocolSession session)
	{
//        if (_logger.IsDebugEnabled())
//            _logger.Debug("Initializing");

        _state = InitState.GetBrokerId;
        _session = (ObjectSession)session;

        _requestThread.Start(true);
        
	}

    public void ProcessMessage(Message message)
	{
        if (message instanceof RequestMessage )
			try {
				HandleRequest((RequestMessage)message);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		else if (message instanceof ResponseMessage)
            HandleResponse((ResponseMessage)message);
	}

	public void Cleanup()
	{
        if (_requestThread.IsRunning())
			try {
				_requestThread.Stop();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

        ObjectBroker.RemoveSession(_session);

        // Get the orphaned proxies
        List<IRemoteSyncProxy> orphanedProxies = ObjectBroker.GetOrphanedProxies();

        for (int i = 0; i < orphanedProxies.size(); i++)
        {
            IService service = (IService)orphanedProxies.get(i);
            if (service != null)
            {
                if (ServiceRegistry.Contains(service))
                    ServiceRegistry.Remove(service);
            }
        }

        _session = null;
	}

	
	class RepeatRequest implements ReturnDelegate
	{

		@Override
		public void invoke(EventArgs args) {
			//_logger.Debug("Calling Repeat Request ");
//			repeatRequest();
			
		}

		@Override
		public boolean invoke() 
		{			
			return repeatRequest();			
		}
	}
   

    private boolean repeatRequest()
    {
    	 
        switch (_state)
        {
            case GetBrokerId:
                {
                    _session.Send(new RequestBrokerId());
                    break;
                }
            case GetKnownObjectIds:
                {
                    _session.Send(new RequestKnownObjectIds());
                    break;
                }
            case GetServices:
                {
                    _session.Send(new RequestServices());
                    break;
                }
            case Finished:
                {
                    return true;
                }
        }
        System.out.println(" State : " + _state);
        _requestThread.Sleep(REQUEST_FREQUENCY);
       // _requestThread.Signal();
        return false;
    }

    private void HandleRequest(RequestMessage message) throws Exception
    {
    	//_logger.Debug("Initialization Processor : HandleRequest() " + message);
        if (message instanceof RequestBrokerId)
        {
        	int messageId = ((Message) message).getMessageId();
        //	_logger.Debug("InitializationProcessor : HandleRequest() : " + message);
        	ReplyBrokerId replyBrokerId = new ReplyBrokerId (messageId, ObjectBroker.getId());
            _session.Send(replyBrokerId);
        }
        else
        {
            // don't handle any other requests, until we have their broker id
            if (_state != InitState.GetBrokerId)
            {
                if (message instanceof RequestKnownObjectIds)
                {
                	//_logger.Debug(" RequestKnownObjectIds : " + message);
                    _session.Send(new ReplyKnownObjectIds(((Message) message).getMessageId(), ObjectBroker.GetKnownObjectIds()));
                }
                else if (message instanceof RequestServices)
                {
                    _session.Send(new ReplyServices(message.getMessageId(), _servicesToDistribute));
                }
                else
                {                	
                    throw new Exception("Incorrect message for processor.");
                }
            }
        }
    }

    private void HandleResponse(ResponseMessage message)
    {
    	//_logger.Debug("Initialization Processor : HandleResponse() " + message);
        synchronized (_stateSync)
        {
            switch (_state)
            {
                case GetBrokerId:
                    {
                        if (message instanceof ReplyBrokerId)
                        {
                            JGUID brokerId = ((ReplyBrokerId)message).getBrokerId();
                            
                            // let the session know which broker it's working with
                            _session.setBrokerId(brokerId);

                            // add the broker to the remote brokers
                            ObjectBroker.AddBroker(brokerId);

                            // add the session to the remote broker
                            ObjectBroker.AddSessionToBroker(brokerId, _session);

                            // move to the next state
                            _state = InitState.GetKnownObjectIds;
                        }
                        break;
                    }
                case GetKnownObjectIds:
                    {
                        if (message instanceof ReplyKnownObjectIds)
                        {
                            List<JGUID> knownObjectIds = ((ReplyKnownObjectIds)message).getKnownObjectIds();

                            for(JGUID proxyId : knownObjectIds)
                            {
                                ObjectBroker.AddKnownObjectToBroker(_session.getBrokerId(), proxyId);
                            }

                            _state = InitState.GetServices;
                        }
                        break;
                    }
                case GetServices:
                    {
                        if (message instanceof ReplyServices)
                        {
                            List<IService> services = ((ReplyServices)message).getServices();
                            for (int i = 0; i < services.size(); i++)
                            {
                                JGUID id = services.get(i).getId();
                                if (!ServiceRegistry.Contains(id))
                                    ServiceRegistry.Add(services.get(i));
                            }

                            _state = InitState.Finished;
                        }
                        break;
                    }
                case Finished:
                    {
                        break;
                    }
            }

            if (_requestThread.IsRunning())
                _requestThread.Signal();
        }
    }
}
