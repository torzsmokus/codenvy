/*
 *  [2012] - [2016] Codenvy, S.A.
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
package com.codenvy.activity.client;

/**
 * Tracks user mouse and keyboard activity
 *
 *
 * @author Max Shaposhnik (mshaposhnik@codenvy.com) on 5/4/16.
 */

import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.RootLayoutPanel;

import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.rest.AsyncRequestCallback;
import org.eclipse.che.ide.rest.AsyncRequestFactory;
import org.eclipse.che.ide.rest.RestContext;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserActivityListener {

    private final int DELAY_MS = 1000;

    private final AsyncRequestFactory asyncRequestFactory;
    private final String              apiUrl;
    private final EventHandler        eventHandler;


    @Inject
    public UserActivityListener(AsyncRequestFactory asyncRequestFactory,
                                @RestContext String restContext,
                                AppContext appContext) {
        this.asyncRequestFactory = asyncRequestFactory;
        this.apiUrl = restContext + "/activity/" + appContext.getWorkspaceId();
        this.eventHandler = new EventHandler();
    }

    public void start() {
        eventHandler.register();
    }


    private void sendActivitySignal() {
        asyncRequestFactory.createRequest(RequestBuilder.PUT, apiUrl, null, false)
                           .send(new AsyncRequestCallback<Void>() {
                               @Override
                               protected void onSuccess(Void result) {
                                   new Timer() {
                                       @Override
                                       public void run() {
                                           eventHandler.register();
                                       }
                                   }.schedule(DELAY_MS);
                               }

                               @Override
                               protected void onFailure(Throwable exception) {
                                   //todo: count failures and stop scheduling
                               }
                           });
    }

    private class EventHandler implements MouseMoveHandler, KeyPressHandler {
        private HandlerRegistration mouseRegistration;
        private HandlerRegistration kbdRegistration;

        public void register() {
            mouseRegistration = RootLayoutPanel.get().addDomHandler(this, MouseMoveEvent.getType());
            kbdRegistration = RootLayoutPanel.get().addDomHandler(this, KeyPressEvent.getType());
        }

        @Override
        public void onMouseMove(MouseMoveEvent event) {
            mouseRegistration.removeHandler();
            kbdRegistration.removeHandler();
            sendActivitySignal();
        }

        @Override
        public void onKeyPress(KeyPressEvent event) {
            mouseRegistration.removeHandler();
            kbdRegistration.removeHandler();
            sendActivitySignal();
        }
    }
}

