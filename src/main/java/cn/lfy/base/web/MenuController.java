package cn.lfy.base.web;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.wordnik.swagger.annotations.ApiOperation;

import cn.lfy.base.model.Menu;
import cn.lfy.base.model.TreeNode;
import cn.lfy.base.service.MenuService;
import cn.lfy.common.framework.exception.ApplicationException;
import cn.lfy.common.framework.exception.ErrorCode;
import cn.lfy.common.model.ResultDTO;


@Controller
@RequestMapping("/manager/menu")
public class MenuController {

    @Resource
    private MenuService menuService;

    @RequestMapping("/api/list")
    @ResponseBody
    @ApiOperation(value = "菜单树", httpMethod = "POST", notes = "菜单树")
    public ResultDTO<List<TreeNode>> api_list() {
    	List<Menu> menus = menuService.findMenuList();
		List<TreeNode> treeList = Lists.newArrayList();
		for(Menu menu : menus) {
			treeList.add(new TreeNode(menu.getId(), menu.getName(), menu.getParentId(), false));
		}
		List<TreeNode> tree = Lists.newArrayList();
		for(TreeNode node1 : treeList){  
			tree.add(node1);   
		}
		ResultDTO<List<TreeNode>> resultDTO = new ResultDTO<>();
		resultDTO.setData(tree);
    	return resultDTO;
    }
    
    @RequestMapping("/update")
    @ResponseBody
    @ApiOperation(value = "更新菜单", httpMethod = "POST", notes = "更新菜单")
    public ResultDTO<Void> update(Menu menu) throws ApplicationException {
    	ResultDTO<Void> resultDTO = new ResultDTO<>();
        Menu menuDb=menuService.getById(menu.getId());
        
        menuDb.setName(menu.getName());
        menuDb.setUrl(menu.getUrl());
        menuDb.setOrderNo(menu.getOrderNo());
        menuDb.setOnMenu(menu.getOnMenu());
        menuService.updateByIdSelective(menuDb);
        return resultDTO;
    }
    
    @RequestMapping("/detail")
    @ResponseBody
    @ApiOperation(value = "菜单详情", httpMethod = "GET", notes = "获取菜单详细信息")
    public ResultDTO<Menu> detail(Long id) throws ApplicationException {
    	ResultDTO<Menu> resultDTO = new ResultDTO<>();
        if(null != id) {
            Menu menu=menuService.getById(id);
            resultDTO.setData(menu);
        }
        return resultDTO;
    }

    @RequestMapping("/add")
    @ResponseBody
    @ApiOperation(value = "添加菜单", httpMethod = "POST", notes = "添加菜单")
    public ResultDTO<Void> add(Menu menu) throws ApplicationException {
    	ResultDTO<Void> resultDTO = new ResultDTO<>();
        menuService.save(menu);
        return resultDTO;
    }

    @RequestMapping("/del")
    @ResponseBody
    @ApiOperation(value = "删除菜单", httpMethod = "POST", notes = "删除菜单")
    public ResultDTO<Void> deleteTreeNode(Long id) throws ApplicationException {
    	ResultDTO<Void> resultDTO = new ResultDTO<>();
        if(null == id) {
            throw ApplicationException.newInstance(ErrorCode.PARAM_ILLEGAL, "menuId");
        }
        menuService.deleteById(id);
        return resultDTO;
    }
}
