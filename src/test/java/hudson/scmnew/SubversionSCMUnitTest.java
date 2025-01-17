package hudson.scmnew;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.model.Run;
import hudson.remoting.VirtualChannel;
import hudson.scmnew.SubversionSCM.ModuleLocation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;

/**
 * Unit tests for {@link SubversionSCM}.
 * 
 * ({@link SubversionSCMTest} is more like an integration test)
 * 
 * @author kutzi
 */
public class SubversionSCMUnitTest {
    
    @Test
    @Issue("JENKINS-12113")
    public void testLocalDirectoryIsExpandedWithEnvVars() {
        FilePath root = new FilePath((VirtualChannel)null, "root");
        
        EnvVars envVars = new EnvVars();
        envVars.put("BRANCH", "test");
        
        SubversionSCM scm = new SubversionSCM("dummyUrl");
        
        FilePath resolvedRoot = scm._getModuleRoot(root, "$BRANCH/someMorePath", envVars);

        // Be sure that paths is plateform independant.
        String fileSeparator = System.getProperty("file.separator");
        String expected = String.format("root%stest%ssomeMorePath", fileSeparator, fileSeparator);

        Assert.assertEquals(expected, resolvedRoot.getRemote());
    }

    @Test
    @Ignore("weird mockito issue, only occurs when running whole test suite, test class or method both pass")
    public void shouldSetEnvironmentVariablesWithSingleSvnModule() throws IOException {
        // GIVEN an scm with a single module location
        SubversionSCM scm = mockSCMForBuildEnvVars();
        
        ModuleLocation[] singleLocation = new ModuleLocation[] {new ModuleLocation("/remotepath", null, "", null, false, false)};
        when(scm.getLocations(any(EnvVars.class), any(AbstractBuild.class))).thenReturn(singleLocation);
        
        Map<String, Long> revisions = new HashMap<>();
        revisions.put("/remotepath", 4711L);
        when(scm.parseSvnRevisionFile(any())).thenReturn(revisions);
        
        // WHEN envVars are build
        AbstractBuild<?,?> build = mock(AbstractBuild.class);
        Map<String, String> envVars = new HashMap<>();
        scm.buildEnvVars(build, envVars);
        
        // THEN: we have the (legacy) SVN_URL and SVN_REVISION vars
        assertThat(envVars.get("SVN_URL"), is("/remotepath"));
        assertThat(envVars.get("SVN_REVISION"), is("4711"));
        
        // AND: also the index-based vars
        assertThat(envVars.get("SVN_URL_1"), is("/remotepath"));
        assertThat(envVars.get("SVN_REVISION_1"), is("4711"));
    }
    
    @Test
    @Ignore("weird mockito issue, only occurs when running whole test suite, test class or method both pass")
    @SuppressWarnings("deprecation")
    public void shouldSetEnvironmentVariablesWithMultipleSvnModules() throws IOException {
        // GIVEN an scm with a 2 module locations
        SubversionSCM scm = mockSCMForBuildEnvVars();
        
        ModuleLocation[] locations = new ModuleLocation[] {
                new ModuleLocation("/remotepath1", ""),
                new ModuleLocation("/remotepath2", "")};
        when(scm.getLocations(any(EnvVars.class), any(AbstractBuild.class))).thenReturn(locations);
        
        Map<String, Long> revisions = new HashMap<>();
        revisions.put("/remotepath1", 4711L);
        revisions.put("/remotepath2", 42L);
        when(scm.parseSvnRevisionFile(any())).thenReturn(revisions);
        
        // WHEN envVars are build
        AbstractBuild<?,?> build = mock(AbstractBuild.class);
        Map<String, String> envVars = new HashMap<>();
        scm.buildEnvVars(build, envVars);
        
        // THEN: we have the SVN_URL_n and SVN_REVISION_n vars
        assertThat(envVars.get("SVN_URL_1"), is("/remotepath1"));
        assertThat(envVars.get("SVN_REVISION_1"), is("4711"));
        
        assertThat(envVars.get("SVN_URL_2"), is("/remotepath2"));
        assertThat(envVars.get("SVN_REVISION_2"), is("42"));
    }
    
    private SubversionSCM mockSCMForBuildEnvVars() {
        SubversionSCM scm = mock(SubversionSCM.class);
        doCallRealMethod().when(scm).buildEnvVars(any(AbstractBuild.class), anyMap());
        doCallRealMethod().when(scm).buildEnvironment(any(Run.class), anyMap());
        return scm;
    }
}
