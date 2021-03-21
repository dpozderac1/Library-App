package ba.unsa.etf.rma.damir.spirala1;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * Created by cico on 5/18/2018.
 */

public class MojResultReceiver extends ResultReceiver {

    private Receiver mReceveier;

    public MojResultReceiver(Handler handle){
        super(handle);
    }
    public void setReceiver(Receiver receiver){
        mReceveier=receiver;
    }

    public interface Receiver{
        public void onReceiveResult(int resultCode, Bundle resultData);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if(mReceveier!=null){
            mReceveier.onReceiveResult(resultCode,resultData);
        }
    }
}
