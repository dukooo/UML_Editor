package umleditor;

// $Id: TextDocument.java,v 1.0 2012/10/04 13:57:18 dalamb Exp $
import TextEditor.*;
import java.awt.Container;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;

//import java.util.*;
import javax.swing.JTextArea;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
// Import only those classes from edfmwk that are essential, for documentation purposes
import ca.queensu.cs.dal.edfmwk.doc.AbstractDocument;
import ca.queensu.cs.dal.edfmwk.doc.DocumentType;
import net.sourceforge.plantuml.GeneratedImage;
import net.sourceforge.plantuml.SourceFileReader;
import ca.queensu.cs.dal.edfmwk.doc.DocumentEvent;
import ca.queensu.cs.dal.edfmwk.doc.DocumentException;
import ca.queensu.cs.dal.edfmwk.doc.DocumentListener;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.sourceforge.plantuml.SourceStringReader;
//import org.apache.commons.io.IOUtils;

/**
 * Implementation of a text document, which is (indirectly) defined in
 * terms of a Swing {@link javax.swing.Document}.
 *<p>
 * Copyright 2010 David Alex Lamb.
 * See the <a href="../doc-files/copyright.html">copyright notice</a> for details.
 */
public class UMLDocument
    extends AbstractDocument
    implements javax.swing.event.DocumentListener
{
    private static int numRows = 20;
    private static int numColumns = 80;
    private UMLContents contents;
    
    JLabel jta;
    SourceStringReader reader;
    EditPane ep = new EditPane();
    File png = new File("out.png");
    BufferedImage diagram;

    /**
     * Constructs a document representation.
     * @param type The type of the document.
     */
    public UMLDocument(DocumentType type) {
	super(type);
	contents = new UMLContents();
	contents.addDocumentListener(this);
        jta = new JLabel();
        JScrollPane js = new JScrollPane(jta);
        JPanel jp = new JPanel();
        jp.add(js);
        contents.addDocumentListener(new UMLDocumentListener());
        ep.setFieldDocument(contents);
        jp.add(ep);
        window = jp;
        ep.loaded();
    } // end TextDocument

    // Text document change listeners: all invoke the framework's own document
    // change listeners.

    /**
     * Gives notification that an attribute or set of attributes changed.
     */
    public void changedUpdate(javax.swing.event.DocumentEvent e) {
	setChanged();
    } // end changedUpdate

    /**
     * Gives notification that there was an insert into the document.
     */
    public void insertUpdate(javax.swing.event.DocumentEvent e) {
	setChanged();
    } // end insertUpdate

    /**
     * Gives notification that a portion of the document has been removed.
     */
    public void removeUpdate(javax.swing.event.DocumentEvent e) {
	setChanged();
    } // end removeUpdate

    /**
     * Saves the entire document.  After this operation completes
     * successfully, {@link #isChanged} returns <b>false</b>
     * @param out Where to write the document.
     * @throws IOException if any I/O errors occur, in which case it will have
     * closed the stream; isChanged() is unchanged.
     */
    public void save(OutputStream out) throws IOException {
	contents.save(out, diagram);
	setChanged(false);
    } // save

    /**
     * Gets an input stream from which the document contents can be read as a
     *  stream of bytes.  This is required when running in a sandbox, where
     *  {@link javax.jnlp.FileSaveService#saveAsFileDialog} does not provide a
     *  means of supplying an output stream to which to write the internal
     *  representation. Document managers should avoid using this method
     *    wherever possible, preferring {@link #save} instead.
     * @throws IOException if such a stream cannot be created.
     */
    public InputStream getContentsStream() throws DocumentException {
	return contents.getContentsStream();
    } // getContentsStream

    /**
     * Reads the entire document, and closes the stream from which it is read.
     * @param in Where to read the document from.
     * @throws IOException if any I/O errors occur, in which case it will have
     * closed the stream.
     */
    public void open(InputStream in)
	throws IOException
    {
	contents.open(in);
        try {
            String uml = contents.safelyGetText(0, contents.getLength());
            reader = new SourceStringReader(uml);
            reader.outputImage(png);
            diagram = ImageIO.read(png);
            ImageIcon ico = new ImageIcon(diagram);
            jta.setIcon(ico);
            JScrollPane js = new JScrollPane(jta);
            JPanel jp = new JPanel();
            jp.add(js);
            contents.addDocumentListener(new UMLDocumentListener());
            ep.setFieldDocument(contents);
            jp.add(ep);
            window = jp;
            ep.loaded();
	} catch (IOException e) {
		e.printStackTrace();
	}
	setChanged(false);
    } // open

    /**
     * Gets the contents of the text document, for those few methods within
     *    this package that need direct access (such as actions).
     */
    UMLContents getContents() { return contents; }
    
    public class UMLDocumentListener implements javax.swing.event.DocumentListener {
        @Override
        public void changedUpdate(javax.swing.event.DocumentEvent e) {
            ep.loaded();
            reader = new SourceStringReader(ep.getSourceText());
            try {
                reader.outputImage(png);
                BufferedImage out = ImageIO.read(png);
                ImageIcon ico = new ImageIcon(out);
                jta.setIcon(ico);
            } catch (IOException ex) {
                Logger.getLogger(UMLDocument.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void insertUpdate(javax.swing.event.DocumentEvent e) {
            ep.loaded();
            reader = new SourceStringReader(ep.getSourceText());
            try {
                reader.outputImage(png);
                BufferedImage out = ImageIO.read(png);
                ImageIcon ico = new ImageIcon(out);
                jta.setIcon(ico);
            } catch (IOException ex) {
                Logger.getLogger(UMLDocument.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void removeUpdate(javax.swing.event.DocumentEvent e) {
            ep.loaded();
            reader = new SourceStringReader(ep.getSourceText());
            try {
                reader.outputImage(png);
                BufferedImage out = ImageIO.read(png);
                ImageIcon ico = new ImageIcon(out);
                jta.setIcon(ico);
            } catch (IOException ex) {
                Logger.getLogger(UMLDocument.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
} // end class TextDocument

