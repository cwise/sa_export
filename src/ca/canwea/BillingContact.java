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

public class BillingContact extends Contact {

	@Override
	protected ArrayList<ColumnMapping> getColumnMappings() {
		ArrayList<ColumnMapping> cols = new ArrayList<ColumnMapping>();

		cols.add(new ColumnMapping("lId", "id", ColumnType.LONG));		
		cols.add(new ColumnMapping("sPhone1", "phone1", ColumnType.VARCHAR));
		cols.add(new ColumnMapping("sPhone2", "phone2", ColumnType.VARCHAR));
		cols.add(new ColumnMapping("sFax", "phoneFax", ColumnType.VARCHAR));
		cols.add(new ColumnMapping("sCntcName", "name", ColumnType.VARCHAR));
		cols.add(new ColumnMapping("sEmail", "emailAddress", ColumnType.VARCHAR));

		return cols;
	}

	@Override
	protected String getTableName() {
		return "tCustomr";
	}

	public static ArrayList<Contact> getContacts(Connection conn) throws DatabaseException, ReflectionException {
		String whereClause = "length(sCntcName) > 0";		
		String selectSql = new BillingContact().getSelectSql() + " WHERE " + whereClause;
		ArrayList<Contact> contacts = new ArrayList<Contact>();

		PreparedStatement statement = null;
		try {
			statement = conn.prepareStatement(selectSql);

			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				BillingContact contact = new BillingContact();

				contact.mapResultSet(rs);
				contacts.add(contact);
			}
			rs.close();
			statement.close();
		} catch (SQLException e) {
			throw new DatabaseException(e, "BillingContact", "getContacts");
		}

		return contacts;
	}

	@Override
	protected String getTypeAttribute() {
		return "billing";
	}		
}
