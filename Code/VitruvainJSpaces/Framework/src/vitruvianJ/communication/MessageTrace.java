package vitruvianJ.communication;

import vitruvianJ.logging.JLogger;

public class MessageTrace
{
    static private JLogger _logger =  new JLogger(MessageTrace.class);

    static public JLogger getLogger()
    {
        return _logger; 
    }
}
