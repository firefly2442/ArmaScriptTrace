import java.io.*;
import java.util.ArrayList;

import javax.swing.JOptionPane;


public class DOTGenerator
{
	FileWriter fstream;
	BufferedWriter out;
	boolean stack;
	ArrayList<String> edges = new ArrayList<String>();
	
	private String filetypes;
	private String methodcalls;
	
	public DOTGenerator()
	{
		//Constructor
	}
	
	public boolean generateDotFile (String path, String filename)
	{
		GUI.open_script.setEnabled(false); //Disable OPEN menu item until we're done
		
		filename = filename.toLowerCase();
		
		if (!filename.endsWith(".sqf") && !filename.endsWith(".sqs") && !filename.endsWith(".ext") && !filename.endsWith(".sqm")
				&& !filename.endsWith(".hpp") && !filename.endsWith(".fsm") && !filename.endsWith(".h") && !filename.endsWith(".cpp"))
		{
			//Display message about file type not being correct and return
			JOptionPane.showMessageDialog(null, "Error: Filename ending does not match one of supported types (.sqf, .sqs, .ext, .sqm, .hpp, .fsm, .h, .cpp");
			return false;
		}
		
		//Reset to default (for the cases when the user runs different settings)
		filetypes = "(";
		methodcalls = "(";
		edges.clear();
		
		//Setup filetype and methodcalls as specified in the preferences
		//(\\#include|execvm|preprocessfile|execfsm|preprocessfilelinenumbers|addaction)
		if (Preferences.include.isSelected()) {
			methodcalls += "\\#include|";
		}
		if (Preferences.execvm.isSelected()) {
			methodcalls += "execvm|";
		}
		if (Preferences.preprocessfile.isSelected()) {
			methodcalls += "preprocessfile|";
		}
		if (Preferences.execfsm.isSelected()) {
			methodcalls += "execfsm|";
		}
		if (Preferences.preprocessfilelinenumbers.isSelected()) {
			methodcalls += "preprocessfilelinenumbers|";
		}
		if (Preferences.addaction.isSelected()) {
			methodcalls += "addaction|";
		}
		methodcalls = methodcalls.substring(0, methodcalls.length()-1); //remove last |
		methodcalls += ")";
		
		//(\\.sqf|\\.sqs|\\.ext|\\.sqm|\\.hpp|\\.fsm|\\.h|\\.cpp)
		if (Preferences.sqf.isSelected()) {
			filetypes += "\\.sqf|";
		}
		if (Preferences.sqs.isSelected()) {
			filetypes += "\\.sqs|";
		}
		if (Preferences.ext.isSelected()) {
			filetypes += "\\.ext|";
		}
		if (Preferences.sqm.isSelected()) {
			filetypes += "\\.sqm|";
		}
		if (Preferences.hpp.isSelected()) {
			filetypes += "\\.hpp|";
		}
		if (Preferences.fsm.isSelected()) {
			filetypes += "\\.fsm|";
		}
		if (Preferences.h.isSelected()) {
			filetypes += "\\.h|";
		}
		if (Preferences.cpp.isSelected()) {
			filetypes += "\\.cpp|";
		}
		filetypes = filetypes.substring(0, filetypes.length()-1); //remove last |
		filetypes += ")";
		
		//Open .dot file for writing
	    try
	    {
	    	fstream = new FileWriter("temp.dot");
	        out = new BufferedWriter(fstream);
	        
	        //TODO: Not sure if we should use this or not...
	        //Strict will make sure only one edge goes into a corresponding node (prevents crazy graphs where all the scripts reference the same file)
	        //out.write("strict digraph G\n");
	        out.write("digraph G\n");
	        //The epsilon value determines how long GraphViz can take to find a nice graph layout (default is 0.1)
	        //overlap=false looks pretty good but crashes on large missions, prism works better (according to the mailing list)
	        out.write("{overlap=prism epsilon=.0001\n");
	        String folder[] = path.split("\\\\");
	        out.write("\"" + folder[folder.length-1] + "\" [ label = \"" + folder[folder.length-1] + "\" shape = box ];\n");
	        out.write("\"about_version\" [ label= \"Created by ArmaScriptTrace Version: " + ArmaScriptTrace.VERSION + "\" shape = box ];\n");
	        
	        //Recursively traverse files
			recursiveSearch(path, filename, "", "", 2);
			
			int result = -1;
			if (filename.equals("description.ext") || filename.equals("mission.sqm") || filename.equals("init.sqf"))
			{
				File f_desc = new File(path + "description.ext");
				File f_mission = new File(path + "mission.sqm");
				File f_init = new File(path + "init.sqf");
				
				if (!filename.equals("description.ext") && f_desc.exists())
				{
					result = JOptionPane.showConfirmDialog(null, "It looks like you are loading a mission, would you also like to load the 'description.ext' file?", "Load File", JOptionPane.YES_NO_OPTION);
					if (result == 0) //yes
						recursiveSearch(path, "description.ext", "", "", 2);
				}
				if (!filename.equals("mission.sqm") && f_mission.exists())
				{
					result = JOptionPane.showConfirmDialog(null, "It looks like you are loading a mission, would you also like to load the 'mission.sqm' file?", "Load File", JOptionPane.YES_NO_OPTION);
					if (result == 0) //yes
						recursiveSearch(path, "mission.sqm", "", "", 2);
				}
				if (!filename.equals("init.sqf") && f_init.exists())
				{
					result = JOptionPane.showConfirmDialog(null, "It looks like you are loading a mission, would you also like to load the 'init.sqf' file?", "Load File", JOptionPane.YES_NO_OPTION);
					if (result == 0) //yes
						recursiveSearch(path, "init.sqf", "", "", 2);
				}
			}
			
			out.write("}\n");
			
			//Finish writing to file, close it out
	        out.close();
	    }
	    catch (Exception e)
	    {
	          System.err.println("Error: " + e.getMessage());
	    }
	    return true;
	}
	
	private void recursiveSearch(String path, String filename, String folder_name, String parent, int depth)
	{
		try
		{
			FileReader freader = new FileReader(path + folder_name + filename);
			BufferedReader in = new BufferedReader(freader);
			
			//System.out.println("Opening: " + path + filename);
			
			//System.out.println("Creating new node: " + folder_name + filename);
			out.write("\t\"" + folder_name + filename + "\" [ label = \"" + folder_name + filename + "\" shape = ellipse ];\n");
			
			stack = false;
			String line;
			while((line = in.readLine()) != null)
			{
				//Cleanup string, makes it easier to do regex
				line = line.trim();
				line = line.toLowerCase();
				line = line.replace(";", "");
				
				
				if (line.indexOf("//") != -1)
				{
					//Cut off all the stuff after the comment
					line = line.substring(0, line.indexOf("//"));
				}
				
				// Remove block comments /* */ (could be across multiple lines)
				line = removeBlockComments(line);
				
				// This is a really good tool for quickly testing regular expressions:
				// http://www.fileformat.info/tool/regex.htm
				// Just start small and keep adding things on
				// Uses reluctant quantifiers for regex
				// http://download.oracle.com/javase/tutorial/essential/regex/quant.html
				
				String regex = ".*?" + //Zero or more characters of any type except comments
								methodcalls + //String literal (match any one)
								".*?" + //Zero or more characters for the path/filename
								filetypes + //String literal (any one of these extensions)
								".*?"; //Zero or more characters of any type
				while (line.matches(regex))
				{
					//There's a match!
					//System.out.println("Match! - " + line);
					String s_array[] = line.split("(\"|\'|<|>)");

					//Remove matching item and get ready for next regex in case we have multiple items in a single line
					line = line.replaceFirst(regex, "");
					
					//Find the file in the split array
					String new_filename = "";
					for (int i = 0; i < s_array.length; i++)
					{
						if (s_array[i].matches(".*?" + filetypes + ".*?"))
						{
							new_filename = s_array[i];
							break;
						}
					}
					
					//Update the folder that we are in
					//System.out.println("Folder name: " + folder_name);
					//System.out.println("new filename: " + new_filename);
					//System.out.println("filename: " + filename);
					if (new_filename.contains("\\"))
					{
						folder_name = new_filename;
						folder_name = new_filename.substring(0, new_filename.lastIndexOf("\\"));
						folder_name += "\\";
						folder_name = folder_name.replace("\\", "\\\\"); //replaces ALL matches
						new_filename = new_filename.substring(new_filename.lastIndexOf("\\"), new_filename.length());
						//System.out.println("Folder name updated: " + folder_name);
					}
					
					new_filename = new_filename.replace("\\", "");
					//System.out.println("new_filename after cleanup: " + new_filename);
					
					//I originally had this to check the past file but sometimes people call the same filename in a different folder
					//if (!new_filename.equals(filename) && !new_filename.equals(parent)) //Don't call the same file or the parent (no loops)
					if (!new_filename.equals(parent)) //Don't call on the parent (no loop)
					{
						if (!edges.contains(filename + folder_name + new_filename))
						{
							//System.out.println("Checking for file: " + path + folder_name + new_filename);
							File temp_f = new File(path + folder_name + new_filename); //file path including folder
							//System.out.println("and checking for file: " + path + new_filename);
							File temp_f_root = new File(path + new_filename); //file path that refers back to the root folder
							if (temp_f.exists())
							{
								out.write("\t\"" + filename + "\" -> \"" + folder_name + new_filename + "\" [ color = black ];\n");
								//System.out.println("New edge: " + filename + " " + folder_name + new_filename);
								//add edge to list
								edges.add(filename + folder_name + new_filename);
								//Recursively call search on this file
								//System.out.println("Recursive call: " + path + " " + new_filename + " " + folder_name + " " + filename);
								recursiveSearch(path, new_filename, folder_name, filename, depth+1);
							}
							else if (temp_f_root.exists())
							{
								out.write("\t\"" + filename + "\" -> \"" + new_filename + "\" [ color = black ];\n");
								//System.out.println("New edge: " + filename + " " + new_filename);
								//add edge to list
								edges.add(filename + new_filename);
								//Recursively call search on this file
								//System.out.println("Recursive call: " + path + " " + new_filename + " " + folder_name + " " + filename);
								folder_name = "";
								recursiveSearch(path, new_filename, folder_name, filename, depth+1);
							}
							else //if not, add node and link for missing file
							{
								//System.out.println("Adding missing file node: " + folder_name + new_filename);
								out.write("\t\"" + new_filename + "\" [ label = \"" + folder_name + new_filename + " (Cannot Find File)\" shape = ellipse color = red ];\n");
								out.write("\t\"" + filename + "\" -> \"" + folder_name + new_filename + "\" [ color = red ];\n");
							}
						}
						//else
							//System.out.println("Edge already accounted for: " + filename + " " + folder_name + new_filename);
					}
					//else
						//System.out.println("Skipping file because of loop: " + folder_name + new_filename);
				}
			}
			
			in.close();
		}
	    catch (Exception e)
	    {
	          System.err.println("Error: " + e.getMessage());
	    }
	}

	private String removeBlockComments(String line)
	{
		if (stack == true) //need to close a block comment
		{
			if (line.indexOf("*/") != -1)
			{
				line = line.replace(line.substring(0, line.indexOf("*/")+2), "");
				stack = false;
				line = removeBlockComments(line);
			}
			else
				line = line.replace(line.substring(0, line.length()), "");
		}
		else
		{
			if (line.indexOf("/*") != -1)
			{
				String temp_line = line.substring(0, line.indexOf("/*"));
				line = line.replace(line.substring(0, line.indexOf("/*")), "");
				stack = true;
				line = removeBlockComments(line);
				line = temp_line + line;
			}
		}
		
		return line;
	}
}
