package org.sbezgin.p2016.services.file;

import org.sbezgin.p2016.common.P2016Exception;

public class FileOperationException extends P2016Exception {
    public FileOperationException() {
        super();
    }

    public FileOperationException(String message) {
        super(message);
    }

    public FileOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileOperationException(Throwable cause) {
        super(cause);
    }

    protected FileOperationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
