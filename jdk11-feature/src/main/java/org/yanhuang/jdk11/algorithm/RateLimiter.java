package org.yanhuang.jdk11.algorithm;

import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public class RateLimiter {

	public static void main(String[] args) throws Exception {
//		smoothLess();
		scheduled();
	}

	private static void smoothLess() throws InterruptedException {
		Rate rate = new SmoothLessRate(1000);
		final TaskGenerator generator = new TaskGenerator(rate);
		generator.start();
		TimeUnit.SECONDS.sleep(60);
		generator.stop();
	}

	private static void scheduled() throws InterruptedException {
		Rate rate = new ScheduledRate(1000);
		final TaskGenerator generator = new TaskGenerator(rate);
		generator.start();
		TimeUnit.SECONDS.sleep(60);
		generator.stop();
	}
}

class TaskGenerator {

	private final int threads = 24;

	private final CountDownLatch startSignal = new CountDownLatch(1);

	private volatile boolean stopSignal = false;

	private ExecutorService es = Executors.newFixedThreadPool(threads);

	private ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);

	public TaskGenerator(Rate rate) {
		RateStats stats = new RateStats(System.currentTimeMillis());
		ses.scheduleAtFixedRate(() -> {
			System.out.println(stats.rateToString());
		}, 1, 1, TimeUnit.SECONDS);
		final long bts = System.currentTimeMillis();
		for (int i = 0; i < threads; i++) {
			es.execute(() -> {
				try {
					startSignal.await();
				} catch (InterruptedException e) {
					//ignore
				}
				while (!stopSignal) {
					rate.acquire(1);
					stats.addDelta(1);
				}
			});
		}
	}

	public void start() {
		if (startSignal.getCount() > 0) {
			startSignal.countDown();
		}
	}

	public void stop() {
		ses.shutdownNow();
		this.stopSignal = true;
		es.shutdown();
		try {
			es.awaitTermination(5, TimeUnit.SECONDS);
			ses.awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			//ignore
		}
	}

}

class RateStats {
	private double total;
	private long beginMillis;

	RateStats(long beginMillis) {
		this.beginMillis = beginMillis;
	}

	public synchronized double addDelta(double delta) {
		total += delta;
		return total;
	}

	public String rateToString() {
		final long time = System.currentTimeMillis() - beginMillis;
		if (time > 0) {
			return "total " + total + " speed " + (total / (time) * 1000) + "/s";
		}
		return "too short time interval of print speed";
	}


}

interface Rate {
	void acquire(double permits);
}

/**
 * simple rate limiter, have no burst control. only sleep when exceeds max rate.
 */
class SmoothLessRate implements Rate {

	private final ReentrantLock lock = new ReentrantLock();

	private final double limitPerSeconds;

	private double released;

	private long lastNanoSecond;

	SmoothLessRate(double limitPerSecond) {
		this.limitPerSeconds = limitPerSecond;
	}

	@Override
	public void acquire(double permits) {
		lock.lock();
		try {
			released += permits;
			if (released >= limitPerSeconds) {
				final long sleep = TimeUnit.SECONDS.toNanos(1) - (System.nanoTime() - lastNanoSecond);
				if (sleep > 0) {
					TimeUnit.NANOSECONDS.sleep(sleep);
				}
				released = 0.0;
				lastNanoSecond = System.nanoTime();
			}
		} catch (Exception e) {
			//ignore
		} finally {
			lock.unlock();
		}
	}
}

/**
 * rate limiter using scheduled executor service.
 */
class ScheduledRate implements Rate {

	private final int scale = 1_000;

	private final Semaphore pool;

	private ScheduledExecutorService ses = Executors.newScheduledThreadPool(1, (r) -> {
		Thread t = new Thread(r);
		t.setDaemon(true);
		return t;
	});

	ScheduledRate(double limitPerSecond) {
		pool = new Semaphore((int) (limitPerSecond * scale));
		ses.scheduleAtFixedRate(() -> {
			pool.release(scale);
		}, TimeUnit.SECONDS.toNanos(1), (long) (TimeUnit.SECONDS.toNanos(1) / limitPerSecond), TimeUnit.NANOSECONDS);
	}

	@Override
	public void acquire(double permits) {
		try {
			pool.acquire((int) (permits * scale));
		} catch (InterruptedException e) {
			//ignore
		}
	}
}




