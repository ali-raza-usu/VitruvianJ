package vitruvianJ.distribution.session.processors;

import vitruvianJ.communication.session.Message;
import vitruvianJ.communication.session.protocols.IProcessor;
import vitruvianJ.communication.session.protocols.ProtocolSession;
import vitruvianJ.distribution.sessions.ObjectSession;
import vitruvianJ.distribution.sessions.messages.RequestAddService;
import vitruvianJ.distribution.sessions.messages.RequestRemoveService;
import vitruvianJ.logging.JLogger;
import vitruvianJ.services.IService;
import vitruvianJ.services.ServiceRegistry;

public class ServicesProcessor implements IProcessor
{
    private static JLogger _logger = new JLogger(ServicesProcessor.class);

    private ObjectSession _session = null;

    public ServicesProcessor()
    {
    }



	public boolean getInitialized()
	{
	 return _session != null;	
	}

	public void Init(ProtocolSession session)
	{
        _session = (ObjectSession)session;
	}

    public void ProcessMessage(Message message) throws Exception
	{
        if (message instanceof RequestAddService)
        {
            RequestAddService request = (RequestAddService)message;

            if (_logger.IsDebugEnabled())
                _logger.DebugFormat("Requesting Add Service : %1s", request.getService().getId());

            AddService(request.getService());
        }
        else if (message instanceof RequestRemoveService)
        {
            RequestRemoveService request = (RequestRemoveService)message;

            if (_logger.IsDebugEnabled())
                _logger.DebugFormat("Requesting Remove Service : %1s ", request.getService().getId());

            RemoveService(request.getService());
        }
        else
            throw new Exception("Incorrect message for processor.");
	}

	public void Cleanup()
	{
        _session = null;
	}

    private void AddService(IService service)
    {
        if (!ServiceRegistry.Contains(service.getId()))
            ServiceRegistry.Add(service);
    }

    private void RemoveService(IService service)
    {
        ServiceRegistry.Remove(service);
    }
	
}
