package com.jetbrains.edu.kotlin;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.RunContentExecutor;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.*;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.filters.ExceptionFilter;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironmentBuilder;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.BalloonBuilder;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.jetbrains.edu.courseFormat.Task;
import com.jetbrains.edu.learning.StudyUtils;
import com.jetbrains.edu.learning.actions.StudyCheckAction;
import com.jetbrains.edu.learning.run.StudyExecutor;
import com.jetbrains.edu.learning.run.StudyTestRunner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.idea.run.JetRunConfiguration;
import org.jetbrains.kotlin.idea.run.JetRunConfigurationType;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.util.List;


public class KotlinStudyExecutor implements StudyExecutor {
    private static final Logger LOG = Logger.getInstance(KotlinStudyExecutor.class);

    public Sdk findSdk(@NotNull final Project project) {
        return ProjectRootManager.getInstance(project).getProjectSdk();
    }

    @Override
    public StudyTestRunner getTestRunner(@NotNull final Task task, @NotNull final VirtualFile taskDir) {
        return new KotlinStudyTestRunner(task, taskDir);
    }

    @Override
    public RunContentExecutor getExecutor(@NotNull final Project project, @NotNull final ProcessHandler handler) {
//      TODO: find TracebackFilter
//        return new RunContentExecutor(project, handler).withFilter(new PythonTracebackFilter(project));
        return new RunContentExecutor(project, handler).withFilter(new ExceptionFilter(GlobalSearchScope.allScope(project)));
    }

    @Override
    public void setCommandLineParameters(@NotNull final GeneralCommandLine cmd,
                                         @NotNull final Project project,
                                         @NotNull final String filePath,
                                         @NotNull final String sdkPath,
                                         @NotNull final Task currentTask) {
        Sdk sdk = ProjectRootManager.getInstance(project).getProjectSdk();
        if (sdk != null) {
            RunnerAndConfigurationSettings temp = RunManager.getInstance(project).createRunConfiguration("temp",
                    JetRunConfigurationType.getInstance().getConfigurationFactories()[0]);
            try {
                String className = KotlinStudyUtils.getClassName(filePath);
                ((JetRunConfiguration) temp.getConfiguration()).setRunClass(className);
                RunProfileState state = temp.getConfiguration().getState(DefaultRunExecutor.getRunExecutorInstance(),
                        ExecutionEnvironmentBuilder.create(DefaultRunExecutor.getRunExecutorInstance(), temp).build());
                JavaCommandLineState javaCmdLine = (JavaCommandLineState) state;
                if (javaCmdLine == null) {
                    return;
                }
                JavaParameters javaParameters = javaCmdLine.getJavaParameters();
                GeneralCommandLine fromJavaParameters = CommandLineBuilder.createFromJavaParameters(javaParameters, project, false);
                cmd.setExePath(fromJavaParameters.getExePath());
                List<String> parameters = fromJavaParameters.getCommandLineList(fromJavaParameters.getExePath());
                cmd.addParameters(parameters.subList(1, parameters.size()));
                return;
            } catch (ExecutionException e) {
                LOG.error(e);
            }
        }
    }

    public void showNoSdkNotification(@NotNull final Project project) {
        final String text = "<html>No Java SDK configured for the project<br><a href=\"\">Configure SDK</a></html>";
        final BalloonBuilder balloonBuilder = JBPopupFactory.getInstance().
                createHtmlTextBalloonBuilder(text, null,
                        MessageType.WARNING.getPopupBackground(),
                        new HyperlinkListener() {
                            @Override
                            public void hyperlinkUpdate(HyperlinkEvent event) {
                                if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                                    ApplicationManager.getApplication()
                                            .invokeLater(new Runnable() {
                                                @Override
                                                public void run() {
                                                    ShowSettingsUtil.getInstance().showSettingsDialog(project, "Project SDK");
                                                }
                                            });
                                }
                            }
                        });
        balloonBuilder.setHideOnLinkClick(true);
        final Balloon balloon = balloonBuilder.createBalloon();
        StudyUtils.showCheckPopUp(project, balloon);
    }

    @Nullable
    @Override
    public StudyCheckAction getCheckAction() {
        return new KotlinStudyCheckAction();
    }

}