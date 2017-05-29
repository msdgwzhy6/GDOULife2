package com.fedming.gdoulife.utils;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Bruce on 2016/10/20.
 */

public abstract class OkHttpLoginCallback extends com.zhy.http.okhttp.callback.Callback<Response> {


    @Override
    public Response parseNetworkResponse(Response response, int id) throws Exception {
        return response;
    }

    @Override
    public void onError(Call call, Exception e, int id) {

    }

    @Override
    public void onResponse(Response response, int id) {

    }
}
