import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatRoomFrame extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;

    private JTextArea chatTextArea;
    private JTextField chatInputField;
    private JButton sendButton;
    private String nickname;

    Client c = null;

    final String lobbyChatTag = "LOBBYCHAT"; // 채팅태그

    ChatRoomFrame(Client _c) {
        c = _c;

        setTitle("익명 채팅방");
        setSize(400, 300);
        setLayout(new BorderLayout());

        chatTextArea = new JTextArea();
        chatTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatTextArea);
        add(scrollPane, BorderLayout.CENTER);

        chatInputField = new JTextField();
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
        String msg = lobbyChatTag + "//" + nickname + " : " + chatInputField.getText() + "\n";
        c.sendMsg(msg);
        chatInputField.setText("");
    }

    // 채팅을 받아서 메세지로 붙여보내는 메소드
    public void appendMsg(String msg) {
        chatTextArea.append(msg);
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