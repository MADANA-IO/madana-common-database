package com.madana.common.database.dbms;
///*******************************************************************************
// * Copyright (C) 2018 MADANA
// * 
// * This program is free software: you can redistribute it and/or modify
// * it under the terms of the GNU General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// * 
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// * 
// * You should have received a copy of the GNU General Public License
// * along with this program.  If not, see <http://www.gnu.org/licenses/>.
// * 
// * @organization:MADANA
// * @author:Jean-Fabian Wenisch
// * @contact:dev@madana.io
// ******************************************************************************/
//package de.madana.common.database.dbms;
//
//import java.io.File;
//import java.io.IOException;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//
//import de.madana.common.database.MDN_SQLConnector;
//import de.madana.security.MDN_Password;
//
//public class MDN_SQLiteConnector extends MDN_SQLConnector
//{
//	/**
//	 * Führt ein Query in der Datenbank durch und gibt das Ergebnis als Resultset zurück
//	 * @param strQuery - Das auszuführende SQL Statement als String
//	 * @return {@link ResultSet}
//	 * @throws SQLException
//	 * @author Jean
//	 * @since 01.04.2017
//	 */
//	public static ResultSet executeQuery(String strQuery) throws SQLException
//	{
//		java.sql.Statement stmt =  connection.createStatement();
//		return stmt.executeQuery(strQuery);
//	}
//	/**
//	 * Führt ein Query in der Datenbank durch
//	 * @param strQuery - Das auszuführende SQL Statement als String
//	 * @throws SQLException
//	 * @author Jean
//	 * @since 01.04.2017
//	 */
//	public static void execute(String strQuery) throws SQLException
//	{
//		java.sql.Statement stmt =  connection.createStatement();
//		stmt.executeUpdate(strQuery);
//	}
//	/**
//	 * Führt ein Query in der Datenbank durch und gibt das Ergebnis als Resultset zurück
//	 * @param strQuery - Das auszuführende SQL Statement als String
//	 * @return {@link ResultSet}
//	 * @throws SQLException
//	 * @author Jean
//	 * @since 01.04.2017
//	 */
//	
//	/**
//	 * Liest alle Tabellen aus der Datenbank aus
//	 * @return eine aus String bestehende Liste welche die Namen der Tabellen enthält
//	 * @throws SQLException
//	 * @author Jean
//	 * @since 01.04.2017
//	 */
//	public List<String> getAllTables() throws SQLException
//	{
//		List<String> oTables = new ArrayList<String>();
//		ResultSet rs = executeQuery("SELECT name FROM sqlite_master WHERE type='table';");
//		while(rs.next())
//			oTables.add(rs.getString(1));
//		return oTables;
//	}
//	protected boolean create() throws IOException
//	{
//		initProperties();
//		String strDefaultDatabasePath = new File(DATABASE_URL).getCanonicalPath();
//        String url = "jdbc:sqlite:"+strDefaultDatabasePath;
// 
//        try (java.sql.Connection conn = DriverManager.getConnection(url)) 
//        {
//            if (conn != null)
//            {
//                java.sql.DatabaseMetaData meta = conn.getMetaData();
//                System.out.println("The driver name is " + meta.getDriverName());
//                System.out.println("A new database has been created.");
//            }
// 
//        } catch (SQLException e)
//        {
//            System.out.println(e.getMessage());
//            return false;
//        }
//		return true;
//	}
//	/**
//	 * Stellt eine Verbindung zur Datenbank her
//	 * @return {@link Connection}
//	 * @author Jean
//	 * @throws IOException 
//	 * @since 01.04.2017
//	 */
//	public Connection connect(boolean bLoadProperties)  
//	{
//
//
//		if (connection == null)
//		{
//			try 
//			{
//				if(bLoadProperties)
//					initProperties();
//				Class.forName(DATABASE_DRIVER);
//				  MDN_Password oPass = new MDN_Password();
//				String strDefaultDatabasePath = new File(DATABASE_URL).getCanonicalPath();
////				if(DATABASE_ENCRYPTIONHASH.length()<1)
////				{
////					  String strPassword = JOptionPane.showInputDialog(null, "The database is currently not encrypted, please select a secure Password");
////					
////					  try 
////					  {
////						  DATABASE_ENCRYPTIONHASH = oPass.getSaltedHash(strPassword);
////						  MDN_RandomString strRandomString = new MDN_RandomString();
////						  saveProperties();
////						  Main.crypt = new SymmetricCryptography(strPassword, 16, "AES");
////						  Main.crypt.encryptFile(new File(DATABASE_URL));
////						
////					} catch (Exception e) {
////						// TODO Auto-generated catch block
////						e.printStackTrace();
////					}
////				}
////				else
////				{
////					  try {
////					
////						boolean bInitialized = false;
////								while (!bInitialized)
////								{
////									 String strPassword = JOptionPane.showInputDialog(null, "Enter Password");
////									 bInitialized = oPass.check(strPassword, DATABASE_ENCRYPTIONHASH);	
////									 if(bInitialized)
////										  Main.crypt = new SymmetricCryptography(strPassword, 16, "AES");	 
////								}
////					} catch (NoSuchAlgorithmException e) {
////						// TODO Auto-generated catch block
////						e.printStackTrace();
////					} catch (NoSuchPaddingException e) {
////						// TODO Auto-generated catch block
////						e.printStackTrace();
////					} catch (Exception e) {
////						// TODO Auto-generated catch block
////						e.printStackTrace();
////					}
////						
////				}
////				 Main.crypt.decryptFile(new File(DATABASE_URL));
//		        String url = "jdbc:sqlite:"+strDefaultDatabasePath;
//				connection = DriverManager.getConnection(url);
//			} 
//			catch (ClassNotFoundException | SQLException | IOException e) 
//			{
//				System.out.println("Datenbank nicht gefunden!");
//				e.printStackTrace();
//			} 
//		}
//		return connection;
//	}
//}
