package com.school.school.response;

public class NotFoundWrapper<T> extends ResponseWrapper<T> {
    public NotFoundWrapper(String element, Integer id) {
        super("No " + element + " with ID " + id + " found.", false);
    }
}
