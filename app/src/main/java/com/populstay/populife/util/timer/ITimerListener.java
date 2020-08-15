package com.populstay.populife.util.timer;

/**
 * Created by Jerry
 */
public interface ITimerListener {

	void onTimerTick(long secondsLeft);

	void onTimerFinish();
}
