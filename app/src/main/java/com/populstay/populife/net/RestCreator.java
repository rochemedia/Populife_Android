package com.populstay.populife.net;

import com.populstay.populife.common.Urls;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by Jerry
 */
public class RestCreator {

	/**
	 * 构建全局 Retrofit 客户端
	 */
	private static final class RetrofitHolder {
		private static final String BASE_URL = Urls.BASE_URL;
		private static final Retrofit RETROFIT_CLIENT = new Retrofit.Builder()
				.baseUrl(BASE_URL)
				.client(OKHttpHolder.OK_HTTP_CLIENT)
				.addConverterFactory(ScalarsConverterFactory.create())
				.build();
	}

	/**
	 * 构建OkHttp
	 */
	private static final class OKHttpHolder {
		private static final int TIME_OUT = 60;
		private static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient.Builder()
				.connectTimeout(TIME_OUT, TimeUnit.SECONDS)
				.build();
	}

	/**
	 * Service接口
	 */
	private static final class RestServiceHolder {
		private static final RestService REST_SERVICE =
				RetrofitHolder.RETROFIT_CLIENT.create(RestService.class);
	}

	public static RestService getRestService() {
		return RestServiceHolder.REST_SERVICE;
	}
}
