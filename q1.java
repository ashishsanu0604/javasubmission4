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
	private final ReentrantLock rd;
    public Test(List<Person> l,ReentrantLock r) {
        this.lp = l;
        this.rd=r;
    }

	public List<Person> call() throws Exception {
		try {
			if(!rd.isLocked())
			{
				rd.lock();
				Thread.sleep(1000);
			}
			else {
				rd.wait();
				return null;
			}
			
		 }
		finally{
			rd.unlock();return this.lp;
		}
	}
}
class TestWrite implements Callable<String>{
	private final List<Person> lp;
	private final Person p; 
	private final ReentrantLock wr;
    public TestWrite(List<Person> l,Person p1,ReentrantLock w) {
        this.lp = l;
        this.p=p1;
        this.wr=w;
    }

	public String call() throws Exception,InterruptedException {
		 try {
		if(!wr.isLocked())
		{
			wr.lock();
			this.lp.add(this.p); 
			Thread.sleep(1000);
		}
		else {
			wr.wait();return "-";
		}
		 
		 }
		 catch(InterruptedException e)
		 {
			 
		 }
		 finally {
			 wr.unlock();
			 return "written";
		 }
	}
}
public class q1 {

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
		ReentrantLock lock = new ReentrantLock();
//		Lock readLock = lock.readLock();
//		Lock writeLock = lock.writeLock();
		FutureTask<List<Person>> future=new FutureTask<List<Person>>(new Test(lp,lock));
		FutureTask<String> futurew1=new FutureTask<String>(new TestWrite(lp,p3,lock));
		FutureTask<String> futurew2=new FutureTask<String>(new TestWrite(lp,p4,lock));
		
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
