package ca.canwea;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import ca.murmurinfo.domlight.XMLElement;

import com.murmurinformatics.db.AbstractEntity;
import com.murmurinformatics.db.ColumnMapping;
import com.murmurinformatics.db.ColumnType;
import com.murmurinformatics.exceptions.DatabaseException;
import com.murmurinformatics.exceptions.ReflectionException;

public class Project extends AbstractEntity {
	private String name;
	
	public static ArrayList<Project> getProjects(Connection conn) throws DatabaseException, ReflectionException {
		String selectSql = new Project().getSelectSql();
		ArrayList<Project> projects = new ArrayList<Project>();

		PreparedStatement statement = null;
		try {
			statement = conn.prepareStatement(selectSql);

			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				Project project = new Project();

				project.mapResultSet(rs);
				projects.add(project);
			}
			rs.close();
			statement.close();
		} catch (SQLException e) {
			throw new DatabaseException(e, "Project", "getProject");
		}

		return projects;
	}

	@Override
	protected ArrayList<ColumnMapping> getColumnMappings() {
		ArrayList<ColumnMapping> cols = new ArrayList<ColumnMapping>();

		cols.add(new ColumnMapping("lId", "id", ColumnType.LONG));
		cols.add(new ColumnMapping("sName", "name", ColumnType.VARCHAR));
		
		return cols;
	}

	@Override
	protected String getTableName() {
		return "tProject";
	}

	@Override
	protected String getTypeAttribute() {
		return null;
	}

	@Override
	protected String getXMLTag() {
		return "project";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
