package ru.itskekoff.utils;

import ru.itskekoff.bots.Bot;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicBoolean;

public class CaptchaUtils extends JFrame {
    private JTextField inputBox;
    private JButton enterButton;

    public CaptchaUtils() {
        JPanel panel = new JPanel(new FlowLayout());
        setSize(250, 250);
        setBounds(0, 0, 250, 250);
        setTitle("Captcha");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        enterButton = new JButton("Send");
        inputBox = new JTextField(10);
        panel.add(inputBox);
        panel.add(enterButton);
        add(panel);
    }

    public boolean processCaptcha(BufferedImage image, Bot bot) {
        Graphics g = getGraphics();
        g.drawImage(image, 60, 75, null);
        AtomicBoolean solved = new AtomicBoolean(false);
        inputBox.addActionListener(e -> {
            bot.sendMessage(inputBox.getText());
            inputBox.setText("");
            solved.set(true);
        });
        enterButton.addActionListener(e -> {
            bot.sendMessage(inputBox.getText());
            inputBox.setText("");
            solved.set(true);
        });
        while (true) {
            if (bot.getSession().isChannelOpen()) {
                if (solved.get()) {
                    return true;
                }
            } else {
                return true;
            }
        }
    }
}
