package cn.itcast.goods.admin.admin.domain;

public class Admin {
	private String adminId;//主键;
	private String adminname;//登陆名;
	private String adminpwd;//登陆密码;
	
	//**************set get constructor 开始******************
	public String getAdminId() {
		return adminId;
	}
	public void setAdminId(String adminId) {
		this.adminId = adminId;
	}
	public String getAdminname() {
		return adminname;
	}
	public void setAdminname(String adminname) {
		this.adminname = adminname;
	}
	public String getAdminpwd() {
		return adminpwd;
	}
	public void setAdminpwd(String adminpwd) {
		this.adminpwd = adminpwd;
	}
	public Admin(String adminId, String adminname, String adminpwd) {
		super();
		this.adminId = adminId;
		this.adminname = adminname;
		this.adminpwd = adminpwd;
	}
	public Admin() {
		super();
	}
	//**************set get constructor 结束******************

}
