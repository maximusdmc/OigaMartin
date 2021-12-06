package somoslobos.mx.oigamartin;

import android.content.Context;

import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadServiceBroadcastReceiver;

public class MyReceiver extends UploadServiceBroadcastReceiver {
    @Override
    public void onProgress(Context context, UploadInfo uploadInfo) {
        // your implementation
    }

    @Override
    public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse, Exception exception) {
        // your implementation
    }

    @Override
    public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
        // your implementation
    }

    @Override
    public void onCancelled(Context context, UploadInfo uploadInfo) {
        // your implementation
    }
}