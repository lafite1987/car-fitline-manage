package com.fitline.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fitline.model.CarInfo;
import com.fitline.service.CarInfoService;
import com.wordnik.swagger.annotations.ApiOperation;

import cn.lfy.common.model.ResultDTO;
import cn.lfy.common.page.Page;

@Controller
@RequestMapping("/manager/carInfo")
public class CarInfoCtrl {

	@Autowired
	private CarInfoService carInfoService;
	
	@RequestMapping("/list")
	@ResponseBody
	@ApiOperation(value = "汽车信息列表", httpMethod = "GET", notes = "汽车信息列表接口")
	public ResultDTO<Page<CarInfo>> list(
			@RequestParam(name = "currentPage", defaultValue = "1")int currentPage, 
			@RequestParam(name = "pageSize", defaultValue = "10")int pageSize) {
		ResultDTO<Page<CarInfo>> resultDTO = new ResultDTO<>();
		Page<CarInfo> page = carInfoService.query(null, currentPage, pageSize);
		resultDTO.setData(page);
		return resultDTO;
	}
}
