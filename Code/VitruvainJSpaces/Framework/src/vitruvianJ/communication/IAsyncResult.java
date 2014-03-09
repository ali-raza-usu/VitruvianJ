
package vitruvianJ.communication;

import vitruvianJ.core.EventWaitHandle;
    public interface IAsyncResult
    {
        // Summary:
        //     Gets a user-defined object that qualifies or contains information about an
        //     asynchronous operation.
        //
        // Returns:
        //     A user-defined object that qualifies or contains information about an asynchronous
        //     operation.
    	
    	
        public Object getAsyncState();
        //
        // Summary:
        //     Gets a System.Threading.WaitHandle that is used to wait for an asynchronous
        //     operation to complete.
        //
        // Returns:
        //     A System.Threading.WaitHandle that is used to wait for an asynchronous operation
        //     to complete.
        public EventWaitHandle getAsyncWaitHandle();
        //
        // Summary:
        //     Gets an indication of whether the asynchronous operation completed synchronously.
        //
        // Returns:
        //     true if the asynchronous operation completed synchronously; otherwise, false.
        public boolean getCompletedSynchronously();
        //
        // Summary:
        //     Gets an indication whether the asynchronous operation has completed.
        //
        // Returns:
        //     true if the operation is complete; otherwise, false.
        public boolean IsCompleted();
    }
