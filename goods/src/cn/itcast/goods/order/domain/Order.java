package cn.itcast.goods.order.domain;

import java.util.List;

import cn.itcast.goods.user.domain.User;

public class Order {
	private String oid;//主键
	private String ordertime;//下单时间;
	private double total;//总计;
	private int status;//订单状态:1.未付款,2.已付款未发货,3.已发货未确认收货,4.已确认收货交易成功,5.已取消订单;
	private String address;//收货地址;
	private User owner;//订单所属用户;
	private List<OrderItem> orderItemList;
	
	//************* set get constructor 开始************
	
	public String getOid() {
		return oid;
	}
	public Order(String oid, String ordertime, double total, int status,
			String address, User owner, List<OrderItem> orderItemList) {
		super();
		this.oid = oid;
		this.ordertime = ordertime;
		this.total = total;
		this.status = status;
		this.address = address;
		this.owner = owner;
		this.orderItemList = orderItemList;
	}
	public List<OrderItem> getOrderItemList() {
		return orderItemList;
	}
	public void setOrderItemList(List<OrderItem> orderItemList) {
		this.orderItemList = orderItemList;
	}
	public void setOid(String oid) {
		this.oid = oid;
	}
	public String getOrdertime() {
		return ordertime;
	}
	public void setOrdertime(String ordertime) {
		this.ordertime = ordertime;
	}
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
	public int getstatus() {
		return status;
	}
	public void setstatus(int status) {
		this.status = status;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public User getOwner() {
		return owner;
	}
	public void setOwner(User owner) {
		this.owner = owner;
	}
	
	public Order() {
		super();
	}
	
	//************* set get constructor 结束************
	
}
