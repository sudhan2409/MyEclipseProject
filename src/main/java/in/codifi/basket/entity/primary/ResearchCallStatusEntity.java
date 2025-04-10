package in.codifi.basket.entity.primary;

import javax.persistence.Column;
import javax.persistence.Entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "tbl_researchcall_status")
public class ResearchCallStatusEntity extends CommonEntity {
	/**	
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "statuscode")
	private int statusCode;
	@Column(name = "status")
	private String status;

}
