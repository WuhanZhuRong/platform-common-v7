package com.zhurong.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

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

	public String contributor;

	public String province;

	public String city;

	public String suburb;

	public String name;

	public String address;

	public String phone;

	public ArrayList<Map<String, String>> supplies;

	public ArrayList<Map<String, String>> getSupplies() { return supplies; };

	public void setSupplies() { this.supplies = supplies; };

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContributor() {
		return contributor;
	}

	public void setContributor(String contributor) {
		this.contributor = contributor;
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

	public String getSuburb() {
		return suburb;
	}

	public void setSuburb(String suburb) {
		this.suburb = suburb;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}



}
