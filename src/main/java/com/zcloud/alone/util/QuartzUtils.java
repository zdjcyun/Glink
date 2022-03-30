package com.zcloud.alone.util;

import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 定时任务工具类
 * @author dzm
 */
@Slf4j
public class QuartzUtils {

	private static Scheduler scheduler = SpringUtil.getBean(SchedulerFactoryBean.class).getScheduler();

	/**
	 * 根据任务名称创建触发器key
	 * @param taskName 任务名称
	 * @return
	 */
	public static TriggerKey getTriggerKey(String taskName) {
		return TriggerKey.triggerKey(taskName);
	}

	/**
	 * 根据任务名称和任务组创建触发器key
	 * @param taskName  任务名称
	 * @param taskGroup 任务组
	 * @return
	 */
	public static TriggerKey getTriggerKey(String taskName, String taskGroup) {
		return TriggerKey.triggerKey(taskName, taskGroup);
	}

	/**
	 * 根据任务名称创建jobKey
	 * @param taskName 任务名称
	 * @return
	 */
	public static JobKey getJobKey(String taskName) {
		return JobKey.jobKey(taskName);
	}

	/**
	 * 根据任务名称和任务组创建jobKey
	 * @param taskName  任务名称
	 * @param taskGroup 任务组
	 * @return
	 */
	public static JobKey getJobKey(String taskName, String taskGroup) {
		return JobKey.jobKey(taskName, taskGroup);
	}

	/**
	 * 根据任务名称获取触发器
	 * @param taskName 任务名称
	 * @return
	 */
	public static Trigger getTrigger(String taskName) {
		return getTrigger(getTriggerKey(taskName));
	}

	/**
	 * 根据triggerKey获取触发器
	 * @param triggerKey
	 * @return
	 */
	public static Trigger getTrigger(TriggerKey triggerKey) {
		try {
			return scheduler.getTrigger(triggerKey);
		} catch (SchedulerException e) {
			log.error("根据triggerKey获取触发器失败：" + e.getMessage());
			return null;
		}
	}

	/**
	 * 根据任务名称获取触发器状态（状态定义在 org.quartz.simpl.RAMJobStore 类中，共有 6 种，分别是 NONE、
	 * COMPLETE、 PAUSED、 BLOCKED、 ERROR、 NORMAL）
	 * @param taskName 任务名称
	 * @return
	 */
	public static String getTriggerState(String taskName) {
		return getTriggerState(getTriggerKey(taskName));
	}

	/**
	 * 根据triggerKey获取触发器状态（状态定义在 org.quartz.simpl.RAMJobStore 类中，共有 6 种，分别是 NONE、
	 * COMPLETE、 PAUSED、 BLOCKED、 ERROR、 NORMAL）
	 * @param triggerKey
	 * @return
	 */
	public static String getTriggerState(TriggerKey triggerKey) {
		try {
			return scheduler.getTriggerState(triggerKey).toString();
		} catch (SchedulerException e) {
			log.error("根据triggerKey获取触发器状态失败：" + e.getMessage());
			return null;
		}
	}

	/**
	 * 根据任务名称获取关联的触发器实例
	 * @param taskName 任务名称
	 * @return
	 */
	public static List<? extends Trigger> getJobTrigger(String taskName) {
		return getJobTrigger(getJobKey(taskName));
	}

	/**
	 * 根据jobKey获取关联的触发器实例
	 * @param jobKey
	 * @return
	 */
	public static List<? extends Trigger> getJobTrigger(JobKey jobKey) {
		try {
			return scheduler.getTriggersOfJob(jobKey);
		} catch (SchedulerException e) {
			log.error("根据jobKey获取关联的触发器实例失败：" + e.getMessage());
			return null;
		}
	}

	/**
	 * 获取所有的触发器实例
	 * @return
	 */
	public static List<? extends Trigger> getAllJobTrigger() {
		List<Trigger> triggerList = new ArrayList<Trigger>();
		try {
			for (String jobGroup : scheduler.getJobGroupNames()) {
				for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.<JobKey>groupEquals(jobGroup))) {
					triggerList.addAll(scheduler.getTriggersOfJob(jobKey));
				}
			}
			return triggerList;
		} catch (SchedulerException e) {
			log.error("获取所有的触发器实例失败：" + e.getMessage());
			return null;
		}
	}

	/**
	 * 根据任务名称获取指定的job实例
	 * @param taskName 任务名称
	 * @return
	 */
	public static JobDetail getJobDetail(String taskName) {
		return getJobDetail(getJobKey(taskName));
	}

	/**
	 * 根据jobKey获取指定的job实例
	 * @param jobKey
	 * @return
	 */
	public static JobDetail getJobDetail(JobKey jobKey) {
		try {
			return scheduler.getJobDetail(jobKey);
		} catch (SchedulerException e) {
			log.error("根据jobKey获取指定的job实例失败：" + e.getMessage());
			return null;
		}
	}

	/**
	 * 获取所有的job实例
	 * @return
	 */
	public static List<JobDetail> getAllJobDetail() {
		List<JobDetail> jobDetailList = new ArrayList<JobDetail>();
		try {
			for (String jobGroup : scheduler.getJobGroupNames()) {
				for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.<JobKey>groupEquals(jobGroup))) {
					JobDetail jobDetail = scheduler.getJobDetail(jobKey);
					jobDetailList.add(jobDetail);
				}
			}
			return jobDetailList;
		} catch (SchedulerException e) {
			log.error("获取所有调度job实例失败：" + e.getMessage());
			return null;
		}
	}

	/**
	 * 获取所有的JobKey
	 * @return
	 */
	public static List<JobKey> getAllJobKey() {
		List<JobKey> jobKeyList = new ArrayList<JobKey>();
		try {
			for (String jobGroup : scheduler.getJobGroupNames()) {
				for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.<JobKey>groupEquals(jobGroup))) {
					jobKeyList.add(jobKey);
				}
			}
			return jobKeyList;
		} catch (SchedulerException e) {
			log.error("获取所有调度任务的jobKey失败：" + e.getMessage());
			return null;
		}
	}

	/**
	 * 根据任务名称创建定时任务
	 * @param taskName        任务名称
	 * @param jobClass        需要执行的任务类
	 * @param taskCron        cron表达式
	 * @param taskDescription 任务描述
	 * @param jobDataMap      运行时可能需要的参数信息
	 * @return 创建成功true，出现异常false
	 */
	public static boolean createTask(String taskName, Class<? extends Job> jobClass, String taskCron,
			String taskDescription, JobDataMap jobDataMap) {
		// 根据任务名称构建JobKey和TriggerKey
		JobKey jobKey = new JobKey(taskName);
		TriggerKey triggerKey = new TriggerKey(taskName);
		return createTask(jobKey, triggerKey, jobClass, taskCron, taskDescription, jobDataMap);
	}

	/**
	 * 根据任务名称创建定时任务
	 * @param taskName          任务名称
	 * @param jobClass          需要执行的任务类
	 * @param startTime         开始执行时间
	 * @param intervalInSeconds 间隔时间（以秒为单位）
	 * @param taskDescription   任务描述
	 * @param jobDataMap        运行时可能需要的参数信息
	 * @return 创建成功true，出现异常false
	 */
	public static boolean createTask(String taskName, Class<? extends Job> jobClass, Date startTime,
			Integer intervalInSeconds, String taskDescription, JobDataMap jobDataMap) {
		// 根据任务名称构建JobKey和TriggerKey
		JobKey jobKey = new JobKey(taskName);
		TriggerKey triggerKey = new TriggerKey(taskName);
		return createTask(jobKey, triggerKey, jobClass, startTime, intervalInSeconds, taskDescription, jobDataMap);
	}

	/**
	 * 根据任务名称和任务组创建定时任务
	 * @param taskName        任务名称
	 * @param taskGroup       任务组
	 * @param jobClass        需要执行的任务类
	 * @param taskCron        cron表达式
	 * @param taskDescription 任务描述
	 * @param jobDataMap      运行时可能需要的参数信息
	 * @return 创建成功true，出现异常false
	 */
	public static boolean createTask(String taskName, String taskGroup, Class<? extends Job> jobClass, String taskCron,
			String taskDescription, JobDataMap jobDataMap) {
		// 根据任务名称和任务组构建JobKey和TriggerKey
		JobKey jobKey = new JobKey(taskName, taskGroup);
		TriggerKey triggerKey = new TriggerKey(taskName, taskGroup);
		return createTask(jobKey, triggerKey, jobClass, taskCron, taskDescription, jobDataMap);
	}

	/**
	 * 根据任务名称和任务组创建定时任务
	 * @param taskName          任务名称
	 * @param taskGroup         任务组
	 * @param jobClass          需要执行的任务类
	 * @param startTime         开始执行时间
	 * @param intervalInSeconds 间隔时间（以秒为单位）
	 * @param taskDescription   任务描述
	 * @param jobDataMap        运行时可能需要的参数信息
	 * @return 创建成功true，出现异常false
	 */
	public static boolean createTask(String taskName, String taskGroup, Class<? extends Job> jobClass, Date startTime,
			Integer intervalInSeconds, String taskDescription, JobDataMap jobDataMap) {
		// 根据任务名称和任务组构建JobKey和TriggerKey
		JobKey jobKey = new JobKey(taskName, taskGroup);
		TriggerKey triggerKey = new TriggerKey(taskName, taskGroup);
		return createTask(jobKey, triggerKey, jobClass, startTime, intervalInSeconds, taskDescription, jobDataMap);
	}

	/**
	 * 根据jobKey和triggerKey创建定时任务
	 * @param jobKey
	 * @param triggerKey
	 * @param jobClass        需要执行的任务类
	 * @param taskCron        cron表达式
	 * @param taskDescription 任务描述
	 * @param jobDataMap      运行时可能需要的参数信息
	 * @return 创建成功true，出现异常false
	 */
	public static boolean createTask(JobKey jobKey, TriggerKey triggerKey, Class<? extends Job> jobClass,
			String taskCron, String taskDescription, JobDataMap jobDataMap) {
		try {
			JobDetail jobDetail = builderJobDetail(jobKey, jobClass, taskDescription, jobDataMap);
			Trigger trigger = builderCronTrigger(triggerKey, taskCron, taskDescription);
			// 设置使用定义的触发器trigger安排执行任务job
			scheduler.scheduleJob(jobDetail, trigger);
			return true;
		} catch (SchedulerException e) {
			log.error("根据jobKey和triggerKey创建定时任务失败：" + e.getMessage());
			return false;
		}
	}

	/**
	 * 根据jobKey和triggerKey创建定时任务
	 * @param jobKey
	 * @param triggerKey
	 * @param jobClass          需要执行的任务类
	 * @param startTime         开始执行时间
	 * @param intervalInSeconds 间隔时间（以秒为单位）
	 * @param taskDescription   任务描述
	 * @param jobDataMap        运行时可能需要的参数信息
	 * @return 创建成功true，出现异常false
	 */
	public static boolean createTask(JobKey jobKey, TriggerKey triggerKey, Class<? extends Job> jobClass,
			Date startTime, Integer intervalInSeconds, String taskDescription, JobDataMap jobDataMap) {
		try {
			JobDetail jobDetail = builderJobDetail(jobKey, jobClass, taskDescription, jobDataMap);
			Trigger trigger = null;
			if (startTime == null) {
				trigger = builderSimpleTrigger(triggerKey, intervalInSeconds, taskDescription);
			} else {
				trigger = builderSimpleTrigger(triggerKey, startTime, intervalInSeconds, taskDescription);
			}
			// 设置使用定义的触发器trigger安排执行任务job
			scheduler.scheduleJob(jobDetail, trigger);
			return true;
		} catch (SchedulerException e) {
			log.error("根据jobKey和triggerKey创建定时任务失败：" + e.getMessage());
			return false;
		}
	}

	/**
	 * 构建SimpleTrigger触发器详情
	 * @param triggerKey
	 * @param intervalInSeconds 间隔时间（以秒为单位）
	 * @param taskDescription   任务描述
	 * @return
	 */
	public static SimpleTrigger builderSimpleTrigger(TriggerKey triggerKey, Integer intervalInSeconds,
			String taskDescription) {
		// 表达式调度构建器,放入传入的间隔时间，以秒为单位（一直执行）
		SimpleScheduleBuilder simpleBuilder = SimpleScheduleBuilder.simpleSchedule()
				.withMisfireHandlingInstructionFireNow();
		simpleBuilder.withIntervalInSeconds(intervalInSeconds).repeatForever();
		// 按新的间隔时间构建一个新的触发器
		TriggerBuilder<SimpleTrigger> triggerBuilder = TriggerBuilder.newTrigger().withIdentity(triggerKey)
				.withDescription(taskDescription).withSchedule(simpleBuilder);
		return triggerBuilder.build();
	}

	/**
	 * 构建SimpleTrigger触发器详情
	 * @param triggerKey
	 * @param startTime         开始执行时间
	 * @param intervalInSeconds 间隔时间（以秒为单位）
	 * @param taskDescription   任务描述
	 * @return
	 */
	public static SimpleTrigger builderSimpleTrigger(TriggerKey triggerKey, Date startTime, Integer intervalInSeconds,
			String taskDescription) {
		// 表达式调度构建器，放入传入的间隔时间，以秒为单位（一直执行）
		SimpleScheduleBuilder simpleBuilder = SimpleScheduleBuilder.simpleSchedule()
				.withMisfireHandlingInstructionFireNow();
		simpleBuilder.withIntervalInSeconds(intervalInSeconds).repeatForever();
		// 按新的间隔时间构建一个新的触发器
		TriggerBuilder<SimpleTrigger> triggerBuilder = TriggerBuilder.newTrigger().withIdentity(triggerKey)
				.withDescription(taskDescription).startAt(startTime).withSchedule(simpleBuilder);
		return triggerBuilder.build();
	}

	/**
	 * 构建CronTrigger触发器详情
	 * @param triggerKey
	 * @param taskCron        cron表达式
	 * @param taskDescription 任务描述
	 * @return
	 */
	public static CronTrigger builderCronTrigger(TriggerKey triggerKey, String taskCron, String taskDescription) {
		// 表达式调度构建器,放入传入的cron表达式
		CronScheduleBuilder cronBuilder = CronScheduleBuilder.cronSchedule(taskCron)
				.withMisfireHandlingInstructionIgnoreMisfires();
		// 按新的cron表达式构建一个新的触发器
		TriggerBuilder<CronTrigger> triggerBuilder = TriggerBuilder.newTrigger().withIdentity(triggerKey)
				.withDescription(taskDescription).withSchedule(cronBuilder);
		return triggerBuilder.build();
	}

	/**
	 * 构建jobDetail任务详情
	 * @param jobKey
	 * @param jobClass        需要执行的任务类
	 * @param taskDescription 任务描述
	 * @param jobDataMap      运行时可能需要的参数信息
	 * @return
	 */
	public static JobDetail builderJobDetail(JobKey jobKey, Class<? extends Job> jobClass, String taskDescription,
			JobDataMap jobDataMap) {
		// 构建job实例信息,放入传入的job类对象
		JobBuilder jobBuilder = JobBuilder.newJob(jobClass).withIdentity(jobKey).withDescription(taskDescription);
		if (null == jobDataMap) {
			return jobBuilder.build();
		} else {
			return jobBuilder.usingJobData(jobDataMap).build();
		}
	}

	/**
	 * 根据任务名称修改cron定时任务的触发时间规则
	 * @param taskName 任务名称
	 * @param taskCron 新的cron表达式
	 * @return 更新成功true，出现异常false
	 */
	public static boolean updateTaskTrigger(String taskName, String taskCron) {
		return updateTaskTrigger(getTriggerKey(taskName), taskCron);
	}

	/**
	 * 根据任务名称修改simple定时任务的触发时间规则
	 * @param taskName          任务名称
	 * @param startTime         开始执行时间
	 * @param intervalInSeconds 新的间隔时间（以秒为单位）
	 * @return 更新成功true，出现异常false
	 */
	public static boolean updateTaskTrigger(String taskName, Date startTime, Integer intervalInSeconds) {
		return updateTaskTrigger(getTriggerKey(taskName), startTime, intervalInSeconds);
	}

	/**
	 * 根据triggerKey修改cron定时任务的触发时间规则
	 * @param triggerKey
	 * @param taskCron   新的cron表达式
	 * @return 更新成功true，出现异常false
	 */
	public static boolean updateTaskTrigger(TriggerKey triggerKey, String taskCron) {
		try {
			CronTrigger trigger = updateCronTrigger(triggerKey, taskCron);
			if (null == trigger) {
				return false;
			}
			// 根据新的触发器重新设置调度任务
			scheduler.rescheduleJob(triggerKey, trigger);
			return true;
		} catch (SchedulerException e) {
			log.error("根据triggerKey修改cron定时任务的触发时间规则失败：" + e.getMessage());
			return false;
		}
	}

	/**
	 * 根据triggerKey修改simple定时任务的触发时间规则
	 * @param triggerKey
	 * @param startTime         开始执行时间
	 * @param intervalInSeconds 新的间隔时间（以秒为单位）
	 * @return 更新成功true，出现异常false
	 */
	public static boolean updateTaskTrigger(TriggerKey triggerKey, Date startTime, Integer intervalInSeconds) {
		try {
			SimpleTrigger trigger = null;
			if (startTime == null) {
				trigger = updateSimpleTrigger(triggerKey, intervalInSeconds);
			} else {
				trigger = updateSimpleTrigger(triggerKey, startTime, intervalInSeconds);
			}
			if (null == trigger) {
				return false;
			}
			// 根据新的触发器重新设置调度任务
			scheduler.rescheduleJob(triggerKey, trigger);
			return true;
		} catch (SchedulerException e) {
			log.error("根据triggerKey修改simple定时任务的触发时间规则失败：" + e.getMessage());
			return false;
		}
	}

	/**
	 * 修改CronTrigger触发器的触发规则
	 * @param triggerKey
	 * @param taskCron   新的cron表达式
	 * @return
	 */
	public static CronTrigger updateCronTrigger(TriggerKey triggerKey, String taskCron) {
		try {
			// 表达式调度构建器,放入传入的cron表达式
			CronScheduleBuilder cronBuilder = CronScheduleBuilder.cronSchedule(taskCron)
					.withMisfireHandlingInstructionIgnoreMisfires();
			// 获取cron触发器
			CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
			// 按新的cron表达式重新构建触发器
			trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(cronBuilder).build();
			return trigger;
		} catch (SchedulerException e) {
			log.error("根据triggerKey获取原Cron触发器失败：" + e.getMessage());
			return null;
		}
	}

	/**
	 * 修改SimpleTrigger触发器的触发规则（错过的任务不执行）详情请参考此博客（https://blog.csdn.net/u010648555/article/details/53672738）
	 * @param triggerKey
	 * @param intervalInSeconds 新的间隔时间（以秒为单位）
	 * @return
	 */
	public static SimpleTrigger updateSimpleTrigger(TriggerKey triggerKey, Integer intervalInSeconds) {
		try {
			// 表达式调度构建器,放入传入的间隔时间（以秒为单位）
			SimpleScheduleBuilder simpleBuilder = SimpleScheduleBuilder.simpleSchedule()
					.withMisfireHandlingInstructionFireNow();
			simpleBuilder.withIntervalInSeconds(intervalInSeconds).repeatForever();
			// 获取simple触发器
			SimpleTrigger trigger = (SimpleTrigger) scheduler.getTrigger(triggerKey);
			// 按新的时间间隔（以秒为单位）重新构建触发器
			trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(simpleBuilder).build();
			return trigger;
		} catch (SchedulerException e) {
			log.error("根据triggerKey获取原Simple触发器失败：" + e.getMessage());
			return null;
		}
	}

	/**
	 * 修改SimpleTrigger触发器的触发规则（错过的任务不执行）详情请参考此博客（https://blog.csdn.net/u010648555/article/details/53672738）
	 * @param triggerKey
	 * @param startTime         开始执行时间
	 * @param intervalInSeconds 新的间隔时间（以秒为单位）
	 * @return
	 */
	public static SimpleTrigger updateSimpleTrigger(TriggerKey triggerKey, Date startTime, Integer intervalInSeconds) {
		try {
			// 表达式调度构建器,放入传入的间隔时间（以秒为单位）
			SimpleScheduleBuilder simpleBuilder = SimpleScheduleBuilder.simpleSchedule()
					.withMisfireHandlingInstructionFireNow();
			simpleBuilder.withIntervalInSeconds(intervalInSeconds).repeatForever();
			// 获取simple触发器
			SimpleTrigger trigger = (SimpleTrigger) scheduler.getTrigger(triggerKey);
			// 按新的时间间隔（以秒为单位）重新构建触发器
			trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).startAt(startTime)
					.withSchedule(simpleBuilder).build();
			return trigger;
		} catch (SchedulerException e) {
			log.error("根据triggerKey获取原Simple触发器失败：" + e.getMessage());
			return null;
		}
	}

	/**
	 * 根据任务名称和jobDataMap立即执行定时任务(只执行一次)
	 * @param taskName   任务名称
	 * @param jobDataMap 存放任务运行时所需要的参数
	 * @return 运行成功true，出现异常false
	 */
	public static boolean runTask(String taskName, JobDataMap jobDataMap) {
		return runTask(getJobKey(taskName), jobDataMap);
	}

	/**
	 * 根据jobKey和jobDataMap立即执行定时任务(只执行一次)
	 * @param jobKey
	 * @param jobDataMap 存放任务运行时所需要的参数
	 * @return 运行成功true，出现异常false
	 */
	public static boolean runTask(JobKey jobKey, JobDataMap jobDataMap) {
		try {
			scheduler.triggerJob(jobKey, jobDataMap);
			return true;
		} catch (SchedulerException e) {
			log.error("根据jobKey和jobDataMap立即执行定时任务失败：" + e.getMessage());
			return false;
		}
	}

	/**
	 * 根据任务名称暂停任务
	 * @param taskName 任务名称
	 * @return 暂停成功true，出现异常false
	 */
	public static boolean pauseTask(String taskName) {
		return pauseTask(getJobKey(taskName));
	}

	/**
	 * 根据jobKey暂停任务
	 *
	 * @param jobKey
	 * @return 暂停成功true，出现异常false
	 */
	public static boolean pauseTask(JobKey jobKey) {
		try {
			scheduler.pauseJob(jobKey);
			return true;
		} catch (SchedulerException e) {
			log.error("根据jobKey暂停定时任务失败：" + e.getMessage());
			return false;
		}
	}

	/**
	 * 根据任务名称恢复任务
	 * @param taskName 任务名称
	 * @return 恢复成功true，出现异常false
	 */
	public static boolean resumeTask(String taskName) {
		return resumeTask(getJobKey(taskName));
	}

	/**
	 * 根据jobKey恢复任务
	 *
	 * @param jobKey
	 * @return 恢复成功true，出现异常false
	 */
	public static boolean resumeTask(JobKey jobKey) {
		try {
			scheduler.resumeJob(jobKey);
			return true;
		} catch (SchedulerException e) {
			log.error("根据jobKey恢复定时任务失败：" + e.getMessage());
			return false;
		}
	}

	/**
	 * 根据任务名称删除定时任务
	 * @param taskName 任务名称
	 * @return 删除成功true，删除失败或出现异常false
	 */
	public static boolean deleteTask(String taskName) {
		return deleteTask(getJobKey(taskName));
	}

	/**
	 * 根据jobKey删除定时任务
	 * @param jobKey
	 * @return 删除成功true，删除失败或出现异常false
	 */
	public static boolean deleteTask(JobKey jobKey) {
		try {
			return scheduler.deleteJob(jobKey);
		} catch (SchedulerException e) {
			log.error("根据jobKey删除定时任务失败：" + e.getMessage());
			return false;
		}
	}

	/**
	 * 启动所有任务
	 * @return
	 */
	public static boolean startAllTask() {
		try {
			scheduler.start();
			return true;
		} catch (SchedulerException e) {
			log.error("启动所有任务失败：" + e.getMessage());
			return false;
		}
	}

	/**
	 * 获取previousFireTime
	 * @param taskName 任务名称
	 * @return 如果不存在，返回null
	 */
	public static Date getTriggerPreviousFireTime(String taskName) {
		try {
			List<? extends Trigger> list = scheduler.getTriggersOfJob(JobKey.jobKey(taskName));
			if (list.size() == 0) {
				log.error("根据jobKey获取触发器previousFireTime失败：该jobName不存在");
				return null;
			}
			return list.get(0).getPreviousFireTime();
		} catch (SchedulerException e) {
			log.error("根据jobKey获取触发器previousFireTime失败：" + e.getMessage());
			return null;
		}
	}

	/**
	 * 获取nextFireTime
	 * @param taskName 任务名称
	 * @return 如果不存在，返回null
	 */
	public static Date getTriggerNextFireTime(String taskName) {
		try {
			List<? extends Trigger> list = scheduler.getTriggersOfJob(JobKey.jobKey(taskName));
			if (list.size() == 0) {
				log.error("根据jobKey获取触发器nextFireTime失败：该jobName不存在");
				return null;
			}
			return list.get(0).getNextFireTime();
		} catch (SchedulerException e) {
			log.error("根据jobKey获取触发器nextFireTime失败：" + e.getMessage());
			return null;
		}
	}

	/**
	 * 获取simple的间隔时间（以秒为单位）
	 * @param taskName 任务名称
	 * @return 如果不存在，返回null
	 */
	public static Integer getSimpleTriggerInterval(String taskName) {
		try {
			SimpleTrigger simpleTrigger = (SimpleTrigger) scheduler.getTrigger(TriggerKey.triggerKey(taskName));
			Long interval = simpleTrigger.getRepeatInterval();
			return interval.intValue() / 1000;
		} catch (SchedulerException e) {
			log.error("根据triggerKey获取触发器interval失败：", e.getMessage());
			return null;
		}
	}
}
