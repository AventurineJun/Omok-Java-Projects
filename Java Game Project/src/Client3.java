import java.net.*;
import java.util.Base64;
import java.util.Vector;
import java.awt.Image;
import java.io.*;
import javax.swing.*;

//서버와의 연결과 각 인터페이스를 관리하는 클래스.
public class Client3 {

    private String id = "";
    int omokorder = 0;
    private String myStat = ""; // 관전자면 관전자로 플레이어면 플레이어로

    Socket mySocket = null;

    /* 메시지 송신을 위한 필드 */
    OutputStream os = null;
    DataOutputStream dos = null;

    /* 각 프레임을 관리할 필드 */
    MainFrame mf = null;
    LoginFrame lf = null;
    JoinFrame jf = null;

    findIdFrame idf = null;
    findPwFrame fpw = null;
    findIdFrame fid = null;
    RankingFrame rf = null;
    InfoFrame inf = null;
    CInfoFrame cinf = null;
    GameFrame gf = null;
    SRankFrame srf = null;
    ChatRoomFrame cf = null;
    AdminFrame af = null;
    AddressFrame adf = null;

    public static void main(String[] args) {
        Client client = new Client();

        try {
            // 서버에 연결

            client.mySocket = new Socket("localhost", 1257);
            System.out.println("[Client] 서버 연결 성공");

            client.os = client.mySocket.getOutputStream();
            client.dos = new DataOutputStream(client.os);

            /* 프레임 생성 */
            client.mf = new MainFrame(client);
            client.lf = new LoginFrame(client);
            client.jf = new JoinFrame(client);
            client.fid = new findIdFrame(client);
            client.fpw = new findPwFrame(client);
            client.cf = new ChatRoomFrame(client);
            client.rf = new RankingFrame(client);
            client.inf = new InfoFrame(client);
            client.cinf = new CInfoFrame(client);
            client.gf = new GameFrame(client);
            client.srf = new SRankFrame(client);
            client.af = new AdminFrame(client);
            client.adf = new AddressFrame(client);

            MessageListener msgListener = new MessageListener(client, client.mySocket);
            msgListener.start(); // 스레드 시작
        } catch (SocketException e) {
            System.out.println("[Client] 서버 연결 오류 > " + e.toString());
        } catch (IOException e) {
            System.out.println("[Client] 입출력 오류 > " + e.toString());
        }

    }

    /* 서버에 메시지 전송 */
    void sendMsg(String _m) {
        try {
            dos.writeUTF(_m);
        } catch (Exception e) {
            System.out.println("[Client] 메시지 전송 오류 > " + e.toString());
        }
    }

    void setId(String _id) {
        this.id = _id;
    }

    String getId() {
        return id;
    }

    void setMyStat(String _myStat) {
        this.myStat = _myStat;
    }

    String getMyStat() {
        return myStat;
    }

}

//서버와의 메시지 송수신을 관리하는 클래스.
//스레드를 상속받아 각 기능과 독립적으로 동작할 수 있도록 한다.
class MessageListener3 extends Thread {
    Socket socket;
    Client client;

    /* 메시지 수신을 위한 필드 */
    InputStream is;
    DataInputStream dis;

    String msg; // 수신 메시지 저장
    String nickname; // 닉네임
    Vector<String> imgStrV = new Vector<>(); // 수신된 플레이어 인코딩 이미지
    Vector<ImageIcon> imgByteV = new Vector<>(); // 수신된 플레이어 디코딩 이미지

    /* 각 메시지를 구분하기 위한 태그 */
    final String loginTag = "LOGIN"; // 로그인
    final String joinTag = "JOIN"; // 회원가입
    final String overTag = "OVER"; // 중복확인
    final String viewTag = "VIEW"; // 회원정보조회
    final String viewImgTag = "VIEWIMG"; // 이미지 조회 태그
    final String addPImgTag = "ADDPIMG"; // 플레이어 imgStr을 벡터에 담기위한 태그
    final String setPImgTag = "SETPIMG"; // 플레이어 imgByte를 세팅하기위한 태그
    final String viewDBTag = "VIEWDB"; // DB조회태그
    final String viewDBClTag = "VIEWCLDB"; // DB조회 태그(클라)
    final String changeTag = "CHANGE"; // 회원정보변경
    final String rankTag = "RANK"; // 전적조회(전체회원)
    final String croomTag = "CROOM"; // 방생성
    final String vroomTag = "VROOM"; // 방목록
    final String uroomTag = "UROOM"; // 방유저
    final String eroomTag = "EROOM"; // 방입장
    final String oroomTag = "OROOM"; // 방 입장 기능 태그(옵저버)
    final String plyrTag = "PLAYER"; // 플레이어
    final String objvTag = "OBJV"; // 관전자
    final String stateTag = "STATE"; // 유저가 플레이어인지 관전자인지
    final String cuserTag = "CUSER"; // 접속유저
    final String searchTag = "SEARCH"; // 전적조회(한명)
    final String omokTag = "OMOK"; // 오목
    final String blackTag = "BLACK"; // 검은색 돌
    final String whiteTag = "WHITE"; // 흰색 돌
    final String winTag = "WIN"; // 승리
    final String loseTag = "LOSE"; // 패배
    final String rexitTag = "REXIT"; // 방퇴장
    final String recordTag = "RECORD"; // 전적업데이트
    final String chatTag = "CHAT"; // 채팅

    final String lobbyChatTag = "LOBBYCHAT"; // 대기방 채팅
    final String nickNameTag = "NICKNAME"; // 닉네임
    final String chatRoomTag = "CHATROOM"; // 채팅방
    final String adminTag = "ADMIN"; // 관리자 로그인 태그
    final String adminClTag = "ADMINCL"; // 관리자 로그인 태그(클라이언트로)

    final String auserIdTag = "AUSERID"; // 모든 유저 아이디 요청 태그
    final String unRegisterIdTag = "UNREGI"; // 회원 탈퇴 태그
    final String unRegisterIdClTag = "UNREGICL"; // 회원 탈퇴 태그(클라이언트로)
    final String chSIdTag = "CHSID"; // 서버에서 처리할 아이디 변경
    final String chatLogTag = "CHATLOG"; // 채팅 로그 조회 태그
    final String spectatorTag = "SPECTATOR"; // 관전자 돌 색상
    final String spectatorXYTag = "SPECTATORXY"; // 관전자 돌 위치 태그
    final String omokMsgTag = "OMOKXY"; // 오목 위치 태그
    final String omokBlackMsgTag = "OMOKBLACKXY"; // 오목 검은 돌 위치 태그
    final String omokWhiteMsgTag = "OMOKWHITEXY"; // 오목 하얀 돌 위치 태그
    final String viewMyInfoTag = "VIEWMYINFO"; // 자신의 회원 정보 조회 기능 태그
    final String uroomoTag = "UROOMO"; // 방 옵저버
    final String fidTag = "FID"; // 아이디 찾기 태그
    final String fpwTag = "FPW"; // 비밀번호 찾기 태그

    MessageListener3(Client _c, Socket _s) {
        this.client = _c;
        this.socket = _s;
    }

    public void run() {
        try {
            is = this.socket.getInputStream();
            dis = new DataInputStream(is);

            while (true) {
                msg = dis.readUTF(); // 메시지 수신을 상시 대기한다.
                System.out.println(msg);

                String m[] = msg.split("//"); // msg를 "//"로 나누어 m[]배열에 차례로 집어넣는다.

                // 수신받은 문자열들의 첫 번째 배열(m[0])은 모두 태그 문자. 각 기능을 분리한다.

                /* 관리자로 로그인시 */
                if (m[0].equals(adminTag)) {
                    client.setId(m[2]);
                    adminLogin(m[1]); // m[1]은 adminClTag임
                }

                /* 관리자 요청 회원탈퇴 */
                if (m[0].equals(unRegisterIdClTag)) {
                    adminUnregister(m[1], m[2]);
                }

                /* 로그인 */
                if (m[0].equals(loginTag)) {
                    loginCheck(m[1]);
                    if (m[1].equals("OKAY")) {
                        client.setId(m[2]);
                        client.sendMsg(chSIdTag + "//" + client.getId());
                    }
                }

                /* id 찾기 */
                if (m[0].equals(fidTag)) {
                    if (!m[1].equals("FAIL")) {
                        JOptionPane.showMessageDialog(null, "찾고자한 id는 : " + m[1] + " 입니다.", "검색 성공",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else if (m[1].equals("FAIL")) {
                        JOptionPane.showMessageDialog(null, "찾고자한 id가 없습니다.", "검색 실패", JOptionPane.ERROR_MESSAGE);
                    }
                }

                /* password 찾기 */
                if (m[0].equals(fpwTag)) {
                    if (!m[1].equals("FAIL")) {
                        JOptionPane.showMessageDialog(null, "찾고자한 password는 : " + m[1] + " 입니다.", "검색 성공",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else if (m[1].equals("FAIL")) {
                        JOptionPane.showMessageDialog(null, "찾고자한 password가 없습니다.\n정보를 확인해 주세요", "검색 실패",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }

                /* 닉네임 */
                if (m[0].equals(nickNameTag)) {
                    client.gf.setNickname(m[1]);
                }

                /* 대기방 채팅 */
                if (m[0].equals(lobbyChatTag)) { // m[0]에는 CHAT m[1]에는 채팅내용이 들어가있음
                    client.cf.appendMsg(m[1]);
                }

                /* 채팅 */
                if (m[0].equals(chatTag)) { // m[0]에는 CHAT m[1]에는 채팅내용이 들어가있음
                    String[] chatInfo = m[1].split(":", 2); // 최대 2개의 파트로 나눔
                    String sender = chatInfo[0].trim(); // 채팅을 보낸 사용자의 닉네임
                    String message = chatInfo[1].trim(); // 채팅 내용

                    // 디버깅: sender와 client.cf.getNickname() 값을 출력
                    System.out.println("Sender: " + sender);
                    System.out.println("Nickname: " + client.gf.getNickname());

                    if (sender.equals(client.gf.getNickname())) {
                        SwingUtilities.invokeLater(() -> {
                            System.out.println("This is my message: " + message);
                            client.gf.appendMsg(message, client.gf.getNickname());
                        });
                    } else {
                        SwingUtilities.invokeLater(() -> {
                            System.out.println("This is someone else's message: " + message);
                            client.gf.appendMsg(message, sender);
                        });
                    }
                }

                /* 채팅방 입장시 */
                if (m[0].equals(chatRoomTag)) {
                    client.cf.appendMsg(m[1] + "\n");
                }

                /* 회원가입 */
                else if (m[0].equals(joinTag)) {
                    joinCheck(m[1]);
                }

                /* 중복확인 */
                else if (m[0].equals(overTag)) {
                    overlapCheck(m[1]);
                }

                /* 회원정보 전체 조회 */
                else if (m[0].equals(viewDBClTag)) {
                    viewAllInfo(m[1]);
                }

                /* 회원정보 조회 */
                else if (m[0].equals(viewTag)) {
                    if (m[1].equals("카테고리에 해당하는 찾고자하는 회원이 없습니다.")) {
                        JOptionPane.showMessageDialog(null, "해당 계정이 존재하지 않습니다. 다시한번 확인해주세요.", "검색 실패",
                                JOptionPane.ERROR_MESSAGE);
                        System.out.println("[Client] 회원 정보 요청 실패");
                    }else {
                        viewInfo(m[1], m[2], m[3], m[4], m[5], m[6], m[7], m[8], m[9], m[10]);
                    }
                }

                /* 회원정보 조회(이미지) */
                else if (m[0].equals(viewImgTag)) {
                    if (m[1].equals("회원정보")) {
                        JOptionPane.showMessageDialog(null, "회원정보 조회에 실패했습니다.", "조회 실패", JOptionPane.ERROR_MESSAGE);
                        System.out.println("[Client] 회원정보 조회 요청 실패");
                    } else if (m[2].equals("이미지 조회에 실패했습니다.")) {
                        JOptionPane.showMessageDialog(null, "이미지 조회에 실패했습니다.", "조회 실패", JOptionPane.ERROR_MESSAGE);
                        System.out.println("[Client] 이미지 조회 요청 실패");
                    } else {
                        viewMyInfo(m[1], m[2]);
                    }
                }

                /* 전체 전적 조회 */
                else if (m[0].equals(rankTag)) {
                    viewRank(m[1]);
                }

                /* 회원정보 변경 */
                else if (m[0].equals(changeTag)) {
                    changeInfo(m[1]);
                }

                /* 방 생성 */
                else if (m[0].equals(croomTag)) {
                    createRoom(m[1]);	// m[2]는 방제목 m[3]은 ? m[4]도 ?
                }

                /* 접속 유저 */
                else if (m[0].equals(cuserTag)) {
                    viewCUser(m[1]);
                }

                /* 방 목록 */
                else if (m[0].equals(vroomTag)) {
                    if (m.length > 1) { // 배열크기가 1보다 클 때
                        roomList(m[1]);
                    } else { // 배열크기가 1보다 작다 == 방이 없다
                        String[] room = { "" }; // 방 목록이 비도록 함
                        client.mf.rList.setListData(room);
                    }
                }

                /* 방 입장 */
                else if (m[0].equals(eroomTag)) {
                    enterRoom(m[1]);
                }

                else if (m[0].equals(addPImgTag)) {
                    imgStrV.add(m[1]); // m[1]은 플레이어의 imgString 벡터에 담는 작업
                }

                else if (m[0].equals(setPImgTag)) {
                    viewPInfo(); // 이미지를 디코딩해서 imgByteV<ImageIcon>에 담음

                    if(imgByteV.size() == 1) {

                        ImageIcon newIcon = new ImageIcon();
                        Image scaledImage = imgByteV.get(0).getImage().getScaledInstance(client.gf.pimgL.getWidth(),
                                client.gf.pimgL.getHeight(), Image.SCALE_SMOOTH);
                        newIcon = new ImageIcon(scaledImage);
                        client.gf.pimgL.setIcon(newIcon);
                        // 닉네임도 표시
                        client.gf.pL1.setText(m[1]);

                        @SuppressWarnings("unused")
                        String tmp = m[2];
                    }
                    else if (imgByteV.size() == 2) {

                        ImageIcon newIcon = new ImageIcon();
                        Image scaledImage = imgByteV.get(0).getImage().getScaledInstance(client.gf.pimgL.getWidth(),
                                client.gf.pimgL.getHeight(), Image.SCALE_SMOOTH);
                        newIcon = new ImageIcon(scaledImage);
                        client.gf.pimgL.setIcon(newIcon);
                        // 닉네임도 표시
                        client.gf.pL1.setText(m[1]);

                        ImageIcon newIcon2 = new ImageIcon();
                        Image scaledImage2 = imgByteV.get(1).getImage().getScaledInstance(client.gf.pimgL2.getWidth(),
                                client.gf.pimgL2.getHeight(), Image.SCALE_SMOOTH);
                        newIcon2 = new ImageIcon(scaledImage2);
                        client.gf.pimgL2.setIcon(newIcon2);
                        // 닉네임도 표시
                        client.gf.pL2.setText(m[2]);
                    }


                    imgStrV.clear();
                    imgByteV.clear();
                }

                /* 방 인원 */
                else if (m[0].equals(uroomTag)) {
                    roomUser(m[1]);
                }

                /* 전적 조회 */
                else if (m[0].equals(searchTag)) {
                    searchRank(m[1]);
                }

                /* 오목 */
                else if (m[0].equals(omokTag)) {
                    inputOmok(m[1], m[2], m[3]);
                }

                else if (m[0].equals(omokBlackMsgTag)) {
                    String[] omokBlack = m[1].split(":");
                    omokSpectatorBlack(omokBlack);
                }

                else if (m[0].equals(omokWhiteMsgTag)) {
                    String[] omokWhite = m[1].split(":");
                    omokSpectatorWhite(omokWhite);
                }

                /* 관전자 돌 위치 */
                else if (m[0].equals(spectatorXYTag)) {
                    if (m[1].equals("") || m[1] != null) {
                        String[] omokBlack = m[1].split(":");
                        omokSpectatorBlack(omokBlack);
                    }

                    if (m[2].equals("") || m[2] != null) {
                        String[] omokWhite = m[2].split(":");
                        omokSpectatorWhite(omokWhite);
                    }
                }

                /* 패배 */
                else if (m[0].equals(loseTag)) {
                    loseGame();
                }

                /* 승리 */
                else if (m[0].equals(winTag)) {
                    winGame();
                }

                /* 전적 업데이트 */
                else if (m[0].equals(recordTag)) {
                    dataRecord(m[1]);
                }
            }
        } catch (Exception e) {
            System.out.println("[Client] Error: 메시지 받기 오류 > " + e.toString());
        }
    }

    /* 관리자 로그인시 사용할 메소드 */
    void adminLogin(String _m) {
        if (_m.equals(adminClTag)) {
            client.af.setVisible(true);
            client.lf.dispose();
        }
    }

    /* 관리자로 아이디 탈퇴시키는 메소드 */
    void adminUnregister(String _m1, String _m2) {
        if (_m2.equals("OKAY")) {
            JOptionPane.showMessageDialog(null, _m1, "탈퇴 성공", JOptionPane.INFORMATION_MESSAGE);
            // client.sendMsg(auserIdTag + "//");
        } else if (_m2.equals("FAIL")) {
            JOptionPane.showMessageDialog(null, _m1, "탈퇴 실패", JOptionPane.ERROR_MESSAGE);
        }
    }

    /* 로그인 성공 여부를 확인하는 메소드 */
    void loginCheck(String _m) {
        if (_m.equals("OKAY")) { // 로그인 성공
            System.out.println("[Client] 로그인 성공 : 메인 인터페이스 열림 : 로그인 인터페이스 종료");
            client.mf.setVisible(true);
            client.lf.dispose();
        }

        else { // 로그인 실패
            System.out.println("[Client] 로그인 실패 : 회원정보 불일치");
            JOptionPane.showMessageDialog(null, "로그인에 실패하였습니다", "로그인 실패", JOptionPane.ERROR_MESSAGE);
            client.lf.id.setText("");
            client.lf.pw.setText("");
        }
    }

    /* 회원가입 성공 여부를 확인하는 메소드 */
    void joinCheck(String _m) {
        if (_m.equals("OKAY")) { // 회원가입 성공
            JOptionPane.showMessageDialog(null, "회원가입에 성공하였습니다", "회원가입 성공", JOptionPane.INFORMATION_MESSAGE);
            client.jf.dispose();
            System.out.println("[Client] 회원가입 성공 : 회원가입 인터페이스 종료");
        }

        else { // 회원가입 실패
            JOptionPane.showMessageDialog(null, "기입하신 정보를 다시한번 확인해주세요", "회원가입 실패", JOptionPane.ERROR_MESSAGE);
            System.out.println("[Client] 회원가입 실패");
        }
    }

    /* 중복 여부를 확인하는 메소드 */
    void overlapCheck(String _m) {
        if (_m.equals("OKAY")) { // 사용 가능
            System.out.println("[Client] 사용 가능");
            JOptionPane.showMessageDialog(null, "사용 가능한 닉네임/아이디 입니다", "중복 확인", JOptionPane.INFORMATION_MESSAGE);
        }

        else { // 사용 불가능
            System.out.println("[Client] 사용 불가능");
            JOptionPane.showMessageDialog(null, "이미 존재하는 닉네임/아이디 입니다", "중복 확인", JOptionPane.ERROR_MESSAGE);
            client.jf.nickname.setText("");
        }
    }

    void viewAllInfo(String _m) {
        if (!_m.equals("FAIL")) {
            System.out.println("[Client] 회원 정보 조회 성공");
            Vector<Vector<Object>> userObjects = strToUserList(_m);

            // 각 내부 Vector<Object>를 Vector<String>으로 변환
            Vector<Vector<String>> userList = new Vector<>();
            for (Vector<Object> userObject : userObjects) {
                Vector<String> userStrings = new Vector<>();
                for (Object obj : userObject) {
                    userStrings.add(String.valueOf(obj));
                }
                userList.add(userStrings);
            }

            client.af.model.setList(userList);
            client.af.model.fireTableDataChanged();
        }
    }

    void viewInfo(String m1, String m2, String m3, String m4, String m5, String m6, String m7, String m8, String m9,
                  String m10) {
        if (!m1.equals("FAIL")) {
            System.out.println("[Client] 회원 정보 조회 성공");
            Vector<Vector<String>> list = new Vector<>(); // 수정된 부분
            Vector<String> record = new Vector<>(); // 수정된 부분
            record.add(m1);
            record.add(m2);
            record.add(m3);
            record.add(m4);
            record.add(m5);
            record.add(m6);
            record.add(m7);
            record.add(m8);
            record.add(m9);
            record.add(m10);
            list.add(record);
            client.af.model.setList(list);
            client.af.model.fireTableDataChanged();
        }
    }

    /* 내 정보를 확인하는 메소드 */
    void viewMyInfo(String _m1, String _encodedImage) { // _m1은 이름, 닉네임, 이메일
        String uInfo[] = _m1.split(",");

        client.inf.name.setText(uInfo[0]);
        client.inf.nickname.setText(uInfo[1]);
        client.inf.email.setText(uInfo[2]);
        client.inf.address.setText(uInfo[3]);

        if (_encodedImage != null && !_encodedImage.isEmpty()) {
            byte[] imageBytes = Base64.getDecoder().decode(_encodedImage);

            try {
                ImageIcon newIcon = new ImageIcon(imageBytes);
                Image scaledImage = newIcon.getImage().getScaledInstance(client.inf.imgL.getWidth(),
                        client.inf.imgL.getHeight(), Image.SCALE_SMOOTH);
                newIcon = new ImageIcon(scaledImage);
                client.inf.imgL.setIcon(newIcon);
                System.out.println("[Client] 이미지가 성공적으로 표시되었습니다.");
            } catch (Exception e) {
                System.out.println("[Client] 이미지 표시 실패: " + e.getMessage());
            }
        }
    }

    void viewPInfo() { // 이미지Str을 디코딩해서 벡터에담기
        for(int i = 0; i < imgStrV.size(); i++) {
            byte[] imageBytes = Base64.getDecoder().decode(imgStrV.get(i));
            try {
                ImageIcon newIcon = new ImageIcon(imageBytes);
                imgByteV.add(newIcon);
                System.out.println("이미지가 imageByte벡터에 담겼습니다.");
            } catch (Exception e) {
                System.out.println("이미지 imageByte벡터에 담기 실패" + e.getMessage());
            }
        }
    }

    /* 전적을 출력하는 메소드 */
    void viewRank(String _m) {
        if (!_m.equals("FAIL")) { // 전적 조회 성공
            System.out.println("[Client] 전적 조회 성공");
            String[] user = _m.split("@");

            client.rf.rank.setListData(user);
        }
    }

    /* 회원정보 변경 여부를 확인하는 메소드 */
    void changeInfo(String _m) {
        if (_m.equals("OKAY")) { // 회원정보 변경 성공
            System.out.println("[Client] 변경 성공");
            JOptionPane.showMessageDialog(null, "정상적으로 변경되었습니다", "회원정보변경", JOptionPane.INFORMATION_MESSAGE);
        }

        else { // 회원정보 변경 실패
            System.out.println("[Client] 이름 변경 실패");
            JOptionPane.showMessageDialog(null, "변경에 실패하였습니다", "회원정보변경", JOptionPane.ERROR_MESSAGE);
        }
    }

    /* 방 생성 여부를 확인하는 메소드 */
    void createRoom(String _m) {
        if (_m.equals("OKAY")) { // 방 생성 성공
            System.out.println("[Client] 방 생성 성공");

            client.gf.setVisible(true);
            client.mf.setVisible(false);
            client.gf.setTitle(client.mf.roomName);
            client.gf.dc = blackTag; // 방을 생성한 사람은 검은 돌
            client.gf.enable = true; // 돌 놓기 가능하게 바꿈
        }
    }

    /* 방 입장 여부를 확인하는 메소드 */
    void enterRoom(String _m) {
        System.out.println("_m: " + _m);
        String[] parts = _m.split("@@");

        System.out.println("parts[4] : " + parts[4]);

        if (parts[0].equals("OKAY")) {
            // 방 입장 성공
            System.out.println("[Client] 방 입장 성공");
            System.out.println(parts[3]);

            // 돌의 색깔 및 놓기 여부를 처리
            if (parts[4].equals("PLAYER")) {
                // 플레이어는 게임 화면을 표시
                client.gf.setVisible(true);
                client.mf.setVisible(false);
                client.gf.setTitle(client.mf.selRoom);

                if (parts[3].equals("BLACK")) {
                    client.gf.dc = blackTag; // 방에 먼저 입장한 사람은 검은 돌
                    client.gf.enable = true; // 검은 돌 먼저 놓기 가능
                } else if (parts[3].equals("WHITE")){
                    client.gf.dc = whiteTag; // 방에 나중에 입장한 사람은 흰 돌
                    client.gf.enable = false; // 흰 돌 나중에 놓기 가능
                }
                client.gf.isSpectator = false; // 플레이어는 관전자가 아님
            } else if (parts[4].equals("OBJ")) {
                // 관전자는 게임 화면을 표시하지 않음
                client.gf.setVisible(true);
                client.mf.setVisible(false);
                client.gf.setTitle(client.mf.selRoom);
                client.gf.dc = spectatorTag; // 관전자는 특정 돌 색상 또는 None으로 설정
                client.gf.enable = false; // 관전자는 돌 놓기 불가능
                client.gf.isSpectator = true; // 관전자는 관전자임
            }

            // 이후 필요한 처리를 추가로 구현해야 할 수 있습니다.
        } else {
            // 방 입장 실패
            System.out.println("[Client] 방 입장 실패");
            JOptionPane.showMessageDialog(null, "이미 2명이 찬 방이므로 입장할 수 없습니다", "방입장", JOptionPane.ERROR_MESSAGE);
        }

        clearChatInputField();
    }

    /* 접속 인원을 출력하는 메소드 */
    void viewCUser(String _m) {
        if (!_m.equals("")) {
            String[] user = _m.split("@");

            client.mf.cuList.setListData(user);
        }
    }

    /* 방 목록을 출력하는 메소드 */
    void roomList(String _m) {
        if (!_m.equals("")) {
            String[] room = _m.split("@");

            client.mf.rList.setListData(room);
        }
    }

    /* 방 인원 목록을 출력하는 메소드 */
    void roomUser(String _m) {
        if (!_m.equals("")) {
            String[] user = _m.split("@");
            int userLength = user.length;

            if (userLength == 1) {
                client.gf.userList.setListData(user);
            } else if (userLength == 2) {
                client.gf.userList.setListData(new String[] { user[0] }); // 첫 번째 값으로 userList 설정
                client.gf.userList.setListData(new String[] { user[1] }); // 두 번째 값으로 userList2 설정
            } else if (userLength > 2) {
                client.gf.userList.setListData(new String[] { user[0] }); // 첫 번째 값으로 userList 설정
                client.gf.userList.setListData(new String[] { user[1] }); // 두 번째 값으로 userList2 설정

                for (int i = 2; i < userLength; i++) {
                    client.gf.spectatorList.setListData(new String[] {user[i] });
                }
            }
        }
    }

    /* 방 인원 목록을 출력하는 메소드 */
    void roomObjv(String _m) {
        if(_m.equals("FAIL")) {
            String[] user = {" "};
            client.gf.spectatorList.setListData(user);
        }
        else if (!_m.equals("")) {
            String[] user = _m.split("@");
            client.gf.spectatorList.setListData(user);
        }
    }

    /* 전적 조회 메소드 */
    void searchRank(String _m) {
        if (!_m.equals("FAIL")) { // 전적 조회 성공
            client.srf.setVisible(true);
            client.srf.l.setText(_m);
        }
    }

    /* 상대 오목을 두는 메소드 */
    void inputOmok(String m1, String m2, String m3) {
        if (!m1.equals("") || !m2.equals("") || !m3.equals("")) {
            int n1 = Integer.parseInt(m1);
            int n2 = Integer.parseInt(m2);

            if (m3.equals(blackTag)) { // 검은 돌 태그면 1
                client.gf.omok[n2][n1] = 1;
            } else { // 흰 돌 태그면 2
                client.gf.omok[n2][n1] = 2;
            }

            client.gf.repaint();
            client.gf.enable = true; // 돌을 놓을 수 있도록 함
            client.gf.enableL.setText("본인 차례입니다.");
        }
    }


    /* 패배를 알리는 메소드 */
    void loseGame() {
        System.out.println("[Client] 게임 패배");
        JOptionPane.showMessageDialog(null, "게임에 패배하였습니다", "패배", JOptionPane.INFORMATION_MESSAGE);
        client.gf.remove(); // 화면을 지움
        client.gf.dispose();
        client.mf.setVisible(true);
    }

    /* 승리를 알리는 메소드 */
    void winGame() {
        System.out.println("[Client] 게임 승리");
        JOptionPane.showMessageDialog(null, "게임에 승리하였습니다", "승리", JOptionPane.INFORMATION_MESSAGE);
        client.gf.remove(); // 화면을 지움
        client.gf.dispose();
        client.mf.setVisible(true);
    }

    /* 전적 업데이트 여부를 알리는 메소드 */
    void dataRecord(String _m) {
        if (_m.equals("NO")) { // 전적 업데이트 안함
            System.out.println("[Client] 데이터 미반영 : 상대가 없음");
            JOptionPane.showMessageDialog(null, "게임 상대가 없어 전적을 반영하지 않았습니다", "전적반영", JOptionPane.INFORMATION_MESSAGE);
            client.sendMsg(rexitTag + "//" + "guser");
        } else if (_m.equals("OKAY")) { // 전적 업데이트 성공
            System.out.println("[Client] 데이터 반영 성공");
            JOptionPane.showMessageDialog(null, "전적 반영이 정상적으로 완료되었습니다", "전적반영", JOptionPane.INFORMATION_MESSAGE);
            client.sendMsg(rexitTag + "//" + "guser");
        } else if (_m.equals("FAIL")) { // 전적 업데이트 실패
            System.out.println("[Client] 데이터 반영 실패");
            JOptionPane.showMessageDialog(null, "시스템 장애로 인하여 전적 반영에 실패하였습니다", "전적반영", JOptionPane.INFORMATION_MESSAGE);
            client.sendMsg(rexitTag + "//" + "guser");
        } else if (_m.equals("OBJ")) {
            System.out.println("[Client] 관전자 ");
            client.gf.gameEnd = true;
            JOptionPane.showMessageDialog(null, "관전자이므로 전적 반영을 하지 않습니다.", "전적반영", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    void cleanGF(String _msg) {
        if(_msg.equals("0")) {
            client.gf.pL1.setText("플레이어1");
            client.gf.pimgL.setIcon(null);
        }else if(_msg.equals("1")){
            client.gf.pL2.setText("플레이어2");
            client.gf.pimgL2.setIcon(null);
        }else {
            client.gf.pL1.setText("플레이어1");
            client.gf.pimgL.setIcon(null);
            client.gf.pL2.setText("플레이어2");
            client.gf.pimgL2.setIcon(null);
        }
    }

    Vector<Vector<Object>> strToUserList(String userListStr) {
        String msg = userListStr;
        Vector<Vector<Object>> resultList = new Vector<>();

        String[] rows = msg.split("\n");
        for (int i = 0; i < rows.length; i++) {
            String row = rows[i];
            String[] elements = row.split(", ");
            Vector<Object> vectorRow = new Vector<>();

            for (int j = 0; j < elements.length; j++) {
                vectorRow.add(elements[j]);
            }

            resultList.add(vectorRow);

        }
        return resultList;
    }

    /* 방을 나가면 채팅 초기화 */
    void clearChatInputField() {
        client.gf.chatInputField.setText("");
        client.gf.chatTextPane.setText("");  // 이 부분을 추가하여 채팅 기록 초기화
    }

    void omokSpectatorBlack(String[] b) {
        client.gf.bd = b;
        client.gf.drawdol(client.gf.getGraphics(), true);
    }

    void omokSpectatorWhite(String[] w) {
        client.gf.wd = w;
        client.gf.drawdol(client.gf.getGraphics(), true);
    }
}