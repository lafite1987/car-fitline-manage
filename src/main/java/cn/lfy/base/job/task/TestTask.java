package cn.lfy.base.job.task;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cn.lfy.common.job.AbstractTaskInterface;

@Service
public class TestTask extends AbstractTaskInterface {

	private final static Logger LOG = LoggerFactory.getLogger(TestTask.class);
	
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		LOG.info("start mock module report data to gateway...");
		
	}

}
