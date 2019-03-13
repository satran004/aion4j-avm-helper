package org.aion4j.avm.helper.exception;

public class AVMRuntimeException extends RuntimeException {

    public AVMRuntimeException(String msg) {
        super(msg);
    }

    public AVMRuntimeException(String msg, Exception ex) {
        super(msg, ex);
    }
}
