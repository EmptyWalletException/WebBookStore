package cn.itcast.goods.admin.book.web.servlet;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.ImageIcon;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.book.domain.Book;
import cn.itcast.goods.book.service.BookService;
import cn.itcast.goods.category.domain.Category;
import cn.itcast.goods.category.service.CategoryService;



public class AdminAddBookServlet extends HttpServlet {

	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");
		resp.setContentType("text/html;charser=utf-8");
		//commons-fileupload上传三步的第一步:创建工具;
		FileItemFactory factory = new DiskFileItemFactory();
		//第二步,创建解析器对象;
		ServletFileUpload sfu = new ServletFileUpload(factory);
		sfu.setFileSizeMax(8000 * 1024);//设置单个上传文件的上限为8000KB;
		//第三步,解析req得到List<FileItem>,完成上传功能;
		List<FileItem> fileItemList= null;//提升作用域;
			try {
				fileItemList = sfu.parseRequest(req);
			} catch (FileUploadException e) {
				error("上传文件大小超出了8000KB", req, resp);
				return;
			}
		Map<String,Object> map = new HashMap<String,Object>();
		for(FileItem fileItem : fileItemList){
			if(fileItem.isFormField()){
				map.put(fileItem.getFieldName(), fileItem.getString("UTF-8"));
			}
		}
		Book book = CommonUtils.toBean(map, Book.class);
		Category category = CommonUtils.toBean(map, Category.class);
		
		//开始完成image_w大图的上传
		//获取文件名;
		FileItem fileItem = fileItemList.get(1);
		String filename = fileItem.getName();
		//截取文件名,及部分浏览器上传的绝对路径;
		int index = filename.lastIndexOf("\\");
		if(index != -1){
			filename = filename.substring(index + 1);
		}
		//给文件名添加uuid前缀,避免文件同名现象;
		filename = CommonUtils.uuid() + "_" + filename;
		//校验文件名称的扩展名;
		if(filename.toLowerCase().endsWith(".jpg")){
			error("必须上传.jpg为后缀的文件",req,resp);
			return;
		}
		//校验图片的尺寸;
		
		//保存上传的图片,把图片new成图片对象:Image,Icon,ImageIcon,BufferedImage,ImageIO
		//获取图片真实路径;
		String savepath = this.getServletContext().getRealPath("/book_img");
		File destFile = new File(savepath,filename);
		try {
			fileItem.write(destFile);//将临时文件重定向到指定的路径,然后在删除文件;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		//校验尺寸
		//1.使用文件路径创建ImageIcon;
		ImageIcon icon = new ImageIcon(destFile.getAbsolutePath());
		//2.通过ImageIcon得到Image;
		Image image = icon.getImage();//此处注意导入包时选swing和awt包;
		//3.获取宽高来进行校验;
		if(image.getWidth(null) > 350 || image.getHeight(null) > 350){
			error("上传的图片尺寸请控制在350X350之内!",req,resp);
			destFile.delete();//删除不符合要求的图片;
			return;
		}
		//把最后符合要求的图片路径设置给book;
		book.setImage_w("book_img/" + filename);
		
		
		//开始完成image_b小图的上传
		//获取文件名;
		 fileItem = fileItemList.get(2);
		  filename = fileItem.getName();
		//截取文件名,及部分浏览器上传的绝对路径;
		  index = filename.lastIndexOf("\\");
		if(index != -1){
			filename = filename.substring(index + 1);
		}
		//给文件名添加uuid前缀,避免文件同名现象;
		filename = CommonUtils.uuid() + "_" + filename;
		//校验文件名称的扩展名;
		if(filename.toLowerCase().endsWith(".jpg")){
			error("必须上传.jpg为后缀的文件",req,resp);
			return;
		}
		//校验图片的尺寸;
		
		//保存上传的图片,把图片new成图片对象:Image,Icon,ImageIcon,BufferedImage,ImageIO
		//获取图片真实路径;
		  savepath = this.getServletContext().getRealPath("/book_img");
		  destFile = new File(savepath,filename);
		try {
			fileItem.write(destFile);//将临时文件重定向到指定的路径,然后在删除文件;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		//校验尺寸
		//1.使用文件路径创建ImageIcon;
		  icon = new ImageIcon(destFile.getAbsolutePath());
		//2.通过ImageIcon得到Image;
		  image = icon.getImage();//此处注意导入包时选swing和awt包;
		//3.获取宽高来进行校验;
		if(image.getWidth(null) > 350 || image.getHeight(null) > 350){
			error("上传的图片尺寸请控制在350X350之内!",req,resp);
			destFile.delete();//删除不符合要求的图片;
			return;
		}
		//把最后符合要求的图片路径设置给book;
		book.setImage_b("book_img/" + filename);
		
		//调用service保存book对象;
		book.setBid(CommonUtils.uuid());
		BookService bookService = new BookService();
		bookService.add(book);
		//保存成功信息;
		req.setAttribute("msg", "添加图书成功!");
		req.getRequestDispatcher("/adminjsps/msg.jsp").forward(req, resp);
	}
	
	//用于保存错误信息到msg.jsp页面;
	private void error(String msg, HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.setAttribute("msg", msg);
		req.setAttribute("parents", new CategoryService().findParents());
		req.getRequestDispatcher("/adminjsps/sdmin/book/add.jsp").forward(req, resp);
	}

}
