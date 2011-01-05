import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;



public class Preferences
{
	public static JLabel option_1 = new JLabel("Graphics Display Mode:");
	private static String[] graphics_mode = {"Tree Mode", "Cluster Mode"};
    public static JComboBox graphics = new JComboBox(graphics_mode);
    
    public static JLabel option_2 = new JLabel("Filter filetypes:");
    public static JCheckBox sqf = new JCheckBox(".sqf", true);
    public static JCheckBox sqs = new JCheckBox(".sqs", true);
    public static JCheckBox ext = new JCheckBox(".ext", true);
    public static JCheckBox sqm = new JCheckBox(".sqm", true);
    public static JCheckBox hpp = new JCheckBox(".hpp", true);
    public static JCheckBox fsm = new JCheckBox(".fsm", true);
    public static JCheckBox h = new JCheckBox(".h", true);
    public static JCheckBox cpp = new JCheckBox(".cpp", true);
    
    public static JLabel option_3 = new JLabel("Filter calls:");
    public static JCheckBox execfsm = new JCheckBox("execfsm", true);
    public static JCheckBox preprocessfilelinenumbers = new JCheckBox("preprocessfilelinenumbers", true);
    public static JCheckBox addaction = new JCheckBox("addaction", true);
    public static JCheckBox preprocessfile = new JCheckBox("preprocessfile", true);
    public static JCheckBox execvm = new JCheckBox("execvm", true);
    public static JCheckBox include = new JCheckBox("#include", true);
    
	public Preferences()
	{
		//Constructor
		option_1.setToolTipText("This will set the way the nodes are displayed in the image.");
		graphics.setSelectedIndex(0);
		
		option_2.setToolTipText("You can specify to follow only certain types of files.");
		
		option_3.setToolTipText("You can specify to follow only certain method calls in the scripts.");
	}
}
