package jarg.jrcm.networking.dependencies.netrequests;

import com.ibm.disni.verbs.IbvWC;
import jarg.jrcm.networking.communicators.RdmaCommunicator;
import jarg.jrcm.networking.communicators.impl.ActiveRdmaCommunicator;
import jarg.jrcm.networking.dependencies.netbuffers.NetworkBufferManager;
import jarg.jrcm.networking.dependencies.netrequests.types.PostedRequestType;
import jarg.jrcm.networking.dependencies.netrequests.types.WorkRequestType;

import static jarg.jrcm.networking.dependencies.netrequests.types.WorkRequestType.*;

/**
 * Provides available {@link WorkRequestProxy WorkRequestProxies} to applications.
 * It also allows releasing WorkRequestProxies, so that they can be reused.
 * Finally, it can "translate" a Work Completion Event notification from the Network Card
 * about an RDMA Work Request to a WorkRequestProxy that represents
 * this Work Request. Thus applications need only use WorkRequestProxies to transfer
 * data or to handle network notifications.
 */
public interface WorkRequestProxyProvider {

    /**
     * Get the {@link NetworkBufferManager} that holds the network communication buffers
     * that the Work Requests use.
     * @return the {@link NetworkBufferManager} that holds the network communication buffers.
     */
    public NetworkBufferManager getBufferManager();

    /**
     * Set the {@link NetworkBufferManager} that holds the network communication buffers
     * that the Work Requests use.
     */
    public void setBufferManager(NetworkBufferManager bufferManager);

    /**
     * Get the {@link RdmaCommunicator} that this provider belongs to.
     * @return the {@link RdmaCommunicator} that this provider belongs to.
     */
    public RdmaCommunicator getCommunicator();

    /**
     * Set the {@link RdmaCommunicator} that this provider belongs to.
     */
    public void setCommunicator(RdmaCommunicator communicator);

    /**
     * <p>
     * Applications need to call this before posting a postSend-type
     * Work Request to the NIC (all one-sided RDMA operations and two-sided
     * SEND).
     * It looks for an available Work Request (WR) id for postSend-type
     * operations and if it finds one, it returns a {@link WorkRequestProxy}
     * that contains information about the WR.
     * If it doesn't find an available WR id, it will block until there is one
     * (e.g. after releasing a WorkRequestProxy).
     * </p>
     *
     * @param requestType specifies the type of request.
     *
     * @return the {@link WorkRequestProxy} object that
     * contains information about the actual Work Request.
     */
    WorkRequestProxy getPostSendRequestBlocking(WorkRequestType requestType);

    /**
     * <p>
     * This method is like {@link WorkRequestProxyProvider#getPostSendRequestBlocking(WorkRequestType)},
     * but if it doesn't find an available WR id, it will return null and will not block.
     * </p>
     *
     * @param requestType specifies the type of request.
     *
     * @return the {@link WorkRequestProxy} object that
     * contains information about the actual Work Request or null if no
     * Work Request id is available.
     */
    WorkRequestProxy getPostSendRequestNow(WorkRequestType requestType);

    /* Unlike postSend-type requests, which are requests to send data, postRecv-type requests
    * don't need to be posted by applications explicitly.
    * They can be all posted at the beginning of the application and re-posted after their use
    * automatically. Applications need only specify that they do not longer need a
    * postRecv request (have finished using the received data).
    * Therefore, there's no need to use methods like the ones above for postRecv requests. */

    /**
     * It makes a Work Request (WR) available for reuse again.
     * @param workRequestProxy the object containing information about the WR.
     */
    void releaseWorkRequest(WorkRequestProxy workRequestProxy);

    /**
     * Retrieves a {@link WorkRequestProxy} representing the Work Request
     * of a {@link IbvWC Work Completion Event}.
     * It should ONLY be used when handling completion events and
     * want to get a proxy object that represents the Work Request for
     * which the completion event was triggered.
     * @return the proxy object representing the Work Request for which the
     * completion event fired.
     */
    WorkRequestProxy getWorkRequestProxyForWc(IbvWC workCompletionEvent);

    /**
     * Helper method to associate a {@link WorkRequestType} with an
     * integer operation code used in Work Completion Events.
     * @param type the type of Work Request.
     * @return the Work Completion operation code.
     */
    default int getWcOperationCodeForWorkRequest(WorkRequestType type){
        switch (type){
            case TWO_SIDED_SEND_SIGNALED:
                return IbvWC.IbvWcOpcode.IBV_WC_SEND.getOpcode();
            case TWO_SIDED_RECV:
                return IbvWC.IbvWcOpcode.IBV_WC_RECV.getOpcode();
            case ONE_SIDED_WRITE_SIGNALED:
                return IbvWC.IbvWcOpcode.IBV_WC_RDMA_WRITE.getOpcode();
            case ONE_SIDED_READ_SIGNALED:
                return IbvWC.IbvWcOpcode.IBV_WC_RDMA_READ.getOpcode();
        }
        return -1;
    }

    /**
     * Helper method to associate an integer operation code used in
     * Work Completion Events with a {@link WorkRequestType}.
     * @param wcOpcode the Work Completion operation code.
     * @return the type of Work Request.
     */
    default WorkRequestType getWorkRequestTypeForWcOperationCode(int wcOpcode){
        if(wcOpcode == IbvWC.IbvWcOpcode.IBV_WC_SEND.getOpcode()){
            return TWO_SIDED_SEND_SIGNALED;
        }
        if(wcOpcode == IbvWC.IbvWcOpcode.IBV_WC_RECV.getOpcode()){
            return TWO_SIDED_RECV;
        }
        if(wcOpcode == IbvWC.IbvWcOpcode.IBV_WC_RDMA_WRITE.getOpcode()){
            return ONE_SIDED_WRITE_SIGNALED;
        }
        if(wcOpcode == IbvWC.IbvWcOpcode.IBV_WC_RDMA_READ.getOpcode()){
            return ONE_SIDED_READ_SIGNALED;
        }
        return null;
    }
}
