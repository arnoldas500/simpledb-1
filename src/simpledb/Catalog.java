package simpledb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * The Catalog keeps track of all available tables in the database and their associated schemas. For now, this is a stub
 * catalog that must be populated with tables by a user program before it can be used -- eventually, this should be
 * converted to a catalog that reads a catalog table from disk.
 */

public class Catalog {
	private class Table {
        public Table(String name, DbFile dbfile, String pkeyField) {
            this.Name = name;
            this.File = dbfile;
            this.PkeyField = pkeyField;
        }
        public DbFile getDbFile() {
            return this.File;
        }
        public String getPkeyField() {
            return this.PkeyField;
        }
        public String getName() {
            return this.Name;
        }
        private String Name;
        private DbFile File;        
        private String PkeyField;
    }

	/**
	 * A map that associates the name of each table with the ID of that table.
	 */
	HashMap<String, Integer> name2tableID = new HashMap<String, Integer>();

	/**
	 * A map that associates the ID of each table with the TupleDesc of that table.
	 */
	HashMap<Integer, TupleDesc> tableID2desc = new HashMap<Integer, TupleDesc>();

	/**
	 * A map that associates the ID of each table with the DbFile of that table.
	 */
	HashMap<Integer, DbFile> tableID2dbFile = new HashMap<Integer, DbFile>();

	/**
	 * Constructor. Creates a new, empty catalog.
	 */
	private HashMap<String,Table> NameHash;
    private HashMap<Integer,Table> IdHash;
    
	public Catalog() {
		this.NameHash = new HashMap<String,Table>();
        this.IdHash = new HashMap<Integer,Table>();
	}

	/**
	 * Add a new table to the catalog. This table's contents are stored in the specified DbFile.
	 * 
	 * @param file
	 *            the contents of the table to add; file.getId() is the identifier of this file/tupledesc param for the
	 *            calls getTupleDesc and getFile
	 * @param name
	 *            the name of the table -- may be an empty string. May not be null. If a name conflict exists, use the
	 *            last table to be added as the table for a given name.
	 * @param pkeyField
	 *            the name of the primary key field
	 */
	public void addTable(DbFile file, String name, String pkeyField) {
		int tableID = file.getId();
		name2tableID.put(name, tableID);
		tableID2desc.put(tableID, file.getTupleDesc());
		tableID2dbFile.put(tableID, file);
		
		Table data = new Table(name,file,pkeyField);
        this.NameHash.put(name,data);
        this.IdHash.put(file.getId(),data);
		
	}

	/**
	 * Add a new table to the catalog. This table's contents are stored in the specified DbFile.
	 * 
	 * @param file
	 *            the contents of the table to add; file.getId() is the identifier of this file/tupledesc param for the
	 *            calls getTupleDesc and getFile
	 * @param name
	 *            the name of the table -- may be an empty string. May not be null. If a name conflict exists, use the
	 *            last table to be added as the table for a given name.
	 */
	public void addTable(DbFile file, String name) {
		addTable(file, name, "");
	}

	/**
	 * Return the id of the table with a specified name,
	 * 
	 * @throws NoSuchElementException
	 *             if the table doesn't exist
	 */
	
	public int getTableId(String name) throws NoSuchElementException 
	{
        // some code goes here
        Table match = this.NameHash.get(name);
        //if(this.name2tableID.get(i).getName().equals(name))
        if (match == null) 
        {
            throw new NoSuchElementException();
        } 
        else 
        {
            DbFile file = match.getDbFile();
            //return this.name2tableID.get(i).getId();
            return file.getId();
        }
    }

	
	
	/**
	 * Returns the tuple descriptor (schema) of the specified table
	 * 
	 * @param tableid
	 *            The id of the table, as specified by the DbFile.getId() function passed to addTable
	 * @throws NoSuchElementException
	 *             if the table doesn't exist
	 */
	
	public TupleDesc getTupleDesc(int tableid) throws NoSuchElementException 
	{
        // some code goes here
        Table match = this.IdHash.get(tableid);
        if (match == null) 
        {
            throw new NoSuchElementException();
        }
        else
        {
            DbFile file = match.getDbFile();
            return file.getTupleDesc();
        }
    }
	

	/**
	 * Returns the DbFile that can be used to read the contents of the specified table.
	 * 
	 * @param tableid
	 *            The id of the table, as specified by the DbFile.getId() function passed to addTable
	 * @throws NoSuchElementException
	 *             if the table doesn't exist
	 */
	
	public DbFile getDbFile(int tableid) throws NoSuchElementException 
	{
        // some code goes here
        Table match = this.IdHash.get(tableid);
        if (match == null) 
        {
            throw new NoSuchElementException();
        } 
        else 
        {
            return match.getDbFile();
        }
    }
	
	
	/** Delete all tables from the catalog */
	public void clear() {
		name2tableID.clear();
		tableID2desc.clear();
		tableID2dbFile.clear();
	}

	public String getPrimaryKey(int tableid) {
		// some code goes here
		return null;
	}

	public Iterator<Integer> tableIdIterator() {
		// some code goes here
		return null;
	}

	public String getTableName(int id) {
		// some code goes here
		return null;
	}

	/**
	 * Reads the schema from a file and creates the appropriate tables in the database.
	 * 
	 * @param catalogFile
	 */
	public void loadSchema(String catalogFile) {
		String line = "";
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(catalogFile)));
			try {
				while ((line = br.readLine()) != null) {
					// assume line is of the format name (field type, field type, ...)
					String name = line.substring(0, line.indexOf("(")).trim();
					// System.out.println("TABLE NAME: " + name);
					String fields = line.substring(line.indexOf("(") + 1, line.indexOf(")")).trim();
					String[] els = fields.split(",");
					ArrayList<String> names = new ArrayList<String>();
					ArrayList<Type> types = new ArrayList<Type>();
					String primaryKey = "";
					for (String e : els) {
						String[] els2 = e.trim().split(" ");
						names.add(els2[0].trim());
						if (els2[1].trim().toLowerCase().equals("int"))
							types.add(Type.INT_TYPE);
						else if (els2[1].trim().toLowerCase().equals("string"))
							types.add(Type.STRING_TYPE);
						else {
							System.out.println("Unknown type " + els2[1]);
							System.exit(0);
						}
						if (els2.length == 3) {
							if (els2[2].trim().equals("pk"))
								primaryKey = els2[0].trim();
							else {
								System.out.println("Unknown annotation " + els2[2]);
								System.exit(0);
							}
						}
					}
					Type[] typeAr = types.toArray(new Type[0]);
					String[] namesAr = names.toArray(new String[0]);
					TupleDesc t = new TupleDesc(typeAr, namesAr);
					HeapFile tabHf = new HeapFile(new File(name + ".dat"), t);
					addTable(tabHf, name, primaryKey);
					System.out.println("Added table : " + name + " with schema " + t);
				}
			} finally {
				br.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		} catch (IndexOutOfBoundsException e) {
			System.out.println("Invalid catalog entry : " + line);
			System.exit(0);
		}
	}
}
