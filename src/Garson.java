import java.util.Iterator;

public class Garson extends Thread {

  static int count = 1;
  private int id;
  private volatile int ilgileniyor = -1;
  private volatile boolean paused = false;

  // Method to pause the thread
  public void pauseThread() {
    paused = true;
  }

  // Method to continue the thread
  public void continueThread() {
    paused = false;
  }

  @Override
  public void run() {
    try {
      while (!Thread.interrupted()) {
        ilgileniyor = -1;
        boolean handleSuccess = false;
        boolean oncelikliHandled = false;

        App.musteriLock.lock();
        try {
          Iterator<Musteri> iterator = App.musteriArrayList.iterator();

          while (iterator.hasNext()) {
            Musteri musteri = iterator.next();

            if (!oncelikliHandled && !musteri.getTaken() && musteri.oncelikli && !paused && App.oyunDevamBool) {
              handleSuccess = handleMusteri(musteri, iterator);
              oncelikliHandled = true;
              break;
            }

            if (!musteri.getTaken() && !oncelikliHandled && !paused && App.oyunDevamBool) {
              handleSuccess = handleMusteri(musteri, iterator);
              break;
            }
          }
        } finally {
          App.musteriLock.unlock();
        }

        if (paused) {
          while (paused && !Thread.interrupted()) {
            Thread.sleep(100); // Add a small delay to reduce CPU usage while paused
          }
        }

        if (handleSuccess && !Thread.interrupted()) {
          // Sleep only if handleMusteri succeeded and the thread is not interrupted
          try {
            Thread.sleep(2000);
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            break;
          }
        }
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private boolean handleMusteri(Musteri musteri, Iterator<Musteri> iterator) {
    App.masaLock.lock();
    try {
      for (int i = 0; i < App.masaSayisi; i++) {
        if (App.Masa[i] == -1) {
          ilgileniyor = musteri.id;
          App.Masa[i] = musteri.id;
          musteri.setTaken(true);

          System.out.println("masa given to " + musteri.id + " by garson:" + id);
          App.panel.repaint();
          iterator.remove();
          return true;
        }
      }
    } finally {
      App.masaLock.unlock();
    }

    return false;
  }

  Garson() {
    id = count++;
  }
}
