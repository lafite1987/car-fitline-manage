package cn.lfy.base.job;

import java.util.List;

import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lfy.base.service.ScheduleJobService;
import cn.lfy.common.job.QuartzJobController;
import cn.lfy.common.job.model.ScheduleJob;

@Service
public class ScheduleJobProxyService {

	@Autowired
	ScheduleJobService scheduleJobService;
	
	@Autowired
	QuartzJobController quartzJobController;

	public List<ScheduleJob> getAllJob() throws SchedulerException {
		return scheduleJobService.findAll("IDC_DEFAULT");
	}

	public List<ScheduleJob> findAllEnabled() throws SchedulerException {
		return scheduleJobService.findAllEnabled("IDC_DEFAULT");
	}
	
	public Integer pauseJob( ScheduleJob scheduleJob ) throws SchedulerException {
		scheduleJob = scheduleJobService.loadJobById(scheduleJob.getJobId());
		quartzJobController.pauseJob( scheduleJob );
		scheduleJob.setJobStatus( ScheduleJob.STATUS_PAUSED );
		return scheduleJobService.updateStatus( scheduleJob );
	}

	public Integer resumeJob( ScheduleJob scheduleJob ) throws SchedulerException {
		scheduleJob = scheduleJobService.loadJobById(scheduleJob.getJobId());
		quartzJobController.resumeJob( scheduleJob );
		scheduleJob.setJobStatus( ScheduleJob.STATUS_NORMAL );
		return scheduleJobService.updateStatus( scheduleJob );
	}

	public Integer deleteJob( ScheduleJob scheduleJob ) throws SchedulerException {
		quartzJobController.deleteJob( scheduleJob );
		return scheduleJobService.delete( scheduleJob.getJobId() );
	}

	public Integer scheduleJob( ScheduleJob scheduleJob ) throws SchedulerException {
		scheduleJob = scheduleJobService.loadJobById(scheduleJob.getJobId());
		if(ScheduleJob.STATUS_NORMAL.equals( scheduleJob.getJobStatus() ) ) {
			quartzJobController.scheduleSingleJob( scheduleJob );
		}
		return 1;
	}

	public Integer triggerJob( ScheduleJob scheduleJob ) throws SchedulerException {
		scheduleJob = scheduleJobService.loadJobById(scheduleJob.getJobId());
		quartzJobController.triggerJob( scheduleJob );
		return 1;
	}

	public Integer createJob( ScheduleJob scheduleJob ) throws SchedulerException {
		int existsCount = scheduleJobService.countByNameAndGroup( scheduleJob );
		if( existsCount > 0 ) {//
			throw new SchedulerException("指定的任务名称与任务分组已存在");
		}
		
		Integer createCount = scheduleJobService.create( scheduleJob );
		if(ScheduleJob.STATUS_NORMAL.equals( scheduleJob.getJobStatus() ) ) {
			quartzJobController.scheduleSingleJob( scheduleJob );
		}
		return createCount;
	}

	public Integer rescheduleJob( ScheduleJob scheduleJob ) throws SchedulerException {
		ScheduleJob dbJob = scheduleJobService.loadJobById( scheduleJob.getJobId() );
		dbJob.setCronExpression( scheduleJob.getCronExpression() );
		
		quartzJobController.rescheduleJob( dbJob );
		
		return scheduleJobService.update( dbJob );
	}

	public ScheduleJob viewJob( Long scheduleJobId ) throws SchedulerException {
		ScheduleJob scheduleJob = scheduleJobService.loadJobById( scheduleJobId );
		scheduleJob.setIsRunning( quartzJobController.isJobRunning( scheduleJob ) );
		return scheduleJob;
	}

	public Integer updateJob( ScheduleJob scheduleJob ) throws SchedulerException {
		scheduleJob.setJobStatus(ScheduleJob.STATUS_NONE);
		scheduleJob.setOperator(0L);
		int existsCount = scheduleJobService.countByNameAndGroup( scheduleJob );
		if( existsCount > 0 ) {//
			throw new SchedulerException("存在重复的任务名称与任务分组");
		}
		return scheduleJobService.update( scheduleJob );
	}

}
