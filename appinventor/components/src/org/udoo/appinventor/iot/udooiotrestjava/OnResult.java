package org.udoo.appinventor.iot.udooiotrestjava;

/**
 * Created by harlem88 on 9/15/17.
 */

public interface OnResult<T> {
    void onSuccess(T model);
    void onError(Throwable error);
}
