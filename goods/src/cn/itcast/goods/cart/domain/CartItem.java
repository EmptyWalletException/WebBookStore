package cn.itcast.goods.cart.domain;

import java.math.BigDecimal;

import cn.itcast.goods.book.domain.Book;
import cn.itcast.goods.user.domain.User;

public class CartItem {
	private String cartItemId;//主键; 
	private int quantity;//数量;
	private Book book;//对应的书;
	private User user;//所属用户;
	
	public double getSubtotal(){
		//使用BigDecimal数据类型防止出现循环小数被截断导致的精度误差问题;
		//bigDecimal.doubleValue()会自动四舍五入;
		BigDecimal b1 = new BigDecimal(book.getCurrPrice()+"");
		BigDecimal b2 = new BigDecimal(quantity+"");
		BigDecimal b3 =b1.multiply(b2);
		return b3.doubleValue();
	}
	//**********set get constructor 开始*****************
	public String getCartItemId() {
		return cartItemId;
	}
	public void setCartItemId(String cartItemId) {
		this.cartItemId = cartItemId;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public Book getBook() {
		return book;
	}
	public void setBook(Book book) {
		this.book = book;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}

	public CartItem() {
		super();
	}
	public CartItem(String cartItemId, int quantity, Book book, User user) {
		super();
		this.cartItemId = cartItemId;
		this.quantity = quantity;
		this.book = book;
		this.user = user;
	}
	//**********set get constructor 开始*****************
	
}
