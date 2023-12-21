import java.util.Iterator;

public class Garson extends Thread {

  static int count = 1;
  private int id;
  private volatile int ilgileniyor = -1;

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

            if (!oncelikliHandled && !musteri.getTaken() && musteri.oncelikli && App.oyunDevamBool) {
              handleSuccess = handleMusteri(musteri, iterator);
              oncelikliHandled = true;

              App.siparisLock.lock();
              try {
              App.siparisArrayList.add(musteri);
              }
              finally {
                App.siparisLock.unlock();
              }
              break;
            }

            if (!musteri.getTaken() && !oncelikliHandled && App.oyunDevamBool) {
              handleSuccess = handleMusteri(musteri, iterator);
              App.siparisLock.lock();
              try {
                App.siparisArrayList.add(musteri);
              }
              finally {
                App.siparisLock.unlock();
              }
              break;
            }
          }
        } finally {
          App.musteriLock.unlock();
        }

        if (!App.oyunDevamBool) {
          while (!App.oyunDevamBool && !Thread.interrupted()) {
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
        if (App.masa[i].musteri == null) {
          ilgileniyor = musteri.id;
          App.masa[i].musteri = musteri;
          musteri.setTaken(true);
          musteri.durum = "Yemek bekliyor";

          App.panel.repaint();
          //iterator.remove();
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
