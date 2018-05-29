package cn.itcast.goods.cart.web.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.book.domain.Book;
import cn.itcast.goods.cart.domain.CartItem;
import cn.itcast.goods.cart.service.CartItemService;
import cn.itcast.goods.user.domain.User;
import cn.itcast.servlet.BaseServlet;

public class CartItemServlet extends BaseServlet {
	private CartItemService cartItemService = new CartItemService();
	
	/**
	 * 我的购物车详情;
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String myCart(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/**
		 * 1.得到uid;
		 * 2.传入uid调用CartItemService里的方法得到购物车条目;
		 * 3.保存到req中;
		 * 4.转发到jsps/cart下/list.jsp页面;
		 */
		User user = (User) req.getSession().getAttribute("sessionUser");
		String uid = user.getUid();
		List<CartItem> cartItem = cartItemService.myCart(uid);
		req.setAttribute("cartItemList", cartItem);
		return "f:/jsps/cart/list.jsp";
	}
	
	/**
	 * 向购物车添加商品;
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String add(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 从表单中取出用户选择的商品和用户的id,各自封装到对应的javaBean中,然后建立关联并添加进购物车;
		 * 最后调用显示购物车详情的方法:myCart(req,resp);
		 * */
		Map map = req.getParameterMap();
		CartItem cartItem = CommonUtils.toBean(map, CartItem.class);
		Book book = CommonUtils.toBean(map, Book.class);
		User user = (User) req.getSession().getAttribute("sessionUser");
		cartItem.setBook(book);
		cartItem.setUser(user);
		
		cartItemService.add(cartItem);
		
		return myCart(req,resp);
		
	}
	
	/**
	 * 批量删除购物车里的商品条目;
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String batchDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String cartItemIds = req.getParameter("cartItemIds");
		cartItemService.batchDelete(cartItemIds);
		return myCart(req,resp);
	}
	
	
	/**
	 * 将购物车页面上显示的商品数量与数据库中的购物车商品数量保持一致;
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 * @throws SQLException
	 */
	public String updateQuantity(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		String cartItemId = req.getParameter("cartItemId");
		int quantity = Integer.parseInt(req.getParameter("quantity"));
		CartItem cartItem = cartItemService.updateQuantity(cartItemId, quantity);
		StringBuilder sb = new StringBuilder("{");
		sb.append("\"quantity\"").append(":").append(cartItem.getQuantity());
		sb.append(",");
		sb.append("\"subtotal\"").append(":").append(cartItem.getSubtotal());
		sb.append("}");
		
		resp.getWriter().print(sb);
		return null;
	}
	
	//************************** 第六天新增代码块 开始**************************
	/**
	 * 在购物车页面点击结算按钮后的提交订单页面显示购物车商品详情;
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 * @throws SQLException
	 */
	public String loadCartItems(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		//获取cartItemIds参数;
		String cartItemIds = req.getParameter("cartItemIds");
		double total = Double.parseDouble(req.getParameter("total"));
		//在数据库中根据cartItemIds查询数据;
		List<CartItem> cartItemList = cartItemService.loadCartItems(cartItemIds);
		//保存到req中并转发;
		req.setAttribute("cartItemList", cartItemList);
		req.setAttribute("total", total);
		req.setAttribute("cartItemIds", cartItemIds);
		return "f:/jsps/cart/showitem.jsp";
	}
	//************************** 第六天新增代码块 结束**************************
	
}
