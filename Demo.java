
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

// 1. Strength Enum
enum Strength {
    LOW, MEDIUM, HIGH, STRONG, CLOSED
}

// 2. Observer Interface
interface DefenseObserver {
    void updateArea(boolean isCleared);
    void updateStrength(Strength strength);
    void receiveMessage(String senderName, String message);
}

// 3. SuperDefence (Base Class for Units)
class SuperDefence extends JFrame implements DefenseObserver {
    protected String unitName;
    protected JLabel areaLabel;
    protected JLabel strengthLabel; // Label to display current strength
    protected JSlider slider;
    protected JCheckBox positionCheckBox;
    protected JTextArea messageArea;
    protected JTextField messageField;
    protected JButton sendButton;
    protected JButton[] controlButtons; 
    protected MainController mainController;
    protected boolean isPositionChecked = false;

    public SuperDefence(String title, MainController controller) {
        this.unitName = title;
        this.mainController = controller;
        setTitle(title);
        setSize(500, 380);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        // Area Status Label
        areaLabel = new JLabel("Area Not Cleared");
        areaLabel.setFont(new Font("Arial", Font.BOLD, 14));
        areaLabel.setBounds(20, 15, 150, 25);
        add(areaLabel);

        // Strength Status Label (Shows current Strength enum value)
        strengthLabel = new JLabel("Strength: CLOSED");
        strengthLabel.setFont(new Font("Arial", Font.BOLD, 12));
        strengthLabel.setBounds(180, 15, 150, 25);
        add(strengthLabel);

        // Position CheckBox
        positionCheckBox = new JCheckBox("Position");
        positionCheckBox.setBounds(350, 15, 100, 25);
        positionCheckBox.addItemListener(e -> {
            isPositionChecked = (e.getStateChange() == ItemEvent.SELECTED);
            mainController.recalculateButtonStates(unitName);
        });
        add(positionCheckBox);

        // Message Display Area
        messageArea = new JTextArea();
        messageArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(messageArea);
        scrollPane.setBounds(20, 140, 320, 120);
        add(scrollPane);

        // Message Input Field
        messageField = new JTextField();
        messageField.setBounds(20, 280, 230, 30);
        add(messageField);

        // Send Button
        sendButton = new JButton("Send");
        sendButton.setBounds(260, 280, 80, 30);
        sendButton.addActionListener(e -> {
            String msg = messageField.getText().trim();
            if (!msg.isEmpty()) {
                mainController.receiveMessageFromSub(unitName, msg);
                messageArea.append("Me: " + msg + "\n");
                messageField.setText("");
            }
        });
        add(sendButton);

        // Vertical Slider (0 to 100)
        slider = new JSlider(JSlider.VERTICAL, 0, 100, 0);
        slider.setBounds(400, 50, 60, 260);
        slider.setMajorTickSpacing(20);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        add(slider);
    }

    public void updateArea(boolean isCleared) {
        if (isCleared) {
            areaLabel.setText("Area Cleared");
        } else {
            areaLabel.setText("Area Not Cleared");
        }
    }

    // Receives strength notification from MainController
    public void updateStrength(Strength strength) {
        strengthLabel.setText("Strength: " + strength);
    }

    public void receiveMessage(String senderName, String message) {
        if (senderName.equals("MainController")) {
            messageArea.append("Main Controller: " + message + "\n");
        }
    }

    // Progressive button enabling based on slider level and Position Checkbox status
    public void updateButtonStatesBySlider(int sliderVal) {
        if (!isPositionChecked || controlButtons == null) {
            for (JButton btn : controlButtons) {
                btn.setEnabled(false);
            }
            return;
        }

        int totalButtons = controlButtons.length;
        int rangeStep = 100 / totalButtons;

        for (int i = 0; i < totalButtons; i++) {
            if (sliderVal >= (i + 1) * (rangeStep > 0 ? rangeStep : 20) || sliderVal == 100) {
                controlButtons[i].setEnabled(true);
            } else {
                controlButtons[i].setEnabled(false);
            }
        }
        
        if (sliderVal > 0) {
            sendButton.setEnabled(true);
        } else {
            sendButton.setEnabled(false);
        }
    }
}

// 4. Helicopter Class
class Helicopter extends SuperDefence {
    public Helicopter(MainController controller) {
        super("Helicopter", controller);

        JButton shoot = new JButton("Shoot");
        JButton missile = new JButton("Missile Oparation");
        JButton laser = new JButton("Laser Oparation");

        shoot.setBounds(20, 50, 100, 30);
        missile.setBounds(130, 50, 110, 30);
        laser.setBounds(20, 90, 120, 30);

        add(shoot);
        add(missile);
        add(laser);

        controlButtons = new JButton[]{shoot, missile, laser};
        updateButtonStatesBySlider(0);
        setVisible(true);
    }
}

// 5. Tank Class
class Tank extends SuperDefence {
    public Tank(MainController controller) {
        super("Tank", controller);

        JButton shoot = new JButton("Shoot");
        JButton missile = new JButton("Missile Oparation");
        JButton radar = new JButton("Radar Oparation");
        JButton rotate = new JButton("Rotate Shoot");

        shoot.setBounds(20, 50, 100, 30);
        missile.setBounds(130, 50, 110, 30);
        radar.setBounds(20, 90, 110, 30);
        rotate.setBounds(140, 90, 120, 30);

        add(shoot);
        add(missile);
        add(radar);
        add(rotate);

        controlButtons = new JButton[]{shoot, missile, radar, rotate};
        updateButtonStatesBySlider(0);
        setVisible(true);
    }
}

// 6. Submarine Class
class Submarine extends SuperDefence {
    public Submarine(MainController controller) {
        super("Submarine", controller);

        JButton shoot = new JButton("Shoot");
        JButton tomahawk = new JButton("Tomahawk");
        JButton sonar = new JButton("Sonar Oparation");
        JButton trident = new JButton("Trident-2");

        shoot.setBounds(20, 50, 90, 30);
        tomahawk.setBounds(120, 50, 110, 30);
        sonar.setBounds(20, 90, 100, 30);
        trident.setBounds(130, 90, 110, 30);

        add(shoot);
        add(tomahawk);
        add(sonar);
        add(trident);

        controlButtons = new JButton[]{shoot, tomahawk, sonar, trident};
        updateButtonStatesBySlider(0);
        setVisible(true);
    }
}

// 7. Main Controller Class (Subject)
class MainController extends JFrame {
    private DefenseObserver[] defenseObserverArray = new DefenseObserver[0];
    private JCheckBox areaClearCheckBox;
    private JSlider mainSlider;
    private JTextArea mainMessageArea;
    private JTextField mainMessageField;
    private JButton mainSendButton;

    public MainController() {
        setTitle("Main Controller");
        setSize(550, 420);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Area Clear CheckBox
        areaClearCheckBox = new JCheckBox("Area Clear");
        areaClearCheckBox.setBounds(400, 20, 120, 25);
        areaClearCheckBox.addItemListener(e -> {
            boolean isCleared = (e.getStateChange() == ItemEvent.SELECTED);
            notifyAreaClear(isCleared);
        });
        add(areaClearCheckBox);

        // Main Slider
        mainSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
        mainSlider.setBounds(30, 80, 480, 50);
        mainSlider.setMajorTickSpacing(20);
        mainSlider.setPaintTicks(true);
        mainSlider.setPaintLabels(true);
        mainSlider.addChangeListener(e -> {
            int val = mainSlider.getValue();
            Strength str = calculateStrength(val);
            notifyStrength(str);       // Broadcasts strength enum to all units
            notifySliderChanged(val);  // Updates button states based on slider
        });
        add(mainSlider);

        // Main Message Display Area
        mainMessageArea = new JTextArea();
        mainMessageArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(mainMessageArea);
        scrollPane.setBounds(30, 150, 480, 120);
        add(scrollPane);

        // Broadcast Message Field & Button
        mainMessageField = new JTextField();
        mainMessageField.setBounds(30, 300, 380, 30);
        add(mainMessageField);

        mainSendButton = new JButton("Send");
        mainSendButton.setBounds(420, 300, 90, 30);
        mainSendButton.addActionListener(e -> {
            String msg = mainMessageField.getText().trim();
            if (!msg.isEmpty()) {
                broadcastMessage("MainController", msg);
                mainMessageArea.append("Main Controller: " + msg + "\n");
                mainMessageField.setText("");
            }
        });
        add(mainSendButton);
        setVisible(true);
    }

    public void addDefenseObserver(DefenseObserver observer) {
        extendArray();
        defenseObserverArray[defenseObserverArray.length - 1] = observer;
    }

    private void extendArray() {
        DefenseObserver[] tempArray = new DefenseObserver[defenseObserverArray.length + 1];
        for (int i = 0; i < defenseObserverArray.length; i++) {
            tempArray[i] = defenseObserverArray[i];
        }
        defenseObserverArray = tempArray;
    }

    public void notifyAreaClear(boolean isCleared) {
        for (DefenseObserver obs : defenseObserverArray) {
            obs.updateArea(isCleared);
        }
    }

    // Notifies all observers with the current Strength enum value
    public void notifyStrength(Strength strength) {
        for (DefenseObserver obs : defenseObserverArray) {
            obs.updateStrength(strength);
        }
    }

    public void notifySliderChanged(int val) {
        for (DefenseObserver obs : defenseObserverArray) {
            if (obs instanceof SuperDefence) {
                ((SuperDefence) obs).updateButtonStatesBySlider(val);
            }
        }
    }

    public void recalculateButtonStates(String unitName) {
        int currentSliderVal = mainSlider.getValue();
        for (DefenseObserver obs : defenseObserverArray) {
            if (obs instanceof SuperDefence) {
                SuperDefence def = (SuperDefence) obs;
                if (def.unitName.equals(unitName)) {
                    def.updateButtonStatesBySlider(currentSliderVal);
                }
            }
        }
    }

    public void broadcastMessage(String sender, String message) {
        for (DefenseObserver obs : defenseObserverArray) {
            obs.receiveMessage(sender, message);
        }
    }

    public void receiveMessageFromSub(String senderName, String message) {
        mainMessageArea.append(senderName + " : " + message + "\n");
    }

    private Strength calculateStrength(int val) {
        if (val > 85) return Strength.STRONG;
        if (val > 75) return Strength.HIGH;
        if (val > 50) return Strength.MEDIUM;
        if (val > 25) return Strength.LOW;
        return Strength.CLOSED;
    }
}

// 8. Execution Class (Starter)
class Demo {
    public static void main(String[] args) {
        MainController mainController = new MainController();
        
        mainController.addDefenseObserver(new Helicopter(mainController));
        mainController.addDefenseObserver(new Tank(mainController));
        mainController.addDefenseObserver(new Submarine(mainController));
    }
}
