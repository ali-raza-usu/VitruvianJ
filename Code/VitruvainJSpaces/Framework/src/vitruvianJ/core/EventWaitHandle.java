package vitruvianJ.core;

//import com.sun.corba.se.impl.orbutil.threadpool.TimeoutException;

public class EventWaitHandle
{
	private int setFlag = 0;
	
	public void WaitOne()
	{
		synchronized(this)
		{
			try {
				if (setFlag==0)
					this.wait();
				setFlag--;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void WaitOne(long time)
	{
		synchronized(this)
		{
			try {
				if (setFlag==0)
				  this.wait(time);
				setFlag--;
			} catch (InterruptedException e) {
			}			
		}
	}
	public void Set()
	{
		synchronized(this)
		{
			setFlag++;
			this.notify();
		}
	}

}
