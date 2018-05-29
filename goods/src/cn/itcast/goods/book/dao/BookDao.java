package cn.itcast.goods.book.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.book.Page.Expression;
import cn.itcast.goods.book.Page.PageBean;
import cn.itcast.goods.book.Page.PageConstants;
import cn.itcast.goods.book.domain.Book;
import cn.itcast.goods.category.domain.Category;
import cn.itcast.jdbc.TxQueryRunner;

public class BookDao {
	private QueryRunner qr = new TxQueryRunner();
	
	/**
	 * 通用的查询方法;
	 * @param exprList
	 * @param pc
	 * @return
	 * @throws SQLException 
	 */
	private PageBean<Book> findByCriteria(List<Expression> exprList,int pc) throws SQLException{
		
		/**
		 * 1.得到ps;tr;;beanList;
		 * 2.创建PageBean,并返回;
		 */
		
		int ps = PageConstants.BOOK_PAGE_SIZE;
		StringBuilder whereSql = new StringBuilder (" where 1=1");//where条件子句的头;
		List<Object> params = new ArrayList<Object>();
		//遍历exprList并调用里面的属性生成where条件子句的后面部分;
		for(Expression expr : exprList) {
			//参考例句: "where 1=1 and bid = "
			whereSql.append(" and ").append(expr.getName()).append(" ").append(expr.getOperator()).append(" ");
			if(!expr.getOperator().equals("is null")){//当where条件子句是"where 1=1 and bid is null "时是不需要参数占位符的;
				whereSql.append("?");//此时变成: "where 1=1 and bid = ?" ;
				params.add(expr.getValue());//此时qr.query(sql , handler ,params)就齐全了;//此处曾抛出一个异常,原因是MySQL中执行查询语句时需要格式为 cid = "cid的值", cid是char类型,有可能需要添加引号;
			}
		}
		
		String sql = "select count(*) from t_book" + whereSql;
		Number number = (Number) qr.query(sql, new ScalarHandler(), params.toArray());
		int tr = number.intValue();//此时得到tr;
		
		//开始执行分页查询,pc就是客户端客户点击的页数,
		
		sql = "select * from t_book" + whereSql + " order by orderBy limit ?,?";
		params.add((pc-1) * ps);
		params.add(ps);
		
		List<Book> beanList = qr.query(sql, new BeanListHandler<Book>(Book.class), params.toArray());
		
		//将所有执行完的数据打包后返回;
		PageBean<Book> pb = new PageBean<Book>();
		pb.setBeanList(beanList);
		pb.setPc(pc);
		pb.setPs(ps);
		pb.setTr(tr);
		
		return pb;
	}
	
	/**
	 * 通过cid分类查询;
	 * @param cid
	 * @param pc
	 * @return
	 * @throws SQLException
	 */
	public PageBean<Book> findByCategory(String cid , int pc) throws SQLException{
		List<Expression> exprList = new ArrayList<Expression>();
		exprList.add(new Expression("cid", "=" , cid));
		return findByCriteria(exprList, pc);
	}
	
	/**
	 * 按书名模糊查询;
	 * @param bname
	 * @param pc
	 * @return
	 * @throws SQLException
	 */
	public PageBean<Book> findByBname(String bname , int pc) throws SQLException{
		List<Expression> exprList = new ArrayList<Expression>();
		exprList.add(new Expression("bname", "like" , "%" + bname + "%"));
		return findByCriteria(exprList, pc);
	}
	
	/**
	 * 按作者模糊查询;
	 * @param author
	 * @param pc
	 * @return
	 * @throws SQLException
	 */
	public PageBean<Book> findByAuthor(String author , int pc) throws SQLException{
		List<Expression> exprList = new ArrayList<Expression>();
		exprList.add(new Expression("author", "like" , "%" + author + "%"));
		return findByCriteria(exprList, pc);
	}
	
	/**
	 * 按出版社查;
	 * @param press
	 * @param pc
	 * @return
	 * @throws SQLException
	 */
	public PageBean<Book> findByPress(String press , int pc) throws SQLException{
		List<Expression> exprList = new ArrayList<Expression>();
		exprList.add(new Expression("press", "like" , "%" + press + "%"));
		return findByCriteria(exprList, pc);
	}
	
	/**
	 * 多条件组合查询;
	 * @param criteria
	 * @param pc
	 * @return
	 * @throws SQLException
	 */
	public PageBean<Book> findByCombination(Book criteria, int pc) throws SQLException{
		List<Expression> exprList = new ArrayList<Expression>();
		exprList.add(new Expression("bname", "like" , "%" +criteria.getBname()+ "%"));
		exprList.add(new Expression("author", "like" , "%" + criteria.getAuthor() + "%"));
		exprList.add(new Expression("press", "like" , "%" + criteria.getPress() + "%"));
		return findByCriteria(exprList, pc);
	}

	
//******************第七天新增代码块	开始************************
	/**
	 * 查询指定父分类下图书的个数;
	 * @param pid
	 * @return
	 * @throws SQLException
	 */
	public int findBookCountByCategory(String pid) throws SQLException{
		String sql = "select count(*) from t_book where cid=?";
		Number cnt = (Number)qr.query(sql, new ScalarHandler(),pid);
		return cnt == null ? 0 : cnt.intValue();
	}
//******************第七天新增代码块	结束************************
//****************第八天重新修改代码块 开始*******************	
	/**
	 * 第八天重新修改成多表查询;
	 * 按bid精确查询;用于在desc.jsp页面显示商品详情;
	 * 将cid属性封装到Category类,其它属性封装到Book类,然后将这两个类关联,然后返回Book对象;
	 * @param bname
	 * @return
	 * @throws SQLException
	 */
	public Book findByBid(String bid) throws SQLException{
		String sql = "select * from t_book b, t_category c where b.bid=c.cid and b.bid=?";
		Map<String,Object> map = qr.query(sql, new MapHandler(), bid);
		Book book = CommonUtils.toBean(map, Book.class);
		Category category = CommonUtils.toBean(map, Category.class);
		//两个对象建立联系;
		book.setCategory(category);
		//取出pid并创建一个Category parent并将pid赋进去,再将parent赋值给category;
		if(null != map.get("pid")){
			Category parent = new Category();
			parent.setCid((String)map.get("pid"));
			category.setParent(parent);
		}
		return book;
	}
//****************第八天重新修改代码块 开始*******************	
//******************第八天新增代码块	开始************************
	/**
	 * 添加图书;
	 * @param book
	 * @throws SQLException 
	 */
	public void add(Book book) throws SQLException {
		String sql = "insert into t_book(bid,bname,author,price,currPrice,discount,press,"
				+"publishtime,edition,pageNum,wordNum,printtime,booksize,paper,cid,image_w,image_b)"
				+" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		Object[] params = {book.getBid(),book.getAuthor(),book.getPrice(),book.getCurrPrice(),book.getDiscount(),
				book.getPress(),book.getPublishtime(),book.getEdition(),book.getPageNum(),book.getWordNum(),book.getPrinttime(),
				book.getBooksize(),book.getPaper(),book.getCategory().getCid(),book.getImage_w(),book.getImage_b()
				
		};
		qr.update(sql, params);
	}
	
	/**
	 * 修改图书;
	 * @param book
	 * @throws SQLException
	 */
	public void edit(Book book) throws SQLException{
		String sql = "update t_book set bname=?,author=?,price=?,currPrice=?,discount=?,press=?,"
				+"publishtime=?,edition=?,pageNum=?,wordNum=?,printtime=?,booksize=?,paper=?,cid=? where bid=?";
				
		Object[] params = {book.getAuthor(),book.getPrice(),book.getCurrPrice(),book.getDiscount(),
				book.getPress(),book.getPublishtime(),book.getEdition(),book.getPageNum(),book.getWordNum(),book.getPrinttime(),
				book.getBooksize(),book.getPaper(),book.getCategory().getCid(),book.getBid()
		};
		qr.update(sql, params);
	}
	
	/**
	 * 删除图书;
	 * @param bid
	 * @throws SQLException 
	 */
	public void delete(String bid) throws SQLException {
		String sql = "delete from t_book where bid=?";
		qr.update(sql,bid);
	}
//******************第八天新增代码块	结束************************

	
}
