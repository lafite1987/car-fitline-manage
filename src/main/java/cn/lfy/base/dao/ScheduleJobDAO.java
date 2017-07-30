package cn.lfy.base.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.lfy.common.job.model.ScheduleJob;

public interface ScheduleJobDAO {

	/**
	 * 获取所有可用的调度配置
	 * 
	 * @return
	 */
	public List<ScheduleJob> findAllEnabled(@Param("idc")String idc);
	
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
