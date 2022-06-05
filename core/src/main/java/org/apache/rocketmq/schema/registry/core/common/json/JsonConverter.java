/**
 * Copyright 2022, Xiaomi.
 * All rights reserved.
 * Author: wangfan8@xiaomi.com
 */

package org.apache.rocketmq.schema.registry.core.common.json;

import org.apache.rocketmq.schema.registry.core.common.exception.SchemaException;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Json <-> Object
 */
public interface JsonConverter {

    /**
     * Parses the given string as json and returns a json ObjectNode representing the json.
     *
     * @param s a string representing a json object
     * @return an object node representation of the string
     * @throws SchemaException if unable to convert the string to json or the json isn't a json object.
     */
    ObjectNode fromJson(String s) throws SchemaException;

    /**
     * Parses the given JSON value.
     *
     * @param s     json string
     * @param clazz class
     * @param <T>   type of the class
     * @return object
     */
    <T> T fromJson(String s, Class<T> clazz);

    /**
     * Parses the given JSON value.
     *
     * @param s     json byte array
     * @param clazz class
     * @param <T>   type of the class
     * @return object
     */
    <T> T fromJson(byte[] s, Class<T> clazz);

    /**
     * Converts JSON as bytes.
     *
     * @param o object
     * @return byte array
     */
    byte[] toJsonAsBytes(Object o);

    /**
     * Converts an object to JSON.
     *
     * @param o object
     * @return JSON node
     */
    ObjectNode toJsonAsObjectNode(Object o);

    /**
     * Converts an object to JSON string.
     *
     * @param o object
     * @return JSON string
     */
    String toJson(Object o);

    /**
     * Converts an object to JSON string.
     *
     * @param o object
     * @return JSON string
     */
    String toString(Object o);
}
