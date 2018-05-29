package cn.itcast.goods.user.domain;

/**
 * 用户模块持久层
 * 对应数据库中的t_user表;
 * @author Administrator
 *
 */
public class User {
		//数据库表中对应的数据
		private String uid;//主键
		private String loginname;
		private String loginpass;
		private String email;
		private boolean status;//激活状态
		private String activationCode;//激活码,因为是唯一的,可以使用uuid码;
		
		//注册表单中对应的数据;
		private String reloginpass;//确认密码;
		private String verifyCode;//验证码;
		
		//修改密码表单中对应的数据;
		private String newpass;//修改密码;
		
		/*set get 方法开始*/
		public String getReloginpass() {
			return reloginpass;
		}
		public void setReloginpass(String reloginpass) {
			this.reloginpass = reloginpass;
		}
		public String getVerifyCode() {
			return verifyCode;
		}
		public String getEmail() {
			return email;
		}
		public void setEmail(String email) {
			this.email = email;
		}
		public void setVerifyCode(String verifyCode) {
			this.verifyCode = verifyCode;
		}
		
		public String getNewpass() {
			return newpass;
		}
		public void setNewpass(String newpass) {
			this.newpass = newpass;
		}
		public String getUid() {
			return uid;
		}
		public void setUid(String uid) {
			this.uid = uid;
		}
		public String getLoginname() {
			return loginname;
		}
		public void setLoginname(String loginname) {
			this.loginname = loginname;
		}
		public String getLoginpass() {
			return loginpass;
		}
		public void setLoginpass(String loginpass) {
			this.loginpass = loginpass;
		}
		public boolean isStatus() {
			return status;
		}
		public void setStatus(boolean status) {
			this.status = status;
		}
		public String getActivationCode() {
			return activationCode;
		}
		public void setActivationCode(String activationCode) {
			this.activationCode = activationCode;
		}
		/*set get 方法结束*/
}
