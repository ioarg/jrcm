package jarg.rdmarpc.networking.dependencies.netrequests;

import com.ibm.disni.verbs.IbvWC;
import jarg.rdmarpc.networking.communicators.RdmaCommunicator;
import jarg.rdmarpc.networking.communicators.impl.ActiveRdmaCommunicator;
import jarg.rdmarpc.networking.dependencies.netbuffers.NetworkBufferManager;
import jarg.rdmarpc.networking.dependencies.netrequests.types.WorkRequestType;

/**
 * Provides {@link WorkRequestProxy WorkRequestProxies} to applications,
 * while also enabling releasing and reusing Work Requests.
 */
public interface WorkRequestProxyProvider {

    /**
     * <p>
     * Applications need to call this before posting a postSend-type
     * Work Request to the NIC.
     * It will look for an available Work Request (WR) id for postSend-type
     * operations and if it finds one, it returns a {@link WorkRequestProxy}
     * that contains information that the application needs about
     * the WR. If it doesn't find an available WR id, it will block
     * until there is one.
     * </p>
     * <p>
     * Once a {@link WorkRequestProxy} is returned, the application can
     * use it to access the buffer associated with a WR and write data to
     * be sent over the network. Afterwards, the application needs to
     * pass the {@link WorkRequestProxy} back to the
     * {@link ActiveRdmaCommunicator RdmaRpcEndpoint},
     * in order for the latter to transmit the written data.
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
     * Applications need to call this before posting a postSend-type
     * Work Request to the NIC.
     * It will look for an available Work Request (WR) id for postSend-type
     * operations and if it finds one, it returns a {@link WorkRequestProxy}
     * that contains information that the application needs about
     * the WR. If it doesn't find an available WR id, it will return null
     * and will not block.
     * </p>
     * <p>
     * Once a {@link WorkRequestProxy} is returned, the application can
     * use it to access the buffer associated with a WR and write data to
     * be sent over the network. Afterwards, the application needs to
     * pass the {@link WorkRequestProxy} back to the
     * {@link ActiveRdmaCommunicator RdmaRpcEndpoint},
     * in order for the latter to transmit the written data.
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
    * automatically. The applications need only specify that they do not longer need a
    * postRecv request (have finished using the received data).
    * Therefore, there's no need to use methods like the ones above for postRecv requests. */


    /**
     * It makes a Work Request (WR) available for reuse again.
     * @param workRequestProxy the object containing information about the WR.
     */
    void releaseWorkRequest(WorkRequestProxy workRequestProxy);


    /**
     * Constructs a {@link WorkRequestProxy} by using the information inside
     * a {@link IbvWC Work Completion Event}.
     * It should ONLY be used when handling completion events and
     * want to get a proxy object that represents the Work Request for
     * which the completion event was fired.
     * @return the proxy object representing the Work Request for which the
     * completion event fired.
     */
    WorkRequestProxy getWorkRequestProxyForWc(IbvWC workCompletionEvent);
}