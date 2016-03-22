package org.sbezgin.p2016.service.file;

import org.sbezgin.p2016.common.P2016Exception;

public class FileChangedException extends P2016Exception {
    public FileChangedException() {
        super();
    }

    public FileChangedException(String message) {
        super(message);
    }

    public FileChangedException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileChangedException(Throwable cause) {
        super(cause);
    }

    protected FileChangedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
