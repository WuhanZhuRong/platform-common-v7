package com.zhurong.model;

import java.util.ArrayList;

public class Filter {
    public Integer page;
    public Integer size;
    public ArrayList<Integer> supplies;
    public String city;

    public Integer getPage() { return page; }

    public Integer getSize() { return size; }

    public ArrayList<Integer> getSupplies() { return supplies; }

    public String getCity() { return city; }

    public void setPage(Integer page) { this.page = page; }

    public void setSize(Integer size) { this.size = size; }

    public void setSupplies(ArrayList<Integer> supplies) { this.supplies = supplies; }

    public void setCity(String city) { this.city = city; }

	@Override
	public String toString() {
		return "Filter [page=" + page + ", size=" + size + ", supplies=" + supplies + ", city=" + city + "]";
	}
    
    
}
