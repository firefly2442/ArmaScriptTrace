import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;


public class PNGGenerator
{
	JProgressBar progressBar;
	int image_index = 0;
	
	public PNGGenerator()
	{
		//Constructor
	}
	
	public void generatePNG(GUI theGUI)
	{
		try
		{
			JFrame frame = new JFrame("Loading...");
	        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	        
	        Container content = frame.getContentPane();
	        progressBar = new JProgressBar();
	        progressBar.setIndeterminate(true);
	        progressBar.setStringPainted(true);
	        progressBar.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
	        content.add(progressBar, BorderLayout.NORTH);
	        
	        
	        frame.pack();
	        frame.setLocationRelativeTo(GUI.frame);
	        frame.setVisible(true);
			
	        image_index++;
	        Task task = new Task(theGUI, frame, image_index);
	        task.execute();
		}
	    catch (Exception e)
	    {
	          System.err.println("Error: " + e.getMessage());
	    }
	}
}


class Task extends SwingWorker<Void, Void>
{
	GUI theGUI;
	JFrame loading_frame;
	int image_index = 0;
	Preferences tprefs;
	
	public Task(GUI tGUI, JFrame f, int img_index)
	{
		theGUI = tGUI;
		loading_frame = f; //loading window
		image_index = img_index;
	}
	
    public Void doInBackground()
    {
    	try
    	{
    		//Note: in order for Graphviz to run, I had to run neato.exe -c AND dot.exe -c to create the config file
    		//then it stopped complaining about no layout engine support
    		//Commands for GraphViz:
    		//http://www.graphviz.org/doc/info/command.html
    		Process prc;
    		if (Preferences.graphics.getSelectedItem() == "Tree Mode")
    		{
    			//Tree mode
    			prc = Runtime.getRuntime().exec("./Graphviz/dot.exe -T png temp.dot -o temp"+image_index+".png");
    			prc.waitFor();
    			prc = Runtime.getRuntime().exec("./Graphviz/dot.exe -T jpg temp.dot -o temp"+image_index+".jpg");
    			prc.waitFor();
    			prc = Runtime.getRuntime().exec("./Graphviz/dot.exe -T pdf temp.dot -o temp"+image_index+".pdf");
    			prc.waitFor(); //This is VERY important, don't continue until GraphViz has finished
    		}
    		else
    		{
    			//Cluster mode
    			prc = Runtime.getRuntime().exec("./Graphviz/neato.exe -T png temp.dot -o temp"+image_index+".png");
    			prc.waitFor();
    			prc = Runtime.getRuntime().exec("./Graphviz/neato.exe -T jpg temp.dot -o temp"+image_index+".jpg");
    			prc.waitFor();
    			prc = Runtime.getRuntime().exec("./Graphviz/neato.exe -T pdf temp.dot -o temp"+image_index+".pdf");
    			prc.waitFor(); //This is VERY important, don't continue until GraphViz has finished
    		}
	    }
	    catch (Exception e)
	    {
	    	System.err.println("doInBackground Error: " + e.getMessage());
	 	}

        return null;
    }
    public void done()
    {
		// Image generated
    	loading_frame.dispose();
    	
    	ImageIcon the_image = new ImageIcon("temp"+image_index+".png");

		GUI.png_image.setIcon(the_image);
		the_image.getImage().flush(); //this is needed to ensure the image gets refreshed if it is run multiple times
		
		GUI.open_script.setEnabled(true); //enable OPEN menu item since we're finished
		GUI.save_menuItem.setEnabled(true); //Enable saving image menu item
		GUI.reloadButton.setEnabled(true); //Enable reload button since we just ran a file
		
		//check file size to see if GraphViz generation worked or not
		File filePNG = new File("temp"+image_index+".png");
		if (filePNG.length() == 0)
		{
			//file size is 0, must not have worked
			JOptionPane.showMessageDialog(new Frame(), "Unable to load image.  It's either too big or we're out of memory.  Sorry, try Tree Mode, it usually results in" +
					" smaller images than Cluster Mode.", "GraphViz Generation Error", JOptionPane.ERROR_MESSAGE);
		}
    }
}
