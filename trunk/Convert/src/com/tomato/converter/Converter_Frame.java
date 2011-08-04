package com.tomato.converter;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.qoppa.pdf.IPassword;
import com.qoppa.pdf.PDFException;
import com.qoppa.pdfImages.PDFImages;
import com.sun.corba.se.impl.ior.ByteBuffer;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import com.zfqjava.swing.JDirChooser;
//import com.zfqjava.license.LicenseUtils;

public class Converter_Frame extends Frame{

	public static void main(String[] arg)
	{
		Converter_Frame MainFrame = new Converter_Frame();
		MainFrame.setTitle("PDF Converting Program");	
		MainFrame.setSize(480,300 );
		MainFrame.setResizable(false);
		MainFrame.setVisible(true);


	}

	TextField inputLocation = new TextField();
	TextField outLocation = new TextField();
	File pdfSouce,exportsDir;
	String imagePath="";
	public Converter_Frame() {
		// TODO Auto-generated constructor stub

		FlowLayout middleLayout = new FlowLayout(FlowLayout.LEFT,15,40);
		MyActionListener act = new MyActionListener();

		Label inputTitle = new Label("In");
		Label outTitle = new Label("Out");


		inputLocation.setEditable(false);
		inputLocation.setEnabled(false);
		inputLocation.setPreferredSize(new Dimension(330,20));


		outLocation.setEditable(false);
		outLocation.setEnabled(false);
		outLocation.setPreferredSize(new Dimension(320,20));

		Button inputBtn = new Button("Find");
		inputBtn.setPreferredSize(new Dimension(50,20));
		inputBtn.addActionListener(act);

		Button outputBtn = new Button("Out");
		outputBtn.setPreferredSize(new Dimension(50,20));
		outputBtn.addActionListener(act);

		Button convert = new Button("Convert");
		convert.setPreferredSize(new Dimension(80,20));
		convert.addActionListener(act);

		Panel top = new Panel();
		top.setBackground(Color.LIGHT_GRAY);
		top.setPreferredSize(new Dimension(480,40));

		Panel inputPanel = new Panel();
		inputPanel.setBackground(Color.LIGHT_GRAY);
		inputPanel.setPreferredSize(new Dimension(480,220));

		inputPanel.setLayout(middleLayout);
		inputPanel.add(inputTitle);
		inputPanel.add(inputLocation);
		inputPanel.add(inputBtn);

		inputPanel.add(outTitle);
		inputPanel.add(outLocation);
		inputPanel.add(outputBtn);

		Panel bottom = new Panel();
		bottom.setBackground(Color.LIGHT_GRAY);
		bottom.setPreferredSize(new Dimension(480,60));
		bottom.add(convert);


		this.add(top,BorderLayout.NORTH);
		this.add(inputPanel);
		this.add(bottom,BorderLayout.SOUTH);


		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		});
	}
	public class MyActionListener extends Frame implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			String Btn = e.getActionCommand();
			if(Btn.equals("Find"))
			{
				Frame frame = getFrame();
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Find PDF");
				FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF files","PDF");
				chooser.setFileFilter(filter);
				chooser.setAcceptAllFileFilterUsed(false);
				int ret = chooser.showOpenDialog(frame);				
				if (ret != JFileChooser.APPROVE_OPTION) {
					return;
				}

				pdfSouce = chooser.getSelectedFile();
				inputLocation.setEnabled(true);
				inputLocation.setText(pdfSouce.getAbsolutePath());
			}
			else if(Btn.equals("Out"))
			{
				imagePath = pdfSouce.getAbsolutePath();
				try
				{
					if(inputLocation.getText()==null)
					{

					}
					exportsDir = chooseDir(imagePath, this, true);    
					if (exportsDir == null)
					{
						return;
					}
					outLocation.setEnabled(true);
					outLocation.setText(exportsDir.getAbsolutePath());
				}
				finally
				{

				}
			}
			else
			{
				try
				{
					// Load the document
					PDFImages images = new PDFImages (pdfSouce.getAbsolutePath(), null);

					// get document pages
					for (int count = 0; count < images.getPageCount(); ++count)
					{
						// Save the buffered image as a JPEG
						File file = new File(pdfSouce.getAbsolutePath());
						RandomAccessFile raf;
						try {
							raf = new RandomAccessFile(file, "r");

							FileChannel channel = raf.getChannel();
							MappedByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
							PDFFile pdffile = new PDFFile(buf);
							// draw the first page to an image
							int num=pdffile.getNumPages();
							for(int i=0;i<num;i++)
							{
								PDFPage page = pdffile.getPage(i);

								//get the width and height for the doc at the default zoom				
								int width=(int)page.getBBox().getWidth();
								int height=(int)page.getBBox().getHeight();				

								Rectangle rect = new Rectangle(0,0,width,height);
								int rotation=page.getRotation();
								Rectangle rect1=rect;
								if(rotation==90 || rotation==270)
									rect1=new Rectangle(0,0,rect.height,rect.width);

								//generate the image
								BufferedImage img = (BufferedImage)page.getImage(
										rect.width, rect.height, //width & height
										rect1, // clip rect
										null, // null for the ImageObserver
										true, // fill background with white
										true  // block until drawing is done
										);

								ImageIO.write(img, "png", new File(exportsDir.getAbsolutePath(),pdfSouce.getName().replaceAll(".pdf", "")+i+".png"));
							}
						} 
						catch (FileNotFoundException e1) {
							System.err.println(e1.getLocalizedMessage());
						} catch (IOException Ioe) {
							System.err.println(Ioe.getLocalizedMessage());
						}
						
					}
					//						File outFile = new File (exportsDir, "page" + count + ".png");
					//						images.savePageAsPNG(count, outFile.getAbsolutePath(), 144);

					
					// Show message
					JOptionPane.showMessageDialog(this, "Files were exported to:\n" + exportsDir.getAbsolutePath());
				} catch (PDFException pdfE) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog (this, pdfE.getMessage());
					
				}
				finally{}
			}
		}
	}
	protected Frame getFrame() {
		for (Container p = getParent(); p != null; p = p.getParent()) {
			if (p instanceof Frame) {
				return (Frame) p;
			}
		}
		return null;
	}

	public File chooseDir (String initDir, Component parent, boolean createDirs)
	{
		JDirChooser dirChooser = new JDirChooser ();
		if (initDir != null)
		{
			File fInitDir = new File (initDir);
			if (fInitDir.exists())
			{
				dirChooser.setSelectedFile(fInitDir);
			}
		}

		// Set properties
		dirChooser.setSize (dirChooser.getWidth(), dirChooser.getHeight() / 2);
		dirChooser.setMultiSelectionEnabled(false);
		dirChooser.setFileHidingEnabled(true);
		dirChooser.setFileSelectionMode(JDirChooser.DIRECTORIES_ONLY);
		dirChooser.putClientProperty("JDirChooser.recursiveModeEnabled", Boolean.FALSE);

		// choose folder
		// Show the dialog
		String msg = "Please select directory";
		Icon icon = UIManager.getIcon ("OptionPane.informationIcon");
		int option = dirChooser.showDialog(parent, msg, icon);
		if(option == JDirChooser.OK_OPTION) 
		{
			File chooseDir = dirChooser.getSelectedFile();
			if (createDirs && chooseDir.exists() == false)
			{
				chooseDir.mkdirs();
			}

			return chooseDir;
		} 
		else
		{
			return null;
		}
	}
}