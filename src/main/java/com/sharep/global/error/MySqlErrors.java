package com.sharep.global.error;

import java.sql.SQLException;

public final class MySqlErrors {
    private static final int DUPLICATE_KEY = 1062;

    private MySqlErrors() {
    }

    public static boolean isDuplicateKey(Throwable exception) {
        for (Throwable cause = exception; cause != null; cause = cause.getCause()) {
            if (cause instanceof SQLException sqlException && sqlException.getErrorCode() == DUPLICATE_KEY) {
                return true;
            }
        }
        return false;
    }
}
