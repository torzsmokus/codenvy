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
package com.codenvy.im.console;

import jline.console.ConsoleReader;

import com.codenvy.commons.json.JsonParseException;
import com.codenvy.im.response.Response;
import com.google.inject.Singleton;

import org.fusesource.jansi.Ansi;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.ConnectException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.codenvy.im.response.Response.isError;
import static org.fusesource.jansi.Ansi.Color.GREEN;
import static org.fusesource.jansi.Ansi.Color.RED;
import static org.fusesource.jansi.Ansi.ansi;

/** @author Dmytro Nochevnov */
@Singleton
public class Console {
    public static final  Ansi   ERASE_LINE_ABOVE   = ansi().a(ansi().cursorUp(1).eraseLine(Ansi.Erase.ALL));
    public static final  Ansi   ERASE_CURRENT_LINE = ansi().eraseLine(Ansi.Erase.ALL);
    public static final  String CODENVY_PREFIX     = "[CODENVY] ";
    private static final Logger LOG                = Logger.getLogger(Console.class.getName());

    private final boolean       interactive;
    protected     ConsoleReader consoleReader;
    private final LoadingBar    loaderBar;
    private       int           rowCounter;

    private static Console consoleInstance;

    protected Console(boolean interactive) throws IOException {
        this.interactive = interactive;
        this.consoleReader = new ConsoleReader();
        this.loaderBar = new LoadingBar();
    }

    public static Console getInstance() throws IllegalStateException {
        if (consoleInstance == null) {
            throw new IllegalStateException("There is no console.");
        }

        return consoleInstance;
    }

    public static Console create(boolean interactive) throws IOException {
        consoleInstance = new Console(interactive);
        return consoleInstance;
    }

    public void println(String message) {
        print(message);
        println();
    }

    public void println() {
        print(ansi().newline());
        rowCounter++;
    }

    public void print(String message) {
        print((Object)message, false);
    }

    void printError(String message) {
        printError(message, false);
    }

    public void printError(String message, boolean suppressCodenvyPrompt) {
        print(ansi().fg(RED).a(message).newline().reset(), suppressCodenvyPrompt);
    }

    public void printProgress(int percents) {
        printProgress(createProgressBar(percents));
    }

    public void printProgress(String message) {
        saveCursorPosition();
        printWithoutCodenvyPrompt(message);
        restoreCursorPosition();
    }

    public void cleanCurrentLine() {
        print(ERASE_CURRENT_LINE);
    }

    public void cleanLineAbove() {
        print(ERASE_LINE_ABOVE);
    }

    public void printWithoutCodenvyPrompt(String message) {
        print((Object)message, true);
    }

    public void printSuccess(String message) {
        printSuccess(message, false);
    }

    public void printSuccessWithoutCodenvyPrompt(String message) {
        printSuccess(message, true);
    }

    private void printSuccess(String message, boolean suppressCodenvyPrompt) {
        print(ansi().fg(GREEN).a(message).newline().reset(), suppressCodenvyPrompt);
    }

    /** @return "true" only if only user typed line equals "y". */
    public boolean askUser(String prompt) throws IOException {
        print(prompt + " [y/N] ");
        String userAnswer = readLine();
        return userAnswer != null && userAnswer.equalsIgnoreCase("y");
    }

    /** @return line typed by user */
    public String readLine() throws IOException {
        return doReadLine(null);
    }

    public String readPassword() throws IOException {
        return doReadLine('*');
    }

    public void pressAnyKey(String prompt) throws IOException {
        print(prompt);
        consoleReader.readCharacter();
    }

    public void printErrorAndExit(Exception ex) {
        String errorMessage;

        if (isConnectionException(ex)) {
            errorMessage = "It is impossible to connect to Installation Manager Service. It might be stopped or it is starting up right now, " +
                           "please retry a bit later.";
        } else {
            errorMessage = Response.valueOf(ex).toJson();
        }

        LOG.log(Level.SEVERE, ex.getMessage(), ex);

        printError(errorMessage);

        if (!interactive) {
            exit(1);
        }
    }


    public void reset() throws IllegalStateException {
        try {
            consoleReader.getTerminal().restore();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public void printResponse(String response) throws JsonParseException {
        if (isError(response)) {
            printErrorAndExit(response);
        } else {
            println(response);
        }
    }

    /**
     * Print error message and exit with status = 1 if the command is not executing in interactive mode.
     */
    public void printErrorAndExit(String message) {
        LOG.log(Level.SEVERE, message);

        printError(message);

        if (!interactive) {
            exit(1);
        }
    }

    public void exit(int status) {
        System.exit(status);
    }

    public void showProgressor() {
        loaderBar.show();
    }

    public void restoreProgressor() {
        loaderBar.restore();
    }

    public void hideProgressor() {
        loaderBar.hide();
    }

    private void saveCursorPosition() {
        print(ansi().saveCursorPosition());
        rowCounter = 0;
    }

    private void restoreCursorPosition() {
        print(ansi().restorCursorPosition());

        for(; rowCounter > 0; rowCounter--) {
            cleanCurrentLine();
            print(ansi().cursorUp(1));
        }
    }

    private String createProgressBar(int percent) {
        StringBuilder bar = new StringBuilder("[");

        for (int i = 0; i < 50; i++) {
            if (i < (percent / 2)) {
                bar.append("=");
            } else if (i == (percent / 2)) {
                bar.append(">");
            } else {
                bar.append(" ");
            }
        }

        bar.append("]   ").append(percent).append("%     ");
        return bar.toString();
    }

    private void print(Object o) {
        System.out.print(o);
        System.out.flush();
    }

    private void print(Object o, boolean suppressCodenvyPrompt) {
        if (!interactive && !suppressCodenvyPrompt) {
            printCodenvyPrompt();
        }

        print(o);
    }

    private void printCodenvyPrompt() {
        final String lightBlue = '\u001b' + "[94m";
        print(ansi().a(lightBlue + CODENVY_PREFIX).reset()); // light blue
    }

    private boolean isConnectionException(Exception e) {
        Throwable cause = e.getCause();
        return cause != null && cause.getClass().getCanonicalName().equals(ConnectException.class.getCanonicalName());
    }

    private String doReadLine(@Nullable Character mask) throws IOException {
        rowCounter++;
        return consoleReader.readLine(mask);
    }

    protected class LoadingBar {
        private Visualizer visualizer;

        public void show() {
            if (visualizer != null && visualizer.isAlive()) {
                return;
            }

            visualizer = new Visualizer();
            visualizer.start();
        }

        public void hide() {
            if (visualizer != null && visualizer.isAlive()) {
                visualizer.interrupt();

                visualizer = null;
                printProgress(" ");  // remove last loader symbol
                saveCursorPosition();
            }
        }

        public void restore() {
            restoreCursorPosition();
            show();
        }

        /** Printing progress thread */
        protected class Visualizer extends Thread {
            protected final int    CHAR_CHANGE_TIMEOUT_MILLIS = 250;
            protected final char[] LOADER_CHARS               = {'-', '\\', '|', '/'};

            @Override
            public void run() throws IllegalStateException {
                int step = 0;
                while (!isInterrupted()) {
                    printProgress(String.valueOf(LOADER_CHARS[step]));
                    try {
                        sleep(CHAR_CHANGE_TIMEOUT_MILLIS);
                    } catch (InterruptedException e) {
                        break;
                    }

                    step++;
                    if (step == LOADER_CHARS.length) {
                        step = 0;
                    }
                }
            }
        }
    }
}
