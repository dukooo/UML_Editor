package umleditor;

// $Id: TextContents.java,v 1.0 2012/10/04 13:57:18 dalamb Exp $
import TextEditor.*;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.*;

import javax.imageio.ImageIO;

//import java.util.*;
import ca.queensu.cs.dal.edfmwk.doc.DocumentException;
import ca.queensu.cs.dal.edfmwk.doc.StringSequence;
import ca.queensu.cs.dal.edfmwk.doc.StringSequenceInputStream;
import java.awt.FileDialog;
import java.awt.Graphics2D;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import net.sourceforge.plantuml.GeneratedImage;
import net.sourceforge.plantuml.SourceFileReader;

/**
 * Internal representation of a text document.
 *<p>
 * Copyright 2010-2011 David Alex Lamb.
 * See the <a href="../doc-files/copyright.html">copyright notice</a> for details.
 */
public class UMLContents
    extends javax.swing.text.PlainDocument
    implements StringSequence
{
    private int bufferSize;
    private char[] buffer;

    /**
     * Constructs an empty text file contents.
     */
    public UMLContents() {
	super();
	//bufferSize = 100;
	//buffer = new char[bufferSize];
        bufferSize = "@startuml\n\n@enduml".length();
        buffer = "@startuml\n\n@enduml".toCharArray();
    } // end constructor

    /**
     * Reads the entire document, and closes the stream from which it is read.
     * @param in Where to read the document from.
     * @throws IOException if any I/O errors occur, in which case it will have
     * closed the stream.
     */
    public void open(InputStream in)
	throws IOException
    {
	//System.err.println("Open...");	
	Reader rdr = new InputStreamReader(in);
	try {
	    int pos = 0;
	    int len;
	    while ((len = rdr.read(buffer)) > 0 ) {
		String chunk = new String(buffer,0,len);
		//System.err.println("Insert '"+chunk+"' len "+len+" at "+pos);
		insertString(pos,chunk,null);
		pos += chunk.length();
	    }
	} catch (Exception e) {
	    rdr.close();
	    //		throw new IOException(e);
	    throw new IOException(e.getLocalizedMessage());
	}
	//System.err.println("Done open");
    } // end method open

    /**
     * Writes the entire document.
     * @param out Where to write the document
     * @throws IOException if any I/O errors occur.
     */
    public void write(Writer out) // throws IOException
    {

	//System.err.println("Writing...");
	PrintWriter pr = new PrintWriter(out);
	int docLength = getLength();
	int lengthLeft = docLength;
	int pos = 0;
	while(lengthLeft>0) {
	    int len = Math.min(bufferSize,lengthLeft);
	    try {
		String line = getText(pos,len);
		pr.print(line);
		pos += len;
		lengthLeft -= len;
	    } catch (javax.swing.text.BadLocationException e) {
		break;
	    }
	}
	pr.close();
	//System.err.println("Written.");
    } // end method write

    /**
     * Saves the entire document.
     * @param out Where to write the document.
     * @throws IOException if any I/O errors occur, in which case it will have
     * closed the stream.
     */
    public void save(OutputStream out, BufferedImage png) throws IOException {
	try {
            int n = JOptionPane.showConfirmDialog(new JFrame(),"Export PNG?","Save",JOptionPane.YES_NO_OPTION);
	    write(new PrintWriter(out));
            if(n == 0) {
                FileDialog fd = new FileDialog(new JFrame(), "Save diagram png", FileDialog.LOAD);
                fd.setFile("*.png");
                fd.setVisible(true);
                if(fd.getFile() != null) {
                    File[] imageFile = fd.getFiles();
                    ImageIO.write(png, "png", imageFile[0]);
                }
            }
	} catch (Exception e) {
	    out.close();
	    //	    throw new IOException(e);
	    throw new IOException(e.getLocalizedMessage());
	}
    } // end save

    /**
     * Gets an input stream from which the document contents can be read as a
     *  stream of bytes.  This is required when running in a sandbox, where
     *  {@link javax.jnlp.FileSaveService#saveAsFileDialog} does not provide a
     *  means of supplying an output stream to which to write the internal
     *  representation. Document managers should avoid using this method
     *    wherever possible, preferring {@link #save} instead.
     * @throws DocumentException if such a stream cannot be created.
     */
    public InputStream getContentsStream() throws DocumentException
    {
	try {
	    // return new StringBytesInputStream(this);
	    return new StringSequenceInputStream(this);
	} catch (Exception e) {
	    throw new DocumentException(e);
	}
    } // end getContentStream

    /**
     * Gets a substring of the text of the document.
     * A variant on {@link javax.swing.text.Document#getText} that raises no
     *  exceptions.
     * @param start Index of the first character in the substring.
     * @param length Length of the substring
     * @return the substring, or null if the supposed substring extends beyond
     *   the bounds of the text.
     */
    public String safelyGetText(int start, int length) {
	try {
	    return getText(start,length);
	} catch (Exception e) {
	    return null;
	}
    } // end safelyGetText

} // end TextContents
