package org.sbezgin.p2016.service.file;

import org.sbezgin.p2016.common.P2016Exception;

public class FileAccessDeniedException extends P2016Exception {
    public FileAccessDeniedException() {
        super();
    }

    public FileAccessDeniedException(String message) {
        super(message);
    }

    public FileAccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileAccessDeniedException(Throwable cause) {
        super(cause);
    }

    protected FileAccessDeniedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
