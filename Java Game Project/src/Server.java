import java.net.*;
import java.util.*;

import java.io.*;

//클라이언트의 연결 요청 및 입출력을 상시 관리하는 클래스.
public class Server {
    ServerSocket ss = null;

    /* 각 객체들을 Vector로 관리 */
    Vector<CCUser> alluser; // 연결된 모든 클라이언트
    Vector<CCUser> waituser; // 대기실에 있는 클라이언트
    Vector<Room> room; // 생성된 Room

    public static void main(String[] args) {
        Server server = new Server();

        server.alluser = new Vector<>();
        server.waituser = new Vector<>();
        server.room = new Vector<>();

        try {
            // 서버 소켓 준비
            server.ss = new ServerSocket(1257);
            System.out.println("[Server] 서버 소켓 준비 완료");

            // 클라이언트의 연결 요청을 상시 대기.
            while (true) {
                Socket socket = server.ss.accept();
                CCUser c = new CCUser(socket, server); // 소켓과 서버를 넘겨 CCUser(접속한 유저 관리)객체 생성

                c.start(); // CCUser 스레드 시작
            }
        } catch (SocketException e) { // 각 오류를 콘솔로 알린다.
            System.out.println("[Server] 서버 소켓 오류 > " + e.toString());
        } catch (IOException e) {
            System.out.println("[Server] 입출력 오류 > " + e.toString());
        }
    }
}

//서버에 접속한 유저와의 메시지 송수신을 관리하는 클래스.
//스레드를 상속받아 연결 요청이 들어왔을 때도 독립적으로 동작할 수 있도록 한다.
class CCUser extends Thread {
    Server server;
    Socket socket;

    /* 각 객체를 Vector로 관리 */
    Vector<CCUser> auser; // 연결된 모든 클라이언트
    Vector<CCUser> wuser; // 대기실에 있는 클라이언트
    Vector<Room> room; // 생성된 Room

    Database db = new Database();

    /* 메시지 송수신을 위한 필드 */
    OutputStream os;
    DataOutputStream dos;
    InputStream is;
    DataInputStream dis;

    String msg; // 수신 메시지를 저장할 필드
    String nickname; // 클라이언트의 닉네임을 저장할 필드
    String id; // 클라이언트의 id를 받거나, 관리자가 요청을보낼 id를 저장
    String P1ImgString;
    String P2ImgString;

    Room myRoom; // 입장한 방 객체를 저장할 필드

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
    final String cuserTag = "CUSER"; // 접속유저
    final String plyrTag = "PLAYER"; // 플레이어
    final String objvTag = "OBJV"; // 관전자
    final String stateTag = "STATE"; // 유저가 플레이어인지 관전자인지
    final String objvuTag = "OUSER"; // 관전자 유저 태그
    final String searchTag = "SEARCH"; // 전적조회(한명)
    final String pexitTag = "PEXIT"; // 프로그램종료
    final String rexitTag = "REXIT"; // 방퇴장
    final String omokTag = "OMOK"; // 오목
    final String startTag = "START"; // 게임 시작
    final String winTag = "WIN"; // 승리
    final String loseTag = "LOSE"; // 패배
    final String recordTag = "RECORD"; // 전적업데이트

    final String lobbyChatTag = "LOBBYCHAT"; // 대기방 채팅
    final String chatTag = "CHAT"; // 채팅
    final String nickNameTag = "NICKNAME"; // 닉네임 태그
    final String chatRoomTag = "CHATROOM"; // 채팅 방 기능 태그
    final String adminTag = "ADMIN"; // 관리자 로그인 태그
    final String adminClTag = "ADMINCL"; // 관리자 로그인 태그(클라이언트로)
    final String auserIdTag = "AUSERID"; // 모든 유저 아이디 요청 태그
    final String unRegisterIdTag = "UNREGI"; // 회원 탈퇴 태그
    final String unRegisterIdClTag = "UNREGICL"; // 회원 탈퇴 태그(클라이언트로)
    final String chSIdTag = "CHSID"; // 서버에서 처리할 아이디 변경
    final String sendImgTag = "SENDIMG"; // 이미지 전송 태그
    final String chatLogTag = "CHATLOG"; // 채팅 로그 조회 태그
    final String omokBlackMsgTag = "OMOKBLACKXY"; // 오목 검은 돌 위치 태그
    final String omokWhiteMsgTag = "OMOKWHITEXY"; // 오목 하얀 돌 위치 태그
    final String spectatorXYTag = "SPECTATORXY"; // 관전자 돌 위치 태그
    final String viewMyInfoTag = "VIEWMYINFO"; // 자신의 회원 정보 조회 기능 태그
    final String uroomoTag = "UROOMO"; // 방 옵저버
	final String fidTag = "FID"; // 아이디 찾기 태그
	final String fpwTag = "FPW"; // 비밀번호 찾기 태그

    CCUser(Socket _s, Server _ss) {
        this.socket = _s;
        this.server = _ss;

        auser = server.alluser;
        wuser = server.waituser;
        room = server.room;
    }

    public void run() {
        try {

            System.out.println("[Server] 클라이언트 접속 > " + this.socket.toString());

            os = this.socket.getOutputStream();
            dos = new DataOutputStream(os);
            is = this.socket.getInputStream();
            dis = new DataInputStream(is);

            while (true) {
                msg = dis.readUTF(); // 메시지 수신을 상시 대기한다.

                String[] m = msg.split("//"); // msg를 "//"로 나누어 m[]배열에 차례로 집어넣는다.

                // 수신받은 문자열들의 첫 번째 배열(m[0])은 모두 태그 문자. 각 기능을 분리한다.

                /* 관리자 로그인 */
                if (m[0].equals(adminTag)) {
                    String mm = db.loginCheck(m[1], m[2]);

                    if (!mm.equals("null")) {
                        nickname = mm;
                        dos.writeUTF(nickNameTag + "//" + nickname);

                        auser.add(this);
                        dos.writeUTF(adminTag + "//" + adminClTag + "//" + m[1]);
                    }

                    else { // 로그인 실패
                        dos.writeUTF(loginTag + "//FAIL");
                    }
                }

                /* 관리자 요청 회원탈퇴 */
                else if (m[0].equals(unRegisterIdTag)) {
                    db.backupId(m[1]); // 백업 진행
                    String msg = db.unRegisterId(m[1]);
                    System.out.println("[Server] " + msg);
                    if (msg.equals("관리자 계정은 삭제할 수 없습니다.")) {
                        dos.writeUTF(unRegisterIdClTag + "//관리자 계정은 삭제할 수 없습니다." + "//FAIL");
                    } else if (msg.equals("해당 계정이 삭제되었습니다.")) {
                        dos.writeUTF(unRegisterIdClTag + "//해당 계정이 삭제되었습니다." + "//OKAY");
                    } else if (msg.equals("회원 탈퇴에 실패했습니다.")) {
                        dos.writeUTF(unRegisterIdClTag + "//회원 탈퇴에 실패했습니다." + "//FAIL");
                    }
                }

                /* 로그인 */
                else if (m[0].equals(loginTag)) { // m[1]은 id, m[2]는 비밀번호
                    String mm1 = db.loginCheck(m[1], m[2]); // 닉네임값을 받음

                    if (!mm1.equals("null")) { // 로그인 성공
                        nickname = mm1; // 로그인한 사용자의 닉네임을 필드에 저장
                        dos.writeUTF(nickNameTag + "//" + nickname);

                        auser.add(this); // 모든 접속 인원에 추가
                        wuser.add(this); // 대기실 접속 인원에 추가

                        dos.writeUTF(loginTag + "//OKAY//" + m[1]); // 로그인하면 id를 클라로보냄

                        sendWait(connectedUser()); // 대기실 접속 유저에 모든 접속 인원을 전송

                        if (room.size() > 0) { // 생성된 방의 개수가 0 이상일 때
                            sendWait(roomInfo()); // 대기실 접속 인원에 방 목록을 전송
                        }

                    }

                    else { // 로그인 실패
                        dos.writeUTF(loginTag + "//FAIL");
                    }
                }
                
                /* id 검색 */
				else if (m[0].equals(fidTag)) {
					String uid = db.findId(m[1], m[2]); // name과 nickname받아서 id를 검색함
					if (uid.equals("FAIL")) {
						dos.writeUTF(fidTag + "//FAIL");
					} else {
						dos.writeUTF(fidTag + "//" + uid);
					}

				}

				/* password 검색 */
				else if (m[0].equals(fpwTag)) {
					String upw = db.findPw(m[1], m[2], m[3]); // name과 nickname받아서 id를 검색함
					if (upw.equals("FAIL")) {
						dos.writeUTF(fpwTag + "//FAIL");
					} else {
						dos.writeUTF(fpwTag + "//" + upw);
					}

				}

                /* 이미지 전송 */
                else if (m[0].equals(sendImgTag)) {
                    db.imageToDatabase(m[1], m[2]);
                }

                /* 대기방 채팅 */
                else if (m[0].equals(lobbyChatTag)) {
                    for (int i = 0; i < auser.size(); i++) {
                        auser.get(i).dos.writeUTF(msg);
                    }
                }

                /* 채팅 */
                else if (m[0].equals(chatTag)) {
                	System.out.println("msg : " + msg);
                    for (int i = 0; i < myRoom.ccu.size(); i++) {
                        myRoom.ccu.get(i).dos.writeUTF(msg);
                    }
                }

                /* 채팅방 입장 */
                else if (m[0].equals(chatRoomTag)) {
                    for (int i = 0; i < auser.size(); i++) {
                        auser.get(i).dos.writeUTF(msg);
                    }
                }

                /* 채팅 내역 확인 */
                else if (m[0].equals(chatLogTag)) { // m[1] = 관리자 프레임에서 보낸 id

                }

                /* 회원가입 */
                else if (m[0].equals(joinTag)) {
                    if (db.joinCheck(m[1], m[2], m[3], m[4], m[5], m[6], m[7], m[8], m[9])) { // 회원가입 성공
                        dos.writeUTF(joinTag + "//OKAY");
                    }

                    else { // 회원가입 실패
                        dos.writeUTF(joinTag + "//FAIL");
                    }
                }

                /* 중복확인 */
                else if (m[0].equals(overTag)) {
                    if (db.overCheck(m[1], m[2])) { // 사용 가능
                        dos.writeUTF(overTag + "//OKAY");
                    }

                    else { // 사용 불가능
                        dos.writeUTF(overTag + "//FAIL");
                    }
                }

                /* 회원정보 전체 조회 */
                else if (m[0].equals(viewDBTag)) {
                    Vector<Vector<Object>> userList = db.getAllUser();
                    String userListStr = userListToStr(userList);
                    if (!userList.equals(null)) {
                        dos.writeUTF(viewDBClTag + "//" + userListStr);
                    } else {
                        dos.writeUTF(viewDBClTag + "//FAIL");
                    }

                }

                /* 회원 이미지 조회 */
				else if (m[0].equals(viewImgTag)) {
					String msg1 = db.viewMyInfo(m[1]);
					String msg2 = db.viewImg(m[1]); // m[1]은 로그인한 id임

					if (msg1.equals("회원정보 검색 실패") || msg2.equals("이미지 조회에 실패했습니다.")) {
						dos.writeUTF(viewImgTag + "//" + msg1 + "//" + msg2); // 이미지 조회에 실패했다는 msg
					} else {
						dos.writeUTF(viewImgTag + "//" + msg1 + "//" + msg2); // String 형태의 인코딩 이미지
					}

				}

                /* 회원정보 조회 */
                else if (m[0].equals(viewTag)) { // m[1]은 category를 받고 m[2]로는 값을 받기
                    String msg = db.viewInfo(m[1], m[2]);
                    if (msg.equals("카테고리에 해당하는 찾고자하는 회원이 없습니다.")) { // 회원 정보가 비어있지 않으면 성공
                        dos.writeUTF(viewTag + "//" + msg);		// 일치하는 회원이 없을 때
                        System.out.println("[Server] 회원정보 조회 실패");
                    } else {
                        dos.writeUTF(viewTag + "//" + msg);		// 회원 정보를 담고 있음
                        System.out.println("[Server] 회원정보 조회 성공");
                    }
                }

                /* 회원정보 변경 */
                else if (m[0].equals(changeTag)) {
                    if (db.changeInfo(id, m[1], m[2])) { // 변경 성공
                        System.out.println(id);
                        dos.writeUTF(changeTag + "//OKAY");
                    }

                    else { // 변경 실패
                        dos.writeUTF(changeTag + "//FAIL");
                    }
                }

                /* 전체 전적 조회 */
                else if (m[0].equals(rankTag)) {
                    if (!db.viewRank().equals("")) { // 조회 성공
                        dos.writeUTF(rankTag + "//" + db.viewRank()); // 태그와 조회한 내용을 같이 전송
                    }

                    else { // 조회 실패
                        dos.writeUTF(rankTag + "//FAIL");
                    }
                }

                /* 방 생성 */
                else if (m[0].equals(croomTag)) {
                    myRoom = new Room(); // 새로운 Room 객체 생성 후 myRoom에 초기화
                    myRoom.title = m[1]; // 방 제목을 m[1]로 설정
                    myRoom.count++; // 방의 인원수 하나 추가
                    myRoom.pcount++;

                    room.add(myRoom); // room 배열에 myRoom을 추가

                    myRoom.ccu.add(this); // myRoom의 접속인원에 클라이언트 추가
                    myRoom.player.add(this);
                    wuser.remove(this); // 대기실 접속 인원에서 클라이언트 삭제

                    dos.writeUTF(croomTag + "//OKAY");
                    dos.writeUTF(eroomTag + "//OKAY@@" + m[1] + "@@" + myRoom.rmsg + "@@" + myRoom.romok + "@@PLAYER");
                    System.out.println("[Server] " + nickname + " : 방 '" + m[1] + "' 생성");

                    sendWait(roomInfo()); // 대기실 접속 인원에 방 목록을 전송
                    sendRoom(roomUser()); // 방에 입장한 인원에 방 인원 목록을 전송
                    sendRoom(roomObjv());
                    
                    // 플레이어 수만큼 이미지Str 추출 
					Vector<String> tmpVector = new Vector<>();
					for(int i = 0; i < myRoom.player.size(); i++) { // 추출은 플레이어의 수만큼만 이루어져야함
						tmpVector.add(db.viewImg2(myRoom.player.get(i).nickname));
					}
					
					for(int i = 0; i < myRoom.ccu.size(); i++) { // 방에 입장한 유저수 만큼 반복
						for(int j = 0; j < myRoom.player.size(); j++) { // 플레이어의 수 만큼 플레이어의 닉네임에 해당하는 인코딩 imgString을 전달
							myRoom.ccu.get(i).dos.writeUTF(addPImgTag + "//" + tmpVector.get(j));
							// 플레이어는 입장한 순서대로임 즉 k = 1이면 첫번째 플레이어
						}
					}
					
					String p1NN = "플레이어1";
					String p2NN = "플레이어2";
					if(myRoom.player.size() == 1) {
						p1NN = myRoom.player.get(0).nickname;
					}else if (myRoom.player.size() == 2) {
						p1NN = myRoom.player.get(0).nickname;
						p2NN = myRoom.player.get(1).nickname;
					}
							
					for(int i = 0; i < myRoom.ccu.size(); i++) { // 방에 입장한 모든 유저에게
						myRoom.ccu.get(i).dos.writeUTF(setPImgTag + "//" + p1NN + "//" + p2NN);
					}
                }

                else if (m[0].equals(eroomTag)) {
                	System.out.println("이미지 : " + P1ImgString);
                    for (int i = 0; i < room.size(); i++) {
                        Room r = room.get(i);
                        if (m.length > 1 && r.title.equals(m[1])) {
                            System.out.println(r.pcount + "와 "+ r.ocount);
                            boolean isPlayer = r.pcount < 2 && r.ocount < 2;
                            System.out.println(isPlayer);

                            if (r.pcount < 3 && r.ocount < 2) {
                                myRoom = room.get(i);
                                myRoom.count++;

                                if (roomUser().split("@").length < 2) {
                                    myRoom.romok = (myRoom.pcount == 2) ? "BLACK" : "WHITE";
                                    myRoom.pcount++;

                                    myRoom.player.add(this);
                                    dos.writeUTF(eroomTag + "//OKAY@@" + m[1] + "@@" + myRoom.rmsg + "@@" + myRoom.romok + "@@PLAYER");
                                    sendPlayer("OPLAYER", myRoom.player.size(), 0);
                                } else if (roomUser().split("@").length >= 2) {
                                    myRoom.ocount++;
                                    myRoom.obj.add(this);
                                    dos.writeUTF(eroomTag + "//OKAY@@" + m[1] + "@@" + myRoom.rmsg + "@@" + myRoom.romok + "@@OBJ");
                                    sendRoom("OUSER//" + objUser());
                                }

                                myRoom.ccu.add(this);
                                sendWait(roomInfo());
                                sendRoom(roomUser());
                                sendRoom(roomObjv());
                                
                                // 플레이어 수만큼 이미지Str 추출 
								Vector<String> tmpVector = new Vector<>();
								for(int j = 0; j < myRoom.player.size(); j++) {
									tmpVector.add(db.viewImg2(myRoom.player.get(j).nickname));
								}
								
								for(int j = 0; j < myRoom.ccu.size(); j++) { // 방에 입장한 모든 유저에게
									for(int k = 0; k < myRoom.player.size(); k++) { // 플레이어의 수 만큼 플레이어의 닉네임에 해당하는 인코딩 imgString을 전달
										myRoom.ccu.get(j).dos.writeUTF(addPImgTag + "//" + tmpVector.get(k));
										// 플레이어는 입장한 순서대로임 즉 k = 1이면 첫번째 플레이어
									}
								}
								
								String p1NN = "플레이어1";
								String p2NN = "플레이어2";
								if(myRoom.player.size() == 1) {
									p1NN = myRoom.player.get(0).nickname;
								}else if (myRoom.player.size() == 2) {
									p1NN = myRoom.player.get(0).nickname;
									p2NN = myRoom.player.get(1).nickname;
								}
								
								for(int j = 0; j < myRoom.ccu.size(); j++) { // 방에 입장한 모든 유저에게
									myRoom.ccu.get(j).dos.writeUTF(setPImgTag + "//" + p1NN + "//" + p2NN);
								}

                                System.out.println("[Server] " + nickname + " : 방 '" + m[1] + "' 입장");
                            } else {
                                dos.writeUTF(eroomTag + "//FAIL");
                                System.out.println("[Server] " + nickname + " : 방 '" + m[1] + "' 입장 실패");
                            }
                        } else {
                            dos.writeUTF(eroomTag + "//FAIL");
                            System.out.println("[Server] " + nickname + " : 방 '" + m[1] + "' 입장 실패");
                        }
                    }
                }
                
                /* 방 관전 */
				else if (m[0].equals(oroomTag)) { // m[1]은 방이름
					for (int i = 0; i < room.size(); i++) { // 생성된 방의 개수만큼 반복
						Room r = room.get(i);
						if (r.title.equals(m[1])) { // 방 제목이 같고
							if (r.ocount < 2) { // 방 관전자수가 2명이하면 관전자를 추가함
								myRoom = room.get(i);
								myRoom.count++; // 방 인원수 추가
								myRoom.ocount++; // 방 관전자수 추가

								wuser.remove(this); // 대기실 인원 해당클라이언트 삭제
								myRoom.obj.add(this); // myRoom의 관전자에 해당클라이언트 추가
								myRoom.ccu.add(this); // myRoom의 접속인원에 해당클라이언트 추가

								sendWait(roomInfo()); // 대기실 접속 인원에 방 목록을 전송
								sendRoom(roomUser()); // 방에 입장한 인원에 방 플레이어 목록을 전송
								sendRoom(roomObjv()); // 방에 입장한 인원에 방 관전자 목록을 전송
								
								// 플레이어 수만큼 이미지Str 추출 
								Vector<String> tmpVector = new Vector<>();
								for(int j = 0; j < myRoom.player.size(); j++) {
									tmpVector.add(db.viewImg2(myRoom.player.get(j).nickname));
								}
								
								for(int j = 0; j < myRoom.ccu.size(); j++) { // 방에 입장한 모든 유저에게
									for(int k = 0; k < myRoom.player.size(); k++) { // 플레이어의 수 만큼 플레이어의 닉네임에 해당하는 인코딩 imgString을 전달
										myRoom.ccu.get(j).dos.writeUTF(addPImgTag + "//" + tmpVector.get(k));
										// 플레이어는 입장한 순서대로임 즉 k = 1이면 첫번째 플레이어
									}
								}

								dos.writeUTF(oroomTag + "//OKAY//" + m[1]);
								System.out.println("[Server] " + nickname + " : 방 '" + m[1] + "'에 입장");
								System.out.println("[Server] 관전자로 입장");
								
								String p1NN = "플레이어1";
								String p2NN = "플레이어2";
								if(myRoom.player.size() == 1) {
									p1NN = myRoom.player.get(0).nickname;
								}else if (myRoom.player.size() == 2) {
									p1NN = myRoom.player.get(0).nickname;
									p2NN = myRoom.player.get(1).nickname;
								}
								
								for(int j = 0; j < myRoom.ccu.size(); j++) { // 방에 입장한 모든 유저에게
									myRoom.ccu.get(j).dos.writeUTF(setPImgTag + "//" + p1NN + "//" + p2NN);
								}
							}
						} else { // 같은 방 제목이 없으니 입장 실패
							dos.writeUTF(oroomTag + "//FAIL");
							System.out.println("[Server] " + nickname + " : 방 '" + m[1] + "' 입장 오류");
						}
					}
				}
                
                /* 방 퇴장 */
                else if (m[0].equals(rexitTag)) {
                	if (m[1].equals("guser")) {
                    	myRoom.player.remove(this);
                    	myRoom.ccu.remove(this); // myRoom의 접속 인원에서 클라이언트 삭제
                    	myRoom.pcount--;
                        myRoom.count--; // myRoom의 인원수 하나 삭제
                        wuser.add(this); // 대기실 접속 인원에 클라이언트 추가
                    } else if (m[1].equals("suser")) {
                    	myRoom.obj.remove(this);
                    	myRoom.ccu.remove(this); // myRoom의 접속 인원에서 클라이언트 삭제
                    	myRoom.ocount--;
                        myRoom.count--; // myRoom의 인원수 하나 삭제
                        wuser.add(this); // 대기실 접속 인원에 클라이언트 추가
                    }
                	
                    System.out.println("[Server] " + nickname + " : 방 '" + myRoom.title + "' 퇴장");
                    
                    if (myRoom.count == 0) { // myRoom의 인원수가 0이면 myRoom을 room 배열에서 삭제
                        room.remove(myRoom);
                    }

                    if (room.size() != 0) { // 생성된 room의 개수가 0이 아니면 방에 입장한 인원에 방 인원 목록을 전송
                        sendRoom(roomUser());

                    }

                    sendWait(roomInfo()); // 대기실 접속 인원에 방 목록을 전송
                    sendWait(connectedUser()); // 대기실 접속 인원에 전체 접속 인원을 전송
                    sendRoom(roomObjv());
                }


                /* 전적 조회 */
                else if (m[0].equals(searchTag)) {
                    String mm = db.searchRank(m[1]);

                    if (!mm.equals("null")) { // 조회 성공
                        dos.writeUTF(searchTag + "//" + mm); // 태그와 조회한 내용을 같이 전송
                    }

                    else { // 조회 실패
                        dos.writeUTF(searchTag + "//FAIL");
                    }
                }

                /* 서버로 회원정보변경 요청이 들어올때 받을 id메소드 */
                else if (m[0].equals(chSIdTag)) {
                    this.id = m[1];
                }

                /* 프로그램 종료 */
                else if (m[0].equals(pexitTag)) {
                    auser.remove(this); // 전체 접속 인원에서 클라이언트 삭제
                    wuser.remove(this); // 대기실 접속 인원에서 클라이언트 삭제

                    sendWait(connectedUser()); // 대기실 접속 인원에 전체 접속 인원을 전송
                }

                /* 오목 */
                else if (m[0].equals(omokTag)) {
                    for (int i = 0; i < myRoom.ccu.size(); i++) { // myRoom의 인원수만큼 반복

                        if (!myRoom.ccu.get(i).nickname.equals(nickname)) { // 방 접속 인원 중 클라이언트와 다른 닉네임의 클라이언트에게만 전송
                            myRoom.ccu.get(i).dos.writeUTF(omokTag + "//" + m[1] + "//" + m[2] + "//" + m[3]);
                        }
                    }
                }

                /* 오목 위치 */
                else if (m[0].equals(omokBlackMsgTag)) {
                	System.out.println("x : " + m[1] + "y : " + m[2]);
                    recordBlackOmok(m[1], m[2]);
                    dos.writeUTF(omokBlackMsgTag + "//" + myRoom.romokblackmsg + "//");
                    
                }
                
                /* 오목 위치 */
                else if (m[0].equals(omokWhiteMsgTag)) {
                	System.out.println("x : " + m[1] + "y : " + m[2]);
                    recordWhiteOmok(m[1], m[2]);
                	dos.writeUTF(omokWhiteMsgTag + "//" + myRoom.romokwhitemsg + "//");
                    
                }
                
                /* 관전자 오목 위치 저장 */
                else if (m[0].equals(spectatorXYTag)) {
                	String black = myRoom.romokblackmsg;
                	String white = myRoom.romokwhitemsg;
                	if (black.length() != 0 && white.length() != 0) {
                	    dos.writeUTF(spectatorXYTag + "//" + black + "//" + white + "//");
                	}
                }

                /* 승리 및 전적 업데이트 */
                else if (m[0].equals(winTag)) {
                	myRoom.romokblackmsg = "";
                	myRoom.romokwhitemsg = "";
                    System.out.println("[Server] " + nickname + " 승리");

                    if (db.winRecord(nickname)) { // 전적 업데이트가 성공하면 업데이트 성공을 전송
                        dos.writeUTF(recordTag + "//OKAY");
                    } else { // 전적 업데이트가 실패하면 업데이트 실패를 전송
                        dos.writeUTF(recordTag + "//FAIL");
                    }

                    for (int i = 0; i < myRoom.player.size(); i++) { // myRoom의 인원수만큼 반복

                        /* 방 접속 인원 중 클라이언트와 다른 닉네임의 클라이언트일때만 */
                        if (!myRoom.player.get(i).nickname.equals(nickname)) {
                            myRoom.player.get(i).dos.writeUTF(loseTag + "//");

                            if (db.loseRecord(myRoom.ccu.get(i).nickname)) { // 전적 업데이트가 성공하면 업데이트 성공을 전송
                                myRoom.player.get(i).dos.writeUTF(recordTag + "//OKAY");
                            } else { // 전적 업데이트가 실패하면 업데이트 실패를 전송
                                myRoom.player.get(i).dos.writeUTF(recordTag + "//FAIL");
                            }
                        }
                    }

                    for (int i = 0; i < myRoom.obj.size(); i++) {
                        myRoom.obj.get(i).dos.writeUTF(recordTag + "//OBJ");
                    }
                }

                /* 패배, 기권 및 전적 업데이트 */
                else if (m[0].equals(loseTag)) {
                	myRoom.romokblackmsg = "";
                	myRoom.romokwhitemsg = "";
                    System.out.println("PLAYER 명 수 : " + myRoom.obj.size());
                    if (myRoom.count == 1) { // 기권을 했는데 방 접속 인원이 1명일 때 전적 미반영을 전송
                        dos.writeUTF(recordTag + "//NO");
                    }

                    else if (myRoom.count >= 2) { // 기권 및 패배를 했을 때 방 접속 인원이 2명일 때
                        dos.writeUTF(loseTag + "//");

                        if (db.loseRecord(nickname)) { // 전적 업데이트가 성공하면 업데이트 성공을 전송
                            dos.writeUTF(recordTag + "//OKAY");
                        } else { // 전적 업데이트가 실패하면 업데이트 실패를 전송
                            dos.writeUTF(recordTag + "//FAIL");
                        }

                        for (int i = 0; i < myRoom.player.size(); i++) { // myRoom의 인원수만큼 반복

                            /* 방 접속 인원 중 클라이언트와 다른 닉네임의 클라이언트일때만 */
                            if (!myRoom.player.get(i).nickname.equals(nickname)) {
                                myRoom.player.get(i).dos.writeUTF(winTag + "//");

                                if (db.winRecord(myRoom.ccu.get(i).nickname)) { // 전적 업데이트가 성공하면 업데이트 성공을 전송
                                    myRoom.player.get(i).dos.writeUTF(recordTag + "//OKAY");
                                } else { // 전적 업데이트가 실패하면 업데이트 실패를 전송
                                    myRoom.player.get(i).dos.writeUTF(recordTag + "//FAIL");
                                }
                            }
                        }

                        for (int i = 0; i < myRoom.obj.size(); i++) {
                            myRoom.obj.get(i).dos.writeUTF(recordTag + "//OBJ");
                        }
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("[Server] 입출력 오류 > " + e.toString());
        }
    }

    String userListToStr(Vector<Vector<Object>> userList) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < userList.size(); i++) {
            Vector<Object> row = userList.get(i);

            for (int j = 0; j < row.size(); j++) {
                result.append(row.get(j)).append(", ");
            }

            result.delete(result.length() - 2, result.length());
            result.append("\n");
        }

        return result.toString();
    }

    /* 현재 존재하는 방의 목록을 조회 */
    String roomInfo() {
        String msg = vroomTag + "//";

        for (int i = 0; i < room.size(); i++) {
            msg = msg + room.get(i).title + " : " + room.get(i).count + "@";
        }
        return msg;
    }

    /* 클라이언트가 입장한 방의 인원을 조회 */
    String roomUser() {
        String msg = uroomTag + "//";

        for (int i = 0; i < myRoom.ccu.size(); i++) {
            msg = msg + myRoom.ccu.get(i).nickname + "@";
        }
        return msg;
    }
    
    /* 클라이언트가 입장한 방의 인원을 조회 */
	String roomObjv() {
		String msg = uroomoTag + "//";
		
		if(myRoom.obj.size() < 1) {
			msg = msg + "FAIL";
		}else {
			for (int i = 0; i < myRoom.obj.size(); i++) {
				msg = msg + myRoom.obj.get(i).nickname + "@";
			}
		}
		
		return msg;
	}

    /* 접속한 모든 회원 목록을 조회 */
    String connectedUser() {
        String msg = cuserTag + "//";

        for (int i = 0; i < auser.size(); i++) {
            msg = msg + auser.get(i).nickname + "@";
        }
        return msg;
    }

    String objUser() {
        String msg = "";

        for (int i = 0; i < myRoom.obj.size(); i++) {
            msg = msg + myRoom.obj.get(i).nickname + "@";
        }

        return msg;
    }

    /* 대기실에 있는 모든 회원에게 메시지 전송 */
    void sendWait(String m) {
        for (int i = 0; i < wuser.size(); i++) {
            try {
                wuser.get(i).dos.writeUTF(m);
            } catch (IOException e) {
                wuser.remove(i--);
            }
        }
    }

    /* 방에 입장한 모든 회원에게 메시지 전송 */
    void sendRoom(String m) {
        for (int i = 0; i < myRoom.ccu.size(); i++) {
            try {
                myRoom.ccu.get(i).dos.writeUTF(m);
            } catch (IOException e) {
                myRoom.ccu.remove(i--);
            }
        }
    }

    /* 방에 입장한 모든 플레이어에게만 순서를 전송하는 메소드 */
    void sendPlayer(String m, int a, int b) {
        for (int i = 0; i < myRoom.player.size(); i++) {
            try {
                myRoom.player.get(i).dos.writeUTF(m + "//" + a);
                a = b;
            } catch (IOException e) {
                myRoom.ccu.remove(i--);
            }
        }
    }

    void recordBlackOmok(String x, String y) {
        myRoom.romokblackmsg += x + "@@" + y + ":" ;
        System.out.println(myRoom.romokblackmsg + "입니다.");
    }

    void recordWhiteOmok(String x, String y) {
        myRoom.romokwhitemsg += x + "@@" + y + ":" ;
        System.out.println(myRoom.romokwhitemsg + "입니다.");
    }
}