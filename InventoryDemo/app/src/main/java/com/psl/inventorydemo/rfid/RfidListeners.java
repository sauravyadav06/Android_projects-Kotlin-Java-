package com.psl.inventorydemo.rfid;

public interface RfidListeners {

    void onSuccess(Object object);

    void onFailure(Exception exception);

    void onFailure(String message);

}
