package cn.lfy.base.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.wordnik.swagger.annotations.ApiOperation;

import cn.lfy.base.job.ScheduleJobProxyService;
import cn.lfy.common.framework.exception.ApplicationException;
import cn.lfy.common.framework.exception.ErrorCode;
import cn.lfy.common.job.model.ScheduleJob;
import cn.lfy.common.model.ResultDTO;
import cn.lfy.common.page.Page;
import cn.lfy.common.utils.RequestUtil;

/**
 * 定时任务控制器
 * @author Leo.liao
 * @date 2017-03-11
 */
@Controller
@RequestMapping("/manager/scheduleJob")
public class ScheduleJobController {

    @Autowired
    private ScheduleJobProxyService scheduleJobProxyService;
    
    @RequestMapping("/api/list")
    @ResponseBody
    @ApiOperation(value = "定时任务列表", httpMethod = "GET", notes = "定时任务列表")
    public ResultDTO<Page<ScheduleJob>> apiList() throws ApplicationException {
        List<ScheduleJob> list = Lists.newArrayList();
		try {
			list = scheduleJobProxyService.getAllJob();
		} catch (SchedulerException e) {
			throw ApplicationException.newInstance(ErrorCode.ERROR);
		}
		ResultDTO<Page<ScheduleJob>> resultDTO = new ResultDTO<>();
		Page<ScheduleJob> page = new Page<ScheduleJob>(list, 1, 100, list.size());
		resultDTO.setData(page);
		return resultDTO;
    }
    /**
     * 删除定时任务
     * @param request
     * @return ModelAndView
     * @throws AdminException
     */
    @RequestMapping("/del")
    @ResponseBody
    @ApiOperation(value = "删除定时任务", httpMethod = "POST", notes = "删除定时任务")
    public ResultDTO<Void> del(ScheduleJob scheduleJob) throws ApplicationException {
    	ResultDTO<Void> result = new ResultDTO<>();
    	try {
			scheduleJobProxyService.deleteJob(scheduleJob);
		} catch (SchedulerException e) {
			throw ApplicationException.newInstance(ErrorCode.ERROR);
		}
        return result;
    }
    
    /**
     * 详情
     * @param request
     * @return
     * @throws AdminException
     */
    @RequestMapping("/detail")
    @ResponseBody
    @ApiOperation(value = "定时任务信息", httpMethod = "GET", notes = "定时任务信息")
    public ResultDTO<ScheduleJob> detail(HttpServletRequest request) throws ApplicationException {
        Long id = RequestUtil.getLong(request, "id");
        ScheduleJob scheduleJob;
		try {
			scheduleJob = scheduleJobProxyService.viewJob(id);
		} catch (SchedulerException e) {
			throw ApplicationException.newInstance(ErrorCode.ERROR);
		}
		ResultDTO<ScheduleJob> resultDTO = new ResultDTO<>();
		resultDTO.setData(scheduleJob);
		return resultDTO;
        
    }
    
    /**
     * 添加
     * @param request
     * @return ModelAndView
     * @throws AdminException
     */
    @RequestMapping("/add")
    @ResponseBody
    @ApiOperation(value = "添加定时任务", httpMethod = "POST", notes = "添加定时任务")
    public ResultDTO<Void> add(ScheduleJob scheduleJob) throws ApplicationException {
        try {
        	scheduleJob.setOperator(0L);
        	scheduleJob.setOpTime(System.currentTimeMillis()/1000);
			scheduleJobProxyService.createJob(scheduleJob);
		} catch (SchedulerException e) {
			throw ApplicationException.newInstance(ErrorCode.ERROR);
		}
        ResultDTO<Void> resultDTO = new ResultDTO<>();
        return resultDTO;
    }

    /**
     * 更新
     * @param request
     * @return ModelAndView
     * @throws AdminException
     */
    @RequestMapping("/update")
    @ResponseBody
    @ApiOperation(value = "更新定时任务", httpMethod = "POST", notes = "更新定时任务")
    public ResultDTO<Void> update(ScheduleJob scheduleJob) throws ApplicationException {
        try {
			scheduleJobProxyService.updateJob(scheduleJob);
		} catch (SchedulerException e) {
			throw ApplicationException.newInstance(ErrorCode.ERROR);
		}
        ResultDTO<Void> resultDTO = new ResultDTO<>();
        return resultDTO;
    }
    
    /**
     * 更新任务表达式
     * @param request
     * @return ModelAndView
     * @throws AdminException
     */
    @RequestMapping("/updateCron")
    @ResponseBody
    @ApiOperation(value = "更新定时任务表达式", httpMethod = "POST", notes = "添加定时任务表达式")
    public ResultDTO<Void> updateCron(ScheduleJob scheduleJob) throws ApplicationException {
        try {
			scheduleJobProxyService.rescheduleJob(scheduleJob);
		} catch (SchedulerException e) {
			throw ApplicationException.newInstance(ErrorCode.ERROR);
		}
        ResultDTO<Void> resultDTO = new ResultDTO<>();
        return resultDTO;
    }
    /**
     * 立即执行
     * @param request
     * @return ModelAndView
     * @throws AdminException
     */
    @RequestMapping("/runOne")
    @ResponseBody
    @ApiOperation(value = "运行一次定时任务", httpMethod = "POST", notes = "立即运行一次定时任务")
    public ResultDTO<Void> runOne(ScheduleJob scheduleJob) throws ApplicationException {
        try {
			scheduleJobProxyService.triggerJob(scheduleJob);
		} catch (SchedulerException e) {
			throw ApplicationException.newInstance(ErrorCode.ERROR);
		}
        ResultDTO<Void> resultDTO = new ResultDTO<>();
        return resultDTO;
    }
    
    /**
     * 立即执行
     * @param request
     * @return ModelAndView
     * @throws AdminException
     */
    @RequestMapping("/schedule")
    @ResponseBody
    @ApiOperation(value = "部署定时任务", httpMethod = "POST", notes = "部署定时任务")
    public ResultDTO<Void> schedule(ScheduleJob scheduleJob) throws ApplicationException {
        try {
			scheduleJobProxyService.scheduleJob(scheduleJob);
		} catch (SchedulerException e) {
			throw ApplicationException.newInstance(ErrorCode.ERROR);
		}
        ResultDTO<Void> resultDTO = new ResultDTO<>();
        return resultDTO;
    }
    /**
     * 暂停执行
     * @param request
     * @return ModelAndView
     * @throws AdminException
     */
    @RequestMapping("/pause")
    @ResponseBody
    @ApiOperation(value = "暂停定时任务", httpMethod = "POST", notes = "暂停定时任务")
    public ResultDTO<Void> pause(ScheduleJob scheduleJob) throws ApplicationException {
        try {
			scheduleJobProxyService.pauseJob(scheduleJob);
		} catch (SchedulerException e) {
			throw ApplicationException.newInstance(ErrorCode.ERROR);
		}
        ResultDTO<Void> resultDTO = new ResultDTO<>();
        return resultDTO;
    }
    /**
     * 暂停执行
     * @param request
     * @return ModelAndView
     * @throws AdminException
     */
    @RequestMapping("/resume")
    @ResponseBody
    @ApiOperation(value = "恢复定时任务", httpMethod = "POST", notes = "恢复暂停的定时任务")
    public ResultDTO<Void> resume(ScheduleJob scheduleJob) throws ApplicationException {
        try {
			scheduleJobProxyService.resumeJob(scheduleJob);
		} catch (SchedulerException e) {
			throw ApplicationException.newInstance(ErrorCode.ERROR);
		}
        ResultDTO<Void> resultDTO = new ResultDTO<>();
        return resultDTO;
    }
}
