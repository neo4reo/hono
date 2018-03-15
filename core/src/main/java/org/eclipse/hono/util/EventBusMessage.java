/**
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * https://www.eclipse.org/legal/epl-v10.html
 *
 * SPDX-License-Identifier: EPL-1.0
 */

package org.eclipse.hono.util;

import java.util.Objects;
import java.util.Optional;

import org.apache.qpid.proton.message.Message;

import io.vertx.core.json.JsonObject;

/**
 * A wrapper around a JSON object which can be used to convey request and/or response
 * information for Hono API operations via the vert.x event bus.
 *
 */
public class EventBusMessage {

    private final JsonObject json;

    private EventBusMessage(final String subject) {
        Objects.requireNonNull(subject);
        this.json = new JsonObject();
        json.put(MessageHelper.SYS_PROPERTY_SUBJECT, subject);
    }

    private EventBusMessage(final JsonObject request) {
        this.json = Objects.requireNonNull(request);
    }

    /**
     * Creates a new (request) message for an operation.
     * 
     * @param subject The name of the operation.
     * @return The request message.
     */
    public static EventBusMessage forOperation(final String subject) {
        return new EventBusMessage(subject);
    }

    /**
     * Creates a new (request) message from an AMQP 1.0 message.
     * <p>
     * The operation will be determined from the message's
     * <em>subject</em>.
     * 
     * @param message The AMQP message.
     * @return The request message.
     * @throws IllegalArgumentException if the message has no subject set.
     */
    public static EventBusMessage forOperation(final Message message) {
        if (message.getSubject() == null) {
            throw new IllegalArgumentException("message has no subject");
        } else {
            return new EventBusMessage(message.getSubject());
        }
    }

    /**
     * Creates a new (response) message for a status code.
     * 
     * @param status The status code indicating the outcome of the operation.
     * @return The response message.
     */
    public static EventBusMessage forStatusCode(final int status) {
        final EventBusMessage result = new EventBusMessage(new JsonObject());
        result.setProperty(MessageHelper.APP_PROPERTY_STATUS, status);
        return result;
    }

    /**
     * Creates a new message from a JSON object.
     * <p>
     * Whether the created message represents a request or a response
     * is determined by the <em>status</em> and <em>operation</em> properties.
     * 
     * @param json The JSON object.
     * @return The message.
     */
    public static EventBusMessage fromJson(final JsonObject json) {
        return new EventBusMessage(Objects.requireNonNull(json));
    }

    /**
     * Gets the operation to invoke.
     * 
     * @return The operation of {@code null} if this is a response message.
     */
    public String getOperation() {
        return getProperty(MessageHelper.SYS_PROPERTY_SUBJECT);
    }

    /**
     * Gets the status code indicating the outcome of the invocation
     * of the operation.
     * 
     * @return The status code or {@code null} if this is a request message.
     */
    public Integer getStatus() {
        return getProperty(MessageHelper.APP_PROPERTY_STATUS);
    }

    /**
     * Adds a property for the tenant identifier.
     * <p>
     * The property will only be added if the value is not {@code null}.
     * 
     * @param tenantId The tenant identifier.
     * @return This message for chaining.
     */
    public EventBusMessage setTenant(final String tenantId) {
        setProperty(MessageHelper.APP_PROPERTY_TENANT_ID, tenantId);
        return this;
    }

    /**
     * Adds a property for the tenant identifier.
     * <p>
     * The property will only be added if the AMQP message contains
     * a non-{@code null} tenant identifier.
     * 
     * @param msg The AMQP message to retrieve the value from.
     * @return This message for chaining.
     */
    public EventBusMessage setTenant(final Message msg) {
        setTenant(MessageHelper.getTenantId(msg));
        return this;
    }

    /**
     * Gets the value of the tenant identifier property.
     * 
     * @return The value or {@code null} if not set.
     */
    public String getTenant() {
        return getProperty(MessageHelper.APP_PROPERTY_TENANT_ID);
    }

    /**
     * Adds a property for the device identifier.
     * <p>
     * The property will only be added if the value is not {@code null}.
     * 
     * @param deviceId The device identifier.
     * @return This message for chaining.
     */
    public EventBusMessage setDeviceId(final String deviceId) {
        setProperty(MessageHelper.APP_PROPERTY_DEVICE_ID, deviceId);
        return this;
    }

    /**
     * Adds a property for the device identifier.
     * <p>
     * The property will only be added if the AMQP message contains
     * a non-{@code null} device identifier.
     * 
     * @param msg The AMQP message to retrieve the value from.
     * @return This message for chaining.
     */
    public EventBusMessage setDeviceId(final Message msg) {
        setDeviceId(MessageHelper.getDeviceId(msg));
        return this;
    }

    /**
     * Gets the value of the device identifier property.
     * 
     * @return The value or {@code null} if not set.
     */
    public String getDeviceId() {
        return getProperty(MessageHelper.APP_PROPERTY_DEVICE_ID);
    }

    /**
     * Adds a property for the request/response payload.
     * <p>
     * The property will only be added if the value is not {@code null}.
     * 
     * @param payload The payload.
     * @return This message for chaining.
     */
    public EventBusMessage setJsonPayload(final JsonObject payload) {
        setProperty(RequestResponseApiConstants.FIELD_PAYLOAD, payload);
        return this;
    }

    /**
     * Adds a property for the request/response payload.
     * <p>
     * The property will only be added if the AMQP message contains
     * a JSON payload.
     * 
     * @param msg The AMQP message to retrieve the payload from.
     * @return This message for chaining.
     */
    public EventBusMessage setJsonPayload(final Message msg) {
        setJsonPayload(MessageHelper.getJsonPayload(msg));
        return this;
    }

    /**
     * Gets the value of the payload property.
     * 
     * @return The value or {@code null} if not set.
     */
    public JsonObject getJsonPayload() {
        return getProperty(RequestResponseApiConstants.FIELD_PAYLOAD);
    }

    /**
     * Adds a property for the gateway identifier.
     * <p>
     * The property will only be added if the value is not {@code null}.
     * 
     * @param id The gateway identifier.
     * @return This message for chaining.
     */
    public EventBusMessage setGatewayId(final String id) {
        setProperty(MessageHelper.APP_PROPERTY_GATEWAY_ID, id);
        return this;
    }

    /**
     * Adds a property for the gateway identifier.
     * <p>
     * The property will only be added if the AMQP message contains
     * a non-{@code null} gateway identifier.
     * 
     * @param msg The AMQP message to retrieve the value from.
     * @return This message for chaining.
     */
    public EventBusMessage setGatewayId(final Message msg) {
        setStringProperty(MessageHelper.APP_PROPERTY_GATEWAY_ID, msg);
        return this;
    }

    /**
     * Gets the value of the gateway identifier property.
     * 
     * @return The value or {@code null} if not set.
     */
    public String getGatewayId() {
        return getProperty(MessageHelper.APP_PROPERTY_GATEWAY_ID);
    }

    /**
     * Adds a property for the cache directive.
     * <p>
     * The property will only be added if the value is not {@code null}.
     * 
     * @param directive The cache directive.
     * @return This message for chaining.
     */
    public EventBusMessage setCacheDirective(final CacheDirective directive) {
        if (directive != null) {
            setProperty(
                    MessageHelper.APP_PROPERTY_CACHE_CONTROL,
                    Objects.requireNonNull(directive).toString());
        }
        return this;
    }

    /**
     * Gets the value of the cache directive property.
     * 
     * @return The value or {@code null} if not set.
     */
    public String getCacheDirective() {
        return getProperty(MessageHelper.APP_PROPERTY_CACHE_CONTROL);
    }

    /**
     * Adds a property for the correlation identifier.
     * <p>
     * The value of the property is set
     * <ol>
     * <li>to the AMQP message's correlation identifier, if not {@code null}, or<li>
     * <li>to the AMQP message's message identifier, if not {@code null}.</li>
     * </ol>
     * 
     * @param message The AMQP message to retrieve the value from.
     * @return This message for chaining.
     * @throws IllegalArgumentException if the message doesn't contain a correlation id
     *            nor a message id.
     */
    public EventBusMessage setCorrelationId(final Message message) {
        if (message.getCorrelationId() != null) {
            return setCorrelationId(message.getCorrelationId());
        } else if (message.getMessageId() != null) {
            return setCorrelationId(message.getMessageId());
        } else {
            throw new IllegalArgumentException("message does not contain message-id nor correlation-id");
        }
    }

    /**
     * Adds a property for the correlation identifier.
     * <p>
     * The property will only be added if the value is not {@code null}.
     * 
     * @param id The correlation identifier.
     * @return This message for chaining.
     * @throws IllegalArgumentException if the identifier is neither a {@code String}
     *                 nor an {@code UnsignedLong} nor a {@code UUID} nor a {@code Binary}.
     */
    public EventBusMessage setCorrelationId(final Object id) {
        setProperty(MessageHelper.SYS_PROPERTY_CORRELATION_ID, RequestResponseApiConstants.encodeIdToJson(id));
        return this;
    }

    /**
     * Gets the value of the correlation identifier property.
     * 
     * @return The value or {@code null} if not set.
     */
    public Object getCorrelationId() {
        final JsonObject encodedId = getProperty(MessageHelper.SYS_PROPERTY_CORRELATION_ID);
        if (encodedId == null) {
            return null;
        } else {
            return RequestResponseApiConstants.decodeIdFromJson(encodedId);
        }
    }

    /**
     * Adds a property for the <em>x-opt-app-correlation-id</em> flag.
     * 
     * @param flag The flag.
     * @return This message for chaining.
     */
    public EventBusMessage setAppCorrelationId(final boolean flag) {
        setProperty(MessageHelper.ANNOTATION_X_OPT_APP_CORRELATION_ID, flag);
        return this;
    }

    /**
     * Adds a property for the <em>x-opt-app-correlation-id</em> flag.
     * <p>
     * The property will be set to the value of the corresponding annotation
     * from the AMQP message or to {@code false}, if the message doesn't
     * contain a corresponding annotation.
     * 
     * @param message The AMQP message to retrieve the value from.
     * @return This message for chaining.
     */
    public EventBusMessage setAppCorrelationId(final Message message) {
        setProperty(
                MessageHelper.ANNOTATION_X_OPT_APP_CORRELATION_ID,
                MessageHelper.getXOptAppCorrelationId(message));
        return this;
    }

    /**
     * Gets the value of the <em>x-opt-app-correlation-id</em> flag.
     * 
     * @return The value or {@code false} if not set.
     */
    public boolean isAppCorrelationId() {
        final Boolean result = getProperty(MessageHelper.ANNOTATION_X_OPT_APP_CORRELATION_ID);
        return Optional.ofNullable(result).orElse(Boolean.FALSE);
    }

    /**
     * Adds a property with a value.
     * <p>
     * The property will only be added if the value is not {@code null}.
     * 
     * @param name The name of the property.
     * @param value the value to set.
     * @return This message for chaining.
     * @throws NullPointerException if name is {@code null}.
     */
    public EventBusMessage setProperty(final String name, final Object value) {
        Objects.requireNonNull(name);
        if (value != null) {
            json.put(name, value);
        }
        return this;
    }

    /**
     * Adds a property with a value from an AMQP message.
     * <p>
     * The property will only be added if the AMQP message contains
     * a non-{@code null} <em>application property</em> of the given name.
     * 
     * @param name The name of the property.
     * @param msg The AMQP message to retrieve the value from.
     * @return This message for chaining.
     */
    public EventBusMessage setStringProperty(final String name, final Message msg) {
        setProperty(name, MessageHelper.getApplicationProperty(
                msg.getApplicationProperties(),
                name,
                String.class));
        return this;
    }

    /**
     * Gets a property value.
     * 
     * @param key The name of the property.
     * @param <T> The type of the field.
     * @return The property value or {@code null} if no such property exists or is not of the expected type.
     * @throws NullPointerException if key is {@code null}.
     */
    @SuppressWarnings({ "unchecked" })
    public <T> T getProperty(final String key) {

        Objects.requireNonNull(key);

        try {
            return (T) json.getValue(key);
        } catch (ClassCastException e) {
            return null;
        }
    }

    /**
     * Creates a JSON object representation of this message.
     * <p>
     * The {@link #fromJson(JsonObject)} method can be used to create
     * a {@code EventBusMethod} from its JSON representation.
     * 
     * @return The JSOn object.
     */
    public JsonObject toJson() {
        return json.copy();
    }
}