package hr.ja.openextern.popup.actions;

import java.io.File;
import java.text.ParseException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJarEntryResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.pde.core.plugin.IPluginModel;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import hr.ja.openextern.Activator;
import hr.ja.openextern.Commands;

public abstract class BaseOpenAction extends AbstractHandler {

	public void execCommand(ExecutionEvent event, String command, String openName) {

		String selectedPath = getSelectedFolderPath(event);
		Shell shell = HandlerUtil.getActiveShell(event);
		if (selectedPath == null) {
			MessageDialog.openError(shell, "Error " + openName, "Path not found");
		} else {
			File file = new File(selectedPath);
			if (!file.exists()) {
				MessageDialog.openError(shell, "Error " + openName, "Path not found: " + file);
				return;
			} else if (file.isFile()) {
				selectedPath = file.getParent();
			}
			try {
				String parseCommand = "";
				// if (Activator.getDefault().getInitPlugin().getOS() != OS.WINDOWS)
				// {
				parseCommand = Commands.parse(command, selectedPath);

				ExecutorCommand.executeCommand(parseCommand, new File(selectedPath));
			} catch (ParseException e) {
				MessageDialog.openError(shell, "Error " + openName, e.getMessage());
			}
		}
	}

	/**
	 *
	 * May return null!!
	 * @param event
	 * @return full path
	 */
	public String getSelectedFolderPath(ExecutionEvent event) {

		ISelection selection = HandlerUtil.getCurrentSelection(event);

		if (selection instanceof IStructuredSelection) {
			Object sel = ((IStructuredSelection) selection).getFirstElement();

			if (sel instanceof IJavaElement) {
				IJavaElement je = (IJavaElement) sel;
				try {
					IResource correspondingResource = je.getCorrespondingResource();
					if (correspondingResource != null) {
						return getPath(correspondingResource);
					}
					String path = getPath(je.getPath());
					if (path != null) {
						return path;
					}
				} catch (JavaModelException ignore) {
				}
			}

			if (sel instanceof IPluginModel) {
				IPluginModel mod = (IPluginModel) sel;
				String installLocation = mod.getInstallLocation();
				return installLocation;
			}

			if (sel instanceof IJarEntryResource) {
				IJarEntryResource jar = (IJarEntryResource) sel;
				return getPath(jar.getPackageFragmentRoot().getPath());
			}

			if (sel instanceof IAdaptable) {
				IAdaptable ad = (IAdaptable) sel;

				IResource resource = (IResource) ad.getAdapter(IResource.class);
				if (resource != null) {
					return getPath(resource);
				}
				File file = (File) ad.getAdapter(File.class);
				if (file != null) {
					return file.getAbsolutePath();
				}
			}
		}

		Activator.getDefault().logError("Can't find path, not implemented " + selection.getClass(), null);
		// System.err.println("cant find path: ");
		return null;
	}

	private String getPath(IResource resource) {
		return getPath(resource.getLocation());
	}

	private String getPath(IPath path) {
		if(path!=null) {
			return path.toOSString();
		}
		return null;
	}

}
