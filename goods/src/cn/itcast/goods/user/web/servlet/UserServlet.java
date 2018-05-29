package cn.itcast.goods.user.web.servlet;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.user.domain.User;
import cn.itcast.goods.user.service.UserService;
import cn.itcast.goods.user.service.exception.UserException;
import cn.itcast.servlet.BaseServlet;


/**
 * 用户模块控制层
 * @author Administrator
 *
 */
public class UserServlet extends BaseServlet {
		private UserService userService=new UserService();
		
		/**
		 * 用于执行注册功能
		 * @param req
		 * @param resp
		 * @throws ServletException
		 * @throws IOException
		 */
		public String regist(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
				System.out.println("执行servlet里的注册功能测试...");
				//1.将得到的req里的所有数据封装到user类中;
				User formUser = CommonUtils.toBean(req.getParameterMap(),User.class);
				
				//2.校验参数;
				Map<String,String> errors =validateRegist(formUser, req);
				if(errors.size() > 0){
					req.setAttribute("form", formUser);//此行代码重新将form表单中的数据发送回去,避免用户输入的数据被清空;
					req.setAttribute("errors", errors);
					return "f:/jsps/user/regist.jsp";
				}
				//3.使用service完成业务;
				userService.regist(formUser);
				
				//4.保存成功信息并转发给专门显示信息的msg.jsp页面中;
				req.setAttribute("code", "success");
				req.setAttribute("msg", "注册成功,请前往注册邮箱中激活!");
				
				return "f:/jsps/msg.jsp";
			}
		
		//创建一个校验方法并使用map专门用于存放校验后的信息;此方法只能在regist方法里调用
		private Map<String,String> validateRegist(User formUser,HttpServletRequest req){
			System.out.println("执行servlet里的validateRegist功能测试...");
			Map<String,String> errors = new HashMap<String,String>();
			
			//1.校验用户名;
			String loginname = formUser.getLoginname();
			if(null == loginname || loginname.trim().isEmpty()){
				errors.put("loginname", "用户名不能为空!");
			}else if(loginname.length()<3 || loginname.length()>20){
				errors.put("loginname", "用户名长度须在3到20之间!");
			}else if(!userService.ajaxValidateLoginname(loginname)){
				errors.put("loginname", "用户名已经存在!");
			}
			
			//2.校验登陆密码;
			String loginpass = formUser.getLoginpass();
			if(null == loginpass || loginpass.trim().isEmpty()){
				errors.put("loginpass", "密码不能为空!");
			}else if(loginpass.length()<3 || loginpass.length()>20){
				errors.put("loginpass", "密码长度须在3到20之间!");
			}
			
			//3.校验确认密码;
			String reloginpass = formUser.getReloginpass();
			if(null == reloginpass || reloginpass.trim().isEmpty()){
				errors.put("reloginpass", "确认密码不能为空!");
			}else if(!reloginpass.equals(loginpass)){
				errors.put("reloginpass", "两次输入的密码必须一致!");
			}
			
			//4.校验email;
			String email = formUser.getEmail();
			if(null == email || email.trim().isEmpty()){
				errors.put("email", "邮箱不能为空!");
			}else if(!email.matches("^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\\.[a-zA-Z0-9_-]{2,3}){1,2})$")){
				errors.put("email", "邮箱格式不正确!");
			}else if(!userService.ajaxValidateEmail(email)){
				errors.put("email", "邮箱已经存在!");
			}
			
			//5.校验验证码;
			String verifyCode = formUser.getVerifyCode();
			String vcode =(String)req.getSession().getAttribute("vCode");
			System.out.println(vcode + "5.校验验证码");
			if(null == verifyCode || verifyCode.trim().isEmpty()){
				errors.put("verifyCode", "请填写验证码!");
			}else if(!verifyCode.equalsIgnoreCase(vcode)){
				errors.put("verifyCode", "验证码错误!");
			}
			
			return errors;
		}
		
		
		/**
		 * ajax异步检测用户名是否符合要求; 
		 * @param req
		 * @param resp
		 * @return
		 * @throws ServletException
		 * @throws IOException
		 */
		public String ajaxValidateLoginname(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
			System.out.println("执行servlet里的ajaxValidateLoginname功能测试...");
			//通过request获得用户名;
			String loginname = req.getParameter("loginname");
			//调用UserService类里的ajax方法判断用户名是否符合要求,并返回一个boolean值;
			boolean b = userService.ajaxValidateLoginname(loginname);
			//将上面返回的boolean值写出到response中发送给客户端;
			resp.getWriter().print(b);
			return null;
		}
		
		/**
		 * ajax异步检测邮箱是否符合要求;
		 * @param req
		 * @param resp
		 * @return
		 * @throws ServletException
		 * @throws IOException
		 */
		public String ajaxValidateEmail(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
			System.out.println("执行servlet里的ajaxValidateEmail功能测试...");
			//通过request获得email邮箱;
			String email = req.getParameter("email");
			//调用UserService类里的ajax方法判断email邮箱是否符合要求,并返回一个boolean值;
			boolean b = userService.ajaxValidateEmail(email);
			//将上面返回的boolean值写出到response中发送给客户端,让客户端去根据boolean执行;
			resp.getWriter().print(b);
			return null;
		}
		
		/**
		 * ajax异步检测验证码是否符合要求;
		 * @param req
		 * @param resp
		 * @return
		 * @throws ServletException
		 * @throws IOException
		 */
		public String ajaxValidateVerifyCode(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
			System.out.println("执行servlet里的ajaxValidateVerifyCode功能测试...");
			//1.获取输入框中的验证码;
			String verifyCode = req.getParameter("verifyCode");
			//2.获取图片上的校验码;
			String vcode = (String)req.getSession().getAttribute("vCode");
			System.out.println(vcode + "ajax异步检测验证码是否符合要求");
			//3.进行忽略大小写的对比判断;
			boolean b = verifyCode.equalsIgnoreCase(vcode);
			//4.将boolean值发送给客户端,让客户端去根据boolean执行;
			resp.getWriter().print(b);
			return null;
			}
		
		//*************第三天完善的代码块 	开始*************
		/**
		 * 用于让用户点击邮箱里的激活链接访问此方法;
		 * 暂时无法利用邮箱服务发送邮件,因此此功能无法测试;
		 * @param req
		 * @param resp
		 * @return
		 * @throws ServletException
		 * @throws IOException
		 */
		public String activation(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
			
				System.out.println("测试激活方法...");
				/*	1.UserServlet 里需要activation方法,此方法实现以下功能:
				*		1.获取激活码;
				*		2.把激活码交给service的activation(String)来完成激活;
				*		3.保存成功信息,转发给msg.jsp显示;
				*/
				String code = req.getParameter("activationCode");
				try {
					userService.activation(code);
					req.setAttribute("msg", "激活成功!");//通知msg.jsp显示激活成功的信息;
					req.setAttribute("code", "success");//此行代码map中的code=errors是为了让msg.jsp判断该显示√图片还是×图片;
				} catch (UserException e) {
					//代码运行到此位置说明userService出了异常;此时获取异常信息并发送给msg.jsp;
					req.setAttribute("msg", e.getMessage());//通知msg.jsp显示userService抛出的异常错误信息;
					req.setAttribute("code", "errors");//此行代码map中的code=errors是为了让msg.jsp判断该显示√图片还是×图片;
				}
				return "f:/jsps/msg.jsp";
			}
		
		//登陆功能
		/**
		 * 
		 * @param req
		 * @param resp
		 * @return
		 * @throws ServletException
		 * @throws IOException
		 */
		public String login(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
				/*
				 * 登陆页面login.jsp会提交数据请求UserService里的login()方法实现以下功能:
					1.封装表单数据到User中;
					2.校验表单数据;
					3.将登陆工作交给UserService,返回user对象;
					4.判断:
						1.如果user存在:
							1.判断用户激活状态是否为true;
								1.如果是false,说明用户没有激活.保存错误信息,转发到login.jsp;
								2.如果是true:
									1.将当前用户保存到session中;
									2.将当前用户保存到cookie中,下次该用户登陆是,用户名会自动填充到登陆框中;
									3.重定向到主页index.jsp;
						2.如果user不存在:
							1.保存错误信息;
							2.保存表单信息,回显到login.jsp;
				 * */
			
			//实现步骤1;
			User formUser = CommonUtils.toBean(req.getParameterMap(), User.class);
			
			//实现步骤2;
			Map<String,String> errors = validateLogin(formUser, req);
			if(errors.size() > 0){
				req.setAttribute("form", formUser);//此行代码重新将form表单中的数据发送回去,避免用户输入的数据被清空;
				req.setAttribute("errors", errors);
				return "f:/jsps/user/login.jsp";
			}
			
			//实现步骤3;
			User user = userService.login(formUser);
			
			//实现步骤4;
			if(null != user){//说明user存在;
				if(!user.isStatus()){//说明user未激活;
					req.setAttribute("msg", "用户没有激活!");
					req.setAttribute("user", formUser);
					return "f:/jsps/user/login.jsp";
				}else{//说明user存在并且已激活;
					//保存用户到session中;
					req.getSession().setAttribute("sessionUser", user);//此处的user必须是从步骤3中返回的数据库中的user;
					//保存用户到cookie中;注意编码问题;
					String loginname = user.getLoginname();
					loginname = URLEncoder.encode(loginname, "utf-8");
					Cookie cookie = new Cookie("loginname",loginname);
					cookie.setMaxAge(60*60*24*10*10);//设置cookie的生命周期,单位是秒,不是毫秒;
					resp.addCookie(cookie);
					return "f:/index.jsp";
				}
			}else{//说明user不存在;
				req.setAttribute("msg", "用户名或者密码错误!");
				req.setAttribute("user", formUser);
				return "f:/jsps/user/login.jsp";
			}
		}
		//创建一个校验方法并使用map专门用于存放校验后的信息;此方法只能在login()方法里调用;
		//代码需要日后根据需求不断完善;
				private Map<String,String> validateLogin(User formUser,HttpServletRequest req){
					System.out.println("执行servlet里的validateRegist功能测试...");
					Map<String,String> errors = new HashMap<String,String>();
					
					//1.校验用户名;
					//2.校验登陆密码;
					//3.校验验证码;
					return errors;
				}
		/**
		 * 修改密码功能;
		 * @param req
		 * @param resp
		 * @return 
		 * @throws ServletException
		 * @throws IOException
		 * @throws UserException 
		 */
		public String updatePassword(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException{
			/*
			 *  1.封装表单数据到User中;
			 *  2.从session中获取uid
				2.校验表单数据;
				获取当前uid,password,给service,来完成修改工作;
					service有可能抛出异常;
						1.把议程新词保存到request中;
						2.转发到pwd.jap中;
					若没有异常,保存成功信息,转发到msg.jsp;
			 * */
			User formUser = CommonUtils.toBean(req.getParameterMap(), User.class);
			User user = (User) req.getSession().getAttribute("sessionUser");//sessionUser是session中的user的自定义别称;
			if(null == user){
				req.setAttribute("msg", "请先登陆再执行操作!");
				return "f:/jsps/user/login.jsp";
			}
			//开始执行更新密码的操作,uid选择从session中获取以确保数据准确对应;
			try {
				userService.updatePassword(user.getUid(), formUser.getNewpass(), formUser.getLoginpass());
				req.setAttribute("msg", "修改密码成功!");
				req.setAttribute("code", "success");
				return "f:/jsps/msg.jsp";
			} catch (UserException e) {
				req.setAttribute("msg", e.getMessage());
				req.setAttribute("user", formUser);//回显;
				return "f:/jsps/user/msg.jsp";
			}
		}
		/**
		 * 退出功能;
		 * @param req
		 * @param resp
		 * @return
		 * @throws ServletException
		 */
		public String quit(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException{
			req.getSession().invalidate();
			return "r:/jsps/user/login.jsp";//注意此处应该是重定向而不是转发;
		}
		//*************第三天完善的代码块 	结束*************
}
