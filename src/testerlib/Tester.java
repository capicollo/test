package testerlib;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import testerlib.annotations.Command;
import testerlib.annotations.View;

/**
 *
 * @author Edoardo Savoia
 */
public class Tester implements ActionListener {

    public static boolean load(Class<?> aClass) {

	// error if !aClass is @Testable annotated
	if (aClass == null) {
	    System.out.println("Tester.load(): aClass null");
	    return false;
	}

	if (!AnnotationHelper.isTestable(aClass)) {
	    System.out.println("Tester.load(): aClass not Testable");
	    return false;
	}

	TesterHolder.INSTANCE.tClass = aClass;

	// pass controll to instance
	return TesterHolder.INSTANCE.load();
    }

    public static void runTest() {
	// instruct instance in order to do
	// init() -> tObject gets created
	// show and forget (#REQUIRES exitOnClose)
	//    \->buildUI (#REQUIRES tObject to be correctly init'ed)
	if (!TesterHolder.INSTANCE.init()) {
	    System.out.println("Tester.runTest(): cannot init test");
	    return;
	}
	TesterHolder.INSTANCE.show();
    }


    public static void viewCreated() {
	TesterHolder.INSTANCE.getViewField();
    }

    public static void viewDestroyed() {
	TesterHolder.INSTANCE.destroyView();
    }

    public static void Log(String txt) {
	TesterHolder.INSTANCE.appendToLog(txt);
    }
    
    private Map<String, String> buttons = new HashMap<String, String>();
    private Map<String, Method> actions = new HashMap<String, Method>();
    private Class<?> tClass = null;
    private Object tObject = null;
    private Field fieldView = null;
    private JFrame view = null;
    private LazyView viewContainer = null;
    private JTextArea logArea = null;

    private Tester() {
    }

    private boolean load() {
	// aClass is not null & is testable
	// aClass MUST HAVE at least one command
	// get commands
	Map<Method, Command> annotionsAndMethods = AnnotationHelper.getAnnotionsAndMethods(tClass, Command.class);
	// fill buttons & actions
	Set<Method> keySet = annotionsAndMethods.keySet();
	if (keySet.isEmpty()) {
	    return false;
	}
	for (Method mtd : keySet) {
	    Command ann = annotionsAndMethods.get(mtd);
	    buttons.put(ann.buttonName(), ann.actionCommand());
	    actions.put(ann.actionCommand(), mtd);
	}
	// get view
	Field[] viewFields = AnnotationHelper.getAnnotatedFields(tClass, View.class);
	if (viewFields.length == 1) {
	    fieldView = viewFields[0];
	}
	else {
	    fieldView = null;
	}

	return true;
    }

    private boolean init() {
	// tObj gets created
	// if @INIT is present use that method
	Method initializer = AnnotationHelper.getInitializer(tClass);
	boolean ret = true;
	if (initializer == null) {
	    // default constructor
	    try {
		tObject = tClass.newInstance();
	    } catch (InstantiationException ex) {
		Logger.getLogger(Tester.class.getName()).log(Level.SEVERE, null, ex);
		ret = false;
		tObject = null;
	    } catch (IllegalAccessException ex) {
		Logger.getLogger(Tester.class.getName()).log(Level.SEVERE, null, ex);
		ret = false;
		tObject = null;
	    }
	}
	else {
	    try {
		// init constructor
		tObject = initializer.invoke(null, (Object[]) null);
	    } catch (IllegalAccessException ex) {
		Logger.getLogger(Tester.class.getName()).log(Level.SEVERE, null, ex);
		ret = false;
		tObject = null;
	    } catch (IllegalArgumentException ex) {
		Logger.getLogger(Tester.class.getName()).log(Level.SEVERE, null, ex);
		ret = false;
		tObject = null;
	    } catch (InvocationTargetException ex) {
		Logger.getLogger(Tester.class.getName()).log(Level.SEVERE, null, ex);
		ret = false;
		tObject = null;
	    }

	}
	return ret;
    }

    private void getViewField() {
	if (viewContainer == null) {
	    return;
	}
	if (fieldView != null) {
	    try {
		// get & add FieldView if present
		Object get = fieldView.get(tObject);
		if (get != null) {
		    ((JComponent) get).setAlignmentX(0.0F);
		    ((JComponent) get).setAlignmentY(0.0F);
		    viewContainer.addView((Component) get);
		}
	    } catch (IllegalArgumentException ex) {
		Logger.getLogger(Tester.class.getName()).log(Level.SEVERE, null, ex);
	    } catch (IllegalAccessException ex) {
		Logger.getLogger(Tester.class.getName()).log(Level.SEVERE, null, ex);
	    }
	}
	else {
	    viewContainer.setText("No view defined");
	}

    }

    private void destroyView() {
	viewContainer.removeView();
	viewContainer.setText("View Destroyed");
	viewContainer.validate();
    }

    private void buildUI() {
	if (view != null) {
	    appendToLog("view already built");
	    return;
	}
	view = new JFrame();
	view.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

	view.setTitle("Test: " + tClass.getName());
	view.getContentPane().setLayout(new BoxLayout(view.getContentPane(), BoxLayout.Y_AXIS));

	JPanel buttonsPanel = new JPanel();
	buttonsPanel.setAlignmentX(0.0F);
	buttonsPanel.setSize(new java.awt.Dimension(400, 300));
	buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));

	JButton[] buildButtons = buildButtons();
	for (JButton jButton : buildButtons) {
	    buttonsPanel.add(jButton);
	}

	view.getContentPane().add(buttonsPanel);


	/*
	 * OLD
	 *
	 * try { // get & add FieldView if present Object get =
	 * fieldView.get(tObject); ((Component) get).setName("FIELD");
	 * ((JComponent) get).setAlignmentX(0.0F); ((JComponent)
	 * get).setAlignmentY(0.0F); view.getContentPane().add((Component) get);
	 * } catch (IllegalArgumentException ex) {
	 * Logger.getLogger(Tester.class.getName()).log(Level.SEVERE, null, ex);
	 * } catch (IllegalAccessException ex) {
	 * Logger.getLogger(Tester.class.getName()).log(Level.SEVERE, null, ex);
	 * } catch (NullPointerException ex){
	 * Logger.getLogger(Tester.class.getName()).log(Level.SEVERE, null, ex);
	 * System.out.println("field null"); }
	 */

	// new
	viewContainer = new LazyView();
	viewContainer.setText("Waiting for view");
	view.getContentPane().add(viewContainer);

	getViewField();

	logArea = new JTextArea(5, 1);
	logArea.setAlignmentX(0.0F);
	logArea.setAlignmentY(0.0F);

	JScrollPane scrollPane = new JScrollPane(logArea);

	view.getContentPane().add(scrollPane);
	logArea.append("LOG Area\n");

    }

    private void show() {
	buildUI();
	Runnable doWorkRunnable = new Runnable() {

	    @Override
	    public void run() {
		// must run in edt
		view.pack();
		view.setVisible(true);
	    }
	};
	SwingUtilities.invokeLater(doWorkRunnable);


    }

    private JButton[] buildButtons() {
	Set<String> keySet = buttons.keySet();
	JButton[] jButtons = new JButton[keySet.size()];
	int cnt = 0;
	for (String name : keySet) {
	    JButton btn = new JButton();
	    btn.setText(name);
	    btn.setActionCommand(buttons.get(name));
	    btn.addActionListener(this);
	    jButtons[cnt] = btn;
	    cnt++;
	}
	return jButtons;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
	String actionCommand = e.getActionCommand();
	Method btnMethod = actions.get(actionCommand);

	try {
	    // exec btnMethod on tObj
	    btnMethod.invoke(tObject, (Object[]) null);
	    // log action
	} catch (IllegalAccessException ex) {
	    Logger.getLogger(Tester.class.getName()).log(Level.SEVERE, null, ex);
	} catch (IllegalArgumentException ex) {
	    Logger.getLogger(Tester.class.getName()).log(Level.SEVERE, null, ex);
	} catch (InvocationTargetException ex) {
	    Logger.getLogger(Tester.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    private void appendToLog(String txt) {
	if (logArea != null) {
	    logArea.append(txt);
	    logArea.append("\n");
	}
	else {
	    System.out.println(txt);
	}
    }

    private static class TesterHolder {

	private static final Tester INSTANCE = new Tester();
    }
}
