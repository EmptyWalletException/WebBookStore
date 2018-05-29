package cn.itcast.goods.cart.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.handlers.MapHandler;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.cart.dao.CartItemDao;
import cn.itcast.goods.cart.domain.CartItem;

public class CartItemService {
		private CartItemDao cartItemDao = new CartItemDao();
		
		/**
		 * 我的购物车详情功能;
		 * @param uid
		 * @return
		 */
		public List<CartItem> myCart(String uid){
			try {
				return cartItemDao.findByUser(uid);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
		
		/**
		 * 往数据库中添加新的购物车条目,注意先判断数据库是否已存在相同的购物车条目;
		 * @param cartItem
		 */
		public void add(CartItem cartItem){
			try {
				String uid = cartItem.getUser().getUid();
				String bid = cartItem.getBook().getBid();
				CartItem oldCartItem = cartItemDao.findByUidAndBid(uid,bid);
				if(null == oldCartItem){//如果购物车已存在相同的商品条目;
					cartItem.setCartItemId(CommonUtils.uuid());//为新的商品条目设置一个永不重复的值做Id;
					cartItemDao.addCartItem(cartItem);//然后添加进数据库中;
				}else{
					int quantity = cartItem.getQuantity() + oldCartItem.getQuantity();//将旧的商品条目数量进行运算;
					cartItemDao.updateQuantity(oldCartItem.getCartItemId(), quantity);//获取旧的商品条目的Id,在数据库中更新此Id所对应的商品条目数据;
				}
			} catch (SQLException e) {
			}
		}
		
		/**
		 * 批量删除购物车里的商品条目;
		 * @param cartItemIds
		 */
		public void batchDelete(String cartItemIds){
			try {
				cartItemDao.batchDelete(cartItemIds);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
		
		/**
		 * 修改购物车中的单类商品数量;
		 * @param cartItemId
		 * @param quantity
		 * @return
		 * @throws SQLException
		 */
		public CartItem updateQuantity(String cartItemId,int quantity) throws SQLException{
			try {
				cartItemDao.updateQuantity(cartItemId, quantity);
				return cartItemDao.findByCartItemId(cartItemId);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		//************************** 第六天新增代码块 开始**************************
		/**
		 * 用于在提交订单页面加载多个商品条目;
		 * @param cartItemIds
		 * @return
		 */
		public List<CartItem> loadCartItems(String cartItemIds){
			try {
				return cartItemDao.loadCartItems(cartItemIds);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
		//************************** 第六天新增代码块 结束**************************
}
