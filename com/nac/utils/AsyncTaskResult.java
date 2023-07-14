package com.nac.utils;

public class AsyncTaskResult<T> {

    private T result;
    private Throwable exception;

    public AsyncTaskResult(T result) {
        this.result = result;
    }

    public AsyncTaskResult(Throwable exception) {
        this.exception = exception;
    }

    public AsyncTaskResult(T result, Throwable exception) {
        this.result = result;
        this.exception = exception;
    }

    /**
     * @return the result
     */
    public T getResult() {
        return this.result;
    }

    /**
     * @param result the result to set
     */
    public void setResult(T result) {
        this.result = result;
    }

    /**
     * @return the exception
     */
    public Throwable getException() {
        return this.exception;
    }

    /**
     * @param exception the exception to set
     */
    public void setException(Throwable exception) {
        this.exception = exception;
    }
}