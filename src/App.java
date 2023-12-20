import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class App extends JFrame implements ActionListener, ChangeListener {


  static int toplamKazanc = 0;
  static volatile Masa[] masa;
  static MusteriGenerator musteriGenerator;

  static ArrayList<Musteri> musteriArrayList;
  static ArrayList<Asci> asciArrayList;
  static ArrayList<Garson> garsonArrayList;
  static ArrayList<Kasiyer> kasiyerArrayList;
  static ArrayList<Musteri> siparisArrayList;
  static ArrayList<Musteri> kasaArrayList;

  static Integer masaSayisi = 100;
  static Integer garsonSayisi = 3;
  static Integer asciSayisi = 2;
  static Integer kasiyerSayisi = 1;

  static Integer masaMaliyeti = 1;
  static Integer garsonMaliyeti = 1;
  static Integer asciMaliyeti = 1;
  static Integer kasiyerMaliyeti = 1;
  static Integer musteriKazanci = 1;

  boolean oyunEkraniBool = false;
  static boolean oyunDevamBool = false;
  static boolean oyunIlkCalistirma = true;

  public static final ReentrantLock musteriLock = new ReentrantLock();
  public static final ReentrantLock masaLock = new ReentrantLock();
  public static final ReentrantLock siparisLock = new ReentrantLock();
  public static final ReentrantLock kasaLock = new ReentrantLock();

  static JPanel panel;
  static Font font = new Font("TimesRoman", Font.BOLD, 24);
  Color primary = new Color(147, 191, 207);


  JButton anaEkranBaslatButonu = StandartButon("Baslat", 450, 275);
  JButton anaEkranAyarlarButonu = StandartButon("Ayarlar", 450, 450);
  JButton ayarlarGeriButonu = StandartButon("Geri", 450, 670);

  JButton bekleyenMusteriButonu = StandartButon("Musteri", 10, 30);
  JButton garsonlarButonu = StandartButon("Garsonlar", 168, 30);
  JButton ascilarButonu = StandartButon("Ascilar", 326, 30);
  JButton kasiyerlerButonu = StandartButon("Kasiyer", 484, 30);
  JButton oyunDevamButonu = StandartButon("Devam", 800, 30);
  JButton ilerletButonu = StandartButon("Ilerlet", 965, 30);
  JButton oyunEkraniGeriButonu = StandartButon("Geri", 1080, 30);

  JSlider oyunHiziSlider = new JSlider(1, 4, 2);
  JLabel oyunuHiziLabel = new JLabel("Oyun hızı: 2");
  JLabel oyunBilgileri = new JLabel("<html>Kazanc: "+ toplamKazanc+"<br><br>Bekleyen Musteriler: "+ "?/?" +"<br><br>Garsonlar: ?/"+garsonSayisi+"<br><br>Ascilar: ?/"+asciSayisi+
          "<br><br>Kasiyerler: ?/"+kasiyerSayisi+"</html>");

  JTable ayarlarTable;

  JButton StandartButon(String baslik, int x, int y){
    JButton btn = new JButton(baslik);
    btn.setSize(300, 60);
    btn.setBorderPainted(false);
    btn.setBackground(primary);
    btn.setFont(font);
    btn.setFocusable(false);
    btn.addActionListener(this);
    btn.setLocation(x, y);
    return btn;
  }

  public static int[] EnYakinIkiSayi(int masaSayisi) {
    int closestDifference = Integer.MAX_VALUE;
    int[] result = new int[]{0, 0};

    for (int i = 1; i <= masaSayisi; i++) {
      for (int j = (i > 1) ? i - 1 : i; j <= masaSayisi; j++) {
        int multiplication = i * j;
        int difference = Math.abs(j - i);

        if (multiplication > masaSayisi && difference < closestDifference) {
          closestDifference = difference;
          result[0] = i;
          result[1] = j;
        }
      }
    }
    return result;
  }

  private class MusteriGenerator extends Thread{

    Random random = new Random();

    @Override
    public void run(){
      try{
        while (!isInterrupted()){
          App.musteriLock.lock();
          try {
          musteriArrayList.add(new Musteri());
          }
          finally {
            App.musteriLock.unlock();
          }
          Thread.sleep(200);
          //Thread.sleep((random.nextInt(5)+1)*1000);
        }
      }
      catch (InterruptedException e){
        throw new RuntimeException(e);
      }
    }

  }

  //Sadece ikinci col'un editable olduğu table modeli
  private static class CustomTableModel extends DefaultTableModel {
    public CustomTableModel(Object[][] data, Object[] columnNames) {
      super(data, columnNames);
    }
    @Override
    public boolean isCellEditable(int row, int column) {
      return column != 0;
    }
  }

  static class CustomCellEditor extends DefaultCellEditor {
    private final DefaultTableModel model;
    private int editingRow;
    private int editingColumn;

    public CustomCellEditor(DefaultTableModel model) {
      super(new JTextField());
      this.model = model;
      JTextField textField = (JTextField) getComponent();
      textField.setFont(font);
      textField.setHorizontalAlignment(SwingConstants.CENTER);
      textField.addActionListener(e -> stopCellEditing());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
      editingRow = row;
      editingColumn = column;
      return super.getTableCellEditorComponent(table, value, isSelected, row, column);
    }

    @Override
    public boolean stopCellEditing() {
      Object editedValue = super.getCellEditorValue();
      if(editedValue.toString().matches("\\d+")){
        int num = Integer.parseInt(editedValue.toString());
        if (num >= 0) {
          if (editingRow != -1 && editingColumn != -1) {
            String setting = (String) model.getValueAt(editingRow, 0);
            switch (setting) {
              case "Masa sayisi" -> masaSayisi = num;
              case "Garson sayisi" -> garsonSayisi = num;
              case "Asci sayisi" -> asciSayisi = num;
              case "Kasiyer sayisi" -> kasiyerSayisi = num;
              case "Masa maliyeti" -> masaMaliyeti = num;
              case "Garson maliyeti" -> garsonMaliyeti = num;
              case "Asci maliyeti" -> asciMaliyeti = num;
              case "Kasiyer maliyeti" -> kasiyerMaliyeti = num;
              case "Musteri kazanci" -> musteriKazanci = num;
              default -> {
              }
            }
          }
        }
      }else {
        JOptionPane.showMessageDialog(null, "Pozitif bir rakam girin", "Error", JOptionPane.ERROR_MESSAGE);
        return false;
      } return super.stopCellEditing();
    }
  }

  void AnaEkran(){
    panel.removeAll();
    panel.add(anaEkranBaslatButonu);
    panel.add(anaEkranAyarlarButonu);

    panel.repaint();
  }

  void AyarlarEkrani(){
    panel.removeAll();

    SwingUtilities.invokeLater(() -> {
      Object[][] data = {
              {"Masa sayisi", masaSayisi},
              {"Garson sayisi", garsonSayisi},
              {"Asci sayisi", asciSayisi},
              {"Kasiyer sayisi", kasiyerSayisi},
              {"Masa maliyeti", masaMaliyeti},
              {"Garson maliyeti", garsonMaliyeti},
              {"Asci maliyeti", asciMaliyeti},
              {"Kasiyer maliyeti", kasiyerMaliyeti},
              {"Musteri kazanci", musteriKazanci}
      };
      Object[] columnNames = {"Ayarlar", "Deger"};

      CustomTableModel model = new CustomTableModel(data, columnNames);
      JTable ayarlarTable = new JTable(model);
      ayarlarTable.setFont(font);
      ayarlarTable.getColumnModel().getColumn(1).setCellEditor(new CustomCellEditor(model));

      DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
      cellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
      for (int i = 0; i < ayarlarTable.getColumnCount(); i++) {
        ayarlarTable.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
      }
      ayarlarTable.setRowHeight(30);
      ayarlarTable.getTableHeader().setReorderingAllowed(false);
      ayarlarTable.getTableHeader().setResizingAllowed(false);

      JScrollPane scrollPane = new JScrollPane(ayarlarTable);
      scrollPane.setBounds(150, 40, 900, 600);
      panel.add(scrollPane);
      panel.add(ayarlarGeriButonu);
      panel.repaint();
    });
  }

  void OyunuBaslat(){
    panel.removeAll();
    toplamKazanc = 0;

    Asci.count = 1;
    Garson.count = 1;
    Kasiyer.count = 1;
    Musteri.count = 1;

    masa = new Masa[masaSayisi];
    for (int i=0; i<masaSayisi; i++) masa[i] = new Masa();

    siparisArrayList = new ArrayList<>();
    kasaArrayList = new ArrayList<>();

    asciArrayList = new ArrayList<>();
    for(int i=0; i<asciSayisi; i++) asciArrayList.add(new Asci());

    garsonArrayList = new ArrayList<>();
    for(int i=0; i<garsonSayisi; i++) garsonArrayList.add(new Garson());

    kasiyerArrayList = new ArrayList<>();
    for (int i=0; i<kasiyerSayisi; i++) kasiyerArrayList.add(new Kasiyer());

    musteriArrayList = new ArrayList<>();

    oyunEkraniBool = true;

    panel.add(bekleyenMusteriButonu);
    panel.add(garsonlarButonu);
    panel.add(ascilarButonu);
    panel.add(kasiyerlerButonu);
    panel.add(oyunDevamButonu);
    panel.add(oyunEkraniGeriButonu);
    panel.add(ilerletButonu);
    panel.add(oyunBilgileri);

    panel.add(oyunuHiziLabel);
    panel.add(oyunHiziSlider);

    panel.repaint();
  }

  void ThreadleriBaslat(){

    oyunIlkCalistirma = false;
    musteriGenerator = new MusteriGenerator();
    musteriGenerator.start();

    for (int i=0; i<garsonSayisi; i++) garsonArrayList.get(i).start();
    for (int i=0; i<asciSayisi; i++) asciArrayList.get(i).start();
    for (int i=0; i<kasiyerSayisi; i++) kasiyerArrayList.get(i).start();
  }

  void ThreadleriYenidenBaslat(){
    musteriGenerator = new MusteriGenerator();
    musteriGenerator.start();
    oyunDevamBool = true;
  }

  void ThreadleriDurdur(){
    if(musteriGenerator.isAlive()) musteriGenerator.stop();

    oyunDevamBool = false;
  }

  void ThreadleriSonlandir(){
    if(musteriGenerator != null) musteriGenerator.stop();

    if (oyunEkraniBool){
      for (int i=0; i<garsonSayisi; i++) garsonArrayList.get(i).stop();
      for (int i=0; i<asciSayisi; i++) asciArrayList.get(i).stop();
      for (int i=0; i<kasiyerSayisi; i++) kasiyerArrayList.get(i).stop();
      }
  }

  App(){
    panel = new JPanel(){
      @Override
      public void paintComponent(Graphics g){
        super.paintComponent(g);
        if(!oyunEkraniBool) return;

        int sayac = 0;
        int aralik = 20;

        int[] sonuc = EnYakinIkiSayi(masaSayisi);
        int kutuSayisiX = sonuc[1];
        int kutuSayisiY = sonuc[0];
        if((kutuSayisiY-1)*(kutuSayisiX-1)>=masaSayisi){
          kutuSayisiY--;
          kutuSayisiX--;
        }
        else if(kutuSayisiX*(kutuSayisiY-1)>=masaSayisi){
          kutuSayisiY--;
        }

        int kutuX = (1000-20*2-((kutuSayisiX-1)*aralik))/kutuSayisiX;
        int kutuY = (750-30-60-20-20-((kutuSayisiY-1)*aralik))/kutuSayisiY;

        for(int j=0; j<kutuSayisiY; j++){
          for(int i=0; i<kutuSayisiX; i++){
            if(sayac<masaSayisi){
              if(masa[sayac].musteri == null){
                g.setColor(new Color(208, 212, 95));
                g.fillRect(20+i*(kutuX+aralik), 110+j*(kutuY+aralik), kutuX, kutuY);
              }
              else {
                if(masa[sayac].musteri.oncelikli){
                  g.setColor(new Color(204, 0, 153));
                  g.fillRect(20+i*(kutuX+aralik), 110+j*(kutuY+aralik), kutuX, kutuY);
                }
                else {
                  g.setColor(new Color(51, 153, 102));
                  g.fillRect(20+i*(kutuX+aralik), 110+j*(kutuY+aralik), kutuX, kutuY);
                }
              }
            }
            else {
              g.setColor(Color.lightGray);
              g.fillRect(20+i*(kutuX+aralik), 110+j*(kutuY+aralik), kutuX, kutuY);
            }
            sayac++;
          }
        }
      }
    };

    bekleyenMusteriButonu.setSize(148, 60);
    garsonlarButonu.setSize(148, 60);
    ascilarButonu.setSize(148, 60);
    kasiyerlerButonu.setSize(148, 60);
    oyunDevamButonu.setSize(148, 60);
    oyunEkraniGeriButonu.setSize(90, 60);
    ilerletButonu.setSize(100, 60);
    oyunBilgileri.setBounds(1010, 90, 180, 600);
    oyunBilgileri.setFont(new Font("TimesRoman", Font.BOLD, 20));
    oyunHiziSlider.setOrientation(JSlider.HORIZONTAL);
    oyunHiziSlider.setMinorTickSpacing(1);
    oyunHiziSlider.setMajorTickSpacing(1);
    oyunHiziSlider.setPaintTicks(true);
    oyunHiziSlider.setPaintLabels(true);
    oyunHiziSlider.setPaintTrack(true);
    oyunHiziSlider.setSnapToTicks(true);
    oyunHiziSlider.addChangeListener(this);
    oyunHiziSlider.setBounds(642, 30, 148, 45);
    oyunuHiziLabel.setBounds(672, 10, 148, 20);
    oyunuHiziLabel.setFont(new Font("TimesRoman", Font.BOLD, 14));

    panel.setLayout(null);
    this.add(panel);
    this.setTitle("Restoran Yönetim Sistemi");
    this.setSize(1200, 800);
    this.setResizable(false);
    this.setLocationRelativeTo(null);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    AnaEkran();
    this.setVisible(true);
  }

  public static void main(String[] args) {
    new App();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if(e.getSource()==anaEkranBaslatButonu){
      OyunuBaslat();
    }
    if(e.getSource()==anaEkranAyarlarButonu){
      AyarlarEkrani();
    }
    if(e.getSource()==ayarlarGeriButonu || e.getSource() == oyunEkraniGeriButonu){
      AnaEkran();
      ThreadleriSonlandir();
      oyunEkraniBool = false;
      oyunDevamButonu.setText("Devam");
      oyunDevamBool = false;
      oyunIlkCalistirma = true;
    }
    if(e.getSource()==oyunDevamButonu){
      if(!oyunDevamBool){
        oyunDevamButonu.setText("Durdur");
        oyunDevamBool = true;
        if(oyunIlkCalistirma){
          ThreadleriBaslat();
        }
        else {
          ThreadleriYenidenBaslat();
        }
      }
      else {
        oyunDevamButonu.setText("Devam");
        oyunDevamBool = false;
        ThreadleriDurdur();
      }
    }
    if(e.getSource()==bekleyenMusteriButonu){

    }
    if(e.getSource()==garsonlarButonu){

    }
    if(e.getSource()==ascilarButonu){

    }
    if(e.getSource()==kasiyerlerButonu){

    }
    if(e.getSource()==ilerletButonu){

    }
  }
  @Override
  public void stateChanged(ChangeEvent e) {
    oyunuHiziLabel.setText("Oyun hızı: "+oyunHiziSlider.getValue());
  }
}