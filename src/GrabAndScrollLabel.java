import java.awt.Container;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JViewport;
import javax.swing.event.MouseInputAdapter;

public class GrabAndScrollLabel extends JLabel
{
	  public GrabAndScrollLabel(ImageIcon i)
	  {
		  super(i);

		  MouseInputAdapter mia = new MouseInputAdapter()
		  {
			  int xDiff, yDiff;

			  Container c;

		      public void mouseDragged(MouseEvent e)
		      {
		    	  c = GrabAndScrollLabel.this.getParent();
			     if (c instanceof JViewport)
			     {
			          JViewport jv = (JViewport) c;
			          Point p = jv.getViewPosition();
			          int newX = p.x - (e.getX() - xDiff);
			          int newY = p.y - (e.getY() - yDiff);
		
			          int maxX = GrabAndScrollLabel.this.getWidth() - jv.getWidth();
			          int maxY = GrabAndScrollLabel.this.getHeight() - jv.getHeight();
			          if (newX < 0)
			            newX = 0;
			          if (newX > maxX)
			            newX = maxX;
			          if (newY < 0)
			            newY = 0;
			          if (newY > maxY)
			            newY = maxY;
		
			          jv.setViewPosition(new Point(newX, newY));
		        }
		      }

	      public void mousePressed(MouseEvent e)
	      {
	    	  setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
	    	  xDiff = e.getX();
	    	  yDiff = e.getY();
	      }

	      public void mouseReleased(MouseEvent e)
	      {
	    	  setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	      }
	    };
	    addMouseMotionListener(mia);
	    addMouseListener(mia);
	}
}
