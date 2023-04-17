package hudson.scmnew.subversion;

import static org.junit.Assert.*;
import hudson.scmnew.RevisionParameterAction;
import hudson.scmnew.SubversionSCM;
import hudson.scmnew.SubversionSCM.External;
import hudson.scmnew.SubversionSCM.ModuleLocation;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.tmatesoft.svn.core.wc.SVNRevision;

public class WorkspaceUpdaterTest {
    
    private static final Date NOW = new Date();
    
    @Test
    public void testGetRevisionFromTimestamp() {
        WorkspaceUpdater.UpdateTask updateTask = createUpdateTask();
        
        ModuleLocation l = new ModuleLocation("remote", "local");
        
        SVNRevision revision = updateTask.getRevision(l);
        assertEquals(NOW, revision.getDate());
        assertEquals(-1L, revision.getNumber());
    }
    
    @Test
    public void testRevisionFromRevisionParametersOverrideTimestamp() {
        WorkspaceUpdater.UpdateTask updateTask = createUpdateTask();
        
        updateTask.revisions = new RevisionParameterAction(new SubversionSCM.SvnInfo("remote", 4711));
        
        ModuleLocation l = new ModuleLocation("remote", "local");
        
        SVNRevision revision = updateTask.getRevision(l);
        assertEquals(4711L, revision.getNumber());
        assertNull(revision.getDate());
    }

    @Test
    public void testRevisionInUrlOverridesEverything() {
        WorkspaceUpdater.UpdateTask updateTask = createUpdateTask();

        updateTask.revisions = new RevisionParameterAction(new SubversionSCM.SvnInfo("remote", 4711));
        
        ModuleLocation l = new ModuleLocation("remote@12345", "local");
        
        SVNRevision revision = updateTask.getRevision(l);
        assertEquals(12345L, revision.getNumber());
        assertNull(revision.getDate());
    }
    
    @Test
    public void testRevisionInUrlOverridesEverything_HEAD() {
        WorkspaceUpdater.UpdateTask updateTask = createUpdateTask();

        updateTask.revisions = new RevisionParameterAction(new SubversionSCM.SvnInfo("remote", 4711));
        
        ModuleLocation l = new ModuleLocation("remote@HEAD", "local");
        
        SVNRevision revision = updateTask.getRevision(l);
        assertEquals(SVNRevision.HEAD.getName(), revision.getName());
        assertEquals(-1L, revision.getNumber());
        assertNull(revision.getDate());
    }

    private WorkspaceUpdater.UpdateTask createUpdateTask() {
         WorkspaceUpdater.UpdateTask updateTask = new WorkspaceUpdater.UpdateTask() {
            private static final long serialVersionUID = 1L;
            @Override
            public List<External> perform() throws IOException, InterruptedException {
                return null;
            }
        };
        updateTask.timestamp = NOW;
        return updateTask;
    }
    
}
