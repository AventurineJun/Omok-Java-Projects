import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JList;

//클라이언트가 요청한 데이터베이스 업데이트 및 쿼리 작업을 수행하는 클래스.
//서버에서 객체 생성 시에 데이터베이스 연동 작업을 수행하고 다른 부가적인 작업들은 메소드를 통해 서버에서 불려지면 수행하도록 한다.
public class Database {

    /* 데이터베이스와의 연결에 사용할 변수들 */
    Connection con = null;
    Statement stmt = null;
    PreparedStatement pstmt = null;
    String url = "jdbc:mysql://localhost/login?serverTimezone=Asia/Seoul";
    String user = "root";
    String passwd = "2558jun@";

    Database() { // Database 객체 생성 시 데이터베이스 서버와 연결한다.
        try { // 데이터베이스 연결은 try-catch문으로 예외를 잡아준다.
            // 데이터베이스와 연결한다.
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url, user, passwd);
            stmt = con.createStatement();
            System.out.println("[Server] MySQL 서버 연동 성공"); // 데이터베이스 연결에 성공하면 성공을 콘솔로 알린다.
        } catch (Exception e) { // 데이터베이스 연결에 예외가 발생했을 때 실패를 콘솔로 알린다.
            System.out.println("[Server] MySQL 서버 연동 실패> " + e.toString());
        }
    }

    // 로그인 여부를 확인하는 메소드. 서버에 닉네임을 String 형식으로 반환한다.
    String loginCheck(String _i, String _p) {
        String nickname = "null"; // 반환할 닉네임 변수를 "null"로 초기화.

        // 매개변수로 받은 id와 password값을 id와 pw값에 초기화한다.
        String id = _i;
        String pw = _p;

        try {
            // id와 일치하는 비밀번호와 닉네임이 있는지 조회한다.
            String checkingStr = "SELECT password, nickname FROM member WHERE id='" + id + "'";
            ResultSet result = stmt.executeQuery(checkingStr);

            int count = 0;
            while (result.next()) {
                // 조회한 비밀번호와 pw 값을 비교.
                if (pw.equals(result.getString("password"))) { // true일 경우 nickname에 조회한 닉네임에 반환하고 로그인 성공을 콘솔로 알린다.
                    nickname = result.getString("nickname");
                    System.out.println("[Server] 로그인 성공");
                }

                else { // false일 경우 nickname을 "null"로 초기화하고 로그인 실패를 콘솔로 알린다.
                    nickname = "null";
                    System.out.println("[Server] 로그인 실패");
                }
                count++;
            }
        } catch (Exception e) { // 조회에 실패했을 때 nickname을 "null"로 초기화. 실패를 콘솔로 알린다.
            nickname = "null";
            System.out.println("[Server] 로그인 실패 > " + e.toString());
        }

        return nickname; // nickname 반환
    }

    /* 아이디 찾기 메소드 */
	String findId(String _m1, String _m2) {
		String uname = _m1;
		String unickname = _m2;
		String msg = "";

		try {
			// name과 일치하는 id가 있는지 조회한다.
			String checkNameStr = "SELECT id FROM member WHERE name = ? AND nickname = ?";
			pstmt = con.prepareStatement(checkNameStr);
			pstmt.setString(1, uname);
			pstmt.setString(2, unickname);
			ResultSet result = pstmt.executeQuery();

			if (result.next()) {
				msg = result.getString("id");
			} else {
				msg = "FAIL";
			}

			result.close();
			pstmt.close();
		} catch (Exception e) {
			msg = "FAIL";
			System.out.println("[Server] id찾기 실패 > " + e.toString());
		}

		return msg;
	}

	/* 패스워드 찾기 메소드 */
	String findPw(String _m1, String _m2, String _m3) {
		String uname = _m1;
		String unickname = _m2;
		String uid = _m3;
		String msg = "";

		try {
			// name과 nickname, id가 일치하는 필드가 있는지 조회한다.
			String checkNameStr = "SELECT password FROM member WHERE name = ? AND nickname = ? AND id = ?";
			pstmt = con.prepareStatement(checkNameStr);
			pstmt.setString(1, uname);
			pstmt.setString(2, unickname);
			pstmt.setString(3, uid);
			ResultSet result = pstmt.executeQuery();

			if (result.next()) {
				msg = result.getString("password");
			} else {
				msg = "FAIL";
			}

			result.close();
			pstmt.close();
		} catch (Exception e) {
			msg = "FAIL";
			System.out.println("[Server] password찾기 실패 > " + e.toString());
		}

		return msg;
	}

    // 회원가입을 수행하는 메소드. 회원가입에 성공하면 true, 실패하면 false를 반환한다.
    boolean joinCheck(String _n, String _nn, String _i, String _p, String _a, String _g, String _b, String _ph, String _e) {
        boolean flag = false; // 참거짓을 반환할 flag 변수. 초기값은 false

        // 매개변수로 받은 각 문자열들을 각 변수에 초기화한다.
        String na = _n;
        String nn = _nn;
        String id = _i;
        String pw = _p;
        String ad = _a;
        String ge = _g;
        String bi = _b;
        String ph = _ph;
        String em = _e;

        try {
            // member 테이블에 각 문자열들을 순서대로 업데이트하는 문장. 승, 패는 초기값을 숫자 0으로 한다.
            String insertStr = "INSERT INTO member VALUES('" + na + "', '" + nn + "', '" + id + "', '" + pw + "', '"
                    + ad + "', '" + ge + "', '" + bi + "', '" + ph + "', '" + em + "', 0, 0)";
            stmt.executeUpdate(insertStr);

            flag = true; // 업데이트문이 정상적으로 수행되면 flag를 true로 초기화하고 성공을 콘솔로 알린다.
            System.out.println("[Server] 회원가입 성공");
        } catch (Exception e) { // 회원가입 절차를 수행하지 못하면 flag를 false로 초기화하고 실패를 콘솔로 알린다.
            flag = false;
            System.out.println("[Server] 회원가입 실패 > " + e.toString());
        }

        return flag; // flag 반환
    }

    // 아이디나 닉네임이 중복되었는지 확인해주는 메소드. 중복 값이 존재하면 false, 존재하지 않으면 true를 반환한다.
    boolean overCheck(String _a, String _v) {
        boolean flag = false; // 참거짓을 반환할 flag 변수. 초기값은 false

        // att는 속성(아이디, 닉네임)을 구분하고, val은 확인할 값이 초기화.
        String att = _a;
        String val = _v;

        try {
            // member 테이블에 존재하는 아이디(혹은 닉네임)를 모두 찾는다.
            String selcectStr = "SELECT " + att + " FROM member";
            ResultSet result = stmt.executeQuery(selcectStr);

            int count = 0;
            while (result.next()) {
                // 조회한 아이디(혹은 닉네임)과 val을 비교.
                if (!val.equals(result.getString(att))) { // val과 같은 것이 존재하지 않으면 flag를 true로 변경한다.
                    flag = true;
                }

                else { // val과 같은 것이 존재하면 flag를 false로 변경한다.
                    flag = false;
                    break;
                }
                count++;
            }
            System.out.println("[Server] 중복 확인 성공"); // 정상적으로 수행되었을 때 성공을 콘솔로 알린다.
        } catch (Exception e) { // 정상적으로 수행하지 못하면 실패를 콘솔로 알린다.
            System.out.println("[Server] 중복 확인 실패 > " + e.toString());
        }

        return flag; // flag 반환
    }

    // 데이터베이스에 저장된 모든 회원의 정보를 조회하여 클라이언트로 전송
    Vector<Vector<Object>> getAllUser() {
        Vector<Vector<Object>> list = new Vector<Vector<Object>>();
        try {
            String viewStr = "SELECT * FROM member";
            pstmt = con.prepareStatement(viewStr);
            ResultSet result = pstmt.executeQuery();

            while (result.next()) {
                Vector<Object> row = new Vector<Object>();
                row.add(result.getString("name"));
                row.add(result.getString("nickname"));
                row.add(result.getString("id"));
                row.add(result.getString("password"));

                // 대체 문자로 변경
                String address = result.getString("address").replace(",", ".");
                row.add(address);

                row.add(result.getString("gender"));
                row.add(result.getString("birth"));
                row.add(result.getString("phone"));
                row.add(result.getString("email"));
                row.add(result.getInt("win"));
                row.add(result.getInt("lose"));

                list.add(row);
            }

            result.close();
            pstmt.close();
            System.out.println("[Server] 회원정보 조회 성공");
        } catch (Exception e) {
            System.out.println("[Server] 회원정보 조회 실패 > " + e.toString());
        }
        return list;
    }

    // 데이터베이스에 저장된 자신의 정보를 조회하는 메소드. 조회한 정보들을 String 형태로 반환한다.
    String viewInfo(String _category, String _value) {
        String msg = ""; // 반환할 문자열 변수를 "null"로 초기화.
        String category = _category; // 검색할 카테고리
        String value = _value; // 카테고리에서 검색할 값
        try {
            // member 테이블에서 카테고리에 해당하는 회원의 정보를 조회한다.
            String viewStr = "SELECT * FROM member WHERE " + category + " = ?";
            pstmt = con.prepareStatement(viewStr);
            pstmt.setString(1, value);
            ResultSet result = pstmt.executeQuery();

            while (result.next()) {
                msg = result.getString("name") + "//" + result.getString("nickname") + "//" + result.getString("id")
                        + "//" + result.getString("password") + "//" + result.getString("address") + "//"
                        + result.getString("gender") + "//" + result.getString("birth") + "//"
                        + result.getString("email") + "//" + result.getInt("win") + "//" + result.getInt("lose");
            }

            if (msg.equals("")) {
                msg = "카테고리에 해당하는 찾고자하는 회원이 없습니다.";
            }

            result.close();
            pstmt.close();
        } catch (Exception e) { // 정상적으로 수행하지 못하면 실패를 콘솔로 알린다.
            System.out.println("[Server] 회원정보 조회 실패 > " + e.toString());
        }
        return msg; // msg 반환
    }

    /* id에 해당하는 name, nickname, email 등등 내 정보를 불러올 메소드 */
	String viewMyInfo(String _id) { // 메소드 오버로딩 매개변수 개수가 다름
		String uid = _id;
		String msg = "";

		try {
			// member 테이블에서 카테고리에 해당하는 회원의 정보를 조회한다.
			String viewStr = "SELECT name, nickname, email, address FROM member WHERE id = ?";
			pstmt = con.prepareStatement(viewStr);
			pstmt.setString(1, uid);
			ResultSet result = pstmt.executeQuery();

			while (result.next()) {
				msg = result.getString("name") + "," + result.getString("nickname") + "," + result.getString("email") + "," + result.getString("address");
			}

			if (msg.equals("")) {
				msg = "회원정보 검색 실패";
			}

			result.close();
			pstmt.close();
		} catch (Exception e) { // 정상적으로 수행하지 못하면 실패를 콘솔로 알린다.
			System.out.println("[Server] 회원정보 조회 실패 > " + e.toString());
		}

		return msg;
	}

    /* 데이터 베이스에 저장된 img를 String 형태로 전송하는 메소드 */
	String viewImg(String _id) {
		String id = _id;
		String encodedImage = ""; // 인코딩이미지
		String msg = "";
		try {
			String imgviewStr = "SELECT image_data FROM image_table WHERE id = ?";
			pstmt = con.prepareStatement(imgviewStr);
			pstmt.setString(1, id);
			ResultSet result = pstmt.executeQuery();

			if (result.next()) {
				InputStream imageStream = result.getBinaryStream("image_data");
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

				int bytesRead;
				byte[] buffer = new byte[16384];

				while ((bytesRead = imageStream.read(buffer)) != -1) {
					byteArrayOutputStream.write(buffer, 0, bytesRead);
				}
				byte[] imageBytes = byteArrayOutputStream.toByteArray();
				encodedImage = Base64.getEncoder().encodeToString(imageBytes);

				msg = encodedImage;
				byteArrayOutputStream.close();
				imageStream.close();
				result.close();
				pstmt.close();
				System.out.println("[Server] 이미지가 성공적으로 추출되었습니다.");
			} else {
				msg = "이미지 조회에 실패했습니다.";
				System.out.println("이미지 조회에 실패했습니다.");
			}
		} catch (Exception e) { // 정상적으로 수행하지 못하면 실패를 콘솔로 알린다.
			System.out.println("[Server] 이미지 조회 실패 > " + e.toString());
		}
		return msg;
	}
	
	/* 데이터 베이스에 저장된 img를 String 형태로 전송하는 메소드 */ 
	String viewImg2(String _nickname) {
		String nickname = _nickname;
		String encodedImage = ""; // 인코딩이미지
		String msg = "";
		String id = "";
		try {
			String getidStr = "SELECT id FROM member WHERE nickname = ?";
			pstmt = con.prepareStatement(getidStr);
			pstmt.setString(1, nickname);
			ResultSet result = pstmt.executeQuery();
			if(result.next()) {
				id = result.getString("id");
			}
			
			String imgviewStr = "SELECT image_data FROM image_table WHERE id = ?";
			pstmt = con.prepareStatement(imgviewStr);
			pstmt.setString(1, id);
			result = pstmt.executeQuery();

			if (result.next()) {
				InputStream imageStream = result.getBinaryStream("image_data");
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

				int bytesRead;
				byte[] buffer = new byte[16384];

				while ((bytesRead = imageStream.read(buffer)) != -1) {
					byteArrayOutputStream.write(buffer, 0, bytesRead);
				}
				byte[] imageBytes = byteArrayOutputStream.toByteArray();
				encodedImage = Base64.getEncoder().encodeToString(imageBytes);

				msg = encodedImage;
				byteArrayOutputStream.close();
				imageStream.close();
				result.close();
				pstmt.close();
				System.out.println("[Server] 이미지가 성공적으로 추출되었습니다.");
			} else {
				msg = "이미지 조회에 실패했습니다.";
				System.out.println("이미지 조회에 실패했습니다.");
			}
		} catch (Exception e) { // 정상적으로 수행하지 못하면 실패를 콘솔로 알린다.
			System.out.println("[Server] 이미지 조회 실패 > " + e.toString());
		}
		return msg;
	}

    // id에 해당하는 계정 삭제
    String unRegisterId(String _id) {
        String msg = "";
        String id = _id;
        try {
            if (id.equals("admin")) {
                msg = "관리자 계정은 삭제할 수 없습니다.";
                return msg;
            } else {
                String deleteMemberStr = "DELETE FROM member WHERE id = ?";
                PreparedStatement pstmtDeleteMember = con.prepareStatement(deleteMemberStr);
                pstmtDeleteMember.setString(1, id);
                pstmtDeleteMember.executeUpdate();

                String deleteImageTableStr = "DELETE FROM image_table WHERE id = ?";
                PreparedStatement pstmtDeleteImageTable = con.prepareStatement(deleteImageTableStr);
                pstmtDeleteImageTable.setString(1, id);
                pstmtDeleteImageTable.executeUpdate();
                msg = "해당 계정이 삭제되었습니다.";
                return msg;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("[Server] 회원탈퇴 실패 > " + e.toString());
            msg = "회원 탈퇴에 실패했습니다.";
            return msg;
        }
    }

    /* 백업 테이블에 삭제된 계정을 집어넣는 메소드 */
    void backupId(String _id) {
        String id = _id;
        try {
            String insertBackupTableStr = "INSERT INTO backup_table SELECT * FROM member WHERE id = ?";
            pstmt = con.prepareStatement(insertBackupTableStr);
            pstmt.setString(1, id);
            pstmt.executeUpdate();

            String insertBackupImageTableStr = "INSERT INTO backup_image_table SELECT * FROM image_table WHERE id = ?";
            pstmt = con.prepareStatement(insertBackupImageTableStr);
            pstmt.setString(1, id);
            pstmt.executeUpdate();
            System.out.println("[Server]" + id + "계정이 임시비활성화 되었습니다.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("[Server] 백업 실패 > " + e.toString());
        }
    }

    // 회원정보 변경을 수행하는 메소드. 변경에 성공하면 true, 실패하면 false를 반환한다.
    boolean changeInfo(String _i, String _a, String _v) {
        boolean flag = false; // 참거짓을 반환할 flag 변수. 초기값은 false.

        // 매개변수로 받은 정보들을 초기화한다. att는 속성(이름, 이메일, 비밀번호) 구분용이고 val은 바꿀 값.
        String id = _i;
        String att = _a;
        String val = _v;

        try {
            // member 테이블에서 id에 해당하는 회원의 att(이름, 이메일, 비밀번호)를 val로 변경한다.
            String changeStr = "UPDATE member SET " + att + "='" + val + "' WHERE id='" + id + "'";
            stmt.executeUpdate(changeStr);

            flag = true; // 정상적으로 수행되면 flag를 true로 바꾸고 성공을 콘솔로 알린다.
            System.out.println("[Server] 회원정보 변경 성공");
        } catch (Exception e) { // 정상적으로 수행하지 못하면 flag를 false로 바꾸고 실패를 콘솔로 알린다.
            flag = false;
            System.out.println("[Server] 회원정보 변경 실패 > " + e.toString());
        }

        return flag; // flag 반환
    }

    // 전체 회원의 전적을 조회하는 메소드. 모든 회원의 전적을 String 형태로 반환한다.
    String viewRank() {
        String msg = ""; // 전적을 받을 문자열. 초기값은 ""로 한다.

        try {
            // member 테이블의 닉네임, 승, 패를 모두 조회한다.
            String viewStr = "SELECT nickname, win, lose FROM member";
            ResultSet result = stmt.executeQuery(viewStr);

            int count = 0;
            while (result.next()) {
                // 기존의 msg에 "닉네임 : n승 n패@" 형태의 문자열을 계속해서 추가한다.
                msg = msg + result.getString("nickname") + " : " + result.getInt("win") + "승 " + result.getInt("lose")
                        + "패@";
                count++;
            }
            System.out.println("[Server] 전적 조회 성공"); // 정상적으로 수행되면 성공을 콘솔로 알린다.
        } catch (Exception e) { // 정상적으로 수행하지 못하면 실패를 콘솔로 알린다.
            System.out.println("[Server] 전적 조회 실패 > " + e.toString());
        }

        return msg; // msg 반환
    }

    // 한 명의 회원의 전적을 조회하는 메소드. 해당 회원의 전적을 String 형태로 반환한다.
    String searchRank(String _nn) {
        String msg = "null"; // 전적을 받을 문자열. 초기값은 "null"로 한다.

        // 매개변수로 받은 닉네임을 초기화한다.
        String nick = _nn;

        try {
            // member 테이블에서 nick이라는 닉네임을 가진 회원의 승, 패를 조회한다.
            String searchStr = "SELECT win, lose FROM member WHERE nickname='" + nick + "'";
            ResultSet result = stmt.executeQuery(searchStr);

            int count = 0;
            while (result.next()) {
                // msg에 "닉네임 : n승 n패" 형태의 문자열을 초기화한다.
                msg = nick + " : " + result.getInt("win") + "승 " + result.getInt("lose") + "패";
                count++;
            }
            System.out.println("[Server] 전적 조회 성공"); // 정상적으로 수행되면 성공을 콘솔로 알린다.
        } catch (Exception e) { // 정상적으로 수행하지 못하면 실패를 콘솔로 알린다.
            System.out.println("[Server] 전적 조회 실패 > " + e.toString());
        }

        return msg; // msg 반환
    }

    // 게임 승리 시 전적을 업데이트하는 메소드. 조회 및 업데이트에 성공하면 true, 실패하면 false를 반환한다.
    boolean winRecord(String _nn) {
        boolean flag = false; // 참거짓을 반환할 flag 변수. 초기값은 false.

        // 매개변수로 받은 닉네임과 조회한 승리 횟수를 저장할 변수. num의 초기값은 0.
        String nick = _nn;
        int num = 0;

        try {
            // member 테이블에서 nick이라는 닉네임을 가진 회원의 승리 횟수를 조회한다.
            String searchStr = "SELECT win FROM member WHERE nickname='" + nick + "'";
            ResultSet result = stmt.executeQuery(searchStr);

            int count = 0;
            while (result.next()) {
                // num에 조회한 승리 횟수를 초기화.
                num = result.getInt("win");
                count++;
            }
            num++; // 승리 횟수를 올림

            // member 테이블에서 nick이라는 닉네임을 가진 회원의 승리 횟수를 num으로 업데이트한다.
            String changeStr = "UPDATE member SET win=" + num + " WHERE nickname='" + nick + "'";
            stmt.executeUpdate(changeStr);
            flag = true; // 조회 및 업데이트 성공 시 flag를 true로 바꾸고 성공을 콘솔로 알린다.
            System.out.println("[Server] 전적 업데이트 성공");
        } catch (Exception e) { // 조회 및 업데이트 실패 시 flag를 false로 바꾸고 실패를 콘솔로 알린다.
            flag = false;
            System.out.println("[Server] 전적 업데이트 실패 > " + e.toString());
        }

        return flag; // flag 반환
    }

    // 게임 패배 시 전적을 업데이트하는 메소드. 조회 및 업데이트에 성공하면 true, 실패하면 false를 반환한다.
    boolean loseRecord(String _nn) {
        boolean flag = false; // 참거짓을 반환할 flag 변수. 초기값은 false.

        // 매개변수로 받은 닉네임과 조회한 패배 횟수를 저장할 변수. num의 초기값은 0.
        String nick = _nn;
        int num = 0;

        try {
            // member 테이블에서 nick이라는 닉네임을 가진 회원의 패배 횟수를 조회한다.
            String searchStr = "SELECT lose FROM member WHERE nickname='" + nick + "'";
            ResultSet result = stmt.executeQuery(searchStr);

            int count = 0;
            while (result.next()) {
                // num에 조회한 패배 횟수를 초기화.
                num = result.getInt("lose");
                count++;
            }
            num++; // 패배 횟수를 올림

            // member 테이블에서 nick이라는 닉네임을 가진 회원의 승리 횟수를 num으로 업데이트한다.
            String changeStr = "UPDATE member SET lose=" + num + " WHERE nickname='" + nick + "'";
            stmt.executeUpdate(changeStr);
            flag = true; // 조회 및 업데이트 성공 시 flag를 true로 바꾸고 성공을 콘솔로 알린다.
            System.out.println("[Server] 전적 업데이트 성공");
        } catch (Exception e) { // 조회 및 업데이트 실패 시 flag를 false로 바꾸고 실패를 콘솔로 알린다.
            flag = false;
            System.out.println("[Server] Error: > " + e.toString());
        }

        return flag; // flag 반환
    }

    // 이미지를 디코딩해서 BLOB 필드에 저장하는 메소드
 	void imageToDatabase(String id, String encodedImage) {
 		try {
 			// Base64 디코딩
 			byte[] decodedBytes = Base64.getDecoder().decode(encodedImage);

 			// BLOB 필드에 저장
 			String saveImgStr = "INSERT INTO image_table (id, image_data) VALUES (?, ?)";
 			PreparedStatement pstmt = con.prepareStatement(saveImgStr);
 			pstmt.setString(1, id);
 			pstmt.setBytes(2, decodedBytes);
 			pstmt.executeUpdate();

 		} catch (SQLException e) {
 			e.printStackTrace();
 		}
 	}

    // 데이터베이스에 저장된 모든 회원의 id를 조회하여 클라이언트로 전송
    ArrayList<String> getAllUserId() {
        ArrayList<String> user = new ArrayList<>();

        try {
            String changeStr = "SELECT id FROM member";
            ResultSet rs = stmt.executeQuery(changeStr);

            while (rs.next()) {
                user.add(rs.getString("id"));
            }

            System.out.println("[Server] 회원조회 검색 성공");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }
    
    ArrayList<String> searchUser(String su) {
    	ArrayList<String> searchUsers = new ArrayList();
    	
    	try {
    		String searchUserStr = "SELECT id FROM member WHERE id LIKE '" + su + "%'";
    		ResultSet rs = stmt.executeQuery(searchUserStr);
    		
    		while(rs.next()) {
    			searchUsers.add(rs.getString("id"));
    		}
    		
    		System.out.println("[Client] 유저 검색 성공");
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    	return searchUsers;
    }
}
