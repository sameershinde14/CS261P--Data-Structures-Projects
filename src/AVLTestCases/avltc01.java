package AVLTestCases;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


import Core.Tree;
import Core.Utilities;

public class avltc01 {
	private static final Logger LOGGER = Logger.getLogger(avltc01.class.getName());
	private static FileHandler fh;  
	
	public static void main(String[] args) {
		
		
		try {
			System.out.println("Starting Test case for Inserts and Display");
			init("./Logs/AVL/avltc01.log");
			
			Tree<Integer> root = Utilities.createTree("AVL", LOGGER);
			
			int count = 10;
			
			LOGGER.warning("INSERTING "+count+" entries");
			
			Utilities.populateTree(root, "Uniform", (long) 5.0,10);
	        
			// LOGGER.warning("Can cause ArrayIndexOutOfBoundsException");
			// LOGGER.config("Number of elements added: 5");
			
			root.display();
			
			
			if(root.check()) {
				System.out.println("Test successfully Executed");
				LOGGER.fine("Test for insert successfully completed");
			}
			else {
				System.out.println("Test not successful");
				LOGGER.fine("Test for insert failed");
			}
		}
		catch (SecurityException e) {  
	        e.printStackTrace();  
	    }  
	}

	private static void init(String path) {
		try {
			fh = new FileHandler(path);
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}  
		LOGGER.setLevel(Level.ALL);
        LOGGER.addHandler(fh);
        SimpleFormatter formatter = new SimpleFormatter();  
        fh.setFormatter(formatter);  
		LOGGER.info("Logger Name: "+LOGGER.getName());
        
		
	}

}
