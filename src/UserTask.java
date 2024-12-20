public class UserTask {
    private int id, startFloor, destFloor;

    public UserTask(int startFloor, int destFloor, int id) {
        this.id = id;
        this.destFloor = destFloor;
        this.startFloor = startFloor;
    }
    public UserTask(){this(1, 1, 0);}

    public int getDest() {return this.destFloor;}
    public int getStart() {return this.startFloor;}
    public int getDirection() {return (this.destFloor-this.startFloor)/(Math.abs(this.destFloor-this.startFloor));}
    public int getId() {return this.id;}

    public void printTaskInfo(){
        synchronized(System.out){
            System.out.format("\tUser no%d is going from %d to %d\n",this.id, this.startFloor, this.destFloor);
        }
    }
    public void printUserTaskAchieved(){
       synchronized(System.out){
        System.out.format("User no%d was going from %d to %d and finally gets the destination!\n",this.id, this.startFloor, this.destFloor);
       }
    }
}
