package cn.itcast.goods.user.dao;

import java.sql.SQLException;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import cn.itcast.goods.user.domain.User;
import cn.itcast.jdbc.TxQueryRunner;

/**
 * 用户模块层
 * @author Administrator
 */
public class UserDao {
		private QueryRunner qr = new TxQueryRunner();
		
		/**
		 * 利用ajax异步检测数据库表t_user中是否已经存在用户名;
		 * @param loginname
		 * @return boolean
		 * @throws SQLException
		 */
		public boolean ajaxValidateLoginname(String loginname) throws SQLException{
			String sql = "select count(1) from t_user where loginname=?";
			Number number = (Number)qr.query(sql, new ScalarHandler(), loginname);
			return number.intValue() == 0;
		}
		
		/**
		 * 利用ajax异步检测数据库表t_user中是否已经存在邮箱;
		 * @param email
		 * @return boolean
		 * @throws SQLException
		 */
		public boolean ajaxValidateEmail(String email) throws SQLException{
			String sql = "select count(1) from t_user where email=?";
			Number number = (Number)qr.query(sql, new ScalarHandler(), email);
			return number.intValue() == 0;
		}
		
		public void add(User user) throws SQLException{
			String sql = "insert into t_user values(?,?,?,?,?,?)";
			Object[] params = {user.getUid(),user.getLoginname(),user.getLoginpass(),user.getEmail(),user.isStatus(),user.getActivationCode()};
			qr.update(sql, params);
		}
		
		
		//***************第三天新增代码块		开始***************
		/**
		 * 通过激活码在数据库中t_user表中查询用户
		 * @param code
		 * @return
		 * @throws SQLException 
		 */
		public User findByCode(String code) throws SQLException{
			String sql = "select * from t_user where activationCode=?";
			return qr.query(sql, new BeanHandler<User>(User.class), code);
		}
		
		/**
		 * 修改对应uid的用户状态
		 * @param uid
		 * @param status
		 * @throws SQLException 
		 */
		public void updateStatus(String uid,boolean status) throws SQLException{
			String sql = "update t_user set status=? where uid=?";
			qr.update(sql, status, uid);
		}
		
		/**
		 * 实现登陆功能;
		 * 通过用户名和密码返回一个User对象;
		 * @param loginname
		 * @param loginpass
		 * @return User.class
		 * @throws SQLException 
		 */
		public User findByLoginnameAndLoginpass(String loginname,String loginpass) throws SQLException{
			String sql = "select * from t_user where loginname=? and loginpass=?";
			User user = (User)qr.query(sql, new BeanHandler<User>(User.class), loginname,loginpass);
			return user;
			
		}
		
		/**
		 * 通过uid和密码查询用户,返回true或false;
		 * @param uid
		 * @param password
		 * @return
		 * @throws SQLException
		 */
		public boolean findByUidAndPassword(String uid,String password) throws SQLException{
			String sql = "select count(1) from t_user where uid=? and loginpass=?";
			Number number = (Number)qr.query(sql, new ScalarHandler(), uid,password);
			return number.intValue() > 0;
		}
		
		/**
		 * 修改t_user表中对应uid的用户的密码;
		 * 此方法需要在findByUidAndPassword(String uid,String password)之后使用,以确保数据库有旧的值可以修改;
		 * @param uid
		 * @param password
		 * @throws SQLException
		 */
		public void updatePassword(String uid,String password) throws SQLException{
			String sql = "update t_user set loginpass=? where uid=?";
			qr.update(sql, password,uid);
		}
		//***************第三天新增代码块		结束***************
}
