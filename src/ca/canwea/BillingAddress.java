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

import fr.dyade.koala.xml.domlight.XMLElement;

public class BillingAddress extends Address {
	@Override
	protected String getTableName() {
		return "tCustomr";
	}
	
	@Override
	protected String getXMLTag() {
		return "address";
	}			

	@Override
	protected ArrayList<ColumnMapping> getColumnMappings() {
		ArrayList<ColumnMapping> cols = new ArrayList<ColumnMapping>();
	
		cols.add(new ColumnMapping("lId", "saCompanyId", ColumnType.LONG));
		cols.add(new ColumnMapping("sStreet1", "street1", ColumnType.VARCHAR));
		cols.add(new ColumnMapping("sStreet2", "street2", ColumnType.VARCHAR));
		cols.add(new ColumnMapping("sCity", "city", ColumnType.VARCHAR));
		cols.add(new ColumnMapping("sProvState", "region", ColumnType.VARCHAR));
		cols.add(new ColumnMapping("sCountry", "country", ColumnType.VARCHAR));
		cols.add(new ColumnMapping("sPostalZip", "postalCode", ColumnType.VARCHAR));
		
		return cols;
	}
	
	public static ArrayList<Address> getAddresses(Connection conn) throws DatabaseException, ReflectionException {
		String whereClause = "length(sStreet1) > 0 OR length(sStreet2) > 0 OR length(sCity) > 0";
		String selectSql = new BillingAddress().getSelectSql() + " WHERE " + whereClause;
		ArrayList<Address> addresses = new ArrayList<Address>();

		PreparedStatement statement = null;
		try {
			statement = conn.prepareStatement(selectSql);

			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				BillingAddress address = new BillingAddress();

				address.mapResultSet(rs);
				addresses.add(address);
			}
			rs.close();
			statement.close();
		} catch (SQLException e) {
			throw new DatabaseException(e, "Address", "getBillingAddresses");
		}

		return addresses;
	}

	@Override
	protected String getTypeAttribute() {
		return "billing";
	}	
}
