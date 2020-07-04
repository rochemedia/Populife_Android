package com.populstay.populife.net.callback;


import android.os.Handler;
import android.support.annotation.NonNull;

import com.populstay.populife.ui.loader.LoaderStyle;
import com.populstay.populife.ui.loader.PeachLoader;

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
		if (response.isSuccessful()) {
			if (call.isExecuted()) {
				if (SUCCESS != null) {
					SUCCESS.onSuccess(response.body());
				}
			}
		} else {
			if (ERROR != null) {
				ERROR.onError(response.code(), response.message());
			}
		}

		onRequestFinish();
	}

	@Override
	public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
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
