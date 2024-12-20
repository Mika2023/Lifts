import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Lift implements Runnable{
    private int id, curCapacity, curFloor, maxFloor;
    private int maxCapacity; //the number of users with that the lift can work
    private short direction; //-1 - goes down, 0 - nowhere, 1 - goes up
    private List<UserTask> curTasks;
    private UserTasksList queue;
    private UserTasksAchievedList uAchievedList;
    private boolean working;
    
        public Lift(int maxCapacity, int id, int maxFloor, UserTasksList queue, UserTasksAchievedList uAchievedList){
            this.id = id;
            this.curCapacity = 0;
            this.curFloor = 1;
            this.maxFloor = maxFloor;
            this.maxCapacity = maxCapacity;
            this.direction = 0;
            this.curTasks = new ArrayList<UserTask>();
            this.queue = queue;
            this.uAchievedList = uAchievedList;
            this.working = true;
        }
    
        public void updateFloor(){
            //change the curFloor;
            if (this.curFloor+this.direction>this.maxFloor || this.curFloor+this.direction<=0) this.curFloor = (this.curFloor+this.direction>this.maxFloor)? this.maxFloor: 1;
            else this.curFloor += this.direction;
    
            //drop people off
            List<UserTask> ahcievedList = this.curTasks.stream().filter(ut -> ut.getDest()==this.curFloor).toList();
            int count = ahcievedList.size();
            if (count>0){
                this.curCapacity -= count;
                ahcievedList.stream().forEach(ut -> ut.printUserTaskAchieved());
                this.uAchievedList.addListTasks(ahcievedList);
                this.curTasks = this.curTasks.stream().filter(ut -> ut.getDest()!=this.curFloor).toList();
            }
    
            //accept new tasks with that floor and direction
            defineDirection();
            queue.acceptedTasks(acceptTasks(), curFloor, direction);
            if (this.direction==0) defineDirection();
            if (this.direction==0){
                zeroDirection();
            }
        }
    
        public int acceptTasks(){
            List<UserTask> possibleTasks = queue.giveTaskToLift(this.curFloor, this.direction);
            if (possibleTasks.isEmpty()) return 0;
            int dif = this.maxCapacity-this.curCapacity;
            if(dif<possibleTasks.size()){
                this.curTasks = Stream.concat(possibleTasks.stream().limit(dif), this.curTasks.stream()).toList(); 
                this.curCapacity += dif;
                return dif;
            }
            else{
                if (this.curTasks.isEmpty()) this.curTasks = possibleTasks;
                else this.curTasks = Stream.concat(possibleTasks.stream(), this.curTasks.stream()).toList(); 
                this.curCapacity+=possibleTasks.size();
                return possibleTasks.size();
            }
        }
    
        public void defineDirection(){
    
            switch (this.direction) {
                case -1:
                    if(this.curTasks.stream().noneMatch(ut->ut.getDest()<this.curFloor)) this.direction = 1;
                    break;
                case 0:
                    if(this.curTasks.stream().filter(ut->ut.getDest()<this.curFloor).count()>this.curTasks.stream().filter(ut->ut.getDest()>this.curFloor).count()) this.direction = -1;
                    else this.direction = 1;
                    break;
                case 1:
                    if(this.curTasks.stream().noneMatch(ut->ut.getDest()>this.curFloor)) this.direction = -1;
                    break;
                default:
                    this.direction = 0;
                    break;
            }
            if(this.curTasks.size()==0) this.direction = 0;
            if (this.curFloor==this.maxFloor && !this.curTasks.isEmpty()) this.direction = -1;
            else if (this.curFloor==1 && !this.curTasks.isEmpty()) this.direction = 1;
        }
        public void zeroDirection(){
            UserTask waitingLong = queue.waitLong();
            if (!queue.empty()){
                this.curFloor = waitingLong.getStart();
                this.direction = (short)waitingLong.getDirection();
                queue.acceptedTasks(acceptTasks(), curFloor, direction);
            }
        }
    
        public int getId() {return this.id; }
        public boolean emptyTasks() {return this.curTasks.isEmpty();}
    
        public void printLiftInfo(){
            String directionString = "";
            switch (this.direction) {
                case -1:
                    directionString = "down";
                    break;
                case 0:
                    directionString = "nowhere";
                    break;
                case 1:
                    directionString = "up";
                    break;
                default:
                    break;
            }
            synchronized(System.out){
                System.out.format("The lift no%d is on %d floor now and goes %s\n", this.id, this.curFloor,directionString);
                System.out.format("There're %d people in this lift out of %d:\n", this.curCapacity, this.maxCapacity);
                curTasks.stream().forEach(ut -> ut.printTaskInfo());
            }
        }
    
        public void workLift(){
            int count = 0;
            while (working){
                if (!working) break;
                
                if(queue.empty()){
                    try {
                        queue.waitWhileEmpty();
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                updateFloor();
                printLiftInfo();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if (direction==0 && curTasks.isEmpty()) count+=1;
                if(count==2) break;
            }
        }
    
        public void stopWorking() throws InterruptedException{
            this.working = false;
            queue.notifyNotEmpty();
        }
        @Override
        public void run() {
            workLift();
        }
}
