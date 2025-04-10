package in.codifi.basket.entity.primary;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "TBL_RESEARCHCALL_MASTER")
public class ResearchcallOrderEntity implements Serializable {
	/**
		 * 
		 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long Id;

	@Column(name = "BASKET_NAME")
	private String basketName;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Column(name = "USER_ID")
	private String userId;

	@Column(name = "EXPIRY_DATE")
	private Date expiryDate;

	@Column(name = "CATEGORY")
	private String Category;

	@Column(name = "SUBCATEGORY")
	private String subCategory;

	@Column(name = "TAGS")
	private String tags;

	@Column(name = "SHORTDESCRIPTION")
	private String shortDescription;

	@Column(name = "LONGDESCRIPTION")
	private String longDescription;

	@Column(name = "IS_EXECUTED")
	private String isExecuted = "0";

	@Column(name = "IS_VENDOR_BASKET")
	private int isVendorBasket;

	@Column(name = "RESEARCHCALL")
	private int researchCall;

	@Column(name = "VENDOR_CODE")
	private String vendorCode;

	@Column(name = "SEND_PUSHNOTIFICATION")
	private int sendPushNotification;

	@Column(name = "PUSHNOTIFICATION_title")
	private String PushNotificationTitle;

	@Column(name = "SOURCE")
	private String source;

	@Column(name = "REMARKS")
	private String remarks;

	@Column(name = "CHANNELS")
	private String channels;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "SPECLIZATION_TAG")
	private String speclizationTag;

//	@Column(name = "DESCRIPTION")
//	private String description;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Column(name = "ACTIVE_STATUS")
	private int activeStatus = 1;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Column(name = "UPDATED_BY")
	private String updatedBy;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Column(name = "UPDATED_ON")
	@UpdateTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedOn;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON", insertable = true, updatable = false)
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdOn;

//	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//	@JoinColumn(name = "BASKET_ID", referencedColumnName = "BASKET_ID")
////	@OrderBy("id")
//	@OrderBy("basketId")
//	private List<ResearchcallScripEntity> basketScrip;
}
