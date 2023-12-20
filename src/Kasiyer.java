public class Kasiyer extends Thread{

  static int count = 1;
  int id;

  @Override
  public void run(){
    try {
      while (!isInterrupted()){
        App.kasaLock.lock();
        try {
          if(App.kasaArrayList.isEmpty() || !App.oyunDevamBool){
            Thread.sleep(100);
          }
          else {
            Thread.sleep(1000);
            System.out.println("Removed "+ App.kasaArrayList.get(0).id +" by " +id);
            App.kasaArrayList.remove(0);
          }
        }
        finally {
          App.kasaLock.unlock();
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
