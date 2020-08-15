package com.populstay.populife.util.timer;

import android.os.CountDownTimer;

/**
 * 倒计时工具类
 * Created by Jerry
 */
public class BaseCountDownTimer extends CountDownTimer {

	private ITimerListener mTimerListener;

	/**
	 * @param totalSeconds 计时总时长（秒）
	 */
	public BaseCountDownTimer(int totalSeconds, ITimerListener timerListener) {
		super(totalSeconds * 1000, 1000);
		this.mTimerListener = timerListener;
	}

	@Override
	public void onTick(long millisUntilFinished) { // 计时过程显示
		mTimerListener.onTimerTick(millisUntilFinished / 1000);
	}

	@Override
	public void onFinish() { // 计时完毕时触发
		mTimerListener.onTimerFinish();
	}
}
