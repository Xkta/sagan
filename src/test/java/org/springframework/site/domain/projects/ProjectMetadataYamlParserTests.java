package org.springframework.site.domain.projects;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;

public class ProjectMetadataYamlParserTests {

	private Map<String,List<Project>> projects;

	@Before
	public void setUp() throws Exception {
		InputStream yaml = new ClassPathResource("/test-project-metadata.yml", getClass()).getInputStream();
		ProjectMetadataYamlParser parser = new ProjectMetadataYamlParser();
		projects = parser.parse(yaml);
	}

	@Test
	public void parseCategoriesIgnoresDiscardCategory() throws IOException {
		assertThat(projects.size(), equalTo(3));
		assertThat(projects.keySet(), containsInAnyOrder("active", "other", "attic"));
	}

	@Test
	public void categoriesHaveListOfProjects() throws IOException {
		List<Project> active = projects.get("active");
		assertThat(active.size(), equalTo(3));
	}

	@Test
	public void projectWithCustomSiteAndRepo() throws IOException {
		Project project =  projects.get("active").get(0);
		assertThat(project.getId(), equalTo("spring-framework"));
		assertThat(project.getName(), equalTo("Spring Framework"));
		assertThat(project.getReferenceUrl(), equalTo("http://static.springsource.org/spring/docs/{version}/spring-framework-reference/html/"));
		assertThat(project.getApiUrl(), equalTo("http://static.springsource.org/spring/docs/{version}/javadoc-api/"));
		assertThat(project.getRepoUrl(), equalTo("http://www.example.com/repo/spring-framework"));
		assertThat(project.getSiteUrl(), equalTo("http://www.example.com/spring-framework"));
		assertThat(project.hasSite(), equalTo(true));
	}

	@Test
	public void projectWithDefaultSiteAndRepo() throws IOException {
		Project project =  projects.get("active").get(2);
		assertThat(project.getId(), equalTo("spring-framework-defaultsite"));
		assertThat(project.getName(), equalTo("Spring Framework"));
		assertThat(project.getReferenceUrl(), equalTo("http://static.springsource.org/spring/docs/{version}/spring-framework-reference/html/"));
		assertThat(project.getApiUrl(), equalTo("http://static.springsource.org/spring/docs/{version}/javadoc-api/"));
		assertThat(project.getRepoUrl(), equalTo("http://github.com/springframework/spring-framework-defaultsite"));
		assertThat(project.getSiteUrl(), equalTo("http://projects.springframework.io/spring-framework-defaultsite"));
		assertThat(project.hasSite(), equalTo(true));
	}

	@Test
	public void projectWithNoSite() throws IOException {
		Project project =  projects.get("active").get(1);
		assertThat(project.getId(), equalTo("spring-framework-nosite"));
		assertThat(project.getName(), equalTo("Spring Framework"));
		assertThat(project.getReferenceUrl(), equalTo("http://static.springsource.org/spring/docs/{version}/spring-framework-reference/html/"));
		assertThat(project.getApiUrl(), equalTo("http://static.springsource.org/spring/docs/{version}/javadoc-api/"));
		assertThat(project.getRepoUrl(), equalTo("http://github.com/springframework/spring-framework-nosite"));
		assertThat(project.getSiteUrl(), nullValue());
		assertThat(project.hasSite(), equalTo(false));
	}

	@Test
	public void projectsHaveSupportedVersionsWithCurrentVersionSet() throws IOException {
		List<Project> active = projects.get("active");
		Project project = active.get(0);

		assertThat(project.getSupportedVersions(), contains(
				new Version("4.0.0.M1", Version.Release.PRERELEASE),
				new Version("3.2.3.RELEASE", Version.Release.CURRENT),
				new Version("3.1.4.RELEASE", Version.Release.SUPPORTED)
		));
	}

	@Test
	public void projectHasNoSupportedVersions() throws IOException {
		List<Project> active = projects.get("other");
		assertThat(active.size(), equalTo(1));
		Project project = active.get(0);
		assertThat(project.getSupportedVersions().size(), equalTo(0));
	}

}
