package vitruvianJ.core;
import java.lang.*;

import org.apache.commons.lang3.time.*;//patime.JStopWatch;
	/// <summary>
	/// Represents a high-resolution stopwatch that is both thread and processor safe.
	/// </summary>
	public class JStopWatch  extends StopWatch
	//sealed classs
	{
		
	}
//	
//	{
//		private static _BackgroundThread _backgroundThread = null;
//		private static EventWaitHandle _tickCountUpdateRequest = new EventWaitHandle();//false, EventResetMode.AutoReset);
//		private static EventWaitHandle _tickCountUpdateDone = new EventWaitHandle();//false, EventResetMode.AutoReset);
//
//		
//		/// <summary>
//		/// Stopwatch's frequency in CPU ticks per second
//		/// </summary>
//		private static long _frequency = 0;
//
//		/// <summary>
//		/// Current CPU tick count
//		/// </summary>
//		private static long _currentTickCount = 0;
//
//		/// <summary>
//		/// The tick count of the last Reset.
//		/// </summary>
//		private long _startTickCount = 0;
//
//		/// <summary>
//		/// A thread to make sure CPU tick counts are always read from the same CPU
//		/// </summary>
//		public class _BackgroundThread extends Thread
//		{
//			public void run()
//			{
//				backgroundThreadFunc();
//			}
//		}
//		private static void backgroundThreadFunc()
//		{
//			//LockThreadToProcessor();
//			_frequency = 1558564;//Stopwatch.Frequency;
//			while (true)
//			{
//				try {
//					_tickCountUpdateRequest.WaitOne();
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}//WaitOne();
//				_currentTickCount = 1336977229;// StopWatch.GetTimestamp();
//				_tickCountUpdateDone.Set();//Set();
//			}
//		}
///*
//		[DllImport("kernel32.dll", SetLastError = true)]
//		private static extern bool GetProcessAffinityMask(IntPtr hProcess, out UIntPtr lpProcessAffinityMask,
//		                                                  out UIntPtr lpSystemAffinityMask);
//
//		[DllImport("kernel32.dll")]
//		private static extern UIntPtr SetThreadAffinityMask(IntPtr hThread, UIntPtr dwThreadAffinityMask);
//
//		[DllImport("kernel32.dll")]
//		private static extern IntPtr GetCurrentProcess();
//
//		[DllImport("kernel32.dll")]
//		private static extern IntPtr GetCurrentThread();
//*/
//		/// <summary>
//		/// Lock the current thread to the first processor.
//		/// </summary>
///*
//		private static void LockThreadToProcessor()
//		{
//			UIntPtr lpProcessAffinityMask;
//			UIntPtr lpSystemAffinityMask;
//
//			if (GetProcessAffinityMask(GetCurrentProcess(), out lpProcessAffinityMask, out lpSystemAffinityMask) &&
//			    (lpProcessAffinityMask != UIntPtr.Zero))
//			{
//				UIntPtr dwThreadAffinityMask =
//					(UIntPtr) (lpProcessAffinityMask.ToUInt64() & (~lpProcessAffinityMask.ToUInt64() + 1));
//				SetThreadAffinityMask(GetCurrentThread(), dwThreadAffinityMask);
//			}
//		}
//*/
//		/// <summary>
//		/// Static Constructor
//		/// </summary>
//		/// <note>
//		/// The System.Diagnostics.Stopwatch (and hence, performance counters) get their tick marks from the CPU that the thread is on.
//		/// StopWatch creates a thread to make sure the tick counts are always read on the same CPU.
//		/// </note>
//		
//		
//		/// <summary>
//		/// The current tick count.
//		/// </summary>
//		static public long getCurrentTickCount()
//		{
//
//				_tickCountUpdateRequest.Set();//Set();
//				try {
//					_tickCountUpdateDone.WaitOne();
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}//WaitOne();
//				return _currentTickCount;
//		}
//
//		/// <summary>
//		/// Initializes a new instance of the StopWatch class.
//		/// </summary>
//		/// <exception cref="NotSupportedException">The system does not have a high-resolution performance counter.</exception>
//		public StopWatch()
//		{
//			_backgroundThread = new _BackgroundThread();// new ThreadStart(_backgroundThreadFunc));
//			_backgroundThread.setDaemon(true);// IsBackground = true;
//			_backgroundThread.start();//Start();
//			Reset();
//			
//		}
//
//		/// <summary>
//		/// Resets the stopwatch. This method should be called when you start measuring.
//		/// </summary>
//		/// <exception cref="NotSupportedException">The system does not have a high-resolution performance counter.</exception>
//		public void Reset()
//		{
//			
//			synchronized(_backgroundThread) // thread-safe
//			{
//				_tickCountUpdateRequest.Set();//Set();
//				try {
//					_tickCountUpdateDone.WaitOne();
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}//WaitOne();
//				_startTickCount = _currentTickCount;
//			}
//			
//		}
//
//		/// <summary>
//		/// Get the current elapsed time in ms.
//		/// </summary>
//		/// <returns>The current tick count in ms.</returns>
//		public long GetCurrentTime_ms()
//		{
//			synchronized (_backgroundThread) // thread-safe
//			{
//				_tickCountUpdateRequest.Set();//Set();
//				try {
//					_tickCountUpdateDone.WaitOne();
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}//WaitOne();
//				return _currentTickCount*1000/_frequency;
//			}
//		}
//
//		/// <summary>
//		/// Get the time elapsed, in seconds, since the last Reset() or since  
//		/// creation if Reset() hasn't been called since then.
//		/// </summary>
//		/// <returns>Elapsed time in seconds.</returns>
//		public double GetElapsed_s()
//		{
//			synchronized (_backgroundThread) // thread-safe
//			{
//				_tickCountUpdateRequest.Set();//Set();
//				try {
//					_tickCountUpdateDone.WaitOne();
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}//WaitOne();
//				return (double) (_currentTickCount - _startTickCount)/(double) _frequency;
//			}
//		}
//
//		/// <summary>
//		/// Get the time elapsed, in milliseconds, since the last Reset() or since  
//		/// creation if Reset() hasn't been called since then.
//		/// </summary>
//		/// <returns>Elapsed time in milliseconds.</returns>
//		public long GetElapsed_ms()
//		{
//			synchronized (_backgroundThread) // thread-safe
//			{
//				_tickCountUpdateRequest.Set();//Set();
//				try {
//					_tickCountUpdateDone.WaitOne();
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}//WaitOne();				
//				try{
//					return (_currentTickCount - _startTickCount)*1000/_frequency;
//				}catch(Exception ex)
//				{
//					return 0;
//				}
//			}
//		}
//		
//	}
