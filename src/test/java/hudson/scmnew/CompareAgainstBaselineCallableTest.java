package hudson.scmnew;

import static java.util.regex.Pattern.compile;
import hudson.scmnew.SubversionSCM.SVNLogHandler;
import hudson.util.StreamTaskListener;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.regex.Pattern;

import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import org.apache.commons.io.output.NullOutputStream;

public class CompareAgainstBaselineCallableTest {

    @Rule
    public JenkinsRule r = new JenkinsRule();
    
    private CompareAgainstBaselineCallable callable;

    @Test
    public void callableShouldBeRemotable() throws IOException {
        givenACallable();
        
        // WHEN callable is serialized
        ObjectOutputStream oos = new ObjectOutputStream(new NullOutputStream());
        oos.writeObject(callable);
        // THEN no NotSerializableException should have been thrown
        oos.close();
    }

    private void givenACallable() {
        @SuppressWarnings("unchecked")
        SVNLogFilter filter = new DefaultSVNLogFilter(new Pattern[] {compile("excludes")}, new Pattern[] {compile("includes")},
                Collections.EMPTY_SET, "", new Pattern[0], false);
        
        StreamTaskListener taskListener = null; // this fails with NPE because of static Channel current(): StreamTaskListener.fromStdout();
        this.callable = new CompareAgainstBaselineCallable(
                new SVNRevisionState(null),
                new SVNLogHandler( filter, taskListener),
                        "projectName", taskListener, null, Collections.emptyMap(), "nodeName");
    }

}
