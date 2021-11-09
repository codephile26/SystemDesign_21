import java.util.Date;

class Bucket{

	private int maxTokens;
	private int bucket; //tokens available in bucket
	private int timeUnit;
	private Date lastTimeStamp;
	
	public Bucket(int tokens, int timeUnit){
		this.maxTokens = tokens;
		this.bucket = tokens;
		this.timeUnit = timeUnit;
		this.lastTimeStamp = new Date();
	}

	public void refreshTokens(int tokens){
		Date currentTimeStamp = new Date();
		long differenceInTime = currentTimeStamp.getTime() - this.lastTimeStamp.getTime();
		long differenceInSeconds = differenceInTime/1000 % 60;
		if (differenceInSeconds >= this.timeUnit){
			bucket = tokens;
		}
		this.lastTimeStamp = currentTimeStamp;
	}

	public int fetchToken(){
		return this.bucket;
	}

	public void updateToken(){
		this.bucket-=1;
		this.lastTimeStamp = new Date();
	}

	public void handleRequests(int requestId){
		int availableTokens = fetchToken();
		if (availableTokens < 1){
			System.out.println("API Limit reached, please try in some time");
		} else {
			System.out.println("Your request " + requestId + " is successfully processed");
		}
		this.updateToken();
	}
}

class RefreshTokenThread extends Thread {
	private Bucket b;
	
	RefreshTokenThread(Bucket b){
		this.b = b;
	}

	public void run(){
		while(true){
			b.refreshTokens(5);
			try{
				Thread.sleep(6000); // thread runs every minute
			} catch(Exception e){}
		}
	}
}

class UserThread extends Thread {
	private Bucket b;

	UserThread(Bucket b){
		this.b = b;
	}

	public void run(){
		int requestId = 1;
		while (true){
			this.b.handleRequests(requestId++);
			try {
				Thread.sleep(5000);
			} catch(Exception e){}
		}
	}
}

public class TokenBucket{

	public static void main(String...ar){
		Bucket b = new Bucket(5,60);
		Thread refreshTokenThread = new RefreshTokenThread(b);
		Thread userThread = new UserThread(b);
		try{
			refreshTokenThread.start();
			userThread.start();
		}
		catch(Exception e){}
	}
}