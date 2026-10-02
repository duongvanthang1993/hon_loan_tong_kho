import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RobotControlSystemRefactored {

    private AtomicInteger totalWeight = new AtomicInteger(0);

    public void dropItem() {
        try {
            Thread.sleep(5);

        } catch (InterruptedException e) {
        }
        totalWeight.addAndGet(10);
    }

    public int getTotalWeight() {
        return totalWeight.get();
    }

    static class Zone {
        String name;
        Lock lock = new ReentrantLock();

        public Zone(String name) {
            this.name = name;
        }
    }

    public void moveRobot(Zone currentZone, Zone nextZone, String robotName) {
        while (true) {
            try {
                if (currentZone.lock.tryLock(50, TimeUnit.MILLISECONDS)) {
                    try {
                        System.out.println(
                                robotName + " da chiem " + currentZone.name + "va dang huong toi" + nextZone.name);
                        if (nextZone.lock.tryLock(50, TimeUnit.MILLISECONDS)) {
                            try {
                                System.out.println(robotName + "da di chuyen thanh cong tu " + currentZone.name
                                        + " sang " + nextZone.name);
                                return;
                            } finally {
                                nextZone.lock.unlock();
                                ;
                            }
                        } else {
                            System.out.println("ket duong" + robotName + "khong the vao " + nextZone.name
                                    + ",dang nha khoa lai...");
                        }
                    } finally {
                        currentZone.lock.unlock();
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        RobotControlSystemRefactored system = new RobotControlSystemRefactored();

        Thread[] robots = new Thread[500];

        for (int i = 0; i < 500; i++) {
            robots[i] = new Thread(() -> system.dropItem());
            robots[i].start();
            ;
        }
        for (Thread r : robots)
            r.join();
        System.out.println("ky vong: 5000kg. Thuc te ghi nhan: " + system.getTotalWeight() + "kg\n");

        System.out.println("bat dau mo phong nga tu thong minh");
        Zone zoneA = new Zone("khu a");
        Zone zoneB = new Zone("khu B");

        Thread robot1 = new Thread(() -> system.moveRobot(zoneA, zoneB, "robot-alpha"));
        Thread robot2 = new Thread(() -> system.moveRobot(zoneB, zoneA, "robot-betal"));

        robot1.start();
        robot2.start();

        robot1.join();
        robot2.join();

        System.out.println("tat ca robot da di chuyen an toan, khong xay ra hien tuong treo hej thong");

    }

}