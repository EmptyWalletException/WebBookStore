package cn.itcast.goods.order.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.book.Page.Expression;
import cn.itcast.goods.book.Page.PageBean;
import cn.itcast.goods.book.Page.PageConstants;
import cn.itcast.goods.book.domain.Book;
import cn.itcast.goods.order.domain.Order;
import cn.itcast.goods.order.domain.OrderItem;
import cn.itcast.jdbc.TxQueryRunner;

public class OrderDao {
	private QueryRunner qr = new TxQueryRunner();
	
	
/**
 * 通用的查询方法,直接从BookDao里的同名方法修改而来;	
 * @param exprList
 * @param pc
 * @return
 * @throws SQLException
 */
private PageBean<Order> findByCriteria(List<Expression> exprList,int pc) throws SQLException{
		
		/**
		 * 1.得到ps;tr;;beanList;
		 * 2.创建PageBean,并返回;
		 */
		
		int ps = PageConstants.ORDER_PAGE_SIZE;
		StringBuilder whereSql = new StringBuilder (" where 1=1");//where条件子句的头;
		List<Object> params = new ArrayList<Object>();
		//遍历exprList并调用里面的属性生成where条件子句的后面部分;
		for(Expression expr : exprList) {
			//参考例句: "where 1=1 and bid = "
			whereSql.append(" and ").append(expr.getName()).append(" ").append(expr.getOperator()).append(" ");
			if(!expr.getOperator().equals("is null")){//当where条件子句是"where 1=1 and bid is null "时是不需要参数占位符的;
				whereSql.append("?");//此时变成: "where 1=1 and bid = ?" ;
				params.add(expr.getValue());//此时qr.query(sql , handler ,params)就齐全了;//此处曾抛出一个异常,原因是MySQL中执行查询语句时需要格式为 cid = "cid的值", cid是char类型,有可能需要添加引号;
			}
		}
		
		String sql = "select count(*) from t_order" + whereSql;
		Number number = (Number) qr.query(sql, new ScalarHandler(), params.toArray());
		int tr = number.intValue();//此时得到tr;
		
		//开始执行分页查询,pc就是客户端客户点击的页数,
		
		sql = "select * from t_order" + whereSql + " order by ordertime desc limit ?,?";
		params.add((pc-1) * ps);
		params.add(ps);
		
		List<Order> beanList = qr.query(sql, new BeanListHandler<Order>(Order.class), params.toArray());
		//此时还需要给每个订单中加载订单条目;
		for(Order order : beanList){
			loadOrderItem(order);
		}
		
		//将所有执行完的数据打包后返回;
		PageBean<Order> pb = new PageBean<Order>();
		pb.setBeanList(beanList);
		pb.setPc(pc);
		pb.setPs(ps);
		pb.setTr(tr);
		
		return pb;
	}

	/**
	 * 为指定的order加载它的所有订单条目;
	 * @param order
	 * @throws SQLException 
	 */
	private void loadOrderItem(Order order) throws SQLException {
		String sql = "select * from t_orderitem where oid=?";
		List<Map<String,Object>> mapList = qr.query(sql, new MapListHandler(),order.getOid());
		List<OrderItem> orderItemList = toOrderItemList(mapList);
		order.setOrderItemList(orderItemList);
	}
	
	/**
	 * 将多个Map转换成多个OrderItem JavaBean对象;
	 * @param mapList
	 * @return
	 */
	private List<OrderItem> toOrderItemList(List<Map<String, Object>> mapList) {
		List<OrderItem> orderItemList = new ArrayList<OrderItem>();
		for(Map<String,Object> map: mapList){
			OrderItem orderItem = tpOrderItem(map);
			orderItemList.add(orderItem);
		}
		return orderItemList;
	}
	
	/**
	 * 将一个Map转换成一个OrderItem;
	 * @param map
	 * @return
	 */
	private OrderItem tpOrderItem(Map<String, Object> map) {
		OrderItem orderItem = CommonUtils.toBean(map, OrderItem.class);
		Book book = CommonUtils.toBean(map, Book.class);
		orderItem.setBook(book);
		return orderItem;
	}

	/**
	 * 通过用户查询订单;直接从BookDao里的cid查询方法修改而来;
	 * @param cid
	 * @param pc
	 * @return
	 * @throws SQLException
	 */
	public PageBean<Order> findByUser(String uid , int pc) throws SQLException{
		List<Expression> exprList = new ArrayList<Expression>();
		exprList.add(new Expression("uid", "=" , uid));
		return findByCriteria(exprList, pc);
	}
	
	/**
	 * 生成订单;
	 * @param order
	 * @throws SQLException 
	 */
	public void add(Order order) throws SQLException{
		String sql = "insert into t_order values(?,?,?,?,?,?)";
		Object[] params = {
				order.getOid(),order.getOrdertime(),order.getTotal(),order.getstatus(),order.getAddress(),order.getOwner().getUid()
		};
		qr.update(sql, params);
		
		sql = "insert into t_orderitem values(?,?,?,?,?,?,?,?)";
		int len = order.getOrderItemList().size();
		Object[][] objs = new Object[len][];
		for(int i = 0 ; i < len ; i++){
			OrderItem item = order.getOrderItemList().get(i);
			objs[i] = new Object[]{
					item.getOrderItemId(),item.getQuantity(),item.getSubtotal(),item.getBook().getBid(),
					item.getBook().getBname(),item.getBook().getCurrPrice(),item.getBook().getImage_b(),
					order.getOid()
			};
		}
		qr.batch(sql, objs);//执行批处理;
	}
	
	/**
	 * 加载订单详情;
	 * @param oid
	 * @return
	 * @throws SQLException
	 */
	public Order load(String oid) throws SQLException{
		String sql = "select * from t_order where oid=?";
		Order order = qr.query(sql, new BeanHandler<Order>(Order.class), oid);
		loadOrderItem(order);//为当前订单加载它的所有订单条目;
		return order;
	}
	
	/**
	 * 查询订单状态;
	 * @param oid
	 * @return
	 * @throws SQLException
	 */
	public int findStatus(String oid) throws SQLException{
		String sql = "select status from t_order where oid=?";
		Number number = (Number) qr.query(sql, new ScalarHandler(),oid);
		return number.intValue();
	}
	
	/**
	 * 修改订单状态;
	 * @param oid
	 * @return
	 * @throws SQLException
	 */
	public void updateStatus(int status,String oid) throws SQLException{
		String sql = "update t_order set status=? where oid=?";
		qr.update(sql, status,oid);
	}
	
	//****************第九天新增代码块	开始*****************
	/**
	 * 查询所有订单订单;
	 * @param pc
	 * @return
	 * @throws SQLException
	 */
	public PageBean<Order> findAllOrder( int pc) throws SQLException{
		List<Expression> exprList = new ArrayList<Expression>();
		return findByCriteria(exprList, pc);
	}
	
	/**
	 * 通过状态查询订单;
	 * @param status
	 * @param pc
	 * @return
	 * @throws SQLException
	 */
	public PageBean<Order> findByStatus(int status , int pc) throws SQLException{
		List<Expression> exprList = new ArrayList<Expression>();
		exprList.add(new Expression("status", "=" , status+""));//此处由于status是int类型,必须转成字符串才能当参数;
		return findByCriteria(exprList, pc);
	}
	
	//****************第九天新增代码块	结束*****************
	
}
