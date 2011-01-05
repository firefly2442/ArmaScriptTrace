import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileSystemView;


 
public class ArmaScriptTrace extends JPanel implements ActionListener
{
	public static final String VERSION = "0.4";
	static GUI theGUI;
	static Preferences prefs;
	
    static JFileChooser fc;
    static DOTGenerator dot;
    static PNGGenerator image;
    
    public static String lastFile;

    public JMenuBar createMenuBar()
    {
        JMenuBar menuBar;
        JMenu menu;
        JMenuItem menuItem;
        
        fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        dot = new DOTGenerator();
        image = new PNGGenerator();

        menuBar = new JMenuBar();

        menu = new JMenu("File");
        menuBar.add(menu);

        GUI.open_script = new JMenuItem("Open Script");
        GUI.open_script.addActionListener(this);
        menu.add(GUI.open_script);
        
        GUI.save_menuItem = new JMenu("Save As...");
        GUI.save_menuItem.setEnabled(false); //Disable until an image is generated
        
        GUI.save_png = new JMenuItem("PNG Image");
        GUI.save_png.addActionListener(this);
        GUI.save_menuItem.add(GUI.save_png);
        
        GUI.save_jpg = new JMenuItem("JPG Image");
        GUI.save_jpg.addActionListener(this);
        GUI.save_menuItem.add(GUI.save_jpg);
        
        GUI.save_pdf = new JMenuItem("PDF Image");
        GUI.save_pdf.addActionListener(this);
        GUI.save_menuItem.add(GUI.save_pdf);
        
        menu.add(GUI.save_menuItem);
        
        menuItem = new JMenuItem("About");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        
        menu.addSeparator();
        menuItem = new JMenuItem("Quit");
        menuItem.addActionListener(this);
        menu.add(menuItem);

        return menuBar;
    }

	public void actionPerformed(ActionEvent e)
    {
        JMenuItem source = (JMenuItem)(e.getSource());
        
        if (source.getText() == "Open Script")
        {
        	fc.addChoosableFileFilter(new ScriptFilter());
        	
        	int returnVal = fc.showOpenDialog(ArmaScriptTrace.this);

            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
            	File file = fc.getSelectedFile();
            	openScript(file.getAbsolutePath());
            }
        }
        else if (source.getText() == "PNG Image")
        {
        	fc.addChoosableFileFilter(new PNGFilter());
        	File old_file = new File("temp"+image.image_index+".png");
        	fc.setSelectedFile(old_file);
        	//Set default location to desktop
        	FileSystemView filesys = FileSystemView.getFileSystemView();
        	fc.setCurrentDirectory(filesys.getHomeDirectory());
        	
        	int returnVal = fc.showSaveDialog(ArmaScriptTrace.this);
        	
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				try
				{
				    File new_file = fc.getSelectedFile();
				    //Save file, copy temp.png to correct location
					copyFile(old_file, new_file);
				}
			    catch (Exception e1)
			    {
			    	System.err.println("Error: " + e1.getMessage());
			    }
			}
        }
        else if (source.getText() == "JPG Image")
        {
        	fc.addChoosableFileFilter(new JPGFilter());
        	File old_file = new File("temp"+image.image_index+".jpg");
        	fc.setSelectedFile(old_file);
        	//Set default location to desktop
        	FileSystemView filesys = FileSystemView.getFileSystemView();
        	fc.setCurrentDirectory(filesys.getHomeDirectory());
        	
        	int returnVal = fc.showSaveDialog(ArmaScriptTrace.this);
        	
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				try
				{
				    File new_file = fc.getSelectedFile();
				    //Save file, copy temp.jpg to correct location
					copyFile(old_file, new_file);
				}
			    catch (Exception e1)
			    {
			          System.err.println("Error: " + e1.getMessage());
			    }
			}
        }
        else if (source.getText() == "PDF Image")
        {
        	fc.addChoosableFileFilter(new PDFFilter());
        	File old_file = new File("temp"+image.image_index+".pdf");
        	fc.setSelectedFile(old_file);
        	//Set default location to desktop
        	FileSystemView filesys = FileSystemView.getFileSystemView();
        	fc.setCurrentDirectory(filesys.getHomeDirectory());
        	
        	int returnVal = fc.showSaveDialog(ArmaScriptTrace.this);
        	
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				try
				{
				    File new_file = fc.getSelectedFile();
				    //Save file, copy temp.pdf to correct location
					copyFile(old_file, new_file);
				}
			    catch (Exception e1)
			    {
			          System.err.println("Error: " + e1.getMessage());
			    }
			}
        }
        else if (source.getText() == "About")
        {
        	theGUI.createAboutWindow();
        }
        else //Must be quit
        {
        	//Delete all image files
        	deleteImages();
        	//Exit
        	System.exit(0);
        }
    }
	
	public static void openScript(String filepath)
	{
		File file = new File(filepath);
        boolean result = dot.generateDotFile(file.getParent() + "\\", file.getName());
        if (result == true)
        {
        	image.generatePNG(theGUI); // This will also update the frame and image when done
        	lastFile = filepath; //update the lastFile string in case someone hits the reload button
        }
	}
	
	private void copyFile(File sourceFile, File destFile)
	{
		try
		{
			 if(!destFile.exists())
				 destFile.createNewFile();
	
			 FileChannel source = null;
			 FileChannel destination = null;
			 try
			 {
				 	source = new FileInputStream(sourceFile).getChannel();
				 	destination = new FileOutputStream(destFile).getChannel();
				 	destination.transferFrom(source, 0, source.size());
			 }
			 finally
			 {
				 if(source != null)
					 source.close();
				 if (destination != null)
					 destination.close();
			 }
		}
	    catch (Exception e)
	    {
	          System.err.println("Error: " + e.getMessage());
	    }
	}


    public static void main(String[] args)
    {
    	//Delete all image files, just in case there are some leftover from a previous run
    	deleteImages();
    	
    	prefs = new Preferences();
        theGUI = new GUI();
    }
    
    private static void deleteImages()
    {
    	//deletes all temp.png temp.jpg and temp.pdf files
    	ImageFilter filter = new ImageFilter();
    	File dir = new File(".");
    	File file;
    	String[] list = dir.list(filter);

    	for (int i = 0; i < list.length; i++)
    	{
    		file = new File(dir, list[i]);
    		try
    		{
    			file.delete();
    		}
			catch (Exception e1)
    	    {
    	          System.err.println("Error: " + e1.getMessage());
    	    }
    	}
    }
}
