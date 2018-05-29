package cn.itcast.goods.order.service;

import java.sql.SQLException;

import cn.itcast.goods.book.Page.PageBean;
import cn.itcast.goods.order.dao.OrderDao;
import cn.itcast.goods.order.domain.Order;
import cn.itcast.jdbc.JdbcUtils;

public class OrderService {
	private OrderDao orderDao = new OrderDao();
	
	
	/**
	 * 通过用户查询订单,以事务的方式执行;
	 * @param uid
	 * @param pc
	 * @return
	 */
	public PageBean<Order> myOrders(String uid, int pc){
		PageBean<Order> pb;
		try {
			JdbcUtils.beginTransaction();
			pb = orderDao.findByUser(uid, pc);
			JdbcUtils.commitTransaction();
			return pb;
		} catch (SQLException e) {
			try {
				JdbcUtils.rollbackTransaction();
			} catch (SQLException e1) {}
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 生成订单的事务;
	 * @param order
	 */
	public void createOrder(Order order){
		PageBean<Order> pb;
		try {
			JdbcUtils.beginTransaction();
			orderDao.add(order);
			JdbcUtils.commitTransaction();
		} catch (SQLException e) {
			try {
				JdbcUtils.rollbackTransaction();
			} catch (SQLException e1) {}
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 加载订单详情;
	 * @param oid
	 * @return
	 * @throws SQLException
	 */
	public Order load(String oid) throws SQLException{
		
		return orderDao.load(oid);
	}
	
	/**
	 * 查询订单状态;
	 * @param oid
	 * @return
	 * @throws SQLException
	 */
	public int findStatus(String oid){
		
		try {
			return orderDao.findStatus(oid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 修改订单状态;
	 * @param oid
	 * @return
	 * @throws SQLException
	 */
	public void updateStatus(int status,String oid){
		try {
			 orderDao.updateStatus(status, oid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	//********************第九天新增代码块	开始*******************
	/**
	 * 查询所有订单,以事务的方式执行;
	 * @param uid
	 * @param pc
	 * @return
	 */
	public PageBean<Order> findAllOrder( int pc){
		PageBean<Order> pb;
		try {
			JdbcUtils.beginTransaction();
			pb = orderDao.findAllOrder( pc);
			JdbcUtils.commitTransaction();
			return pb;
		} catch (SQLException e) {
			try {
				JdbcUtils.rollbackTransaction();
			} catch (SQLException e1) {}
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 查询所有订单,以事务的方式执行;
	 * @param uid
	 * @param pc
	 * @return
	 */
	public PageBean<Order> findByStatus(int status, int pc){
		PageBean<Order> pb;
		try {
			JdbcUtils.beginTransaction();
			pb = orderDao.findByStatus(status, pc);
			JdbcUtils.commitTransaction();
			return pb;
		} catch (SQLException e) {
			try {
				JdbcUtils.rollbackTransaction();
			} catch (SQLException e1) {}
			throw new RuntimeException(e);
		}
	}
	//********************第九天新增代码块	结束*******************
	
	
}
