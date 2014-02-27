import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import org.w3c.dom.ls.LSInput;

public class GUI {
	
	GUI(){
		
		final JFrame jframe = new JFrame();
		jframe.setBounds(111, 111, 300, 400);
		
		jframe.setLayout(new BorderLayout());
		
		Container pane = jframe.getContentPane();
		
		//Create a file chooser
		final JFileChooser fc = new JFileChooser();

		//In response to a button click:
		//int returnVal = fc.showOpenDialog(aComponent);
		
		
		final DefaultListModel<String> listModel = new DefaultListModel<String>();
		listModel.addElement("eins");
		listModel.addElement("zwo");
		
		JList<String> list = new JList<String>(listModel); //data has type Object[]
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL);
		list.setVisibleRowCount(-1);

		JScrollPane listScroller = new JScrollPane(list);
		listScroller.setPreferredSize(new Dimension(250, 80));
		
		final JButton button1 = new JButton("Open");
		final JButton button2 = new JButton("Remove");
		
		button1.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
			    //Handle open button action.
			    if (e.getSource() == button1) {
			        int returnVal = fc.showOpenDialog(jframe);

			        if (returnVal == JFileChooser.APPROVE_OPTION) {
			            File file = fc.getSelectedFile();
			            //This is where a real application would open the file.
			            listModel.addElement(file.getName());	            
			            System.out.println("Opening: " + file.getName() + ".");
			        } else {
			            System.out.println("Open command cancelled by user.");
			        }
			   } 
			}
			
		
		});
		//pane.add(new FlowLayout(), BorderLayout.WEST);
		pane.add(button1, BorderLayout.WEST);
		pane.add(button2, BorderLayout.WEST);
		
		pane.add(listScroller, BorderLayout.CENTER);
		
		//pane.
		
		jframe.setVisible(true);
//		jframe.
		
	}
}
