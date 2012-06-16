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

	private String username;
	private Date fromTime;
	private Date toTime;

	public ReservationData(String username, Date fromTime, Date toTime) {
		this.username = username;
		this.fromTime = fromTime;
		this.toTime = toTime;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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
