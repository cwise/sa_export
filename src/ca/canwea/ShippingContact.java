package ca.canwea;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.murmurinformatics.db.ColumnMapping;
import com.murmurinformatics.db.ColumnType;
import com.murmurinformatics.exceptions.DatabaseException;
import com.murmurinformatics.exceptions.ReflectionException;

public class ShippingContact extends Contact {

	@Override
	protected ArrayList<ColumnMapping> getColumnMappings() {
		ArrayList<ColumnMapping> cols = new ArrayList<ColumnMapping>();

		cols.add(new ColumnMapping("lCustId", "saCompanyId", ColumnType.LONG));		
		cols.add(new ColumnMapping("sShipPhn1", "phone1", ColumnType.VARCHAR));
		cols.add(new ColumnMapping("sShipPhn2", "phone2", ColumnType.VARCHAR));
		cols.add(new ColumnMapping("sShipFax", "phoneFax", ColumnType.VARCHAR));
		cols.add(new ColumnMapping("sShipCntc", "name", ColumnType.VARCHAR));
		cols.add(new ColumnMapping("sShipEmail", "emailAddress", ColumnType.VARCHAR));

		return cols;
	}

	@Override
	protected String getTableName() {
		return "tCustShp";
	}

	public static ArrayList<Contact> getContacts(Connection conn) throws DatabaseException, ReflectionException {
		String whereClause = "length(sShipCntc) > 0";		
		String selectSql = new ShippingContact().getSelectSql() + " WHERE " + whereClause;
		ArrayList<Contact> contacts = new ArrayList<Contact>();

		PreparedStatement statement = null;
		try {
			statement = conn.prepareStatement(selectSql);

			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				ShippingContact contact = new ShippingContact();

				contact.mapResultSet(rs);
				contacts.add(contact);
			}
			rs.close();
			statement.close();
		} catch (SQLException e) {
			throw new DatabaseException(e, "ShippingContact", "getContacts");
		}

		return contacts;
	}		
	
	@Override
	protected String getTypeAttribute() {
		return "shipping";
	}			
}
