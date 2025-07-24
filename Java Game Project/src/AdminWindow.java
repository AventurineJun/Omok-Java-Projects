import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

public class AdminWindow extends JFrame{
    private static final long serialVersionUID = 1L;

    Client c = null;

    /* model */
    AdminModel model = new AdminModel();

    /* Panel */
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

    AdminWindow(Client _c){
        c = _c;

        setTitle("--관리자 프레임--");

        /* JTextField */
        keyword = new JTextField();
        keyword.setPreferredSize(new Dimension(80, 26));

        /* JTable */
        memberTable = new JTable(model);

        /* ScrollPane */
        memberSp = new JScrollPane(memberTable);

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
        categoryPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        categoryPanel.setBackground(Color.PINK);
        categoryPanel.add(chCategory);
        categoryPanel.add(keyword);
        categoryPanel.add(selectBtn);
        categoryPanel.add(getAllBtn);

        btnPanel.add(registerBtn);
        btnPanel.add(modifyBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(exitBtn);

        centerPanel.add(categoryPanel, BorderLayout.NORTH);
        centerPanel.add(memberSp);
        centerPanel.add(btnPanel, BorderLayout.SOUTH);

        ButtonListener bl = new ButtonListener();
        selectBtn.addActionListener(bl);
        getAllBtn.addActionListener(bl);
        exitBtn.addActionListener(bl);

        /* Panel 크기및 동작 설정 */
        setSize(700, 700);
        setResizable(false);
        setVisible(true);
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

            if(b.getText().equals("조회")) {

            }else if(b.getText().equals("전체 조회")) {

            }else if(b.getText().equals("종료")) {
                dispose();
            }

        }
    }

    // regist() 메소드
    // edit() 메소드
    // delete() 메소드
}
