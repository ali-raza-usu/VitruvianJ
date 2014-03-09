package vitruvianJ.distribution;
import java.lang.ref.*;
import java.lang.reflect.*;

import vitruvianJ.core.EventWaitHandle;

public interface IAsyncResult
{
    
    // Summary:
    //     Gets a System.Threading.WaitHandle that is used to wait for an asynchronous
    //     operation to complete.
    //
    // Returns:
    //     A System.Threading.WaitHandle that is used to wait for an asynchronous operation
    //     to complete.
    EventWaitHandle getAsyncWaitHandle();
    //
    // Summary:
    //     Gets an indication of whether the asynchronous operation completed synchronously.
    //
    // Returns:
    //     true if the asynchronous operation completed synchronously; otherwise, false.
    boolean CompletedSynchronously();
    //
    // Summary:
    //     Gets an indication whether the asynchronous operation has completed.
    //
    // Returns:
    //     true if the operation is complete; otherwise, false.
    boolean IsCompleted();
}