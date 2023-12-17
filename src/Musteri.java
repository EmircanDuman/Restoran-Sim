import java.util.Random;

public class Musteri extends Thread{

  static Random r = new Random();
  static int count = 1;
  int id;
  boolean oncelikli = false;
  private volatile Boolean taken = false;

  @Override
  public void run(){

  }

  Musteri(){
    id = count++;
    if(r.nextInt(5)==0) oncelikli = true;
  }

  public Boolean getTaken() {
    return taken;
  }

  public void setTaken(Boolean taken) {
    this.taken = taken;
  }
}
