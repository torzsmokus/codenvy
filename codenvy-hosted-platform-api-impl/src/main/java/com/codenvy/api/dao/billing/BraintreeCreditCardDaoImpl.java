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
package com.codenvy.api.dao.billing;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.ClientTokenRequest;
import com.braintreegateway.Customer;
import com.braintreegateway.CustomerRequest;
import com.braintreegateway.Result;
import com.braintreegateway.exceptions.BraintreeException;
import com.braintreegateway.exceptions.NotFoundException;
import com.codenvy.api.account.AccountLocker;
import com.codenvy.api.account.billing.BillingPeriod;
import com.codenvy.api.account.billing.BillingService;
import com.codenvy.api.account.billing.CreditCardDao;
import com.codenvy.api.account.billing.CreditCardRegistrationEvent;
import com.codenvy.api.account.billing.MonthlyBillingPeriod;
import com.codenvy.api.account.billing.ResourcesFilter;
import com.codenvy.api.account.impl.shared.dto.AccountResources;
import com.codenvy.api.account.impl.shared.dto.CreditCard;
import com.codenvy.api.core.ForbiddenException;
import com.codenvy.api.core.ServerException;
import com.codenvy.api.core.notification.EventService;
import com.codenvy.commons.env.EnvironmentContext;
import com.codenvy.dto.server.DtoFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of credit card DAO based on Braintree service.
 *
 * @author Max Shaposhnik (mshaposhnik@codenvy.com) on 1/26/15.
 */
public class BraintreeCreditCardDaoImpl implements CreditCardDao {

    private static final Logger LOG = LoggerFactory.getLogger(BraintreeCreditCardDaoImpl.class);

    private final BraintreeGateway gateway;

    private final EventService eventService;

    private final BillingService billingService;

    private final BillingPeriod billingPeriod;

    private final AccountLocker accountLocker;


    @Inject
    public BraintreeCreditCardDaoImpl(BraintreeGateway gateway, EventService eventService, BillingService billingService,
                                      BillingPeriod billingPeriod, AccountLocker accountLocker) {
        this.gateway = gateway;
        this.eventService = eventService;
        this.billingService = billingService;
        this.billingPeriod = billingPeriod;
        this.accountLocker = accountLocker;
    }

    @Override
    public String getClientToken(String accountId) throws ServerException, ForbiddenException {
        if (accountId == null) {
            throw new ForbiddenException("Account ID required.");
        }
        try {
            String token =  gateway.clientToken().generate(new ClientTokenRequest().customerId(accountId));
            if (token == null) { // e.g. account not yet registered in btree
                return  gateway.clientToken().generate();
            }
            return token;
        } catch (BraintreeException e) {
            LOG.warn("Braintree exception: ", e);
            throw new ServerException("Internal server error. Please, contact support.");
        }
    }

    @Override
    public void registerCard(String accountId, String nonce, String streetAddress, String city, String state, String country) throws ServerException, ForbiddenException {
        if (accountId == null) {
            throw new ForbiddenException("Account ID required.");
        }
        if (nonce == null) {
            throw new ForbiddenException("Credit card nonce is required.");
        }
        Result<Customer> result;
        try {
            Customer customer = gateway.customer().find(accountId);
            if (customer.getCreditCards().size() >= 1) {
                String msg = String.format(" Failed to add a new card to account %s, because there is already a card linked with it. ", accountId);
                LOG.error(msg);
                throw new ForbiddenException(msg);
            }
            CustomerRequest request = new CustomerRequest().creditCard()
                                                           .paymentMethodNonce(nonce)
                                                           .billingAddress()
                                                               .streetAddress(streetAddress)
                                                               .locality(city)
                                                               .region(state)
                                                               .countryName(country)
                                                               .done()
                                                           .done();
            result = gateway.customer().update(customer.getId(), request);
            if (!result.isSuccess()) {
                String msg = String.format("Failed to register new card for account %s. Error message: %s ", accountId, result.getMessage());
                LOG.error(msg);
                throw new ForbiddenException(msg);
            }
            eventService.publish(CreditCardRegistrationEvent
                                         .creditCardAddedEvent(accountId, result.getTarget().getCreditCards().get(0).getMaskedNumber(),
                                                               EnvironmentContext.getCurrent().getUser().getId()));
        } catch (NotFoundException nf) {
            CustomerRequest request = new CustomerRequest().id(accountId).creditCard()
                                                           .paymentMethodNonce(nonce)
                                                           .billingAddress()
                                                               .streetAddress(streetAddress)
                                                               .locality(city)
                                                               .region(state)
                                                               .countryName(country)
                                                               .done()
                                                           .done();
            result = gateway.customer().create(request);
            if (!result.isSuccess()) {
                String msg = String.format("Failed to register new card for account %s. Error message: %s ", accountId, result.getMessage());
                LOG.error(msg);
                throw new ForbiddenException(msg);
            }
            eventService.publish(CreditCardRegistrationEvent
                                         .creditCardAddedEvent(accountId, result.getTarget().getCreditCards().get(0).getMaskedNumber(),
                                                               EnvironmentContext.getCurrent().getUser().getId()));
        } catch (BraintreeException e) {
            LOG.warn("Braintree exception: ", e);
            throw new ServerException("Internal server error. Please, contact support.");
        }
    }

    @Override
    public List<CreditCard> getCards(String accountId) throws ServerException, ForbiddenException {
        if (accountId == null) {
            throw new ForbiddenException("Account ID required.");
        }
        List<CreditCard> result = new ArrayList<>();
        try {
            Customer customer =  gateway.customer().find(accountId);
            for (com.braintreegateway.CreditCard card : customer.getCreditCards()){
                result.add(DtoFactory.getInstance().createDto(CreditCard.class).withAccountId(accountId)
                                     .withToken(card.getToken())
                                     .withType(card.getCardType())
                                     .withNumber(card.getMaskedNumber())
                                     .withCardholder(card.getCardholderName())
                                     .withStreetAddress(card.getBillingAddress().getStreetAddress())
                                     .withCity(card.getBillingAddress().getLocality())
                                     .withState(card.getBillingAddress().getRegion())
                                     .withCountry(card.getBillingAddress().getCountryName())
                                     .withExpiration(card.getExpirationDate()));
            }
        } catch (NotFoundException nf) {
            // nothing found - empty list
            return result;
        } catch (BraintreeException e) {
            LOG.warn("Braintree exception: ", e);
            throw new ServerException("Internal server error. Please, contact support.");
        }
        return result;
    }

    @Override
    public void deleteCard(String accountId, String token) throws ServerException, ForbiddenException {
        if (accountId == null) {
            throw new ForbiddenException("Account ID required.");
        }
        if (token == null) {
            throw new ForbiddenException("Token is required.");
        }
        try {
            com.braintreegateway.CreditCard  card = gateway.creditCard().find(token);
            Result<com.braintreegateway.CreditCard> result = gateway.creditCard().delete(token);
            if (!result.isSuccess()) {
                LOG.warn(String.format("Failed to remove card. Error message: %s", result.getMessage()));
                throw new ForbiddenException(String.format("Failed to remove card. Error message: %s",result.getMessage()));
            }
            eventService.publish(CreditCardRegistrationEvent
                                         .creditCardRemovedEvent(accountId, card.getMaskedNumber(),
                                                               EnvironmentContext.getCurrent().getUser().getId()));
            checkAndLockAccount(accountId);
        } catch (BraintreeException e) {
            LOG.warn("Braintree exception: ", e);
            throw new ServerException("Internal server error. Please, contact support.");
        }
    }


    private void checkAndLockAccount(String accountId) throws ServerException {
        ResourcesFilter filter = ResourcesFilter.builder().withAccountId(accountId)
                                                .withPaidGbHMoreThan(0)
                                                .withFromDate(billingPeriod.getCurrent().getStartDate().getTime())
                                                .withTillDate(System.currentTimeMillis())
                                                .build();
        List<AccountResources> resources = billingService.getEstimatedUsageByAccount(filter);
        if (!resources.isEmpty()) {
            accountLocker.lockAccount(accountId);
        }
    }
}
