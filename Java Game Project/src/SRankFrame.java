import javax.swing.*;
import java.awt.*;

//검색한 전적을 출력하는 인터페이스
public class SRankFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    /* Panel */
    JPanel panel = new JPanel();

    /* Label 및 Font */
    JLabel l = new JLabel();
    Font f = new Font("Dialog", Font.PLAIN, 15);

    Client c = null;

    SRankFrame(Client _c) {
        c = _c;
        setTitle("전적검색");

        // 폰트 작업
        l.setFont(f);
        l.setHorizontalAlignment(JLabel.CENTER);

        setContentPane(panel); // panel을 기본 컨테이너로 설정
        panel.add(l);

        setSize(250, 70);
        setLocationRelativeTo(null);
        setResizable(false);
    }
}
