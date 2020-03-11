package exer42;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.Semaphore;


class Person{
	Integer id;
	LocalDate dob;
	List<String> jobs;
	Person(Integer id1,LocalDate d,List<String> jo)
	{
		this.id=id1;
		this.dob=d;
		this.jobs=jo;
	}
	@Override
	public String toString() {
		return "Person has id="+id+",with dob="+dob.toString()+"has these jobs="+jobs.toString();
	}
}
class Test implements Callable<List<Person>>{
	private final List<Person> lp;
	private Semaphore sem; 
    public Test(List<Person> l,Semaphore s) {
        this.lp = l;
        this.sem=s;
    }

	public List<Person> call() throws Exception {
		try {
			sem.acquire();
			return this.lp;
		 }
		finally{
			sem.release();
		}
	}
}
class TestWrite implements Callable<String>{
	private final List<Person> lp;
	private final Person p; 
	private Semaphore sem; 
    public TestWrite(List<Person> l,Person p1,Semaphore w) {
        this.lp = l;
        this.p=p1;
        this.sem=w;
    }

	public String call() throws Exception,InterruptedException {
		 try {
			 sem.acquire();
			this.lp.add(this.p); 
			Thread.sleep(1000);
			return "written";
		 }
		 finally {
			 sem.release();
		 }
	}
}
public class q2 {

	public static void main(String[] args)throws InterruptedException, ExecutionException {
		// TODO Auto-generated method stub
		List<String> l1=new ArrayList<>(),l2=new ArrayList<>(),l3=new ArrayList<>();
		l1.add("Eng");
		l2.add("Doc");
		Person p1=new Person(1,LocalDate.now(),l1);
		Person p2=new Person(2,LocalDate.now(),l2);
		l3.add("Arts");
		Person p3=new Person(3,LocalDate.now(),l3);
		Person p4=new Person(4,LocalDate.now().minusDays(2),l2);
		List<Person> lp=new ArrayList<>();
		lp.add(p1);
		lp.add(p2);
		
		
		ExecutorService service = Executors.newFixedThreadPool(10);
		Semaphore semaphore = new Semaphore(10);
		FutureTask<List<Person>> future=new FutureTask<List<Person>>(new Test(lp,semaphore));
		FutureTask<String> futurew1=new FutureTask<String>(new TestWrite(lp,p3,semaphore));
		FutureTask<String> futurew2=new FutureTask<String>(new TestWrite(lp,p4,semaphore));
		
		service.execute(future);
		for(Person p:future.get())
		{
			System.out.println("The result after seraching is  "+p.toString());
		}//future.wait();
		service.execute(futurew1);
		System.out.println(futurew1.get());
		
		//if(futurew1.isDone())
		
		{
			service.execute(future);
			for(Person p:future.get())
			{
				System.out.println("The result after seraching is  "+p.toString());
			}
		}
		
		//service.execute(futurew2);
		//System.out.println(futurew2.get());
		//if(future1.isDone())
//		{
//			service.execute(future);
//			System.out.println();System.out.println();
//			for(Person p:future.get())
//			{
//				System.out.println("The result after seraching is  "+p.toString());
//			}
//		}
		service.shutdown();
	}

}
