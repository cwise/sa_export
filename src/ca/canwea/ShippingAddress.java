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

public class ShippingAddress extends Address {
	@Override
	protected String getTableName() {
		return "tCustShp";
	}

	@Override
	protected ArrayList<ColumnMapping> getColumnMappings() {
		ArrayList<ColumnMapping> cols = new ArrayList<ColumnMapping>();
	
		cols.add(new ColumnMapping("lCustId", "id", ColumnType.LONG));
		cols.add(new ColumnMapping("sShipStrt1", "street1", ColumnType.VARCHAR));
		cols.add(new ColumnMapping("sShipStrt2", "street2", ColumnType.VARCHAR));
		cols.add(new ColumnMapping("sShipCity", "city", ColumnType.VARCHAR));
		cols.add(new ColumnMapping("sShipPrvSt", "region", ColumnType.VARCHAR));
		cols.add(new ColumnMapping("sShipCnty", "country", ColumnType.VARCHAR));
		cols.add(new ColumnMapping("sShipPstZp", "postalCode", ColumnType.VARCHAR));
		
		return cols;
	}
	
	public static ArrayList<Address> getAddresses(Connection conn) throws DatabaseException, ReflectionException {
		String whereClause = "length(sShipStrt1) > 0 OR length(sShipStrt2) > 0";
		String selectSql = new ShippingAddress().getSelectSql() + " WHERE " + whereClause;
		ArrayList<Address> addresses = new ArrayList<Address>();

		PreparedStatement statement = null;
		try {
			statement = conn.prepareStatement(selectSql);

			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				ShippingAddress address = new ShippingAddress();

				address.mapResultSet(rs);
				addresses.add(address);
			}
			rs.close();
			statement.close();
		} catch (SQLException e) {
			throw new DatabaseException(e, "Address", "getShippingAddresses");
		}

		return addresses;
	}

	@Override
	protected String getTypeAttribute() {
		return "shipping";
	}		
}
