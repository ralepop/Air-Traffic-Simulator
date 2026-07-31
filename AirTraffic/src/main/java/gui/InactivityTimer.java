package gui;

import java.awt.*;

public class InactivityTimer implements Runnable {

    private static final int TIMEOUT_MS = 60000;
    private static final int WARNING_MS = 55000;

    private long lastActivity;
    private final Frame parentFrame;

    private Dialog warningDialog;
    private Label countdownLabel;
    private boolean warningShown = false;

    private volatile boolean paused = false;

    public InactivityTimer(Frame parentFrame) {

        this.parentFrame = parentFrame;
        this.lastActivity = System.currentTimeMillis();

        Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
            lastActivity = System.currentTimeMillis();
        }, AWTEvent.MOUSE_EVENT_MASK | AWTEvent.KEY_EVENT_MASK);
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
        if (!paused) {
            lastActivity = System.currentTimeMillis(); // Resetujemo vreme po povratku
        }
    }

    @Override
    public void run() {

        while (true) {

            if (paused) {
                lastActivity = System.currentTimeMillis();
            }

            long elapsed = System.currentTimeMillis() - lastActivity;

            if (elapsed >= TIMEOUT_MS) {
                parentFrame.dispose();
                System.exit(0);
                return;
            } else if (elapsed >= WARNING_MS) {
                showWarningDialog((int) ((TIMEOUT_MS - elapsed) / 1000));
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    private void showWarningDialog(int secondsLeft) {
        EventQueue.invokeLater(() -> {
            if (!warningShown) {

                warningDialog = new Dialog(parentFrame, "Upozorenje", true);
                countdownLabel = new Label("Program ce se ugasiti za " + secondsLeft +
                        " sekundi. Zelite li da nastavite?");
                Button buttonYes = new Button("Da");
                Button buttonNo = new Button("Ne");

                buttonYes.addActionListener(al -> {
                    lastActivity = System.currentTimeMillis();
                    warningShown = false;
                    warningDialog.dispose();
                });

                buttonNo.addActionListener(al -> {
                    warningDialog.dispose();
                    parentFrame.dispose();
                    System.exit(0);
                });

                warningDialog.setLayout(new FlowLayout());
                warningDialog.add(countdownLabel);
                warningDialog.add(buttonYes);
                warningDialog.add(buttonNo);

                warningDialog.setSize(300, 150);
                warningDialog.setLocationRelativeTo(parentFrame);

                warningShown = true;
                warningDialog.setVisible(true);

            } else {
                countdownLabel.setText("Program ce se ugasiti za " + secondsLeft +
                        " sekundi. Zelite li da nastavite?");
            }
        });
    }
}
