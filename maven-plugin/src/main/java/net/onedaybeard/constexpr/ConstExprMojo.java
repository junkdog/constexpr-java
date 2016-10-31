package net.onedaybeard.constexpr;

import net.onedaybeard.constexpr.inspect.ClassMetadata;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.objectweb.asm.Type;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.apache.maven.plugins.annotations.LifecyclePhase.PROCESS_CLASSES;
import static org.apache.maven.plugins.annotations.ResolutionScope.COMPILE_PLUS_RUNTIME;

@Mojo(
	name = "constexpr",
	defaultPhase = PROCESS_CLASSES,
	requiresDependencyResolution = COMPILE_PLUS_RUNTIME)
public class ConstExprMojo extends AbstractMojo {
	@Parameter(defaultValue = "${project}", readonly = true, required = true)
	private MavenProject project;

	@Parameter(property="project.build.outputDirectory")
	private File classDirectory;

	@Parameter(property="project.build.directory")
	private File saveDirectory;
	
	@Parameter(property="project.name")
	private String name;

	@Parameter(property="constexpr.skip", defaultValue = "false")
	public boolean skip;

	@Parameter(property="constexpr.verbose", defaultValue = "false")
	public boolean verbose;

	public static final int RELATIVE_WIDTH = 72;
	public static final String LINE = horizontalLine();

	@Override
	public void execute() throws MojoExecutionException {
		if (skip)
			return;

		if (!classDirectory.exists()) {
			getLog().info("Skipping execution, no classes found");
			return;
		}

		// ConstExprFieldWeaver needs to reflectively resolve static fields;
		// make the project runtime classpath available to this mojo
		configureClassLoader(project);

		ConstExprMain constExpr = new ConstExprMain();
		ConstExprMain.Stats stats = constExpr.execute(classDirectory.getAbsolutePath());

		List<ClassMetadata> transformed = stats.scanned.stream()
			.filter(ClassMetadata::containsConstExpr)
			.collect(toList());

		logf(keyValue(
			"Scanned " + stats.scanned.size() + " classes",
			stats.scanTime + "ms"));
		logf(keyValue(
			"Transformed " + transformed.size() + " classes",
			stats.transformTime + "ms"));

		if (verbose && transformed.size() > 0) {
			logf("");
			logf("@ConstExpr Log");
			logf(LINE);
			for (ClassMetadata meta : transformed) {
				long fields = meta.fields.stream()
					.filter(f -> f.isConstExpr)
					.count();
				long methods = meta.methods.stream()
					.filter(f -> f.isConstExpr)
					.count();

				String s = "fields:" + fields;
				if (methods > 0)
					s += " methods:" + methods;

				logKeyValue(shortenClass(meta.type.getDescriptor()), s);
			}
			logf(LINE);
		}
	}

	private void configureClassLoader(MavenProject project)
			throws MojoExecutionException {

		try {
			URL[] urls = project.getRuntimeClasspathElements()
				.stream()
				.map(File::new)
				.map(ConstExprMojo::toUrl)
				.collect(toList())
				.toArray(new URL[0]);

			ClassLoader parentClassLoader = Thread.currentThread().getContextClassLoader();
			URLClassLoader classLoader = URLClassLoader.newInstance(urls, parentClassLoader);
			Thread.currentThread().setContextClassLoader(classLoader);
		} catch (DependencyResolutionRequiredException e) {
			throw new RuntimeException(e);
		}
	}

	private static URL toUrl(File f) {
		try {
			return f.toURI().toURL();
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	private void logKeyValue(String key, Object value) {
		getLog().info(keyValue(key, value));
	}

	private void logf(String format, Object... args) {
		getLog().info(String.format(format, args));
	}

	private static String horizontalLine() {
		char[] raw = new char[RELATIVE_WIDTH];
		Arrays.fill(raw, '-');
		return String.valueOf(raw);
	}

	private static String keyValue(String key, Object value) {
		return keyValue(key, value, '.');
	}

	private static String keyValue(String key, Object value, char delim) {
		int length = key.length() + value.toString().length() + 2; // margin
		length = Math.max(length, 3);

		char[] padding = new char[Math.max(RELATIVE_WIDTH - length, 0)];
		Arrays.fill(padding, delim);

		return new StringBuilder(RELATIVE_WIDTH)
			.append(key)
			.append(" ").append(String.valueOf(padding)).append(" ")
			.append(value)
			.toString();
	}

	private static String shortenClass(Type type) {
		return shortenClass(type.getClassName());
	}

	private static String shortenClass(String className) {
		StringBuilder sb = new StringBuilder();

		String[] split = className.split("[./]");
		for (int i = 0; (split.length - 1) > i; i++) {
			sb.append(split[i].charAt(0)).append('.');
		}
		sb.append(split[split.length - 1]);
		return sb.toString();
	}
}
