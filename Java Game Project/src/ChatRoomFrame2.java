import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.HTMLDocument;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ChatRoomFrame2 extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;

    private JTextPane chatTextPane;
    private JTextField chatInputField;
    private JButton sendButton;
    private String nickname;

    private ImageIcon icon;
    Client c = null;

    final String chatTag = "CHAT"; // 채팅태그

    @SuppressWarnings("serial")
    ChatRoomFrame2(Client _c) {
        c = _c;

        setTitle("채팅방");
        setSize(400, 300);
        setLayout(new BorderLayout());

        icon = new ImageIcon("/Users/jeongjun-yeong/Downloads/background.jpg");

        chatTextPane = new JTextPane() {
            public void paintComponent(Graphics g) {
                g.drawImage(icon.getImage(), 0, 0, null);
                setOpaque(false);
                super.paintComponent(g);
            }
        };

        chatTextPane.setEditable(false);
        chatTextPane.setBackground(Color.cyan);
        chatTextPane.setContentType("text/html"); // HTML을 지원하도록 설정
        JScrollPane scrollPane = new JScrollPane(chatTextPane);
        add(scrollPane, BorderLayout.CENTER);

        chatInputField = new JTextField() {
            public void paintComponent(Graphics g) {
                g.drawImage(icon.getImage(), 0, 0, null);
                setOpaque(false);
                super.paintComponent(g);
            }
        };
        chatInputField.setBackground(Color.cyan);
        sendButton = new JButton("보내기");

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        inputPanel.add(chatInputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        chatInputField.addActionListener(this);
        sendButton.addActionListener(this);
    }

    // 이벤트 발생시 sendMsg(msg)를 사용해서 클라이언트로 msg를 보냄
    public void actionPerformed(ActionEvent e) {
        String msg = chatTag + "//" + nickname + ":" + chatInputField.getText() + "\n";
        c.sendMsg(msg);
        chatInputField.setText("");
    }

    // client.cf.appendMsg 메소드 내에서도 SwingUtilities.invokeLater 사용
    public void appendMsg(String msg, String owner) {
        SwingUtilities.invokeLater(() -> {
            if (msg.contains("님이 채팅방에 입장 했습니다.")) {
                String enteredNickname = msg.replace("님이 채팅방에 입장 했습니다.", "").trim();
                appendLeftAligned(enteredNickname + " 님이 채팅방에 입장하셨습니다.\n");
            } else {
                if (owner.equals(c.cf.getNickname())) {
                    appendRightAligned("나");
                    appendRightAligned(msg + "\n");
                } else {
                    appendLeftAligned(owner);
                    appendLeftAligned(msg + "\n");
                }
            }
        });
    }

    // 채팅을 받아서 메세지로 붙여보내는 메소드
    public void appendMsg2(String msg) {
        SwingUtilities.invokeLater(() -> {
            HTMLDocument doc = (HTMLDocument) chatTextPane.getStyledDocument();
            Element root = doc.getDefaultRootElement();
            try {
                doc.insertBeforeEnd(root, "<div>" + msg + "</div>");
            } catch (BadLocationException | IOException e) {
                e.printStackTrace();
            }
        });
    }

    // 오른쪽 정렬 메소드
    private void appendRightAligned(String text) {
        SwingUtilities.invokeLater(() -> {
            HTMLDocument doc = (HTMLDocument) chatTextPane.getStyledDocument();
            Element root = doc.getDefaultRootElement();
            try {
                doc.insertBeforeEnd(root, "<div style='text-align: right;'>" + text + "</div>");
            } catch (BadLocationException | IOException e) {
                e.printStackTrace();
            }
        });
    }

    // 왼쪽 정렬 메소드
    private void appendLeftAligned(String text) {
        SwingUtilities.invokeLater(() -> {
            HTMLDocument doc = (HTMLDocument) chatTextPane.getStyledDocument();
            Element root = doc.getDefaultRootElement();
            try {
                doc.insertBeforeEnd(root, "<div style='text-align: left;'>" + text + "</div>");
            } catch (BadLocationException | IOException e) {
                e.printStackTrace();
            }
        });
    }

    // 닉네임 가져오기
    public String getNickname() {
        return nickname;
    }

    // 닉네임 설정하기
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}