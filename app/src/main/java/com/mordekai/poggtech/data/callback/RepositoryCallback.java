package com.mordekai.poggtech.data.callback;

public interface RepositoryCallback<T> {
    void onSuccess(T result);
    void onFailure(Throwable t);
}
