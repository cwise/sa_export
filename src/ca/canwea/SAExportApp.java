package ca.canwea;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.cli.*;

import com.murmurinformatics.exceptions.DatabaseException;
import com.murmurinformatics.exceptions.ReflectionException;

import fr.dyade.koala.xml.domlight.XMLElement;
import fr.dyade.koala.xml.domlight.XMLElementSerializer;


public class SAExportApp {
	private static final String USAGE = "java -jar sa_export.jar [options]";
	private CommandLine cmd = null; 
	private static Options options = null; 
	
	private static final String HELP_OPTION = "h";
	private static final String EXPORT_TYPE_OPTION = "t";
	private static final String DATE_OPTION = "d";
	
	private static final char CUSTOMER_EXPORT = 'c';
	private static final char TRANSACTION_EXPORT = 't';
	private static final char ACCOUNT_EXPORT = 'a';
	
	private Connection conn = null;
	private String hostname;
	private String user;
	private String password;
	private String database;
	private int port;
	
	private char exportType = ' ';
	private String outputFilename = "";
	private String dateFrom = "";
	
	static{
		options = new Options();
		options.addOption(HELP_OPTION, false, "Print help for this application");
		options.addOption(EXPORT_TYPE_OPTION, true, "Export Type [c=customers, t=transactions, a=accounts]");
		options.addOption(DATE_OPTION, true, "Date from");		
	}	
	
	public static void main(String[] args) {
		// validate arguments
		SAExportApp saExportApp = new SAExportApp();
		saExportApp.loadArgs(args);		
		
		// read settings
		saExportApp.loadSettings();
		
		// attempt to connect to database
		saExportApp.connectToDatabase();
		
		switch(saExportApp.exportType) {
		case CUSTOMER_EXPORT:
			saExportApp.exportCustomers();	
			break;
		case TRANSACTION_EXPORT:
			saExportApp.exportTransactions();
			break;
		case ACCOUNT_EXPORT:
			saExportApp.exportAccounts();			
			break;
		}

		saExportApp.disconnectFromDatabase();
	}

	private void exportAccounts() {
		try {
			ArrayList<Account> accounts = Account.getAccounts(conn);
			String timestamp;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddkkmmss");
			XMLElement rootNode = new XMLElement();
			rootNode.setName("accounts");
			
			// add addresses and contacts to customers before getting XML
			for(Account account : accounts)
				rootNode.appendChild(account.getXML(false));
			
			timestamp = sdf.format(new Date());
			exportFile(String.format("accounts_%s.xml", timestamp), rootNode);
		} catch (DatabaseException e) {
			System.out.println(String.format("Database exception: %s", e.getMessage()));
			return;
		} catch (ReflectionException e) {
			System.out.println(String.format("Reflection exception: %s", e.getMessage()));
			return;
		} catch (NoSuchMethodException e) {
			System.err.println(String.format("NoSuchMethodException: " + e.getMessage()));
		} catch (ClassNotFoundException e) {
			System.err.println(String.format("ClassNotFoundException: " + e.getMessage()));
		} catch (IllegalAccessException e) {
			System.err.println(String.format("IllegalAccessException: " + e.getMessage()));
		} catch (InvocationTargetException e) {
			System.err.println(String.format("InvocationTargetException: " + e.getMessage()));
		}
	}

	private void loadArgs(String[] args) {
		CommandLineParser parser = new PosixParser();
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.err.println("Error parsing arguments");
			e.printStackTrace();
			System.exit(1);
		}

		if (cmd.hasOption(HELP_OPTION)) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(USAGE, options);
			System.exit(1);
		}		
		
		if (!cmd.hasOption(EXPORT_TYPE_OPTION)) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(USAGE, options);
			System.exit(1);
		}
		
		if (cmd.hasOption(EXPORT_TYPE_OPTION)){
			String exportTypeString = cmd.getOptionValue(EXPORT_TYPE_OPTION);
			
			if(exportTypeString.length()==1)
				exportType = exportTypeString.charAt(0);
			if(!(exportType==CUSTOMER_EXPORT || exportType==TRANSACTION_EXPORT || exportType==ACCOUNT_EXPORT)) {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("Invalid export type specified", options);
				System.exit(1);
			}
		}

		if (cmd.hasOption(DATE_OPTION)){
			dateFrom = cmd.getOptionValue(DATE_OPTION);
		}
	}	
	
	private void loadSettings() {
		Properties properties = new Properties();
		try {
		    properties.load(new FileInputStream("sa_export.properties"));
		} catch (IOException e) {
			System.err.print("Failed to locate sa_export.properties");
			System.exit(1);
		}
		
		hostname = properties.getProperty("hostname", "localhost");
		user = properties.getProperty("user", "sysadmin");
		password = properties.getProperty("password", "");
		database = properties.getProperty("database", "simply");
		port = new Integer(properties.getProperty("port", "13541")).intValue();
	}
	
	private void connectToDatabase() {
		String connectString;
		String fmt = "jdbc:mysql://%s:%d/%s";
				
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.err.print("Failed to load the MySQL JDBC Driver. Make sure the jar is in the lib directory");
			System.exit(1);
		}

		connectString = String.format(fmt, hostname, port, database);
		try {
			conn = DriverManager.getConnection(connectString, user, password);
		} catch (SQLException e) {
			System.err.print(String.format("Failed to connect to database: %s", e.getMessage()));
			System.exit(1);
		}		
		
		System.out.println("Connected to database");
	}
	
	private void exportTransactions() {
		try {
			ArrayList<JournalEntry> entries = JournalEntry.getJournalEntries(conn);
			String timestamp;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddkkmmss");
			XMLElement rootNode = new XMLElement();
			rootNode.setName("journal_entries");
			
			// add addresses and contacts to customers before getting XML
			for(JournalEntry je : entries)
				rootNode.appendChild(je.getXML(false));
			
			timestamp = sdf.format(new Date());
			exportFile(String.format("transactions_%s.xml", timestamp), rootNode);
		} catch (DatabaseException e) {
			System.out.println(String.format("Database exception: %s", e.getMessage()));
			return;
		} catch (ReflectionException e) {
			System.out.println(String.format("Reflection exception: %s", e.getMessage()));
			return;
		} catch (NoSuchMethodException e) {
			System.err.println(String.format("NoSuchMethodException: " + e.getMessage()));
		} catch (ClassNotFoundException e) {
			System.err.println(String.format("ClassNotFoundException: " + e.getMessage()));
		} catch (IllegalAccessException e) {
			System.err.println(String.format("IllegalAccessException: " + e.getMessage()));
		} catch (InvocationTargetException e) {
			System.err.println(String.format("InvocationTargetException: " + e.getMessage()));
		}
	}

	private void exportCustomers() {
		try {
			ArrayList<Customer> customers = Customer.getCustomers(conn);
			ArrayList<Address> billingAddresses = BillingAddress.getAddresses(conn);
			ArrayList<Contact> billingContacts = BillingContact.getContacts(conn);
			ArrayList<Address> shippingAddresses = ShippingAddress.getAddresses(conn);
			ArrayList<Contact> shippingContacts = ShippingContact.getContacts(conn);
			String timestamp;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddkkmmss");
			XMLElement rootNode = new XMLElement();
			rootNode.setName("customers");
			
			// add addresses and contacts to customers before getting XML
			for(Customer customer : customers) {
				customer.addAddresses(billingAddresses);
				customer.addContacts(billingContacts);
				customer.addAddresses(shippingAddresses);
				customer.addContacts(shippingContacts);				
				rootNode.appendChild(customer.getXML(false));
			}
			
			timestamp = sdf.format(new Date());
			exportFile(String.format("customers_%s.xml", timestamp), rootNode);
		} catch (DatabaseException e) {
			System.out.println(String.format("Database exception: %s", e.getMessage()));
			return;
		} catch (ReflectionException e) {
			System.out.println(String.format("Reflection exception: %s", e.getMessage()));
			return;
		} catch (NoSuchMethodException e) {
			System.err.println(String.format("NoSuchMethodException: " + e.getMessage()));
		} catch (ClassNotFoundException e) {
			System.err.println(String.format("ClassNotFoundException: " + e.getMessage()));
		} catch (IllegalAccessException e) {
			System.err.println(String.format("IllegalAccessException: " + e.getMessage()));
		} catch (InvocationTargetException e) {
			System.err.println(String.format("InvocationTargetException: " + e.getMessage()));
		}
	}
	
	private void disconnectFromDatabase() {
		try {
			conn.close();
		} catch (SQLException e) {
			System.err.print(String.format("Error disconnecting from database: %s", e.getMessage()));
			System.exit(1);
		}
		System.out.println("Disconnected from database");
	}	
	
	protected void exportFile(String fileName, XMLElement rootNode) {
		File xmlFile = new File(fileName);
		FileOutputStream fs;
		try {
			fs = new FileOutputStream(xmlFile);
			xmlToStream(fs, rootNode);
			fs.close();				
		} catch (FileNotFoundException e) {
			System.err.println(String.format("FileNotFoundException: " + e.getMessage()));
		} catch (IOException e) {
			System.err.println(String.format("IOException: " + e.getMessage()));
		}
	}

	private void xmlToStream(FileOutputStream fs, XMLElement rootNode) {
		XMLElementSerializer ser = new XMLElementSerializer(fs);

		try {			
			ser.writeMetaData("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			ser.writeXMLElement(rootNode);
			ser.flush();
			ser.close();
		} catch (IOException e) {
			System.err.println(String.format("IOException: " + e.getMessage()));
		} catch (SecurityException e) {
			System.err.println(String.format("SecurityException: " + e.getMessage()));
		} catch (IllegalArgumentException e) {
			System.err.println(String.format("IllegalArgumentException: " + e.getMessage()));
		} 
	}	
}
