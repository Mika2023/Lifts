import java.util.ArrayList;
import java.util.List;

public class UserTasksList {
    private List<UserTask> queueTasks;
    private int allGeneratedTasks;
    private Object while_empty;

    public UserTasksList() {
        this.queueTasks = new ArrayList<UserTask>();
        this.allGeneratedTasks = 0;
        this.while_empty = new Object();
    }

    public synchronized List<UserTask> giveTaskToLift(int floor, int direction){
        return queueTasks.stream().filter(ut -> ut.getStart()==floor && ut.getDirection()==direction).toList();
    }
    public synchronized void acceptedTasks(int count, int floor, int direction){
        //lift has accepted the first count tasks
        this.queueTasks.removeAll(giveTaskToLift(floor, direction).subList(0, count));
    }

    public synchronized UserTask waitLong(){
        if (queueTasks.size()!=0) return queueTasks.get(0);
        else return new UserTask();
    }

    public synchronized void addTask(UserTask ut) throws InterruptedException{
        this.queueTasks.add(ut);
        this.allGeneratedTasks+=1;
        notifyNotEmpty();
    }
    
    public synchronized boolean empty() {return this.queueTasks.isEmpty();}
    public synchronized int getSize() {return this.queueTasks.size();}
    public synchronized int getAllGeneratedTasks() {return this.allGeneratedTasks;}

    public void waitWhileEmpty() throws InterruptedException{
        synchronized(while_empty){
            while_empty.wait(1000);
        }
    }

    public void notifyNotEmpty() throws InterruptedException{
        synchronized(while_empty){
            while_empty.notify();
        }
    }
}
