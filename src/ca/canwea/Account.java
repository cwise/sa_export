package ca.canwea;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.murmurinformatics.db.AbstractEntity;
import com.murmurinformatics.db.ColumnMapping;
import com.murmurinformatics.db.ColumnType;
import com.murmurinformatics.exceptions.DatabaseException;
import com.murmurinformatics.exceptions.ReflectionException;

import fr.dyade.koala.xml.domlight.XMLElement;

public class Account extends AbstractEntity {
	private String name;
	private String acctNumber;
	
	public static ArrayList<Account> getAccounts(Connection conn) throws DatabaseException, ReflectionException {
		String selectSql = new Account().getSelectSql();
		ArrayList<Account> accounts = new ArrayList<Account>();

		PreparedStatement statement = null;
		try {
			statement = conn.prepareStatement(selectSql);

			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				Account account = new Account();

				account.mapResultSet(rs);
				accounts.add(account);
			}
			rs.close();
			statement.close();
		} catch (SQLException e) {
			throw new DatabaseException(e, "Account", "getAccounts");
		}

		return accounts;
	}

	@Override
	protected ArrayList<ColumnMapping> getColumnMappings() {
		ArrayList<ColumnMapping> cols = new ArrayList<ColumnMapping>();

		cols.add(new ColumnMapping("lId", "acctNumber", ColumnType.VARCHAR));
		cols.add(new ColumnMapping("sName", "name", ColumnType.VARCHAR));
		
		return cols;
	}

	@Override
	protected String getTableName() {
		return "tAccount";
	}

	@Override
	protected String getTypeAttribute() {
		return null;
	}

	@Override
	protected String getXMLTag() {
		return "account";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setAcctNumber(String acctNumber) {
		this.acctNumber = acctNumber;
	}

	public String getAcctNumber() {
		return acctNumber;
	}

	@Override
	protected ArrayList<XMLElement> getChildXMLElements()
			throws SecurityException, IllegalArgumentException,
			NoSuchMethodException, ClassNotFoundException,
			IllegalAccessException, InvocationTargetException {
		ArrayList<XMLElement> emptyList = new ArrayList<XMLElement>();
		
		return emptyList;
	}

}
