package cz.zcu.kiv.eeg.lab.reservation.service.data;

/**
 * Data container for Research Group information.
 * @author Petr Miko
 *
 */
public class ResearchGroupData {

	private int groupId;
	private String groupName;
	
	public ResearchGroupData(){}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
}
