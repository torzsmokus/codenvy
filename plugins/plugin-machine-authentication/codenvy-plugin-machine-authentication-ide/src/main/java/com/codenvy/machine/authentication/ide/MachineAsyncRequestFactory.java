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
package com.codenvy.machine.authentication.ide;

import com.codenvy.machine.authentication.shared.dto.MachineTokenDto;
import com.google.common.base.Strings;
import com.google.gwt.http.client.RequestBuilder;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import org.eclipse.che.api.promises.client.Function;
import org.eclipse.che.api.promises.client.FunctionException;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.js.Promises;
import org.eclipse.che.api.workspace.shared.dto.event.WorkspaceStatusEvent;
import org.eclipse.che.ide.MimeType;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.rest.AsyncRequest;
import org.eclipse.che.ide.rest.AsyncRequestFactory;
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;
import org.eclipse.che.ide.rest.HTTPHeader;
import org.eclipse.che.ide.util.loging.Log;
import org.eclipse.che.ide.websocket.MessageBus;
import org.eclipse.che.ide.websocket.MessageBusProvider;
import org.eclipse.che.ide.websocket.WebSocketException;
import org.eclipse.che.ide.websocket.rest.SubscriptionHandler;
import org.eclipse.che.ide.websocket.rest.Unmarshallable;

import java.util.List;

import static org.eclipse.che.ide.api.notification.StatusNotification.DisplayMode.NOT_EMERGE_MODE;
import static org.eclipse.che.ide.api.notification.StatusNotification.Status.FAIL;

/**
 * Looks at the request and substitutes an appropriate implementation.
 *
 * @author Anton Korneta
 */
@Singleton
public class MachineAsyncRequestFactory extends AsyncRequestFactory {
    private static final String DTO_CONTENT_TYPE = MimeType.APPLICATION_JSON;

    private final Provider<MachineTokenServiceClient> machineTokenServiceProvider;
    private final DtoFactory                          dtoFactory;
    private final NotificationManager                 notificationManager;

    private String machineToken;

    @Inject
    public MachineAsyncRequestFactory(DtoFactory dtoFactory,
                                      Provider<MachineTokenServiceClient> machineTokenServiceProvider,
                                      AppContext appContext,
                                      DtoUnmarshallerFactory dtoUnmarshallerFactory,
                                      NotificationManager notificationManager,
                                      MessageBusProvider messageBusProvider) {
        super(dtoFactory);
        this.machineTokenServiceProvider = machineTokenServiceProvider;
        this.dtoFactory = dtoFactory;
        this.notificationManager = notificationManager;
        final Unmarshallable<WorkspaceStatusEvent> unmarshaller = dtoUnmarshallerFactory.newWSUnmarshaller(WorkspaceStatusEvent.class);
        try {
            MessageBus messageBus = messageBusProvider.getMessageBus();
            if (messageBus == null) {
                messageBus = messageBusProvider.createMessageBus(appContext.getWorkspaceId());
            }
            messageBus.subscribe("workspace:" + appContext.getWorkspaceId(), new SubscriptionHandler<WorkspaceStatusEvent>(unmarshaller) {

                @Override
                protected void onMessageReceived(WorkspaceStatusEvent statusEvent) {
                    switch (statusEvent.getEventType()) {
                        case RUNNING: {
                            machineToken = null;
                        }
                    }
                }

                @Override
                protected void onErrorReceived(Throwable exception) {
                    MachineAsyncRequestFactory.this.notificationManager.notify(exception.getMessage(), FAIL, NOT_EMERGE_MODE);
                }
            });
        } catch (WebSocketException ex) {
            Log.error(getClass(), ex);
        }
    }

    @Override
    protected AsyncRequest doCreateRequest(RequestBuilder.Method method,
                                           String url,
                                           Object dtoBody,
                                           boolean async) {
        if (!url.contains("/ext/")) {
            return super.doCreateRequest(method, url, dtoBody, async);
        }
        return doCreateMachineRequest(method, url, dtoBody, async);
    }

    private AsyncRequest doCreateMachineRequest(RequestBuilder.Method method, String url, Object dtoBody, boolean async) {
        final AsyncRequest asyncRequest = new MachineAsyncRequest(method, url, async, getMachineToken());
        if (dtoBody != null) {
            if (dtoBody instanceof List) {
                asyncRequest.data(dtoFactory.toJson((List)dtoBody));
            } else {
                asyncRequest.data(dtoFactory.toJson(dtoBody));
            }
            asyncRequest.header(HTTPHeader.CONTENT_TYPE, DTO_CONTENT_TYPE);
        }
        return asyncRequest;
    }

    private Promise<String> getMachineToken() {
        if (!Strings.isNullOrEmpty(machineToken)) {
            return Promises.resolve(machineToken);
        } else {
            return machineTokenServiceProvider.get().getMachineToken()
                                              .then(new Function<MachineTokenDto, String>() {
                                                  @Override
                                                  public String apply(MachineTokenDto tokenDto) throws FunctionException {
                                                      machineToken = "Bearer " + tokenDto.getMachineToken();
                                                      return machineToken;
                                                  }
                                              });
        }
    }
}
