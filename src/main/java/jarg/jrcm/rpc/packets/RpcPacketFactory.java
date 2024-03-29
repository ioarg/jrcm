package jarg.jrcm.rpc.packets;

import jarg.jrcm.networking.dependencies.netrequests.WorkRequestProxy;

/**
 * A factory for RPC packets. This interface makes assumptions
 * about the types of parameters and the type of the packet
 * to return. For a more generic approach, use {@link GenericRpcPacketFactory}.
 * @param <P> the type of packet to generate.
 */
@FunctionalInterface
public interface RpcPacketFactory<P extends AbstractRpcPacket> {

    /**
     * Generates an RPC packet.
     * @param workRequestProxy associates a packet with a {@link WorkRequestProxy} and hence, with a
     *                        network buffer that will contain its data.
     * @param messageType the type of the message (e.g. request, response or error - see {@link RpcMessageType}).
     * @param operationType determines the type of the RPC method that the packet is created for.
     * @return the generated RPC packet.
     */
    P generatePacket(WorkRequestProxy workRequestProxy, byte messageType, int operationType);
}
