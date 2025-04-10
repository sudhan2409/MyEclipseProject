package com.sas.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DefaultLoginDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	// implements DataSerializable
	private String stat_DFlogin;
	private Object exch;
	private Object prctyp;
	private Object pCode;
	private Object s_prdt_ali;
	private String sTransFlg;
	private String default_market_watch_name;
	private String broker_name;
	private String branch_id;
	private String market_watch_count;
	private String email;
	private Object weblink;
	private String account_id;
	private Object exchDeatil;
	private String lots_weight;
	private String password_special_character;
	private String accountName;
	private String userPrivileges;
	private String ySXorderEntry;
	private Object criteriaAttribute_array;
	private String emsg_DFlogin;

}