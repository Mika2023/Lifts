import java.util.Random;

public class GeneratorTasks implements Runnable{
    private int allToGenerate, timeout;//in ms
    private int maxFloor;
    private UserTasksList userTasksList;


    public GeneratorTasks(int allToGenerate, int timeout, UserTasksList userTasksList, int maxFloor){
        this.allToGenerate = allToGenerate;
        this.timeout = timeout;
        this.userTasksList = userTasksList;
        this.maxFloor = maxFloor;
    }

    public void generate(int id, Random random){

        int startFloor = random.nextInt(this.maxFloor) + 1;
        int destFloor = random.nextInt(this.maxFloor) + 1;
        while(startFloor==destFloor){
            destFloor = random.nextInt(this.maxFloor) + 1;
        }
        UserTask ut = new UserTask(startFloor,destFloor,id);
        synchronized(System.out){
            System.out.format("User no%d has called lift from %d to %d floor\n", id, startFloor, destFloor);
            try {
                userTasksList.addTask(ut);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.format("User's order with no%d has added to the queue! User is waiting lift now\n", id);
        }
    }

    @Override
    public void run() {
        Random random = new Random();
        for (int i = 1; i<=this.allToGenerate;++i){
            generate(i, random);
            try {
                Thread.sleep(this.timeout);
            } catch (InterruptedException e) {
                return;
            }
        }
        try {
            userTasksList.notifyNotEmpty();
            Thread.currentThread().interrupt();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void stop(){

    }
}
