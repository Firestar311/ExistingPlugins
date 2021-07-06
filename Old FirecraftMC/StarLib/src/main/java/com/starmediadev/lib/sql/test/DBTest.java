package com.starmediadev.lib.sql.test;

import com.starmediadev.lib.StarLib;
import com.starmediadev.lib.sql.*;

public final class DBTest {
    private DBTest() {
    }
    
    public static void init() {
        Database database = new Database(StarLib.getInstance(), DBType.MYSQL, "localhost", "root", "niles3408", 3306, "realms"); //Create a connection
        Table table = new Table(database, "test"); //Create a table
        Column id = new Column("id", DataType.INT, true, true); //create an id column
        table.addColumn(id); //add to the table
//        Column string = new Column("string", DataType.VARCHAR, 30, false, false); //create another column that holds a string named string
//        table.addColumn(string); //add to the table
        Column name = new Column("name", DataType.VARCHAR, 30, false, false);
        table.addColumn(name);
        database.registerTable(table); //register the tables with the database
        database.generateTables(); //create tables in the database based on the registry of tables
        database.mapTableToRecord(table, TestRecord.class); //Maps a table to a record class, which is an interface
    
        TestRecord testRecord = new TestRecord("testString1"); //create a new record that is of the type TestRecord
        testRecord.setId(1); //manually setting the id, this is not needed, was used to test the update statement. If this is not provided a new row will be created
        database.addRecordToQueue(testRecord); //adds the record to the queue
        database.pushQueue(); //pushes changes to the database
    }
}