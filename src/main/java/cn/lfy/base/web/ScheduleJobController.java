package cn.lfy.base.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Lists;

import cn.lfy.base.job.ScheduleJobProxyService;
import cn.lfy.common.framework.exception.ApplicationException;
import cn.lfy.common.framework.exception.ErrorCode;
import cn.lfy.common.job.model.ScheduleJob;
import cn.lfy.common.model.Message;
import cn.lfy.common.utils.RequestUtil;

/**
 * 定时任务控制器
 * @author Leo.liao
 * @date 2017-03-11
 */
@Controller
@RequestMapping("/manage/scheduleJob")
public class ScheduleJobController {

    @Autowired
    private ScheduleJobProxyService scheduleJobProxyService;
    
    /**
     * 定时任务列表
     * 
     * @param request
     * @return ModelAndView
     * @throws AdminException
     */
    @RequestMapping("/list")
    public ModelAndView list(HttpServletRequest request) throws ApplicationException {
        return new ModelAndView("/system/scheduler/scheduler");
    }
    
    @RequestMapping("/api/list")
    @ResponseBody
    public Object apiList(HttpServletRequest request) throws ApplicationException {
        List<ScheduleJob> list = Lists.newArrayList();
		try {
			list = scheduleJobProxyService.getAllJob();
		} catch (SchedulerException e) {
			throw ApplicationException.newInstance(ErrorCode.ERROR);
		}
		return Message.newBuilder().data(list).total(list.size()).build();
    }
    /**
     * 删除定时任务
     * 
     * @param request
     * @return ModelAndView
     * @throws AdminException
     */
    @RequestMapping("/del")
    @ResponseBody
    public Object del(ScheduleJob scheduleJob) throws ApplicationException {
    	Message.Builder builder = Message.newBuilder();
    	try {
			scheduleJobProxyService.deleteJob(scheduleJob);
		} catch (SchedulerException e) {
			throw ApplicationException.newInstance(ErrorCode.ERROR);
		}
        return builder.build();
    }
    
    /**
     * 详情
     * 
     * @param request
     * @return
     * @throws AdminException
     */
    @RequestMapping("/detail")
    @ResponseBody
    public Object detail(HttpServletRequest request) throws ApplicationException {
        Long id = RequestUtil.getLong(request, "id");
        ScheduleJob scheduleJob;
		try {
			scheduleJob = scheduleJobProxyService.viewJob(id);
		} catch (SchedulerException e) {
			throw ApplicationException.newInstance(ErrorCode.ERROR);
		}
		Message.Builder builder = Message.newBuilder().data(scheduleJob);
		return builder.build();
        
    }
    
    /**
     * 添加
     * 
     * @param request
     * @return ModelAndView
     * @throws AdminException
     */
    @RequestMapping("/add")
    @ResponseBody
    public Object add(ScheduleJob scheduleJob) throws ApplicationException {
    	Message.Builder builder = Message.newBuilder();
        try {
        	scheduleJob.setOperator(0L);
        	scheduleJob.setOpTime(System.currentTimeMillis()/1000);
			scheduleJobProxyService.createJob(scheduleJob);
		} catch (SchedulerException e) {
			throw ApplicationException.newInstance(ErrorCode.ERROR);
		}
        
        return builder.build();
    }

    /**
     * 更新
     * 
     * @param request
     * @return ModelAndView
     * @throws AdminException
     */
    @RequestMapping("/update")
    @ResponseBody
    public Object update(ScheduleJob scheduleJob) throws ApplicationException {
    	Message.Builder builder = Message.newBuilder();
        try {
			scheduleJobProxyService.updateJob(scheduleJob);
		} catch (SchedulerException e) {
			throw ApplicationException.newInstance(ErrorCode.ERROR);
		}
        return builder.build();
    }
    
    /**
     * 更新任务表达式
     * 
     * @param request
     * @return ModelAndView
     * @throws AdminException
     */
    @RequestMapping("/updateCron")
    @ResponseBody
    public Object updateCron(ScheduleJob scheduleJob) throws ApplicationException {
    	Message.Builder builder = Message.newBuilder();
        try {
			scheduleJobProxyService.rescheduleJob(scheduleJob);
		} catch (SchedulerException e) {
			throw ApplicationException.newInstance(ErrorCode.ERROR);
		}
        return builder.build();
    }
    /**
     * 立即执行
     * 
     * @param request
     * @return ModelAndView
     * @throws AdminException
     */
    @RequestMapping("/runOne")
    @ResponseBody
    public Object runOne(ScheduleJob scheduleJob) throws ApplicationException {
    	Message.Builder builder = Message.newBuilder();
        try {
			scheduleJobProxyService.triggerJob(scheduleJob);
		} catch (SchedulerException e) {
			throw ApplicationException.newInstance(ErrorCode.ERROR);
		}
        return builder.build();
    }
    
    /**
     * 立即执行
     * 
     * @param request
     * @return ModelAndView
     * @throws AdminException
     */
    @RequestMapping("/schedule")
    @ResponseBody
    public Object schedule(ScheduleJob scheduleJob) throws ApplicationException {
    	Message.Builder builder = Message.newBuilder();
        try {
			scheduleJobProxyService.scheduleJob(scheduleJob);
		} catch (SchedulerException e) {
			throw ApplicationException.newInstance(ErrorCode.ERROR);
		}
        return builder.build();
    }
    /**
     * 暂停执行
     * 
     * @param request
     * @return ModelAndView
     * @throws AdminException
     */
    @RequestMapping("/pause")
    @ResponseBody
    public Object pause(ScheduleJob scheduleJob) throws ApplicationException {
    	Message.Builder builder = Message.newBuilder();
        try {
			scheduleJobProxyService.pauseJob(scheduleJob);
		} catch (SchedulerException e) {
			throw ApplicationException.newInstance(ErrorCode.ERROR);
		}
        return builder.build();
    }
    /**
     * 暂停执行
     * 
     * @param request
     * @return ModelAndView
     * @throws AdminException
     */
    @RequestMapping("/resume")
    @ResponseBody
    public Object resume(ScheduleJob scheduleJob) throws ApplicationException {
    	Message.Builder builder = Message.newBuilder();
        try {
			scheduleJobProxyService.resumeJob(scheduleJob);
		} catch (SchedulerException e) {
			throw ApplicationException.newInstance(ErrorCode.ERROR);
		}
        return builder.build();
    }
}
