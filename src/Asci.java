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
          for (Musteri musteri: App.siparisArrayList){
            if(musteri1 == null){
              musteri1 = musteri;
              App.siparisArrayList.remove(musteri);
            }
            if(musteri1 != null && musteri2 == null){
              musteri2 = musteri;
              App.siparisArrayList.remove(musteri);
            }
            if(musteri1 != null && musteri2 != null) break;
          }
        }
        finally {
          App.siparisLock.unlock();
        }

        if(musteri1 != null && musteri2 == null){
          Thread.sleep(3000);
          musteri1.start();
        } else if (musteri1 != null && musteri2 != null) {
          Thread.sleep(3000);
          musteri1.start();
          musteri2.start();
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
