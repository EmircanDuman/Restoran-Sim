public class Kasiyer extends Thread{

  static int count = 1;
  int id;

  @Override
  public void run(){
    try {
      while (!isInterrupted()){
        if(!App.kasaArrayList.isEmpty() && App.oyunDevamBool){
          App.kasaLock.lock();
          App.musteriLock.lock();
          App.masaLock.lock();
          App.kazancLock.lock();
          try {
            Thread.sleep(1000);

            for(int i=0; i<App.masaSayisi; i++){
              if(App.masa[i].musteri == App.kasaArrayList.get(0)){
                App.masa[i].musteri = null;
                App.toplamKazanc += App.musteriKazanci;
                App.oyunBilgileri.setText("Kazanc: " + App.toplamKazanc);
                break;
              }
            }
            App.musteriArrayList.remove(App.kasaArrayList.get(0));
            App.kasaArrayList.remove(0);

          }
          finally {
            App.kasaLock.unlock();
            App.musteriLock.unlock();
            App.masaLock.unlock();
            App.kazancLock.unlock();
          }
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

  Kasiyer(){
    id = count++;
  }

}
