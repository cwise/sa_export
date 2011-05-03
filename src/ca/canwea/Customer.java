package ca.canwea;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import com.murmurinformatics.db.AbstractEntity;
import com.murmurinformatics.db.ColumnMapping;
import com.murmurinformatics.db.ColumnType;
import com.murmurinformatics.exceptions.DatabaseException;
import com.murmurinformatics.exceptions.ReflectionException;

import ca.murmurinfo.domlight.XMLElement;

public class Customer extends AbstractEntity {
	private String name;
	private String website;
	private ArrayList<Address> addresses = new ArrayList<Address>();
	private ArrayList<Contact> contacts = new ArrayList<Contact>();
	
	@Override
	protected ArrayList<ColumnMapping> getColumnMappings() {
		ArrayList<ColumnMapping> cols = new ArrayList<ColumnMapping>();

		cols.add(new ColumnMapping("lId", "id", ColumnType.LONG));
		cols.add(new ColumnMapping("sName", "name", ColumnType.VARCHAR));
		cols.add(new ColumnMapping("sWebSite", "website", ColumnType.VARCHAR));
		
		return cols;
	}

	@Override
	protected String getTableName() {
		return "tCustomr";
	}
	
	@Override
	protected String getXMLTag() {
		return "customer";
	}		
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public static ArrayList<Customer> getCustomers(Connection conn) throws DatabaseException, ReflectionException {
		String selectSql = new Customer().getSelectSql();
		String whereClause = " WHERE dtLastSal >= str_to_date((YEAR(CurDate())-2) + '-01-01', '%Y-%m-%d')";
		ArrayList<Customer> customers = new ArrayList<Customer>();

		PreparedStatement statement = null;
		try {
			statement = conn.prepareStatement(selectSql + whereClause);

			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				Customer customer = new Customer();

				customer.mapResultSet(rs);
				customers.add(customer);
			}
			rs.close();
			statement.close();
		} catch (SQLException e) {
			throw new DatabaseException(e, "Customer", "getCustomers");
		}

		return customers;
	}	
	
	public void addAddresses(ArrayList<Address> addresses) {
		Iterator<Address> it = addresses.iterator();
		
		while(it.hasNext()) {
			Address address = it.next();
			
			if(address.getId()==this.id)
				this.addresses.add(address);
		}
	}
	
	public void addContacts(ArrayList<Contact> contacts) {
		Iterator<Contact> it = contacts.iterator();
		
		while(it.hasNext()) {
			Contact contact = it.next();
			
			if(contact.getId()==this.id)
				this.contacts.add(contact);
		}
	}
	
	@Override
	protected ArrayList<XMLElement> getChildXMLElements() throws SecurityException, IllegalArgumentException, NoSuchMethodException, ClassNotFoundException, IllegalAccessException, InvocationTargetException {
		ArrayList<XMLElement> childNodes = new ArrayList<XMLElement>();
		
		for(Address address : addresses)
			childNodes.add(address.getXML(false));
		for(Contact contact : contacts)
			childNodes.add(contact.getXML(false));		
		
		return childNodes;
	}

	@Override
	protected String getTypeAttribute() {
		return null;
	}	
}
