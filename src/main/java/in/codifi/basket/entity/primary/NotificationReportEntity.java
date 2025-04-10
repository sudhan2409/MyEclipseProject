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
@Entity(name = "TBL_NOTIFICATION_REPORT")
public class NotificationReportEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "BASKET_ID")
	private int basketId;

	@Column(name = "BASKET_NAME")
	private String basketName;

	@Column(name = "NOTIFY_ID")
	private String notifyId;

	@Column(name = "CLICKED_STATUS")
	private String clickedStatus;

	@Column(name = "USER_ID")
	private String userId;

	@Column(name = "VENDOR")
	private String vendor;

	@Column(name = "APP_CODE")
	private String appCode;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Column(name = "created_on", insertable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@CreationTimestamp
	private Date createdOn;

	@Column(name = "created_by")
	private String createdBy;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@UpdateTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated_on")
	private Date updatedOn;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "active_status")
	private int activeStatus;

}
