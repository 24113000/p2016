package org.sbezgin.p2016.service.file;

import org.sbezgin.p2016.common.P2016Exception;

public class FileAccessDeiniedException extends P2016Exception {
    public FileAccessDeiniedException() {
        super();
    }

    public FileAccessDeiniedException(String message) {
        super(message);
    }

    public FileAccessDeiniedException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileAccessDeiniedException(Throwable cause) {
        super(cause);
    }

    protected FileAccessDeiniedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
