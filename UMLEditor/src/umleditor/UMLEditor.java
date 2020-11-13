package umleditor;

// $Id: TextEditor.java,v 1.2 2012/12/14 20:23:49 dalamb Exp dalamb $
import TextEditor.*;
import javax.swing.JPanel;

import java.awt.FlowLayout;

import javax.swing.JMenuBar;
// Import only those classes from edfmwk that are essential, for documentation purposes
import ca.queensu.cs.dal.edfmwk.Application;
import ca.queensu.cs.dal.edfmwk.act.AboutAction;
import ca.queensu.cs.dal.edfmwk.act.CreditAction;
import ca.queensu.cs.dal.edfmwk.act.DefaultAction;
import ca.queensu.cs.dal.edfmwk.act.HelpAction;
import ca.queensu.cs.dal.edfmwk.act.NewAction;
import ca.queensu.cs.dal.edfmwk.doc.DocumentType;
import ca.queensu.cs.dal.edfmwk.doc.DocumentManager;
import ca.queensu.cs.dal.edfmwk.i18n.Localizers;
import ca.queensu.cs.dal.edfmwk.menu.MenuDescriptor;
import ca.queensu.cs.dal.edfmwk.menu.MenuElement;
import ca.queensu.cs.dal.edfmwk.Menus;
import ca.queensu.cs.dal.edfmwk.win.CommonWindow;
import ca.queensu.cs.dal.flex.Register;
import ca.queensu.cs.dal.flex.i18n.Localizer;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import net.sourceforge.plantuml.SourceStringReader;

/**
 * A simple text editor using the document framework.
 *<p>
 * Copyright 2010 David Alex Lamb.
 * See the <a href="../doc-files/copyright.html">copyright notice</a> for details.
 */

public class UMLEditor extends Application {
    private MenuDescriptor menu;
    /* package */ MainPanel mainPanel;
    private static String title = "Simple UML Class Diagram Editor";
    private static String copyright = "2017 Queen's";
    private static String aboutMsg = title + "\nCopyright " + copyright;
    private static String packageName = "ca.queensu.cs.dal.txt";
    private static String version = "0.1";
    private static Register register;
    private final String helpURI =
	"http://cs.queensu.ca/home/dalamb/java/txt/help.html";

    /**
     * Gets the menu descriptor for the main window, which contains only menu
     *  items independent of the type of document to be displayed.
     */
    public MenuDescriptor getMainMenu() {
	if (menu==null) {
	    menu = new MenuDescriptor(Menus.getStandardMenu());
	    //System.err.println("Got standard menu.");
	    try {
			//menu.addElement(new MenuElement("Tools")); // position empty menu
			menu.addElement(new MenuElement("Help/Help", new HelpAction(helpURI), "Help contents"));
			menu.addElement(new MenuElement("Help/About", new AboutAction(aboutMsg)));
			menu.addElement(new MenuElement("Help/Credits", new CreditAction()));
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
	return menu;
    } // end getMainMenu

    /*
     * Constructs and initializes the editor.
     */
    public UMLEditor() {
		super(title);
		mainPanel = new MainPanel();
                //mainPanel.add(new JLabel());
		DocumentType fac = new UMLType();
		DocumentManager manager = getDocumentManager();
		//System.err.println("Got to return from getDocumentManager");
		if (manager!=null) {
		    manager.addExtension(fac);
		}
		// Localizers.Menu.getLocalizer().addBaseName(packageName+".Text");
		// Localizers.Message.getLocalizer().addBaseName(packageName+".Text");
		addLocalizerBaseName(packageName+".Text");
		//System.err.println("About to get main menu");
		MenuDescriptor mainMenu = getMainMenu();
		//System.err.println("Returned from get main menu");
		MenuElement newAction = mainMenu.getElement("New");
		if (newAction!=null)
		    newAction.setAction(new NewAction(fac));
		register = new Register(packageName, title, copyright, version);
		Register.setPrimaryRegister(register);
		//System.err.println("About set up main menu");
		setup(mainPanel, mainMenu);
		finishSetup();
		//System.err.println("Got to end of Text Editor constructor");
    } // end constructor TextEditor
    
    
    /**
     * Editor main program.  Constructs an instance of TextEditor and
     * waits for user interface events.
     */
    public static void main(String args[]) {
	new UMLEditor();
    } // end main
    
    public static void updateGUI(){
        try {
            JLabel jta = new JLabel();
            Application app = Application.getApplication();
            CommonWindow win = (CommonWindow) app.getActiveWindow();
            UMLDocument doc = (UMLDocument) app.getActiveDocument();
            UMLContents contents = (UMLContents) doc.getContents();
            String uml = contents.safelyGetText(0, contents.getLength());
            //String uml = IOUtils.toString(source, StandardCharsets.UTF_8);
            SourceStringReader reader = new SourceStringReader(uml);
            File png = new File("out.png");
            reader.outputImage(png);
            BufferedImage out = ImageIO.read(png);
            ImageIcon ico = new ImageIcon(out);
            jta.setIcon(ico);
            JScrollPane js = new JScrollPane(jta);
            JPanel jp = new JPanel();
            jp.add(js);
            EditPane ep = new EditPane();
            ep.setFieldDocument(contents);
            jp.add(ep);
            win.setContentPane(jp);
        } catch (IOException ex) {
            Logger.getLogger(UMLEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns the main application, so that other classes can access some
     *   global information.
     */
    public static UMLEditor getApplication() {
	return (UMLEditor) Application.getApplication();
    }

} // end class TextEditor
