package cn.itcast.goods.book.Page;

import java.util.List;

public class PageBean<T> {
	private int pc;//当前页码;
	private int tr;//总记录数;
	private int ps;//每页记录数;
	private String url;//请求路径和参数
	private List<T> beanList;//
	
	//********** set get 开始 **********
	
	//计算总页数 tp;
	public int getTp() {
		int tp = tr / ps ;
		return tr%ps == 0 ? tp : tp + 1;//此处是考虑到  101/10   多出来一页的情况;
	}
	public int getPc() {
		return pc;
	}
	public void setPc(int pc) {
		this.pc = pc;
	}
	public int getTr() {
		return tr;
	}
	public void setTr(int tr) {
		this.tr = tr;
	}
	public int getPs() {
		return ps;
	}
	public void setPs(int ps) {
		this.ps = ps;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public List<T> getBeanList() {
		return beanList;
	}
	public void setBeanList(List<T> beanList) {
		this.beanList = beanList;
	}
	//********** set get 结束 **********

}
