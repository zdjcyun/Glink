package com.zcloud.alone.network.manage;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.*;

/**
 * 线程池管理
 * 适用于CPU密集型任务调度
 * @author dzm
 */
public class ThreadManage {

	private ScheduledExecutorService service;

	private ThreadManage() {
		service = new ScheduledThreadPoolExecutor(3,
				new BasicThreadFactory.Builder().namingPattern("async-task-pool-%d").daemon(true).build(),
				new ThreadPoolExecutor.AbortPolicy());
	}

	private static volatile ThreadManage manager;

	public static ThreadManage getInstance() {
		if (null == manager) {
			synchronized (ThreadManage.class) {
				if (null == manager) {
					manager = new ThreadManage();
				}
			}
		}
		return manager;
	}

	public ExecutorService getExecutorService() {
		return service;
	}

	/**
	 * 立即执行任务
	 * @param runnable
	 */
	public void addTask(Runnable runnable) {
		service.submit(runnable);
	}

	/**
	 * 延迟一段时间执行任务
	 * @param runnable
	 * @param delay 延迟的时间
	 * @param unit  时间的单位
	 */
	public void addScheduleTask(Runnable runnable, long delay, TimeUnit unit) {
		service.schedule(runnable, delay, unit);
	}

	/**
	 * 延迟一段时间后固定时间段执行任务（任务是周期性的）
	 * 以上一个任务开始的时间计时，period时间过去后，检测上一个任务是否执行完毕，如果上一个任务执行完毕，则当前任务立即执行，
	 * 如果上一个任务没有执行完毕，则需要等上一个任务执行完毕后立即执行
	 * @param runnable
	 * @param initialDelay 延迟的时间
	 * @param period 周期的时间
	 * @param unit  时间的单位
	 */
	public void addScheduleFixedTask(Runnable runnable, long initialDelay, long period, TimeUnit unit) {
		service.scheduleAtFixedRate(runnable, initialDelay, period, unit);
	}
}