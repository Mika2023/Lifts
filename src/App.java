import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {
        int maxCapacity, maxFloor, timeout, allToGenerate;
        Scanner sc = new Scanner(System.in);
        maxCapacity = sc.nextInt();
        maxFloor = sc.nextInt();
        timeout = sc.nextInt();
        allToGenerate = sc.nextInt();
        sc.close();

        UserTasksList uList = new UserTasksList();
        UserTasksAchievedList userTasksAchievedList = new UserTasksAchievedList();
        Lift lift1 = new Lift(maxCapacity,1, maxFloor,uList, userTasksAchievedList);
        Lift lift2 = new Lift(maxCapacity,2, maxFloor,uList, userTasksAchievedList);

        GeneratorTasks generatorTasks = new GeneratorTasks(allToGenerate, timeout, uList, maxFloor);

        Thread generator = new Thread(generatorTasks);
        
        Thread lift1_work = new Thread(lift1);
        Thread lift2_work = new Thread(lift2);

        generator.start();

        lift1_work.start();
        lift2_work.start();

    }
}
