CREATE TABLE Member(
	MemberID Integer,
	Last_name VARCHAR(30),
	First_name VARCHAR(30),
	DOB Date,
	Gender CHAR(1),
	PRIMARY KEY (MemberID));

CREATE TABLE Publisher(
	PubID Integer,
	Pub_name VARCHAR(40),
	PRIMARY KEY (PubID));

CREATE TABLE Book(
	ISBN VARCHAR(30),
	Title VARCHAR(40),
	Year_Published Date,
	PRIMARY KEY (ISBN));
	
CREATE TABLE Author(
	Author_ID Integer,
	Last_name VARCHAR(30),
	First_name VARCHAR(30),
	PRIMARY KEY (Author_ID));

CREATE TABLE Phone(
	PNumber CHAR(12),
	Type CHAR(3),
	PRIMARY KEY (PNumber));
	
CREATE TABLE Library(
	Name VARCHAR(30),
	Street VARCHAR(30),
	City VARCHAR(30),
	State VARCHAR(30),
	PRIMARY KEY (Name));
	
CREATE TABLE borrowedby(
	MemberID Integer,
	ISBN VARCHAR(30),
	Library VARCHAR(100),
	Checkout_Date Date,
	Checkin_Date Date,
	PRIMARY KEY (MemberID, ISBN, Checkout_Date),
	FOREIGN KEY (MemberID) REFERENCES Member(MemberID),
	FOREIGN KEY (ISBN) REFERENCES Book(ISBN));
	
CREATE TABLE writtenby(
	ISBN VARCHAR(30),
	Author_ID Integer,
	PRIMARY KEY (ISBN, Author_ID),
	FOREIGN KEY (ISBN) REFERENCES Book(ISBN),
	FOREIGN KEY (Author_ID) REFERENCES Author(Author_ID));
	
CREATE TABLE publishedby(
	ISBN VARCHAR(30),
	PubID Integer,
	PRIMARY KEY (ISBN, PubID),
	FOREIGN KEY (ISBN) REFERENCES Book(ISBN),
	FOREIGN KEY (PubID) REFERENCES Publisher(PubID));
	
CREATE TABLE locatedat(
	ISBN VARCHAR(30),
	Name VARCHAR(30),
	Shelf Integer,
	Floor Integer,
	Total_Copies Integer,
	Copies_Not_Checked_Out Integer,
	PRIMARY KEY (Name, ISBN),
	FOREIGN KEY (Name) REFERENCES Library(Name),
	FOREIGN KEY (ISBN) REFERENCES Book(ISBN));

CREATE TABLE publisherPhone(
	PubID Integer,
	PNumber CHAR(12),
	PRIMARY KEY (PubID, PNumber),
	FOREIGN KEY (PubID) REFERENCES Publisher(PubID),
	FOREIGN KEY (PNumber) REFERENCES Phone(PNumber));
	
CREATE TABLE authorPhone(
	Author_ID Integer,
	PNumber CHAR(12),
	PRIMARY KEY (Author_ID, PNumber),
	FOREIGN KEY (Author_ID) REFERENCES Author(Author_ID),
	FOREIGN KEY (PNumber) REFERENCES Phone(PNumber));
	
CREATE SQL SECURITY INVOKER VIEW checkedout AS 
SELECT
	ISBN
FROM
	borrowedby
WHERE
	Checkin_Date="0000-00-00" OR Checkin_Date=null;

CREATE TABLE Audit(
	Action VARCHAR(30),
	datetime datetime);