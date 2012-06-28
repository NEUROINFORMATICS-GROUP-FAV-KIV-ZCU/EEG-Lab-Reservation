package cz.zcu.kiv.eeg.lab.reservation.data;

import java.io.Serializable;
import java.util.Date;

/**
 * Data container for reservation information.
 * 
 * @author Petr Miko
 * 
 */
public class ReservationData implements Serializable {

	private static final long serialVersionUID = 8850665675446609744L;
	private String researchGroup;
	private Date fromTime;
	private Date toTime;

	public ReservationData(String username, Date fromTime, Date toTime) {
		researchGroup = username;
		this.fromTime = fromTime;
		this.toTime = toTime;
	}

	public String getResearchGroup() {
		return researchGroup;
	}

	public void setResearchGroup(String researchGroup) {
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
}
