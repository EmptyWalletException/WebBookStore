package cn.itcast.goods.order.web.servlet;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.book.Page.PageBean;
import cn.itcast.goods.cart.domain.CartItem;
import cn.itcast.goods.cart.service.CartItemService;
import cn.itcast.goods.order.domain.Order;
import cn.itcast.goods.order.domain.OrderItem;
import cn.itcast.goods.order.service.OrderService;
import cn.itcast.goods.user.domain.User;
import cn.itcast.servlet.BaseServlet;

public class OrderServlet extends BaseServlet {
	private OrderService orderService = new OrderService();
	private CartItemService cartItemService = new CartItemService();
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	}
	
	/**
	 * 从页面发送的request中获取pc值,如果没有,返回默认的1;
	 * @param req
	 * @return
	 */
	private int getPc(HttpServletRequest req){
		int pc = 1;
		String param = req.getParameter("pc");
		if(null != param && !param.trim().isEmpty()){
			try {
				pc = Integer.parseInt(param);
			} catch (RuntimeException e) {
			}
		}
		return pc;
	}
	
	/**
	 * 截取出url,用来做页面中的分页导航中的超链接;
	 * @param req
	 * @return
	 */
	private String getUrl(HttpServletRequest req){
		/**
		 * req.getRequestURI()→/goods/BookServlet;
		 * req.getQueryString()→method=finBYCateGory&cid=xxx&pc=8;
		 * 最终执行完方法后返回的url = /goods/BookServlet?method=finBYCateGory&cid=xxx;
		 * 
		 */
		String url = req.getRequestURI() + "?" + req.getQueryString();
		int index = url.lastIndexOf("&pc=");
		if(-1 != index){
			url = url.substring(0,index);
		}
		return url;
	}
	
	/**
	 * 按分类模糊查询;直接通过修改BookServlet里的方法而来;
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String myOrders(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		int pc = getPc(req);
		String url = getUrl(req);
		User user = (User) req.getSession().getAttribute("sessionUser");
		PageBean<Order> pb = orderService.myOrders(user.getUid(), pc);
		pb.setUrl(url);
		req.setAttribute("pb", pb);
		return "f:/jsps/order/list.jsp";
	}
	
	
	public String createOrder(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		//获得购物车所有条目并查询;
		String cartItemIds = req.getParameter("cartItemIds");
		List<CartItem> cartItemList = cartItemService .loadCartItems(cartItemIds);
		
		//创建Order;
		Order order = new Order();
		order.setOid(CommonUtils.uuid());
		order.setOrdertime(String.format("%tF %<tT", new Date()));//下单时间;
		order.setstatus(1);//设置订单状态;
		order.setAddress(req.getParameter("address"));
		User owner = (User) req.getSession().getAttribute("sessionUser");
		order.setOwner(owner);
		
		BigDecimal total = new BigDecimal("0");
		for(CartItem cartItem : cartItemList){
			total = total.add(new BigDecimal(cartItem.getSubtotal() + ""));
		}
		order.setTotal(total.doubleValue());//设置总计;
		
		//创建List<OrderItem>;一个CartItem对应一个OrderItem;
		List<OrderItem> orderItemList = new ArrayList<OrderItem>();
		for(CartItem cartItem : cartItemList){
			OrderItem orderItem = new OrderItem();
			orderItem.setOrderItemId(CommonUtils.uuid());
			orderItem.setQuantity(cartItem.getQuantity());
			orderItem.setSubtotal(cartItem.getSubtotal());
			orderItem.setBook(cartItem.getBook());
			orderItem.setOrder(order);
			orderItemList.add(orderItem);
		}
		order.setOrderItemList(orderItemList);//设置关联;
		
		//调用OrderService完成添加;
		orderService.createOrder(order);
		//当购物车的商品提交订单后应该被移除;
		cartItemService.batchDelete(cartItemIds);
		//保存并转发;
		req.setAttribute("order", order);
		return "f:/jsps/order/ordersucc.jsp";
	}
	
	/**
	 * 加载订单详情;
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 * @throws SQLException
	 */
	public String load(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		String oid = req.getParameter("oid");
		Order order = orderService.load(oid);
		req.setAttribute("order", order);
		String btn = req.getParameter("btn");//btn用来区分用户是点击了页面上哪个按钮;
		req.setAttribute("btn", btn);
		return "/jsps/order/desc.jsp";
	}
	
	/**
	 * 取消订单;
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 * @throws SQLException
	 */
	public String cancel(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		String oid = req.getParameter("oid");
		int status = orderService.findStatus(oid);
		if(status != 1){
			req.setAttribute("code", "error");
			req.setAttribute("msg", "订单状态错误,无法取消!");
			return "f:/jsps/msg.jsp";
		}
		orderService.updateStatus(5, oid);
		req.setAttribute("code", "success");
		req.setAttribute("msg", "您的订单已成功取消,欢迎您继续选购其它商品!");
		return "f:/jsps/msg.jsp";
	}
	
	/**
	 * 确认订单;
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 * @throws SQLException
	 */
	public String confirm(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		String oid = req.getParameter("oid");
		int status = orderService.findStatus(oid);
		if(status != 3){
			req.setAttribute("code", "error");
			req.setAttribute("msg", "订单状态错误,无法确认收货!");
			return "f:/jsps/msg.jsp";
		}
		orderService.updateStatus(4, oid);
		req.setAttribute("code", "success");
		req.setAttribute("msg", "您的订单已确认收货,欢迎您下次光临!");
		return "f:/jsps/msg.jsp";
	}
	
	/**
	 * 支付准备功能;
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 * @throws SQLException
	 */
	public String paymentPre(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		req.setAttribute("order", orderService.load(req.getParameter("oid")));
		return "f:/jsps/order/pay.jsp";
	}
	
	public String payment(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		/**
		 * 此处在线支付由于易宝账号原因暂时无法实现;
		 */
		return "f:/jsps/order/pay.jsp";
	}
	
	
}
