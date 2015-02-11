/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2015] Codenvy, S.A.
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.im.command;

import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

/**
 * @author Anatoliy Bazko
 */
public class TestDetectRedHatVersionCommand {

    @Test
    public void testCommand() throws Exception {
        DetectRedHatVersionCommand command = new DetectRedHatVersionCommand();
        try {
            String output = command.execute();
            assertFalse(output.isEmpty());
        } catch (CommandException e) { // won't be failed if test is run under RedHat Linux
            assertTrue(e.getMessage().contains("Output: ; Error: This isn't RedHat Linux."));
        }
    }
}
