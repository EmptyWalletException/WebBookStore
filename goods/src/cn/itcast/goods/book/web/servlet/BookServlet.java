package cn.itcast.goods.book.web.servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.book.Page.PageBean;
import cn.itcast.goods.book.domain.Book;
import cn.itcast.goods.book.service.BookService;
import cn.itcast.goods.user.service.UserService;
import cn.itcast.servlet.BaseServlet;

public class BookServlet extends BaseServlet {
	private BookService bookService = new BookService();
	
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
	 * 按分类模糊查询;
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String findByCategory(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		    1.获取pc参数,如果没有,则默认为1;
			2.获取其它参数,连同pc参数一起访问BookService里的查询方法,得到查询出来的PageBean;
			3.BookServlet需要获取url,设置到PageBean对象中;
			4.保存PageBean,转发到list.jsp;
		*/
		int pc = getPc(req);
		String url = getUrl(req);
		String cid = req.getParameter("cid");
		PageBean<Book> pb = bookService.findByCategory(cid, pc);
		pb.setUrl(url);
		req.setAttribute("pb", pb);
		return "f:/jsps/book/list.jsp";
	}
	
	
	
	/**
	 * 按作者模糊查询;findByAuthor
	 * @param author
	 * @param pc
	 * @return
	 */
	public String findByAuthor(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		    1.获取pc参数,如果没有,则默认为1;
			2.获取其它参数,连同pc参数一起访问BookService里的查询方法,得到查询出来的PageBean;
			3.BookServlet需要获取url,设置到PageBean对象中;
			4.保存PageBean,转发到list.jsp;
		*/
		int pc = getPc(req);
		String url = getUrl(req);
		String author = req.getParameter("author");
		PageBean<Book> pb = bookService.findByAuthor(author, pc);
		pb.setUrl(url);
		req.setAttribute("pb", pb);
		return "f:/jsps/book/list.jsp";
	}
	
	/**
	 * 按出版社模糊查询;findByPress
	 * @param press
	 * @param pc
	 * @return
	 */
	public String findByPress(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		    1.获取pc参数,如果没有,则默认为1;
			2.获取其它参数,连同pc参数一起访问BookService里的查询方法,得到查询出来的PageBean;
			3.BookServlet需要获取url,设置到PageBean对象中;
			4.保存PageBean,转发到list.jsp;
		*/
		int pc = getPc(req);
		String url = getUrl(req);
		String press = req.getParameter("press");
		PageBean<Book> pb = bookService.findByPress(press, pc);
		pb.setUrl(url);
		req.setAttribute("pb", pb);
		return "f:/jsps/book/list.jsp";
	}
	
	/**
	 * 按书名模糊查询;findByBname
	 * @param bname
	 * @param pc
	 * @return
	 */
	public String findByBname(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		    1.获取pc参数,如果没有,则默认为1;
			2.获取其它参数,连同pc参数一起访问BookService里的查询方法,得到查询出来的PageBean;
			3.BookServlet需要获取url,设置到PageBean对象中;
			4.保存PageBean,转发到list.jsp;
		*/
		int pc = getPc(req);
		String url = getUrl(req);
		String bname = req.getParameter("bname");
		PageBean<Book> pb = bookService.findByBname(bname, pc);
		pb.setUrl(url);
		req.setAttribute("pb", pb);
		return "f:/jsps/book/list.jsp";
	}
	
	/**
	 * 多条件组合查询;findByCombination
	 * @param criteria
	 * @param pc
	 * @return
	 */
	public String findByCombination(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		    1.获取pc参数,如果没有,则默认为1;
			2.获取其它参数,连同pc参数一起访问BookService里的查询方法,得到查询出来的PageBean;
			3.BookServlet需要获取url,设置到PageBean对象中;
			4.保存PageBean,转发到list.jsp;
		*/
		int pc = getPc(req);
		String url = getUrl(req);
		Book book = CommonUtils.toBean(req.getParameterMap(), Book.class);
		PageBean<Book> pb = bookService.findByCombination(book, pc);
		pb.setUrl(url);
		req.setAttribute("pb", pb);
		return "f:/jsps/book/list.jsp";
	}
	
	/**
	 * 加载图书详情;
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String load(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String bid = req.getParameter("bid");
		Book book = bookService.load(bid);
		req.setAttribute("book", book);
		return "f:/jsps/book/desc.jsp";
	}
	 
}
