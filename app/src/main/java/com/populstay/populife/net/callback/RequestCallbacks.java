package com.populstay.populife.net.callback;


import android.os.Handler;
import android.support.annotation.NonNull;

import com.populstay.populife.ui.loader.LoaderStyle;
import com.populstay.populife.ui.loader.PeachLoader;
import com.populstay.populife.util.log.PeachLogger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jerry
 */
public final class RequestCallbacks implements Callback<String> {

	private static final Handler HANDLER = new Handler();
	private final IRequest REQUEST;
	private final ISuccess SUCCESS;
	private final IFailure FAILURE;
	private final IError ERROR;
	private final LoaderStyle LOADER_STYLE;

	public RequestCallbacks(IRequest request, ISuccess success, IFailure failure, IError error, LoaderStyle style) {
		this.REQUEST = request;
		this.SUCCESS = success;
		this.FAILURE = failure;
		this.ERROR = error;
		this.LOADER_STYLE = style;
	}

	@Override
	public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
		String url = response.raw().request().url().url().toString();
		int code = response.code();
		String message = response.message();
		String body = response.body();
		PeachLogger.d("Http", "RequestCallbacks--onResponse--url=" + url + ", code=" + code + ", message=" + message + ", body=" + body);
		if (response.isSuccessful()) {
			if (call.isExecuted()) {
				if (SUCCESS != null) {
					SUCCESS.onSuccess(body);
				}
			}
		} else {
			if (ERROR != null) {
				ERROR.onError(code, message);
			}
		}

		onRequestFinish();
	}

	@Override
	public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
		PeachLogger.d("Http", "RequestCallbacks--onFailure=" + t.getMessage());
		if (FAILURE != null) {

			FAILURE.onFailure();
		}
		if (REQUEST != null) {
			REQUEST.onRequestEnd();
		}

		onRequestFinish();
	}

	private void onRequestFinish() {
		if (LOADER_STYLE != null) {
//			HANDLER.postDelayed(new Runnable() {
//				@Override
//				public void run() {
					PeachLoader.stopLoading();
//				}
//			}, 1000);
		}
	}
}
