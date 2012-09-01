package cz.zcu.kiv.eeg.lab.reservation.data;

import java.io.Serializable;
import java.util.Date;

/**
 * Data container for reservation information.
 * 
 * @author Petr Miko
 * 
 */
public class Reservation implements Serializable {

	private static final long serialVersionUID = 8850665675446609744L;
	private CharSequence researchGroup;
	private Date fromTime;
	private Date toTime;
	private String creatorName;
	private String email;

	public Reservation(CharSequence researchGroup, Date fromTime, Date toTime, String creatorName, String email) {
		this.researchGroup = researchGroup;
		this.fromTime = fromTime;
		this.toTime = toTime;
		this.creatorName = creatorName;
		this.email = email;
	}

	public CharSequence getResearchGroup() {
		return researchGroup;
	}

	public void setResearchGroup(CharSequence researchGroup) {
		this.researchGroup = researchGroup;
	}

	public Date getFromTime() {
		return fromTime;
	}

	public void setFromTime(Date fromTime) {
		this.fromTime = fromTime;
	}

	public Date getToTime() {
		return toTime;
	}

	public void setToTime(Date toTime) {
		this.toTime = toTime;
	}

	public String getCreatorName() {
		return creatorName;
	}

	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
