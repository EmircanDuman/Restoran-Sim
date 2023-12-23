import java.util.Random;

public class Musteri extends Thread{

  static Random r = new Random();
  static int count = 1;
  int id;
  boolean oncelikli = false;
  private volatile Boolean taken = false;
  volatile String durum;
  int sayac = 0;

  @Override
  public void run(){
    while (!taken){
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      if(sayac >= 20){
        App.musteriLock.lock();
        try {
        App.musteriArrayList.remove(this);
        }
        finally {
          App.musteriLock.unlock();
        }
        stop();
      }
      if(App.oyunDevamBool) sayac++;
    }
    try{
      durum = "Yemek yiyor";

      Thread.sleep(3000);
      App.kasaLock.lock();
      try {
        App.kasaArrayList.add(this);
        durum = "Kasa bekliyor";
      }
      finally {
        App.kasaLock.unlock();
      }
    }
    catch (InterruptedException e){
      throw new RuntimeException(e);
    }
  }

  Musteri(){
    id = count++;
    if(r.nextInt(5)==0) oncelikli = true;
    durum = "Sıra bekliyor";
  }

  public Boolean getTaken() {
    return taken;
  }

  public void setTaken(Boolean taken) {
    this.taken = taken;
  }
}
