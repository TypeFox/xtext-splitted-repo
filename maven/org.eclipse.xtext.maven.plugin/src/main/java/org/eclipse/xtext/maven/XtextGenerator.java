/**
 * 
 */
package org.eclipse.xtext.maven;

import static com.google.common.collect.Iterables.filter;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.eclipse.xtext.builder.standalone.LanguageAccess;
import org.eclipse.xtext.builder.standalone.LanguageAccessFactory;
import org.eclipse.xtext.builder.standalone.StandaloneBuilder;
import org.eclipse.xtext.builder.standalone.compiler.CompilerConfiguration;
import org.eclipse.xtext.builder.standalone.compiler.IJavaCompiler;
import org.eclipse.xtext.util.Strings;
import org.eclipse.xtext.xbase.lib.IterableExtensions;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Dennis Huebner - Initial contribution and API
 * 
 * @goal generate
 * @phase generate-sources
 * @requiresDependencyResolution compile
 */
public class XtextGenerator extends AbstractMojo {

	/**
	 * Location of the generated source files.
	 * 
	 * @parameter expression="${project.build.directory}/xtext-temp"
	 */
	private String tmpClassDirectory;

	/**
	 * File encoding argument for the generator.
	 * 
	 * @parameter expression="${xtext.encoding}"
	 *            default-value="${project.build.sourceEncoding}"
	 */
	protected String encoding;

	/**
	 * The project itself. This parameter is set by maven.
	 * 
	 * @parameter expression="${project}"
	 * @readonly
	 * @required
	 */
	protected MavenProject project;

	/**
	 * Project classpath.
	 * 
	 * @parameter expression="${project.compileClasspathElements}"
	 * @readonly
	 * @required
	 */
	private List<String> classpathElements;

	/**
	 * Project source roots.
	 * 
	 * @parameter
	 */
	private List<String> sourceRoots;

	/**
	 * @parameter
	 * @required
	 */
	private List<Language> languages;

	/**
	 * @parameter expression="${xtext.generator.skip}" default-value="false"
	 */
	private Boolean skip;

	/**
	 * @parameter default-value="true"
	 */
	private Boolean failOnValidationError;

	/**
	 * @parameter expression="${maven.compiler.source}" default-value="1.5"
	 */
	private String compilerSourceLevel;

	/**
	 * @parameter expression="${maven.compiler.target}" default-value="1.5"
	 */
	private String compilerTargetLevel;

	/**
	 * RegEx expression to filter class path during model files look up
	 * 
	 * @parameter
	 */
	private String classPathLookupFilter;

	/**
	 * Clustering configuration to avoid OOME
	 *
	 * @parameter
	 */
	private ClusteringConfig clusteringConfig;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.maven.plugin.Mojo#execute()
	 */
	public void execute() throws MojoExecutionException, MojoFailureException {
		if (skip) {
			getLog().info("skipped.");
		} else {
			new MavenLog4JConfigurator().configureLog4j(getLog());
			configureDefaults();
			internalExecute();
		}
	}

	private void configureDefaults() {
		if (sourceRoots == null) {
			sourceRoots = Lists.newArrayList(project.getCompileSourceRoots());
		}
	}

	protected void internalExecute() throws MojoExecutionException, MojoFailureException {
		Map<String, LanguageAccess> languages = new LanguageAccessFactory().createLanguageAccess(getLanguages(), this
				.getClass().getClassLoader(), project.getBasedir());
		Iterable<String> classPathEntries = filter(getClasspathElements(), emptyStringFilter());
		Injector injector = Guice.createInjector(new MavenStandaloneBuilderModule());
		StandaloneBuilder builder = injector.getInstance(StandaloneBuilder.class);
		builder.setLanguages(languages);
		builder.setEncoding(encoding);
		builder.setClassPathEntries(classPathEntries);
		builder.setClassPathLookUpFilter(classPathLookupFilter);
		builder.setSourceDirs(sourceRoots);
		builder.setFailOnValidationError(failOnValidationError);
		builder.setTempDir(createTempDir().getAbsolutePath());
		builder.setClusteringConfig(clusteringConfig.convertToStandaloneConfig());
		configureCompiler(builder.getCompiler());
		logState();
		boolean errorDetected = !builder.launch();
		if (errorDetected && failOnValidationError) {
			throw new MojoExecutionException("Execution failed due to a severe validation error.");
		}
	}

	private void configureCompiler(IJavaCompiler compiler) {
		CompilerConfiguration conf = compiler.getConfiguration();
		conf.setSourceLevel(compilerSourceLevel);
		conf.setTargetLevel(compilerTargetLevel);
		conf.setVerbose(false);
	}

	private void logState() {
		getLog().info("Encoding: " + (encoding == null ? "not set. Encoding provider will be used." : encoding));
		getLog().info("Compiler source level: " + compilerSourceLevel);
		getLog().info("Compiler target level: " + compilerTargetLevel);
		if (getLog().isDebugEnabled()) {
			getLog().debug("Source dirs: " + IterableExtensions.join(sourceRoots, ", "));
			getLog().debug("Classpath entries: " + IterableExtensions.join(classpathElements, ", "));
		}
	}

	private File createTempDir() {
		File tmpDir = new File(tmpClassDirectory);
		if (!tmpDir.mkdirs() && !tmpDir.exists()) {
			throw new IllegalArgumentException("Couldn't create directory '" + tmpClassDirectory + "'.");
		}
		return tmpDir;
	}

	private Predicate<String> emptyStringFilter() {
		return new Predicate<String>() {
			public boolean apply(String input) {
				return !Strings.isEmpty(input.trim());
			}
		};
	}

	public List<String> getClasspathElements() {
		return classpathElements;
	}

	public List<Language> getLanguages() {
		return languages;
	}

	public void setLanguages(List<Language> languages) {
		this.languages = languages;
	}
}
