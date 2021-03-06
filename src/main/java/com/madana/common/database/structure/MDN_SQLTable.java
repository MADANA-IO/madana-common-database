/*******************************************************************************
 * Copyright (C) 2018 MADANA
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * @organization:MADANA
 * @author:Jean-Fabian Wenisch
 * @contact:dev@madana.io
 ******************************************************************************/
package com.madana.common.database.structure;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import com.madana.common.database.MDN_SQLConnector;
/**
 * Klasse zur Verwaltung einer Tabelle in der Datenbank
 * @author Jean
 * @since 01.04.2017
 */
public class MDN_SQLTable
{
	private String strName;
	/**
	 * 
	 * @param strName - Name der Tabelle
	 * @author Jean
	 * @since 01.04.2017
	 */
	public MDN_SQLTable(String strName)
	{
		this.strName=strName;
	}
	/**
	 * Fügt einen neuen Datensatz in die Tabelle ein, der Primary Key wird unter Nutzung von getCount() automatisch gesetzt
	 * @param oValues ArrayList<String> der einzufügenden Werte
	 * @throws SQLException
	 * @author J.-Fabian Wenisch
	 * @since 09.04.2017
	 */
	public void addEntry(ArrayList<String> oValues) throws SQLException
	{
		String strValues="";
		for(int i=0; i < oValues.size();i++)
			strValues+="'"+oValues.get(i)+"',";
		strValues= strValues.substring(0, strValues.length()-1);
		MDN_SQLConnector.execute("INSERT INTO "+strName+" VALUES ("+strValues+");" );
	}
	/**
	 * Fügt einen neuen Datensatz in die Tabelle ein, der Primary Key wird unter Nutzung von getCount() automatisch gesetzt
	 * @param oValues ArrayList<String> der einzufügenden Werte
	 * @throws SQLException
	 * @author J.-Fabian Wenisch
	 * @since 09.04.2017
	 */
	public void addEntry(List<Object> oValues) throws SQLException
	{
		List <String> oColumnNames = getColumnNames();
		String strColumNames="";
		for(int i=1; i < oColumnNames.size(); i++)
		{
			strColumNames+=oColumnNames.get(i)+", ";
		}
		strColumNames=strColumNames.substring(0, strColumNames.length()-2);
		String strValues="";
		for(int i=0; i < oValues.size();i++)
		{
			strValues+="?, ";
		}
		strValues= strValues.substring(0, strValues.length()-2);
		PreparedStatement oStatement = MDN_SQLConnector.connection.prepareStatement("INSERT INTO "+strName+" ("+strColumNames+")VALUES ("+strValues+")");

		try
		{
			for(int i=0; i < oValues.size();i++)
			{
				if(oValues.get(i) instanceof byte[])
				{
					oStatement.setBytes(i+1, (byte[]) oValues.get(i));

				}
				else if(oValues.get(i) instanceof java.sql.Timestamp)
				{
					oStatement.setTimestamp(i+1, ( java.sql.Timestamp) oValues.get(i));

				}
				else //if(oValues.get(i) instanceof String)
				{
					oStatement.setString(i+1,  (String) oValues.get(i));
				}

			}

		 oStatement.executeUpdate();
		}
		finally
		{
			oStatement.close();
		}

	}
	/**
	 * Löscht den Eintrag mit der ID aus der Tablle
	 * @param string - Primary ID
	 * @throws SQLException
	 * @author J.-Fabian Wenisch
	 * @since 09.04.2017
	 */
	public void deleteEntry(String string) throws SQLException
	{
		MDN_SQLConnector.execute("DELETE FROM "+strName + " WHERE "+getColumnNames().get(0)+" = "+ string);
	}

	public void changeEntry(String strID, String strColumn, String strValue) throws SQLException
	{
		MDN_SQLConnector.execute("UPDATE "+ strName+ " SET "+ strColumn+"= '"+strValue+"' WHERE "+getColumnNames().get(0)+"="+strID );
	}
	public void changeEntry(String strUserID, String strColumn, byte[] bBytes) throws SQLException 
	{
		PreparedStatement oStatement = MDN_SQLConnector.connection.prepareStatement("UPDATE "+strName+" set "+strColumn+" =? where id ="+strUserID);
		oStatement.setBytes(1, bBytes);
		oStatement.executeUpdate();
		oStatement.close();

	}
	/**
	 * Gibt den zuerst gefunden Primärschlüssel aus der Tabelle zurück
	 * @param strColumName - Spalte in der Tabelle
	 * @param strValue - Wert in der Tabelle
	 * @return
	 * @throws SQLException
	 * @author Jean
	 * @since 01.04.2017
	 */
	public String getID(String strColumName, String strValue) throws SQLException
	{
		String strId = "";
		ResultSet oSet=	 MDN_SQLConnector.executeQuery("SELECT * FROM "+strName+ " WHERE "+strColumName+" = '"+ strValue+"'");
		if(oSet.next())
			strId=oSet.getString(1);
		oSet.close();
		return strId;
	}
	public Object getEntryByKey(String strKeyColumn, String strKey, String strColumn) throws SQLException
	{
		ResultSet oSet=	 MDN_SQLConnector.executeQuery("SELECT * FROM "+strName+ " WHERE "+strKeyColumn+" = '"+ strKey+"'");
		ResultSetMetaData rsmd=oSet.getMetaData();
		if(rsmd.getColumnTypeName(oSet.findColumn(strColumn)).toUpperCase().equals("BLOB"))
		{
			oSet.next();
			Blob oBlob = oSet.getBlob(oSet.findColumn(strColumn));
			return oBlob.getBytes(1L, (int)oBlob.length());
		}
		oSet.next();
		String entry = oSet.getString(oSet.findColumn(strColumn));
		oSet.close();
		return entry;
	}
	/**
	 * Gibt die Einträge zurück die in der übergebenen Spalte die entsprechenden Werte haben
	 * @param strColumName - Name der Spalte
	 * @param strValue - Wert in der SPalte
	 * @return ResultSet
	 * @throws SQLException
	 * @author J.-Fabian Wenisch
	 * @since 09.04.2017
	 */
	public List<String> getEntries(String strColumName, String strValue) throws SQLException
	{
		List<String> oValues = new ArrayList<String>();
		ResultSet oSet=	 MDN_SQLConnector.executeQuery("SELECT * FROM "+strName+ " WHERE "+strColumName+" = '"+ strValue+"'");
		while(oSet.next())
			oValues.add(oSet.getString(1));
		oSet.close();
		return oValues;
	}

	/**
	 * Gibt alle Einträge aus einer Spalte zurück
	 * @param strColumnName
	 * @return
	 * @throws SQLException
	 * @author Jean
	 * @since 01.04.2017
	 */
	public List<String> getEntries(String strColumnName) throws SQLException
	{
		List<String> oValues = new ArrayList<String>();
		ResultSet oSet=		 MDN_SQLConnector.executeQuery("SELECT "+strColumnName+" FROM "+strName+ " WHERE "+strColumnName +" is not NULL");
		while(oSet.next())
			oValues.add(oSet.getString(1));
		oSet.close();
		return oValues;

	}
	/**
	 * Gibt die Anzahl der Datensätze in der Tabelle zurück
	 * @return Long
	 * @throws SQLException
	 * @author J.-Fabian Wenisch
	 * @since 09.04.2017
	 */
	public Long getRowCount() throws SQLException
	{
		ResultSet rs = MDN_SQLConnector.executeQuery("SELECT count(*) FROM "+strName);
		rs.next();
		long zeilen = rs.getLong(1);
		rs.close();
		return zeilen;
	}
	/**
	 * Liest alle Fremdschlüssel aus einer Tabelle aus
	 * @return
	 * @throws SQLException
	 * @author Jean
	 * @since 01.04.2017
	 */
	public List <MDN_SQLForeignKey> getForeignKeys() throws SQLException
	{
		List <MDN_SQLForeignKey> oForeignKeys = new ArrayList<MDN_SQLForeignKey>();
		ResultSet oSet=		 MDN_SQLConnector.executeQuery("SELECT COLUMN_NAME,REFERENCED_TABLE_NAME,REFERENCED_COLUMN_NAME from INFORMATION_SCHEMA.KEY_COLUMN_USAGE where TABLE_SCHEMA = '"+MDN_SQLConnector.DATABASE_NAME+"' and TABLE_NAME = '"+strName+"'and referenced_column_name is not NULL;");
		while(oSet.next())
			oForeignKeys.add(new MDN_SQLForeignKey(strName,oSet));
		oSet.close();
		return oForeignKeys;
	}
	/**
	 * Gibt die Anzahl der Datensätze in der Tabelle zurück
	 * @return Long
	 * @throws SQLException
	 * @author J.-Fabian Wenisch
	 * @since 09.04.2017
	 */
	public Long getColumnCount() throws SQLException
	{
		ResultSet rs = MDN_SQLConnector.executeQuery("SELECT count(*) FROM information_schema.columns WHERE table_schema = '"+MDN_SQLConnector.DATABASE_NAME+"' AND TABLE_NAME = '"+strName+"'");
		rs.next();
		long zeilen = rs.getLong(1);
		return zeilen;
	}
	/**
	 * Gibt die Namen aller Spalten zurück
	 * @return List<String>
	 * @throws SQLException
	 * @author J.-Fabian Wenisch
	 * @since 09.04.2017
	 */
	public List<String> getColumnNames() throws SQLException
	{
		List<String> oColumns = new ArrayList<String>();
		ResultSet rs = MDN_SQLConnector.executeQuery("SELECT * FROM "+strName+"  LIMIT 0, 1");
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		for (int i = 1; i <= columnCount; i++ ) {
			oColumns.add(rsmd.getColumnName(i));
		}
		rs.close();
		return oColumns;
	}
	/**
	 * Gibt alle Spalten aus der Tabelle zurück
	 * @return
	 * @throws SQLException
	 * @author Jean
	 * @since 01.04.2017
	 */
	public List <MDN_SQLColumn> getColumns() throws SQLException
	{
		List <MDN_SQLColumn> oColumns = new ArrayList<MDN_SQLColumn>();
		List<MDN_SQLForeignKey> oForeignKeys = getForeignKeys();
		List<String> oColumnNames = getColumnNames();

		for(int i=0; i < oColumnNames.size(); i++)
		{
			oColumns.add(new MDN_SQLColumn(oColumnNames.get(i)));
		}
		for(int i=0; i < oColumns.size(); i++)
		{
			MDN_SQLColumn oCurrentColumn = oColumns.get(i);
			for(int j=0; j < oForeignKeys.size(); j++)
			{
				if(oCurrentColumn.getName().equals(oForeignKeys.get(j).getColumName()))
				{
					oCurrentColumn.setForeignKey(oForeignKeys.get(j));
					break;
				}
			}
		}

		return oColumns;
	}
	/**
	 * Löscht alle Datensätze in der Tabelle
	 * @throws SQLException
	 * @author J.-Fabian Wenisch
	 * @since 09.04.2017
	 */
	public void clear() throws SQLException
	{
		MDN_SQLConnector.execute("TRUNCATE TABLE "+strName);

	}
	/**
	 * Liest alle Datensätze aus der Tabelle aus
	 * @return
	 * @throws SQLException
	 * @author Jean
	 * @since 01.04.2017
	 */
	public ResultSet getData() throws SQLException
	{
		return MDN_SQLConnector.executeQuery("SELECT * FROM "+strName);
	}
	/**
	 * Liest die in ./conf/ liegende Default Datei aus um default-Datensätze anzulegen
	 * @throws FileNotFoundException
	 * @throws SQLException 
	 * @author J.-Fabian Wenisch
	 * @since 09.04.2017
	 */
	public void initDefaults() throws FileNotFoundException, SQLException
	{
		File file = new File("./conf/"+strName+".default");
		Scanner scanner = new Scanner(file);
		while (scanner.hasNextLine())
		{
			try
			{
				addEntry(new ArrayList<String>(Arrays.asList(scanner.nextLine())));
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		scanner.close();

	}
	public void createTable(List <MDN_SQLColumn> oColumns) throws SQLException
	{
		String strColumns="";
		for(int i=0; i < oColumns.size();i++)
			strColumns+=oColumns.get(i).getName()+ "varchar(255) ,";
		MDN_SQLConnector.execute("CREATE TABLE "+getName()+ "( "+strColumns+" )");
	}
	/**
	 * Gibt den Namen der Tabelle zurück
	 * @return
	 */
	public String getName() 
	{
		return strName;
	}
}
