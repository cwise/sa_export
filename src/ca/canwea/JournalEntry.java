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

import fr.dyade.koala.xml.domlight.XMLElement;

public class JournalEntry extends AbstractEntity {
	private Date entryDate;
	private long acctId;
	private long custId;
	private long amount;
	private String invoice;
	
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

		ColumnSubquery dateSubquery = new ColumnSubquery("dtJourDate", "dtJourDate", "tJourEnt", "lJEntId", "lId");
		ColumnSubquery custIdSubquery = new ColumnSubquery("lRecId", "lRecId", "tJourEnt", "lJEntId", "lId");
		ColumnSubquery sourceSubquery = new ColumnSubquery("sSource", "sSource", "tJourEnt", "lJEntId", "lId");
		
		cols.add(new ColumnMapping("lJEntId", "id", ColumnType.LONG));		
		cols.add(new ColumnMapping("dtJourDate", "entryDate", ColumnType.DATE, dateSubquery));
		cols.add(new ColumnMapping("lAcctId", "acctId", ColumnType.LONG));
		cols.add(new ColumnMapping("lRecId", "custId", ColumnType.LONG, custIdSubquery));
		cols.add(new ColumnMapping("dAmount", "amount", ColumnType.LONG));
		cols.add(new ColumnMapping("sSource", "invoice", ColumnType.VARCHAR, sourceSubquery));

		return cols;
	}

	@Override
	protected String getTableName() {
		return "tJEntAct";
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

	// TODO: add date support
	public static ArrayList<JournalEntry> getJournalEntries(Connection conn) throws DatabaseException, ReflectionException {
		String whereClause = "lJEntId IN (SELECT lId FROM tJourEnt WHERE nModule = 2 AND nType = 1)";
		String selectSql = new JournalEntry().getSelectSql() + " WHERE " + whereClause;
		System.out.println(selectSql);
		ArrayList<JournalEntry> entries = new ArrayList<JournalEntry>();

		PreparedStatement statement = null;
		try {
			statement = conn.prepareStatement(selectSql);

			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				JournalEntry entry = new JournalEntry();

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
}
