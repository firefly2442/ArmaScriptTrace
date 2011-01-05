import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;


public class ImageFilter implements FilenameFilter
{
	private ArrayList<String> extensions = new ArrayList<String>();
	
	public ImageFilter()
	{
		extensions.add(".png");
		extensions.add(".jpg");
		extensions.add(".pdf");
		extensions.add(".dot");
	}
  
	public boolean accept(File dir, String name)
	{
		for (int i = 0; i < extensions.size(); i++)
		{
			if (name.endsWith(extensions.get(i)))
			{
				return true;
			}
		}
		return false;
	}
}
