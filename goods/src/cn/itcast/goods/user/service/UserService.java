package cn.itcast.goods.user.service;

import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.user.dao.UserDao;
import cn.itcast.goods.user.domain.User;
import cn.itcast.goods.user.service.exception.UserException;
import cn.itcast.mail.Mail;
import cn.itcast.mail.MailUtils;

/**
 * 用户模块的业务层
 * @author Administrator
 *
 */
public class UserService {
		private UserDao userDao = new UserDao();
		
		/**
		 * 利用ajax异步检测数据库表t_user中是否已经存在用户名;直接搬运userDao中的方法,并抛出运行时异常,防止上级处理异常;
		 * @param loginname
		 * @return boolean
		 * @throws RuntimeException
		 */
		public boolean ajaxValidateLoginname(String loginname){
			try {
				return userDao.ajaxValidateLoginname(loginname);
			} catch (SQLException e) {
				throw new RuntimeException();
			}
		}
		
		/**
		 * 利用ajax异步检测数据库表t_user中是否已经存在邮箱;直接搬运userDao中的方法,并抛出运行时异常,防止上级处理异常;
		 * @param email
		 * @return boolean
		 * @throws RuntimeException
		 */
		public boolean ajaxValidateEmail(String email){
			try {
				return userDao.ajaxValidateEmail(email);
			} catch (SQLException e) {
				throw new RuntimeException();
			}
		}
		
		public void regist(User user){
			//补齐user里的数据以保证与数据库中的表对应;
			user.setUid(CommonUtils.uuid());
			user.setStatus(false);
			user.setActivationCode(CommonUtils.uuid()+CommonUtils.uuid());
			
			//向数据库中写入;
			try {
				userDao.add(user);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
			
			//发送注册邮件;
			System.out.println("已到达发送邮件的模块!");
			
			
			/********此处代码存在异常!抛出反射错误,疑似无法连接发送邮件的服务器;**********
			 * 
			Properties prop = new Properties();
			try {
				prop.load(this.getClass().getClassLoader().getResourceAsStream("email_template.properties"));
			} catch (IOException e1) {
				throw new RuntimeException(e1);
			}
			String host = prop.getProperty("host");//服务器主机名;
			String name = prop.getProperty("username");//用户名;
			String pass = prop.getProperty("password");//登陆密码;
			Session session = MailUtils.createSession(host, name, pass);

			String from = prop.getProperty("from");
			String to = user.getEmail();
			String subject = prop.getProperty("subject");
			//MessageFormat.format(parentstr,str2...)方法可以将parentstr模版中的占位符用str2补全;content属性里有一位占位符需要我们补上激活码;
			String content = MessageFormat.format(prop.getProperty("content"),user.getActivationCode());
			Mail mail = new Mail(from,to,subject,content);
			
			try {
				MailUtils.send(session, mail);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			*/
		}
		
		
		//***************第三天新增代码块		开始***************
		
		/**
		 * 激活用户
		 * @param code
		 * @throws UserException 
		 * @throws SQLException 
		 */
		public void activation(String code) throws UserException{
			/*  1.使用激活吗进行数据库查询,得到User;
				2.判断User是不是null:
					1.如果是null则抛出一个自定义的异常(无效的激活码);
					2.如果不是null,查看User的状态是否是已经激活(true),如果是,则抛出自定义异常(已经是激活状态,无需再次激活);
					3.如果不是null,并且不是已激活状态,则修改用户的状态为true;
			*/
			
			//实现2.1,抛出无效激活码的提示;
			User user;
			try {
				user = userDao.findByCode(code);
				if(null == user) throw new UserException("无效的激活码!");
				if(user.isStatus()) throw new UserException("您已经激活过了,无需再次激活!");
				userDao.updateStatus(user.getUid(), true);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
			
		}
		
		
		//登陆功能
		
		/**
		 * 登陆功能,调用UserDao里的findByLoginnameAndLoginpass(loginname,loginpass)方法;
		 * @param user
		 * @return
		 */
		public User login(User user){
			try {
				String loginname = user.getLoginname();
				String loginpass = user.getLoginpass();
				return userDao.findByLoginnameAndLoginpass(loginname,loginpass);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
		
		
		public void updatePassword(String uid,String newPassword,String oldPassword) throws UserException{
			/*
			 * 校验老密码,使用uid和old去访问UserDao,得到结果;
					如果校验失败,抛出异常;
					如果校验通过,使用uid和old去访问UserDao,完成修改密码;
			*/
			
			try {
				boolean bool = userDao.findByUidAndPassword(uid, oldPassword);
				if(!bool){
					throw new UserException("原密码错误!");
				}else{
					userDao.updatePassword(uid, newPassword);
				}
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
		//***************第三天新增代码块		开始***************
		
}
