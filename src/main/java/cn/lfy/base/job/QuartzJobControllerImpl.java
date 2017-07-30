package cn.lfy.base.job;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import cn.lfy.base.service.ScheduleJobService;
import cn.lfy.common.job.AbstractTaskInterface;
import cn.lfy.common.job.QuartzAsyncJobFactory;
import cn.lfy.common.job.QuartzJobController;
import cn.lfy.common.job.QuartzJobFactory;
import cn.lfy.common.job.model.ScheduleJob;

/**
 * 
 * 定时任务的控制类<br>
 * 本类实现了InitializingBean, <br>
 * 在Spring 初始化后就会自动加载DB中配置好的Task并部署
 *
 * @author leo.liao
 * @date 2014-12-8 下午4:39:51
 */
@Service
public class QuartzJobControllerImpl implements QuartzJobController, ApplicationListener<ContextRefreshedEvent> {

	private static final Logger logger = LoggerFactory.getLogger( QuartzJobControllerImpl.class );

	@Autowired
	private SchedulerFactoryBean schedulerFactoryBean;

	@Autowired
	ScheduleJobService scheduleJobService;

	@Override
	public void loadAndStartTaskFromDB() throws SchedulerException {
		List<ScheduleJob> jobList = scheduleJobService.findAllEnabled("DEFAULT");
		logger.info( "Enabled Job List size: {}", jobList.size() );
		scheduleAllJobs( jobList );
	}

	@Override
	public void scheduleAllJobs( List<ScheduleJob> jobList ) {
		logger.info( "scheduleAllJobs: size: {}", jobList.size() );
		for( ScheduleJob job : jobList ) {
			scheduleSingleJob( job );
		}
	}

	@Override
	public void scheduleSingleJob( ScheduleJob scheduleJob ) {
		logger.info( "scheduleSingleJob: {}", scheduleJob );
		if( !context.containsBean( scheduleJob.getJobName() ) ) {
			logger.warn( "Task bean with name:{} not found! schedule job skipped", scheduleJob );
			return;
		}

		Scheduler scheduler = schedulerFactoryBean.getScheduler();

		try {
			// 如果这里报compilation error, 请检查quartz的包是否引用的2.1.7以上
			TriggerKey triggerKey = TriggerKey.triggerKey( scheduleJob.getJobName(), scheduleJob.getJobGroup() );

			/** 获取trigger，即在spring配置文件中定义的 bean id="myTrigger" */
			CronTrigger trigger = ( CronTrigger )scheduler.getTrigger( triggerKey );

			// 不存在，创建一个
			if( null == trigger ) {
				logger.warn( "scheduleSingleJob's trigger not exist, try to create one with the cron expression from the given scheduleJob: " + scheduleJob );
				 Class<? extends Job> jobClass = scheduleJob.isConcurrent() ? QuartzAsyncJobFactory.class : QuartzJobFactory.class;
				JobDetail jobDetail = JobBuilder.newJob( jobClass )
						.withIdentity( scheduleJob.getJobName(), scheduleJob.getJobGroup() ).build();
				jobDetail.getJobDataMap().put( "scheduleJob", scheduleJob );
				// 表达式调度构建器
				CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule( scheduleJob
						.getCronExpression() );

				// 按新的cronExpression表达式构建一个新的trigger
				trigger = TriggerBuilder.newTrigger().withIdentity( scheduleJob.getJobName(), scheduleJob.getJobGroup() ).withSchedule( scheduleBuilder )
						.build();

				scheduler.scheduleJob( jobDetail, trigger );
			} else {
				// Trigger已存在，那么更新相应的定时设置
				logger.warn( "scheduleSingleJob's trigger existed, try to update the cron expression with the given scheduleJob: " + scheduleJob );
				// 表达式调度构建器
				CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule( scheduleJob
						.getCronExpression() );

				// 按新的cronExpression表达式重新构建trigger
				trigger = trigger.getTriggerBuilder().withIdentity( triggerKey )
						.withSchedule( scheduleBuilder ).build();

				// 按新的trigger重新设置job执行
				scheduler.rescheduleJob( triggerKey, trigger );
			}
			if(scheduleJob.getIsBoot()){
				JobKey jobKey = JobKey.jobKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());
				scheduler.triggerJob(jobKey);
			}
		} catch( SchedulerException e ) {
			logger.error( "scheduleSingleJob error: {}", e.getMessage() );
		}
		logger.info( "scheduleSingleJob done" );
	}

	@Override
	public List<ScheduleJob> getScheduledJobs() throws SchedulerException {
		logger.info( "getScheduledJobs" );

		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		GroupMatcher<JobKey> matcher = GroupMatcher.anyJobGroup();
		Set<JobKey> jobKeys = scheduler.getJobKeys( matcher );
		List<ScheduleJob> jobList = new ArrayList<ScheduleJob>();
		for( JobKey jobKey : jobKeys ) {
			List<? extends Trigger> triggers = scheduler.getTriggersOfJob( jobKey );
			for( Trigger trigger : triggers ) {
				ScheduleJob job = new ScheduleJob();
				job.setJobName( jobKey.getName() );
				job.setJobGroup( jobKey.getGroup() );
				job.setDesc( "触发器:" + trigger.getKey() );
				Trigger.TriggerState triggerState = scheduler.getTriggerState( trigger.getKey() );
				job.setJobStatus( triggerState.name() );
				if( trigger instanceof CronTrigger ) {
					CronTrigger cronTrigger = ( CronTrigger )trigger;
					String cronExpression = cronTrigger.getCronExpression();
					job.setCronExpression( cronExpression );
				}
				jobList.add( job );
			}
		}
		return jobList;
	}

	@Override
	public List<ScheduleJob> getExecutingJobs() throws SchedulerException {
		logger.info( "getExecutingJobs" );
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		List<JobExecutionContext> executingJobs = scheduler.getCurrentlyExecutingJobs();
		List<ScheduleJob> jobList = new ArrayList<ScheduleJob>( executingJobs.size() );
		for( JobExecutionContext executingJob : executingJobs ) {
			ScheduleJob job = new ScheduleJob();
			JobDetail jobDetail = executingJob.getJobDetail();
			JobKey jobKey = jobDetail.getKey();
			Trigger trigger = executingJob.getTrigger();
			job.setJobName( jobKey.getName() );
			job.setJobGroup( jobKey.getGroup() );
			job.setDesc( "触发器:" + trigger.getKey() );
			Trigger.TriggerState triggerState = scheduler.getTriggerState( trigger.getKey() );
			job.setJobStatus( triggerState.name() );
			if( trigger instanceof CronTrigger ) {
				CronTrigger cronTrigger = ( CronTrigger )trigger;
				String cronExpression = cronTrigger.getCronExpression();
				job.setCronExpression( cronExpression );
			}
			jobList.add( job );
		}
		return jobList;
	}

	@Override
	public void pauseJob( ScheduleJob scheduleJob ) throws SchedulerException {
		logger.info( "pauseJob " + scheduleJob );
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		JobKey jobKey = JobKey.jobKey( scheduleJob.getJobName(), scheduleJob.getJobGroup() );
		if( scheduler.checkExists( jobKey ) ) {
			scheduler.pauseJob( jobKey );
		}
		else {
			logger.warn( "pauseJob {} not exists " + scheduleJob );
		}

		List<ScheduleJob> executingJobs = this.getExecutingJobs();
		for( ScheduleJob executingJob : executingJobs ) {
			if( executingJob.getJobName().equalsIgnoreCase( this.getClass().getName() ) ) {
				if( scheduler.checkExists( jobKey ) ) {
					scheduler.pauseJob( jobKey );
				}
				logger.warn( "pauseJob {} " + executingJob );
			}
		}

		Object obj = context.getBean( scheduleJob.getJobName() );
		if( obj instanceof AbstractTaskInterface ) {
			AbstractTaskInterface job = ( AbstractTaskInterface )obj;
			job.setInterrupt( true );
		}
	}

	@Override
	public void resumeJob( ScheduleJob scheduleJob ) throws SchedulerException {
		logger.info( "resumeJob " + scheduleJob );
		Object obj = context.getBean( scheduleJob.getJobName() );
		if( obj instanceof AbstractTaskInterface ) {
			AbstractTaskInterface job = ( AbstractTaskInterface )obj;
			job.setInterrupt( false );
		}

		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		JobKey jobKey = JobKey.jobKey( scheduleJob.getJobName(), scheduleJob.getJobGroup() );
		if( scheduler.checkExists( jobKey ) ) {
			scheduler.resumeJob( jobKey );
		}
		else {
			//有时在job暂停的时候,cron/jobName等会被更新,所以不可以直接resumeJob
			scheduleSingleJob( scheduleJob );
		}
	}

	@Override
	public void deleteJob( ScheduleJob scheduleJob ) throws SchedulerException {
		logger.info( "deleteJob " + scheduleJob );
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		JobKey jobKey = JobKey.jobKey( scheduleJob.getJobName(), scheduleJob.getJobGroup() );
		if( scheduler.checkExists( jobKey ) ) {
			scheduler.deleteJob( jobKey );
		}
		else {
			logger.warn( "deleteJob {} not exists " + scheduleJob );
		}
	}

	@Override
	public void triggerJob( ScheduleJob scheduleJob ) throws SchedulerException {
		logger.info( "triggerJob " + scheduleJob );
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		JobKey jobKey = JobKey.jobKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());
		scheduler.triggerJob(jobKey);
	}

	/**
	 * 先前我们在更新任务时，虽然更新了定时任务的执行时间，
	 * 但是并没有对参数进行更新，即使用context.getMergedJobDataMap().get(...)方法获取到的参数还是旧的。 
	 * 假设我们更新了任务的时间表达式，任务已按新的时间表达式在执行，但在获取到参数后发现时间表达式还是原来的。 
	 * 发现无法更新，试过其它几个api发现都不行，没有办法，最后采用了先删除任务再进行创建的方式来迂回实现参数的更新
	 * 
	 * */
	@Override
	public void rescheduleJob( ScheduleJob scheduleJob ) throws SchedulerException {
		logger.info( "rescheduleJob " + scheduleJob );
		this.deleteJob(scheduleJob);
		this.scheduleSingleJob(scheduleJob);
	}

	public Boolean isJobRunning( ScheduleJob scheduleJob ) throws SchedulerException {
		Boolean isRunningFlag = null;
		try {
			Object obj = context.getBean( scheduleJob.getJobName() );
			if( obj instanceof AbstractTaskInterface ) {
				AbstractTaskInterface job = ( AbstractTaskInterface )obj;
				isRunningFlag = job.isRunning();
			}
		} catch( NoSuchBeanDefinitionException e ) {
			logger.error( e.getMessage() );
		}
		return isRunningFlag;
	}

	private static org.springframework.context.ApplicationContext context = null;

	private static boolean isStarted = false;
	@Override
	public void onApplicationEvent( final ContextRefreshedEvent event ) {
		synchronized (this) {
			if(!isStarted) {
				context = event.getApplicationContext();
				try {
					this.loadAndStartTaskFromDB();
				} catch( SchedulerException e ) {
					logger.error( e.getMessage() );
				}
				isStarted = true;
			}
		}
		
	}

}
