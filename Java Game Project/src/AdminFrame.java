import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

public class AdminFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    Client c = null;

    /* JLabel */
    JLabel adminL = new JLabel("관리자 모드");

    /* model */
    AdminModel model = new AdminModel();

    /* Panel */
    JPanel adminLabelPanel = new JPanel();
    JPanel categoryPanel = new JPanel();
    JPanel centerPanel = new JPanel();
    JPanel btnPanel = new JPanel();

    /* JTable */
    JTable memberTable = new JTable();

    /* JScrollPane */
    JScrollPane memberSp = new JScrollPane();

    /* JComboBox */
    JComboBox<String> chCategory = new JComboBox<>();

    /* JTextField */
    JTextField keyword = new JTextField();

    /* JButton */
    JButton selectBtn = new JButton();
    JButton getAllBtn = new JButton();
    JButton registerBtn = new JButton();
    JButton modifyBtn = new JButton();
    JButton deleteBtn = new JButton();
    JButton exitBtn = new JButton();

    String category = "";

    /* Tag */
    final String viewTag = "VIEW"; // 회원정보조회
    final String viewDBTag = "VIEWDB"; // DB조회태그
    final String pexitTag = "PEXIT"; // 프로그램종료
    final String chSIdTag = "CHSID"; // 서버에서 처리할 아이디 변경
    final String unRegisterIdTag = "UNREGI"; // 회원 탈퇴 태그

    AdminFrame(Client _c) {
        c = _c;

        setTitle("--관리자 프레임--");

        /* JLabel */
        adminL.setPreferredSize(new Dimension(100, 35));
        adminL.setBackground(Color.WHITE);
        adminL.setFont(new Font("HY견고딕", Font.PLAIN, 17));

        /* JTextField */
        keyword.setPreferredSize(new Dimension(80, 26));

        /* JTable */
        memberTable = new JTable(model);

        /* ScrollPane */
        memberSp = new JScrollPane(memberTable);
        memberSp.setPreferredSize(new Dimension(700, 400));

        /* JComboBox */
        chCategory = new JComboBox<String>();
        chCategory.addItem("카테고리");
        chCategory.addItem("name");
        chCategory.addItem("nickname");
        chCategory.addItem("id");
        chCategory.addItem("password");
        chCategory.addItem("address");
        chCategory.addItem("gender");
        chCategory.addItem("birth");
        chCategory.addItem("email");

        /* Button */
        selectBtn = new JButton("조회");
        getAllBtn = new JButton("전체 조회");
        registerBtn = new JButton("등록");
        modifyBtn = new JButton("수정");
        deleteBtn = new JButton("삭제");
        exitBtn = new JButton("종료");

        /* Panel */
        adminLabelPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        adminLabelPanel.setPreferredSize(new Dimension(700, 80));
        adminLabelPanel.setBackground(Color.BLACK);
        adminLabelPanel.add(adminL);
        adminLabelPanel.add(categoryPanel);

        categoryPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        categoryPanel.setPreferredSize(new Dimension(700, 40));
        categoryPanel.setBackground(Color.BLUE);
        categoryPanel.add(chCategory);
        categoryPanel.add(keyword);
        categoryPanel.add(selectBtn);
        categoryPanel.add(getAllBtn);

        btnPanel.add(registerBtn);
        btnPanel.add(modifyBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(exitBtn);

        centerPanel.add(adminLabelPanel, BorderLayout.NORTH);

        centerPanel.add(memberSp);
        centerPanel.add(btnPanel, BorderLayout.SOUTH);

        /* ButtonListener 장착 */
        ButtonListener bl = new ButtonListener();
        registerBtn.addActionListener(bl);
        modifyBtn.addActionListener(bl);
        deleteBtn.addActionListener(bl);
        selectBtn.addActionListener(bl);
        getAllBtn.addActionListener(bl);
        exitBtn.addActionListener(bl);

        /* Panel 크기및 동작 설정 */
        setSize(800, 580);
        setResizable(false);
        setVisible(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        Container contentPane = getContentPane();
        contentPane.add(centerPanel);

        chCategory.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent ie) {
                category = (String) ie.getItem();
            }
        });
    }

    class ButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton b = (JButton) e.getSource();

            if (b.getText().equals("조회")) { // 입력한 카테고리와 값으로 회원 검색
                String selectedCategory = (String) chCategory.getSelectedItem();
                String keywordInput = keyword.getText();
                if (selectedCategory.equals("카테고리")) {
                    JOptionPane.showMessageDialog(null, "카테고리를 선택해주세요.", "카테고리 미선택", JOptionPane.ERROR_MESSAGE);
                } else if (keywordInput.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "검색어를 입력해주세요.", "검색어 미입력", JOptionPane.ERROR_MESSAGE);
                } else {
                    c.sendMsg(viewTag + "//" + category + "//" + keyword.getText());
                }
            } else if (b.getText().equals("등록")) { // 회원가입 프레임 띄움
                c.jf.setVisible(true);
            } else if (b.getText().equals("수정")) { // 회원 수정 -> 이름 비밀번호 이메일만 가능
                String inputId = JOptionPane.showInputDialog("수정할 계정의 id를 입력");
                keyword.setText(inputId);
                c.sendMsg(chSIdTag + "//" + inputId);
                if (inputId == null) {
                    JOptionPane.showMessageDialog(null, "수정할 계정을 확인해주세요.", "계정 id 확인", JOptionPane.ERROR_MESSAGE);
                } else {
                    c.cinf.setVisible(true);
                }
            } else if (b.getText().equals("삭제")) { // 회원 삭제 -> 백업테이블로 이동
                String inputId = JOptionPane.showInputDialog("삭제할 계정의 id를 입력");
                keyword.setText(inputId);
                if (inputId == null) {
                    JOptionPane.showMessageDialog(null, "삭제할 계정을 확인해주세요.", "계정 id 확인", JOptionPane.ERROR_MESSAGE);
                } else {
                    c.sendMsg(unRegisterIdTag + "//" + keyword.getText());
                }
            } else if (b.getText().equals("전체 조회")) { // 회원 전체 조회
                c.sendMsg(viewDBTag);
            } else if (b.getText().equals("종료")) { // 서버에 존재하는 접속유저벡터에서 해당 클라이언트를 지움
                c.sendMsg(pexitTag);
                System.exit(0);
            }
        }
    }
}
