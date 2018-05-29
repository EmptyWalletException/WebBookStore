package cn.itcast.goods.admin.book.web.servlet;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.book.Page.PageBean;
import cn.itcast.goods.book.domain.Book;
import cn.itcast.goods.book.service.BookService;
import cn.itcast.goods.category.domain.Category;
import cn.itcast.goods.category.service.CategoryService;
import cn.itcast.servlet.BaseServlet;

public class AdminBookServlet extends BaseServlet {
	private BookService bookService = new BookService();
	private CategoryService categoryService = new CategoryService();
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	}
	
	/**
	 * 查询所有分类的方法;直接从CategoryServlet里复制过来;
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String findCategoryAll(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		List<Category> parents = categoryService.findAll();
		req.setAttribute("parents", parents);
		return "f:/anminjsps/admin/book/left.jsp";
	}
	
	//*************从前台BookServlet处copy的代码块,加以修改	开始***************
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
		return "f:/adminjsps/admin/book/list.jsp";
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
		return "f:/adminjsps/admin/book/list.jsp";
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
		return "f:/adminjsps/admin/book/list.jsp";
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
		return "f:/adminjsps/admin/book/list.jsp";
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
		return "f:/adminjsps/admin/book/list.jsp";
	}
	

	//*************从前台BookServlet处copy的代码块,加以修改	结束***************
	
	/**
	 * 添加图书的第一步;
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String addPre(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		List<Category> parents = categoryService.findParents();
		req.setAttribute("parents", parents);
		return "f:/anminjsps/admin/book/add.jsp";
	}
	
	/**
	 * ajax异步查询父分类的子分类;
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String ajaxFindChildren(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String pid = req.getParameter("pid");
		//通过pid查出所有的二级分类;
		List<Category> children = categoryService.findChildren(pid);
		String json = toJson(children);
		resp.getWriter().print(json);
		return null;
	}
	
	//下面是两个将对象和对象List转换为json的方法;
	//json参考样式 {"cid":"string","cname":"string"}
	private String toJson(Category category){
		StringBuilder sb = new StringBuilder("{");
		sb.append("\"cid\"").append(":").append("\"").append(category.getCid()).append("\"");
		sb.append(",");
		sb.append("\"cname\"").append(":").append("\"").append(category.getCname()).append("\"");
		sb.append("}");
		System.out.println(sb.toString());
		return sb.toString();
	}
	
	//json参考样式 [{"cid":"string","cname":"string"},{"cid":"string","cname":"string"}]
	private String toJson(List<Category> categoryList){
		StringBuilder sb = new StringBuilder("[");
		for(int i = 0 ; i < categoryList.size(); i++){
			sb.append(toJson(categoryList.get(i)));
			if(i < categoryList.size() - 1){
				sb.append(",");
			}
		}
		sb.append("]");
		System.out.println(sb.toString());
		return sb.toString();
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
		
		req.setAttribute("parents",	categoryService.findParents());
		String pid = book.getCategory().getParent().getCid();
		req.setAttribute("children", categoryService.findChildren(pid));
		return "f:/adminjsps/admin/book/desc.jsp";
	}
	
	/**
	 * 修改图书;
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String edit(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		Map map = req.getParameterMap();
		Book book = CommonUtils.toBean(map, Book.class);
		Category category = CommonUtils.toBean(map, Category.class);
		book.setCategory(category);
		bookService.edit(book);
		req.setAttribute("msg", "修改图书成功!");
		return "f:/adminjsps/msg.jsp";
	}
	
	/**
	 * 删除图书;
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String delete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String bid = req.getParameter("bid");
		Book book = bookService.load("bid");
		//删除文件夹里的图书封面图片;
		String savepath = this.getServletContext().getRealPath("/");
		new File(savepath, book.getImage_w()).delete();
		new File(savepath, book.getImage_b()).delete();
		//删除数据库里的图书;
		bookService.delete(bid);
		req.setAttribute("msg", "删除图书成功!");
		return "f:/adminjsps/msg.jsp";
	}
	
}
