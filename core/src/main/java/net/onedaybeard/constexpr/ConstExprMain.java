package net.onedaybeard.constexpr;

import net.onedaybeard.constexpr.exec.ConstExprScannerTask;
import net.onedaybeard.constexpr.exec.ConstExprTransformer;
import net.onedaybeard.constexpr.inspect.ClassMetadata;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class ConstExprMain {
	private final ExecutorService executor;

	public ConstExprMain() {
		executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	}

	public Stats execute(String root) {
		Stats stats = new Stats();

		long startScan = System.currentTimeMillis();
		List<ClassMetadata> scanned = scan(root);
		stats.scanned = scanned;
		stats.scanTime = System.currentTimeMillis() - startScan;

		long startTransform = System.currentTimeMillis();
		scanned.stream()
			.filter(ClassMetadata::containsConstExpr)
			.map(ConstExprTransformer::new)
			.map(executor::submit)
			.collect(toList())
			.forEach(ConstExprMain::resolveFuture);
		stats.transformTime = System.currentTimeMillis() - startTransform;

		return stats;
	}

	private List<ClassMetadata> scan(String root) {
		List<Future<ClassMetadata>> futures = walk(Paths.get(root).normalize())
			.filter(path -> path.toString().endsWith(".class"))
			.map(ConstExprScannerTask::new)
			.map(executor::submit)
			.collect(toList());

		return futures.stream()
			.map(ConstExprMain::resolveFuture)
			.collect(toList());
	}

	private static <T> T resolveFuture(Future<T> future) {
		try {
			return future.get();
		} catch (ExecutionException | InterruptedException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public static void main(String[] args) {
		String directory = args.length > 0 ? args[0] : "core/target/test-classes";
		ConstExprMain cem = new ConstExprMain();
		cem.execute(directory);
	}

	private static Stream<Path> walk(Path path) {
		try {
			return Files.walk(path);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static class Stats {
		public long scanTime;
		public long transformTime;
		public List<ClassMetadata> scanned;
	}
}
