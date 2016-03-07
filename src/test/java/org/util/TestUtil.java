package org.util;

import org.springframework.test.context.transaction.TestTransaction;

public class TestUtil {
    public static void commitAndStartTransaction() {
        TestTransaction.flagForCommit();
        TestTransaction.end();
        TestTransaction.start();
    }
}
