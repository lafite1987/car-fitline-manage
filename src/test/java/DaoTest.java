import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.lfy.base.dao.UserDAO;
import cn.lfy.base.model.User;
import cn.lfy.common.page.Page;

import com.alibaba.fastjson.JSON;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring.xml")
public class DaoTest {

	@Autowired
	private UserDAO userDAO;
	
	@Test
	public void getByUsername() {
		User user = userDAO.selectByUsername("admin");
		System.out.println(user);
	}
	
	@Test
	public void pageList() {
		User user = new User();
		user.setPageIndex(0);
		user.setPageSize(2);
		Page<User> page = userDAO.list2(user);
		System.out.println(JSON.toJSONString(page));
	}
	
	@Test
	public void list3() {
		Page<User> page = userDAO.list3();
		System.out.println(JSON.toJSONString(page));
	}
}
