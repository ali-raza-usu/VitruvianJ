package vitruvianJ.distribution.session.processors;

import vitruvianJ.communication.session.Message;
import vitruvianJ.communication.session.protocols.IProcessor;
import vitruvianJ.communication.session.protocols.ProtocolSession;
import vitruvianJ.distribution.ObjectBroker;
import vitruvianJ.distribution.ReplyInitializeObject;
import vitruvianJ.distribution.sessions.ObjectSession;
import vitruvianJ.distribution.sessions.messages.RequestInitializeObject;
import vitruvianJ.logging.JLogger;

public class ObjectInitializationProcessor implements IProcessor
{
    private JLogger _logger = new JLogger(ObjectInitializationProcessor.class);
    private ObjectSession _session = null;

    

	public boolean getInitialized()
	{
		return _session != null;
	}

	public void Init(ProtocolSession session)
	{
		//_logger.Debug("ObjectInitialization Processor : session ");
        _session = (ObjectSession)session;
	}

    public void ProcessMessage(Message message)
	{
    	//_logger.Debug("ObjectInitialization Processor : ProcessMessage() " + message);
        if (message instanceof RequestInitializeObject)
        {
            RequestInitializeObject request = (RequestInitializeObject)message;

            // Remove the object from the broker's known list of
            // objects, so that it will resend the initial state
            ObjectBroker.RemoveKnownObjectFromBroker(request.getBrokerId(), request.getProxyId());
            ReplyInitializeObject replyObj =  new ReplyInitializeObject(request.getMessageId(), request.getBrokerId(), request.getProxyId(), request.getValue());
           //  _logger.Debug("message reply Id " + replyObj.toString() + " message id " + replyObj.getMessageId() + " value : " + replyObj.getValue());
            _session.Send(replyObj);
        }
	}

	public void Cleanup()
	{
        _session = null;
	}



}
