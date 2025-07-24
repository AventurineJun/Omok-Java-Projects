import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

//회원정보 변경 기능을 수행하는 인터페이스
public class CInfoFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    /* Panel */
    JPanel panel = new JPanel();

    /* Button */
    JButton nameBtn = new JButton("이름 변경하기");
    JButton pwBtn = new JButton("비밀번호 변경하기");

    JButton phoneBtn = new JButton("전화번호 변경하기");

    JButton emailBtn = new JButton("이메일 변경하기");

    JButton exitBtn = new JButton("나가기");

    String name; // 변경할 이름
    String pw; // 변경할 비밀번호
    String email; // 변경할 이메일

    String phone; // 변경할 전화번호

    Client c = null;

    final String changeTag = "CHANGE"; // 회원정보 변경 기능 태그

    CInfoFrame(Client _c) {
        c = _c;

        setTitle("회원정보 수정");

        /* Button 크기 작업 */
        nameBtn.setPreferredSize(new Dimension(250, 30));
        pwBtn.setPreferredSize(new Dimension(250, 30));
        phoneBtn.setPreferredSize(new Dimension(250, 30));
        emailBtn.setPreferredSize(new Dimension(250, 30));
        exitBtn.setPreferredSize(new Dimension(250, 30));

        /* panel 추가 작업 */
        setContentPane(panel); // panel을 기본 컨테이너로 설정

        panel.add(nameBtn);
        panel.add(pwBtn);
        panel.add(phoneBtn);
        panel.add(emailBtn);
        panel.add(exitBtn);

        /* 버튼 이벤트 리스너 추가 */
        ButtonListener bl = new ButtonListener();

        nameBtn.addActionListener(bl);
        pwBtn.addActionListener(bl);
        phoneBtn.addActionListener(bl);
        emailBtn.addActionListener(bl);
        exitBtn.addActionListener(bl);

        setSize(280, 205);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }

    /* Button 이벤트 리스너 */
    class ButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton b = (JButton) e.getSource();

            /* 이름 변경하기 버튼 이벤트 */
            if (b.getText().equals("이름 변경하기")) {
                System.out.println("[Client] 이름 변경 시도");
                // 변경할 이름을 받음
                name = JOptionPane.showInputDialog(null, "변경할 이름을 입력하시오", "이름변경", JOptionPane.QUESTION_MESSAGE);

                if (name != null) { // name이 null이 아니면 서버에 "태그//이름//변경할이름" 형태의 메시지를 전송
                    c.sendMsg(changeTag + "//name//" + name);
                }
            }

            /* 비밀번호 변경하기 버튼 이벤트 */
            else if (b.getText().equals("비밀번호 변경하기")) {
                System.out.println("[Client] 비밀번호 변경 시도");
                // 변경할 비밀번호를 받음
                pw = JOptionPane.showInputDialog(null, "변경할 비밀번호를 입력하시오", "비밀번호변경", JOptionPane.QUESTION_MESSAGE);

                if (pw != null) { // pw가 null이 아니면 서버에 "태그//비밀번호//변경할비밀번호" 형태의 메시지를 전송
                    c.sendMsg(changeTag + "//password//" + pw);
                }
            }

            /* 이메일 변경하기 버튼 이벤트 */
            else if (b.getText().equals("이메일 변경하기")) {
                System.out.println("[Client] 이메일 변경 시도");
                // 변경할 이메일을 받음
                email = JOptionPane.showInputDialog(null, "변경할 이메일을 입력하시오", "이메일변경", JOptionPane.QUESTION_MESSAGE);

                if (email != null) { // email이 null이 아니면 서버에 "태그//이메일//변경할이메일" 형태의 메시지를 전송
                    c.sendMsg(changeTag + "//email//" + email);
                }
            }

            /* 전화번호 변경하기 버튼 이벤트 */
            else if (b.getText().equals("전화번호 변경하기")) {
                System.out.println("[Client] 전화번호 변경 시도");
                // 변경할 전화번호를 받음
                phone = JOptionPane.showInputDialog(null, "변경할 전화번호을 입력하시오", "전화번호변경", JOptionPane.QUESTION_MESSAGE);

                if (phone != null) { // phone이 null이 아니면 서버에 "태그//전화번호//변경할전화번" 형태의 메시지를 전송
                    c.sendMsg(changeTag + "//phone//" + phone);
                }
            }

            /* 나가기 버튼 이벤트 */
            else if (b.getText().equals("나가기")) {
                System.out.println("[Client] 회원 정보 변경 인터페이스 종료");
                dispose(); // 인터페이스 닫음
            }
        }
    }
}