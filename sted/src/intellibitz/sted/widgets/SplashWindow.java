/**
 * Copyright (C) IntelliBitz Technologies.,  Muthu Ramadoss
 * 168, Medavakkam Main Road, Madipakkam, Chennai 600091, Tamilnadu, India.
 * http://www.intellibitz.com
 * training@intellibitz.com
 * +91 44 2247 5106
 * http://groups.google.com/group/etoe
 * http://sted.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * STED, Copyright (C) 2007 IntelliBitz Technologies
 * STED comes with ABSOLUTELY NO WARRANTY;
 * This is free software, and you are welcome
 * to redistribute it under the GNU GPL conditions;
 *
 * Visit http://www.gnu.org/ for GPL License terms.
 */

/**
 * $Id: SplashWindow.java 55 2007-05-19 05:55:34Z sushmu $
 * $HeadURL: svn+ssh://sushmu@svn.code.sf.net/p/sted/code/FontTransliterator/trunk/src/intellibitz/sted/widgets/SplashWindow.java $
 */

package intellibitz.sted.widgets;

import intellibitz.sted.event.IStatusListener;
import intellibitz.sted.event.StatusEvent;

import javax.swing.BoxLayout;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import java.awt.Component;
import java.awt.Container;

public class SplashWindow
        extends JWindow
        implements IStatusListener
{
    private final JProgressBar progress;

    public SplashWindow(Component component)
    {
        final Container contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        contentPane.add(component);
        progress = new JProgressBar(0, 100);
        progress.setStringPainted(true);
        contentPane.add(progress);
        pack();
    }

    public void setProgress(int percent)
    {
        progress.setValue(percent);
    }

    public void statusPosted(StatusEvent event)
    {
        setProgress(Integer.valueOf(event.getStatus()));
    }
}
