import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;

public class findPwFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    /* Panel */
    JPanel panel = new JPanel(new FlowLayout());

    /* Label */
    JLabel nameL = new JLabel("이름");
    JLabel nicknameL = new JLabel("닉네임");
    JLabel idL = new JLabel("id");

    /* TextField */
    JTextField name = new JTextField();
    JTextField nickname = new JTextField();
    JTextField id = new JTextField();

    Client c = null;

    JButton findPwBtn = new JButton("pw 찾기");
    JButton previousBtn = new JButton("이전 화면");

    final String fpwTag = "FPW"; // 아이디 찾기 태그

    public findPwFrame(Client _c) {
        c = _c;

        setTitle("ID 찾기");

        /* Panel 크기 작업 */
        panel.setPreferredSize(new Dimension(200, 100));

        /* Label 크기 작업 */
        nameL.setPreferredSize(new Dimension(50, 30));
        nicknameL.setPreferredSize(new Dimension(50, 30));
        idL.setPreferredSize(new Dimension(50, 30));

        /* TextField 크기 작업 */
        name.setPreferredSize(new Dimension(220, 30));
        nickname.setPreferredSize(new Dimension(220, 30));
        id.setPreferredSize(new Dimension(220, 30));

        /* Button 크기 작업 */
        findPwBtn.setPreferredSize(new Dimension(135, 25));
        previousBtn.setPreferredSize(new Dimension(135, 25));

        /* Panel 추가 작업 */
        setContentPane(panel);

        panel.add(nameL);
        panel.add(name);
        panel.add(nicknameL);
        panel.add(nickname);
        panel.add(idL);
        panel.add(id);
        panel.add(findPwBtn);
        panel.add(previousBtn);

        ButtonListener bl = new ButtonListener();
        findPwBtn.addActionListener(bl);
        previousBtn.addActionListener(bl);

        KeyBoardListener kl = new KeyBoardListener();
        id.addKeyListener(kl);

        /* Panel 크기및 동작 설정 */
        setSize(310, 180);
        setLocationRelativeTo(null);
        setVisible(false);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }

    class ButtonListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            JButton b = (JButton) e.getSource();

            String uname = name.getText();
            String unickname = nickname.getText();
            String uid = id.getText();

            if (b.getText().equals("pw 찾기")) {
                if (uname.equals("") || unickname.equals("") || uid.equals("")) {
                    JOptionPane.showMessageDialog(null, "이름,닉네임,id를 확인해주세요", "검색 실패",
                            JOptionPane.ERROR_MESSAGE);
                }else {
                    c.sendMsg(fpwTag + "//" + uname + "//" + unickname + "//" + uid);
                }
            } else if (b.getText().equals("이전 화면")) {
                dispose();
            }
        }

    }

    class KeyBoardListener implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                String uname = name.getText();
                String unickname = nickname.getText();
                String uid = id.getText();

                if (uname.equals("") || unickname.equals("") || uid.equals("")) {
                    JOptionPane.showMessageDialog(null, "이름 혹은 닉네임을 입력해주세요", "검색 실패",
                            JOptionPane.ERROR_MESSAGE);
                }else {
                    c.sendMsg(fpwTag + "//" + uname + "//" + unickname + "//" + uid);
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {

        }

    }
}