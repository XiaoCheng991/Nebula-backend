package com.nebula.common.exception;

/**
 * Redis 操作异常
 */
public class RedisException extends RuntimeException {

    private final String operation;
    private final String key;

    public RedisException(String operation, String key, String message) {
        super(message);
        this.operation = operation;
        this.key = key;
    }

    public RedisException(String operation, String key, String message, Throwable cause) {
        super(message, cause);
        this.operation = operation;
        this.key = key;
    }

    public String getOperation() {
        return operation;
    }

    public String getKey() {
        return key;
    }
}