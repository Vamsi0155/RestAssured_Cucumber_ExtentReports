package factory;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.sql.*;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DBConnection {
	
	private static final Logger logger = LogManager.getLogger(DBConnection.class);
	
	private static SecretKeySpec secretKey;
	private static byte[] key;
	
	
	private DBConnection() {
		logger.error("Enetered DB Connection class: ");
	}
	
	
	private static Connection createDataBaseConnnection() {
		
		try {
			String user = ReadConfigFiles.config.getProperty("DatabaseUserName");
			String password = null;
			String url = ReadConfigFiles.config.getProperty("DatabaseURL");
			
			DriverManager.registerDriver((Driver)Class.forName(ReadConfigFiles.config.getProperty("")).getDeclaredConstructor().newInstance());
			if(ReadConfigFiles.config.getProperty("DBPasswordEnc") !=null && ReadConfigFiles.config.getProperty("DBPasswordEnc").equals("Y")) {
				password = encryptDecrypt("decrypt", ReadConfigFiles.config.getProperty("DatabasePassword"));
			}
			else {
				password = ReadConfigFiles.config.getProperty("DatabasePassword");
			}
			
			Properties info = new Properties();
			info.put("user", user);
			info.put("password", password);
			
			return DriverManager.getConnection(url, info);
			
		}
		catch(Exception e) {
			
			logger.error("Error in connecting to the database: " + e.getMessage());
			return null;
		}
	}
	
	
	public static Map<String, String> executeDBQuery(String query) {
		
		HashMap<String, String> dbColResult = new HashMap<>();
		
		try {
			
			Connection dbConnection = createDataBaseConnnection();
			Throwable var3 = null;
			
			try {
				
				PreparedStatement pstmt = dbConnection != null ? dbConnection.prepareStatement(query) : null;
				Throwable var4 = null;
				
				try {
					
					ResultSet result = pstmt !=null ? pstmt.executeQuery() : null;
					Throwable var5 = null;
					
					try {
						
						if(result != null) {
							
							ResultSetMetaData rsmd = result.getMetaData();
							int colCount = rsmd.getColumnCount();
							
							int i = 1;
							if(result.next()) {
								do {
									for(int j=1; j<= colCount; ++j) {
										dbColResult.put(rsmd.getCatalogName(j) + "%$$%" + i, result.getString(j));
									}
									
									++i;
								} while(result.next());
							}
						}
					}
					catch(Throwable e) {
						var5 = e;
						throw e;
					}
					finally {
						if(result != null ) {
							if(var5 != null) {
								try {
									result.close();
								} catch(Throwable e) {
									var5.addSuppressed(e);
								}
							} else result.close();
						}
					}
				}
				catch(Throwable e ) {
					var4 = e;
					throw e;
				}
				finally {
					if(pstmt != null ) {
						if(var4 != null) {
							try {
								pstmt.close();
							} catch(Throwable e) {
								var4.addSuppressed(e);
							}
						} else pstmt.close();
					}
				}
			}
			catch(Throwable e) {
				var3 = e;
				throw e;
			}
			finally {
				if(dbConnection != null) {
					if(var3 != null) {
						try {
							dbConnection.close();
						} catch(Throwable e) {
							var3.addSuppressed(e);
						}
					} 
					else dbConnection.close();
				}
			}
		}
		catch(Exception e) {
			logger.error("Error in executing query: " + query);
			logger.error("Error in execute DB query method " + e);
			logger.error("Error in connecting to the database in execute DB query method");
		}
		
		
		return dbColResult;
	}

	
	public static void executeFile(String fileName) {
		
		try {
			
			Connection dbConnection = createDataBaseConnnection();
			Throwable var2 = null;
			
			try {
				
				File file = new File(ReadConfigFiles.config.getProperty("DBScriptsPath") + fileName);
				List<String> line = IOUtils.readLines(new FileInputStream(file), "UTF-8");
				Iterator<String> var5 = line.iterator();
				
				while(var5.hasNext()) {
					String query = var5.next();
					if(!query.isEmpty()) {
						if(query.contains("DbAlias#")) {
							query = query.replace("DbAlias#", ReadConfigFiles.config.getProperty("DatabaseAlias"));
						}
						
						if(query.contains(";")) {
							query = query.split(";")[0];
						}
						
						executeUpdateQuery(dbConnection, query);
					}
				}
				
			} catch (Throwable e) {
				var2 = e;
				throw e;
			} finally {
				if(dbConnection != null) {
					if(var2 != null) {
						try {
							dbConnection.close();
						} catch(Throwable e) {
							var2.addSuppressed(e);
						}
					} 
					else dbConnection.close();
				}
			}
			
		}
		catch (Exception e) {
			logger.error("Error while executing file: " + e.getMessage());
		}
	}
	
	
	public static void executeInsertQuery(String query) {
		
		try {
			
			Connection dbConnection = createDataBaseConnnection();
			PreparedStatement pstmt = dbConnection != null ? dbConnection.prepareStatement(query) : null;
			Throwable var3 = null;
			
			try {
				int rowsUpdated = pstmt != null ? pstmt.executeUpdate() : 0;
				logger.info("Execution of the query: '" + query + "' is successful with number of rows updated: " + rowsUpdated);
			} catch(Throwable e ) {
				var3 = e;
				throw e;
			} finally {
				if(pstmt != null ) {
					if(var3 != null) {
						try {
							pstmt.close();
						} catch(Throwable e) {
							var3.addSuppressed(e);
						}
					} else pstmt.close();
				}
				
				if(dbConnection != null) {
					if(var3 != null) {
						try {
							dbConnection.close();
						}
						catch(Throwable e) {
							var3.addSuppressed(e);
						}
					} else dbConnection.close();
				}
			}
			
		}
		catch(Exception e) {
			logger.error("Error in connecting to the database for executing file data method: ", query, e);
		}
	}
	
	
	
	public static void executeUpdateQuery(Connection dbConnection, String query) {
		
		try {
			
			PreparedStatement pstmt = dbConnection != null ? dbConnection.prepareStatement(query) : null;
			Throwable var3 = null;
			
			try {
				int rowsUpdated = pstmt != null ? pstmt.executeUpdate() : 0;
				logger.info("Execution of the query: '" + query + "' is successful with number of rows updated: " + rowsUpdated);
			} catch(Throwable e ) {
				var3 = e;
				throw e;
			} finally {
				if(pstmt != null ) {
					if(var3 != null) {
						try {
							pstmt.close();
						} catch(Throwable e) {
							var3.addSuppressed(e);
						}
					} else pstmt.close();
				}
			}
		}
		catch(Exception e) {
			logger.error("Error in connecting to the database for executing file data method: ", query, e);
		}
		
	}


	/* Encrypt Decrypt methods from here */

	private static String encryptDecrypt(String option, String key) {
		
		String secretKey1 = "";
		
		if(option.equals("encrypt")) {
			return encrypt(key, secretKey1);
		}
		else {
			return option.equals("decrypt") ? decrypt(key, secretKey1) : null;
		}
		
	}


	private static String decrypt(String key1, String secretKey1) {
		
		try {
			setKey(secretKey1);
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			cipher.init(2, secretKey);
			return new String(cipher.doFinal(Base64.getDecoder().decode(key1)));
		}
		catch(Exception e) {
			System.out.println("Error while decrypting: " + e.toString());
			return null;
		}
	}


	private static String encrypt(String key1, String secretKey1) {
		
		try {
			setKey(secretKey1);
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			cipher.init(1, secretKey);
			return Base64.getEncoder().encodeToString(cipher.doFinal(key1.getBytes("UTF-8")));
		}
		catch(Exception e) {
			System.out.println("Error while encrypting: " + e.toString());
			return null;
		}
	}
	
	private static void setKey(String mykey) {
		
		MessageDigest sha = null;
		try {
			key = mykey.getBytes("UFT-8");
			sha = MessageDigest.getInstance("SHA-1");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16);
			secretKey = new SecretKeySpec(key, "AES");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}
