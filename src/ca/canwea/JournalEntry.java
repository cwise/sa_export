package ca.canwea;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.murmurinformatics.db.AbstractEntity;
import com.murmurinformatics.db.ColumnMapping;
import com.murmurinformatics.db.ColumnSubquery;
import com.murmurinformatics.db.ColumnType;
import com.murmurinformatics.exceptions.DatabaseException;
import com.murmurinformatics.exceptions.ReflectionException;

import ca.murmurinfo.domlight.XMLElement;

public class JournalEntry extends AbstractEntity {
	private Date entryDate;
	private long acctId;
	private long custId;
	private long amount;
	private String invoice;
	private long projId;
	private String fy;
	
	public JournalEntry(String fy) {
		this.fy = fy;
	}

	@Override
	protected ArrayList<XMLElement> getChildXMLElements()
			throws SecurityException, IllegalArgumentException,
			NoSuchMethodException, ClassNotFoundException,
			IllegalAccessException, InvocationTargetException {
		ArrayList<XMLElement> emptyList = new ArrayList<XMLElement>();
		
		return emptyList;
	}

	@Override
	protected ArrayList<ColumnMapping> getColumnMappings() {
		ArrayList<ColumnMapping> cols = new ArrayList<ColumnMapping>();

		ColumnSubquery dateSubquery = new ColumnSubquery("dtJourDate", "dtJourDate", getJETableName(), "lId", getTableName(), "lJEntId");
		ColumnSubquery custIdSubquery = new ColumnSubquery("lRecId", "lRecId", getJETableName(), "lId", getTableName(), "lJEntId");
		ColumnSubquery sourceSubquery = new ColumnSubquery("sSource", "sSource", getJETableName(), "lId", getTableName(), "lJEntId");
		ColumnSubquery projectSubquery = new ColumnSubquery("lPrjId", "lPrjId", getJEProjectTableName(), "lJEntId", getTableName(), "lJEntId");
		
		cols.add(new ColumnMapping("lJEntId", "id", ColumnType.LONG));		
		cols.add(new ColumnMapping("dtJourDate", "entryDate", ColumnType.DATE, dateSubquery));
		cols.add(new ColumnMapping("lAcctId", "acctId", ColumnType.LONG));
		cols.add(new ColumnMapping("lRecId", "custId", ColumnType.LONG, custIdSubquery));
		cols.add(new ColumnMapping("dAmount", "amount", ColumnType.LONG));
		cols.add(new ColumnMapping("sSource", "invoice", ColumnType.VARCHAR, sourceSubquery));
		cols.add(new ColumnMapping("lPrjId", "projId", ColumnType.LONG, projectSubquery));

		return cols;
	}

	@Override
	protected String getTableName() {
		String tableName = "tJEntAct";
		
		if(fy.equals("current"))
			tableName = "tJEntAct";
		else if(fy.equals("last"))
			tableName = "tJEntLYA";
		else
			tableName = String.format("tJEAH%s", fy);
		
		return tableName;
	}

	protected String getJETableName() {
		String tableName = "tJourEnt";

		if(fy.equals("current"))
			tableName = "tJourEnt";
		else if(fy.equals("last"))
			tableName = "tJEntLY";
		else
			tableName = String.format("tJEH%s", fy);		
	
		return tableName;
	}	
	
	protected String getJEProjectTableName() {
		String tableName = "tJEntPrj";

		if(fy.equals("current"))
			tableName = "tJEntPrj";
		else if(fy.equals("last"))
			tableName = "tJEntLYP";
		else
			tableName = String.format("tJEPH%s", fy);		
			
		return tableName;
	}		
	
	@Override
	protected String getTypeAttribute() {
		return null;
	}

	@Override
	protected String getXMLTag() {
		return "journal_entry";
	}

	public Date getEntryDate() {
		return entryDate;
	}

	public void setEntryDate(Date entryDate) {
		this.entryDate = entryDate;
	}

	public long getAcctId() {
		return acctId;
	}

	public void setAcctId(Long acctId) {
		this.acctId = acctId.longValue();
	}

	public long getCustId() {
		return custId;
	}

	public void setCustId(Long custId) {
		this.custId = custId.longValue();
	}

	public long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount.longValue();
	}

	public String getInvoice() {
		return invoice;
	}

	public void setInvoice(String invoice) {
		this.invoice = invoice;
	}

	public static ArrayList<JournalEntry> getJournalEntries(Connection conn, String fy, String dateFrom) throws DatabaseException, ReflectionException {
		if(fy.isEmpty())
			fy="current";
		
		JournalEntry baseJE = new JournalEntry(fy);
		
		String whereClause = "lJEntId IN (SELECT lId FROM " + baseJE.getJETableName() + " WHERE nModule = 2 AND nType = 1) AND lAcctId LIKE '4%'";
		String selectSql = baseJE.getSelectSql() + " WHERE " + whereClause;
		
		// TODO: add date filter
		
		System.out.println(selectSql);
		ArrayList<JournalEntry> entries = new ArrayList<JournalEntry>();
		
		PreparedStatement statement = null;
		try {
			statement = conn.prepareStatement(selectSql);

			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				JournalEntry entry = new JournalEntry(fy);

				entry.mapResultSet(rs);
				entries.add(entry);
			}
			rs.close();
			statement.close();
		} catch (SQLException e) {
			throw new DatabaseException(e, "JournalEntry", "getJournalEntries");
		}

		return entries;
	}

	public void setProjId(Long projId) {
		this.projId = projId.longValue();
	}

	public long getProjId() {
		return projId;
	}
}
