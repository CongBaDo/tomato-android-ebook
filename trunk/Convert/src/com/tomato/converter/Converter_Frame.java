package com.tomato.converter;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Image;
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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.zip.*;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import com.itextpdf.text.pdf.PdfReader;
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
	PDFFile pdffile;
	PdfReader reader;
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
				try {
					pdfSouce = chooser.getSelectedFile();
					File file = new File(pdfSouce.getAbsolutePath());
					RandomAccessFile raf = new RandomAccessFile(file, "r");
					FileChannel channel = raf.getChannel();
					MappedByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
					pdffile = new PDFFile(buf);
					String pdfInfo = pdffile.getStringMetadata("Producer");
					if(pdfInfo.contains("PowerPoint"))
					{
						JOptionPane.showMessageDialog(this, "PPT形式のPDFは変更できません。");
					}

					else
					{
						inputLocation.setEnabled(true);
						inputLocation.setText(pdfSouce.getAbsolutePath());
					}
				} 
				catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
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

				try {
					File file = new File(pdfSouce.getAbsolutePath());
					RandomAccessFile raf = new RandomAccessFile(file, "r");
					FileChannel channel = raf.getChannel();
					MappedByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
					pdffile = new PDFFile(buf);


				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				// draw the first page to an image
				for(int i = 1 ;i<=pdffile.getNumPages();i++ )
				{				
					PDFPage page = pdffile.getPage(i);

					//get the width and height for the doc at the default zoom 
					Rectangle rect = new Rectangle(0,0,
							(int)page.getBBox().getWidth(),
							(int)page.getBBox().getHeight());

					//generate the image

					Image image = page.getImage(
							rect.width, rect.height, //width & height
							rect, // clip rect
							null, // null for the ImageObserver
							true, // fill background with white
							true  // block until drawing is done
							);

					int w = image.getWidth(null);
					int h = image.getHeight(null);
					BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
					Graphics2D g2 = bi.createGraphics();
					g2.drawImage(image, 0, 0, null);
					g2.dispose();
					try
					{
						ImageIO.write(bi, "PNG", new File(exportsDir.getAbsolutePath(),pdfSouce.getName().replaceAll(".pdf", "_")+i+".png"));
					}
					catch(IOException ioe)
					{
						System.out.println("write: " + ioe.getMessage());
					}                
				}
				Compress zip = new Compress();
				zip.Zip();
				JOptionPane.showMessageDialog(this, "Files were exported to:\n" + exportsDir.getAbsolutePath());
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
	
	static final int BUFFER = 2048;
	public class Compress
	{
		public void Zip()
		{
			try
			{
			BufferedInputStream origin = null;
			File f = new File(exportsDir.getAbsolutePath());
	         String files[] = f.list();
	         FileOutputStream dest = new FileOutputStream(exportsDir.getAbsolutePath()+"\\"+(pdfSouce.getName().replaceAll(".pdf", ".tmt")));
	         CheckedOutputStream checksum = new CheckedOutputStream(dest, new Adler32());
	         ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(checksum));
	         //out.setMethod(ZipOutputStream.DEFLATED);
	         byte data[] = new byte[BUFFER];
	         // get a list of files from current directory
	         
	         for (int i=0; i<files.length; i++) {
	        //    System.out.println("Adding: "+files[i]);
	            FileInputStream fi = new FileInputStream(exportsDir.getAbsolutePath()+"\\"+files[i]);
	            origin = new BufferedInputStream(fi, BUFFER);
	            ZipEntry entry = new ZipEntry(files[i]);
	            out.putNextEntry(entry);
	            int count;
	            while((count = origin.read(data, 0, BUFFER)) != -1) {
	               out.write(data, 0, count);
	            }
	            origin.close();
	         }
	         out.close();
	         System.out.println("checksum: "+checksum.getChecksum().getValue());
	      } catch(Exception e) {
	         e.printStackTrace();
	      }

		}
	}
}