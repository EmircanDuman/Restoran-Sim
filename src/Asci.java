import java.util.Iterator;

public class Asci extends Thread{

  static int count = 1;
  int id;
  Musteri musteri1;
  Musteri musteri2;

  @Override
  public void run(){
    try {
      while (!Thread.interrupted()){
        musteri1 = null;
        musteri2 = null;

        //musteri listesinden bostaki musterileri cek
        App.siparisLock.lock();
        try {
          Iterator<Musteri> iterator = App.siparisArrayList.iterator();

          while (iterator.hasNext()) {
            Musteri musteri = iterator.next();

            if (musteri1 == null) {
              musteri1 = musteri;
              iterator.remove();
              continue;
            }

            if (musteri1 != null && musteri2 == null) {
              musteri2 = musteri;
              iterator.remove();
            }

            if (musteri1 != null && musteri2 != null) {
              break;
            }
          }
        }
        finally {
          App.siparisLock.unlock();
        }

        if(musteri1 != null){
          Thread.sleep(3000);
        }
        else {
          Thread.sleep(100);
        }

      }
    }
    catch (InterruptedException e){
      throw new RuntimeException(e);
    }
  }

  Asci(){
    id = count++;
  }

}
