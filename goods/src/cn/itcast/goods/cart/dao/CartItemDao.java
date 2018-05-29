package cn.itcast.goods.cart.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.book.domain.Book;
import cn.itcast.goods.cart.domain.CartItem;
import cn.itcast.goods.user.domain.User;
import cn.itcast.jdbc.TxQueryRunner;

public class CartItemDao {
	private QueryRunner qr = new TxQueryRunner();
	
	/**
	 * 通过用户uid查询购物车里的条目;
	 * @param uid
	 * @return
	 * @throws SQLException
	 */
	public List<CartItem> findByUser(String uid) throws SQLException{
		String sql = "select * from t_cartitem c, t_book b where c.bid=b.bid and uid=? order by c.orderBy";
		List<Map<String,Object>> mapList = qr.query(sql, new MapListHandler(), uid);
		return toCartItemList(mapList);
	}
	
	/**
	 * 将查询出来的MapList集合封装到对应的三个类中,建立一个完整的关联关系;
	 * @param map
	 * @return
	 */
	private CartItem toCartItem(Map<String,Object> map){
		if(null == map || map.size() == 0) return null;
		CartItem cartItem = CommonUtils.toBean(map, CartItem.class);
		Book book = CommonUtils.toBean(map, Book.class);
		User user = CommonUtils.toBean(map, User.class);
		cartItem.setBook(book);
		cartItem.setUser(user);
		return cartItem;
	}
	
	/**
	 * 将多个Map(List<Map>)映射成多个CartItem(List<CartItem>);
	 * @param mapList
	 * @return
	 */
	private List<CartItem> toCartItemList(List<Map<String,Object>> mapList){
		List<CartItem> cartItemList = new ArrayList<CartItem>();
		for(Map<String,Object> map : mapList){
			CartItem cartItem = toCartItem(map);
			cartItemList.add(cartItem);
		}
		return cartItemList;
	}
	
	/**
	 * 通过uid和bid查询购物车数据表中是否已经存在相同商品,用于判断是否是在已有的商品上增加数量;
	 * @param uid
	 * @param bid
	 * @return
	 * @throws SQLException
	 */
	public CartItem findByUidAndBid(String uid,String bid) throws SQLException{
		String sql = "select * from t_cartitem where uid=? and bid=?";
		Map<String,Object> map = qr.query(sql, new MapHandler(), uid,bid);
		CartItem cartItem = toCartItem(map);
		return cartItem;
	}
	
	/**
	 * 修改购物车数据表中对应商品的数量;
	 * @param cartItemId
	 * @param quantity
	 * @throws SQLException
	 */
	public void updateQuantity(String cartItemId,int quantity) throws SQLException{
		String sql = "update t_cartitem set quantity=? where cartItemId=?";
		qr.update(sql, quantity,cartItemId);
	}
	
	/**
	 * 往数据库中添加购物车的条目;
	 * @param cartItem
	 * @throws SQLException
	 */
	public void addCartItem(CartItem cartItem) throws SQLException{
		String sql = "insert into t_cartitem(cartItemId, quantity, bid, uid)"+" values(?,?,?,?)";
		Object[] params = {
				cartItem.getCartItemId(),
				cartItem.getQuantity(),
				cartItem.getBook().getBid(),
				cartItem.getUser().getUid()
		};
		qr.update(sql, params);
	}
	
	/**
	 * 购物车批量删除功能;
	 * @param cartItemIds
	 * @throws SQLException 
	 */
	public void batchDelete(String cartItemIds) throws SQLException{
		Object[] cartItemIdArray = cartItemIds.split(",");
		String whereSql = toWhereSql(cartItemIdArray.length);
		String sql = "delete from t_cartitem where "+ whereSql;
		qr.update(sql, cartItemIdArray);// 	警  告	 :cartItemIdArray必须是Object类型!
	}
	
	private String toWhereSql(int len){
		StringBuilder sb = new StringBuilder("cartItemId in(");
		for(int i = 0; i< len; i++){
			sb.append("?");
			if(i<len - 1){
				sb.append(",");
			}
		}
		sb.append(")");//假设len=3,则执行到此行之后会sb变成"(?,?,?)"
		return sb.toString();
	}
	
	
	//************************** 第六天新增代码块 开始**************************
	/**
	 * 按购物车商品条目Id查询;
	 * @param cartItemId
	 * @return
	 * @throws SQLException
	 */
	public CartItem findByCartItemId(String cartItemId) throws SQLException{
		String sql = "select * from t_cartItem c, t_book b where c.bid=b.bid and c.cartItemId=?";
		Map<String,Object> map = qr.query(sql, new MapHandler(),cartItemId);
		return toCartItem(map);
	}
	
	/**
	 * 用于在提交订单页面加载多个购物车商品条目;
	 * @param cartItemIds
	 * @return
	 * @throws SQLException
	 */
	public List<CartItem> loadCartItems(String cartItemIds) throws SQLException{
		Object[] cartItemIdArray = cartItemIds.split(",");
		String whereSql = toWhereSql(cartItemIdArray.length);
		String sql = "select * from t_cartitem c, t_book b where c.bid=b.bid and " + whereSql;
		List<Map<String, Object>> listMap = qr.query(sql, new MapListHandler(), cartItemIdArray);
		return toCartItemList(listMap);
	}
	//************************** 第六天新增代码块 结束**************************
}
