package ca.canwea;

import java.util.ArrayList;

import com.murmurinformatics.db.AbstractEntity;

import fr.dyade.koala.xml.domlight.XMLElement;

public abstract class Contact extends AbstractEntity {
	private long saCompanyId;
	private String name;
	private String emailAddress;
	private String phone1;
	private String phone2;
	private String phoneFax;
	
	@Override
	protected String getXMLTag() {
		return "contact";
	}			
	
	public long getSaCompanyId() {
		return saCompanyId;
	}
	public void setSaCompanyId(Long saCompanyId) {
		this.saCompanyId = saCompanyId.longValue();
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	public String getPhone1() {
		return phone1;
	}
	public void setPhone1(String phone1) {
		this.phone1 = phone1;
	}
	public String getPhone2() {
		return phone2;
	}
	public void setPhone2(String phone2) {
		this.phone2 = phone2;
	}
	public String getPhoneFax() {
		return phoneFax;
	}
	public void setPhoneFax(String phoneFax) {
		this.phoneFax = phoneFax;
	}
	
	@Override
	protected ArrayList<XMLElement> getChildXMLElements() {
		ArrayList<XMLElement> emptyList = new ArrayList<XMLElement>();
		
		return emptyList;
	}		
}

