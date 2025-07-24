import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//회원가입 기능을 수행하는 인터페이스
public class JoinFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    /* Panel */
    JPanel panel = new JPanel();

    /* Label */
    JLabel imgLabel = new JLabel(); // 이미지를 넣을 레이블
    JLabel nameL = new JLabel("이름");
    JLabel nicknameL = new JLabel("닉네임");
    JLabel idL = new JLabel("아이디");
    JLabel pwL = new JLabel("비밀번호");
    JLabel pwChkL = new JLabel("비밀번호 확인");
    JLabel pwStrengthL = new JLabel("비밀번호 강도 : ");
    JLabel addressL = new JLabel("주소");
    JLabel detailaddL = new JLabel("상세 주소");

    JLabel phone1L = new JLabel("전화번호");
    JLabel phone2L = new JLabel("-");
    JLabel phone3L = new JLabel("-");
    JLabel emailL = new JLabel("이메일");
    JLabel emailL2 = new JLabel("@");
    JLabel birthL = new JLabel("생년월일");
    JLabel voidL = new JLabel("");
    JLabel genderL = new JLabel("성별");
    JLabel genderL1 = new JLabel("남");
    JLabel genderL2 = new JLabel("여");
    JLabel voidL2 = new JLabel("");
    JLabel voidL3 = new JLabel("");

    /* ComboBox */
    JComboBox<String> birthC1 = new JComboBox<String>();
    JComboBox<String> birthC2 = new JComboBox<String>();
    JComboBox<String> birthC3 = new JComboBox<String>();

    JComboBox<String> phoneC = new JComboBox<>();
    JComboBox<String> emailC = new JComboBox<String>();

    /* radioButton */
    JRadioButton radio1 = new JRadioButton();
    JRadioButton radio2 = new JRadioButton();
    ButtonGroup radioGroup = new ButtonGroup();

    /* Border 설정 */
    Border border = BorderFactory.createLineBorder(Color.BLACK, 1);

    /* ImageIcon 설정 */
    ImageIcon icon = new ImageIcon();

    /* TextField */
    JTextField name = new JTextField();
    JTextField nickname = new JTextField();
    JTextField id = new JTextField();
    JPasswordField pw = new JPasswordField();
    JTextField pwChk = new JPasswordField();
    JTextField addressField = new JTextField();
    JTextField detailaddField = new JTextField();
    JTextField phone2 = new JTextField();

    JTextField phone3 = new JTextField();
    JTextField email = new JTextField();
    JTextField email2 = new JTextField();
    JTextField imagePathField = new JTextField();

    /* Button */
    JButton nnolBtn = new JButton("확인");
    JButton idolBtn = new JButton("확인");
    JButton joinBtn = new JButton("가입하기");
    JButton cancelBtn = new JButton("가입취소");
    JButton imageBtn = new JButton("이미지 삽입");
    JButton addressBtn = new JButton("검색");

    final String joinTag = "JOIN"; // 회원가입 기능 태그
    final String overTag = "OVER"; // 중복확인 기능 태그
    final String pwcTag = "PWC"; // 패스워드 체크 기능 태그
    final String sendImgTag = "SENDIMG"; // 이미지 전송 태그

    Client c = null;

    String imageString = "";

    JoinFrame(Client _c) {
        c = _c;

        setTitle("회원가입");

        /* Label 크기 작업 */
        imgLabel.setPreferredSize(new Dimension(200, 160));
        imgLabel.setBorder(border);
        nameL.setPreferredSize(new Dimension(80, 30));
        nicknameL.setPreferredSize(new Dimension(80, 30));
        idL.setPreferredSize(new Dimension(80, 30));
        pwL.setPreferredSize(new Dimension(80, 30));
        pwChkL.setPreferredSize(new Dimension(80, 30));
        pwStrengthL.setPreferredSize(new Dimension(325, 30));
        addressL.setPreferredSize(new Dimension(80, 30));
        detailaddL.setPreferredSize(new Dimension(80, 30));
        genderL.setPreferredSize(new Dimension(80, 30));
        genderL1.setPreferredSize(new Dimension(25, 30));
        voidL2.setPreferredSize(new Dimension(45, 30));
        genderL2.setPreferredSize(new Dimension(25, 30));
        voidL3.setPreferredSize(new Dimension(50, 30));
        birthL.setPreferredSize(new Dimension(80, 30));
        phone1L.setPreferredSize(new Dimension(55, 30));
        phone2L.setPreferredSize(new Dimension(10, 30));
        phone3L.setPreferredSize(new Dimension(10, 30));
        emailL.setPreferredSize(new Dimension(80, 30));
        emailL2.setPreferredSize(new Dimension(15, 30));
        voidL.setPreferredSize(new Dimension(230, 30));

        /* JcomboBox 객체 및 크기 작업 */
        for (int i = 6; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                birthC1.addItem("19" + i + j + "년");
            }
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 10; j++) {
                birthC1.addItem("20" + i + j + "년");
            }
        }
        for (int i = 0; i <= 12; i++) {
            birthC2.addItem("" + i + "월");
        }
        for (int i = 0; i <= 31; i++) {
            birthC3.addItem("" + i + "일");
        }

        phoneC.addItem("010");
        phoneC.addItem("011");
        phoneC.addItem("016");
        phoneC.addItem("017");
        phoneC.addItem("018");
        phoneC.addItem("019");

        emailC.addItem("직접 입력");
        emailC.addItem("naver.com");
        emailC.addItem("daum.net");
        emailC.addItem("nate.com");
        emailC.addItem("gmail.com");
        emailC.addItem("kakao.com");

        birthC1.setPreferredSize(new Dimension(90, 30));
        birthC2.setPreferredSize(new Dimension(70, 30));
        birthC3.setPreferredSize(new Dimension(70, 30));
        phoneC.setPreferredSize(new Dimension(90, 30));
        emailC.setPreferredSize(new Dimension(90, 30));

        /* radioButton 작업 */
        radioGroup.add(radio1);
        radioGroup.add(radio2);

        /* TextField 크기 작업 */
        name.setPreferredSize(new Dimension(240, 30));
        nickname.setPreferredSize(new Dimension(175, 30));
        id.setPreferredSize(new Dimension(175, 30));
        pw.setPreferredSize(new Dimension(240, 30));
        pwChk.setPreferredSize(new Dimension(240, 30));
        addressField.setPreferredSize(new Dimension(175, 30));
        addressField.setEnabled(false);
        detailaddField.setPreferredSize(new Dimension(240, 30));
        phone2.setPreferredSize(new Dimension(65, 30));
        phone3.setPreferredSize(new Dimension(65, 30));
        email.setPreferredSize(new Dimension(125, 30));
        email2.setPreferredSize(new Dimension(90, 30));

        /* Button 크기 작업 */
        imageBtn.setPreferredSize(new Dimension(100, 100));
        nnolBtn.setPreferredSize(new Dimension(60, 30));
        idolBtn.setPreferredSize(new Dimension(60, 30));
        joinBtn.setPreferredSize(new Dimension(160, 30));
        cancelBtn.setPreferredSize(new Dimension(160, 30));
        addressBtn.setPreferredSize(new Dimension(60, 30));

        /* Panel 추가 작업 */
        setContentPane(panel); // panel을 기본 컨테이너로 설정

        panel.add(imageBtn);
        panel.add(imgLabel);
        panel.add(nameL);
        panel.add(name);

        panel.add(nicknameL);
        panel.add(nickname);
        panel.add(nnolBtn);

        panel.add(idL);
        panel.add(id);
        panel.add(idolBtn);

        panel.add(pwL);
        panel.add(pw);
        panel.add(pwChkL);
        panel.add(pwChk);
        panel.add(pwStrengthL);

        panel.add(addressL);
        panel.add(addressField);
        panel.add(addressBtn);

        panel.add(detailaddL);
        panel.add(detailaddField);

        panel.add(genderL);
        panel.add(genderL1);
        panel.add(radio1);
        panel.add(voidL2);
        panel.add(genderL2);
        panel.add(radio2);
        panel.add(voidL3);

        panel.add(birthL);
        panel.add(birthC1);
        panel.add(birthC2);
        panel.add(birthC3);

        panel.add(phone1L);
        panel.add(phoneC);
        panel.add(phone2L);
        panel.add(phone2);
        panel.add(phone3L);
        panel.add(phone3);

        panel.add(emailL);
        panel.add(email);
        panel.add(emailL2);
        panel.add(email2);

        panel.add(voidL);
        panel.add(emailC);

        panel.add(cancelBtn);
        panel.add(joinBtn);

        /* Button 이벤트 리스너 추가 */
        ButtonListener bl = new ButtonListener();

        cancelBtn.addActionListener(bl);
        joinBtn.addActionListener(bl);

        /* Panel 크기및 동작 설정 */
        setSize(387, 770);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        /* Button 이벤트 추가 */
        imageBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(JoinFrame.this);

                if (result == JFileChooser.APPROVE_OPTION) {
                    String imagePath = fileChooser.getSelectedFile().getAbsolutePath();

                    // 선택한 이미지를 JLabel에 표시
                    icon = new ImageIcon(new ImageIcon(imagePath).getImage()
                            .getScaledInstance(imgLabel.getWidth(),imgLabel.getHeight(),
                                    Image.SCALE_SMOOTH));
                    imgLabel.setIcon(icon);

                    imageString = encodeImageToString(imagePath);
                }
            }
        });

        nnolBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!nickname.getText().equals("")) {
                    System.out.println("[Client] 닉네임 중복 확인");
                    c.sendMsg(overTag + "//nickname//" + nickname.getText()); // 서버에 태그와 함께 닉네임 전송
                }
            }
        });

        idolBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!id.getText().equals("")) {
                    System.out.println("[Client] 아이디 중복 확인");
                    c.sendMsg(overTag + "//id//" + id.getText()); // 서버에 태그와 함께 아이디 전송
                }
            }
        });

        pw.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String password = "";
                for (int i = 0; i < pw.getPassword().length; i++) {
                    password = password + pw.getPassword()[i];
                }

                int strength = calculatePasswordStrength(password);
                pwStrengthL.setText("비밀번호 강도: " + strength);

                if (strength == 4) {
                    pwStrengthL.setText("비밀번호 강도: 매우 강력");
                    pwStrengthL.setForeground(Color.GREEN);
                } else if (strength == 3) {
                    pwStrengthL.setText("비밀번호 강도: 강력");
                    pwStrengthL.setForeground(Color.ORANGE);
                } else if (strength == 2) {
                    pwStrengthL.setText("비밀번호 강도: 보통");
                    pwStrengthL.setForeground(Color.RED);
                } else if (strength == 1) {
                    pwStrengthL.setText("비밀번호 강도: 약함");
                    pwStrengthL.setForeground(Color.RED);
                }
            }
        });

        addressBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                c.adf.setVisible(true);
            }
        });

        // 이메일을 직접 입력할지 선택할지 고르는 리스너
        emailC.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                email2.setText((String) emailC.getSelectedItem());
            }

        });

    }

    /* Button 이벤트 리스너 */
    class ButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton b = (JButton) e.getSource();

            /* TextField에 입력된 회원 정보들을 변수에 초기화 */
            String uname = name.getText();
            String unick = nickname.getText();
            String uid = id.getText();
            String upass = "";
            for (int i = 0; i < pw.getPassword().length; i++) {
                upass = upass + pw.getPassword()[i];
            }

            String upassCh = pwChk.getText();
            String uphone = phoneC.getSelectedItem() + phone2L.getText() + phone2.getText() + phone3L.getText() + phone3.getText();
            String uemail = email.getText() + emailL2.getText() + email2.getText();
            String ugender = "";
            String ubirth = (String) birthC1.getSelectedItem() + (String) birthC2.getSelectedItem()
                    + (String) birthC3.getSelectedItem();
            String uaddress = addressField.getText() + detailaddField.getText();

            if (radio1.isSelected()) {
                ugender = genderL1.getText();
            } else {
                ugender = genderL2.getText();
            }

            /* 가입취소 버튼 이벤트 */
            if (b.getText().equals("가입취소")) {
                System.out.println("[Client] 회원가입 인터페이스 종료");
                dispose(); // 인터페이스 닫음
            }

            /* 가입하기 버튼 이벤트 */
            else if (b.getText().equals("가입하기")) {

                // 비밀번호가 동일하면 비밀번호 검사
                if (upass.equals(upassCh)) {
                    String tmpStr = checkPassword(upass, uid);
                    if (!tmpStr.equals("true")) {
                        JOptionPane.showMessageDialog(null, tmpStr, "회원가입 실패", JOptionPane.ERROR_MESSAGE);
                        upassCh = "";
                        pwChk.setText("");
                    } else if (tmpStr.equals("true")) {
                        upassCh = "true";
                    }
                }

                else {
                    JOptionPane.showMessageDialog(null, "비밀번호가 동일하지 않습니다.", "회원가입 실패", JOptionPane.ERROR_MESSAGE);
                    System.out.println("[Client] 회원가입 실패 : 비밀번호 비동일");
                }

                if (uname.equals("") || unick.equals("") || uid.equals("") || upass.equals("") || upassCh.equals("")
                        || uaddress.equals("") || ugender.equals("") || ubirth.equals("") || uphone.equals("") || uemail.equals("")) {
                    // 모든 정보가 입력되지 않으면 회원가입 시도 실패
                    JOptionPane.showMessageDialog(null, "모든 정보를 기입해주세요", "회원가입 실패", JOptionPane.ERROR_MESSAGE);
                    System.out.println("[Client] 회원가입 실패 : 회원정보 미입력");
                }

                else if (!uname.equals("") && !unick.equals("") && !uid.equals("") && !upass.equals("")
                        && upassCh.equals("true") && !uaddress.equals("") && !ugender.equals("") && !ubirth.equals("")
                && !uemail.equals("") && !uemail.equals("")) {
                    // 회원가입 시도 성공시 서버에 회원가입 정보 전송
                    c.sendMsg(joinTag + "//" + uname + "//" + unick + "//" + uid + "//" + upass + "//" + uaddress + "//"
                            + ugender + "//" + ubirth + "//" + uphone + "//" + uemail);
                    c.sendMsg(sendImgTag + "//" + uid  + "//" + imageString);

                    // 회원가입 시도시
                }
            }
        }
    }

    // 이미지를 크기를 줄인 후 스트링으로 인코딩하는 메소드
    private String encodeImageToString(String imagePath) {
        String encodedImage = "";
        String fileExt = "";
        try {
            // 이미지 파일 읽기
            File imageFile = new File(imagePath);
            BufferedImage originalImage = ImageIO.read(imageFile);

            // 원하는 이미지 크기 지정
            int newWidth = 120; // 원하는 너비
            int newHeight = 200; // 원하는 높이
            BufferedImage resizedImage =
                    new BufferedImage(newWidth, newHeight, originalImage.getType());
            resizedImage.getGraphics()
                    .drawImage(originalImage, 0, 0, newWidth, newHeight, null);

            // 들어온 이미지의 확장자대로 만들기위한 코드
            if (imageFile != null) {
                int index = imagePath.lastIndexOf(".");
                if (index > 0) {
                    String extension = imagePath.substring(index + 1);
                    fileExt = extension;
                }
            }

            // 이미지를 바이트 배열로 인코딩
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, fileExt, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            // 인코딩된 이미지를 문자열로 변환
            encodedImage = Base64.getEncoder().encodeToString(imageBytes);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return encodedImage;
    }

    public static int calculatePasswordStrength(String password) {
        int upperCaseCount = password.replaceAll("[^A-Z]", "").length();
        int lowerCaseCount = password.replaceAll("[^a-z]", "").length();
        int digitCount = password.replaceAll("[^0-9]", "").length();
        int specialCharCount = password.replaceAll("[ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9]", "").length();

        if (upperCaseCount >= 1)
            upperCaseCount = 1;
        if (lowerCaseCount >= 1)
            lowerCaseCount = 1;
        if (digitCount >= 1)
            digitCount = 1;
        if (specialCharCount >= 1)
            specialCharCount = 1;

        int strength = upperCaseCount + lowerCaseCount + digitCount + specialCharCount;

        // 비밀번호의 길이 및 다양한 문자 유형의 사용을 고려하여 강도를 계산합니다.
        // 필요에 따라 추가적인 보안 정책을 적용할 수 있습니다.
        return strength;
    }

    private String checkPassword(String pwd, String id) {

        // 비밀번호 포맷 확인(영문, 특수문자, 숫자 포함 8자 이상)
        Pattern passPattern1 = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*\\W).{8,20}$");
        Matcher passMatcher1 = passPattern1.matcher(pwd);

        if (!passMatcher1.find()) {
            return "비밀번호는 영문과 특수문자 숫자를 포함하며 8자 이상이어야 합니다.";
        }

        // 반복된 문자 확인
        Pattern passPattern2 = Pattern.compile("(\\w)\\1\\1\\1");
        Matcher passMatcher2 = passPattern2.matcher(pwd);

        if (passMatcher2.find()) {
            return "비밀번호에 동일한 문자를 과도하게 연속해서 사용할 수 없습니다.";
        }

        // 공백 확인
        Pattern passPattern3 = Pattern.compile("(\\w)\\1\\1\\1");
        Matcher passMatcher3 = passPattern3.matcher(pwd);

        if (passMatcher3.find()) {
            return "비밀번호에 공백을 포함할 수 없습니다.";
        }

        // 아이디 포함 확인
        if (pwd.contains(id)) {
            return "비밀번호에 ID를 포함할 수 없습니다.";
        }

        // 특수문자 확인
        Pattern passPattern4 = Pattern.compile("\\W");
        Pattern passPattern5 = Pattern.compile("[!@#$%^*+=-]");

        for (int i = 0; i < pwd.length(); i++) {
            String s = String.valueOf(pwd.charAt(i));
            Matcher passMatcher4 = passPattern4.matcher(s);

            if (passMatcher4.find()) {
                Matcher passMatcher5 = passPattern5.matcher(s);
                if (!passMatcher5.find()) {
                    return "비밀번호에 특수문자는 !@#$^*+=-만 사용 가능합니다.";
                }
            }
        }

        // 연속된 문자 확인
        int ascSeqCharCnt = 0; // 오름차순 연속 문자 카운트
        int descSeqCharCnt = 0; // 내림차순 연속 문자 카운트

        char char_0;
        char char_1;
        char char_2;

        int diff_0_1;
        int diff_1_2;

        for (int i = 0; i < pwd.length() - 2; i++) {

            char_0 = pwd.charAt(i);
            char_1 = pwd.charAt(i + 1);
            char_2 = pwd.charAt(i + 2);

            diff_0_1 = char_0 - char_1;
            diff_1_2 = char_1 - char_2;

            if (diff_0_1 == 1 && diff_1_2 == 1) {
                ascSeqCharCnt += 1;
            }

            if (diff_0_1 == -1 && diff_1_2 == -1) {
                descSeqCharCnt -= 1;
            }
        }

        if (ascSeqCharCnt > 1 || descSeqCharCnt > 1) {
            return "비밀번호에 연속된 문자열을 사용할 수 없습니다.";
        }

        return "true";
    }

}