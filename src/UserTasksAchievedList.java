import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class UserTasksAchievedList {
    private List<UserTask> achievedList;

    public UserTasksAchievedList() {this.achievedList = new ArrayList<UserTask>();}

    public synchronized void addTask(UserTask ut){
        achievedList.add(ut);
    }
    public synchronized void addListTasks(List<UserTask> uList){
        this.achievedList = Stream.concat(uList.stream(), this.achievedList.stream()).toList(); 
    }

    public synchronized int getSize() {return this.achievedList.size();}
}
