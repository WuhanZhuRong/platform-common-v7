package com.zhurong.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.elasticsearch.common.geo.GeoPoint;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Hospital implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public String id;

	public String province;

	public String city;

	public String area;

	public String hospital;

	public String street;

	public String mobile;

	public String contacts;

	public GeoPoint location;

	public ArrayList<Map<String, String>> supplies;

	public void setContacts(String contacts) {
		this.contacts = contacts;
	}

	public String getContacts() {
		return contacts;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getMobile() {
		return mobile;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getStreet() {
		return street;
	}

	public void setSupplies(ArrayList<Map<String, String>> supplies) {
		this.supplies = supplies;
	}

	public ArrayList<Map<String, String>> getSupplies() {
		return supplies;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public void setHospital(String hospital) {
		this.hospital = hospital;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getHospital() {
		return hospital;
	}

}
