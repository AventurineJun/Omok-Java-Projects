import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
//import java.awt.List;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTMLDocument;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;  // java.util 패키지의 List를 명시적으로 import

public class GameFrame extends JFrame {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    
    boolean isSpectator = false; // 관전자 여부
    boolean gameEnd = false; // 게임 종료 여부
    boolean start = false;
    
    JTextPane chatTextPane = new JTextPane();
    JTextField chatInputField;

    private String nickname;

    private ImageIcon icon;

    // 이모티콘을 선택할 때 사용할 다이얼로그
    class EmojiDialog extends JDialog {
        EmojiDialog(JFrame parent) {
            super(parent, "이모티콘 선택하기", true);
            setLayout(new GridLayout(2, 4));

            JButton emojiButton1 = new JButton("😀");//😊\"
            JButton emojiButton2 = new JButton("🎉");
            JButton emojiButton3 = new JButton("😂");
            JButton emojiButton4 = new JButton("😍");
            JButton emojiButton5 = new JButton("🚀");
            JButton emojiButton6 = new JButton("🌈");
            JButton emojiButton7 = new JButton("🤔");
            JButton emojiButton8 = new JButton("😎");

            // 각 버튼에 대한 이벤트 리스너 추가
            emojiButton1.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    insertEmoji("😀");
                    dispose();
                }
            });
            emojiButton2.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    insertEmoji("🎉");
                    dispose();
                }
            });
            emojiButton3.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    insertEmoji("😂");
                    dispose();
                }
            });
            emojiButton4.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    insertEmoji("😍");
                    dispose();
                }
            });
            emojiButton5.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    insertEmoji("🚀");
                    dispose();
                }
            });
            emojiButton6.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    insertEmoji("🌈");
                    dispose();
                }
            });
            emojiButton7.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    insertEmoji("🤔");
                    dispose();
                }
            });
            emojiButton8.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    insertEmoji("😎");
                    dispose();
                }
            });
            add(emojiButton1);
            add(emojiButton2);
            add(emojiButton3);
            add(emojiButton4);
            add(emojiButton5);
            add(emojiButton6);
            add(emojiButton7);
            add(emojiButton8);

            setSize(300, 150);
            setLocationRelativeTo(parent);
        }
    }
    private void insertEmoji(String emoji) {
        chatInputField.setText(chatInputField.getText() + " " + emoji);
    }


    // 관전자를 위한 리스트
    DefaultListModel<String> spectatorListModel = new DefaultListModel<>();
    JList<String> spectatorList = new JList<>(spectatorListModel);

    /* Panel */
    JPanel basePanel = new JPanel(new BorderLayout());
    JPanel centerPanel = new JPanel();
    JPanel eastPanel = new JPanel();
    
    /* List */
    JList<String> userList = new JList<String>();

    /* Label */
    JLabel pL1 = new JLabel("플레이어1");
	JLabel pL2 = new JLabel("플레이어2");
    JLabel la1 = new JLabel();
    JLabel la2 = new JLabel();
    JLabel la3 = new JLabel();
    JLabel userListL = new JLabel("플레이어 목록");
    JLabel spectatorListL = new JLabel("관전자 목록");
    JLabel enableL = new JLabel();
    JLabel pimgL = new JLabel();
    JLabel pimgL2 = new JLabel();
    
    Border border = BorderFactory.createLineBorder(Color.BLACK, 1);

    /* Button */
    JButton searchBtn = new JButton("전적검색");
    JButton loseBtn = new JButton("기권하기");
    JButton exitBtn = new JButton("나가기");
    JButton sendButton = new JButton("보내기");

    JButton emojiButton = new JButton("이모티콘 선택");


    String selUser; // 선택된 사용자

    String dc = ""; // 돌 색깔
    int col; // 돌 색깔

    int omok[][] = new int[20][20]; // 오목 위치 배열
    boolean enable = false; // 돌을 둘 수 있는지 여부
    String[] bd = new String[0];
    String[] wd = new String[0];
    
    Client c = null;

    final String searchTag = "SEARCH"; // 전적 조회 기능 태그
    final String rexitTag = "REXIT"; // 방 퇴장 기능 태그
    final String omokBlackMsgTag = "OMOKBLACKXY"; // 오목 검은 돌 위치 태그
    final String omokWhiteMsgTag = "OMOKWHITEXY"; // 오목 하얀 돌 위치 태그
    final String spectatorXYTag = "SPECTATORXY"; // 관전자 돌 위치 태그
    final String omokTag = "OMOK"; // 오목 기능 태그
    final String blackTag = "BLACK"; // 검정색 돌 태그
    final String whiteTag = "WHITE"; // 흰색 돌 태그
    final String winTag = "WIN"; // 승리 태그
    final String loseTag = "LOSE"; // 패배 태그
    final String chatTag = "CHAT"; // 채팅태그

    GameFrame(Client _c) {
        c = _c;

        icon = new ImageIcon("/Users/jeongjun-yeong/Downloads/background.jpg");
    
        // JTextPane 초기화      
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
        
        JScrollPane chatScrollPane1 = new JScrollPane(chatTextPane);
        chatScrollPane1.setPreferredSize(new Dimension(235, 100));

        // 스크롤을 항상 아래로 이동시키는 코드
        chatScrollPane1.getVerticalScrollBar().setValue(chatScrollPane1.getVerticalScrollBar().getMaximum());
        
        chatInputField = new JTextField() {
            public void paintComponent(Graphics g) {
                g.drawImage(icon.getImage(), 0, 0, null);
                setOpaque(false);
                super.paintComponent(g);
            }
        };
        chatInputField.setBackground(Color.cyan);
        
        // JTextField 초기화
        chatInputField = new JTextField();
        chatInputField.setPreferredSize(new Dimension(235, 30));
        
        // 관전자 리스트 크기 및 위치 설정
        spectatorList.setPreferredSize(new Dimension(120, 50));
        spectatorListL.setPreferredSize(new Dimension(105, 20));
        spectatorListL.setHorizontalAlignment(JLabel.CENTER);
        
        JScrollPane spectatorScrollPanel = new JScrollPane(spectatorList);
        spectatorScrollPanel.setPreferredSize(new Dimension(235, 50));

        /* List 크기 작업 */
        userList.setPreferredSize(new Dimension(120, 50));
        
        /* Label 크기 작업 */
        pL1.setPreferredSize(new Dimension(45, 20));
		pL1.setHorizontalAlignment(JLabel.LEFT);
		pL2.setPreferredSize(new Dimension(105, 20));
		pL2.setHorizontalAlignment(JLabel.RIGHT);
		userListL.setPreferredSize(new Dimension(105, 20));
		userListL.setHorizontalAlignment(JLabel.CENTER);
        la1.setPreferredSize(new Dimension(250, 30));
        pimgL.setPreferredSize(new Dimension(120, 150));
        pimgL.setBorder(border);
        pimgL2.setPreferredSize(new Dimension(120, 150));
        pimgL2.setBorder(border);
        la2.setPreferredSize(new Dimension(155, 20));
        enableL.setPreferredSize(new Dimension(235, 45));
        enableL.setHorizontalAlignment(JLabel.CENTER);
        enableL.setForeground(Color.RED);
        la3.setPreferredSize(new Dimension(250, 25));
        
        /* Button 크기 작업 */
        searchBtn.setPreferredSize(new Dimension(235, 30));
        loseBtn.setPreferredSize(new Dimension(235, 30));
        exitBtn.setPreferredSize(new Dimension(235, 30));
        sendButton.setPreferredSize(new Dimension(235, 30));

        /* Panel 추가 작업 */
        setContentPane(basePanel); // panel을 기본 컨테이너로 설정

        centerPanel.setPreferredSize(new Dimension(625, 652));
        centerPanel.setLayout(new FlowLayout());

        eastPanel.setPreferredSize(new Dimension(250, 852));
        eastPanel.setLayout(new FlowLayout());

        centerPanel.setBackground(new Color(206, 167, 61));
 
        basePanel.add(centerPanel, BorderLayout.CENTER);
        basePanel.add(eastPanel, BorderLayout.EAST);

        eastPanel.add(pL1);
		eastPanel.add(pL2);
        eastPanel.add(pimgL);
        eastPanel.add(pimgL2);
        eastPanel.add(userListL);
        eastPanel.add(spectatorListL);
        eastPanel.add(userList);       
        eastPanel.add(spectatorList);
        eastPanel.add(la2);
        eastPanel.add(searchBtn);
        eastPanel.add(enableL);
        eastPanel.add(la3);
        eastPanel.add(chatScrollPane1, BorderLayout.CENTER);
        eastPanel.add(chatInputField, BorderLayout.SOUTH);
        eastPanel.add(sendButton, BorderLayout.SOUTH);
        eastPanel.add(emojiButton); // 이모티콘 선택 버튼 추가
        eastPanel.add(loseBtn);
        eastPanel.add(exitBtn);
        
        /* Button 이벤트 리스너 추가 */
        ButtonListener bl = new ButtonListener();
        loseBtn.addActionListener(bl);
        searchBtn.addActionListener(bl);
        exitBtn.addActionListener(bl);
        chatInputField.addActionListener(new ChatInputFieldListener());
        sendButton.addActionListener(bl);

        // 이모티콘 버튼에 대한 이벤트 리스너 추가
        emojiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EmojiDialog emojiDialog = new EmojiDialog(GameFrame.this);
                emojiDialog.setVisible(true);
            }
        });

        /* Mouse 이벤트 리스너 추가 */
        DolAction da = new DolAction();
        centerPanel.addMouseListener(da);

        /* Mouse 이벤트 추가 */
        userList.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!userList.isSelectionEmpty()) {
                    String[] m = userList.getSelectedValue().split(" : ");
                    selUser = m[0];
                }
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }

        });
        
        /* Mouse 이벤트 추가 */
		spectatorList.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (!spectatorList.isSelectionEmpty()) {
					String[] m = spectatorList.getSelectedValue().split(" : ");
					selUser = m[0];
				}
			}

			public void mousePressed(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {
			}

		});

        setSize(985, 752);
        setResizable(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
    }

    @Override
    public void paint(Graphics g) { // panel에 그리기 작업
        super.paintComponents(g);
        g.setColor(Color.BLACK);

        for (int i = 1; i <= 20; i++) {
        	 g.drawLine(35, i * 35 + 20, 35 * 20, i * 35 + 20); // 가로 줄 그리기
        	 g.drawLine(i * 35, 55, i * 35, 35 * 20 + 20); // 세로 줄 그리기
        }

        drawdol(g); // 돌 그리기
        
        if (isSpectator) {
        	c.sendMsg(spectatorXYTag + "//");
            drawdol(g, true); // 관전자인 경우에만 돌 그리기
        }
    }

    void drawdol(Graphics g) { // 돌 그리기 작업
    	for (int i = 0; i < 20; i++) {
    		for (int j = 0; j < 20; j++) {
    			if (omok[j][i] == 1) { // 1일 때 검정 돌
    				g.setColor(Color.BLACK);
    				g.fillOval((i + 1) * 35 - 12, (j) * 35 + 37, 30, 30);
	            } else if (omok[j][i] == 2) { // 2일 때 흰 돌
	            	g.setColor(Color.WHITE);
	                g.fillOval((i + 1) * 35 - 12, (j) * 35 + 37, 30, 30);
	            }
    		}
    	}
    }
    
    void drawdol(Graphics g, boolean isSpectator) {
    	int[] blackDol = new int[bd.length * 2 + 2];
    	int[] whiteDol = new int[wd.length * 2 + 2];
    	
    	for (int i = 0; i < bd.length; i++) {
    	    System.out.println("bd[" + i + "]: " + bd[i]);
    	}
    	
    	// bd 배열의 값들을 blackDol 배열에 복사
    	for (int i = 0; i < bd.length; i++) {
    	    String[] parts = bd[i].split("@@");
    	    blackDol[i * 2] = Integer.parseInt(parts[0]); // 첫 번째 값
    	    blackDol[i * 2 + 1] = Integer.parseInt(parts[1]); // 두 번째 값
    	}

    	// wd 배열의 값들을 whiteDol 배열에 복사
    	for (int i = 0; i < wd.length; i++) {
    	    String[] parts = wd[i].split("@@");
    	    whiteDol[i * 2] = Integer.parseInt(parts[0]); // 첫 번째 값
    	    whiteDol[i * 2 + 1] = Integer.parseInt(parts[1]); // 두 번째 값
    	}
    	
    	for (int i = 0; i < blackDol.length; i += 2) {
            int x = blackDol[i];
            int y = blackDol[i + 1];
            g.setColor(Color.BLACK);
            g.fillOval((x + 1) * 35 - 12, y * 35 + 37, 25, 25);
        }

        for (int i = 0; i < whiteDol.length; i += 2) {
            int x = whiteDol[i];
            int y = whiteDol[i + 1];
            g.setColor(Color.WHITE);
            g.fillOval((x + 1) * 35 - 12, y * 35 + 37, 25, 25);
        }
    }

    void remove() { // 돌 초기화 작업
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                omok[i][j] = 0;
            }
        }
        repaint();
    }

    /* Button 이벤트 리스너 */
    class ButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton b = (JButton) e.getSource();

            if (b.getText().equals("보내기") || e.getSource() == chatInputField) {
            	System.out.println("nickname : " + nickname);
                String msg = chatTag + "//" + nickname + ":" + chatInputField.getText() + "\n";
                c.sendMsg(msg);
                chatInputField.setText("");
            }
            
            /* 전적검색 버튼 이벤트 */
            if (b.getText().equals("전적검색")) {
                if (selUser != null) { // selUser가 null이 아니면 서버에 "태그//닉네임" 형태의 메시지를 전송
                    c.sendMsg(searchTag + "//" + selUser);
                } else { // selUser가 null이면 전적검색 시도 실패
                    JOptionPane.showMessageDialog(null, "검색할 닉네임을 선택해주세요", "검색 실패", JOptionPane.ERROR_MESSAGE);
                }
            }

            /* 기권하기 버튼 이벤트 */
            else if (b.getText().equals("기권하기")) {
                if (!isSpectator) { // 기권할 수 있는지 확인
                	gameEnd = true;
                	remove();
                    c.sendMsg(loseTag + "//"); // 서버에 패배 태그 전송
                    dispose(); // 인터페이스 닫음
                    c.mf.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, "게임 플레이어만 기권할 수 있습니다", "기권 실패", JOptionPane.ERROR_MESSAGE);
                }
            }
            
            /* 나가기 버튼 이벤트 */
            else if (b.getText().equals("나가기")) {
            	if(!isSpectator) {
            		c.sendMsg(loseTag + "//"); // 서버에 패배 태그 전송
            		gameEnd = true;
                    dispose(); // 인터페이스 닫음
                    c.mf.setVisible(true);
            	} else if(isSpectator) {
            		remove();
            		isSpectator = false;
            		c.sendMsg(rexitTag + "//" + "suser");
            		dispose();
            		c.mf.setVisible(true);
            	}
            }
        }
    }
    
    // chatInputField에 대한 ActionListener 추가
    class ChatInputFieldListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String msg = chatTag + "//" + nickname + ":" + chatInputField.getText() + "\n";
            c.sendMsg(msg);
            chatInputField.setText("");
        }
    }

    /* Mouse 이벤트 리스너 : 돌 올릴 위치 선정 */
    class DolAction implements MouseListener {
        @Override
        public void mousePressed(MouseEvent e) {

        	// 관전자인 경우 둘 수 없음
            if (isSpectator) {
                System.out.println("둘 수 없습니다. 관전 중입니다.");
                return;
            }

            if (!enable) {
                System.out.println("놓을 수 없습니다. enable : " + enable);
                return; // 누를 수 없으면 return
            }

            // 각 좌표 계산
            int x = (int) (Math.round(e.getX() / (double) 35) - 1);
            int y = (int) (Math.round(e.getY() / (double) 35) - 1);

            if (x < 0 || x > 19 || y < 0 || y > 19)
                return; // 둘 수 없는 위치면 return
            if (omok[y][x] == 1 || omok[y][x] == 2)
                return; // 다른 돌이 있으면 return

            System.out.println("[Client] 돌을 (" + x + ", " + y + ")에 두었습니다"); // 돌을 둔 위치를 알림

            if (dc.equals(blackTag)) { // 검정색 태그면 1
                omok[y][x] = 1;
                col = 1;
                c.sendMsg(omokBlackMsgTag + "//" + x + "//" + y + "//");
            } else { // 흰색 태그면 2
                omok[y][x] = 2;
                col = 2;
                c.sendMsg(omokWhiteMsgTag + "//" + x + "//" + y + "//");
            }
            
            // 만약 플레이어가 돌을 두었다면 메시지를 서버에 전송
            if (!isSpectator) {
            	String msg = ("omokMsgTag//" + x + "@@" + y);
            	c.sendMsg(msg);
                c.sendMsg(omokTag + "//" + x + "//" + y + "//" + dc);
            }

            repaint();

            if(!isSpectator) {
            	if (check(new Point(x, y), col)) { // 이겼는지 확인. true면 서버에 승리 태그 전송
	                c.sendMsg(winTag + "//");
	                JOptionPane.showMessageDialog(null, "게임에 승리하였습니다", "승리", JOptionPane.INFORMATION_MESSAGE);
	                gameEnd = true;
	                remove();
	                dispose(); // 인터페이스 닫음
	                c.mf.setVisible(true);
            	}
            } else if (isSpectator) {
            	gameEnd = true;
            	remove();
            	dispose();
            	c.mf.setVisible(true);
            }

            enable = false; // 돌을 두면 false로 바꿈
            enableL.setText("상대가 두기를 기다리는 중..."); // 본인 차례인지 아닌지 알려줌
        }

        public void mouseClicked(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }
    }

    // 승리 여부를 확인하는 메소드. 승리 시 true, 승리가 아니면 false를 반환
    boolean check(Point p, int c) {
        /* 돌을 올린 위치의 가로, 세로, 대각선에 같은 색의 돌이 연달아 4개가 있으면 true를 반환 */
        if (count(p, 1, 0, c) + count(p, -1, 0, c) == 4) { // 가로
            return true;
        }

        if (count(p, 0, 1, c) + count(p, 0, -1, c) == 4) { // 세로
            return true;
        }

        if (count(p, -1, -1, c) + count(p, 1, 1, c) == 4) { // 오른쪽 대각선
            return true;
        }

        if (count(p, 1, -1, c) + count(p, -1, 1, c) == 4) { // 왼쪽대각선
            return true;
        }

        return false;
    }

    // 특정 위치에 같은 색의 돌이 있는지 확인하는 메소드.
    int count(Point p, int _x, int _y, int c) {
        int i = 0;
        // omok[p.y+(i+1)*_y][p.x+(i+1)*_x]==c가 true면 i가 무한대로 증가한다.
        for (i = 0; omok[p.y + (i + 1) * _y][p.x + (i + 1) * _x] == c; i++)
            ;
        return i;
    }
    
    // client.cf.appendMsg 메소드 내에서도 SwingUtilities.invokeLater 사용
    public void appendMsg(String msg, String owner) {
        SwingUtilities.invokeLater(() -> {       	
            if (owner.equals(c.gf.getNickname())) {
            	appendRightAligned("나", Color.BLACK);
            	appendRightAligned(msg + "\n", Color.BLUE);
            } else {
            	appendLeftAligned(owner, Color.BLACK);
                appendLeftAligned(msg + "\n", Color.RED);
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
    private void appendRightAligned(String text, Color textColor) {
        SwingUtilities.invokeLater(() -> {
            HTMLDocument doc = (HTMLDocument) chatTextPane.getStyledDocument();
            Element root = doc.getDefaultRootElement();
            
            // 스타일 추가
            SimpleAttributeSet style = new SimpleAttributeSet();
            StyleConstants.setForeground(style, textColor);
            try {
            	// HTML 태그를 사용하여 스타일 적용
                doc.insertBeforeEnd(root, "<div style='text-align: right; color: #" + Integer.toHexString(textColor.getRGB()).substring(2) + "'>" + text + "</div>");
            } catch (BadLocationException | IOException e) {
                e.printStackTrace();
            }
        });
    }

    // 왼쪽 정렬 메소드
    private void appendLeftAligned(String text, Color textColor) {
        SwingUtilities.invokeLater(() -> {
            HTMLDocument doc = (HTMLDocument) chatTextPane.getStyledDocument();
            Element root = doc.getDefaultRootElement();
            
            // 스타일 추가
            SimpleAttributeSet style = new SimpleAttributeSet();
            StyleConstants.setForeground(style, textColor);
            try {
            	// HTML 태그를 사용하여 스타일 적용
                doc.insertBeforeEnd(root, "<div style='text-align: left; color: #" + Integer.toHexString(textColor.getRGB()).substring(2) + "'>" + text + "</div>");
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