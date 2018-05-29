package cn.itcast.goods.book.service;

import java.sql.SQLException;

import org.apache.commons.dbutils.handlers.ScalarHandler;

import cn.itcast.goods.book.Page.PageBean;
import cn.itcast.goods.book.dao.BookDao;
import cn.itcast.goods.book.domain.Book;

public class BookService {
	private BookDao bookDao = new BookDao();
	
	/**
	 * 按分类模糊查询;
	 * @param cid
	 * @param pc
	 * @return
	 */
	public PageBean<Book> findByCategory(String cid, int pc){
		try {
			return bookDao.findByCategory(cid, pc);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 按作者模糊查询;
	 * @param author
	 * @param pc
	 * @return
	 */
	public PageBean<Book> findByAuthor(String author, int pc){
		try {
			return bookDao.findByAuthor(author, pc);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 按出版社模糊查询;
	 * @param press
	 * @param pc
	 * @return
	 */
	public PageBean<Book> findByPress(String press, int pc){
		try {
			return bookDao.findByPress(press, pc);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 按书名模糊查询;
	 * @param bname
	 * @param pc
	 * @return
	 */
	public PageBean<Book> findByBname(String bname, int pc){
		try {
			return bookDao.findByBname(bname, pc);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 多条件组合查询;
	 * @param criteria
	 * @param pc
	 * @return
	 */
	public PageBean<Book> findByCombination(Book criteria, int pc){
		try {
			return bookDao.findByCombination(criteria, pc);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 按bid加载图书;在UserDao里是findByBid(str)方法;
	 * @param bid
	 * @return
	 */
	public Book load(String bid){
		try {
			return bookDao.findByBid(bid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	//******************第七天新增代码块	开始************************
		/**
		 * 查询指定父分类下图书的个数;
		 * @param pid
		 * @return
		 * @throws SQLException
		 */
		public int findBookCountByCategory(String pid){
			try {
				return bookDao.findBookCountByCategory(pid);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	//******************第七天新增代码块	结束************************
	//******************第八天新增代码块	开始************************
		/**
		 * 添加图书;
		 * @param book
		 */
		public void add(Book book){
			try {
				bookDao.add(book);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
		
		/**
		 * 修改图书;
		 * @param book
		 */
		public void edit(Book book){
			try {
				bookDao.edit(book);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
		
		/**
		 * 修改图书;
		 * @param book
		 */
		public void delete(String bid){
			try {
				bookDao.delete(bid);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
		
		
	//******************第八天新增代码块	结束************************
}
