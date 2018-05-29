package cn.itcast.goods.category.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.category.domain.Category;
import cn.itcast.jdbc.TxQueryRunner;

public class CategoryDao {
	private QueryRunner qr = new TxQueryRunner();
	
	/**
	 * 此方法用来将map中的数据转换成Category类;
	 * @param map
	 * @return
	 */
	private Category toCategory(Map<String,Object> map){
		Category category = CommonUtils.toBean(map, Category.class);//此行代码执行完之后map中的pid仍然未被转换;
		String pid = (String)map.get("pid");
		if(null != pid){//pid不等与null时,说明不是一级分类,需要给它绑定父分类;
			Category parent = new Category();
			//创建一个父分类,将pid数值装载到父分类的cid属性上,这样才能将pid数据转给子分类对应的Category类中,
			//原因是Category类中并没有对应的pid属性,而是有parent属性;
			parent.setCid(pid);
			category.setParent(parent);//最后将设置好的父分类绑定给子分类,这样子分类就携带了完整的数值;
		}
		return category;
	
	}
	
	/**
	 * 将多个Map(List<Map>)映射成多个Category(List<Category>);
	 * @param mapList
	 * @return
	 */
	private List<Category> toCategoryList(List<Map<String,Object>> mapList){
		List<Category> categoryList = new ArrayList<Category>();
		for(Map<String,Object> map:mapList){//循环遍历所有的map;
			Category c = toCategory(map);//对所有的map执行转换成Category的方法;
			categoryList.add(c);//将所有转化好的Category添加进List中;
		}
		return categoryList;
		
	}
	
	/**
	 * 通过父分类的pid查询子分类;
	 * @param pid
	 * @return
	 * @throws SQLException 
	 */
	public List<Category> findByParent(String pid) throws SQLException{
		String sql = "select * from t_category where pid=? order by orderBy";
		List<Map<String,Object>> mapList = qr.query(sql, new MapListHandler(), pid);
		return toCategoryList(mapList);
		
	}
	
	/**
	 * 查询所有分类的方法的主体;
	 * @return
	 * @throws SQLException
	 */
	public List<Category> findAll() throws SQLException{
		//想要查询所有一级分类的数据,只需要查询父分类id(pid)为null的就可以;如果pid不是null说明不是一级分类;
		String sql = "select * from t_category where pid is null order by orderBy";
		List<Map<String,Object>> mapList = qr.query(sql, new MapListHandler());//因为查询到的数据每行数值不是与Category一一对应,所以不能用BeanHandler;需要自定义转换方法;
		List<Category> parents = toCategoryList(mapList);
		
		//循环遍历所有的父分类,然后通过pid cid加载它的子分类到自己的属性中;
		for(Category parent : parents){
			List<Category> children = findByParent(parent.getCid());//通过父分类的cid查询到子分类;
			parent.setChildren(children);//将子分类绑定给父分类;
		}
		return parents;
	}
	
	//************第七天新增代码块	开始********************
	
	/**
	 * 添加一级分类和二级分类;
	 * @param category
	 * @throws SQLException
	 */
	public void add(Category category) throws SQLException{
		//注意,表中的desc是一个关键字,必须用``符号包括;
		String sql = "insert into t_category(cid,cname,pid,`desc`) values(?,?,?,?)";
		String pid = null;
		if(null != category.getParent()){
			pid = category.getParent().getCid();
		}
		Object[] params = {
				category.getCid(),category.getCname(),pid,category.getDesc()
		};
		qr.update(sql,params);
	}
	
	/**
	 * 查询所有父分类但是不带上子分类;
	 * @return
	 * @throws SQLException
	 */
	public List<Category> findParents() throws SQLException{
		//想要查询所有一级分类的数据,只需要查询父分类id(pid)为null的就可以;如果pid不是null说明不是一级分类;
		String sql = "select * from t_category where pid is null order by orderBy";
		List<Map<String,Object>> mapList = qr.query(sql, new MapListHandler());//因为查询到的数据每行数值不是与Category一一对应,所以不能用BeanHandler;需要自定义转换方法;
		List<Category> parents = toCategoryList(mapList);
		return parents;
	}
	
	/**
	 * 加载分类,可加载一级分类也可加载二级分类;
	 * @param cid
	 * @return
	 * @throws SQLException
	 */
	public Category load(String cid) throws SQLException{
		String sql = "select * from t_category where cid=?";
		Map<String,Object> map = qr.query(sql, new MapHandler(),cid);
		return toCategory(map);
	}
	
	/**
	 * 修改分类;
	 * @param category
	 * @throws SQLException
	 */
	public void edit(Category category) throws SQLException{
		String sql = "update t_category set cname=?, pid=?, `desc`=? where cid=?";
		String pid = null;
		if(null != category.getParent()){
			pid = category.getParent().getCid();
		}
		Object[] params = {
				category.getCname(), pid, category.getDesc(), category.getCid()
		};
		qr.update(sql, params);
	}
	
	/**
	 * 查询指定父分类下子分类的个数;
	 * @param pid
	 * @return
	 * @throws SQLException
	 */
	public int findChildrenCountByParent(String pid) throws SQLException{
		String sql = "select count(*) from t_category where pid=?";
		Number cnt = (Number)qr.query(sql, new ScalarHandler(),pid);
		return cnt == null ? 0 : cnt.intValue();
	}
	/**
	 * 删除分类;
	 * @param cid
	 * @throws SQLException
	 */
	public void delete(String cid) throws SQLException{
		String sql = "delete from t_category where cid=?";
		qr.update(sql,cid);
	}
	
	//************第七天新增代码块	结束********************
}
