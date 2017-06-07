package com.androidquanjiakan.entity;

import java.io.Serializable;
import java.util.List;

/**
 *礼物实体类
 */
public class ProdctBean implements Serializable {
	/**
	 * code : 200
	 * message : 返回成功
	 * rows : [{"effect":1,"experience":3,"icon":"http://picture.quanjiakan.com:9080/quanjiakan/resources/activity/20170116144157_pqqb1y.png","id":31,"name":"药不能停","price":3,"stage":0,"state":1,"type":1},{"effect":1,"experience":6,"icon":"http://picture.quanjiakan.com:9080/quanjiakan/resources/activity/20170116144208_qxiye4.png","id":32,"name":"妙手回春","price":6,"stage":0,"state":1,"type":1},{"effect":1,"experience":18,"icon":"http://picture.quanjiakan.com:9080/quanjiakan/resources/activity/20170116144223_13c7iq.png","id":33,"name":"一束玫瑰","price":18,"stage":0,"state":1,"type":1},{"effect":1,"experience":88,"icon":"http://picture.quanjiakan.com:9080/quanjiakan/resources/activity/20170116150422_x9u0ed.png","id":34,"name":"悬壶济世","price":88,"stage":0,"state":1,"type":1},{"effect":1,"experience":188,"icon":"http://picture.quanjiakan.com:9080/quanjiakan/resources/activity/20170116150048_rg45iy.png","id":35,"name":"包治百病","price":188,"stage":0,"state":1,"type":1},{"effect":1,"experience":214,"icon":"http://picture.quanjiakan.com:9080/quanjiakan/resources/activity/20170116144305_u4jlqc.png","id":36,"name":"天使的翅膀","price":214,"stage":0,"state":1,"type":1},{"effect":1,"experience":520,"icon":"http://picture.quanjiakan.com:9080/quanjiakan/resources/activity/20170116144321_73vqos.png","id":37,"name":"一生一世","price":520,"stage":0,"state":1,"type":1},{"effect":1,"experience":888,"icon":"http://picture.quanjiakan.com:9080/quanjiakan/resources/activity/20170116144552_diq06d.png","id":39,"name":"轮船","price":888,"stage":0,"state":1,"type":1},{"effect":1,"experience":1314,"icon":"http://picture.quanjiakan.com:9080/quanjiakan/resources/activity/20170116151712_r2x7a4.png","id":40,"name":"爱的诺言","price":1314,"stage":0,"state":1,"type":1},{"effect":1,"experience":2626,"icon":"http://picture.quanjiakan.com:9080/quanjiakan/resources/activity/20170116144411_shky96.png","id":41,"name":"专机护送","price":2626,"stage":0,"state":1,"type":1},{"effect":1,"experience":8888,"icon":"http://picture.quanjiakan.com:9080/quanjiakan/resources/activity/20170116144424_53xl6l.png","id":42,"name":"法拉利","price":8888,"stage":0,"state":1,"type":1},{"effect":1,"experience":9999,"icon":"http://picture.quanjiakan.com:9080/quanjiakan/resources/activity/20170116144434_4fec12.png","id":43,"name":"一飞冲天","price":9999,"stage":0,"state":1,"type":1},{"effect":1,"experience":0,"icon":"http://picture.quanjiakan.com:9080/quanjiakan/resources/activity/20170116144830_91cin7.png","id":44,"name":"鼓掌","price":0,"stage":1,"state":1,"type":0},{"effect":1,"experience":0,"icon":"http://picture.quanjiakan.com:9080/quanjiakan/resources/activity/20170116144850_85u3h4.png","id":45,"name":"赞","price":0,"stage":2,"state":1,"type":0},{"effect":1,"experience":0,"icon":"http://picture.quanjiakan.com:9080/quanjiakan/resources/activity/20170116144858_3jpjbp.png","id":46,"name":"眼睛","price":0,"stage":3,"state":1,"type":0},{"effect":1,"experience":6,"icon":"http://picture.quanjiakan.com:9080/quanjiakan/resources/activity/20170116145808_ubbu98.png","id":47,"name":"起死回生药水","price":6,"stage":0,"state":1,"type":1},{"effect":1,"experience":6,"icon":"http://picture.quanjiakan.com:9080/quanjiakan/resources/activity/20170116145827_b1e8st.png","id":48,"name":"波板糖","price":6,"stage":0,"state":1,"type":1},{"effect":1,"experience":6,"icon":"http://picture.quanjiakan.com:9080/quanjiakan/resources/activity/20170116145938_mqpdxm.png","id":49,"name":"再世华佗","price":6,"stage":0,"state":1,"type":1},{"effect":1,"experience":520,"icon":"http://picture.quanjiakan.com:9080/quanjiakan/resources/activity/20170116150006_2kocld.png","id":50,"name":"心的声音","price":520,"stage":0,"state":1,"type":1},{"effect":1,"experience":8888,"icon":"http://picture.quanjiakan.com:9080/quanjiakan/resources/activity/20170116150036_4mvqi0.png","id":51,"name":"兰博基尼","price":8888,"stage":0,"state":1,"type":1}]
	 * total : 23
	 */

	private String code;
	private String message;
	private int total;
	private List<RowsBean> rows;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public List<RowsBean> getRows() {
		return rows;
	}

	public void setRows(List<RowsBean> rows) {
		this.rows = rows;
	}

	public static class RowsBean {
		/**
		 * effect : 1
		 * experience : 3
		 * icon : http://picture.quanjiakan.com:9080/quanjiakan/resources/activity/20170116144157_pqqb1y.png
		 * id : 31
		 * name : 药不能停
		 * price : 3
		 * stage : 0
		 * state : 1
		 * type : 1
		 */

		private int effect;
		private int experience;
		private String icon;
		private int id;
		private String name;
		private int price;
		private int stage;
		private int state;
		private int type;

		public int getEffect() {
			return effect;
		}

		public void setEffect(int effect) {
			this.effect = effect;
		}

		public int getExperience() {
			return experience;
		}

		public void setExperience(int experience) {
			this.experience = experience;
		}

		public String getIcon() {
			return icon;
		}

		public void setIcon(String icon) {
			this.icon = icon;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getPrice() {
			return price;
		}

		public void setPrice(int price) {
			this.price = price;
		}

		public int getStage() {
			return stage;
		}

		public void setStage(int stage) {
			this.stage = stage;
		}

		public int getState() {
			return state;
		}

		public void setState(int state) {
			this.state = state;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}
	}

//	private String name;
//	private int url;
//	private boolean isLian;
//
//
//	public boolean isLian() {
//		return isLian;
//	}
//
//	public void setLian(boolean lian) {
//		isLian = lian;
//	}
//
//
//
//	public String getName() {
//		return name;
//	}
//
//	public void setName(String name) {
//		this.name = name;
//	}
//
//	public int getUrl() {
//		return url;
//	}
//
//	public void setUrl(int url) {
//		this.url = url;
//	}
//
//	public ProdctBean(String name, int url,boolean lian) {
//		super();
//		this.name = name;
//		this.url = url;
//		this.isLian = lian;
//	}


}