package vitruvianJ.core;
import java.lang.Thread;

import vitruvianJ.communication.session.MessageEventArgs;
import vitruvianJ.eventargs.EventArgs;
import vitruvianJ.events.Delegate;
import vitruvianJ.events.Event;
import vitruvianJ.events.EventRegistry;
import vitruvianJ.events.IEventSubject;
import vitruvianJ.events.ReturnDelegate;
import vitruvianJ.logging.JLogger;

import vitruvianJ.serialization.xml.XmlSerializer;
import vitruvianJ.services.IRunnable;


	/// <summary>
	/// A delegate used to exit a signal sync thread from the executing function.
	/// </summary>
	/// <returns>A value indicating exit status.</returns>
	

	/// <summary>
	/// This class encapsulates a System.Thread.
	/// The thread that sleeps until it is signaled.
	/// Once signaled it calls the given function.  When
	/// the function completes, the thread goes back to sleep.
	/// Starting and stopping of the thread is synchronized with
	/// the calling thread.
	/// </summary>
	public class SignalSyncThread implements IEventSubject, Runnable 
	{
		private static  JLogger _logger = new JLogger(SignalSyncThread.class);
		public static final Class[] MSG_OUTPUT_ARGS = {boolean.class};		
		public Event SignalSyncHandler= new Event(this);//(MSG_OUTPUT_ARGS, Void.TYPE);
		
		private  EventWaitHandle _ewhThreadSync = new EventWaitHandle();//false, EventResetMode.AutoReset);
		private EventWaitHandle _ewhRun = new EventWaitHandle();//EventWaitHandle(false, EventResetMode.AutoReset);
		
		private Thread _thread = null;
		private boolean _exitThread = false;
        private boolean _isSuicidal = false;
		private String _name = "";

		private Delegate _function = null;
		private Delegate _exitingFunction = null;

		private Object _syncObject = new Object();

		private boolean _trueToExit = true;

		/// <summary>
		/// Construct a SignalSyncThread.
		/// </summary>
		/// <param name="name">The name of the thread.</param>
		/// <param name="function">The function the thread should call when signaled.</param>
		public SignalSyncThread(String name, Delegate function)
		{
			
			Thread thread = Thread.currentThread();
//			_logger.Debug("SignalSyncThread Constructor : Thread %1$2s, name %2$2s",thread.getId(), name);
			_name = name;
			_function = function;
		}

		/// <summary>
		/// Construct a SignalSyncThread.
		/// </summary>
		/// <param name="name">The name of the thread.</param>
		/// <param name="function">The function the thread should call when signaled.  This thread
		/// may cause the SignalSyncThread to Stop, so that future Signals are ignored.</param>
		/// <param name="trueToExit">If the function wants to exit by returning true, then True, otherwise False</param>
		public SignalSyncThread(String name, Delegate function, boolean trueToExit)
		{
			_name = name;
			_exitingFunction = function;
			_trueToExit = trueToExit;			
		}

		public boolean IsRunning()
		{			
                // it's shutting down
                if (_exitThread)
                    return false;

				return (_thread!= null);		
		}

		/// <summary>
		/// Start the thread.
		/// This will cause the function to be executed once.
		/// <note>The function will not return until the thread is started.</note>
		/// </summary>
		/// <param name="signal">Signal the function immediately.</param>
		/// <exception cref="ApplicationException">Thrown when the thread is already running.</exception>
		
		void print(String value)
		{
			System.out.println(value);
		}
		
		
		public void Start(boolean signal)
		{
			synchronized (_syncObject) {
						
				if (_thread != null)
					return;				
				//_ewhRun.Reset();
				_exitThread = false;
                _isSuicidal = false;
                Thread currentThread = Thread.currentThread();                
				_thread = new Thread(this);
				//_logger.Debug("Setting up to start: "+ _name + " new Thread ID=" + _thread.getId()+" from current Thead ID=" + currentThread.getId());
				_thread.setDaemon(true);
				_thread.setName(_name);
				_thread.start();								 
				_ewhThreadSync.WaitOne();																	
				if (signal)
				{
					//_logger.Debug("Starting : "+_name);
					_ewhRun.Set();				
				}
					
			}
		}
		
		
		/// <summary>
		/// Stop the thread.
		/// <note>The function will not return until the thread is stopped.</note>
		/// </summary>
		/// <exception cref="ApplicationException">Thrown when the thread is already stopped.</exception>
		/// <exception cref="ApplicationException">Thrown when the calling thread is the internal executing thread.</exception>
		public void Stop() throws Exception
		{
			//_logger.Debug("SignalSyncThread : Stop");
			synchronized (_syncObject) // thread-safe
			{
				// Don't remove this exception.  It shows that there are shutdown issues.
				// The startup/shutdown should be exactly symmetrical.
				if (_thread == null)
					throw new Exception(String.format("Thread %1s is already stopped.", _name));

				// The thread calling stop cannot be _thread.  This would cause the system to hang.
                // throw new ApplicationException("Suicidal Thread.  Inspect the call stack to find the problem.");
                _isSuicidal = IsRunThread();

                _exitThread = true;

                if (!_isSuicidal)
                {
                    _ewhRun.Set();
                    _ewhThreadSync.WaitOne();
                    _thread = null;
                }
			}
		}

		/// <summary>
		/// Check if the current thread is the run thread.
		/// </summary>
		/// <returns>True if the current thread is the run thread.</returns>
		public boolean IsRunThread()
		{
			return (_thread == Thread.currentThread());
		}

		/// <summary>
		/// Signal the thread to run the function.
		/// </summary>
		public void Signal()
		{
			_ewhRun.Set();
		}

		/// <summary>
		/// Sleep for the given amount of time.
		/// </summary>
		/// <param name="time">The amount of time (ms) to sleep.</param>
		public void Sleep(long time) 
		{
			// The thread calling sleep must be the running thread.  Otherwise it could cause the system to hang.
			Thread _currentThread = Thread.currentThread();
			//_logger.Debug("Sleep() : current Thead ID=" + _currentThread.getId() + " _thread ID=" + _thread.getId());

			if (_thread != Thread.currentThread())
			{
				_logger.Error("Cross-thread problem in the calling of Sleep()");				
				return;
			}
			try
			{
				_ewhRun.WaitOne(time);			
				// signal the handle if it didn't
				// wait the full amount of time
				_ewhRun.Set();
			}catch(Exception e){}
		}

		

		@Override
		public EventArgs getEventArgs() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void run() {
			//print("Run : _ewhThreadSync.Set();");
			_ewhThreadSync.Set();
			Thread currentThread = Thread.currentThread();
			//_logger.Debug("Run() : current Thead ID=" + currentThread.getId() + " _thread ID=" + _thread.getId());
			do
			{
				//_logger.Debug("Run : _ewhRun.WaitOne();");
				_ewhRun.WaitOne();				
				
			//	_logger.Debug(" exitThread : " + _exitThread);			
				if (_exitThread){
					break;
				}

				try
				{
				//	_logger.Debug("Run() in try block : current Thead ID = " + currentThread.getId() + " _thread ID=" + _thread.getId());
//					if (_function !=null)
//						_logger.Debug("_function : " + _function.toString());
//					else
//						_logger.Debug("_function is null");
					if (_function != null){
			//			_logger.Debug("Run() : _functiom in try block : current Thead ID = " + currentThread.getId() + " _thread ID= " + _thread.getId());						
						_function.invoke(null);
						//SignalSyncHandler.RaiseEvent();
					}
					else if (_exitingFunction != null)
					{
						//_logger.Debug("Run(): _exitingFunction in try block : current Thead ID = " + currentThread.getId() + " _thread ID = " + _thread.getId() + " is Interrupted : " + currentThread.isInterrupted() + " isAlive : " + currentThread.isAlive());
						boolean result = SignalSyncHandler.CallEvent((ReturnDelegate)_exitingFunction);	
						
						//_logger.Debug(" result : _exitingFunction : " + result);
						if (result && _trueToExit)
							break;
						else if (!result && !_trueToExit)
							break;						
					}
				}
				catch(Exception e)
				{}

			} while (!_exitThread);

			_thread = null;

            if (!_isSuicidal){
            	//_logger.Debug("Run : _ewhThreadSync.Set();");
			    _ewhThreadSync.Set();			    
            }
			
		} 
	}
