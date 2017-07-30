package cn.lfy.base.web;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;

import cn.lfy.base.model.Menu;
import cn.lfy.base.model.TreeNode;
import cn.lfy.base.service.MenuService;
import cn.lfy.common.framework.exception.ApplicationException;
import cn.lfy.common.framework.exception.ErrorCode;
import cn.lfy.common.model.Message;
import cn.lfy.common.utils.RequestUtil;


@Controller
@RequestMapping("/manage/menu")
public class MenuController {

    @Resource
    private MenuService menuService;

    @RequestMapping("/list")
    public String list(HttpServletRequest request) throws ApplicationException {
        return "/system/menu/list-menu";
    }
    
    @RequestMapping("/api/list")
    @ResponseBody
    public Object api_list() {
    	List<Menu> menus = menuService.findMenuList();
		List<TreeNode> treeList = Lists.newArrayList();
		for(Menu menu : menus) {
			treeList.add(new TreeNode(menu.getId(), menu.getName(), menu.getParentId(), false));
		}
		List<TreeNode> tree = Lists.newArrayList();
		for(TreeNode node1 : treeList){  
			tree.add(node1);   
		}
        Message.Builder builder = Message.newBuilder();
        builder.data(tree);
    	return builder.build();
    }
    
    @RequestMapping("/update")
    @ResponseBody
    public Object update(Menu menu, HttpServletRequest request, HttpServletResponse response) throws ApplicationException {
    	Message.Builder builder = Message.newBuilder();
        Long id = RequestUtil.getLong(request, "id");
        Menu menuDb=menuService.getById(id);
        
        menuDb.setName(menu.getName());
        menuDb.setUrl(menu.getUrl());
        menuDb.setOrderNo(menu.getOrderNo());
        menuDb.setOnMenu(menu.getOnMenu());
        menuService.updateByIdSelective(menuDb);
        return builder.build();
    }
    
    @RequestMapping("/detail")
    @ResponseBody
    public Object detail(HttpServletRequest request, HttpServletResponse response) throws ApplicationException {
    	Message.Builder builder = Message.newBuilder();
        Long id=RequestUtil.getLong(request, "id");
        if(null != id) {
            Menu menu=menuService.getById(id);
            builder.data(menu);
        }
        return builder.build();
    }

    @RequestMapping("/add")
    @ResponseBody
    public Object add(HttpServletRequest request, Menu menu) throws ApplicationException {
    	Message.Builder builder = Message.newBuilder();
        menuService.save(menu);
        return builder.build();
    }

    @RequestMapping("/del")
    @ResponseBody
    public Object deleteTreeNode(HttpServletRequest request) throws ApplicationException {
    	Message.Builder builder = Message.newBuilder();
        Long menuId =RequestUtil.getLong(request, "id");
        if(null == menuId) {
            throw ApplicationException.newInstance(ErrorCode.PARAM_ILLEGAL, "menuId");
        }
        menuService.deleteById(menuId);
        return builder.build();
    }
}
