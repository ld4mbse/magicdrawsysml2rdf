package edu.gatech.mbsec.adapter.magicdraw.parser;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.core.project.ProjectDescriptor;
import com.nomagic.magicdraw.core.project.ProjectDescriptorsFactory;
import com.nomagic.magicdraw.core.project.ProjectsManager;
import com.nomagic.runtime.ApplicationExitedException;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Singleton wrapper for the {@link Application MagicDraw Application}.
 * @author rherrera
 */
public class MagicDrawApplication {
    /**
     * Logger of this class.
     */
    private static final Logger LOG = Logger.getLogger(MagicDrawApplication.class.getName());
    /**
     * The SysML profile name.
     */
    public static final String SYSML_PROFILE = "SysML";
    /**
     * This singleton of this class.
     */
    private static MagicDrawApplication singleton;
    /**
     * Gets this singleton.
     * @return this singleton reference.
     */
    public static MagicDrawApplication getInstance() {
        if (singleton == null)
            singleton = new MagicDrawApplication();
        return singleton;
    }
    /**
     * The MagicDraw application.
     */
    private final Application application;
    /**
     * Determines whether the MD application has been started.
     */
    private boolean started;
    /**
     * Constructs the singleton.
     */
    private MagicDrawApplication() {
        application = Application.getInstance();
        started = false;
    }
    /**
     * Determines whether the MD application has been started.
     * @return {@code true} if the MD application has been started;
     * {@code false} otherwise.
     */
    public boolean isStarted() {
        return started;
    }
    /**
     * Loads a project into the MD application and gets its reference.
     * @param mdzip the input mdzip file.
     * @return the MD application reference.
     * @throws ApplicationExitedException if the application ends unexpectedly.
     */
    private Project getProject(File mdzip) throws ApplicationExitedException {
        ProjectDescriptor desc;
        String mdzipPath = mdzip.toString();
        desc = ProjectDescriptorsFactory.createProjectDescriptor(mdzip.toURI());
        ProjectsManager manager = application.getProjectsManager();
        Project project = manager.findProject(desc);
        if (project != null) {
            LOG.log(Level.INFO, "CLOSING PROJECT \"{0}\" TO RELOAD IT", mdzipPath);
            manager.setActiveProject(project);
            manager.closeProject();
        }
        LOG.log(Level.INFO, "LOADING PROJECT \"{0}\"", mdzipPath);
        manager.loadProject(desc, true);
        project = manager.getActiveProject();
        if (StereotypesHelper.getProfile(project, SYSML_PROFILE) == null)
            throw new IllegalStateException(mdzipPath + " is not a SysML project.");
        LOG.info("PROJECT LOADED");
        return project;
    }
    /**
     * Gets the project reference corresponding to a valid mdzip file.
     * @param mdzip the mdzip file path.
     * @return the MD project reference.
     * @throws IllegalArgumentException if {@code mdzip} does not exist or is
     * not a mdzip file (its name does not end with "mdzip" string).
     * @throws ApplicationExitedException if the application ends unexpectedly.
     */
    public Project getProject(String mdzip) throws ApplicationExitedException {
        File source = new File(mdzip);
        if (!source.exists()) {
            throw new IllegalArgumentException(mdzip + " file does not exist");
        }
        if (!source.getPath().endsWith("mdzip")) {
            throw new IllegalArgumentException(mdzip + " is not a mdzip file");
        }
        if (!started) {
            LOG.info("TURNING ON MD");
            application.start(false, true, false, new String[0], null);
            LOG.info("MD TURNED ON");
            started = true;
        }
        return getProject(source);
    }
    /**
     * Finishes the application and the current java process.
     * @throws ApplicationExitedException if the application ends unexpectedly.
     */
    public void shutdown() throws ApplicationExitedException {
        if (started) {
            LOG.info("MD TURNING OFF");
            application.shutdown();
        }
    }
}