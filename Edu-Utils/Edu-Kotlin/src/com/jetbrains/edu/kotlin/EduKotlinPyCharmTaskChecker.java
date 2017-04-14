package com.jetbrains.edu.kotlin;

import com.intellij.execution.application.ApplicationConfiguration;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.edu.learning.courseFormat.tasks.PyCharmTask;
import com.jetbrains.edu.utils.EduIntelliJNames;
import com.jetbrains.edu.utils.EduPyCharmTasksChecker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.psi.KtClass;

import java.util.Collection;

public class EduKotlinPyCharmTaskChecker extends EduPyCharmTasksChecker {
  public EduKotlinPyCharmTaskChecker(@NotNull PyCharmTask task, @NotNull Project project) {
    super(task, project);
  }

  @Nullable
  @Override
  protected VirtualFile getTestsFile() {
    VirtualFile taskDir = myTask.getTaskDir(myProject);
    if (taskDir == null) {
      return null;
    }
    VirtualFile testsFile = taskDir.findChild(EduKotlinPluginConfigurator.LEGACY_TESTS_KT);
    if (testsFile != null) {
      return testsFile;
    }
    return taskDir.findChild(EduKotlinPluginConfigurator.TESTS_KT);
  }

  @Override
  protected void setProcessParameters(Project project, ApplicationConfiguration configuration, Module module, @NotNull VirtualFile testsFile) {
    configuration.setMainClassName(EduIntelliJNames.TEST_RUNNER_CLASS);
    configuration.setModule(module);
    PsiFile psiFile = PsiManager.getInstance(project).findFile(testsFile);
    Collection<KtClass> ktClasses = PsiTreeUtil.findChildrenOfType(psiFile, KtClass.class);
    for (KtClass ktClass : ktClasses) {
      String name = ktClass.getName();
      configuration.setProgramParameters(name);
    }
  }
}
