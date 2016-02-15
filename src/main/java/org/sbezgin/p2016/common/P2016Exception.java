package org.sbezgin.p2016.common;

public class P2016Exception extends RuntimeException {
    public P2016Exception() {
        super();
    }

    public P2016Exception(String message) {
        super(message);
    }

    public P2016Exception(String message, Throwable cause) {
        super(message, cause);
    }

    public P2016Exception(Throwable cause) {
        super(cause);
    }

    protected P2016Exception(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
