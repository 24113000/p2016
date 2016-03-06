package org.sbezgin.p2016.services.file;

public class FolderIsNotEmpty extends FileOperationException {
    public FolderIsNotEmpty() {
        super();
    }

    public FolderIsNotEmpty(String message) {
        super(message);
    }

    public FolderIsNotEmpty(String message, Throwable cause) {
        super(message, cause);
    }

    public FolderIsNotEmpty(Throwable cause) {
        super(cause);
    }

    protected FolderIsNotEmpty(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
