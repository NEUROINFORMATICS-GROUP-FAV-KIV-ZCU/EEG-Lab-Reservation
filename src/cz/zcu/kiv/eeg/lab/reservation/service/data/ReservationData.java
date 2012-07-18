package cz.zcu.kiv.eeg.lab.reservation.service.data;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Data container for reservation information.
 * 
 * @author Petr Miko
 * 
 */
@Root(name = "reservation")
public class ReservationData {

	@Element
	private String researchGroup;
	@Element
	private int researchGroupId;
	@Element
	private int reservationId;
	@Element
	private String fromTime;
	@Element
	private String toTime;

	public ReservationData() {
	}

	public ReservationData(String username, String fromTime, String toTime) {
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

	public int getResearchGroupId() {
		return researchGroupId;
	}

	public void setResearchGroupId(int researchGroupId) {
		this.researchGroupId = researchGroupId;
	}

	public int getReservationId() {
		return reservationId;
	}

	public void setReservationId(int reservationId) {
		this.reservationId = reservationId;
	}

	public String getFromTime() {
		return fromTime;
	}

	public void setFromTime(String fromTime) {
		this.fromTime = fromTime;
	}

	public String getToTime() {
		return toTime;
	}

	public void setToTime(String toTime) {
		this.toTime = toTime;
	}
}
