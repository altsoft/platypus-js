/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.mina.platypus;

import com.eas.client.threetier.Request;
import com.eas.client.threetier.binary.PlatypusRequestReader;
import com.eas.client.threetier.binary.RequestsTags;
import com.eas.proto.CoreTags;
import com.eas.proto.ProtoReader;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

/**
 *
 * @author pk
 */
public class RequestDecoder extends CumulativeProtocolDecoder {

    public static class RequestEnvelope {

        public Request request;
        public String ticket;

        public RequestEnvelope(Request aRequest, String aTicket) {
            request = aRequest;
            ticket = aTicket;
        }
    }

    public RequestDecoder() {
        super();
    }

    @Override
    protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        String ticket = null;
        int start = in.position();
        int tag = 0, tagSize = 0;
        do {
            if (in.remaining() < 5) {
                in.position(start);
                return false;
            }
            tag = in.get() & 0xff;
            tagSize = in.getInt();
            if (in.remaining() < tagSize) {
                in.position(start);
                return false;
            }
            if (tag == CoreTags.TAG_SESSION_TICKET) {
                byte[] ticketBuf = new byte[tagSize];
                in.get(ticketBuf);
                ticket = new String(ticketBuf, "UTF-16LE");
            } else {
                in.skip(tagSize);
            }
        } while (tag != RequestsTags.TAG_REQUEST_END);

        int position = in.position();
        int limit = in.limit();
        try {
            in.position(start);
            in.limit(position);
            final ProtoReader requestReader = new ProtoReader(in.slice().asInputStream());
            Request request = PlatypusRequestReader.read(requestReader);
            out.write(new RequestEnvelope(request, ticket));
            return true;
        } finally {
            in.position(position);
            in.limit(limit);
        }
    }
}
