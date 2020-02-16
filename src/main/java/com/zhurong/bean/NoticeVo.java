package com.zhurong.bean;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class NoticeVo implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private String id;
	
	private String createdAt;

	private String url;

	private String name;

	private String age;

	private String city;

	private String community;

	private String sickTime;

	private String contactPhone;
	
	private String contactName;
	
	private String description;
	
	

}
