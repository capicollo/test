package testerlib;
// JPanel with CardLayout the first "card" shows a textField
// Tester can: flip card setText
// TestedObject must notify changes (CREATE | UPDATED | DESTROYED)

import java.awt.*;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;


/**
 * @date Apr 13, 2012
 * @author Edoardo Savoia
 */
public class LazyView extends JPanel {

    private JPanel firstPanel;
    private JLabel textLabel;
    private CardLayout layout;
    private Component addedComponent;
    
    
    public void setText(String txt){
	textLabel.setText(txt);
    }

    public void showText(){
	layout.show(this, "first");
    }

    public void removeView(){
	if (addedComponent != null) {
	    remove(addedComponent);
	    addedComponent = null;
	}

    }

    public void addView(Component cmpt){
	if (addedComponent != null) {
	    Tester.Log("LazyView.addView(): addedComponent not null");
	    return;
	}
	addedComponent = cmpt;
	add(addedComponent, "second");
	layout.show(this, "second");
    }

    public void showView(){
	layout.show(this, "second");
    }


    private Component getComponentByName(String name){
	Component[] components = getComponents();
	Component result = null;
	for (Component component : components) {

	    if (name.equals(component.getName())) {
		result = component;
		break;
	    }
	}
	return result;

    }
    /**
     * Creates new form LazyPanel
     */
    public LazyView() {
	initComponents();
    }


    private void initComponents() {
        firstPanel = new JPanel();
        textLabel = new JLabel();

	layout = new CardLayout(5, 5);
        setAlignmentX(0.0F);
        setAlignmentY(0.0F);
        setName("ViewPanel");
	setLayout(layout);

        firstPanel.setAlignmentX(0.0F);
        firstPanel.setAlignmentY(0.0F);
        firstPanel.setMinimumSize(new Dimension(100, 100));
        firstPanel.setName("firstPanel");
	firstPanel.setLayout(new GridBagLayout());

        textLabel.setHorizontalAlignment(SwingConstants.CENTER);
        textLabel.setText("Default Text");
	textLabel.setName("textLabel");
	firstPanel.add(textLabel, new GridBagConstraints());
        add(firstPanel, "first");
	
    }

}





