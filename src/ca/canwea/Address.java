package ca.canwea;

import java.util.ArrayList;

import com.murmurinformatics.db.AbstractEntity;

import ca.murmurinfo.domlight.XMLElement;

public abstract class Address extends AbstractEntity {
	private String street1;
	private String street2;
	private String city;
	private String region;
	private String country;
	private String postalCode;
	
	public Address(long id) {
		super(id);
	}

	public Address() {
		super();
	}

	@Override
	protected String getXMLTag() {
		return "address";
	}			
	
	public String getStreet1() {
		return street1;
	}

	public void setStreet1(String street1) {
		this.street1 = street1;
	}

	public String getStreet2() {
		return street2;
	}

	public void setStreet2(String street2) {
		this.street2 = street2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	
	@Override
	protected ArrayList<XMLElement> getChildXMLElements() {
		ArrayList<XMLElement> emptyList = new ArrayList<XMLElement>();
		
		return emptyList;
	}	
}