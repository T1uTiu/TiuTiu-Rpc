package com.tiutiu.protocol;

import java.io.Serializable;

public class RpcProtocol<T> implements Serializable {
    public static final int HEADER_TOTAL_LEN =  17;
    public static final short MAGIC = 0x10;
    public static final byte VERSION = 0x1;
    public static final byte MSG_TYPE_REQUEST = 0;
    public static final byte MSG_TYPE_RESPONSE = 1;
    private MsgHeader msgHeader;
    private T body;

    public MsgHeader getMsgHeader() {
        return msgHeader;
    }

    public void setMsgHeader(MsgHeader msgHeader) {
        this.msgHeader = msgHeader;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }
}
