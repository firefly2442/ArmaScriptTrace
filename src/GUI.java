import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;


public class GUI
{
	public static JScrollPane scrollPane;
	public static JMenuItem open_script;
	public static JMenu save_menuItem;
		public static JMenuItem save_png;
		public static JMenuItem save_jpg;
		public static JMenuItem save_pdf;
    public static JLabel png_image;
    public static JFrame frame;
    public static JButton reloadButton;
    
    public GUI()
    {
    	//Constructor
    	javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createGUI();
            }
        });
    }
    
    
    public void createAboutWindow()
    {
        JFrame frame = new JFrame("About ArmaScriptTrace");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
  
        JTextArea aboutText = new JTextArea();
        aboutText.insert("ArmaScriptTrace - version: " + ArmaScriptTrace.VERSION +
        								"\n\nCreated by: firefly2442" +
        								"\n\nLicense: GPL version 3", 0);
        aboutText.setEditable(false);
        
        aboutText.setPreferredSize(new Dimension(300, 100));
        frame.getContentPane().add(aboutText, BorderLayout.CENTER);
  
        frame.pack();
        frame.setVisible(true);
     }

    private void createGUI()
    {
        frame = new JFrame("Arma Script Trace");
        frame.setLayout(new BorderLayout());
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ArmaScriptTrace gui = new ArmaScriptTrace();
        frame.setJMenuBar(gui.createMenuBar());
        frame.setContentPane(createImagePane());

        frame.setSize(800, 600); //resolution/size of the main window
        frame.setVisible(true);
    }
    
    private Container createImagePane()
    {
        JPanel contentPane = new JPanel(new BorderLayout());

        png_image = new GrabAndScrollLabel(null);
        png_image.setToolTipText("Drag and drop Arma script files here.");
        scrollPane = new JScrollPane(png_image);
        
        JPanel prefsPane = new JPanel();
        prefsPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        reloadButton = new JButton("Reload");
        reloadButton.setToolTipText("This will re-run whatever Arma script file you previously loaded.");
        reloadButton.setEnabled(false); //disable on launch, only enable after we ran one file
        reloadButton.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
        	  //Reload button pressed
        	  ArmaScriptTrace.openScript(ArmaScriptTrace.lastFile);
          }
        });

        //Add top options --------------------------------------------------------

        //Set the layout for all these options
        GroupLayout layout = new GroupLayout(prefsPane);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        
        prefsPane.setLayout(layout); //<- this is very important, otherwise things appear in a line horizontally
       
        layout.setHorizontalGroup(layout.createSequentialGroup()
        		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        				.addComponent(Preferences.option_1)
        				.addComponent(Preferences.graphics))
        	    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        	        .addComponent(Preferences.option_2)
        	        .addGroup(layout.createSequentialGroup()
        	            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        	                .addComponent(Preferences.sqf)
        	                .addComponent(Preferences.sqs))
        	            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        	                .addComponent(Preferences.ext)
        	                .addComponent(Preferences.sqm))
        	            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        	                .addComponent(Preferences.hpp)
        	                .addComponent(Preferences.fsm))
    	                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        	                .addComponent(Preferences.h)
        	                .addComponent(Preferences.cpp))))
        	    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        	        .addComponent(Preferences.option_3)
        	        .addGroup(layout.createSequentialGroup()
        	            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        	                .addComponent(Preferences.execfsm)
        	                .addComponent(Preferences.preprocessfilelinenumbers))
        	            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        	                .addComponent(Preferences.addaction)
        	                .addComponent(Preferences.preprocessfile))
        	            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        	                .addComponent(Preferences.execvm)
        	                .addComponent(Preferences.include))
        	    .addComponent(GUI.reloadButton)))
        	);
        
        	layout.linkSize(SwingConstants.HORIZONTAL, Preferences.option_1, Preferences.graphics);

        	layout.setVerticalGroup(layout.createSequentialGroup()
        	    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
        	        .addComponent(Preferences.option_1)
        	        .addComponent(Preferences.option_2)
        	        .addComponent(Preferences.option_3)
        	        .addComponent(GUI.reloadButton))
        	    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        	    	.addComponent(Preferences.graphics)
        	        .addGroup(layout.createSequentialGroup()
        	            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
        	                .addComponent(Preferences.sqf)
        	                .addComponent(Preferences.ext)
        	                .addComponent(Preferences.hpp)
        	                .addComponent(Preferences.h))
        	            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
        	                .addComponent(Preferences.sqs)
        	                .addComponent(Preferences.sqm)
        	                .addComponent(Preferences.fsm)
        	                .addComponent(Preferences.cpp)))
	                .addGroup(layout.createSequentialGroup()
        	            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
        	                .addComponent(Preferences.execfsm)
        	                .addComponent(Preferences.addaction)
        	                .addComponent(Preferences.execvm))
        	            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
        	                .addComponent(Preferences.preprocessfilelinenumbers)
        	                .addComponent(Preferences.preprocessfile)
        	                .addComponent(Preferences.include))))
        	);

        prefsPane.setVisible(true);
        
        contentPane.add(prefsPane, BorderLayout.PAGE_START);
        //----------------------------------------------------
        
        contentPane.add(scrollPane, BorderLayout.CENTER);
        contentPane.setVisible(true);
        
        setupFileDrop();

        return contentPane;
    }
    
    public void SetNewImage(ImageIcon image)
    {
    	JFrame frame = new JFrame("Image");
        frame.setLayout(new BorderLayout());
        
        JLabel png_image = new GrabAndScrollLabel(image);
        JScrollPane scrollPane = new JScrollPane(png_image);
        
        frame.setContentPane(scrollPane);
        frame.setVisible(true);
    }
    
    private void setupFileDrop()
    {
        new FileDrop(png_image, new FileDrop.Listener()
        {
        	public void  filesDropped( java.io.File[] files )
            {   
	        	for( int i = 0; i < files.length; i++ )
	            {
	        		try
	                {  
	        			ArmaScriptTrace.openScript(files[i].getCanonicalPath());
	                }
	                catch( java.io.IOException e ) {}
	            }
            }
        }); // end FileDrop.Listener
    }
}
