package jarg.concerrt.requests;

import com.ibm.disni.verbs.IbvMr;
import com.ibm.disni.verbs.IbvSendWR;
import com.ibm.disni.verbs.SVCPostSend;

import java.io.IOException;
import java.util.List;

/**
 * Defines a "send" Work Request for the NIC
 */
public abstract class SendRequest extends BasicWorkRequest{
    IbvSendWR sendWR;
    int opCode;
    int flags;

    public SendRequest(IbvMr memoryRegion){
        super(memoryRegion);
        // attach scatter/gather list to the work request
        sendWR.setSg_list(sgeList);
    }

    /* *************************************************************
     * Implementing Abstract Methods
     * *************************************************************/

    @Override
    public void setRequestId(int workRequestId) {
        sendWR.setWr_id(workRequestId);
    }

    @Override
    public void setBufferMemoryAddress(long address) {
        requestSge.setAddr(address);
    }


    /* *************************************************************
     * Getters
     * *************************************************************/

    public IbvSendWR getSendWR() {
        return sendWR;
    }

    public int getOpCode() {
        return opCode;
    }

    public int getFlags() {
        return flags;
    }
}
