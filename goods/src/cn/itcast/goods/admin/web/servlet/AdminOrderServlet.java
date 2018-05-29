package cn.itcast.goods.admin.web.servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.goods.book.Page.PageBean;
import cn.itcast.goods.order.domain.Order;
import cn.itcast.goods.order.service.OrderService;
import cn.itcast.goods.user.domain.User;
import cn.itcast.servlet.BaseServlet;

public class AdminOrderServlet extends BaseServlet {
	private OrderService orderService = new OrderService();


	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	}
	
	/**
	 * 查询所有订单;
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String findAllOrder(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		int pc = getPc(req);
		String url = getUrl(req);
		PageBean<Order> pb = orderService.findAllOrder( pc);
		pb.setUrl(url);
		req.setAttribute("pb", pb);
		return "f:/adminjsps/admin/order/list.jsp";
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
	 * 根据状态查询订单;
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String findByStatus(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		int pc = getPc(req);
		String url = getUrl(req);
		int status = Integer.parseInt(req.getParameter("status")) ;
		PageBean<Order> pb = orderService.findByStatus(status, pc);
		pb.setUrl(url);
		req.setAttribute("pb", pb);
		return "f:/adminjsps/admin/order/list.jsp";
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
		return "/adminjsps/admin/order/desc.jsp";
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
			return "f:/adminjsps/msg.jsp";
		}
		orderService.updateStatus(5, oid);
		req.setAttribute("code", "success");
		req.setAttribute("msg", "订单已成功取消!");
		return "f:/adminjsps/msg.jsp";
	}
	
	
	/**
	 * 发货功能;
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 * @throws SQLException
	 */
	public String deliver(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, SQLException {
		String oid = req.getParameter("oid");
		int status = orderService.findStatus(oid);
		if(status != 2){
			req.setAttribute("code", "error");
			req.setAttribute("msg", "订单状态错误,请先检查订单再发货!");
			return "f:/adminjsps/msg.jsp";
		}
		orderService.updateStatus(3, oid);
		req.setAttribute("code", "succes");
		req.setAttribute("msg", "订单发货成功!");
		return "f:/adminjsps/msg.jsp";
	}

}
