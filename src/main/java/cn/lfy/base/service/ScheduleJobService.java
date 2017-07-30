package cn.lfy.base.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.lfy.common.job.model.ScheduleJob;

public interface ScheduleJobService {

	/**
	 * 根据IDC机房查询可用定时任务列表
	 * @param idc
	 * @return
	 */
	List<ScheduleJob> findAllEnabled(String idc);
	
	/**
	 * 获取所有调度配置
	 * 
	 * @return
	 */
	public List<ScheduleJob> findAll(@Param("idc")String idc);

	public ScheduleJob loadJobById( @Param( "scheduleJobId" ) Long scheduleJobId);

	public Integer update( @Param( "job" ) ScheduleJob scheduleJob );

	public Integer updateStatus( @Param( "job" ) ScheduleJob scheduleJob );

	public Integer create( @Param( "job" ) ScheduleJob scheduleJob );

	public Integer delete( @Param( "scheduleJobId" ) Long scheduleJobId );

	public Integer countByNameAndGroup( @Param( "job" ) ScheduleJob scheduleJob );
}
