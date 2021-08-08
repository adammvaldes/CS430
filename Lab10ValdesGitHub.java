import java.sql.*;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.swing.JOptionPane;
import java.util.Scanner;

public class Lab10Valdes {
	
	public void readXML(String fileName)
	{
		try {
			File file = new File(fileName);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			doc.getDocumentElement().normalize();
			NodeList nodeLst = doc.getElementsByTagName("Borrowed_by");

			for (int s = 0; s < nodeLst.getLength(); s++) {

				Node fstNode = nodeLst.item(s);

				if (fstNode.getNodeType() == Node.ELEMENT_NODE) {

					Element sectionNode = (Element) fstNode;
					
					NodeList memberIdElementList = sectionNode.getElementsByTagName("MemberID");
					Element memberIdElmnt = (Element) memberIdElementList.item(0);
					NodeList memberIdNodeList = memberIdElmnt.getChildNodes();
					System.out.println("MemberID : "  + ((Node) memberIdNodeList.item(0)).getNodeValue().trim());

					NodeList secnoElementList = sectionNode.getElementsByTagName("ISBN");
					Element secnoElmnt = (Element) secnoElementList.item(0);
					NodeList secno = secnoElmnt.getChildNodes();
					System.out.println("ISBN : "  + ((Node) secno.item(0)).getNodeValue().trim());

					NodeList codateElementList = sectionNode.getElementsByTagName("Checkout_date");
					Element codElmnt = (Element) codateElementList.item(0);
					NodeList cod = codElmnt.getChildNodes();
					System.out.println("Checkout_date : "  + ((Node) cod.item(0)).getNodeValue().trim());

					NodeList cidateElementList = sectionNode.getElementsByTagName("Checkin_date");
					Element cidElmnt = (Element) cidateElementList.item(0);
					NodeList cid = cidElmnt.getChildNodes();
					System.out.println("Checkin_date : "  + ((Node) cid.item(0)).getNodeValue().trim());

					System.out.println();

				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void insertXML(String fileName, String tableName, Connection con) throws ISBNException, CheckinException
	{
		try {
			File file = new File(fileName);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			doc.getDocumentElement().normalize();
			NodeList nodeLst = doc.getElementsByTagName("Borrowed_by");

			for (int s = 0; s < nodeLst.getLength(); s++) {

				Node fstNode = nodeLst.item(s);

				if (fstNode.getNodeType() == Node.ELEMENT_NODE) {

					Element sectionNode = (Element) fstNode;
					
					NodeList memberIdElementList = sectionNode.getElementsByTagName("MemberID");
					Element memberIdElmnt = (Element) memberIdElementList.item(0);
					NodeList memberIdNodeList = memberIdElmnt.getChildNodes();
					System.out.println("\nMemberID : "  + ((Node) memberIdNodeList.item(0)).getNodeValue().trim());

					NodeList secnoElementList = sectionNode.getElementsByTagName("ISBN");
					Element secnoElmnt = (Element) secnoElementList.item(0);
					NodeList secno = secnoElmnt.getChildNodes();
					System.out.println("ISBN : "  + ((Node) secno.item(0)).getNodeValue().trim());
					
					NodeList libraryElementList = sectionNode.getElementsByTagName("Library");
					Element libraryElmnt = (Element) libraryElementList.item(0);
					NodeList lib = libraryElmnt.getChildNodes();
					System.out.println("Library : "  + ((Node) lib.item(0)).getNodeValue().trim());

					NodeList codateElementList = sectionNode.getElementsByTagName("Checkout_date");
					Element codElmnt = (Element) codateElementList.item(0);
					NodeList cod = codElmnt.getChildNodes();
					System.out.println("Checkout_date : "  + ((Node) cod.item(0)).getNodeValue().trim());

					NodeList cidateElementList = sectionNode.getElementsByTagName("Checkin_date");
					Element cidElmnt = (Element) cidateElementList.item(0);
					NodeList cid = cidElmnt.getChildNodes();
					System.out.println("Checkin_date : "  + ((Node) cid.item(0)).getNodeValue().trim());

					System.out.println();
					
					//check if the book exists in the library
					PreparedStatement pStmt = con.prepareStatement("select * from Book inner join locatedat on Book.ISBN=locatedat.ISBN where Book.ISBN=? and Name=?");
					pStmt.setString(1, ((Node) secno.item(0)).getNodeValue().trim());
					pStmt.setString(2, ((Node) lib.item(0)).getNodeValue().trim());
					try {
						ResultSet rs = pStmt.executeQuery();
						if(!rs.isBeforeFirst()) {
							throw new ISBNException("Book does not exist at that library\n");
						}
						//if checking a book out (creating a new record)
						if(((Node) cid.item(0)).getNodeValue().trim().equals("N/A")) {
							pStmt = con.prepareStatement("insert into borrowedby values(?,?,?,?,?)");
							pStmt.setInt(1, Integer.parseInt(((Node) memberIdNodeList.item(0)).getNodeValue().trim()));
							pStmt.setString(2, ((Node) secno.item(0)).getNodeValue().trim());
							pStmt.setString(3, ((Node) lib.item(0)).getNodeValue().trim());
							String checkout_date = ((Node) cod.item(0)).getNodeValue().trim();
							String checkout_date_update = checkout_date.substring(6,10) + "-" + checkout_date.substring(0,2) + "-" + checkout_date.substring(3,5);
							pStmt.setString(4, checkout_date_update);
							pStmt.setNull(5, java.sql.Types.DATE);
							try {
								pStmt.executeUpdate();
								System.out.println("Book checked out");
							}catch(Exception e){
						        System.out.print(e);
						      }//end catch
							
						}
						//if checking a book in (modifying a record)
						else if(((Node) cod.item(0)).getNodeValue().trim().equals("N/A")) {
							//check if the book has been checked out
							pStmt = con.prepareStatement("select * from borrowedby where MemberID=? and ISBN=? and Library=? and Checkin_date is NULL");
							pStmt.setInt(1, Integer.parseInt(((Node) memberIdNodeList.item(0)).getNodeValue().trim()));
							pStmt.setString(2, ((Node) secno.item(0)).getNodeValue().trim());
							pStmt.setString(3, ((Node) lib.item(0)).getNodeValue().trim());
							//pStmt.setNull(4, java.sql.Types.DATE);
							//System.out.println(pStmt.toString());
							//try {
								rs = pStmt.executeQuery();
								//}catch{
									if(!rs.isBeforeFirst()) {
										throw new CheckinException("Book cannot be checked in\n");
									}
								//}
								
								//update
								PreparedStatement pStmtCheckin = con.prepareStatement("update borrowedby set Checkin_date=? WHERE MemberID=? and ISBN=? and Library=? and Checkin_date is NULL");
								String checkin_date = ((Node) cid.item(0)).getNodeValue().trim();
								String checkin_date_update = checkin_date.substring(6,10) + "-" + checkin_date.substring(0,2) + "-" + checkin_date.substring(3,5);
								pStmtCheckin.setString(1, checkin_date_update);
								pStmtCheckin.setInt(2, Integer.parseInt(((Node) memberIdNodeList.item(0)).getNodeValue().trim()));
								pStmtCheckin.setString(3, ((Node) secno.item(0)).getNodeValue().trim());
								pStmtCheckin.setString(4, ((Node) lib.item(0)).getNodeValue().trim());
								try {
									pStmtCheckin.executeUpdate();
									System.out.println("Book checked in");
								}catch(Exception e){
							        System.out.print(e);
							      }//end catch
							}/*catch (Exception e) {
								e.printStackTrace();
							}*/
						}catch(Exception e){
							System.out.print(e);
					    }
					}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean checkMember(int mID, Connection con) throws MemberIDException{
		try {
			PreparedStatement pStmt = con.prepareStatement("select * from Member where MemberID=?");
			pStmt.setInt(1, mID);
			ResultSet rs = pStmt.executeQuery();
			//check if member exists
			if(!rs.isBeforeFirst()) {
				//if member doesn't exist return false
				return false;
			}else {
				//if member does exist return true
				return true;
		}
		}catch(Exception e){
			System.out.print(e);
		}
		return false;
	}
	
	public void createNewMember(int memberID, String lastname, String firstname, String DOB, String gender, Connection con) {
		try {
			PreparedStatement pStmt = con.prepareStatement("insert into Member (MemberID, Last_name, First_name, DOB, Gender) values(?,?,?,?,?)");
			pStmt.setInt(1, memberID);
			pStmt.setString(2, lastname);
			pStmt.setString(3, firstname);
			pStmt.setDate(4, java.sql.Date.valueOf(DOB));
			pStmt.setString(5, gender);
			pStmt.executeUpdate();
		}catch(Exception e) {
			System.out.print(e);
		}
	}
	
	//returns ResultSet to evaluate using evaluateResults
	public ResultSet getBookByISBN(String ISBN, Connection con){
		try {
			PreparedStatement pStmt = con.prepareStatement("select Name, Shelf, Floor, Total_Copies, Copies_Not_Checked_Out from locatedat where ISBN=(?)");
			pStmt.setString(1, ISBN);
			ResultSet rs = pStmt.executeQuery();
			if(!rs.isBeforeFirst()) {
				System.out.println("The library does not currently have that book in stock.");
				return null;
			}
			return rs;
		}catch(Exception e) {
			System.out.print(e);
		}
		return null;
	}
	
	//returns ResultSet to evaluate using evaluateResults
	public ResultSet getBookByTitle(String title, Connection con){
		try {
			PreparedStatement pStmt = con.prepareStatement("select ISBN, Title from Book where Title like ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			pStmt.setString(1, "%"+title+"%");
			ResultSet rs = pStmt.executeQuery();
			if(!rs.isBeforeFirst()) {
				System.out.println("The library does not currently have that book in stock.");
				return null;
			}
			rs.last();
			int numRows = rs.getRow();
			rs.beforeFirst();
			//if there is only 1 matching book get its details and process the results
			if(numRows == 1) {
				String ISBN = "";
				while(rs.next()) {
					ISBN = rs.getString("ISBN");
				}
				pStmt = con.prepareStatement("select Name, Shelf, Floor, Total_Copies, Copies_Not_Checked_Out from locatedat where ISBN=(?)");
				pStmt.setString(1, ISBN);
				rs = pStmt.executeQuery();
				return rs;
			}else {
				//otherwise there is more than 1 result (already checked for #results=0), so have the user choose which book
				System.out.println("There were multiple books with a title like that:");
				int bookNumCounter = 1;
				while(rs.next()){
					System.out.println(bookNumCounter + ": Title: " + rs.getString("Title"));
					bookNumCounter++;
				}
				System.out.println("Enter the number corresponding to the book you want:");
				Scanner input = new Scanner(System.in);
				int bookNum = input.nextInt();
				rs.beforeFirst();
				while(rs.next()) {
					if(rs.getRow() == bookNum) {
						pStmt = con.prepareStatement("select Name, Shelf, Floor, Total_Copies, Copies_Not_Checked_Out from locatedat where ISBN=(?)");
						String ISBN = rs.getString("ISBN");
						pStmt.setString(1, ISBN);
						rs = pStmt.executeQuery();
						return rs;
					}
				}
			}
			return rs;
		}catch(Exception e) {
			System.out.print(e);
		}
		return null;
	}
	
	//returns ResultSet to evaluate using evaluateResults
	public ResultSet getBookByAuthor(String authorFirstName, String authorLastName, Connection con){
		try {
			PreparedStatement pStmt = con.prepareStatement("select Book.ISBN, Title from Author inner join writtenby on Author.Author_ID=writtenby.Author_ID inner join Book on writtenby.ISBN=Book.ISBN where First_name like ? and Last_name like ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			pStmt.setString(1, "%"+authorFirstName+"%");
			pStmt.setString(2, "%"+authorLastName+"%");
			ResultSet rs = pStmt.executeQuery();
			if(!rs.isBeforeFirst()) {
				System.out.println("The library does not currently have that book in stock.");
				return null;
			}
			rs.last();
			int numRows = rs.getRow();
			rs.beforeFirst();
			//if there is only 1 matching book get its details and process the results
			if(numRows == 1) {
				String ISBN = "";
				while(rs.next()) {
					ISBN = rs.getString("ISBN");
				}
				pStmt = con.prepareStatement("select Name, Shelf, Floor, Total_Copies, Copies_Not_Checked_Out from locatedat where ISBN=(?)");
				pStmt.setString(1, ISBN);
				rs = pStmt.executeQuery();
				return rs;
			}else {
				//otherwise there is more than 1 result (already checked for #results=0), so have the user choose which book
				System.out.println("There were multiple books by that author:");
				int bookNumCounter = 1;
				while(rs.next()){
					System.out.println(bookNumCounter + ": Title: " + rs.getString("Title"));
					bookNumCounter++;
				}
				System.out.println("Enter the number corresponding to the book you want:");
				Scanner input = new Scanner(System.in);
				int bookNum = input.nextInt();
				rs.beforeFirst();
				while(rs.next()) {
					if(rs.getRow() == bookNum) {
						pStmt = con.prepareStatement("select Name, Shelf, Floor, Total_Copies, Copies_Not_Checked_Out from locatedat where ISBN=(?)");
						String ISBN = rs.getString("ISBN");
						pStmt.setString(1, ISBN);
						rs = pStmt.executeQuery();
						return rs;
					}
				}
			}
			return rs;
		}catch(Exception e) {
			System.out.print(e);
		}
		return null;
	}
	
	//evaluate a resultset to see if the copies are checked out or not
	public void evaluateResults(ResultSet rs) {
		try {
			if(rs == null) {
				return;
			}
			while(rs.next()) {
				if(rs.getInt("Total_Copies") == rs.getInt("Copies_Not_Checked_Out")){
					System.out.println("Library " + rs.getString("Name") + " has that book on shelf " + rs.getString("Shelf") + ", floor " + rs.getString("Floor") + ".");
				}else if(rs.getInt("Copies_Not_Checked_Out") == 0){
					System.out.println("All copies are checked out at library " + rs.getString("Name"));
				}
			}
		}catch(Exception e) {
			System.out.print(e);
		}
	}
	
	class ISBNException extends Exception{
		public ISBNException(String message) {
			super(message);
		}
	}
	
	class CheckinException extends Exception{
		public CheckinException(String message) {
			super(message);
		}
	}
	
	class MemberIDException extends Exception{
		public MemberIDException(String message) {
			super(message);
		}
	}
	
	
  public static void main(String args[]){

    Connection con = null;

    try {
      Statement stmt;
      ResultSet rs;

      // Register the JDBC driver for MySQL.
      Class.forName("com.mysql.jdbc.Driver");

      // Define URL of database server for
      // database named 'user' on the faure.
      String url =
            "jdbc:mysql://faure/user";

      // Get a connection to the database for a
      // user named 'user' with the password
      // 123456789.
      con = DriverManager.getConnection(
                        url,"user", "password");

      // Display URL and connection information
      System.out.println("URL: " + url);
      System.out.println("Connection: " + con);

      // Get a Statement object
      stmt = con.createStatement();

      //command line input interaction
      Scanner input = new Scanner(System.in);
      
      Lab10Valdes lab10 = new Lab10Valdes();
      
      while(true) {
    	  System.out.println("\nEnter member ID:");
          
          int memberID = input.nextInt();
          
          //System.out.println("You inputted member ID = " + memberID);
          
          boolean memberExists = lab10.checkMember(memberID, con);
          
          //if the memberID is already in database proceed
          if(memberExists) {
        	  //System.out.println("Member exists.");
          }else {
        	  //if the memberID is not in the database ask to create a new member
        	  //MemberID, Last_name, First_name, DOB, Gender
        	  System.out.println("That member does not exist. Create a new member? yes/no");
        	  String memberYesNo = input.next();
        	  //if user wants to create a new member proceed to create a new member
        	  if(memberYesNo.equals("yes")) {
        		  System.out.println("What is the new member's MemberID?");
        		  int newMemberID = input.nextInt();
        		  System.out.println("What is the new member's last name?");
        		  String newLastName = input.next();
        		  System.out.println("What is the new member's first name?");
        		  String newFirstName = input.next();
        		  System.out.println("What is the new member's date of birth (YYYY-MM-DD)?");
        		  String newDOB = input.next();
        		  System.out.println("What is the new member's Gender (M/F)?");
        		  String newGender = input.next();
        		  System.out.println("Added new member with MemberID=" + newMemberID + ", Last_name=" + newLastName + ", First_name=" + newFirstName + ", DOB=" + newDOB + ", Gender=" + newGender);
        		  lab10.createNewMember(newMemberID, newLastName, newFirstName, newDOB, newGender, con);
        	  }else {
        		  //executes if the input is not "yes"
        		  //if user does not want to create a new member then exit
        		  System.out.println("You did not say yes. Exiting.");
        		  con.close();
        		  return;
        	  }
          }
          //let user choose how to identify their book
          System.out.println("How would you like to identify your book (ISBN, Name or Author)?");
          String bookIdentifier = input.next();
          if(bookIdentifier.toUpperCase().equals("ISBN".toUpperCase())) {
        	  System.out.println("You have chosen to identify your book using ISBN. Please enter the ISBN of the book you would like to check out:");
        	  String ISBN = input.next();
        	  rs = lab10.getBookByISBN(ISBN, con);
        	  lab10.evaluateResults(rs);
          }else if(bookIdentifier.toUpperCase().equals("Name".toUpperCase())) {
        	  System.out.println("You have chosen to identify your book using Name. Please enter the Name of the book you would like to check out:");
        	  String name = input.next();
        	  rs = lab10.getBookByTitle(name, con);
        	  lab10.evaluateResults(rs);
          }else if(bookIdentifier.toUpperCase().equals("Author".toUpperCase())) {
        	  System.out.println("You have chosen to identify your book using Author.");
        	  System.out.println("Please enter the first name of the author:");
        	  String authorFirstName = input.next();
        	  System.out.println("Please enter the last name of the author:");
        	  String authorLastName = input.next();
        	  rs = lab10.getBookByAuthor(authorFirstName, authorLastName, con);
        	  lab10.evaluateResults(rs);
          }else {
        	  System.out.println("Invalid identification method. Exiting.");
        	  con.close();
          return;
          }
      }

      //lab 9 code
      /*
      try{
        rs = stmt.executeQuery("SELECT * FROM Author");
        while (rs.next()) {
          System.out.println (rs.getString("Author_ID"));
      }
      }catch(Exception e){
        System.out.print(e);
        System.out.println(
                  "No Author table to query");
      }//end catch
	
	try {
		Lab9_xml insertXML = new Lab9_xml();
		insertXML.insertXML ("/s/bach/c/under/adammv/CS430/Lab9/Libdata.xml", "borrowedby", con);
	}catch(Exception e){
        System.out.print(e);
        System.out.println(
                  "Could not read in XML data");
      }//end catch*/
	
	

      //con.close();
    }catch( Exception e ) {
      e.printStackTrace();

    }//end catch

  }//end main

}//end class Lab10Valdes
