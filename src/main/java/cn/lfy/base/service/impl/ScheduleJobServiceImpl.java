package cn.lfy.base.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lfy.base.dao.ScheduleJobDAO;
import cn.lfy.base.service.ScheduleJobService;
import cn.lfy.common.job.model.ScheduleJob;

@Service
public class ScheduleJobServiceImpl implements ScheduleJobService {

	@Autowired
	private ScheduleJobDAO scheduleJobDAO;

	@Override
	public List<ScheduleJob> findAllEnabled(String idc) {
		return scheduleJobDAO.findAllEnabled(idc);
	}

	@Override
	public List<ScheduleJob> findAll(String idc) {
		return scheduleJobDAO.findAll(idc);
	}

	@Override
	public ScheduleJob loadJobById(Long scheduleJobId) {
		return scheduleJobDAO.loadJobById(scheduleJobId);
	}

	@Override
	public Integer update(ScheduleJob scheduleJob) {
		scheduleJob.setOpTime(System.currentTimeMillis()/1000);
		return scheduleJobDAO.update(scheduleJob);
	}

	@Override
	public Integer updateStatus(ScheduleJob scheduleJob) {
		return scheduleJobDAO.updateStatus(scheduleJob);
	}

	@Override
	public Integer create(ScheduleJob scheduleJob) {
		return scheduleJobDAO.create(scheduleJob);
	}

	@Override
	public Integer delete(Long scheduleJobId) {
		return scheduleJobDAO.delete(scheduleJobId);
	}

	@Override
	public Integer countByNameAndGroup(ScheduleJob scheduleJob) {
		return scheduleJobDAO.countByNameAndGroup(scheduleJob);
	}
	
}
